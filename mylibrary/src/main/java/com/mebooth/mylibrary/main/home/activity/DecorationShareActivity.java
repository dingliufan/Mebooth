package com.mebooth.mylibrary.main.home.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DecorationShareActivity extends BaseTransparentActivity {

    private ImageView img;
    private Bitmap bitmap;

    @Override
    protected int getContentViewId() {
        return R.layout.decorationshare_layout;
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

        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        img = findViewById(R.id.decorationshareimg);
        /*为什么图片一定要转化为 Bitmap格式的！！ */
        bitmap = getLoacalBitmap(getIntent().getStringExtra("imgurl")); //从本地取图片(在cdcard中获取)  //
        img.setImageBitmap(bitmap); //设置Bitmap

    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();

    }
}
