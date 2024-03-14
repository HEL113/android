package com.mk.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PickupActivity extends AppCompatActivity {
    private DeliveryList deliveryList;
    private String username;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.express_activity);

        // 初始化控件
        ivBack = findViewById(R.id.iv_back);

        // 设置返回按钮点击事件
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(PickupActivity.this, CommunityService.class);
            startActivity(intent);
            finish(); // 结束当前页面
        });

        // 获取用户名
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // 异步获取快递列表数据
        fetchDeliveryListInBackground(username);
    }

    private void fetchDeliveryListInBackground(final String username) {
        new Thread(() -> {
            List<Express> deliveryListData = getDeliveryListFromDatabase(username);
            runOnUiThread(() -> updateDeliveryList(deliveryListData));
            System.out.println("mysql1" + deliveryListData);
        }).start();
    }

    private List<Express> getDeliveryListFromDatabase(String phoneNumber) {
        List<Express> deliveryListData = new ArrayList<>();
        try {
            Resident_dao residentDao = new Resident_dao();
            List<Express> expressList = residentDao.seexp(phoneNumber);
            if (expressList != null) {
                deliveryListData.addAll(expressList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveryListData;
    }

    private void updateDeliveryList(final List<Express> deliveryListData) {
        runOnUiThread(() -> {
            if (deliveryListData.isEmpty()) {
                // 如果快递列表为空，则显示“暂无包裹”信息
                TextView textView = new TextView(PickupActivity.this);
                textView.setText("暂无包裹");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                textView.setLayoutParams(params);
                ((LinearLayout) findViewById(R.id.container)).addView(textView);
            } else {
                // 如果有快递信息，则显示列表
                deliveryList = new DeliveryList(PickupActivity.this, deliveryListData);
                deliveryList.displayDeliveryList(findViewById(R.id.container));
            }
        });
    }


}
