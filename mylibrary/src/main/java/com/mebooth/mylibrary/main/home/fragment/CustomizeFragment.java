package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.alertview.AlertView;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.NewsFeatureActivity;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.bean.CustomizeJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.utils.GridSpacingItemDecoration;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.SpacesItemDecoration;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CustomizeFragment extends BaseFragment implements OnRefreshListener {

    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter1;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

//    private int pageSize = 10;
//    private String offSet = "";

    private MyHandler mHandler;

    private ArrayList<CustomizeJson.CustomizeData.CustomizeSubjects> subjects = new ArrayList<>();
    private String headerImage = "";
    private String newid = "";


    @Override
    protected int getLayoutResId() {
        return R.layout.customize_layout;
    }

    @Override
    protected void initView(View view) {

        recyclerView = view.findViewById(R.id.classify_recycle);
        recyclerView.setItemViewCacheSize(10);
        mSmart = view.findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mHandler = new MyHandler(this);
//        getConfigBanner();
        initRecycle();
        mSmart.autoRefresh();
    }

    private void getCustomizeInfo(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .customiseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<CustomizeJson>() {
                    @Override
                    public void onNext(CustomizeJson customizeJson) {
                        super.onNext(customizeJson);

                        if (null != customizeJson && customizeJson.getErrno() == 0) {
                            initList(tag, customizeJson);

                            headerImage = customizeJson.getData().getBanner().getImage();
                            newid = customizeJson.getData().getBanner().getNewsid();

                            UIUtils.clearMemoryCache(getActivity());

                        } else if (null != customizeJson && customizeJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("RecommendFragment", "token已被清空");
                            cancelRefresh(tag);
                        } else if (null != customizeJson && customizeJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(customizeJson.getErrmsg()) ? "数据加载失败" : customizeJson.getErrmsg());
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

    private void initList(int tag, CustomizeJson customizeJson) {

        if (tag == REFLUSH_LIST) {
            subjects.clear();
            commonAdapter.notifyDataSetChanged();
            if (customizeJson.getData().getSubjects().size() != 0) {
                subjects.add(customizeJson.getData().getSubjects().get(0));
            }
            subjects.addAll(customizeJson.getData().getSubjects());
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        }
    }


    private static class MyHandler extends Handler {

        WeakReference<Fragment> reference;

        public MyHandler(Fragment context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != reference) {
                CustomizeFragment activity = (CustomizeFragment) reference.get();
                if (reference.get() != null) {
                    if (msg.what == activity.REFLUSH_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
//                            activity.adapter.notifyDataSetChanged();
                            activity.mSmart.finishRefresh();
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
        mSmart.setEnableLoadMore(false);

    }

    private void initRecycle() {

        commonAdapter = new CommonAdapter(getActivity(), R.layout.customiseitem_layout, subjects) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position == 0) {

                    holder.setVisible(R.id.customiseitem_headerimg, View.VISIBLE);
                    holder.setVisible(R.id.customiseitem_content, View.GONE);
                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_headerimg), 0, headerImage, RoundedCornersTransformation.CORNER_ALL);

                    holder.setOnClickListener(R.id.customiseitem_headerimg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
//                            intent.putExtra("uid", );
                            intent.putExtra("relateid", Integer.valueOf(newid));
                            getActivity().startActivity(intent);

                        }
                    });

                } else {
                    holder.setVisible(R.id.customiseitem_headerimg, View.GONE);
                    holder.setVisible(R.id.customiseitem_content, View.VISIBLE);
                    holder.setText(R.id.customiseitem_title, subjects.get(position).getTitle() + " · " + subjects.get(position).getTotal() + "篇");

                    holder.setOnClickListener(R.id.customiseitem_title_lly, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewsFeatureActivity.class);
                            intent.putExtra("type", subjects.get(position).getName());
                            startActivity(intent);
                        }
                    });

                    if (subjects.get(position).getFeeds().size() == 1) {

                        holder.setVisible(R.id.customiseitem_lly2, View.GONE);
                        holder.setVisible(R.id.customiseitem_lly3, View.GONE);
                        holder.setVisible(R.id.customiseitem_lly4, View.GONE);

                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image1), 0, subjects.get(position).getFeeds().get(0).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text1, subjects.get(position).getFeeds().get(0).getFeed().getContent());

                    } else if (subjects.get(position).getFeeds().size() == 2) {
                        holder.setVisible(R.id.customiseitem_lly2, View.VISIBLE);
                        holder.setVisible(R.id.customiseitem_lly3, View.GONE);
                        holder.setVisible(R.id.customiseitem_lly4, View.GONE);

                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image1), 0, subjects.get(position).getFeeds().get(0).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text1, subjects.get(position).getFeeds().get(0).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image2), 0, subjects.get(position).getFeeds().get(1).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text2, subjects.get(position).getFeeds().get(1).getFeed().getContent());

                    } else if (subjects.get(position).getFeeds().size() == 3) {
                        holder.setVisible(R.id.customiseitem_lly2, View.VISIBLE);
                        holder.setVisible(R.id.customiseitem_lly3, View.VISIBLE);
                        holder.setVisible(R.id.customiseitem_lly4, View.GONE);

                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image1), 0, subjects.get(position).getFeeds().get(0).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text1, subjects.get(position).getFeeds().get(0).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image2), 0, subjects.get(position).getFeeds().get(1).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text2, subjects.get(position).getFeeds().get(1).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image3), 0, subjects.get(position).getFeeds().get(2).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text3, subjects.get(position).getFeeds().get(2).getFeed().getContent());

                    } else if (subjects.get(position).getFeeds().size() == 4) {
                        holder.setVisible(R.id.customiseitem_lly2, View.VISIBLE);
                        holder.setVisible(R.id.customiseitem_lly3, View.VISIBLE);
                        holder.setVisible(R.id.customiseitem_lly4, View.VISIBLE);

                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image1), 0, subjects.get(position).getFeeds().get(0).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text1, subjects.get(position).getFeeds().get(0).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image2), 0, subjects.get(position).getFeeds().get(1).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text2, subjects.get(position).getFeeds().get(1).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image3), 0, subjects.get(position).getFeeds().get(2).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text3, subjects.get(position).getFeeds().get(2).getFeed().getContent());
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitem_image4), 0, subjects.get(position).getFeeds().get(3).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                        holder.setText(R.id.customiseitem_text4, subjects.get(position).getFeeds().get(3).getFeed().getContent());

                    }

                    holder.setOnClickListener(R.id.customiseitem_lly1, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("uid", subjects.get(position).getFeeds().get(0).getUser().getUid());
                            intent.putExtra("relateid", subjects.get(position).getFeeds().get(0).getFeed().getRelateid());
                            getActivity().startActivity(intent);
                        }
                    });
                    holder.setOnClickListener(R.id.customiseitem_lly2, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("uid", subjects.get(position).getFeeds().get(1).getUser().getUid());
                            intent.putExtra("relateid", subjects.get(position).getFeeds().get(1).getFeed().getRelateid());
                            getActivity().startActivity(intent);
                        }
                    });

                    holder.setOnClickListener(R.id.customiseitem_lly3, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("uid", subjects.get(position).getFeeds().get(2).getUser().getUid());
                            intent.putExtra("relateid", subjects.get(position).getFeeds().get(2).getFeed().getRelateid());
                            getActivity().startActivity(intent);
                        }
                    });

                    holder.setOnClickListener(R.id.customiseitem_lly4, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("uid", subjects.get(position).getFeeds().get(3).getUser().getUid());
                            intent.putExtra("relateid", subjects.get(position).getFeeds().get(3).getFeed().getRelateid());
                            getActivity().startActivity(intent);
                        }
                    });


