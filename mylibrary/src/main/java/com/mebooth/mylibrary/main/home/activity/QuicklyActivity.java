package com.mebooth.mylibrary.main.home.activity;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.utils.UIUtils;

public class QuicklyActivity extends BaseTransparentActivity {

    private WebView quicklyWeb;
    private String url;
    private ImageView back;
    private TextView title;

    @Override
    protected int getContentViewId() {
        return R.layout.quickly_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initView() {
        super.initView();

        quicklyWeb = findViewById(R.id.quickly_web);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);

        url = getIntent().getStringExtra("url");

        title.setText(getIntent().getStringExtra("title"));

        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        //访问网页
        quicklyWeb.loadUrl(url);
        quicklyWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        quicklyWeb.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        quicklyWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
