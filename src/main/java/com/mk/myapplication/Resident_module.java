package com.mk.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

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
                finish(); // 结束当前登录页面
            } else if (msg.what == 0) {
                // 用户不存在的处理逻辑
                Toast.makeText(getApplicationContext(), "用户不存在或密码错误", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查登录状态
        boolean isLoggedIn = checkLoginStatus();

        if (isLoggedIn) {
            // 用户已登录，直接跳转到用户信息页面
            Intent intent = new Intent(Resident_module.this, UserInfoActivity.class);
            intent.putExtra("username", getUsername());
            startActivity(intent);
            finish(); // 结束当前登录页面
        } else {
            // 用户未登录，显示登录页面
            setContentView(R.layout.login);

            initViews();
            setupListeners();
        }
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
                    resident_dao.updateCheckInDate(usernameText.getText().toString());
                    String token = generateToken(); // 生成6位随机数作为token

                    resident_dao.infotoken(usernameText.getText().toString(),token);
                    // 存储登录状态和用户名
                    saveLoginStatus(true, username);
                } else {
                    msg.what = 0; // 用户不存在
                }
                hand1.sendMessage(msg);
            }
        }.start();
    }

    private String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            token.append(chars.charAt(index));
        }

        return token.toString();
    }

    private void register() {
        // 跳转到注册Activity的逻辑
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // 检查登录状态和获取用户名
    private boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }


    // 存储登录状态和用户名
    private void saveLoginStatus(boolean isLoggedIn, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.putString("username", username);
        editor.apply();
    }
}
