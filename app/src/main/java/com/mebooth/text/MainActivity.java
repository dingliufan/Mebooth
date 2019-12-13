package com.mebooth.text;

import android.content.Intent;
import android.view.View;
import android.widget.Button;


import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.activity.NewMainActivity;

import butterknife.BindView;

public class MainActivity extends BaseTransparentActivity {
    @BindView(R.id.bt_true)
    Button btTrue;
    @BindView(R.id.bt_false)
    Button btFalse;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

        btTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewMainActivity.class);
                startActivity(intent);
            }
        });

        btFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
}
