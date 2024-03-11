package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserInfoActivity extends AppCompatActivity {
    private TextView tvName, tvContactNumber, tvHouseNumber, tvGender, tvAge, tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initViews();
        displayUserInfo();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        tvContactNumber = findViewById(R.id.tv_contact_number);
        tvHouseNumber = findViewById(R.id.tv_house_number);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        tvId = findViewById(R.id.tv_id);
    }

    private void displayUserInfo() {
        // 从数据库获取用户信息，这里假设使用 Resident_dao 类中的方法获取信息
        String username = getIntent().getStringExtra("username");
        Resident_dao residentDao = new Resident_dao();
        Resident resident = residentDao.getUserInfo(username);
        System.out.println("resident："+resident);
        if (resident != null) {
            tvName.setText(resident.getResident_name());
            tvContactNumber.setText(resident.getContact_number());
            tvHouseNumber.setText(String.valueOf(resident.getHouse_number()));
            tvGender.setText(resident.getGender());
            tvAge.setText(String.valueOf(resident.getAge()));

            // 处理id的显示
            String id = resident.getId();
            String displayedId = id.substring(0, 4) + "****" + id.substring(id.length() - 4);
            tvId.setText(displayedId);
        }
    }
}
