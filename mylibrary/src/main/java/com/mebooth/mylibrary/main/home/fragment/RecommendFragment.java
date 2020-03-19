package com.mebooth.mylibrary.main.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.mebooth.mylibrary.main.home.activity.NewsFeatureActivity;
import com.mebooth.mylibrary.main.home.activity.NewsOtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.NowDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.QuicklyActivity;
import com.mebooth.mylibrary.main.home.bean.EntranceJson;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.ResourcseMessage;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.DateUtils;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
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
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener, OnItemClickListener {

    //    private MultiItemTypeAdapter commonAdapter;
    private CommonAdapter commonAdapter;
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

    private ArrayList<EntranceJson.EntranceData.EntranceConfig> config = new ArrayList<>();
    public static boolean isRecommendRefresh = false;
    private int id;
    private boolean isPraise = false;
    private String index = "";
    private int type;
    private String foward = "";
    private String banner = "";
    private String entrance = "";

    public static RecommendFragment getInstance(String foward) {
        RecommendFragment sf = new RecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("foward", foward);
        sf.setArguments(bundle);
        return sf;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.recommend_layout;
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

//        Log.d("packagename", getActivity().getApplicationInfo().processName);

        if (getActivity().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            banner = "mfbanner";
            entrance = "mfquick_entrance";

        } else if (getActivity().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            banner = "xmbanner";
            entrance = "xmquick_entrance";
        } else {
            banner = "banner";
            entrance = "quick_entrance";
        }

        Bundle bundle = getArguments();
        foward = bundle.getString("foward");

        //注册广播
        IntentFilter filter = new IntentFilter("dataRefresh");
        getActivity().registerReceiver(broadcastReceiver, filter);

        mHandler = new MyHandler(this);
//        getConfigBanner();
        mSmart.setEnableLoadMore(false);
        initRecycle();
        mSmart.autoRefresh();
//        getRecommend(REFLUSH_LIST);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            index = intent.getStringExtra("index");
            type = intent.getIntExtra("type", 11111);
            id = intent.getIntExtra("id", 0);
            isPraise = intent.getBooleanExtra("isPraise", false);

            if (index.equals("cancel")) {

                for (int i = 0; i < recommend.size(); i++) {

                    if (recommend.get(i).getFeed().getRelateid() == id) {

                        if (recommend.get(i).getFeed().getType() == type) {
                            recommend.get(i).getFeed().setPraised(isPraise);
                            recommend.get(i).getFeed().setPraises(recommend.get(i).getFeed().getPraises() - 1);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }

            } else if (index.equals("add")) {

                for (int i = 0; i < recommend.size(); i++) {

                    if (recommend.get(i).getFeed().getRelateid() == id) {
                        if (recommend.get(i).getFeed().getType() == type) {
                            recommend.get(i).getFeed().setPraised(isPraise);
                            recommend.get(i).getFeed().setPraises(recommend.get(i).getFeed().getPraises() + 1);
                            commonAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        }
    };

    private void getConfigEntrance() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .entranceList(entrance)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<EntranceJson>() {
                    @Override
                    public void onNext(EntranceJson entranceJson) {
                        super.onNext(entranceJson);

                        if (null != entranceJson && entranceJson.getErrno() == 0) {

                            config.clear();
                            config.addAll(entranceJson.getData().getConfig());

                            getRecommend(REFLUSH_LIST);
                        } else if (null != entranceJson && entranceJson.getErrno() == 1101) {
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("RecommendFragment", "token已被清空");
                        } else if (null != entranceJson && entranceJson.getErrno() != 200) {
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(entranceJson.getErrmsg()) ? "数据加载失败" : entranceJson.getErrmsg());
                        } else {
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mSmart != null) {
                            mSmart.finishRefresh();
                        }
                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });


    }

    private void getConfigBanner() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .bannerList(banner)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<FlushJson>() {
                    @Override
                    public void onNext(FlushJson flushJson) {
                        super.onNext(flushJson);

                        if (null != flushJson && flushJson.getErrno() == 0) {


                            bannerJson = flushJson;
                            getConfigEntrance();

                        } else if (null != flushJson && flushJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("RecommendFragment", "token已被清空");
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                        } else if (null != flushJson && flushJson.getErrno() != 200) {
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(flushJson.getErrmsg()) ? "数据加载失败" : flushJson.getErrmsg());
                        } else {
                            if (mSmart != null) {
                                mSmart.finishRefresh();
                            }
                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mSmart != null) {
                            mSmart.finishRefresh();
                        }
                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });


    }

    private void getRecommend(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getRecommend(foward, offSet, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetRecommendJson>() {
                    @Override
                    public void onNext(GetRecommendJson getRecommendJson) {
                        super.onNext(getRecommendJson);

                        if (null != getRecommendJson && getRecommendJson.getErrno() == 0) {
                            offSet = String.valueOf(getRecommendJson.getData().getOffset());
                            initList(tag, getRecommendJson);

                            UIUtils.clearMemoryCache(getActivity());

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
            mSmart.setEnableLoadMore(true);
            recommend.clear();
            commonAdapter.notifyDataSetChanged();
            if (getRecommendJson.getData().getList().size() != 0) {
                recommend.add(getRecommendJson.getData().getList().get(0));
            }
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
                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_headerimg), 0, bannerJson.getData().getConfig().getImage(), RoundedCornersTransformation.CORNER_ALL);
                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.buy_car), 0, config.get(0).getImage(), RoundedCornersTransformation.CORNER_ALL);
                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.publicusecar), 0, config.get(1).getImage(), RoundedCornersTransformation.CORNER_ALL);
                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.logisticsusecar), 0, config.get(2).getImage(), RoundedCornersTransformation.CORNER_ALL);
