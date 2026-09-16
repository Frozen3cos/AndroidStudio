package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.UserDao;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegPassword, etRegConfirm, etRegPhone, etRegEmail;
    private ImageView ivRegAvatar;
    private CheckBox cbAgree;
    private TextView tvRegError, tvStrengthLabel, tvGoLogin;
    private View vStrength1, vStrength2, vStrength3;
    private CustomButton btnRegister;

    private final int[] avatarRes = {
            R.drawable.avatar1, R.drawable.avatar2,
            R.drawable.avatar3, R.drawable.avatar4
    };
    private final String[] avatarNames = {"红色头像", "蓝色头像", "绿色头像", "橙色头像"};
    private int selectedAvatarIndex = 0;

    private UserDao userDao;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null
                                && result.getData().getData() != null) {
                            Uri uri = result.getData().getData();
                            // 使用图片时，暂时用默认的下标
                            ivRegAvatar.setImageURI(uri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#4CAF50"));
        }
        setContentView(R.layout.activity_register);

        userDao = new UserDao(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirm = findViewById(R.id.etRegConfirm);
        etRegPhone = findViewById(R.id.etRegPhone);
        etRegEmail = findViewById(R.id.etRegEmail);
        ivRegAvatar = findViewById(R.id.ivRegAvatar);
        cbAgree = findViewById(R.id.cbAgree);
        tvRegError = findViewById(R.id.tvRegError);
        tvStrengthLabel = findViewById(R.id.tvStrengthLabel);
        tvGoLogin = findViewById(R.id.tvGoLogin);
        vStrength1 = findViewById(R.id.vStrength1);
        vStrength2 = findViewById(R.id.vStrength2);
        vStrength3 = findViewById(R.id.vStrength3);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupListeners() {
        ivRegAvatar.setOnClickListener(v -> showAvatarPicker());
        btnRegister.setOnClickListener(v -> handleRegister());
        tvGoLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        // 密码强度实时监听
        etRegPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                updatePasswordStrength(s.toString());
            }
        });
    }

    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setItems(avatarNames, (dialog, which) -> {
                    selectedAvatarIndex = which;
                    ivRegAvatar.setImageResource(avatarRes[which]);
                })
                .show();
    }

    /** 实时更新密码强度指示条 */
    private void updatePasswordStrength(String pwd) {
        int strength = calcStrength(pwd);
        // 重置
        vStrength1.setBackgroundColor(Color.parseColor("#E0E0E0"));
        vStrength2.setBackgroundColor(Color.parseColor("#E0E0E0"));
        vStrength3.setBackgroundColor(Color.parseColor("#E0E0E0"));

        if (pwd.isEmpty()) {
            tvStrengthLabel.setText("密码强度");
            tvStrengthLabel.setTextColor(Color.parseColor("#BBBBBB"));
            return;
        }
        if (strength == 1) {
            vStrength1.setBackgroundColor(Color.parseColor("#FF4444"));
            tvStrengthLabel.setText("弱：建议使用字母+数字组合");
            tvStrengthLabel.setTextColor(Color.parseColor("#FF4444"));
        } else if (strength == 2) {
            vStrength1.setBackgroundColor(Color.parseColor("#FFA500"));
            vStrength2.setBackgroundColor(Color.parseColor("#FFA500"));
            tvStrengthLabel.setText("中：可以再复杂一点");
            tvStrengthLabel.setTextColor(Color.parseColor("#FFA500"));
        } else {
            vStrength1.setBackgroundColor(Color.parseColor("#4CAF50"));
            vStrength2.setBackgroundColor(Color.parseColor("#4CAF50"));
            vStrength3.setBackgroundColor(Color.parseColor("#4CAF50"));
            tvStrengthLabel.setText("强：密码很安全");
            tvStrengthLabel.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    /** 计算密码强度：1=弱，2=中，3=强 */
    private int calcStrength(String pwd) {
        if (pwd.length() < 6) return 1;
        boolean hasLetter = pwd.matches(".*[a-zA-Z].*");
        boolean hasDigit = pwd.matches(".*\\d.*");
        boolean hasSpecial = pwd.matches(".*[^a-zA-Z0-9].*");
        int kinds = (hasLetter ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        if (pwd.length() >= 8 && kinds >= 2) return 3;
        if (kinds >= 2) return 2;
        return 1;
    }

    private void handleRegister() {
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirm = etRegConfirm.getText().toString().trim();
        String phone = etRegPhone.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();

        // 逐项校验
        if (username.isEmpty()) { showError("请输入用户名"); return; }
        if (username.length() < 3 || username.length() > 16) {
            showError("用户名需要 3~16 位字符"); return;
        }
        if (userDao.exists(username)) {
            showError("该用户名已被注册，换一个试试"); return;
        }
        if (password.isEmpty()) { showError("请输入密码"); return; }
        if (password.length() < 6) { showError("密码至少 6 位"); return; }
        if (!password.equals(confirm)) { showError("两次输入的密码不一致"); return; }
        if (phone.isEmpty() || phone.length() != 11) {
            showError("请输入正确的 11 位手机号"); return;
        }
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            showError("手机号格式不正确"); return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("请输入正确的邮箱地址"); return;
        }
        if (!cbAgree.isChecked()) {
            showError("请先阅读并同意用户协议"); return;
        }

        tvRegError.setVisibility(View.GONE);

        // 写入用户存储
        boolean ok = userDao.insert(username, password, phone, email, selectedAvatarIndex);
        if (!ok) {
            showError("注册失败，请稍后再试"); return;
        }

        // 把用户名和头像下标回传给登录页，方便自动填充
        Intent data = new Intent();
        data.putExtra("username", username);
        data.putExtra("avatarIndex", selectedAvatarIndex);
        setResult(RESULT_OK, data);

        Utils.hideKeyboard(this);

        // 弹出注册成功提示
        new AlertDialog.Builder(this)
                .setTitle("注册成功")
                .setMessage("账号「" + username + "」已创建成功，现在去登录吧！")
                .setPositiveButton("去登录", (d, w) -> {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                })
                .setCancelable(false)
                .show();
    }

    private void showError(String msg) {
        tvRegError.setText(msg);
        tvRegError.setVisibility(View.VISIBLE);
    }
}