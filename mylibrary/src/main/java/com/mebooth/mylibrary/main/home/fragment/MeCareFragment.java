package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.MineActivity;
import com.mebooth.mylibrary.main.home.activity.NewsOtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
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

import static com.mebooth.mylibrary.main.home.fragment.ExperienceFragment.isExperienceRefresh;
import static com.mebooth.mylibrary.main.home.fragment.InformationFragment.isInformationRefresh;
import static com.mebooth.mylibrary.main.home.fragment.NowFragment.isNowRefresh;
import static com.mebooth.mylibrary.main.home.fragment.RecommendFragment.isRecommendRefresh;

public class MeCareFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {
    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;
    private TextView meCareTv;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private ArrayList<GetCareJson.CareData.CareUser> users = new ArrayList<>();

    private Conversation.ConversationType conversationType;
    private String offSet = "";

    private MineActivity.refreshData refreshData;

    public MeCareFragment(MineActivity.refreshData refreshData) {
        this.refreshData = refreshData;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.mecare_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
        meCareTv = view.findViewById(R.id.mecare_notfollow);
        mSmart.setRefreshHeader(new MaterialHeader(getActivity()).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);
        mSmart.setOnLoadMoreListener(this);

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
                .getCareList(offSet, 10)
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
            users.clear();
            users.addAll(getCareJson.getData().getUsers());
            if (users.size() == 0) {
                meCareTv.setVisibility(View.VISIBLE);
            } else {
                meCareTv.setVisibility(View.GONE);
            }
//            recyclerView.setAdapter(commonAdapter);
            mHandler.sendEmptyMessageDelayed(tag, 1000);
        } else {
            if (getCareJson.getData().getUsers().size() == 0) {

                mSmart.finishLoadMoreWithNoMoreData();

            } else {
                users.addAll(getCareJson.getData().getUsers());
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

    private void initRecycle() {

        commonAdapter = new CommonAdapter(getActivity(), R.layout.care_item, users) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                GlideImageManager.glideLoader(getActivity(), users.get(position).getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);

                holder.setText(R.id.recommenditem_nickname, users.get(position).getNickname());
                holder.setOnClickListener(R.id.recommenditem_follow, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isRecommendRefresh = true;
                        isNowRefresh = true;
                        isExperienceRefresh = true;
                        isInformationRefresh = true;
                        //取消关注
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelFollow(users.get(position).getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            ToastUtils.getInstance().showToast("已取消关注");
                                            users.remove(position);
                                            if (users.size() == 0) {
                                                meCareTv.setVisibility(View.VISIBLE);
                                            } else {
                                                meCareTv.setVisibility(View.GONE);
                                            }
                                            refreshData.refresh();
                                            commonAdapter.notifyDataSetChanged();
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
                });
            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

                Intent intent = new Intent(getActivity(), NewsOtherUserActivity.class);
                intent.putExtra("uid", users.get(position).getUid());
                intent.putExtra("nickname", users.get(position).getNickname());
                getActivity().startActivity(intent);
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
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getCareList(LOADMORE_LIST);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getCareList(REFLUSH_LIST);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);

    }

}
