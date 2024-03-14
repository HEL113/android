package com.mk.myapplication;// RootInfoActivity.java
import static com.mk.myapplication.IDCardUtil.calculateAgeFromBirthDate;
import static com.mk.myapplication.IDCardUtil.getGenderFromIDCard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mk.myapplication.R;

import java.util.Date;

public class RootInfoActivity extends AppCompatActivity {
    private androidx.appcompat.widget.SearchView searchView;
    private Toolbar toolbar;
    private ImageView ivBack,Esclogin;

    private TextView tvDate;
    private Button btnSearch, btnSave,btnDelete;
    private String adminUsername;

    private EditText etName, etId, etHouseNumber, etPassword, etGender,etPhone,etAge;

    private TableLayout tbDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
        adminUsername = sharedPreferences.getString("admin_username", "");
        System.out.println("admin_username"+adminUsername);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_root);
        btnSearch= findViewById(R.id.btn_search);
        ivBack = findViewById(R.id.iv_back);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        Esclogin=findViewById(R.id.esclogin);
        Esclogin.setOnClickListener(v -> showExitLoginDialog());
        initViews();
        // 设置返回按钮的点击监听器
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // 结束当前页面
        });
        // 设置Save按钮的点击监听器
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString().trim();
                final String id = etId.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();
                final String houseNumberStr = etHouseNumber.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String gender = etGender.getText().toString().trim();
                final String ageStr = etAge.getText().toString().trim();

                // 验证输入信息是否为空
                if (name.isEmpty() || id.isEmpty() || phone.isEmpty() || houseNumberStr.isEmpty() ||
                        password.isEmpty() || gender.isEmpty() || ageStr.isEmpty()) {
                    Toast.makeText(RootInfoActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 将 houseNumber 和 age 转换为整数
                int houseNumber = Integer.parseInt(houseNumberStr);
                int age = Integer.parseInt(ageStr);
                if (name.isEmpty()) {
                    Toast.makeText(RootInfoActivity.this, R.string.us, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!id.matches("^\\d{17}[0-9Xx]$")) {
                    Toast.makeText(RootInfoActivity.this, R.string.idc, Toast.LENGTH_SHORT).show();
                    return;
                } else if (houseNumber== 0) {
                    Toast.makeText(RootInfoActivity.this, R.string.house_number1, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!(password.matches("(?=.*\\d)(?=.*[a-zA-Z])") ||
                        !(password.matches("(?=.*\\d)(?=.*[\\.\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+])")) ||
                       !(password.matches("(?=.*[a-zA-Z])(?=.*[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+])")))) {
                    Toast.makeText(RootInfoActivity.this, R.string.pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                // 执行保存逻辑
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Admin_dao adminDao = new Admin_dao();
                        boolean userExists = adminDao.isUserExist(phone);

                        runOnUiThread(() -> {
                            if (userExists) {
                                // 用户已存在，提示是否更新用户信息
                                showUpdateUserDialog(name, id, phone, houseNumberStr, password, gender, ageStr);
                            } else {
                                // 用户不存在，提示是否新建用户
                                showCreateUserDialog(name, id, phone, houseNumberStr, password, gender, ageStr);
                            }
                        });
                    }
                }).start();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RootInfoActivity.this);
                builder.setTitle("确认删除");
                String user = etName.getText().toString();
                builder.setMessage("确定要删除用户"+user+"?");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyword = searchView.getQuery().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean deleteSuccess = new Admin_dao().deleteUser(keyword);
                                runOnUiThread(() -> {
                                    if (deleteSuccess) {
                                        Toast.makeText(RootInfoActivity.this, "用户删除成功", Toast.LENGTH_SHORT).show();
                                        // 清空相关信息
                                        etName.setText("");
                                        etPhone.setText("");
                                        etHouseNumber.setText("");
                                        etGender.setText("");
                                        etAge.setText("");
                                        etId.setText("");
                                        tvDate.setText("");
                                        etPassword.setText("");
                                    } else {
                                        Toast.makeText(RootInfoActivity.this, "用户删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击取消按钮，不做任何操作，对话框消失
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // 设置搜索按钮的点击监听器
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchView.getQuery().toString();
                if (keyword.isEmpty()) {
                    Toast.makeText(RootInfoActivity.this, "请输入搜索手机号", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            displayUserInfo();
                        }
                    }).start();
                }

            }
        });

    }

    // 显示更新用户信息对话框
    private void showUpdateUserDialog(String name, String id, String phone, String houseNumberStr, String password, String gender, String ageStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RootInfoActivity.this);
        builder.setTitle("确认更新用户信息");

        builder.setMessage("用户"+phone+"已存在，确定要更新用户信息吗？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行更新用户信息的逻辑
                updateUserInfo(name, id, phone, houseNumberStr, password, gender, ageStr);
            }
        });

        builder.setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 更新用户信息
    private void updateUserInfo(String name, String id, String phone, String houseNumberStr, String password, String gender, String ageStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Admin_dao adminDao = new Admin_dao();
                boolean updateSuccess = adminDao.updateUserInfo(name, id, phone, Integer.parseInt(houseNumberStr), password, gender, Integer.parseInt(ageStr));

                runOnUiThread(() -> {
                    if (updateSuccess) {
                        Toast.makeText(RootInfoActivity.this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RootInfoActivity.this, "更新用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    // 显示新建用户对话框
    private void showCreateUserDialog(String name, String id, String phone, String houseNumberStr, String password, String gender, String ageStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RootInfoActivity.this);
        builder.setTitle("确认新建用户");
        builder.setMessage("确定要新建用户" + phone + "吗？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行新建用户的逻辑
                saveUserInfo(name, id, phone, Integer.parseInt(houseNumberStr), password, gender, Integer.parseInt(ageStr));
            }
        });

        builder.setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 保存新建用户信息
    private void saveUserInfo(String name, String id, String phone, int houseNumber, String password, String gender, int age) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Admin_dao adminDao = new Admin_dao();
                boolean saveSuccess = adminDao.insertUserInfo(name, id, phone, houseNumber, password, gender, age);

                runOnUiThread(() -> {
                    if (saveSuccess) {
                        Toast.makeText(RootInfoActivity.this, "新建用户成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RootInfoActivity.this, "新建用户失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
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

    private void initViews(){
        searchView = findViewById(R.id.search_view);
        btnSearch = findViewById(R.id.btn_search);
        etPhone=findViewById(R.id.phone1);
        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.id);
        etHouseNumber = findViewById(R.id.et_house_number);
        etPassword = findViewById(R.id.password);
        etGender = findViewById(R.id.et_gender);
        tvDate = findViewById(R.id.tv_date);
        btnSave = findViewById(R.id.btn_save);
        etAge=findViewById(R.id.et_age);
    }

    //    private void searchInDatabase(String keyword) {
//        // 在这里编写数据库查询逻辑，根据关键字在数据库中查找相关信息，并将结果展示给用户
//        // 例如使用SQLiteOpenHelper或Room等进行数据库操作
//    }
    private void displayUserInfo() {
        // 从数据库获取用户信息

        String keyword = searchView.getQuery().toString();
        System.out.println("有运行");

        Admin_dao adminDao = new Admin_dao();
        Resident resident = adminDao.getUser(keyword);

        if (resident != null) {

            String gender = getGenderFromIDCard(resident.getId());
            Date birthDate = IDCardUtil.getBirthDateFromIDCard(resident.getId());
            int age = calculateAgeFromBirthDate(birthDate);

            runOnUiThread(() -> {
                etName.setText(resident.getResident_name());
                etPhone.setText(resident.getContact_number());
                etHouseNumber.setText(String.valueOf(resident.getHouse_number()));
                etGender.setText(gender);
                etAge.setText(String.valueOf(age));
                etId.setText(resident.getId());
                tvDate.setText(resident.getCheckInDate());
                etPassword.setText(resident.getPassword());
            });
        }else {
            runOnUiThread(() -> {
              Toast.makeText(RootInfoActivity.this, "该用户不在数据库", Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void logoutAndNavigateToLogin() {
        clearLoginStatus();
        Intent intent = new Intent(RootInfoActivity.this, CenterHorizontal.class);
        intent.putExtra("admin_username", adminUsername); // 传递用户名到登录页面
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

        private void clearLoginStatus() {
            SharedPreferences sharedPreferences = getSharedPreferences("admin_login", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("isAdminLoggedIn"); // 只移除登录状态
            editor.apply();
        }
}
