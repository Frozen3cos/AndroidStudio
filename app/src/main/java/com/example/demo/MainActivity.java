package com.example.myfirstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 声明控件变量
    private EditText etInput;
    private CustomButton btnCustom;
    private Button btnNormal;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载布局文件
        setContentView(R.layout.activity_main);

        // 初始化控件 (通过findViewById获取布局中的控件)
        initViews();

        // 设置监听器
        setupListeners();
    }

    /**
     * 初始化所有控件
     */
    private void initViews() {
        etInput = findViewById(R.id.etInput);
        btnCustom = findViewById(R.id.btnCustom);
        btnNormal = findViewById(R.id.btnNormal);
        tvResult = findViewById(R.id.tvResult);
    }

    /**
     * 设置所有控件的监听事件
     */
    private void setupListeners() {
        // 自定义按钮点击事件
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    tvResult.setText("自定义按钮点击，输入内容：" + inputText);
                    Toast.makeText(MainActivity.this,
                            "自定义按钮被点击！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "请先输入内容！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 普通按钮点击事件
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    tvResult.setText("普通按钮点击，输入内容：" + inputText);
                    Toast.makeText(MainActivity.this,
                            "普通按钮被点击！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "请先输入内容！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}