package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FriendDetailActivity extends AppCompatActivity {

    private ImageView ivDetailAvatar;
    private TextView tvDetailName, tvDetailSignature;
    private TextView tvDetailGender, tvDetailPhone, tvDetailEmail;
    private CustomButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // C9
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#4CAF50"));
        }
        setContentView(R.layout.activity_friend_detail);
        initViews();
        receiveFriendData();
        btnBack.setOnClickListener(v -> {
            finish();
            // C2: 返回淡出动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void initViews() {
        ivDetailAvatar = findViewById(R.id.ivDetailAvatar);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSignature = findViewById(R.id.tvDetailSignature);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        btnBack = findViewById(R.id.btnBack);
    }

    private void receiveFriendData() {
        Friend friend = (Friend) getIntent().getSerializableExtra("friend");
        if (friend == null) return;
        ivDetailAvatar.setImageResource(friend.getAvatarRes());
        tvDetailName.setText(friend.getName());
        tvDetailSignature.setText(friend.getSignature());
        tvDetailGender.setText(friend.getGender());
        tvDetailPhone.setText(friend.getPhone());
        tvDetailEmail.setText(friend.getEmail());
    }
}