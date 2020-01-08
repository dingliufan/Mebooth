package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mebooth.mylibrary.main.adapter.MineOrderPagerAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetMineCountJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.main.utils.TabLayoutUtil;
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
    private TextView chat;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;
    private MultiItemTypeAdapter commonAdapter;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private int offSet = 0;

    private ArrayList<GetNowJson.NowData.NowDataList> list = new ArrayList<>();

    private Conversation.ConversationType conversationType;
    private int uid;
    private String nickName;
    private ImageView headerIcon;
    private TextView nickNameTv;
    private LinearLayout otherUserMedal;
    private ImageView otherUserMedal1;
    private ImageView otherUserMedal2;
    private ImageView otherUserMedal3;
    private ImageView otherUserMedal4;
    private ImageView otherUserMedal5;
    private ImageView otherUserMedal6;
    private TextView otherUserMedalCount;
    private TextView otherUserFollow;

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
        recyclerView = findViewById(R.id.classify_recycle);
        mSmart = findViewById(R.id.classify_smart);
        headerIcon = findViewById(R.id.mine_headericon);
        nickNameTv = findViewById(R.id.mine_nickname);
        otherUserMedal = findViewById(R.id.otheruser_medal);
        otherUserMedal1 = findViewById(R.id.otheruser_medal_1);
        otherUserMedal2 = findViewById(R.id.otheruser_medal_2);
        otherUserMedal3 = findViewById(R.id.otheruser_medal_3);
        otherUserMedal4 = findViewById(R.id.otheruser_medal_4);
        otherUserMedal5 = findViewById(R.id.otheruser_medal_5);
        otherUserMedal6 = findViewById(R.id.otheruser_medal_6);
        otherUserMedalCount = findViewById(R.id.otheruser_medal_count);
        otherUserFollow = findViewById(R.id.otheruser_follow);

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

        initRecycle();
        getCountInfo();
        getIsFollow();
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

    private void getCountInfo() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getMineCountInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMineCountJson>() {
                    @Override
                    public void onNext(GetMineCountJson getMineCountJson) {
                        super.onNext(getMineCountJson);

                        if (null != getMineCountJson && getMineCountJson.getErrno() == 0) {

                            GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getAvatar(), headerIcon, GlideImageManager.TAG_ROUND);
                            nickNameTv.setText(getMineCountJson.getData().getUser().getNickname());
                            nickName = getMineCountJson.getData().getUser().getNickname();
                            if (getMineCountJson.getData().getUser().getMedals().size() == 0) {
                                otherUserMedal1.setVisibility(View.GONE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("0枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 1) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("1枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 2) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("2枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 3) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("3枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 4) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("4枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 5) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.VISIBLE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("5枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() >= 6) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.VISIBLE);
                                otherUserMedal6.setVisibility(View.VISIBLE);
                                otherUserMedalCount.setText(getMineCountJson.getData().getUser().getMedals().size() + "枚勋章");
                            }

                            if (getMineCountJson.getData().getUser().getMedals().size() == 0) {
                                otherUserMedal1.setVisibility(View.GONE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("0枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 1) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("1枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 2) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(1).getIcon(), otherUserMedal2, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("2枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 3) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal2.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(1).getIcon(), otherUserMedal2, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal3.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(2).getIcon(), otherUserMedal3, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("3枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 4) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal2.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(1).getIcon(), otherUserMedal2, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal3.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(2).getIcon(), otherUserMedal3, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal4.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(3).getIcon(), otherUserMedal4, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("4枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 5) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal2.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(1).getIcon(), otherUserMedal2, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal3.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(2).getIcon(), otherUserMedal3, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal4.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(3).getIcon(), otherUserMedal4, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal5.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(4).getIcon(), otherUserMedal5, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("5枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() >= 6) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(0).getIcon(), otherUserMedal1, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal2.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(1).getIcon(), otherUserMedal2, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal3.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(2).getIcon(), otherUserMedal3, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal4.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(3).getIcon(), otherUserMedal4, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal5.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(4).getIcon(), otherUserMedal5, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedal6.setVisibility(View.VISIBLE);
                                GlideImageManager.glideLoader(OtherUserActivity.this, getMineCountJson.getData().getUser().getMedals().get(5).getIcon(), otherUserMedal6, GlideImageManager.TAG_RECTANGLE);

                                otherUserMedalCount.setText(getMineCountJson.getData().getUser().getMedals().size() + "枚勋章");
                            }

                        } else if (null != getMineCountJson && getMineCountJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getMineCountJson && getMineCountJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMineCountJson.getErrmsg()) ? "数据加载失败" : getMineCountJson.getErrmsg());
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

    private void getIsFollow() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getIsFollow(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetIsFollowJson>() {
                    @Override
                    public void onNext(final GetIsFollowJson getIsFollowJson) {
                        super.onNext(getIsFollowJson);

                        if (null != getIsFollowJson && getIsFollowJson.getErrno() == 0) {

                            if (getIsFollowJson.getData().getUsers().get(0).isFollowed()) {
                                otherUserFollow.setText("已关注");
                                otherUserFollow.setBackgroundResource(R.drawable.nofollow);
                            } else {
                                otherUserFollow.setText("关注");
                                otherUserFollow.setBackgroundResource(R.drawable.follow);
                            }

                            otherUserFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getIsFollowJson.getData().getUsers().get(0).isFollowed()) {

                                        //取消关注
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .cancelFollow(uid)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            getIsFollowJson.getData().getUsers().get(0).setFollowed(false);
                                                            ToastUtils.getInstance().showToast("已取消关注");
                                                            otherUserFollow.setText("关注");
                                                            otherUserFollow.setBackgroundResource(R.drawable.follow);
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

                                    } else {
                                        //添加关注
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .addFollow(uid)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            getIsFollowJson.getData().getUsers().get(0).setFollowed(true);
                                                            ToastUtils.getInstance().showToast("已关注");
                                                            otherUserFollow.setText("已关注");
                                                            otherUserFollow.setBackgroundResource(R.drawable.nofollow);
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
                                }
                            });

                        } else if (null != getIsFollowJson && getIsFollowJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getIsFollowJson && getIsFollowJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getIsFollowJson.getErrmsg()) ? "数据加载失败" : getIsFollowJson.getErrmsg());
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
                            offSet = (int) getNowJson.getData().getOffset();
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
        commonAdapter.addItemViewDelegate(new NowItemVIewZero(this, "other", commonAdapter, list, noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewOne(this, "other", commonAdapter, list, noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewTwo(this, "other", commonAdapter, list, noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewThree(this, "other", commonAdapter, list, noPublishinterface));
        commonAdapter.addItemViewDelegate(new NowItemVIewFour(this, "other", commonAdapter, list, noPublishinterface));

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
        offSet = 0;
        getRecommend(REFLUSH_LIST);
    }

}
