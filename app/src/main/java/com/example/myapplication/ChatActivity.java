package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.MessageDao;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView lvChat;
    private EditText etInput;
    private CustomButton btnSend;
    private ImageView ivBack;
    private TextView tvChatTitle;

    private ChatAdapter adapter;
    private MessageDao messageDao;
    private String friendName;
    private int friendAvatarIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#4CAF50"));
        }
        setContentView(R.layout.activity_chat);

        // 获取好友名
        friendName = getIntent().getStringExtra("friend_name");
        friendAvatarIndex = getIntent().getIntExtra("friend_avatar", 0);
        if (friendName == null) friendName = "好友";

        messageDao = new MessageDao(this);

        initViews();
        loadMessages();
    }

    private void initViews() {
        lvChat = findViewById(R.id.lvChat);
        etInput = findViewById(R.id.etInput);
        btnSend = findViewById(R.id.btnSend);
        ivBack = findViewById(R.id.ivBack);
        tvChatTitle = findViewById(R.id.tvChatTitle);

        tvChatTitle.setText(friendName);

        ivBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnSend.setOnClickListener(v -> sendMessage());
    }

    /** 从数据库加载聊天记录 */
    private void loadMessages() {
        List<ChatMessage> messages = messageDao.queryByFriend(friendName);
        adapter = new ChatAdapter(this, messages);
        lvChat.setAdapter(adapter);

        // 滚动到底部（最新的消息）
        if (!messages.isEmpty()) {
            lvChat.setSelection(messages.size() - 1);
        }
    }

    /** 发送消息：写入数据库 + 刷新列表 */
    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (text.isEmpty()) return;

        // 1. 写入数据库
        long id = messageDao.insert(friendName, text, true);

        // 2. 添加到列表（不用重新查库，性能更好）
        ChatMessage msg = new ChatMessage((int) id, friendName, text,
                true, System.currentTimeMillis());
        adapter.addMessage(msg);

        // 3. 清空输入框 + 滚到底部
        etInput.setText("");
        lvChat.setSelection(adapter.getCount() - 1);

        // 4. 可选：1.2 秒后模拟好友自动回复
        simulateReply();
    }

    /** 模拟好友自动回复（演示用） */
    private void simulateReply() {
        String[] replies = {"嗯嗯", "好的", "哈哈哈", "我知道了", "稍等，我看看", "厉害！", "？"};
        String reply = replies[(int) (Math.random() * replies.length)];

        lvChat.postDelayed(() -> {
            long id = messageDao.insert(friendName, reply, false);
            ChatMessage msg = new ChatMessage((int) id, friendName, reply,
                    false, System.currentTimeMillis());
            adapter.addMessage(msg);
            lvChat.setSelection(adapter.getCount() - 1);
        }, 1200);
    }
}