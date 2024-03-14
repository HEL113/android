package com.mk.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

public class CommunityService extends AppCompatActivity {
private ImageView ivBack,ivEx,ivComm,ivPhone,ivMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_activity);

        ivBack = findViewById(R.id.iv_back);
        ivEx=findViewById(R.id.iv_ex);


        ivComm = findViewById(R.id.iv_comm);
        ivPhone = findViewById(R.id.iv_phone);
        ivMap = findViewById(R.id.iv_map);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityService.this, MainActivity.class);
                startActivity(intent);
                finish(); // 结束当前页面
            }
        });
        // 执行异步任务，获取 ifex 的值
        SharedPreferences sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        new Thread(() -> {
            Resident_dao resident_dao = new Resident_dao();
            boolean ifex = resident_dao.ifex(username);
            System.out.println("ifex"+ifex);
            runOnUiThread(() -> {
                if (ifex) {
                    ivEx.setImageResource(R.drawable.ex); // 设置图片资源
                } else {
                    ivEx.setImageResource(R.drawable.e1);
                }
            });
        }).start();

        ivEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityService.this,PickupActivity.class);
                startActivity(intent);
            }
        });

        ivComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityService.this,DialogActivity.class);
                startActivity(intent);
            }
        });

        ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityService.this,Phoneinfo.class);
                startActivity(intent);
            }
        });
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityService.this,PickupActivity.class);
                startActivity(intent);
            }
        });
    }
}
