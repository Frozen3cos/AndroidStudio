package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FriendDetailActivity extends AppCompatActivity {

    // ===== 控件 =====
    private ImageView ivDetailAvatar;
    private TextView tvDetailName, tvDetailSignature;
    private TextView tvDetailGender, tvDetailPhone, tvDetailEmail;
    private CustomButton btnBack;
    private CustomButton btnSendMsg;

    // ===== 好友数据（成员变量，供点击事件使用） =====
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#4CAF50"));
        }

        setContentView(R.layout.activity_friend_detail);

        initViews();
        receiveFriendData();   // 先把 friend 加载好

        // 返回按钮
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 发消息按钮（独立的事件，不能嵌在上面的 lambda 里）
        btnSendMsg.setOnClickListener(v2 -> {
            Intent intent = new Intent(FriendDetailActivity.this, ChatActivity.class);
            intent.putExtra("friend_name", friend.getName());
            intent.putExtra("friend_avatar", friend.getAvatarIndex());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    /** 初始化控件 */
    private void initViews() {
        ivDetailAvatar = findViewById(R.id.ivDetailAvatar);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSignature = findViewById(R.id.tvDetailSignature);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        btnBack = findViewById(R.id.btnBack);
        btnSendMsg = findViewById(R.id.btnSendMsg);
    }

    /** 从 Intent 接收好友信息 */
    private void receiveFriendData() {
        friend = (Friend) getIntent().getSerializableExtra("friend");
        if (friend == null) return;

        ivDetailAvatar.setImageResource(FriendAdapter.avatarResOf(friend.getAvatarIndex()));
        tvDetailName.setText(friend.getName());
        tvDetailSignature.setText(friend.getSignature());
        tvDetailGender.setText(friend.getGender());
        tvDetailPhone.setText(friend.getPhone());
        tvDetailEmail.setText(friend.getEmail());
    }
}