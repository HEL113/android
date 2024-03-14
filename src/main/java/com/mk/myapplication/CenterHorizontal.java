package com.mk.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CenterHorizontal extends AppCompatActivity {
    private static final String TAG = "rootlogin";

    private ImageView ivBack;

    private Button loginButton, esc;
    private EditText usernameText, passwordText;

    private Handler hand1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                // 登录成功的处理逻辑
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(CenterHorizontal.this, RootInfoActivity.class);
                intent.putExtra("username", usernameText.getText().toString());
                startActivity(intent);

                // 存储管理员登录状态和用户名
                saveAdminLoginStatus(true, usernameText.getText().toString());

                finish(); // 结束当前登录页面
            } else if (msg.what == 0) {
                // 用户不存在的处理逻辑
                Toast.makeText(getApplicationContext(), "管理员不存在或密码错误", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查管理员登录状态
        boolean isAdminLoggedIn = checkAdminLoginStatus();

        if (isAdminLoggedIn) {
            // 管理员已登录，直接跳转到用户信息页面
            Intent intent = new Intent(CenterHorizontal.this, RootInfoActivity.class);
            intent.putExtra("admin_username", getAdminUsername());
            startActivity(intent);
            finish(); // 结束当前登录页面
        } else {
            // 管理员未登录，显示登录页面
            setContentView(R.layout.centerhorizontal_activity);
            initViews();
            setupListeners();
        }
    }

    private void initViews() {
        usernameText = findViewById(R.id.usname1);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.button);
        esc = findViewById(R.id.esc);
        ivBack = findViewById(R.id.iv_back);

        // 获取管理员用户名并显示
        String username = getIntent().getStringExtra("admin_username");
        if (username != null) {
            usernameText.setText(username);
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        EditText EditTextname = findViewById(R.id.usname1);
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
                boolean isSuccess = resident_dao.rootlogin(EditTextname.getText().toString(), EditTextpassword.getText().toString());
                Message msg = hand1.obtainMessage();

                if (isSuccess) {
                    msg.what = 1; // 登录成功
                    saveAdminLoginStatus(true,username);

                } else {
                    msg.what = 0; // 用户不存在
                }

                hand1.sendMessage(msg);
            }
        }.start();
    }

    // 检查管理员登录状态和获取用户名
    private boolean checkAdminLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isAdminLoggedIn", false);
    }

    private String getAdminUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
        return sharedPreferences.getString("admin_username", "");
    }

    // 存储管理员登录状态和用户名
    private void saveAdminLoginStatus(boolean isAdminLoggedIn, String adminUsername) {
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAdminLoggedIn", isAdminLoggedIn);
        editor.putString("admin_username", adminUsername);

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity
    }
}
