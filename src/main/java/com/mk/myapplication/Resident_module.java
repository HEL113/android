package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Resident_module extends AppCompatActivity {
    private static final String TAG = "login";

    private ImageView ivBack;
    private EditText usernameText, passwordText;
    private Button loginButton, zhuceButton;

    private Handler hand1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                // 登录成功的处理逻辑
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Resident_module.this, UserInfoActivity.class);
                intent.putExtra("username", usernameText.getText().toString());
                startActivity(intent);
            } else if (msg.what == 0) {
                // 用户不存在的处理逻辑
                Toast.makeText(getApplicationContext(), "用户不存在或密码错误", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // 确保这里的布局文件名与你的XML文件名相匹配

        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameText = findViewById(R.id.phone);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        zhuceButton = findViewById(R.id.register);
        ivBack = findViewById(R.id.iv_back);
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText(username);
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        loginButton.setOnClickListener(v -> login());
        zhuceButton.setOnClickListener(v -> register());
    }

    private void login() {
        EditText EditTextname = findViewById(R.id.phone);
        EditText EditTextpassword = findViewById(R.id.password);
        String username = EditTextname.getText().toString();
        String password = EditTextpassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "用户名和密码不能为空", Toast.LENGTH_LONG).show();
            return; // 直接返回，不执行登录操作
        }

        new Thread(){
            @Override
            public void run () {
                Resident_dao resident_dao = new Resident_dao();
                boolean isSuccess = resident_dao.login(EditTextname.getText().toString(), EditTextpassword.getText().toString());
                Message msg = hand1.obtainMessage();

                if (isSuccess) {
                    msg.what = 1; // 登录成功
                } else {
                    msg.what = 0; // 用户不存在
                }

                hand1.sendMessage(msg);
            }
        }.start();
    }

    private void register() {
        // 跳转到注册Activity的逻辑
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}