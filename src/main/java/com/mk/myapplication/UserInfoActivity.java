package com.mk.myapplication;

import static com.mk.myapplication.IDCardUtil.calculateAgeFromBirthDate;
import static com.mk.myapplication.IDCardUtil.getGenderFromIDCard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class UserInfoActivity extends AppCompatActivity {
    private TextView tvName, tvContactNumber, tvHouseNumber, tvGender, tvAge, tvId,tvDate,tvToekn;

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
        tvToekn=findViewById(R.id.tv_token);
        Button btnEdit = findViewById(R.id.btn_edit);

        ImageView ivExitLogin = findViewById(R.id.esclogin);
        ivExitLogin.setOnClickListener(v -> showExitLoginDialog());
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            if (checkLoginStatus()) {
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 结束当前页面
            } else {
                onBackPressed(); // 默认返回操作
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, Resident_management.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                finish(); // 结束当前页面
            }
        });
    }

    private void displayUserInfo() {
        // 从数据库获取用户信息
        String username = getIntent().getStringExtra("username");
        Resident_dao resident_dao = new Resident_dao();
        Resident resident = resident_dao.getUserInfo(username);


        if (resident != null) {
            String gender = getGenderFromIDCard(resident.getId());
            Date birthDate = IDCardUtil.getBirthDateFromIDCard(resident.getId());

            int age = calculateAgeFromBirthDate(birthDate);
            // 使用Handler更新UI
            Message message = handler.obtainMessage(1, resident);
            Bundle data = new Bundle();
            data.putString("gender", gender);
            data.putInt("age", age);
            message.setData(data);
            handler.sendMessage(message);

        }
    }
    private Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            Resident resident = (Resident) msg.obj;
            tvName.setText(resident.getResident_name());
            tvContactNumber.setText(resident.getContact_number());
            tvHouseNumber.setText(String.valueOf(resident.getHouse_number()));
            tvGender.setText(msg.getData().getString("gender"));
            tvAge.setText(String.valueOf(msg.getData().getInt("age")));
            tvToekn.setText(resident.getToken());
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
        clearLoginStatus();

        Intent intent = new Intent(UserInfoActivity.this, Resident_module.class);
        intent.putExtra("username", getIntent().getStringExtra("username")); // 传递用户名到登录页面
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn"); // 只移除登录状态
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        if (checkLoginStatus()) {
            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkLoginStatus() {
        // 检查登录状态的逻辑
        return false;
    }

}
