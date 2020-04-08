package com.mebooth.mylibrary.main.home.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetMeCollectJson;
import com.mebooth.mylibrary.main.utils.ResourcseMessage;
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
import io.rong.imlib.RongIMClient;

public class NewMineFollowActivity extends BaseTransparentActivity implements OnLoadMoreListener, OnRefreshListener {

    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";

    private ImageView back;
    private TextView title;

    private ArrayList<GetCareJson.CareData.CareUser> list = new ArrayList<>();
    private String index = "";
    private boolean isFllow;
    private int id;

    @Override
    protected int getContentViewId() {
        return R.layout.minefollow_layout;
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

        //注册广播
        IntentFilter filter = new IntentFilter("dataRefresh");
        registerReceiver(broadcastReceiver, filter);

        initRecycle();
//        getRecommend(REFLUSH_LIST);
        mSmart.autoRefresh();

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            index = intent.getStringExtra("index");
            id = intent.getIntExtra("id", 0);
            isFllow = intent.getBooleanExtra("isFollow", false);
            if (index.equals("follow")) {
                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getUid() == id) {
                        list.remove(i);
//                        list.get(i).setFollowed(isFllow);
                        commonAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void getMineFollow(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getCareList(offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetCareJson>() {
                    @Override
                    public void onNext(GetCareJson getCareJson) {
                        super.onNext(getCareJson);

                        if (null != getCareJson && getCareJson.getErrno() == 0) {
                            offSet = String.valueOf(getCareJson.getData().getOffset());
                            initList(tag, getCareJson);

                        } else if (null != getCareJson && getCareJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != getCareJson && getCareJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getCareJson.getErrmsg()) ? "数据加载失败" : getCareJson.getErrmsg());
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

    private void initList(int tag, GetCareJson getCareJson) {

        if (tag == REFLUSH_LIST) {
            list.clear();
            list.addAll(getCareJson.getData().getUsers());
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        } else {
            if (getCareJson.getData().getUsers().size() == 0) {

                mSmart.finishLoadMoreWithNoMoreData();

            } else {
                list.addAll(getCareJson.getData().getUsers());
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

        title.setText("我的关注");

    }

    private void initRecycle() {


        commonAdapter = new CommonAdapter(this, R.layout.newminefollow_item_layout, list) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                GlideImageManager.glideLoader(NewMineFollowActivity.this, list.get(position).getAvatar(), (ImageView) holder.getView(R.id.newminefollow_item_headericon), GlideImageManager.TAG_ROUND);
                holder.setText(R.id.newminefollow_item_nickname, list.get(position).getNickname());
//                if (list.get(position).getSignature().isEmpty()) {

                holder.setVisible(R.id.newminefollow_item_signature, View.GONE);

//                } else {
//                    holder.setVisible(R.id.newminefollow_item_signature, View.VISIBLE);
//                    holder.setText(R.id.newminefollow_item_signature, list.get(position).getSignature());
//                }

                holder.setTextColor(R.id.newminefollow_item_follow, getResources().getColor(ResourcseMessage.getFontColor()));
                holder.setBackgroundRes(R.id.newminefollow_item_follow, ResourcseMessage.getFollowBackground());

                holder.setOnClickListener(R.id.newminefollow_item_follow, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {

                            connect(list.get(position).getUid(), list.get(position).getNickname());
                        } else {

                            RongIM.getInstance().startPrivateChat(NewMineFollowActivity.this, String.valueOf(list.get(position).getUid()), list.get(position).getNickname());
                        }

                    }
                });


            }
        };


        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情
                Intent intent = new Intent(NewMineFollowActivity.this, NewMineActivity1.class);
                intent.putExtra("index", "other");
                intent.putExtra("uid", list.get(position).getUid());
//                                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
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

    private void connect(final int uidIndex, final String nickName) {
//        RongIM.connect(rongToken, new RongIMClient.ConnectCallback() {
        RongIMClient.connect(SharedPreferencesUtils.readString("rong_token"), new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
//                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {
//
//                } else {
//                    getConnectToken();
//                }
            }

            @Override
            public void onSuccess(String userid) {
                Log.d("TAG", "--onSuccess" + userid);
//                ToastUtils.getInstance().showToast("已连接融云");

                RongIM.getInstance().startPrivateChat(NewMineFollowActivity.this, String.valueOf(uidIndex), nickName);


            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("TAG", "--onSuccess" + errorCode);
                ToastUtils.getInstance().showToast("连接融云失败");
            }
        });

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getMineFollow(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getMineFollow(REFLUSH_LIST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);

    }

}
