package com.mebooth.mylibrary.main.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.utils.GlideImageManager;

import razerdp.basepopup.BasePopupWindow;

public class GetDecorationPopup extends BasePopupWindow {

    private ImageView guang;
    private ImageView img;
    private TextView title;
    private TextView content;
    private TextView sure;

    private String imgUrl;
    private String titleStr;
    private String contentStr;
    private Context context;

    private Animation operatingAnim;

    public GetDecorationPopup(Context context, String img, String title, String content) {
        super(context);
        this.context = context;
        this.imgUrl = img;
        this.titleStr = title;
        this.contentStr = content;
        setPopupGravity(Gravity.CENTER);
        initData();
    }

    private void initData() {

        guang = findViewById(R.id.decoration_popup_guang);
        img = findViewById(R.id.decoration_popup_img);
        title = findViewById(R.id.decoration_popup_title);
        content = findViewById(R.id.decoration_popup_content);
        sure = findViewById(R.id.decoration_popup_sure);

        GlideImageManager.glideLoader(context, imgUrl, img, GlideImageManager.TAG_RECTANGLE);
        title.setText(titleStr);
        content.setText(contentStr);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        guangAnimal();
    }

    private void guangAnimal() {

        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        if (operatingAnim != null) {
            guang.startAnimation(operatingAnim);
        } else {
            guang.setAnimation(operatingAnim);
            guang.startAnimation(operatingAnim);
        }

    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.decorationpopup_layout);
    }
}
