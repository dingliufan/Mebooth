package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIew;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIewFour;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIewOne;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIewThree;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIewTwo;
import com.mebooth.mylibrary.main.RecommendMultiItemView.RecommendItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ExperienceFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {

    private MultiItemTypeAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private int offSet;

    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();


    @Override
    protected int getLayoutResId() {
        return R.layout.experience_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initRecycle();
//        getRecommend(REFLUSH_LIST);
        mSmart.autoRefresh();
    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getRecommend("evaluate", offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetRecommendJson>() {
                    @Override
                    public void onNext(GetRecommendJson getRecommendJson) {
                        super.onNext(getRecommendJson);

                        if (null != getRecommendJson && getRecommendJson.getErrno() == 0) {
                            offSet = (int) getRecommendJson.getData().getOffset();
                            initList(tag, getRecommendJson);

                        } else if (null != getRecommendJson && getRecommendJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != getRecommendJson && getRecommendJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getRecommendJson.getErrmsg()) ? "数据加载失败" : getRecommendJson.getErrmsg());
                            cancelRefresh(tag);
                        } else {

                            ToastUtils.getInstance().showToast("数据加载失败");
                            cancelRefresh(tag);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        ToastUtils.getInstance().showToast("数据加载失败");
                        cancelRefresh(tag);
                    }
                });
    }

    private void cancelRefresh(int tag) {

        if (tag == REFLUSH_LIST) {
            if (mSmart != null) {
                mSmart.finishRefresh();
            }

        } else if (tag == LOADMORE_LIST) {
            if (mSmart != null) {
                mSmart.finishLoadMore();
            }

        }

    }

    private void initList(int tag, GetRecommendJson getRecommendJson) {

        if (tag == REFLUSH_LIST) {
            recommend.clear();
            recommend.addAll(getRecommendJson.getData().getList());
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        } else {
            if (getRecommendJson.getData().getList().size() == 0) {

                mSmart.finishLoadMoreWithNoMoreData();

            } else {
                recommend.addAll(getRecommendJson.getData().getList());
                mHandler.sendEmptyMessageDelayed(tag, 1000);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == REFLUSH_LIST) {
                if (mSmart != null) {
                    commonAdapter.notifyDataSetChanged();
                    mSmart.finishRefresh();
                }

            } else if (msg.what == LOADMORE_LIST) {
                if (mSmart != null) {
                    mSmart.finishLoadMore();
                }

            }
        }
    };

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

    }

    private void initRecycle() {
        commonAdapter = new MultiItemTypeAdapter(getActivity(), recommend);
        commonAdapter.addItemViewDelegate(new RecommendItemVIew(getActivity()));
        commonAdapter.addItemViewDelegate(new RecommendItemVIewZero(getActivity()));
        commonAdapter.addItemViewDelegate(new RecommendItemVIewOne(getActivity()));
        commonAdapter.addItemViewDelegate(new RecommendItemVIewTwo(getActivity()));
        commonAdapter.addItemViewDelegate(new RecommendItemVIewThree(getActivity()));
        commonAdapter.addItemViewDelegate(new RecommendItemVIewFour(getActivity()));

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情
                if (recommend.get(position).getFeed().getType() == 1) {
                    Intent intent = new Intent(getActivity(), NowDetailsActivity.class);
                    intent.putExtra("relateid", recommend.get(position).getFeed().getRelateid());
                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                    intent.putExtra("relateid", recommend.get(position).getFeed().getRelateid());
                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                    intent.putExtra("image", recommend.get(position).getUser().getAvatar());
                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
                    intent.putExtra("browse", recommend.get(position).getFeed().getWatches());
                    intent.putExtra("replies", recommend.get(position).getFeed().getReplies());
                    intent.putExtra("praises", recommend.get(position).getFeed().getPraises());
                    startActivity(intent);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(commonAdapter);

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getRecommend(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = 0;
        getRecommend(REFLUSH_LIST);
    }

}
