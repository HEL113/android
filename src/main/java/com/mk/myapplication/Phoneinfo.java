package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Phoneinfo extends AppCompatActivity {

    EditText etName, etId, etContactNumber, etContactNumber1;
    Button btnEdit;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phoneinfo);

        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.et_id);
        etContactNumber = findViewById(R.id.et_contact_number);
        etContactNumber1 = findViewById(R.id.et_contact_number1);
        btnEdit = findViewById(R.id.btn_edit);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(Phoneinfo.this, CommunityService.class);
            startActivity(intent);
            finish(); // 结束当前页面
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });
    }

    private void updateDatabase() {
        final String name = etName.getText().toString().trim();
        final String id = etId.getText().toString().trim();
        final String contactNumber = etContactNumber.getText().toString().trim();
        final String contactNumber1 = etContactNumber1.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || contactNumber.isEmpty() || contactNumber1.isEmpty()) {
            Toast.makeText(Phoneinfo.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(Phoneinfo.this, R.string.us, Toast.LENGTH_SHORT).show();
        } else if (!id.matches("^\\d{17}[0-9Xx]$")) {
            Toast.makeText(Phoneinfo.this, R.string.idc, Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Resident_dao resident_dao = new Resident_dao();
                boolean isExist = resident_dao.setphone(name, id, contactNumber);

                if (isExist) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isUpdated = resident_dao.updatephone(name, id, contactNumber, contactNumber1);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isUpdated) {
                                        Toast.makeText(Phoneinfo.this, "用户信息更新成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Phoneinfo.this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Phoneinfo.this, "条件不符合", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    }
