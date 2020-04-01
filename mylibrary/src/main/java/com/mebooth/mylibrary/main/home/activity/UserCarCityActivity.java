package com.mebooth.mylibrary.main.home.activity;

import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.fragment.ChooseProvincesFragment;
import com.mebooth.mylibrary.utils.UIUtils;

public class UserCarCityActivity extends BaseTransparentActivity {

    public static String chooseCity = "";

    @Override
    protected int getContentViewId() {
        return R.layout.usercarcity_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        chooseCity = "";
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ChooseProvincesFragment())
                    .commit();
        }

    }
}
