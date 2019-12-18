package com.mebooth.text;

import android.content.Intent;
import android.view.View;
import android.widget.Button;


import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.activity.FriendActivity;
import com.mebooth.mylibrary.main.home.activity.NewMainActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;

import butterknife.BindView;
import io.rong.imkit.RongIM;

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
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
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
//                RongIM.getInstance().startPrivateChat(MainActivity.this, "10001", "标题");

//                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
////                startActivity(intent);
            }
        });

    }
}
