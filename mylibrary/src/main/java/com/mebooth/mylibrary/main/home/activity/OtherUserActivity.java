package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewFour;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewThree;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewTwo;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
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
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class OtherUserActivity extends BaseTransparentActivity implements OnLoadMoreListener, OnRefreshListener {

    private ImageView back;
    private TextView title;
    private ImageView chat;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;
    private MultiItemTypeAdapter commonAdapter;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";

    private ArrayList<GetNowJson.NowData.NowDataList> list = new ArrayList<>();

    private Conversation.ConversationType conversationType;
    private int uid;
    private String nickName;
    private TextView notTopic;

    @Override
    protected int getContentViewId() {
        return R.layout.otheruser_layout;
    }

    @Override
    protected void initView() {
        super.initView();

        back = findViewById(R.id.otheruser_back);
        title = findViewById(R.id.otheruser_title);
        chat = findViewById(R.id.otheruser_right);
        notTopic = findViewById(R.id.otheruser_nottopic);
        recyclerView = findViewById(R.id.classify_recycle);
        mSmart = findViewById(R.id.classify_smart);
        findViewById(R.id.otheruserheader).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);
        mSmart.setOnLoadMoreListener(this);

    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initData() {
        super.initData();

        uid = getIntent().getIntExtra("uid", 0);
        nickName = getIntent().getStringExtra("nickname");

        title.setText(nickName);
        initRecycle();
//        getRecommend(REFLUSH_LIST);
        mSmart.autoRefresh();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startPrivateChat(OtherUserActivity.this, String.valueOf(uid), nickName);
            }
        });

    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .userPublishList(uid, offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNowJson>() {
                    @Override
                    public void onNext(GetNowJson getNowJson) {
                        super.onNext(getNowJson);

                        if (null != getNowJson && getNowJson.getErrno() == 0) {
                            offSet = String.valueOf(getNowJson.getData().getOffset());
                            initList(tag, getNowJson);

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
            list.addAll(nowJson.getData().getList());
            if(list.size() == 0){
                notTopic.setVisibility(View.VISIBLE);
            }else{
                notTopic.setVisibility(View.GONE);
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

    private void initRecycle() {

        NoPublish noPublishinterface = new NoPublish() {
            @Override
            public void isPublish() {

            }

            @Override
            public void isCollect() {
            }
        };

        commonAdapter = new MultiItemTypeAdapter(this, list);
        commonAdapter.addItemViewDelegate(new NowItemVIewZero(this, "other", commonAdapter, list,noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewOne(this, "other", commonAdapter, list,noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewTwo(this, "other", commonAdapter, list,noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewThree(this, "other", commonAdapter, list,noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewFour(this, "other", commonAdapter, list,noPublishinterface));

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情
                Intent intent = new Intent(OtherUserActivity.this, NowDetailsActivity.class);
                intent.putExtra("relateid", list.get(position).getTopic().getTid());
                intent.putExtra("uid", list.get(position).getTopic().getUid());
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
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);

    }

}
