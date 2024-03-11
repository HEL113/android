package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;
//主页面
public class MainActivity extends AppCompatActivity {

    private Banner banner;
    private List<Integer> bannerList;
    private List<String> bannerTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        final TextView imageTitle = findViewById(R.id.imageTitle); // 获取标题的TextView
        initBannerList();
        BannerImageAdapter<Integer> adapter = new BannerImageAdapter<Integer>(bannerList) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        };

        banner.setAdapter(adapter);
        banner.setIndicator(new CircleIndicator(this));

        // 设置轮播图的页面更改监听器
        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 更新标题文本
                imageTitle.setText(bannerTitles.get(position));
            }@Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }


        });
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                Toast.makeText(MainActivity.this, "点击了" +bannerTitles.get(position), Toast.LENGTH_SHORT).show();
                // 根据点击位置跳转到不同的Activity或执行其他操作
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                // 可以通过Intent传递数据，比如传递被点击图片的位置或其他标识
                intent.putExtra("image_position", position);
                startActivity(intent);
            }
        });banner.start();
        LinearLayout residentLogin = findViewById(R.id.resident_login);
        LinearLayout communityService = findViewById(R.id.community_service);
        LinearLayout centerHorizontal = findViewById(R.id.center_horizontal);
        // 为 社区用户登录 设置点击事件监听器
        residentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Resident_module.class);
                // 启动 LoginActivity
                startActivity(intent);
            }
        });
        // 为 进入社区服务 设置点击事件监听器
        communityService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CommunityService.class);
                startActivity(intent);
            }
        });
        // 为 管理员登录 设置点击事件监听器
        centerHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CenterHorizontal.class);
                startActivity(intent);
            }
        });

    }
    private void initBannerList() {
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.detail_4);
        bannerList.add(R.drawable.detail_1);
        bannerList.add(R.drawable.detail_2);
        bannerList.add(R.drawable.detail_3);
        bannerList.add(R.drawable.detail_5);
        bannerTitles = new ArrayList<>();
        bannerTitles.add("天天学习丨中南海月刊（2024.02）");
        bannerTitles.add("公告！晋安多个地块将启动征收！");
        bannerTitles.add("一季度“好年华 聚福州”");
        bannerTitles.add("福建代表团审议国务院组织法修订草案");
        bannerTitles.add("A股3月迎来“开门红”");
    }
}
