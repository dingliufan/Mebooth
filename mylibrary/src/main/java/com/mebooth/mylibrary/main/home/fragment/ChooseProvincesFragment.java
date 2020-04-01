package com.mebooth.mylibrary.main.home.fragment;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.NewsOtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.ProvincesListJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.GridSpacingItemDecoration;
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

import static com.mebooth.mylibrary.main.home.activity.UserCarCityActivity.chooseCity;

public class ChooseProvincesFragment extends BaseFragment implements OnRefreshListener {
    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter1;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private ArrayList<String> provinces = new ArrayList<>();
    private ArrayList<String> hotCity = new ArrayList<>();

    private ImageView back;
    private TextView title;

    @Override
    protected int getLayoutResId() {
        return R.layout.chooseprovinces_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
        back = view.findViewById(R.id.public_back);
        title = view.findViewById(R.id.public_title);

        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

        view.findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(getActivity()), 0, 0);


    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSmart.setEnableLoadMore(false);
        title.setText("用车城市");
        initRecycle();
//        getCareList(REFLUSH_LIST);
        mSmart.autoRefresh();
    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从栈中将当前fragment推出
                getActivity().finish();
            }
        });

    }

    private void getProvincesList(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .ProvincesListInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<ProvincesListJson>() {
                    @Override
                    public void onNext(ProvincesListJson provincesListJson) {
                        super.onNext(provincesListJson);

                        if (null != provincesListJson && provincesListJson.getErrno() == 0) {
                            initList(tag, provincesListJson);

                        } else if (null != provincesListJson && provincesListJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(tag);
                        } else if (null != provincesListJson && provincesListJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(provincesListJson.getErrmsg()) ? "数据加载失败" : provincesListJson.getErrmsg());
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

    private void initList(int tag, ProvincesListJson provincesListJson) {

        if (tag == REFLUSH_LIST) {
            provinces.clear();
            hotCity.clear();
            hotCity.addAll(provincesListJson.getData().getHots());
            provinces.add("");
            provinces.addAll(provincesListJson.getData().getProvinces());
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

            } else if (msg.what == LOADMORE_LIST) {
                if (mSmart != null) {
                    commonAdapter.notifyDataSetChanged();
                    mSmart.finishLoadMore();
                }

            }
        }
    };

    private void initRecycle() {

        commonAdapter = new CommonAdapter(getActivity(), R.layout.provinces_item, provinces) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position == 0) {

                    holder.setVisible(R.id.provincesitem_hotcity_lly, View.VISIBLE);
                    holder.setVisible(R.id.provincesitem_city_lly, View.GONE);

                    RecyclerView recycle = holder.getView(R.id.provincesitem_hotcity_recycle);

                    commonAdapter1 = new CommonAdapter(getActivity(), R.layout.hoycity_item, hotCity) {
                        @Override
                        protected void convert(ViewHolder holder, Object o, final int position) {

                            holder.setText(R.id.hotcityitem_city, hotCity.get(position));

                        }
                    };
                    commonAdapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO
                            chooseCity = hotCity.get(position);
                            getActivity().finish();


//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

                        }

                        @Override
                        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                            return false;
                        }
                    });
                    recycle.addItemDecoration(new GridSpacingItemDecoration(3, UIUtils.dp2px(getActivity(), 16), true));
                    recycle.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    recycle.setAdapter(commonAdapter1);

                } else {
                    holder.setVisible(R.id.provincesitem_hotcity_lly, View.GONE);
                    holder.setVisible(R.id.provincesitem_city_lly, View.VISIBLE);

                    holder.setText(R.id.provincesitem_city, provinces.get(position));

                }

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

                if (provinces.get(position).contains("市")) {

                    chooseCity = provinces.get(position);
                    getActivity().finish();

                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)  //将当前fragment加入到返回栈中
                            .replace(R.id.container, new ChooseCityFragment(provinces.get(position))).commit();
                }
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

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
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getProvincesList(REFLUSH_LIST);
    }
}
