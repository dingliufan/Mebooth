package com.mebooth.mylibrary.utils;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/12/15.
 */

public class GlideLoader extends ImageLoader{


    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideImageManager.glideLoader(context, String.valueOf(path), imageView, GlideImageManager.TAG_RECTANGLE);

    }
}
