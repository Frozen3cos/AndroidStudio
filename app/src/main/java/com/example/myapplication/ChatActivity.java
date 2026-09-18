package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.FriendDao;
import com.example.myapplication.db.MessageDao;
import com.example.myapplication.network.AiChatService;

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

    // 好友的人设信息（用于让 AI 模仿）
    private String friendSignature = "";
    private String friendGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#4CAF50"));
        }
        setContentView(R.layout.activity_chat);

        // 接收好友信息
        friendName = getIntent().getStringExtra("friend_name");
        friendAvatarIndex = getIntent().getIntExtra("friend_avatar", 0);
        if (friendName == null) friendName = "好友";

        messageDao = new MessageDao(this);

        // 从数据库读取好友人设（签名和性别）
        loadFriendProfile();

        initViews();
        loadMessages();
    }

    /** 从数据库读取好友签名和性别 */
    private void loadFriendProfile() {
        try {
            FriendDao friendDao = new FriendDao(this);
            List<Friend> friends = friendDao.queryAll();
            for (Friend f : friends) {
                if (f.getName().equals(friendName)) {
                    friendSignature = f.getSignature() == null ? "" : f.getSignature();
                    friendGender = f.getGender() == null ? "" : f.getGender();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void loadMessages() {
        List<ChatMessage> messages = messageDao.queryByFriend(friendName);
        adapter = new ChatAdapter(this, messages);
        lvChat.setAdapter(adapter);
        if (!messages.isEmpty()) {
            lvChat.setSelection(messages.size() - 1);
        }
    }

    /** 发送消息：入库 → 显示 → 让 AI 模拟好友回复 */
    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (text.isEmpty()) return;

        // 1. 用户消息写入数据库
        long id = messageDao.insert(friendName, text, true);

        // 2. 显示在界面
        ChatMessage msg = new ChatMessage((int) id, friendName, text,
                true, System.currentTimeMillis());
        adapter.addMessage(msg);
        etInput.setText("");
        scrollToBottom();

        // 3. 让 AI 以好友身份回复
        requestAiReply(text);
    }

    /** 请求 AI 扮演好友回复 */
    private void requestAiReply(String userText) {
        // 禁用发送按钮，防止连发
        btnSend.setEnabled(false);
        btnSend.setText("...");

        AiChatService.getFriendReply(
                friendName, friendSignature, friendGender, userText,
                new AiChatService.Callback() {
                    @Override
                    public void onSuccess(String reply) {
                        runOnUiThread(() -> {
                            // 显示"正在输入"结束
                            btnSend.setEnabled(true);
                            btnSend.setText("发送");

                            // 把 AI 回复当作好友消息入库 + 显示
                            long aiId = messageDao.insert(friendName, reply, false);
                            ChatMessage aiMsg = new ChatMessage((int) aiId,
                                    friendName, reply, false,
                                    System.currentTimeMillis());
                            adapter.addMessage(aiMsg);
                            scrollToBottom();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnSend.setEnabled(true);
                            btnSend.setText("发送");
                            Toast.makeText(ChatActivity.this,
                                    "AI 回复失败：" + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void scrollToBottom() {
        lvChat.post(() -> lvChat.setSelection(adapter.getCount() - 1));
    }
}