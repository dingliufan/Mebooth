package com.mebooth.mylibrary.main.home.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.fragment.NewMainFragment;


public class NewMainActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmain_layout);
        switchContent();

    }

    public void switchContent() {
        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getSupportFragmentManager().beginTransaction().replace(R.id.onef, NewMainFragment.newInstance()).commitAllowingStateLoss();
    }

}
