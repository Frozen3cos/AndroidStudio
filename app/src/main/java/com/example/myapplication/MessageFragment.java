package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.db.MessageDao;

import java.util.List;

public class MessageFragment extends Fragment {

    private ListView lvMessages;
    private MessageDao messageDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageDao = new MessageDao(requireContext());
        lvMessages = view.findViewById(R.id.lvMessages);
        loadConversations();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从聊天页返回时刷新
        if (messageDao != null) loadConversations();
    }

    private void loadConversations() {
        List<ChatMessage> conversations = messageDao.queryConversations();

        // 复用现有的 MessageAdapter（如果 Message 类还在用），
        // 也可以直接用 ChatAdapter，这里用简化版：把会话映射为 Message 列表
        // 为了简单，直接用 ChatMessage 列表 + 一个简易适配器

        // 用 ChatAdapter 不合适（它是聊天气泡），所以这里直接用 ArrayAdapter 走已有布局
        // 为了不改动 MessageAdapter，我们把它改成接收 ChatMessage
        // 最简做法：用 ChatAdapter 的父类 BaseAdapter 太复杂，直接给 lvMessages 用 MessageAdapter
        // —— 但因为 Message 和 ChatMessage 结构不同，我们新写一个内部适配器

        lvMessages.setAdapter(new ConversationAdapter(requireContext(), conversations));

        lvMessages.setEmptyView(requireView().findViewById(R.id.tvMsgEmpty));

        lvMessages.setOnItemClickListener((parent, v, position, id) -> {
            ChatMessage conv = conversations.get(position);
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("friend_name", conv.getFriendName());
            startActivity(intent);
            requireActivity().overridePendingTransition(
                    android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    /**
     * 会话列表适配器（内部类）
     * 只显示：头像 + 好友名 + 最后一条消息 + 时间
     */
    private static class ConversationAdapter extends android.widget.BaseAdapter {
        private final android.content.Context ctx;
        private final List<ChatMessage> list;

        ConversationAdapter(android.content.Context ctx, List<ChatMessage> list) {
            this.ctx = ctx;
            this.list = list;
        }

        @Override public int getCount() { return list.size(); }
        @Override public Object getItem(int p) { return list.get(p); }
        @Override public long getItemId(int p) { return list.get(p).getId(); }

        @Override
        public View getView(int position, View cv, ViewGroup parent) {
            if (cv == null) {
                cv = LayoutInflater.from(ctx).inflate(R.layout.item_message, parent, false);
            }
            ChatMessage m = list.get(position);

            android.widget.ImageView ivAvatar = cv.findViewById(R.id.ivMsgAvatar);
            android.widget.TextView tvName = cv.findViewById(R.id.tvMsgName);
            android.widget.TextView tvContent = cv.findViewById(R.id.tvMsgContent);
            android.widget.TextView tvTime = cv.findViewById(R.id.tvMsgTime);

            ivAvatar.setImageResource(FriendAdapter.avatarResOf(0));
            tvName.setText(m.getFriendName());
            tvContent.setText(m.getContent());

            // 格式化时间：HH:mm
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm",
                    java.util.Locale.getDefault());
            tvTime.setText(sdf.format(new java.util.Date(m.getTimestamp())));

            return cv;
        }
    }
}