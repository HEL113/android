package com.mk.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity { // 或者继承自 Activity

    private Banner banner;
    private List<Integer> bannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner = findViewById(R.id.banner);
        initBannerList();

        banner.setAdapter(new BannerImageAdapter<Integer>(bannerList) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }
        });

        banner.setIndicator(new CircleIndicator(this)); // 想要有指示器这一步必须有
        banner.start();
    }

    private void initBannerList() {
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.d);
        bannerList.add(R.drawable.d1);
    }
}
