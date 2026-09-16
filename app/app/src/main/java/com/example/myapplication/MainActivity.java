package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ivUserAvatar;
    private TextView tvWelcome;
    private ListView lvFriends;
    private CustomButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        receiveDataFromLogin();   // 接收登录页传来的数据
        setupFriendList();        // 填充好友列表
        setupListeners();
    }

    private void initViews() {
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvWelcome = findViewById(R.id.tvWelcome);
        lvFriends = findViewById(R.id.lvFriends);
        btnLogout = findViewById(R.id.btnLogout);
    }

    /** 接收 LoginActivity 传来的用户名和头像 */
    private void receiveDataFromLogin() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        int avatarRes = intent.getIntExtra("avatarRes", R.drawable.avatar1);

        if (username != null) {
            tvWelcome.setText("欢迎回来，" + username);
        }
        ivUserAvatar.setImageResource(avatarRes);
    }

    /** 准备好友数据并绑定到 ListView */
    private void setupFriendList() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("小明", R.drawable.avatar1));
        friends.add(new Friend("小红", R.drawable.avatar2));
        friends.add(new Friend("小刚", R.drawable.avatar3));
        friends.add(new Friend("小美", R.drawable.avatar4));
        friends.add(new Friend("阿杰", R.drawable.avatar1));
        friends.add(new Friend("莉莉", R.drawable.avatar2));
        friends.add(new Friend("大壮", R.drawable.avatar3));
        friends.add(new Friend("悠悠", R.drawable.avatar4));

        FriendAdapter adapter = new FriendAdapter(this, friends);
        lvFriends.setAdapter(adapter);
    }

    private void setupListeners() {
        // 点击“退出登录”按钮回到登录页
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}