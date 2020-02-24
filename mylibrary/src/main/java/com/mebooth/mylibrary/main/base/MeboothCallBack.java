package com.mebooth.mylibrary.main.base;

import android.app.Activity;
import android.graphics.Bitmap;

public abstract class MeboothCallBack {

    //登陆
    public abstract void setLogin();
    //扫码
    public abstract void setIntentScan(Activity activity);

    //分享
    public abstract void setShare(String way, String url, Bitmap image, String title, String description);


}
