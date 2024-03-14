package com.mk.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DialogActivity extends AppCompatActivity {

    private TextView messageBoard;
    private EditText newMessageInput;
    private Button postButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ivBack=findViewById(R.id.iv_back);
        messageBoard = findViewById(R.id.message_board);
        newMessageInput = findViewById(R.id.new_message_input);
        postButton = findViewById(R.id.post_button);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommunityService.class);
            startActivity(intent);
            finish(); // 结束当前页面
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage = newMessageInput.getText().toString().trim();
                if (!TextUtils.isEmpty(newMessage)) {
                    if (checkLoginStatus()) { // 检查登录状态
                        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                        String username = sharedPreferences.getString("username", "");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Resident_dao resident_dao = new Resident_dao();
                                String residentName = resident_dao.getuser(username);
                                if (residentName != null) {
                                    String residentFirstLetter = Character.toString(residentName.charAt(0));
                                    String newMessageWithPrefix = residentFirstLetter + "xx :" + newMessage;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveMessage(newMessageWithPrefix);
                                            updateMessageBoard();
                                            newMessageInput.setText("");
                                        }
                                    });
                                } else {
                                    // 处理未找到居民名称的情况
                                    String newMessageWithPrefix = "游客评论 :" + newMessage;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveMessage(newMessageWithPrefix);
                                            updateMessageBoard();
                                            newMessageInput.setText("");
                                        }
                                    });
                                }
                            }
                        }).start();
                    } else {
                        // 未登录状态，评论标记为游客评论
                        String newMessageWithPrefix = "游客评论 :" + newMessage;

                        saveMessage(newMessageWithPrefix);
                        updateMessageBoard();
                        newMessageInput.setText("");
                    }
                }
            }
        });

        // 初始化留言板内容
        updateMessageBoard();
    }

    private boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void saveMessage(String message) {
        // 可以将留言信息保存到SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("messages", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0);
        editor.putString("message_" + count, message);
        editor.putInt("count", count + 1);
        editor.apply();
    }

    private void updateMessageBoard() {
        // 从数据存储中获取留言信息，并更新留言板内容
        SharedPreferences sharedPreferences = getSharedPreferences("messages", MODE_PRIVATE);
        int count = sharedPreferences.getInt("count", 0);
        StringBuilder messages = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String message = sharedPreferences.getString("message_" + i, "");
            messages.append(message).append("\n");
        }
        messageBoard.setText(messages.toString());
    }

    private void clearMessageBoard() {
        // 清空留言板内容
        SharedPreferences sharedPreferences = getSharedPreferences("messages", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply(); // 清空所有留言
        messageBoard.setText(""); // 清空留言板显示
    }
}
