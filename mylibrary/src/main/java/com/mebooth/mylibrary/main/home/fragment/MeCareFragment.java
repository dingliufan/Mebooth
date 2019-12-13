package com.mebooth.mylibrary.main.home.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.R2;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class MeCareFragment extends BaseFragment implements OnLoadMoreListener, OnRefreshListener {
    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private ArrayList<GetCareJson.CareData.CareUser> users = new ArrayList<>();

    private Conversation.ConversationType conversationType;


    @Override
    protected int getLayoutResId() {
        return R.layout.mecare_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.classify_recycle);
        mSmart = view.findViewById(R.id.classify_smart);
    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initRecycle();
        getCareList(REFLUSH_LIST);
    }

    private void getCareList(final int tag) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getCareList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetCareJson>() {
                    @Override
                    public void onNext(GetCareJson getCareJson) {
                        super.onNext(getCareJson);

                        if (null != getCareJson && getCareJson.getErrno() == 0) {

                            initList(tag, getCareJson);

                        } else if (null != getCareJson && getCareJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getCareJson.getErrmsg()) ? "数据加载失败" : getCareJson.getErrmsg());
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

    private void initList(int tag, GetCareJson getCareJson) {

        if (tag == REFLUSH_LIST) {
            users.clear();
            users.addAll(getCareJson.getData().getUsers());
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
                    mSmart.finishLoadMore();
                }

            }
        }
    };

    private void initRecycle() {

        commonAdapter = new CommonAdapter(getActivity(), R.layout.care_item, users) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {

                GlideImageManager.glideLoader(getActivity(), users.get(position).getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);

                holder.setText(R.id.recommenditem_nickname,users.get(position).getNickname());

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

//                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
//                intent.putExtra("orders", orders.get(position));
//                getActivity().startActivity(intent);
                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

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

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

        getCareList(REFLUSH_LIST);

    }

}
