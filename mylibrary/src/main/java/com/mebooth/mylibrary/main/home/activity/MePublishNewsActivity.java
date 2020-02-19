package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
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
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UserNewsListJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
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

public class MePublishNewsActivity extends BaseTransparentActivity implements OnLoadMoreListener, OnRefreshListener {

    private CommonAdapter commonAdapter1;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private int pageSize = 10;
    private String offSet = "";

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;
    private ArrayList<UserNewsListJson.UserNewsListData.UserNewsList> userNewsList = new ArrayList<>();
    private int uid;

    private ImageView back;
    private TextView title;
    private String index = "";

    @Override
    protected int getContentViewId() {
        return R.layout.mepublishnewslist_layout;
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
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        mSmart = findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        uid = getIntent().getIntExtra("uid", 0);
        index = getIntent().getStringExtra("index");
        if (index.equals("newsother")) {

            title.setText("ta发布的笔记");
        } else {
            title.setText("我发布的笔记");
        }
        initRecycle();
        mSmart.autoRefresh();

    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);
        mSmart.setOnLoadMoreListener(this);

    }

    private void getNews(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .userNewsList(uid, offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<UserNewsListJson>() {
                    @Override
                    public void onNext(UserNewsListJson userNewsListJson) {
                        super.onNext(userNewsListJson);

                        if (null != userNewsListJson && userNewsListJson.getErrno() == 0) {

                            offSet = String.valueOf(userNewsListJson.getData().getOffset());
                            initList(tag, userNewsListJson);
                        } else if (null != userNewsListJson && userNewsListJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != userNewsListJson && userNewsListJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(userNewsListJson.getErrmsg()) ? "数据加载失败" : userNewsListJson.getErrmsg());
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
                        cancelRefresh(0);
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

    private void initList(int tag, UserNewsListJson nowJson) {

        if (tag == REFLUSH_LIST) {
            userNewsList.clear();
            userNewsList.addAll(nowJson.getData().getList());
//            if(userNewsList.size() == 0){
//                noPublish.setVisibility(View.VISIBLE);
//            }else{
//                noPublish.setVisibility(View.GONE);
//            }
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        } else {
            if (nowJson.getData().getList().size() == 0) {

                mSmart.finishLoadMoreWithNoMoreData();

            } else {
                userNewsList.addAll(nowJson.getData().getList());
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
                    commonAdapter1.notifyDataSetChanged();
                    mSmart.finishRefresh();
                }

            } else if (msg.what == LOADMORE_LIST) {
                if (mSmart != null) {
                    commonAdapter1.notifyDataSetChanged();
                    mSmart.finishLoadMore();
                }

            }
        }
    };


    private void initRecycle() {

        commonAdapter1 = new CommonAdapter(this, R.layout.usernews_item, userNewsList) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (index.equals("newsother")) {
                    holder.setVisible(R.id.usernews_delete, View.GONE);
                    holder.setVisible(R.id.usernews_isreview, View.GONE);
                } else {
                    holder.setVisible(R.id.usernews_delete, View.VISIBLE);
                    holder.setVisible(R.id.usernews_isreview, View.VISIBLE);
                }

                UIUtils.loadRoundImage((ImageView) holder.getView(R.id.usernews_img), 0, userNewsList.get(position).getCover(), RoundedCornersTransformation.CORNER_ALL);
                if (userNewsList.get(position).getPublish().equals("Y")) {

                    holder.setText(R.id.usernews_isreview, "已审核");
                    holder.setBackgroundRes(R.id.usernews_isreview, R.drawable.review);

                } else {
                    holder.setText(R.id.usernews_isreview, "未审核");
                    holder.setBackgroundRes(R.id.usernews_isreview, R.drawable.underreview);
                }
                holder.setText(R.id.usernews_title, userNewsList.get(position).getTitle());

                holder.setOnClickListener(R.id.usernews_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteNews(userNewsList.get(position).getNewsid(), position);


                    }
                });

            }
        };
        commonAdapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

                Intent intent = new Intent(MePublishNewsActivity.this, NewDetailsActivity.class);
                intent.putExtra("uid", userNewsList.get(position).getUid());
                intent.putExtra("relateid", userNewsList.get(position).getNewsid());
                startActivity(intent);
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commonAdapter1);


    }

    private void deleteNews(int newsid, final int position) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .deleteNews(newsid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PublicBean>() {
                    @Override
                    public void onNext(PublicBean publicBean) {
                        super.onNext(publicBean);

                        if (null != publicBean && publicBean.getErrno() == 0) {

                            ToastUtils.getInstance().showToast("删除新闻成功");
                            userNewsList.remove(position);
                            commonAdapter1.notifyDataSetChanged();
                        } else if (null != publicBean && publicBean.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != publicBean && publicBean.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(publicBean.getErrmsg()) ? "数据加载失败" : publicBean.getErrmsg());
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

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getNews(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getNews(REFLUSH_LIST);
    }
}
