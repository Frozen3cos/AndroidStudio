package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private final Context context;
    private final List<ChatMessage> messages;

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() { return messages.size(); }

    @Override
    public Object getItem(int position) { return messages.get(position); }

    @Override
    public long getItemId(int position) { return messages.get(position).getId(); }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromMe() ? TYPE_RIGHT : TYPE_LEFT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage msg = messages.get(position);
        int type = getItemViewType(position);

        if (convertView == null) {
            int layoutId = (type == TYPE_RIGHT)
                    ? R.layout.item_chat_right
                    : R.layout.item_chat_left;
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }

        TextView tvContent = convertView.findViewById(R.id.tvChatContent);
        ImageView ivAvatar = convertView.findViewById(R.id.ivChatAvatar);

        tvContent.setText(msg.getContent());

        // 头像：自己用头像1，好友用头像2
        ivAvatar.setImageResource(msg.isFromMe()
                ? R.drawable.avatar1
                : R.drawable.avatar2);

        return convertView;
    }

    /** 外部添加一条新消息 */
    public void addMessage(ChatMessage msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }
}