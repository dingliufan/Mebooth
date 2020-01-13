package com.mebooth.mylibrary.main.RecommendMultiItemView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.QuicklyActivity;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.utils.GlideImageManager;

import java.util.ArrayList;

public class RecommendItemHeaderVIew implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private FlushJson bannerJson;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend;

    public RecommendItemHeaderVIew(Context context, FlushJson bannerJson, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend) {
        this.context = context;
        this.bannerJson = bannerJson;
        this.recommend = recommend;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommenditem_headerviewlayout;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {

        if(position == 0){

            return true;

        }else{
            return false;
        }

    }

    @Override
    public void convert(ViewHolder holder, GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        GlideImageManager.glideLoader(context, bannerJson.getData().getConfig().getImage(), (ImageView) holder.getView(R.id.recommenditem_headerimg), GlideImageManager.TAG_FILLET);

        holder.setOnClickListener(R.id.recommenditem_headerimg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, NewDetailsActivity.class);
                intent.putExtra("relateid",bannerJson.getData().getConfig().getNewsid());
                context.startActivity(intent);

            }
        });


        holder.setOnClickListener(R.id.buy_car, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, QuicklyActivity.class);
                intent.putExtra("type","1");
                intent.putExtra("title","购车指南");
                context.startActivity(intent);

            }
        });
        holder.setOnClickListener(R.id.publicusecar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuicklyActivity.class);
                intent.putExtra("type","2");
                intent.putExtra("title","公务用车");
                context.startActivity(intent);
            }
        });
        holder.setOnClickListener(R.id.logisticsusecar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuicklyActivity.class);
                intent.putExtra("type","3");
                intent.putExtra("title","物流用车");
                context.startActivity(intent);
            }
        });

    }
}