//                    holder.setText(R.id.recommenditem_headertitle, bannerJson.getData().getConfig().getTitle());
//                    holder.setText(R.id.recommenditem_headername, bannerJson.getData().getConfig().getNickname());
//                    holder.setText(R.id.recommenditem_headertime, bannerJson.getData().getConfig().getAddtime());
//                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_headeravatar), 50, bannerJson.getData().getConfig().getAvatar(), RoundedCornersTransformation.CORNER_ALL);


//                    GlideImageManager.glideLoader(getActivity(), bannerJson.getData().getConfig().getImage(), (ImageView) holder.getView(R.id.recommenditem_headerimg), GlideImageManager.TAG_FILLET);

                    holder.setOnClickListener(R.id.recommenditem_headerimg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), NewDetailsActivity.class);
                            intent.putExtra("relateid", bannerJson.getData().getConfig().getNewsid());
                            startActivity(intent);

                        }
                    });

                    holder.setText(R.id.buy_car_text, config.get(0).getName());
                    holder.setText(R.id.publicusecar_text, config.get(1).getName());
                    holder.setText(R.id.logisticsusecar_text, config.get(2).getName());
                    holder.setOnClickListener(R.id.buy_car, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

//                            Intent intent = new Intent(getActivity(), QuicklyActivity.class);

                            if (config.get(0).getTarget().equals("url")) {
                                Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                                intent.putExtra("url", config.get(0).getFoward());
                                intent.putExtra("title", config.get(0).getName());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity(), NewsFeatureActivity.class);
                                intent.putExtra("type", config.get(0).getFoward());
                                intent.putExtra("image", config.get(0).getImage());
                                intent.putExtra("title", config.get(0).getName());
                                startActivity(intent);
                            }


                        }
                    });
                    holder.setOnClickListener(R.id.publicusecar, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (config.get(1).getTarget().equals("url")) {
                                Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                                intent.putExtra("url", config.get(1).getFoward());
                                intent.putExtra("title", config.get(1).getName());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity(), NewsFeatureActivity.class);
                                intent.putExtra("type", config.get(1).getFoward());
                                intent.putExtra("image", config.get(1).getImage());
                                intent.putExtra("title", config.get(1).getName());
                                startActivity(intent);
                            }


                        }
                    });
                    holder.setOnClickListener(R.id.logisticsusecar, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (config.get(1).getTarget().equals("url")) {
                                Intent intent = new Intent(getActivity(), QuicklyActivity.class);
                                intent.putExtra("url", config.get(2).getFoward());
                                intent.putExtra("title", config.get(2).getName());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity(), NewsFeatureActivity.class);
                                intent.putExtra("type", config.get(2).getFoward());
                                intent.putExtra("image", config.get(2).getImage());
                                intent.putExtra("title", config.get(2).getName());
                                startActivity(intent);
                            }

                        }
                    });

                } else {

                    if (recommend.get(position).getFeed().getType() != 1) {
                        holder.setVisible(R.id.recommend_header_lly, View.GONE);
                        holder.setVisible(R.id.recommendnews_item, View.VISIBLE);
                        holder.setVisible(R.id.recommendnow_item, View.GONE);
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_headericon), 50, recommend.get(position).getUser().getAvatar(), RoundedCornersTransformation.CORNER_ALL);

                        if (AppApplication.getInstance().userid != null) {
                            if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                                holder.setVisible(R.id.recommenditemzixun_follow, View.GONE);
                            } else {
                                holder.setVisible(R.id.recommenditemzixun_follow, View.VISIBLE);
                            }
                        }

                        if (recommend.get(position).getFeed().getLocation().equals("")) {
                            holder.setVisible(R.id.news_address, View.GONE);
                        } else {
                            holder.setVisible(R.id.news_address, View.VISIBLE);
                        }

                        holder.setText(R.id.news_address, recommend.get(position).getFeed().getLocation());

