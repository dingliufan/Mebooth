package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.activity.NewsFeatureActivity;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.UIUtils;

import static com.mebooth.mylibrary.main.home.activity.NewsFeatureActivity.featureTitle;
import static com.mebooth.mylibrary.main.home.activity.NewsFeatureActivity.image;

public class FeatureHeaderAdapter implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private MultiItemTypeAdapter multiItemTypeAdapter;

    public FeatureHeaderAdapter(Context context, MultiItemTypeAdapter multiItemTypeAdapter) {
        this.context = context;
        this.multiItemTypeAdapter = multiItemTypeAdapter;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.newsfeatureheader;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {
        if (position == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void convert(ViewHolder holder, GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.newsfeature_header_img), 0, image, RoundedCornersTransformation.CORNER_ALL);
        holder.setText(R.id.newsfeature_header_title, featureTitle + " · " + (multiItemTypeAdapter.getItemCount() - 1) + "篇");
        holder.setText(R.id.newsfeature_header_count, "共" + (multiItemTypeAdapter.getItemCount() - 1) + "篇内容");
    }

}
