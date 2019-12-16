package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewFour;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewThree;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewTwo;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MePublishFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {

    private MultiItemTypeAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private int offSet = 0;

    private ArrayList<GetNowJson.NowData.NowDataList> list = new ArrayList<>();
    private int uid;

    public static MePublishFragment getInstance(int uid) {
        MePublishFragment sf = new MePublishFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);
        sf.setArguments(bundle);
        return sf;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.mepublish_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initRecycle();
        getRecommend(REFLUSH_LIST);
//
        Bundle bundle = getArguments();
        uid = bundle.getInt("uid");
    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .userPublishList(uid,offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNowJson>() {
                    @Override
                    public void onNext(GetNowJson getNowJson) {
                        super.onNext(getNowJson);

                        if (null != getNowJson && getNowJson.getErrno() == 0) {
                            offSet = (int) getNowJson.getData().getOffset();
                            initList(tag, getNowJson);

                        } else if (null != getNowJson && getNowJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNowJson.getErrmsg()) ? "数据加载失败" : getNowJson.getErrmsg());
                        } else {

                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });
    }

    private void initList(int tag, GetNowJson nowJson) {

        if (tag == REFLUSH_LIST) {
            list.clear();
            list.addAll(nowJson.getData().getList());
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        } else {
            if (nowJson.getData().getList().size() == 0) {

                mSmart.finishLoadMoreWithNoMoreData();

            } else {
                list.addAll(nowJson.getData().getList());
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
        commonAdapter = new MultiItemTypeAdapter(getActivity(), list);
        commonAdapter.addItemViewDelegate(new NowItemVIewZero(getActivity(), "mine", commonAdapter, list));
        commonAdapter.addItemViewDelegate(new NowItemVIewOne(getActivity(), "mine", commonAdapter, list));
        commonAdapter.addItemViewDelegate(new NowItemVIewTwo(getActivity(), "mine", commonAdapter, list));
        commonAdapter.addItemViewDelegate(new NowItemVIewThree(getActivity(), "mine", commonAdapter, list));
        commonAdapter.addItemViewDelegate(new NowItemVIewFour(getActivity(), "mine", commonAdapter, list));

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情
                Intent intent = new Intent(getActivity(), NowDetailsActivity.class);
                intent.putExtra("relateid", list.get(position).getTopic().getTid());
                intent.putExtra("uid", list.get(position).getTopic().getUid());
                startActivity(intent);
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