//                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
                        holder.setText(R.id.recommenditem_nickname, recommend.get(position).getUser().getNickname());

                        holder.setText(R.id.recommenditem_content, recommend.get(position).getFeed().getContent());
                        holder.setText(R.id.recommenditem_zhaiyao, recommend.get(position).getFeed().getDescribe().replace("\\n", "\n"));
                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_img), 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
//                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_img), GlideImageManager.TAG_FILLET);
//                        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
//                        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
//                        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
//                        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
//                        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));

                        Date date = DateUtils.parseDate(recommend.get(position).getFeed().getAddtime(), "yyyy-MM-dd HH:mm:ss");
                        if (date == null) {
                            return;
                        }
                        long diff = new Date().getTime() - date.getTime();
                        long r = (diff / (60 * (60 * 1000)));

                        if (r > 12) {
                            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
                            int date1 = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
                            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
                            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));

                            if (month < 10 && date1 < 10) {

                                holder.setText(R.id.recommenditem_time, "0" + (month + 1) + "-0" + date1);
                            } else if (month < 10) {
                                holder.setText(R.id.recommenditem_time, "0" + (month + 1) + "-" + date1);
                            } else if (date1 < 10) {
                                holder.setText(R.id.recommenditem_time, (month + 1) + "-0" + date1);
                            }

                        } else {
                            String time = DateUtils.getTimeFormatText(date);
                            holder.setText(R.id.recommenditem_time, time);
                        }


                        if (recommend.get(position).getUser().isFollowed()) {
                            holder.setText(R.id.recommenditemzixun_follow, "已关注");
                            holder.setTextColor(R.id.recommenditemzixun_follow, getResources().getColor(R.color.bg_999999));
                            holder.setBackgroundRes(R.id.recommenditemzixun_follow, R.drawable.nofollow);
                        } else {
                            holder.setText(R.id.recommenditemzixun_follow, "关注");
                            holder.setTextColor(R.id.recommenditemzixun_follow, getResources().getColor(ResourcseMessage.getFontColor()));
                            holder.setBackgroundRes(R.id.recommenditemzixun_follow, ResourcseMessage.getFollowBackground());
                        }


                        if (recommend.get(position).getFeed().isPraised()) {

                            holder.setImageResource(R.id.recommenditemzixun_collect_img, ResourcseMessage.getPraiseRes());

                        } else {
                            holder.setImageResource(R.id.recommenditemzixun_collect_img, R.drawable.nopraise);
                        }

                        holder.setText(R.id.recommenditem_browsecount, String.valueOf(recommend.get(position).getFeed().getWatches()));
                        holder.setText(R.id.recommenditem_commentcount, String.valueOf(recommend.get(position).getFeed().getReplies()));
                        holder.setText(R.id.recommenditemzixun_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));

                        holder.setOnClickListener(R.id.recommenditemzixun_follow, new View.OnClickListener() {
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
                                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                                    recommendDataList.getUser().setFollowed(false);
                                                                }
                                                            }
