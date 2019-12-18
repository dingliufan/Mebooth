package com.mebooth.mylibrary.main.RecommendMultiItemView;

import android.content.Context;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.utils.GlideImageManager;


public class RecommendItemVIew implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;

    public RecommendItemVIew(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommend_item;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {

        if (item.getFeed().getType() == 1) {
            return false;

        } else {
            return true;
        }

    }

    @Override
    public void convert(ViewHolder holder, GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        holder.setText(R.id.recommenditem_content,recommendDataList.getFeed().getContent());
        GlideImageManager.glideLoader(context, recommendDataList.getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_img), GlideImageManager.TAG_FILLET);
        int month = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(14, 16));
        int second = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(17, 19));
        holder.setText(R.id.recommenditem_time, (month + 1) + "-" + date + " " + hour + ":" + minute);

        holder.setText(R.id.recommenditem_browsecount, String.valueOf(recommendDataList.getFeed().getWatches()));
        holder.setText(R.id.recommenditem_commentcount, String.valueOf(recommendDataList.getFeed().getReplies()));

    }
}
