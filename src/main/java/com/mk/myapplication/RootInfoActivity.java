package com.mk.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RootInfoActivity extends AppCompatActivity {
    private TextView tvName, tvContactNumber, tvHouseNumber, tvGender, tvAge, tvId,tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();
        // 在子线程中获取并显示用户信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                displayUserInfo();
            }
        }).start();

    }

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        tvContactNumber = findViewById(R.id.tv_contact_number);
        tvHouseNumber = findViewById(R.id.tv_house_number);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        tvId = findViewById(R.id.tv_id);
        tvDate = findViewById(R.id.tv_date);
        ImageView ivExitLogin = findViewById(R.id.esclogin);
        ivExitLogin.setOnClickListener(v -> showExitLoginDialog());
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            if (checkLoginStatus()) {
                Intent intent = new Intent(RootInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 结束当前页面
            } else {
                onBackPressed(); // 默认返回操作
            }
        });
    }

    private void displayUserInfo() {
        // 从数据库获取用户信息
        String username = getIntent().getStringExtra("username");
        Resident_dao resident_dao = new Resident_dao();
        Resident resident = resident_dao.getUserInfo(username);


        if (resident != null) {

            // 使用Handler更新UI
            Message message = handler.obtainMessage(1, resident);
            handler.sendMessage(message);

        }
    }

    private Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            Resident resident = (Resident) msg.obj;
            tvName.setText(resident.getResident_name());
            tvContactNumber.setText(resident.getContact_number());
            tvHouseNumber.setText(String.valueOf(resident.getHouse_number()));
            tvGender.setText(resident.getGender());
            tvAge.setText(String.valueOf(resident.getAge()));

            // 处理id的显示
            String id = resident.getId();
            String displayedId = id.substring(0, 4) + "****" + id.substring(id.length() - 4);
            tvId.setText(displayedId);
            tvDate.setText(resident.getCheckInDate());
        }
        return true;
    });
    private void showExitLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认退出登录")
                .setMessage("您确定要退出登录吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutAndNavigateToLogin();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    private void logoutAndNavigateToLogin() {
        clearAdminLoginStatus(); // 修改这里调用清除管理员登录状态的方法

        Intent intent = new Intent(RootInfoActivity.this, CenterHorizontal.class);
        intent.putExtra("username", getIntent().getStringExtra("username")); // 传递用户名到登录页面
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearAdminLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE); // 修改为"admin_login"
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isAdminLoggedIn"); // 移除管理员登录状态
        editor.remove("admin_username"); // 建议也移除管理员用户名
        editor.apply();
    }

    private boolean checkLoginStatus() {
        // 修改此方法以检查管理员登录状态
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE); // 修改为"admin_login"
        return sharedPreferences.getBoolean("isAdminLoggedIn", false);
    }

}
