package com.mebooth.mylibrary.main.NewsPublishAdapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.adapter.GridImageAdapter;
import com.mebooth.mylibrary.main.home.activity.NewsPublishActivity;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.NewPublish;
import com.mebooth.mylibrary.main.utils.GlideEngine;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.main.utils.PictureConfig;
import com.mebooth.mylibrary.utils.GlideImageManager;

import java.util.ArrayList;
import java.util.List;

import static com.mebooth.mylibrary.main.home.activity.NewsPublishActivity.selectList;

public class PublishHeaderAdapter implements ItemViewDelegate<NewPublish> {

    private Context context;
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener;
    public static String publishNewsTitle = "";
    private NoPublish noPublish;

    public PublishHeaderAdapter(Context context, GridImageAdapter.onAddPicClickListener onAddPicClickListener, NoPublish noPublish) {
        this.context = context;
        this.onAddPicClickListener = onAddPicClickListener;
        this.noPublish = noPublish;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.newspublishheader;
    }

    @Override
    public boolean isForViewType(NewPublish item, int position) {

        if (position == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void convert(ViewHolder holder, NewPublish newPublish, int position) {

        EditText edt = holder.getView(R.id.newspublish_title);

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                publishNewsTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.setOnClickListener(R.id.newspublishheader, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPicClickListener.onAddPicClick();
            }
        });
        holder.setOnClickListener(R.id.newspublishedit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noPublish.showAddButton();
            }
        });


        if (selectList.size() != 0) {
            GlideImageManager.glideLoader(context, selectList.get(0).getCutPath(), (ImageView) holder.getView(R.id.newspublishheader_cover), GlideImageManager.TAG_RECTANGLE);

        }
    }

}