//                    commonAdapter1 = new CommonAdapter(getActivity(), R.layout.customise_itemtwo_layout, subjects.get(position).getFeeds()) {
//                        @Override
//                        protected void convert(ViewHolder holder, Object o, final int position1) {
//
//                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.customiseitemtwo_image), 0, subjects.get(position).getFeeds().get(position1).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
//
//                            holder.setText(R.id.customiseitemtwo_title, subjects.get(position).getFeeds().get(position1).getFeed().getContent());
//
//
//                        }
//                    };
//                    commonAdapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position1) {
//
//                            //TODO 订单详情
//
//                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
//                            intent.putExtra("uid", subjects.get(position).getFeeds().get(position1).getUser().getUid());
//                            intent.putExtra("relateid", subjects.get(position).getFeeds().get(position1).getFeed().getRelateid());
//                            getActivity().startActivity(intent);
////                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");
//
//                        }
//
//                        @Override
//                        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
//                            return false;
//                        }
//                    });
//                    RecyclerView recyclerView1 = holder.getView(R.id.customiseitem_recycle);
//                    recyclerView1.addItemDecoration(new GridSpacingItemDecoration(2, UIUtils.dp2px(getActivity(), 5), true));
//                    recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//                    recyclerView1.setAdapter(commonAdapter1);

                }

            }

        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {


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
        getCustomizeInfo(REFLUSH_LIST);

    }

}
