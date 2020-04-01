package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewFour;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewThree;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewTwo;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.MePublishNewsActivity;
import com.mebooth.mylibrary.main.home.activity.MePublishTopicActivity;
import com.mebooth.mylibrary.main.home.activity.MineActivity;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
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
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MePublishFragment extends BaseFragment implements OnRefreshListener {
    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter1;
    private MultiItemTypeAdapter commonAdapter2;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;


    private int pageSize = 3;
    private String offSet = "";

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private ArrayList<String> userPublishList = new ArrayList<>();
    private ArrayList<UserNewsListJson.UserNewsListData.UserNewsList> userNewsList = new ArrayList<>();
    private ArrayList<GetNowJson.NowData.NowDataList> userTopicList = new ArrayList<>();

    private int uid;
    private TextView notPublish;
    private MineActivity.refreshData refreshData;

    public MePublishFragment(int uid, MineActivity.refreshData refreshData) {
        this.uid = uid;
        this.refreshData = refreshData;
    }

//    public static MePublishFragment getInstance(int uid) {
//        MePublishFragment sf = new MePublishFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("uid", uid);
//        sf.setArguments(bundle);
//        return sf;
//    }

    @Override
    protected int getLayoutResId() {
        return R.layout.mepublish_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        recyclerView.setVisibility(View.INVISIBLE);
        notPublish = view.findViewById(R.id.mepublish_notpublish);
        mSmart = view.findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

        view.findViewById(R.id.public_header).setVisibility(View.GONE);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

//        Bundle bundle = getArguments();
//        uid = bundle.getInt("uid");

        userPublishList.add("我发布的笔记");
        userPublishList.add("我发布的此刻");


        initRecycle();
//        getRecommend(REFLUSH_LIST);
//
    }

    private void initRecycle() {

        commonAdapter = new CommonAdapter(getActivity(), R.layout.userpublish_item, userPublishList) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                holder.setText(R.id.userpublish_title, userPublishList.get(position));

                holder.setOnClickListener(R.id.userpublish_lly, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {
                            Intent intent = new Intent(getActivity(), MePublishNewsActivity.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("index","");
                            startActivity(intent);

                        }else{
                            Intent intent = new Intent(getActivity(), MePublishTopicActivity.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("index","");
                            startActivity(intent);
                        }

                    }
                });

                if (position == 0) {
//                    holder.setVisible(R.id.bgf6f6f6,View.VISIBLE);
                    commonAdapter1 = new CommonAdapter(getActivity(), R.layout.usernews_item, userNewsList) {
                        @Override
                        protected void convert(ViewHolder holder, Object o, final int position) {

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

                                    new AlertView("温馨提示", "您确定要删除？", "取消", new String[]{"确定"}, null, getActivity(),
                                            AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            if (position == 0) {

                                                deleteNews(userNewsList.get(position).getNewsid());
                                            }
                                        }
                                    }).show();
                                }
                            });

                        }
                    };
                    commonAdapter1.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO 订单详情

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("uid", userNewsList.get(position).getUid());
                            intent.putExtra("relateid", userNewsList.get(position).getNewsid());
                            getActivity().startActivity(intent);
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

                        }

                        @Override
                        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                            return false;
                        }
                    });
                    RecyclerView recyclerView1 = holder.getView(R.id.userpublish_recycleview);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView1.setAdapter(commonAdapter1);

                } else if (position == 1) {

//                    holder.setVisible(R.id.bgf6f6f6,View.GONE);
                    NoPublish noPublishinterface = new NoPublish() {
                        @Override
                        public void isPublish() {

//                noPublish.setVisibility(View.VISIBLE);
                            getRecommend();
                        }

                        @Override
                        public void isCollect() {

                        }

                        @Override
                        public void showAddButton() {
                            refreshData.refresh();
                        }
                    };

                    commonAdapter2 = new MultiItemTypeAdapter(getActivity(), userTopicList);
                    commonAdapter2.addItemViewDelegate(new NowItemVIewZero(getActivity(), "minepublic", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewOne(getActivity(), "minepublic", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewTwo(getActivity(), "minepublic", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewThree(getActivity(), "minepublic", commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewFour(getActivity(), "minepublic", commonAdapter2, userTopicList, noPublishinterface));

                    commonAdapter2.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO 详情
                            Intent intent = new Intent(getActivity(), NowDetailsActivity.class);
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
                    recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(commonAdapter);


    }

    private void deleteNews(int newsid) {

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
                            refreshData.refresh();
                            ToastUtils.getInstance().showToast("删除新闻成功");
                            getNews();

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

                                recyclerView.setVisibility(View.GONE);
                                notPublish.setVisibility(View.VISIBLE);

                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                notPublish.setVisibility(View.GONE);
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
