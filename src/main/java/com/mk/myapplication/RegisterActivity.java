package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText phoneText;
    private EditText passwordText;
    private EditText password1Text;
    private EditText idCardText;
    private EditText house_number1Text;
    private ImageView ivBack;

    private Button button;
    private Button esc;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // 初始化控件
        usernameText = findViewById(R.id.uname);
        phoneText = findViewById(R.id.phone);
        passwordText = findViewById(R.id.password);
        password1Text = findViewById(R.id.password1);
        idCardText = findViewById(R.id.id1);
        house_number1Text = findViewById(R.id.house_number1);
        button = findViewById(R.id.register);
        esc = findViewById(R.id.esc);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                String phone = phoneText.getText().toString();
                String confirmPassword = password1Text.getText().toString();
                String idCard = idCardText.getText().toString();

                int houseNumber;

                try {
                    houseNumber = Integer.parseInt(house_number1Text.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(RegisterActivity.this, R.string.house_number1, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.use, Toast.LENGTH_SHORT).show();
                } else {
                    if (phone.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, R.string.phone, Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!phone.matches("(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}")){
                        Toast.makeText(RegisterActivity.this, R.string.phone1, Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!password.equals(confirmPassword)) {
                        Toast.makeText(RegisterActivity.this, R.string.mandm, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                        Toast.makeText(RegisterActivity.this, R.string.pwd, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!idCard.matches("^\\d{17}[0-9Xx]$")) {
                        Toast.makeText(RegisterActivity.this, R.string.idc, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (house_number1Text.getText().toString().trim().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, R.string.house_number1, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 使用子线程来执行isUserExist
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Resident_dao resident_dao = new Resident_dao();
                            boolean isExist = resident_dao.isUserExist(phone);

                            // 根据isExist的结果，决定是直接显示用户已存在的Toast，还是继续进行注册流程
                            if (isExist) {
                                // 用户已存在，通过Handler发送消息更新UI
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, R.string.userno, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // 用户不存在，继续注册逻辑
                                String gender = IDCardUtil.getGenderFromIDCard(idCard);
                                Date birthDate = IDCardUtil.getBirthDateFromIDCard(idCard);
                                int age = IDCardUtil.calculateAgeFromBirthDate(birthDate);
                                boolean success = resident_dao.register(new Resident(username, phone, password, houseNumber, gender, age, idCard));

                                // 发送注册结果的消息
                                Message message = handler.obtainMessage(1, success);
                                handler.sendMessage(message);
                            }
                        }
                    }).start();
                }
            }
        });

        esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                boolean isSuccess = (boolean) msg.obj;
                if (isSuccess) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, Resident_module.class);
                    intent.putExtra("username", phoneText.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    });

}
