package com.mebooth.mylibrary.main.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.main.adapter.FeatureAdapter;
import com.mebooth.mylibrary.main.adapter.FeatureHeaderAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
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


public class NewsFeatureActivity extends BaseTransparentActivity implements OnLoadMoreListener, OnRefreshListener {
    private LinearLayout headerlly;
    private ImageView back;
    private ImageView share;
//    private View header;
    private ImageView headerImg;
    private RecyclerView mRecycleView;
    private MultiItemTypeAdapter commonAdapter;
//    private TextView headerTitle;
//    private TextView headerCount;

//    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";
    private MyHandler mHandler;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();
    private String typeName = "";
    public static String image = "";
    public static String featureTitle = "";
    private LinearLayout headerlly1;
    private ImageView back1;

    private int mDistance = 0;
    private int maxDistance = 255;//当距离在[0,255]变化时，透明度在[0,255之间变化]


    @Override
    protected int getContentViewId() {
        return R.layout.newsfeature_layout;
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

        typeName = getIntent().getStringExtra("type");


//        header = LayoutInflater.from(NewsFeatureActivity.this).inflate(R.layout.newsfeatureheader, null);
//
//        headerImg = header.findViewById(R.id.newsfeature_header_img);
//        headerTitle = header.findViewById(R.id.newsfeature_header_title);
//        headerCount = header.findViewById(R.id.newsfeature_header_count);

        headerlly = findViewById(R.id.newdetails_header);
        headerlly1 = findViewById(R.id.newdetails_header1);
        back = findViewById(R.id.public_back);
        back1 = findViewById(R.id.public_back1);
        setSystemBarAlpha(0);

        mRecycleView = findViewById(R.id.classify_recycle);
        mSmart = findViewById(R.id.newest_smart);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

        headerlly.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
        headerlly1.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

//        ((SimpleItemAnimator)mRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);


        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                //是否为顶部
//                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 :
//                        recyclerView.getChildAt(0).getTop();
//                if (topRowVerticalPosition >= 0) {
//                    //滑动到顶部
//                    headerlly.setVisibility(View.GONE);
//                    headerlly1.setVisibility(View.VISIBLE);
//                } else {
//                    headerlly.setVisibility(View.VISIBLE);
//                    headerlly1.setVisibility(View.GONE);
//                }
                mDistance += dy;
                float percent = mDistance * 1f / maxDistance;//百分比
                int alpha = (int) (percent * 255);
//            int argb = Color.argb(alpha, 57, 174, 255);
                setSystemBarAlpha(alpha);

            }
        });
    }

    /**
     * 设置标题栏背景透明度
     *
     * @param alpha 透明度
     */
    private void setSystemBarAlpha(int alpha) {
        if (alpha > 255) {
            alpha = 255;
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            headerlly.getBackground().mutate().setAlpha(alpha);
        } else {
            //标题栏渐变。a:alpha透明度 r:红 g：绿 b蓝
//        titlebar.setBackgroundColor(Color.rgb(57, 174, 255));//没有透明效果
//        titlebar.setBackgroundColor(Color.argb(alpha, 57, 174, 255));//透明效果是由参数1决定的，透明范围[0,255]
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            if (alpha <= 50) {
                back.setVisibility(View.GONE);
            } else {
                back.setVisibility(View.VISIBLE);
            }
            headerlly.getBackground().mutate().setAlpha(alpha);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        mHandler = new MyHandler(this);
        initRecycle();
        mSmart.autoRefresh();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getRecommend(typeName, offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetRecommendJson>() {
                    @Override
                    public void onNext(GetRecommendJson getRecommendJson) {
                        super.onNext(getRecommendJson);

                        if (null != getRecommendJson && getRecommendJson.getErrno() == 0) {
                            offSet = String.valueOf(getRecommendJson.getData().getOffset());

//                            headerCount.setText("共" + getRecommendJson.getData().getList().size() + "篇内容");
                            featureTitle = getRecommendJson.getData().getHeader().getTitle();
                            image = getRecommendJson.getData().getHeader().getImage();
                            initList(tag, getRecommendJson);
                            UIUtils.clearMemoryCache(NewsFeatureActivity.this);
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
            recommend.add(new GetRecommendJson.RecommendData.RecommendDataList());
            commonAdapter.notifyDataSetChanged();
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

    private static class MyHandler extends Handler {
        WeakReference<Activity> reference;

        public MyHandler(Activity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != reference) {
                NewsFeatureActivity activity = (NewsFeatureActivity) reference.get();
                if (reference.get() != null) {

                    if (msg.what == activity.REFLUSH_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
//                            activity.commonAdapter.notifyItemRangeChanged(0,activity.recommend.size());
                            activity.mSmart.finishRefresh();
                        }

                    } else if (msg.what == activity.LOADMORE_LIST) {
                        if (activity.mSmart != null) {
//                            activity.commonAdapter.notifyDataSetChanged();
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

        commonAdapter = new MultiItemTypeAdapter(NewsFeatureActivity.this, recommend);
        commonAdapter.addItemViewDelegate(new FeatureHeaderAdapter(NewsFeatureActivity.this,commonAdapter));
        commonAdapter.addItemViewDelegate(new FeatureAdapter(NewsFeatureActivity.this));

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情
                if(position == 0){

                }else{
                    Intent intent = new Intent(NewsFeatureActivity.this, NewDetailsActivity.class);
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
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(commonAdapter);
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
