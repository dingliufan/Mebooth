package com.mebooth.mylibrary.main.home.activity;


import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;

public class ConversationActivity extends BaseTransparentActivity {
    private String title;

    private ImageView back;
    private TextView tvTitle;


    @Override
    protected int getContentViewId() {
        return R.layout.rongim_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initData() {
        super.initData();

        Uri uri = getIntent().getData();

        back = findViewById(R.id.public_back);
        tvTitle = findViewById(R.id.public_title);

        title = uri.getQueryParameter("title").toString();
        tvTitle.setText(title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