//                                                            recommend.get(position).getUser().setFollowed(false);
                                                            commonAdapter.notifyDataSetChanged();
                                                            ToastUtils.getInstance().showToast("已取消关注");
//                                                            holder.setText(R.id.recommenditemzixun_follow, "关注");
//                                                            holder.setTextColor(R.id.recommenditemzixun_follow, getResources().getColor(R.color.bg_E73828));
//                                                            holder.setBackgroundRes(R.id.recommenditemzixun_follow, R.drawable.follow);
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
                                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                                    recommendDataList.getUser().setFollowed(true);
                                                                }
                                                            }
                                                            commonAdapter.notifyDataSetChanged();
//                                                            recommend.get(position).getUser().setFollowed(true);
                                                            ToastUtils.getInstance().showToast("已关注");
//                                                            holder.setText(R.id.recommenditemzixun_follow, "已关注");
//                                                            holder.setTextColor(R.id.recommenditemzixun_follow, getResources().getColor(R.color.bg_999999));
//                                                            holder.setBackgroundRes(R.id.recommenditemzixun_follow, R.drawable.nofollow);
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

                        holder.setOnClickListener(R.id.recommenditemzixun_collect_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                    AppApplication.getInstance().setLogin();

                                } else {
                                    if (recommend.get(position).getFeed().isPraised()) {
                                        //取消收藏
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .cancelPraises(recommend.get(position).getFeed().getRelateid(), 1)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(false);
                                                            ToastUtils.getInstance().showToast("已取消点赞");
                                                            holder.setImageResource(R.id.recommenditemzixun_collect_img, R.drawable.nopraise);
                                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() - 1);
                                                            holder.setText(R.id.recommenditemzixun_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
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
                                                .addPraises(recommend.get(position).getFeed().getRelateid(), 1)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(true);
                                                            ToastUtils.getInstance().showToast("已点赞");
                                                            holder.setImageResource(R.id.recommenditemzixun_collect_img, ResourcseMessage.getPraiseRes());
                                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() + 1);
                                                            holder.setText(R.id.recommenditemzixun_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
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

                                    Intent intent = new Intent(getActivity(), NewsOtherUserActivity.class);
                                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
                                    startActivity(intent);
                                }
                            }
                        });


                    } else {
                        holder.setVisible(R.id.recommend_header_lly, View.GONE);
                        holder.setVisible(R.id.recommendnews_item, View.GONE);
                        holder.setVisible(R.id.recommendnow_item, View.VISIBLE);
                        if (recommend.get(position).getFeed().getImages().size() == 1) {

                            holder.setVisible(R.id.recommenditem_imgone1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.GONE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgone1), 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);

//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone1), GlideImageManager.TAG_FILLET);

                        } else if (recommend.get(position).getFeed().getImages().size() == 2) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.GONE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);

                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgone2), 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgtwo1), 8, recommend.get(position).getFeed().getImages().get(1), RoundedCornersTransformation.CORNER_ALL);

//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone2), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo1), GlideImageManager.TAG_FILLET);


                        } else if (recommend.get(position).getFeed().getImages().size() == 3) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.GONE);

                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgone3), 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgtwo2), 8, recommend.get(position).getFeed().getImages().get(1), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgthree1), 8, recommend.get(position).getFeed().getImages().get(2), RoundedCornersTransformation.CORNER_ALL);
