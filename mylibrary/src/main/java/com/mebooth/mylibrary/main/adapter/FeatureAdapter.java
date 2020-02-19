package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.NewPublish;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.UIUtils;

public class FeatureAdapter implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList>  {

    private Context context;

    public FeatureAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.newsfeature_item;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {
        if(position != 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void convert(ViewHolder holder, GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {
        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.newsfeature_img), 0, recommendDataList.getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
        holder.setText(R.id.newsfeature_title, recommendDataList.getFeed().getContent());

    }

}
