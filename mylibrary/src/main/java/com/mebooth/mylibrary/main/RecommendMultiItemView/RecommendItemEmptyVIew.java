package com.mebooth.mylibrary.main.RecommendMultiItemView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;

public class RecommendItemEmptyVIew implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommend_headerempty_layout;
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

    }
}
