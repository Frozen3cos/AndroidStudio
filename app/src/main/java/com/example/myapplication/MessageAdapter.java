package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final Context context;
    private final List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_message, parent, false);
        }
        Message msg = messages.get(position);
        ImageView ivAvatar = convertView.findViewById(R.id.ivMsgAvatar);
        TextView tvName = convertView.findViewById(R.id.tvMsgName);
        TextView tvContent = convertView.findViewById(R.id.tvMsgContent);
        TextView tvTime = convertView.findViewById(R.id.tvMsgTime);

        ivAvatar.setImageResource(msg.getAvatarRes());
        tvName.setText(msg.getSenderName());
        tvContent.setText(msg.getLastMessage());
        tvTime.setText(msg.getTime());
        return convertView;
    }
}