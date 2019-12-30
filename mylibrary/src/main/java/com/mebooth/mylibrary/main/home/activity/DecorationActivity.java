package com.mebooth.mylibrary.main.home.activity;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.adapter.DecorationGridAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetDecorationJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DecorationActivity extends BaseTransparentActivity {
    private int uid;

    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private ImageView back;
    private ImageView right;
    private TextView decorationCount;
    private ImageView decorationUserIcon;
    private TextView decorationRank;
    private TextView decorationFirst;

    private DecorationGridAdapter adapter;

    private ArrayList<GetDecorationJson.DecorationData.DecorationView.DecorationGroup> group = new ArrayList<>();
    private LinearLayout decorationHeader1;
    private NestedScrollView decorationScroll;
    private ImageView back1;
    private ImageView right1;

    @Override
    protected int getContentViewId() {
        return R.layout.drcoration_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        super.initView();
        recyclerView = findViewById(R.id.decoration_recycle);
        back = findViewById(R.id.public_back);
        back1 = findViewById(R.id.public_back1);
        right = findViewById(R.id.public_right);
        right1 = findViewById(R.id.public_right1);
        decorationCount = findViewById(R.id.decoration_count);
        decorationUserIcon = findViewById(R.id.decoration_usericon);
        decorationRank = findViewById(R.id.decoration_rank);
        decorationFirst = findViewById(R.id.decoration_first);
        decorationHeader1 = findViewById(R.id.decorationheader1);
        decorationScroll = findViewById(R.id.decoration_scrollview);
        decorationHeader1.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
        findViewById(R.id.decorationheader).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        decorationScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > UIUtils.getStatusBarHeight(DecorationActivity.this)) {
                    decorationHeader1.setVisibility(View.VISIBLE);
                    findViewById(R.id.decorationheader).setVisibility(View.GONE);
                } else {
                    decorationHeader1.setVisibility(View.GONE);
                    findViewById(R.id.decorationheader).setVisibility(View.VISIBLE);
                }

            }
        });

    }

    @Override
    protected void initData() {
        super.initData();

        uid = getIntent().getIntExtra("uid", 0);

        initRecycle();
        getDecoration();

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

    private void getDecoration() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getDecorationInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetDecorationJson>() {
                    @Override
                    public void onNext(GetDecorationJson getDecorationJson) {
                        super.onNext(getDecorationJson);

                        if (null != getDecorationJson && getDecorationJson.getErrno() == 0) {

                            decorationCount.setText(getDecorationJson.getData().getView().getStats().getTotal() + "枚");
                            decorationRank.setText("前" + getDecorationJson.getData().getView().getStats().getRatio() + "%");
                            GlideImageManager.glideLoader(DecorationActivity.this, getDecorationJson.getData().getUser().getAvatar(), decorationUserIcon, GlideImageManager.TAG_ROUND);


                            group.clear();
                            group.addAll(getDecorationJson.getData().getView().getGroup());

                            if (group.size() != 0) {

                                decorationFirst.setText("- " + group.get(0).getType().getName() + " -");

                            }
                            commonAdapter.notifyDataSetChanged();

                        } else if (null != getDecorationJson && getDecorationJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getDecorationJson && getDecorationJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getDecorationJson.getErrmsg()) ? "数据加载失败" : getDecorationJson.getErrmsg());
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

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.decoration_item, group) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position == 0) {
                    holder.setVisible(R.id.decoration_itembar, View.GONE);
                } else {
                    holder.setVisible(R.id.decoration_itembar, View.VISIBLE);
                }

                holder.setText(R.id.decoration_itembar, "- " + group.get(position).getType().getName() + " -");
                adapter = new DecorationGridAdapter(DecorationActivity.this, group.get(position).getList());
//                GridView gridView = new GridView(DecorationActivity.this);
                GridView gridView = holder.getView(R.id.decoration_item_gridview);
                gridView.setFocusable(false);
                gridView.setAdapter(adapter);

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

//                Intent intent = new Intent(getActivity(), OtherUserActivity.class);
//                intent.putExtra("uid", users.get(position).getUid());
//                intent.putExtra("nickname", users.get(position).getNickname());
//                getActivity().startActivity(intent);
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commonAdapter);

    }
}
