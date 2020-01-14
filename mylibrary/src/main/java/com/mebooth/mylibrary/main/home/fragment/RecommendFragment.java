package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.RecommendAdapter;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.QuicklyActivity;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
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

public class RecommendFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener, OnItemClickListener {

    //    private MultiItemTypeAdapter commonAdapter;
//    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 10;
    private String offSet = "";

    private MyHandler mHandler;

    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();
    private FlushJson bannerJson;

    private RecommendAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.recommend_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mHandler = new MyHandler(this);
        getConfigBanner();

//        getRecommend(REFLUSH_LIST);
    }

    private void getConfigBanner() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .bannerList("banner")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<FlushJson>() {
                    @Override
                    public void onNext(FlushJson flushJson) {
                        super.onNext(flushJson);

                        if (null != flushJson && flushJson.getErrno() == 0) {


                            bannerJson = flushJson;
                            initRecycle();
                            mSmart.autoRefresh();
                        } else if (null != flushJson && flushJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("RecommendFragment", "token已被清空");
                        } else if (null != flushJson && flushJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(flushJson.getErrmsg()) ? "数据加载失败" : flushJson.getErrmsg());
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
                .getRecommend("recommend", offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetRecommendJson>() {
                    @Override
                    public void onNext(GetRecommendJson getRecommendJson) {
                        super.onNext(getRecommendJson);

                        if (null != getRecommendJson && getRecommendJson.getErrno() == 0) {
                            offSet = String.valueOf(getRecommendJson.getData().getOffset());
                            initList(tag, getRecommendJson);

//                            UIUtils.clearMemoryCache(getActivity());

                        } else if (null != getRecommendJson && getRecommendJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("RecommendFragment", "token已被清空");
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
            GetRecommendJson.RecommendData.RecommendDataList recommendDataList = new GetRecommendJson.RecommendData.RecommendDataList();
            recommend.add(recommendDataList);
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

    @Override
    public void OnItemClick(int position) {
        Log.d("TAG", "");
        //TODO 详情
        if (recommend.get(position).getFeed().getType() == 1) {
            Intent intent = new Intent(getActivity(), NowDetailsActivity.class);
            intent.putExtra("relateid", recommend.get(position).getFeed().getRelateid());
            intent.putExtra("uid", recommend.get(position).getUser().getUid());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
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

    private static class MyHandler extends Handler {
        WeakReference<Fragment> reference;

        public MyHandler(Fragment context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != reference) {
                RecommendFragment activity = (RecommendFragment) reference.get();
                if (reference.get() != null) {
                    if (msg.what == activity.REFLUSH_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
//                            activity.adapter.notifyDataSetChanged();
                            activity.mSmart.finishRefresh();
                        }

                    } else if (msg.what == activity.LOADMORE_LIST) {
                        if (activity.mSmart != null) {
                            activity.commonAdapter.notifyDataSetChanged();
//                            activity.adapter.notifyDataSetChanged();
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

    private int praises;

    private void initRecycle() {
        commonAdapter = new CommonAdapter(getActivity(), R.layout.recommenditem, recommend) {

            @Override
            protected void convert(final ViewHolder holder, Object o, final int position) {

//                holder.setText(R.id.tv_item_message_time , mListMessageData.get(position).getCreate_time());
//                holder.setText(R.id.tv_content , mListMessageData.get(position).getContent());

                if (position == 0) {

                    holder.setVisible(R.id.recommend_header_lly, View.VISIBLE);
                    holder.setVisible(R.id.recommendnews_item, View.GONE);
                    holder.setVisible(R.id.recommendnow_item, View.GONE);
                    GlideImageManager.glideLoader(getActivity(), bannerJson.getData().getConfig().getImage(), (ImageView) holder.getView(R.id.recommenditem_headerimg), GlideImageManager.TAG_FILLET);

                    holder.setOnClickListener(R.id.recommenditem_headerimg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("relateid", bannerJson.getData().getConfig().getNewsid());
                            startActivity(intent);

                        }
                    });


                    holder.setOnClickListener(R.id.buy_car, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("title", "购车指南");
                            startActivity(intent);

                        }
                    });
                    holder.setOnClickListener(R.id.publicusecar, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                            intent.putExtra("type", "2");
                            intent.putExtra("title", "公务用车");
                            startActivity(intent);
                        }
                    });
                    holder.setOnClickListener(R.id.logisticsusecar, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                            intent.putExtra("type", "3");
                            intent.putExtra("title", "物流用车");
                            startActivity(intent);
                        }
                    });

                } else {
                    holder.setVisible(R.id.recommend_header_lly, View.GONE);
                    holder.setVisible(R.id.recommendnews_item, View.VISIBLE);
                    holder.setVisible(R.id.recommendnow_item, View.GONE);

                    if (recommend.get(position).getFeed().getType() != 1) {

                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
                        holder.setText(R.id.recommenditem_nickname, recommend.get(position).getUser().getNickname());

                        holder.setText(R.id.recommenditem_content, recommend.get(position).getFeed().getContent());
                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_img), GlideImageManager.TAG_FILLET);
                        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
                        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
                        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
                        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
                        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
                        holder.setText(R.id.recommenditem_time, (month) + "-" + date + " " + hour + ":" + minute);

                        holder.setText(R.id.recommenditem_browsecount, String.valueOf(recommend.get(position).getFeed().getWatches()));
                        holder.setText(R.id.recommenditem_commentcount, String.valueOf(recommend.get(position).getFeed().getReplies()));


                    } else {

                        if (recommend.get(position).getFeed().getImages().size() == 1) {

                            holder.setVisible(R.id.recommenditem_imgone1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.GONE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);

                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone1), GlideImageManager.TAG_FILLET);

                        } else if (recommend.get(position).getFeed().getImages().size() == 2) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.GONE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);

                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone2), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo1), GlideImageManager.TAG_FILLET);


                        } else if (recommend.get(position).getFeed().getImages().size() == 3) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);

                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone3), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo2), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(2), (ImageView) holder.getView(R.id.recommenditem_imgthree1), GlideImageManager.TAG_FILLET);


                        } else if (recommend.get(position).getFeed().getImages().size() >= 4) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.VISIBLE);

                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone4), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo3), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(2), (ImageView) holder.getView(R.id.recommenditem_imgthree2), GlideImageManager.TAG_FILLET);
                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(3), (ImageView) holder.getView(R.id.recommenditem_imgfour), GlideImageManager.TAG_FILLET);
                            if (recommend.get(position).getFeed().getImages().size() == 4) {
                                holder.setVisible(R.id.recommenditem_imgmore, View.GONE);
                            } else {
                                holder.setVisible(R.id.recommenditem_imgmore, View.VISIBLE);
                                holder.setText(R.id.recommenditem_imgmore, recommend.get(position).getFeed().getImages().size() + "图");
                            }
                        }

                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon1), GlideImageManager.TAG_ROUND);
                        holder.setText(R.id.recommenditem_nickname1, recommend.get(position).getUser().getNickname());

                        if (recommend.get(position).getUser().isFollowed()) {
                            holder.setText(R.id.recommenditem_follow, "已关注");
                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
                        } else {
                            holder.setText(R.id.recommenditem_follow, "关注");
                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
                        }
                        if (AppApplication.getInstance().userid != null) {
                            if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                                holder.setVisible(R.id.recommenditem_follow, View.GONE);
                            } else {
                                holder.setVisible(R.id.recommenditem_follow, View.VISIBLE);
                            }
                        }

                        holder.setText(R.id.recommenditem_content1, recommend.get(position).getFeed().getContent());
                        if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                            holder.setVisible(R.id.recommenditem_address, View.GONE);
                        } else {
                            holder.setText(R.id.recommenditem_address, recommend.get(position).getFeed().getLocation());
                            holder.setVisible(R.id.recommenditem_address, View.VISIBLE);
                        }
                        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
                        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
                        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
                        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
                        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
                        holder.setText(R.id.recommenditem_time, (month + 1) + "-" + date + " " + hour + ":" + minute);


                        if (recommend.get(position).getFeed().isPraised()) {
                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
                        } else {
                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
                        }
                        praises = recommend.get(position).getFeed().getPraises();
                        holder.setText(R.id.recommenditem_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
                        holder.setText(R.id.recommenditem_comment, String.valueOf(recommend.get(position).getFeed().getReplies()));


                        holder.setOnClickListener(R.id.recommenditem_follow, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                    AppApplication.getInstance().setLogin();

                                } else {
                                    if (recommend.get(position).getUser().isFollowed()) {
                                        //取消关注
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .cancelFollow(recommend.get(position).getUser().getUid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getUser().setFollowed(false);
                                                            ToastUtils.getInstance().showToast("已取消关注");
                                                            holder.setText(R.id.recommenditem_follow, "关注");
                                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
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
                                                .addFollow(recommend.get(position).getUser().getUid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getUser().setFollowed(true);
                                                            ToastUtils.getInstance().showToast("已关注");
                                                            holder.setText(R.id.recommenditem_follow, "已关注");
                                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
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
                            }
                        });

                        holder.setOnClickListener(R.id.recommenditem_collect_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                    AppApplication.getInstance().setLogin();

                                } else {
                                    if (recommend.get(position).getFeed().isPraised()) {
                                        //取消收藏
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .cancelPraises(recommend.get(position).getFeed().getRelateid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(false);
                                                            ToastUtils.getInstance().showToast("已取消收藏");
                                                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
                                                            praises = praises - 1;
                                                            holder.setText(R.id.recommenditem_collect, String.valueOf(praises));
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

                                        //添加收藏
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .addPraises(recommend.get(position).getFeed().getRelateid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(true);
                                                            ToastUtils.getInstance().showToast("已收藏");
                                                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
                                                            praises = praises + 1;
                                                            holder.setText(R.id.recommenditem_collect, String.valueOf(praises));
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

                            }
                        });
                        holder.setOnClickListener(R.id.recommenditem_headericon, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                    AppApplication.getInstance().setLogin();

                                } else {
                                    Intent intent = new Intent(getActivity(), OtherUserActivity.class);
                                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
                                    startActivity(intent);
                                }
                            }
                        });


                    }


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

//        recyclerView.setAdapter(commonAdapter);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbindDrawables(recyclerView);

    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }


}