//
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone3), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo2), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(2), (ImageView) holder.getView(R.id.recommenditem_imgthree1), GlideImageManager.TAG_FILLET);


                        } else if (recommend.get(position).getFeed().getImages().size() >= 4) {
                            holder.setVisible(R.id.recommenditem_imgone1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llytwo, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llythree, View.GONE);
                            holder.setVisible(R.id.recommenditem_imgthree1, View.GONE);
                            holder.setVisible(R.id.recommenditem_img_llyfour1, View.VISIBLE);
                            holder.setVisible(R.id.recommenditem_img_llyfour2, View.VISIBLE);


                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgone4), 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgtwo3), 8, recommend.get(position).getFeed().getImages().get(1), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgthree2), 8, recommend.get(position).getFeed().getImages().get(2), RoundedCornersTransformation.CORNER_ALL);
                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_imgfour), 8, recommend.get(position).getFeed().getImages().get(3), RoundedCornersTransformation.CORNER_ALL);


//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone4), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo3), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(2), (ImageView) holder.getView(R.id.recommenditem_imgthree2), GlideImageManager.TAG_FILLET);
//                            GlideImageManager.glideLoader(getActivity(), recommend.get(position).getFeed().getImages().get(3), (ImageView) holder.getView(R.id.recommenditem_imgfour), GlideImageManager.TAG_FILLET);
                            if (recommend.get(position).getFeed().getImages().size() == 4) {
                                holder.setVisible(R.id.recommenditem_imgmore, View.GONE);
                            } else {
                                holder.setVisible(R.id.recommenditem_imgmore, View.VISIBLE);
                                holder.setText(R.id.recommenditem_imgmore, recommend.get(position).getFeed().getImages().size() + "");
                            }
                        }

                        UIUtils.loadRoundImage((ImageView) holder.getView(R.id.recommenditem_headericon1), 50, recommend.get(position).getUser().getAvatar(), RoundedCornersTransformation.CORNER_ALL);

//                        GlideImageManager.glideLoader(getActivity(), recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon1), GlideImageManager.TAG_ROUND);
                        holder.setText(R.id.recommenditem_nickname1, recommend.get(position).getUser().getNickname());

                        if (recommend.get(position).getUser().isFollowed()) {
                            holder.setText(R.id.recommenditem_follow, "已关注");
                            holder.setTextColor(R.id.recommenditem_follow, getResources().getColor(R.color.bg_999999));
                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
                        } else {
                            holder.setText(R.id.recommenditem_follow, "关注");
                            holder.setTextColor(R.id.recommenditem_follow, getResources().getColor(ResourcseMessage.getFontColor()));
                            holder.setBackgroundRes(R.id.recommenditem_follow, ResourcseMessage.getFollowBackground());
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
//                        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
//                        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
//                        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
//                        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
//                        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
                        Date date = DateUtils.parseDate(recommend.get(position).getFeed().getAddtime(), "yyyy-MM-dd HH:mm:ss");
                        if (date == null) {
                            return;
                        }
                        long diff = new Date().getTime() - date.getTime();
                        long r = (diff / (60 * (60 * 1000)));

                        if (r > 12) {
                            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
                            int date1 = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
                            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
                            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));

                            if (month < 10 && date1 < 10) {

                                holder.setText(R.id.recommenditem_time1, "0" + (month + 1) + "-0" + date1);
                            } else if (month < 10) {
                                holder.setText(R.id.recommenditem_time1, "0" + (month + 1) + "-" + date1);
                            } else if (date1 < 10) {
                                holder.setText(R.id.recommenditem_time1, (month + 1) + "-0" + date1);
                            }

//                            holder.setText(R.id., (month + 1) + "-" + date1);
                        } else {
                            String time = DateUtils.getTimeFormatText(date);
                            holder.setText(R.id.recommenditem_time1, time);
                        }


                        if (recommend.get(position).getFeed().isPraised()) {
                            holder.setImageResource(R.id.recommenditem_collect_img, ResourcseMessage.getPraiseRes());
                        } else {
                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nopraise);
                        }
                        holder.setText(R.id.recommenditem_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
                        holder.setText(R.id.recommenditem_comment, String.valueOf(recommend.get(position).getFeed().getReplies()));
                        holder.setText(R.id.recommenditem_browsecount1, String.valueOf(recommend.get(position).getFeed().getWatches()));


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
                                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                                    recommendDataList.getUser().setFollowed(false);
                                                                }
                                                            }
                                                            commonAdapter.notifyDataSetChanged();
