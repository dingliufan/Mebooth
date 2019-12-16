package com.mebooth.text;


import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.main.base.BaseTransparentActivity;

import butterknife.BindView;

public class ConversationActivity extends BaseTransparentActivity {

    @BindView(R.id.public_rongback)
    ImageView back;
    @BindView(R.id.public_rongtitle)
    TextView tvTitle;
    private String title;

    @Override
    protected int getContentViewId() {
        return R.layout.rongim_layout;
    }

    @Override
    protected void initData() {
        super.initData();
        Uri uri = getIntent().getData();

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
