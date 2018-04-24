package com.example.janakannandakumaran.tchat.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.janakannandakumaran.tchat.Entities.Message;
import com.example.janakannandakumaran.tchat.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class TchatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;

    private static final int SELF_MESSAGE = 0;
    private static final int OTHER_MESSAGE = 1;

    private FirebaseUser user;

    public TchatAdapter(ArrayList<Message> messages){
        this.messages = messages;

    }

    public void setUser(FirebaseUser user)
    {
            this.user = user;
    }

    public void addMessage(Message message)
    {
        messages.add(message);
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {

        if (messages.size() > 0)
        {
            if (messages.get(position).getUserId().equals(user.getUid()))
            {
                return SELF_MESSAGE;
            }else {
                return OTHER_MESSAGE;
            }
        }

        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case SELF_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_self_message, parent, false);
                return new SelfMessageViewHolder(view);

             default:
                 view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
                 return new MessageViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getItemViewType() == OTHER_MESSAGE)
        {
            ((MessageViewHolder) holder).bind(message);
        }else {
            ((SelfMessageViewHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SelfMessageViewHolder extends RecyclerView.ViewHolder
    {
        private TextView selfMessage;

        public SelfMessageViewHolder(View itemView) {
            super(itemView);
            selfMessage = (TextView) itemView.findViewById(R.id.selfMessage);
        }

        void bind(Message message)
        {
            selfMessage.setText(message.getContent());
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        private TextView content, username;

        public MessageViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            content = (TextView) itemView.findViewById(R.id.message);
        }

        void bind(Message message)
        {
            username.setText(message.getUsername());
            content.setText(message.getContent());
        }
    }
}
