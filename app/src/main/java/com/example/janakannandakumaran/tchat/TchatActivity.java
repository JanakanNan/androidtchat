package com.example.janakannandakumaran.tchat;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

import com.example.janakannandakumaran.tchat.Adapters.TchatAdapter;
import com.example.janakannandakumaran.tchat.Entities.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TchatActivity extends AppCompatActivity {

    private EditText etMessage;
    private ImageButton sendButton;
    private RecyclerView recycler;
    private Button logout;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;


    private TchatAdapter adapter;

    private SharedPreferences prefs;
    private String username;
    private String userId;

    private static final String TAG = "TCHAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchat);

        //Initialisation de la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tchat");
        setSupportActionBar(toolbar);

        //Initialisation des vues
        initView();
        initFirebase();

        prefs = getSharedPreferences("tchat", MODE_PRIVATE);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    attachChildListener();
                    username = prefs.getString("PSEUDO",null);
                    userId = user.getUid();
                    adapter.setUser(user);

                }else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
    }

    private void attachChildListener(){
        if (childEventListener == null)
        {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.w(TAG, "onChildAdded :");
                    Message message = dataSnapshot.getValue(Message.class);
                    message.setUid(dataSnapshot.getKey());
                    adapter.addMessage(message);
                    recycler.scrollToPosition(adapter.getItemCount() - 1);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRef.child(Constants.MESSAGE_DB).limitToLast(100).addChildEventListener(childEventListener);
        }
    }

    private void detachChildListener()
    {
        if (childEventListener != null)
        {
            mRef.child(Constants.MESSAGE_DB).removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
    private void initView()
    {
        etMessage = (EditText) findViewById(R.id.etMessage);
        sendButton =  (ImageButton) findViewById(R.id.sendButton);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);


        ArrayList<Message> messages = new ArrayList<>();
        adapter = new TchatAdapter(messages);
        recycler.setAdapter(adapter);


        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString();

        if (!TextUtils.isEmpty(content))
        {
            Message message = new Message("Jordan", "DAVtbHpBfIW1gH2JxHA3Jn1mjgX2", content, null);
            mRef.child(Constants.MESSAGE_DB).push().setValue(message);
            etMessage.setText(" ");
        }
    }

    private void initFirebase()
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase =FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        userId = "";
        username = "";
        LoginActivity user = new LoginActivity();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null)
        {
            mAuth.removeAuthStateListener(authStateListener);
        }
        detachChildListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
