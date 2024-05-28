package com.example.llamachatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChatMessage> messages;

    public ChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = messages.get(position);

        if (message.isUser()) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_message, parent, false);
            TextView messageText = convertView.findViewById(R.id.user_message_text);
            ImageView profileImage = convertView.findViewById(R.id.user_profile_image);
            messageText.setText(message.getMessage());
            // Set user profile image if needed
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bot_message, parent, false);
            TextView messageText = convertView.findViewById(R.id.bot_message_text);
            ImageView profileImage = convertView.findViewById(R.id.bot_profile_image);
            messageText.setText(message.getMessage());
            // Set bot profile image if needed
        }

        return convertView;
    }
}
