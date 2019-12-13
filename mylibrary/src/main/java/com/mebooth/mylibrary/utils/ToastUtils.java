package com.mebooth.mylibrary.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.mebooth.mylibrary.main.AppApplication;


public class ToastUtils {

    private static ToastUtils mInstance;
    private Toast mToast;

    public static ToastUtils getInstance() {
        if (mInstance == null) {
            mInstance = new ToastUtils();
        }
        return mInstance;
    }


    public void showToast(String text) {
        try {

            if (mToast == null) {
                mToast = Toast.makeText(AppApplication.getInstance(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        } catch (Exception e) {

        }
    }

    public void showToast(@StringRes int text) {
        try {

            if (mToast == null) {
                mToast = Toast.makeText(AppApplication.getInstance(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        } catch (Exception e) {

        }
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
