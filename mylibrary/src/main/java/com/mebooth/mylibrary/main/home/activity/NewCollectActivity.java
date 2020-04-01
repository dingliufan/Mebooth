package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetMeCollectJson;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewCollectActivity extends BaseTransparentActivity implements OnLoadMoreListener, OnRefreshListener {

    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";

    private ArrayList<GetMeCollectJson.MeCollectData.MeCollectList> list = new ArrayList<>();
    private TextView noCollect;
    private MineActivity.refreshData refreshData;

    public static boolean isMeCollectRefresh = false;
    private ImageView back;
    private TextView title;

    @Override
    protected int getContentViewId() {
        return R.layout.newcollect_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initView() {
        super.initView();

        recyclerView = findViewById(R.id.classify_recycle);
        mSmart = findViewById(R.id.classify_smart);
        noCollect = findViewById(R.id.mecollect_notpublish);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);


    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        isMeCollectRefresh = false;
        initRecycle();
//        getRecommend(REFLUSH_LIST);
        mSmart.autoRefresh();

    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .userPraiseList(offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMeCollectJson>() {
                    @Override
                    public void onNext(GetMeCollectJson getMeCollectJson) {
                        super.onNext(getMeCollectJson);

                        if (null != getMeCollectJson && getMeCollectJson.getErrno() == 0) {
                            offSet = String.valueOf(getMeCollectJson.getData().getOffset());
                            initList(tag, getMeCollectJson);

                        } else if (null != getMeCollectJson && getMeCollectJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != getMeCollectJson && getMeCollectJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMeCollectJson.getErrmsg()) ? "数据加载失败" : getMeCollectJson.getErrmsg());
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

    private void initList(int tag, GetMeCollectJson nowJson) {

        if (tag == REFLUSH_LIST) {
            list.clear();
            list.addAll(nowJson.getData().getList());
            if (list.size() == 0) {
                noCollect.setVisibility(View.VISIBLE);
            } else {
                noCollect.setVisibility(View.GONE);
            }
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
                    commonAdapter.notifyDataSetChanged();
                    mSmart.finishLoadMore();
                }

            }
        }
    };


    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);
        mSmart.setOnLoadMoreListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("我的收藏");

    }

    private void initRecycle() {


        commonAdapter = new CommonAdapter(NewCollectActivity.this, R.layout.mecollect_item_layout, list) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                GlideImageManager.glideLoader(NewCollectActivity.this, list.get(position).getNews().getCover(), (ImageView) holder.getView(R.id.mecollect_item_img), GlideImageManager.TAG_RECTANGLE);

                holder.setText(R.id.mecollect_item_title, list.get(position).getNews().getTitle());
                holder.setText(R.id.mecollect_item_content, list.get(position).getNews().getDescribe().replace("\\n", "\n"));

            }
        };


        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情

                Intent intent = new Intent(NewCollectActivity.this, NewDetailsActivity.class);
                intent.putExtra("relateid", list.get(position).getNews().getNewsid());
                intent.putExtra("uid", list.get(position).getUser().getUid());
                intent.putExtra("image", list.get(position).getUser().getAvatar());
                intent.putExtra("nickname", list.get(position).getUser().getNickname());
                intent.putExtra("browse", list.get(position).getNews().getWatches());
                intent.putExtra("replies", list.get(position).getNews().getReplies());
                intent.putExtra("praises", list.get(position).getNews().getPraises());
                startActivity(intent);


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commonAdapter);

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
    public void onResume() {
        super.onResume();

        if (isMeCollectRefresh) {

            ServiceFactory.getNewInstance()
                    .createService(YService.class)
                    .userPraiseList("", list.size() - 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonObserver<GetMeCollectJson>() {
                        @Override
                        public void onNext(GetMeCollectJson getMeCollectJson) {
                            super.onNext(getMeCollectJson);

                            if (null != getMeCollectJson && getMeCollectJson.getErrno() == 0) {
                                offSet = String.valueOf(getMeCollectJson.getData().getOffset());

                                list.clear();
                                list.addAll(getMeCollectJson.getData().getList());
                                if (list.size() == 0) {
                                    noCollect.setVisibility(View.VISIBLE);
                                } else {
                                    noCollect.setVisibility(View.GONE);
                                }

                                commonAdapter.notifyDataSetChanged();
                                refreshData.refresh();
                                isMeCollectRefresh = false;
                                UIUtils.clearMemoryCache(NewCollectActivity.this);
                            } else if (null != getMeCollectJson && getMeCollectJson.getErrno() == 1101) {

                                SharedPreferencesUtils.writeString("token", "");
                            } else if (null != getMeCollectJson && getMeCollectJson.getErrno() != 200) {

                                ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMeCollectJson.getErrmsg()) ? "数据加载失败" : getMeCollectJson.getErrmsg());
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
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);

    }

}
