package com.mebooth.mylibrary.main.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NowFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {

    private MultiItemTypeAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";
    private MyHandler mHandler;

    private ArrayList<GetNowJson.NowData.NowDataList> list = new ArrayList<>();

    public static boolean isNowRefresh = false;
    private String index = "";
    private int id;
    private boolean isPraise;
    private int type;

    @Override
    protected int getLayoutResId() {
        return R.layout.now_layout;
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

        //注册广播
        IntentFilter filter = new IntentFilter("dataRefresh");
        getActivity().registerReceiver(broadcastReceiver, filter);

        mHandler = new MyHandler(this);
        initRecycle();
//        getRecommend(REFLUSH_LIST);
        mSmart.autoRefresh();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            index = intent.getStringExtra("index");
            type = intent.getIntExtra("type", 1111);
            id = intent.getIntExtra("id", 0);
            isPraise = intent.getBooleanExtra("isPraise", false);

            if (index.equals("cancel")) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getTopic().getTid() == id) {
                        if (type == 1) {
                            list.get(i).getTopic().setPraised(isPraise);
                            list.get(i).getTopic().setPraises(list.get(i).getTopic().getPraises() - 1);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }

            } else if (index.equals("add")) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getTopic().getTid() == id) {
                        if (type == 1) {
                            list.get(i).getTopic().setPraised(isPraise);
                            list.get(i).getTopic().setPraises(list.get(i).getTopic().getPraises() + 1);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        }
    };

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getNow(offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNowJson>() {
                    @Override
                    public void onNext(GetNowJson getNowJson) {
                        super.onNext(getNowJson);

                        if (null != getNowJson && getNowJson.getErrno() == 0) {
                            offSet = String.valueOf(getNowJson.getData().getOffset());
                            initList(tag, getNowJson);
                            UIUtils.clearMemoryCache(getActivity());
                        } else if (null != getNowJson && getNowJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != getNowJson && getNowJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNowJson.getErrmsg()) ? "数据加载失败" : getNowJson.getErrmsg());
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

    private void initList(int tag, GetNowJson nowJson) {

        if (tag == REFLUSH_LIST) {
            list.clear();
            commonAdapter.notifyDataSetChanged();
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

    private static class MyHandler extends Handler {
        WeakReference<Fragment> reference;

        public MyHandler(Fragment context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != reference) {
                NowFragment activity = (NowFragment) reference.get();
                if (reference.get() != null) {
                    if (msg.what == activity.REFLUSH_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
                            activity.mSmart.finishRefresh();
                        }

                    } else if (msg.what == activity.LOADMORE_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
                            activity.mSmart.finishLoadMore();
                        }

                    }

                }
            }

        }
    }


    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);
        mSmart.setOnLoadMoreListener(this);

    }

    private void initRecycle() {
        commonAdapter = new MultiItemTypeAdapter(getActivity(), list);
        commonAdapter.addItemViewDelegate(new NowItemVIewZero(getActivity(), list, commonAdapter));
        commonAdapter.addItemViewDelegate(new NowItemVIewOne(getActivity(), list, commonAdapter));
        commonAdapter.addItemViewDelegate(new NowItemVIewTwo(getActivity(), list, commonAdapter));
        commonAdapter.addItemViewDelegate(new NowItemVIewThree(getActivity(), list, commonAdapter));
        commonAdapter.addItemViewDelegate(new NowItemVIewFour(getActivity(), list, commonAdapter));

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
    public void onResume() {
        super.onResume();

        if (isNowRefresh) {
            ServiceFactory.getNewInstance()
                    .createService(YService.class)
                    .getNow("", list.size())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonObserver<GetNowJson>() {
                        @Override
                        public void onNext(GetNowJson getNowJson) {
                            super.onNext(getNowJson);

                            if (null != getNowJson && getNowJson.getErrno() == 0) {

                                list.clear();
                                list.addAll(getNowJson.getData().getList());
                                commonAdapter.notifyDataSetChanged();
                                UIUtils.clearMemoryCache(getActivity());
                                isNowRefresh = false;
                            } else if (null != getNowJson && getNowJson.getErrno() == 1101) {

                                SharedPreferencesUtils.writeString("token", "");
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

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getRecommend(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getRecommend(REFLUSH_LIST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler == null) {


        } else {
            mHandler.removeCallbacksAndMessages(null);
        }

    }
}
