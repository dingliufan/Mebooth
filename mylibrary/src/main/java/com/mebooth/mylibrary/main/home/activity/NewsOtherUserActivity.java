package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
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
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewFour;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewThree;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewTwo;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.UserNewsListJson;
import com.mebooth.mylibrary.main.utils.NoPublish;
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
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class NewsOtherUserActivity extends BaseTransparentActivity implements OnRefreshListener {

    private ImageView back;
    private TextView title;
    private ImageView chat;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;
    private TextView notTopic;

    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter1;
    private MultiItemTypeAdapter commonAdapter2;

    private int pageSize = 3;
    private String offSet = "";

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int uid;
    private String nickName;

    private ArrayList<String> userPublishList = new ArrayList<>();

    private ArrayList<UserNewsListJson.UserNewsListData.UserNewsList> userNewsList = new ArrayList<>();
    private ArrayList<GetNowJson.NowData.NowDataList> userTopicList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.newsotheruser_layout;
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
    protected void initData() {
        super.initData();

        userPublishList.add("ta发布的笔记");
        userPublishList.add("ta发布的此刻");

        uid = getIntent().getIntExtra("uid", 0);
        nickName = getIntent().getStringExtra("nickname");

        title.setText(nickName);

        initRecycle();

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
                RongIM.getInstance().startPrivateChat(NewsOtherUserActivity.this, String.valueOf(uid), nickName);
            }
        });

    }

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.userpublish_item, userPublishList) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                holder.setText(R.id.userpublish_title, userPublishList.get(position));

                holder.setOnClickListener(R.id.userpublish_lly, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {
                            Intent intent = new Intent(NewsOtherUserActivity.this, MePublishNewsActivity.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("index","newsother");
                            startActivity(intent);

                        }else{
                            Intent intent = new Intent(NewsOtherUserActivity.this, OtherUserActivity.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("index","newsother");
                            startActivity(intent);
                        }

                    }
                });

                if (position == 0) {
//                    holder.setVisible(R.id.bgf6f6f6,View.VISIBLE);
                    commonAdapter1 = new CommonAdapter(NewsOtherUserActivity.this, R.layout.usernews_item, userNewsList) {
                        @Override
                        protected void convert(ViewHolder holder, Object o, final int position) {

                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.usernews_img), 0, userNewsList.get(position).getCover(), RoundedCornersTransformation.CORNER_ALL);

                            holder.setVisible(R.id.usernews_isreview,View.GONE);
                            holder.setText(R.id.usernews_title, userNewsList.get(position).getTitle());

                            holder.setVisible(R.id.usernews_delete,View.GONE);

                        }
                    };
                    commonAdapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO 订单详情

                            Intent intent = new Intent(NewsOtherUserActivity.this, NewDetailsActivity.class);
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
                    RecyclerView recyclerView1 = holder.getView(R.id.userpublish_recycleview);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(NewsOtherUserActivity.this));
                    recyclerView1.setAdapter(commonAdapter1);

                } else if (position == 1) {
//                    holder.setVisible(R.id.bgf6f6f6,View.GONE);
                    NoPublish noPublishinterface = new NoPublish() {
                        @Override
                        public void isPublish() {

//                noPublish.setVisibility(View.VISIBLE);
//                            getRecommend();
                        }

                        @Override
                        public void isCollect() {

                        }

                        @Override
                        public void showAddButton() {

                        }
                    };

                    commonAdapter2 = new MultiItemTypeAdapter(NewsOtherUserActivity.this, userTopicList);
                    commonAdapter2.addItemViewDelegate(new NowItemVIewZero(NewsOtherUserActivity.this, "others", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewOne(NewsOtherUserActivity.this, "others", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewTwo(NewsOtherUserActivity.this, "others", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewThree(NewsOtherUserActivity.this, "others", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewFour(NewsOtherUserActivity.this, "others", commonAdapter2, userTopicList, noPublishinterface));

                    commonAdapter2.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO 详情
                            Intent intent = new Intent(NewsOtherUserActivity.this, NowDetailsActivity.class);
                            intent.putExtra("relateid", userTopicList.get(position).getTopic().getTid());
                            intent.putExtra("uid", userTopicList.get(position).getTopic().getUid());
                            startActivity(intent);
                        }

                        @Override
                        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                            return false;
                        }
                    });
                    RecyclerView recyclerView1 = holder.getView(R.id.userpublish_recycleview);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(NewsOtherUserActivity.this));
                    recyclerView1.setAdapter(commonAdapter2);
                    mSmart.autoRefresh();
                }

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(NewsOtherUserActivity.this));
        recyclerView.setAdapter(commonAdapter);


    }

    private void getNews() {

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

                            userNewsList.clear();
                            userNewsList.addAll(userNewsListJson.getData().getList());
                            commonAdapter1.notifyDataSetChanged();
                            mSmart.finishRefresh();
                            getRecommend();
                        } else if (null != userNewsListJson && userNewsListJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(0);
                        } else if (null != userNewsListJson && userNewsListJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(userNewsListJson.getErrmsg()) ? "数据加载失败" : userNewsListJson.getErrmsg());
                            cancelRefresh(0);
                        } else {

                            ToastUtils.getInstance().showToast("数据加载失败");
                            cancelRefresh(0);
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

    private void getRecommend() {

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

                            userTopicList.clear();
                            userTopicList.addAll(getNowJson.getData().getList());
                            commonAdapter2.notifyDataSetChanged();
                            mSmart.finishRefresh();

                            if(userNewsList.size() ==0&&userTopicList.size() ==0){

                                mSmart.setVisibility(View.GONE);
                                notTopic.setVisibility(View.VISIBLE);

                            }else{
                                mSmart.setVisibility(View.VISIBLE);
                                notTopic.setVisibility(View.GONE);
                            }

                        } else if (null != getNowJson && getNowJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(0);
                        } else if (null != getNowJson && getNowJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNowJson.getErrmsg()) ? "数据加载失败" : getNowJson.getErrmsg());
                            cancelRefresh(0);
                        } else {

                            ToastUtils.getInstance().showToast("数据加载失败");
                            cancelRefresh(0);
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

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getNews();
    }

}