//                                                            recommend.get(position).getUser().setFollowed(false);
                                                            ToastUtils.getInstance().showToast("已取消关注");
//                                                            holder.setTextColor(R.id.recommenditem_follow, getResources().getColor(R.color.bg_E73828));
//                                                            holder.setText(R.id.recommenditem_follow, "关注");
//                                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
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
                                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                                    recommendDataList.getUser().setFollowed(true);
                                                                }
                                                            }

                                                            commonAdapter.notifyDataSetChanged();
//                                                            recommend.get(position).getUser().setFollowed(true);
                                                            ToastUtils.getInstance().showToast("已关注");
//                                                            holder.setText(R.id.recommenditem_follow, "已关注");
//                                                            holder.setTextColor(R.id.recommenditem_follow, getResources().getColor(R.color.bg_999999));
//                                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
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
                                                .cancelPraises(recommend.get(position).getFeed().getRelateid(), 0)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(false);
                                                            ToastUtils.getInstance().showToast("已取消点赞");
                                                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nopraise);
                                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() - 1);
                                                            holder.setText(R.id.recommenditem_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
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
                                                .addPraises(recommend.get(position).getFeed().getRelateid(), 0)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            recommend.get(position).getFeed().setPraised(true);
                                                            ToastUtils.getInstance().showToast("已点赞");
                                                            holder.setImageResource(R.id.recommenditem_collect_img, ResourcseMessage.getPraiseRes());
                                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() + 1);
                                                            holder.setText(R.id.recommenditem_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
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
                        holder.setOnClickListener(R.id.recommenditem_headericon1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                    AppApplication.getInstance().setLogin();

                                } else {

                                    Intent intent = new Intent(getActivity(), NewsOtherUserActivity.class);
                                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
                                    startActivity(intent);
                                }
                            }
                        });


                    }


                }


            }

            @Override
            public void onViewRecycled(@NonNull ViewHolder holder) {
                super.onViewRecycled(holder);

                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_img));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgone1));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgone2));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgone3));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgtwo1));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgtwo2));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgtwo3));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgthree1));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgthree2));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_imgfour));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_headericon1));
                clearViewResource(holder, (ImageView) holder.getView(R.id.recommenditem_headericon));

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情
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

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(commonAdapter);

//        recyclerView.setAdapter(commonAdapter);

    }

    private void clearViewResource(ViewHolder holder, ImageView imageView) {
        if (imageView != null) {
            imageView.setImageDrawable(null);
            Glide.with(AppApplication.getInstance()).clear(imageView);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getRecommend(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getConfigBanner();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isRecommendRefresh) {

            ServiceFactory.getNewInstance()
                    .createService(YService.class)
                    .getRecommend(foward, "", recommend.size() - 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommonObserver<GetRecommendJson>() {
                        @Override
                        public void onNext(GetRecommendJson getRecommendJson) {
                            super.onNext(getRecommendJson);

                            if (null != getRecommendJson && getRecommendJson.getErrno() == 0) {

                                recommend.clear();
                                if (getRecommendJson.getData().getList().size() != 0) {
                                    recommend.add(getRecommendJson.getData().getList().get(0));
                                }
                                recommend.addAll(getRecommendJson.getData().getList());
                                commonAdapter.notifyDataSetChanged();
                                isRecommendRefresh = false;

                                UIUtils.clearMemoryCache(getActivity());

                            } else if (null != getRecommendJson && getRecommendJson.getErrno() == 1101) {

                                SharedPreferencesUtils.writeString("token", "");
                                Log.d("RecommendFragment", "token已被清空");
                            } else if (null != getRecommendJson && getRecommendJson.getErrno() != 200) {

                                ToastUtils.getInstance().showToast(TextUtils.isEmpty(getRecommendJson.getErrmsg()) ? "数据加载失败" : getRecommendJson.getErrmsg());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler == null) {
//
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }


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

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide.get(getActivity()).clearMemory();

    }
}
