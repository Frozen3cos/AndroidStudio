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

    // 消息类型：左边（好友）、右边（自己）
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private final Context context;
    private final List<ChatMessage> messages;

    private final int friendAvatarRes;     // 好友头像资源ID
    private final int myAvatarRes;         // 自己的头像资源ID

    /**
     * 简化构造：默认好友头像 avatar2，自己头像 avatar1
     * 适合 AI 助手聊天等场景
     */
    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this(context, messages, R.drawable.avatar2, R.drawable.avatar1);
    }

    /**
     * 完整构造：可自定义双方头像
     * @param context       上下文
     * @param messages      消息列表
     * @param friendAvatarRes 好友（左侧）头像资源ID
     * @param myAvatarRes   自己（右侧）头像资源ID
     */
    public ChatAdapter(Context context, List<ChatMessage> messages,
                       int friendAvatarRes, int myAvatarRes) {
        this.context = context;
        this.messages = messages;
        this.friendAvatarRes = friendAvatarRes;
        this.myAvatarRes = myAvatarRes;
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
        return messages.get(position).getId();
    }

    /** 两种 item 布局 */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromMe() ? TYPE_RIGHT : TYPE_LEFT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage msg = messages.get(position);
        int type = getItemViewType(position);

        // 根据类型选择布局
        if (convertView == null) {
            int layoutId = (type == TYPE_RIGHT)
                    ? R.layout.item_chat_right
                    : R.layout.item_chat_left;
            convertView = LayoutInflater.from(context)
                    .inflate(layoutId, parent, false);
        }

        TextView tvContent = convertView.findViewById(R.id.tvChatContent);
        ImageView ivAvatar = convertView.findViewById(R.id.ivChatAvatar);

        // 设置文本内容
        tvContent.setText(msg.getContent());

        // 设置头像：自己/好友
        ivAvatar.setImageResource(msg.isFromMe() ? myAvatarRes : friendAvatarRes);

        return convertView;
    }

    /** 追加一条消息并刷新列表 */
    public void addMessage(ChatMessage msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }
}