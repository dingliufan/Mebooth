package com.mebooth.mylibrary.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transforms(new CenterCrop(), new GlideRoundTransform(context));

        Glide.with(context.getApplicationContext())
                .load(path)
//                .transform(new CenterCrop(context.getApplicationContext()), new GlideRoundTransform(context.getApplicationContext(),4))
                .apply(options)
                .into(imageView);
    }

//    @Override
//    public ImageView createImageView(Context context) {
//        //圆角
//        return new RoundAngleImageView(context);
//    }
}
