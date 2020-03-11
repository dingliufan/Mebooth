package com.mebooth.mylibrary.main.home.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.PlacesInfoJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
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
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class ChooseNearbyActivity extends BaseTransparentActivity implements OnRefreshListener {

    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;
    private ImageView back;
    private TextView title;

    private final int REFLUSH_LIST = 0;

    private ArrayList<PlacesInfoJson.PlacesData.PlacesList> placesLists = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.choosenearby_layout;
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
        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initRecycle();
//        getCareList(REFLUSH_LIST);
        mSmart.autoRefresh();
    }

    private void getCareList(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .placesInfo(AppApplication.getInstance().getLng(), AppApplication.getInstance().getLat())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PlacesInfoJson>() {
                    @Override
                    public void onNext(PlacesInfoJson placesInfoJson) {
                        super.onNext(placesInfoJson);

                        if (null != placesInfoJson && placesInfoJson.getErrno() == 0) {
                            initList(tag, placesInfoJson);

                        } else if (null != placesInfoJson && placesInfoJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != placesInfoJson && placesInfoJson.getErrno() != 200) {
                            cancelRefresh(tag);
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(placesInfoJson.getErrmsg()) ? "数据加载失败" : placesInfoJson.getErrmsg());
                        } else {
                            cancelRefresh(tag);
                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        cancelRefresh(tag);
                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });
    }

    private void cancelRefresh(int tag) {

        if (tag == REFLUSH_LIST) {
            if (mSmart != null) {
                mSmart.finishRefresh();
            }

        }

    }


    private void initList(int tag, PlacesInfoJson placesInfoJson) {

        if (tag == REFLUSH_LIST) {
            placesLists.clear();
            if (placesInfoJson.getData().getPlaces().size() != 0) {

                placesLists.add(placesInfoJson.getData().getPlaces().get(0));
            }
            placesLists.addAll(placesInfoJson.getData().getPlaces());

//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
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

            }
        }
    };

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.chooseaddressitem_layout, placesLists) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position == 0) {
                    holder.setImageResource(R.id.chooseaddress_gpsimg, R.drawable.nochoosegps);
                    holder.setVisible(R.id.chooseaddressitem_address, View.GONE);
                    holder.setText(R.id.chooseaddressitem_name, "不显示位置");
                } else {
                    holder.setImageResource(R.id.chooseaddress_gpsimg, R.drawable.choosegps);
                    holder.setVisible(R.id.chooseaddressitem_address, View.VISIBLE);
                    holder.setText(R.id.chooseaddressitem_name, placesLists.get(position).getName());
                    holder.setText(R.id.chooseaddressitem_address, placesLists.get(position).getAddress());
                }

//                holder.setText(R.id.recommenditem_nickname, placesLists.get(position).getName());

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情
//
//                Intent intent = new Intent(ChooseNearbyActivity.this, OtherUserActivity.class);
//                intent.putExtra("uid", users.get(position).getUid());
//                intent.putExtra("nickname", users.get(position).getNickname());
//                startActivity(intent);

                if (position == 0) {

                    Intent i = new Intent();
                    i.putExtra("result", "不显示位置");
                    setResult(3, i);
                    finish();

                } else {

                    Intent i = new Intent();
                    i.putExtra("result", placesLists.get(position).getName());
                    setResult(3, i);
                    finish();
                }

//                RongIM.getInstance().startPrivateChat(ChooseNearbyActivity.this, String.valueOf(users.get(position).getUid()), users.get(position).getNickname());


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(ChooseNearbyActivity.this));
        recyclerView.setAdapter(commonAdapter);

    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getCareList(REFLUSH_LIST);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);

    }

}
