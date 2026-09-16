package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String SP_NAME = "login_prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR_INDEX = "avatar_index";

    private EditText etUsername, etPassword;
    private ImageView ivAvatar;
    private CheckBox cbRemember;
    private CustomButton btnLogin;
    private TextView tvError, tvGoRegister;

    private final int[] avatarRes = {
            R.drawable.avatar1, R.drawable.avatar2,
            R.drawable.avatar3, R.drawable.avatar4
    };
    private final String[] avatarNames = {"红色头像", "蓝色头像", "绿色头像", "橙色头像"};

    private int selectedAvatar = avatarRes[0];
    private boolean isPasswordVisible = false;

    private UserStore userStore;

    /** 注册页返回结果的接收器 */
    private final ActivityResultLauncher<Intent> registerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String username = result.getData().getStringExtra("username");
                            int avatarIdx = result.getData().getIntExtra("avatarIndex", 0);
                            if (username != null) {
                                etUsername.setText(username);
                                etPassword.setText("");
                                etPassword.requestFocus();
                                if (avatarIdx >= 0 && avatarIdx < avatarRes.length) {
                                    selectedAvatar = avatarRes[avatarIdx];
                                    ivAvatar.setImageResource(selectedAvatar);
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userStore = new UserStore(this);

        initViews();
        loadRememberedAccount();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivAvatar = findViewById(R.id.ivAvatar);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvGoRegister = findViewById(R.id.tvGoRegister);
    }

    private void loadRememberedAccount() {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean remember = sp.getBoolean(KEY_REMEMBER, false);
        if (remember) {
            etUsername.setText(sp.getString(KEY_USERNAME, ""));
            etPassword.setText(sp.getString(KEY_PASSWORD, ""));
            cbRemember.setChecked(true);
            int idx = sp.getInt(KEY_AVATAR_INDEX, 0);
            if (idx >= 0 && idx < avatarRes.length) {
                selectedAvatar = avatarRes[idx];
                ivAvatar.setImageResource(selectedAvatar);
            }
        } else {
            // 即使没勾选记住密码，也把上次的用户名填上，方便切换账号
            String lastUsername = sp.getString(KEY_USERNAME, "");
            if (!lastUsername.isEmpty()) {
                etUsername.setText(lastUsername);
            }
        }
    }

    private void setupListeners() {
        ivAvatar.setOnClickListener(v -> showAvatarPicker());
        btnLogin.setOnClickListener(v -> handleLogin());
        setupPasswordToggle();
        findViewById(android.R.id.content).setOnClickListener(v -> Utils.hideKeyboard(this));

        // 去注册
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            registerLauncher.launch(intent);
        });
    }

    private void setupPasswordToggle() {
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable d = etPassword.getCompoundDrawables()[2];
                if (d != null) {
                    int iconW = d.getBounds().width();
                    int iconStart = etPassword.getRight()
                            - etPassword.getPaddingEnd() - iconW;
                    if (event.getRawX() >= iconStart) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.ic_visibility_off, 0);
            isPasswordVisible = false;
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.ic_visibility_on, 0);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void showAvatarPicker() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setItems(avatarNames, (dialog, which) -> {
                    selectedAvatar = avatarRes[which];
                    ivAvatar.setImageResource(selectedAvatar);
                })
                .show();
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) { showError("用户名不能为空"); return; }
        if (username.length() < 3) { showError("用户名至少需要 3 位字符"); return; }
        if (password.isEmpty()) { showError("密码不能为空"); return; }
        if (password.length() < 6) { showError("密码至少需要 6 位字符"); return; }

        // 账号是否存在
        if (!userStore.isUserExists(username)) {
            showError("该账号未注册，请点击下方“立即注册”");
            return;
        }
        // 密码是否正确
        if (!userStore.validateLogin(username, password)) {
            showError("用户名或密码错误");
            return;
        }

        tvError.setVisibility(View.GONE);
        saveOrClearAccount(username, password);
        Utils.hideKeyboard(this);
        showLoadingAndJump();
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void showLoadingAndJump() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("登录中，请稍候...");
        pd.setCancelable(false);
        pd.show();

        btnLogin.postDelayed(() -> {
            pd.dismiss();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1200);
    }

    private void saveOrClearAccount(String username, String password) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (cbRemember.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASSWORD);
        }

        // 无论是否记住密码，都保存用户名和头像下标，方便后续页面展示
        editor.putString(KEY_USERNAME, username);
        int idx = 0;
        for (int i = 0; i < avatarRes.length; i++) {
            if (avatarRes[i] == selectedAvatar) { idx = i; break; }
        }
        editor.putInt(KEY_AVATAR_INDEX, idx);
        editor.apply();
    }
}