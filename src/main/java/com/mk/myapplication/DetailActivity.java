package com.mk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
//网页跳转
public class DetailActivity extends AppCompatActivity {
    private WebView detailWebView;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int imagePosition = intent.getIntExtra("image_position", 0);
        String url = getUrlForImagePosition(imagePosition);

        detailWebView = findViewById(R.id.detail_webview);
        ivBack = findViewById(R.id.iv_back);

        WebSettings webSettings = detailWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用JavaScript（如果网页需要）
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 使用缓存策略

        detailWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        detailWebView.loadUrl(url);

        // 设置返回按钮的点击事件
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // 根据图像位置获取相应的URL
    private String getUrlForImagePosition(int imagePosition) {
        String url;
        switch (imagePosition) {
            case 0:
                url = "https://news.fznews.com.cn/node/12851/20240304/J6U81C4V7u.shtml";
                break;
            case 1:
                url = "https://house.fznews.com.cn/node/21561/20240307/65e98ce0a2ead.shtml";
                break;
            case 2:
                url = "https://news.fznews.com.cn/node/12851/20240307/oZ7c8Z08bF.shtml";
                break;
            case 3:
                url ="https://house.fznews.com.cn/node/21559/20240222/65d74d40d5933.shtml" ;
                break;
            case 4:
                url = "https://money.fznews.com.cn/node/16046/20240301/65e19c08a97bd.shtml";
                break;
            default:
                url = "https://www.fznews.com.cn/";
                break;
        }
        return url;
    }

    @Override
    public void onBackPressed() {
        if (detailWebView.canGoBack()) {
            detailWebView.goBack(); // 如果WebView有历史记录，就后退一页
        } else {
            super.onBackPressed(); // 否则退出Activity
        }
    }

    @Override
    protected void onDestroy() {
        if (detailWebView != null) {
            detailWebView.removeAllViews();
            detailWebView.destroy();
        }
        super.onDestroy();
    }
}
