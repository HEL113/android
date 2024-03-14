
package com.mk.myapplication;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import java.util.ArrayList;
        import java.util.List;

//住户管理
public class Resident_management extends AppCompatActivity {
    private EditText etName, etId, etHouseNumber, etPsw;
    private Spinner etGender, etAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_resident);

        initViews();
        new Thread(new Runnable() {
            @Override
            public void run() {
                displayUserInfo();
            }
        }).start();

    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.id);
        etHouseNumber = findViewById(R.id.et_house_number);
        etPsw=findViewById(R.id.password);
        Button btnSave = findViewById(R.id.btn_save);
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
                Intent intent = new Intent(this, Resident_module.class);
                startActivity(intent);
                finish(); // 结束当前页面
        });
//        List<String> ageList = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            ageList.add(String.valueOf(i));
//        }
//        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageList);
//        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        etAge.setAdapter(ageAdapter);
//
//        // 设置性别下拉框数据
//        List<String> genderList = new ArrayList<>();
//        genderList.add("男");
//        genderList.add("女");
//        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
//        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//       etGender.setAdapter(genderAdapter);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }
    private void displayUserInfo() {
        // 从数据库获取用户信息
        String username = getIntent().getStringExtra("username");
        System.out.println("name1"+username);
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
            etName.setText(resident.getResident_name());
            etHouseNumber.setText(String.valueOf(resident.getHouse_number()));

            etId.setText(resident.getId());
            etPsw.setText(resident.getPassword());
        }
        return true;
    });

    private void saveUserInfo() {
        final String name = etName.getText().toString().trim();
        final String id = etId.getText().toString().trim();
        final int houseNumber = Integer.parseInt(etHouseNumber.getText().toString().trim());
        final String psw = etPsw.getText().toString().trim();

        String username = getIntent().getStringExtra("username");

        if (name.isEmpty()){
            Toast.makeText(this, R.string.us, Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!id.matches("^\\d{17}[0-9Xx]$")) {
                Toast.makeText(this, R.string.idc, Toast.LENGTH_SHORT).show();
                return;
            } else if (houseNumber == 0) {
                Toast.makeText(this, R.string.house_number1, Toast.LENGTH_SHORT).show();
                return;
            } else if (!(psw.matches("(?=.*\\d)(?=.*[a-zA-Z])") ||
                    !(psw.matches("(?=.*\\d)(?=.*[\\.\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+])")) ||
                    !(psw.matches("(?=.*[a-zA-Z])(?=.*[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+])")))) {
                Toast.makeText(this, R.string.pwd, Toast.LENGTH_SHORT).show();
                return;
            }
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从数据库获取最新的用户信息
                Resident_dao resident_dao = new Resident_dao();
                Resident resident = resident_dao.getUserInfo(username);
                boolean isPswChanged = !resident.getPassword().equals(psw); // 判断密码是否有修改
                // 比较从数据库中获取的用户信息与当前输入的信息是否相同
                if (resident != null && resident.getResident_name().equals(name) &&
                        resident.getId().equals(id) && resident.getHouse_number() == houseNumber &&
                        resident.getPassword().equals(psw)) {
                    // 信息相同，不执行保存操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Resident_management.this, "用户信息未修改", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    boolean update = resident_dao.updateUserInfo(username, name, id, houseNumber, psw);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (update) {
                                Toast.makeText(Resident_management.this, "用户信息更新成功", Toast.LENGTH_SHORT).show();

                                // 如果密码有修改，则执行清除登录状态操作
                                if (isPswChanged) {
                                    logoutAndNavigateToLogin();
                                    clearLoginStatus();
                                }else if (!isPswChanged){
                                    Intent intent = new Intent(Resident_management.this, Resident_management.class);
                                    intent.putExtra("username", getIntent().getStringExtra("username"));
                                    startActivity(intent);
                                    finish(); // 关闭当前页面
                                }
                            } else {
                                Toast.makeText(Resident_management.this, "用户信息更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
    private void logoutAndNavigateToLogin() {
        Intent intent = new Intent(Resident_management.this, Resident_module.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
        finish();
    }

    private void clearLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn"); // 只移除登录状态
        editor.apply();
    }

}

