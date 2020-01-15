package com.mebooth.mylibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mebooth.mylibrary.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GlideImageManager {

    /**
     * 通用的ImageView
     *
     * @param context
     * @param url
     * @param iv
     * @param tag     (矩形:TAG_RECTANGLE   圆形:TAG_ROUND   圆角:TAG_FILLET)
     */
    public static void glideLoader(Context context, String url, ImageView iv, int tag) {

        boolean allowLoad = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !((Activity) context).isDestroyed()) allowLoad = true;
            else allowLoad = false;
        }
        if (tag == TAG_RECTANGLE) {

            if (allowLoad) Glide.with(context).load(url).apply(rectangleOptions).into(iv);
        } else if (tag == TAG_ROUND) {

            if (allowLoad) Glide.with(context).load(url).apply(roundOptions).into(iv);
        } else if (tag == TAG_FILLET) {

            if (allowLoad) Glide.with(context).load(url).apply(filletOptions).into(iv);
        }
    }

    /**
     * 动态设置图片的圆角
     *
     * @param context
     * @param url
     * @param iv
     * @param roundNum (圆角的角度)
     */
    public static void glideLoaderAngle(Context context, String url, ImageView iv, int roundNum) {

        boolean allowLoad = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !((Activity) context).isDestroyed()) allowLoad = true;
            else allowLoad = false;
        }
        if (allowLoad) {
            angleOptions.transform(new RoundedCorners(roundNum));
            Glide.with(context).load(url).apply(angleOptions).into(iv);
        }
    }

    /**
     * 动态设置图片的圆角
     *
     * @param context
     * @param url
     * @param iv
     * @param roundNum
     * @param loadingImg
     * @param erroImg
     * @param emptyImg
     */
    public static void glideLoaderAngle(Context context, String url, ImageView iv, int roundNum, int loadingImg, int erroImg, int emptyImg) {

        boolean allowLoad = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !((Activity) context).isDestroyed()) allowLoad = true;
            else allowLoad = false;
        }
        if (allowLoad) {
            RequestOptions itemOptions = new RequestOptions()
                    .placeholder(loadingImg)
                    .error(erroImg)
                    .fallback(emptyImg)
                    .centerCrop()
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            itemOptions.transform(new RoundedCorners(roundNum));
            Glide.with(context).load(url).apply(angleOptions).into(iv);
        }
    }

    /**
     * 动态设置图片的圆角(图片居中等比放大，且居中显示)
     *
     * @param context
     * @param url
     * @param iv
     * @param roundNum (圆角的角度)
     */
    public static void glideLoaderLoaderAngleCenter(Context context, String url, ImageView iv, int roundNum) {

        boolean allowLoad = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !((Activity) context).isDestroyed()) allowLoad = true;
            else allowLoad = false;
        }
        if (allowLoad) {
            angleOptions.transform(new GlideRoundTransform(context, roundNum));
            Glide.with(context)
                    .load(url)
                    .apply(angleOptions)
                    .into(iv);
        }
    }

    //上边圆角
    public static void glideLoadeTop(Context context, String url, ImageView iv , int loadingImg, int erroImg, int emptyImg) {

        boolean allowLoad = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != context && !((Activity) context).isDestroyed()) {

                allowLoad = true;
            } else {

                allowLoad = false;
            }
        }
        RequestOptions options = new RequestOptions()
                .placeholder(loadingImg)
                .error(erroImg)
                .fallback(emptyImg)
                .centerInside()
                .priority(Priority.IMMEDIATE)
                .transform(new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.TOP))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if (allowLoad)
            Glide.with(context).load(url).apply(options).into(iv);

    }

    //矩形
    public static final int TAG_RECTANGLE = 0;
    public static RequestOptions rectangleOptions = new RequestOptions()
            .placeholder(R.drawable.errorimage)
            .error(R.drawable.errorimage)
            .fallback(R.color.bg_ffffff)
            .priority(Priority.IMMEDIATE)
//            .diskCacheStrategy(DiskCacheStrategy.ALL);
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    //圆形
    public static final int TAG_ROUND = 1;
    public static RequestOptions roundOptions = new RequestOptions()
            .placeholder(R.drawable.defaulticon)
            .error(R.drawable.defaulticon)
            .fallback(R.color.bg_ffffff)
            .centerCrop()
            .transform(new CircleCrop())
            .priority(Priority.IMMEDIATE)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    //圆角(8dip)
    public static final int TAG_FILLET = 2;
    public static RequestOptions filletOptions = new RequestOptions()
            .placeholder(R.drawable.errorimage)
            .error(R.drawable.errorimage)
            .fallback(R.color.bg_ffffff)
            .centerCrop()
            .transform(new RoundedCorners(8))
            .priority(Priority.IMMEDIATE)
            .diskCacheStrategy(DiskCacheStrategy.ALL);


    //设置圆角的公共Options
    public static RequestOptions angleOptions = new RequestOptions()
            .placeholder(R.drawable.errorimage)
            .error(R.drawable.errorimage)
            .fallback(R.color.bg_ffffff)
            .centerCrop()
            .priority(Priority.IMMEDIATE)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

}
