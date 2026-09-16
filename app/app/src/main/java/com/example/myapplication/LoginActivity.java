package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageView ivAvatar;
    private CustomButton btnLogin;

    // 可选头像的资源ID数组
    private final int[] avatarRes = {
            R.drawable.avatar1, R.drawable.avatar2,
            R.drawable.avatar3, R.drawable.avatar4
    };
    private final String[] avatarNames = {"红色头像", "蓝色头像", "绿色头像", "橙色头像"};

    // 当前选中的头像资源ID（默认第一个）
    private int selectedAvatar = avatarRes[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupListeners() {
        // 点击头像弹出选择框
        ivAvatar.setOnClickListener(v -> showAvatarPicker());

        // 点击登录按钮
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    /** 弹出头像选择对话框 */
    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setItems(avatarNames, (dialog, which) -> {
                    selectedAvatar = avatarRes[which];
                    ivAvatar.setImageResource(selectedAvatar);
                })
                .show();
    }

    /** 校验输入，并通过 Intent 传递数据到 MainActivity */
    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳转到 MainActivity，携带用户名和头像资源ID
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("avatarRes", selectedAvatar);
        startActivity(intent);
        finish(); // 登录后关闭登录页，防止返回键退回
    }
}