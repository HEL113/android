package com.mk.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mk.myapplication.Express;
import com.mk.myapplication.R;
import com.mk.myapplication.Resident_dao;

import java.util.List;

public class DeliveryList {
    private Context mContext;
    private List<Express> mDeliveryList;
    private String username;

    public DeliveryList(Context context, List<Express> deliveryList) {
        mContext = context;
        mDeliveryList = deliveryList;
    }

    @SuppressLint("SetTextI18n")
    public void displayDeliveryList(ViewGroup container) {
        for (Express express : mDeliveryList) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_express, container, false);
            TextView tvName = itemView.findViewById(R.id.tvName);
            TextView tvContact = itemView.findViewById(R.id.tv_contact_number);
            TextView tvExp = itemView.findViewById(R.id.tv_exp);
            EditText etToken = itemView.findViewById(R.id.etToken);
            Button btnPickupItem = itemView.findViewById(R.id.btnPickupItem);

            tvName.setText("姓名：" + express.getName());
            tvContact.setText("手机号：" + express.getContact_number());
            tvExp.setText("快递号：" + express.getExname());

            btnPickupItem.setOnClickListener(view -> {
                String token = etToken.getText().toString(); // 获取输入的 token
                new CheckTokenTask().execute(token);
            });

            container.addView(itemView);
        }
    }

    private class CheckTokenTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String token = params[0];
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
            username = sharedPreferences.getString("username", "");
            Resident_dao resident_dao = new Resident_dao();
            return resident_dao.iftoken(username, token);
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(mContext, "该快递已被取件", Toast.LENGTH_SHORT).show();
                Resident_dao resident_dao = new Resident_dao();
                resident_dao.deleteexpInBackground(username);
                Intent intent = new Intent(mContext, PickupActivity.class);
                mContext.startActivity(intent);
                ((PickupActivity) mContext).finish(); // 结束当前页面
            } else {
                Toast.makeText(mContext, "验证码输入错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
