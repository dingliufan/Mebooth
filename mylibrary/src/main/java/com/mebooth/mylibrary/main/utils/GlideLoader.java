package com.mebooth.mylibrary.main.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.mebooth.mylibrary.imagepicker.loader.ImageLoader;
import com.mebooth.mylibrary.utils.GlideImageManager;

/**
 * Created by Administrator on 2017/12/15.
 */

public class GlideLoader implements ImageLoader {


    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        GlideImageManager.glideLoader(activity, path, imageView, GlideImageManager.TAG_RECTANGLE);

    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {

    }

    @Override
    public void clearMemoryCache() {

    }
}
