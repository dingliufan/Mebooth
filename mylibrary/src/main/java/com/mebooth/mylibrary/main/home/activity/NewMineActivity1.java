package com.mebooth.mylibrary.main.home.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.alertview.AlertView;
import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewFour;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewThree;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewTwo;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetMineCountJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UserNewsListJson;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.main.utils.ResourcseMessage;
import com.mebooth.mylibrary.main.utils.YService;
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

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class NewMineActivity1 extends BaseTransparentActivity implements OnRefreshListener {

    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter1;
    private MultiItemTypeAdapter commonAdapter2;
//    private TextView headerTitle;
//    private TextView headerCount;

    //    private RecyclerView recyclerView;
    private SmartRefreshLayout mSmart;

    private final int REFLUSH_LIST = 0;
    private final int LOADMORE_LIST = 1;

    private int pageSize = 3;
    private String offSet = "";
    //    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();
    private ArrayList<String> userPublishList = new ArrayList<>();
    private ArrayList<UserNewsListJson.UserNewsListData.UserNewsList> userNewsList = new ArrayList<>();
    private ArrayList<GetNowJson.NowData.NowDataList> userTopicList = new ArrayList<>();
    private int uid;
    private GetMineCountJson newUserInfo;
    private TextView editInfo;
    private String index;
    private String type = "";
    private int id;
    private boolean isFllow;
    private GetIsFollowJson getIsFollowJson1;
    private LinearLayout headerlly;
    private ImageView back;

    private int mDistance = 0;
    private int maxDistance = 255;//当距离在[0,255]变化时，透明度在[0,255之间变化]
    private ImageView chat;


    @Override
    protected int getContentViewId() {
        return R.layout.newmine_layout;
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
        editInfo = findViewById(R.id.newmine_editinfo);
        headerlly = findViewById(R.id.newmine_header);
        back = findViewById(R.id.newmine_header_back);
        chat = findViewById(R.id.newmine_header_chat);
        setSystemBarAlpha(0);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色

        headerlly.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mDistance += dy;
                float percent = mDistance * 1f / maxDistance;//百分比
                int alpha = (int) (percent * 255);
//            int argb = Color.argb(alpha, 57, 174, 255);
                setSystemBarAlpha(alpha);

            }
        });

    }

    /**
     * 设置标题栏背景透明度
     *
     * @param alpha 透明度
     */
    private void setSystemBarAlpha(int alpha) {
        if (alpha > 255) {
            alpha = 255;
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            headerlly.getBackground().mutate().setAlpha(alpha);
        } else {
            //标题栏渐变。a:alpha透明度 r:红 g：绿 b蓝
//        titlebar.setBackgroundColor(Color.rgb(57, 174, 255));//没有透明效果
//        titlebar.setBackgroundColor(Color.argb(alpha, 57, 174, 255));//透明效果是由参数1决定的，透明范围[0,255]
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            if (alpha <= 50) {
                back.setImageResource(R.drawable.icon_back);
                chat.setImageResource(R.drawable.chaticon_white);
            } else {
                back.setImageResource(R.drawable.back);
                chat.setImageResource(R.drawable.chaticon);
            }
            headerlly.getBackground().mutate().setAlpha(alpha);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        uid = getIntent().getIntExtra("uid", 0);
        index = getIntent().getStringExtra("index");

        initRecycle();
        mSmart.autoRefresh();

        //注册广播
        IntentFilter filter = new IntentFilter("dataRefresh");
        registerReceiver(broadcastReceiver, filter);

        editInfo.setBackgroundColor(getResources().getColor(ResourcseMessage.getFontColor()));

        if (!index.equals("mine")) {
            getIsFollow();
            editInfo.setText("关注");
            type = "others";
            chat.setVisibility(View.VISIBLE);
        } else {
            chat.setVisibility(View.GONE);
            type = "minepublic";
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            index = intent.getStringExtra("index");
            id = intent.getIntExtra("id", 0);
            isFllow = intent.getBooleanExtra("isFollow", false);
            if (index.equals("follow")) {
                if (uid == id) {
                    getIsFollowJson1.getData().getUsers().get(0).setFollowed(isFllow);
                    if (getIsFollowJson1.getData().getUsers().get(0).isFollowed()) {
                        editInfo.setText("已关注");
                        editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                        editInfo.setBackgroundColor(getResources().getColor(R.color.bg_909090));
                    } else {
                        editInfo.setText("关注");
                        editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                        editInfo.setBackgroundColor(getResources().getColor(ResourcseMessage.getFontColor()));
                    }

                    for (int i = 0; i < userTopicList.size(); i++) {

                        if (userTopicList.get(i).getUser().getUid() == id) {
                            userTopicList.get(i).getUser().setFollowed(isFllow);
                            if(commonAdapter2!=null){
                                commonAdapter2.notifyDataSetChanged();
                            }
                        }
                    }

                }
            }
        }
    };

    private void getIsFollow() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getIsFollow(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetIsFollowJson>() {
                    @Override
                    public void onNext(final GetIsFollowJson getIsFollowJson) {
                        super.onNext(getIsFollowJson);

                        if (null != getIsFollowJson && getIsFollowJson.getErrno() == 0) {

                            getIsFollowJson1 = getIsFollowJson;

                            if (getIsFollowJson.getData().getUsers().get(0).isFollowed()) {
                                editInfo.setText("已关注");
                                editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                                editInfo.setBackgroundColor(getResources().getColor(R.color.bg_909090));
                            } else {
                                editInfo.setText("关注");
                                editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                                editInfo.setBackgroundColor(getResources().getColor(ResourcseMessage.getFontColor()));
                            }

                            editInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getIsFollowJson1.getData().getUsers().get(0).isFollowed()) {
                                        //取消关注
                                        ServiceFactory.getNewInstance()
                                                .createService(YService.class)
                                                .cancelFollow(uid)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            getIsFollowJson.getData().getUsers().get(0).setFollowed(false);
                                                            ToastUtils.getInstance().showToast("已取消关注");
                                                            editInfo.setText("关注");
                                                            editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                                                            editInfo.setBackgroundColor(getResources().getColor(ResourcseMessage.getFontColor()));

                                                            Intent intent = new Intent("dataRefresh");
                                                            intent.putExtra("index", "follow");
                                                            intent.putExtra("id", uid);
                                                            intent.putExtra("isFollow", false);
                                                            sendBroadcast(intent);

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
                                                .addFollow(uid)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new CommonObserver<PublicBean>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onNext(PublicBean publicBean) {
                                                        super.onNext(publicBean);

                                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                                            getIsFollowJson.getData().getUsers().get(0).setFollowed(true);
                                                            ToastUtils.getInstance().showToast("已关注");
                                                            editInfo.setText("已关注");
                                                            editInfo.setTextColor(getResources().getColor(R.color.bg_ffffff));
                                                            editInfo.setBackgroundColor(getResources().getColor(R.color.bg_909090));

                                                            Intent intent = new Intent("dataRefresh");
                                                            intent.putExtra("index", "follow");
                                                            intent.putExtra("id", uid);
                                                            intent.putExtra("isFollow", true);
                                                            sendBroadcast(intent);

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
                            });

                        } else if (null != getIsFollowJson && getIsFollowJson.getErrno() == 1101) {
                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getIsFollowJson && getIsFollowJson.getErrno() != 200) {
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getIsFollowJson.getErrmsg()) ? "数据加载失败" : getIsFollowJson.getErrmsg());
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

    private ImageView getView(String url) {
        ImageView imgView = new ImageView(this);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        GlideImageManager.glideLoader(NewMineActivity1.this, url, imgView, GlideImageManager.TAG_ROUND);

        return imgView;
    }


    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.userpublish_item, userPublishList) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position != 0) {
                    holder.setText(R.id.userpublish_title, userPublishList.get(position));
                }

                holder.setOnClickListener(R.id.userpublish_lly, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 1) {
                            Intent intent = new Intent(NewMineActivity1.this, MePublishNewsActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("index", type);
                            startActivity(intent);

                        } else if (position == 2) {
                            Intent intent = new Intent(NewMineActivity1.this, MePublishTopicActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("index", type);
                            startActivity(intent);
                        }

                    }
                });
                holder.setBackgroundRes(R.id.newminebg_iv, ResourcseMessage.getMineBg());

                if (position == 0) {

                    FrameLayout linearLayout = holder.getView(R.id.newmine_header);
                    linearLayout.setFocusable(true);
                    linearLayout.setFocusableInTouchMode(true);
                    linearLayout.requestFocus();
                    linearLayout.setVisibility(View.VISIBLE);
                    holder.setVisible(R.id.newmine_header_recycle, View.GONE);

                    if (newUserInfo.getData().getUser().getEmployee().equals("Y")) {
                        holder.setVisible(R.id.staff_tab, View.VISIBLE);
                    } else {
                        holder.setVisible(R.id.staff_tab, View.GONE);
                    }

//                    UIUtils.loadRoundImage((ImageView) holder.getView(R.id.personal_headericon), 50, newUserInfo.getData().getUser().getAvatar(), RoundedCornersTransformation.CORNER_ALL);

                    GlideImageManager.glideLoader(NewMineActivity1.this, newUserInfo.getData().getUser().getAvatar(), (ImageView) holder.getView(R.id.personal_headericon), GlideImageManager.TAG_ROUND);

                    holder.setOnClickListener(R.id.personal_headericon, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // 全屏显示的方法
                            final Dialog dialog = new Dialog(NewMineActivity1.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            ImageView imgView = getView(newUserInfo.getData().getUser().getAvatar());
                            dialog.setContentView(imgView);
                            dialog.show();

                            // 点击图片消失
                            imgView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
// TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });

                        }
                    });

                    if (newUserInfo.getData().getUser().getCity().isEmpty()) {
                        holder.setVisible(R.id.personal_location, View.INVISIBLE);
                    } else {
                        holder.setVisible(R.id.personal_location, View.VISIBLE);
                        holder.setText(R.id.personal_location, newUserInfo.getData().getUser().getCity());
                    }

                    if (newUserInfo.getData().getUser().getSignature().isEmpty()) {
                        if (index.equals("mine")) {
                            holder.setText(R.id.personal_autograph, "暂无个人简介");
                        } else {
                            holder.setText(R.id.personal_autograph, "该作者很懒，什么也没留下");
                        }

                    } else {
                        holder.setText(R.id.personal_autograph, newUserInfo.getData().getUser().getSignature());
                    }

                    if (newUserInfo.getData().getUser().getGender().equals("男")) {
                        Drawable drawableRight = getResources().getDrawable(
                                R.drawable.mine_sexman);

                        TextView tvNickNameSex = holder.getView(R.id.personal_nickname);
                        tvNickNameSex.setCompoundDrawablesWithIntrinsicBounds(null,
                                null, drawableRight, null);

                        tvNickNameSex.setCompoundDrawablePadding(10);

                        tvNickNameSex.setText(newUserInfo.getData().getUser().getNickname());
                    } else if (newUserInfo.getData().getUser().getGender().equals("女")) {
                        Drawable drawableRight = getResources().getDrawable(
                                R.drawable.mine_sexwoman);

                        TextView tvNickNameSex = holder.getView(R.id.personal_nickname);
                        tvNickNameSex.setCompoundDrawablesWithIntrinsicBounds(null,
                                null, drawableRight, null);

                        tvNickNameSex.setCompoundDrawablePadding(10);
                        tvNickNameSex.setText(newUserInfo.getData().getUser().getNickname());
                    } else {
                        holder.setText(R.id.personal_nickname, newUserInfo.getData().getUser().getNickname());
                    }

                    if (index.equals("mine")) {

                        holder.setTextColor(R.id.personal_collect_tv, getResources().getColor(ResourcseMessage.getFontColor()));
                        holder.setTextColor(R.id.personal_follow_tv, getResources().getColor(ResourcseMessage.getFontColor()));
                        holder.setTextColor(R.id.personal_fans_tv, getResources().getColor(ResourcseMessage.getFontColor()));

                    } else {
                        holder.setTextColor(R.id.personal_collect_tv, getResources().getColor(R.color.bg_666666));
                        holder.setTextColor(R.id.personal_follow_tv, getResources().getColor(R.color.bg_666666));
                        holder.setTextColor(R.id.personal_fans_tv, getResources().getColor(R.color.bg_666666));


                    }
                    holder.setText(R.id.personal_bepraised_tv, newUserInfo.getData().getStats().getPraise() + "");
                    holder.setText(R.id.personal_collect_tv, newUserInfo.getData().getStats().getFavorite() + "");
                    holder.setText(R.id.personal_follow_tv, newUserInfo.getData().getStats().getFollowing() + "");
                    holder.setText(R.id.personal_fans_tv, newUserInfo.getData().getStats().getFollower() + "");


                    holder.setOnClickListener(R.id.personal_collect_lly, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (index.equals("mine")) {

                                Intent intent = new Intent(NewMineActivity1.this, NewCollectActivity.class);
                                startActivity(intent);

                            }

                        }
                    });
                    holder.setOnClickListener(R.id.personal_follow_lly, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (index.equals("mine")) {

                                Intent intent = new Intent(NewMineActivity1.this, NewMineFollowActivity.class);
                                startActivity(intent);

                            }

                        }
                    });
                    holder.setOnClickListener(R.id.personal_fans_lly, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (index.equals("mine")) {

                                Intent intent = new Intent(NewMineActivity1.this, NewMineFansActivity.class);
                                startActivity(intent);

                            }

                        }
                    });


                } else if (position == 1) {
//                    if(userNewsList.size() == 0){
//                        holder.setVisible(R.id.newmine_header, View.GONE);
//                        holder.setVisible(R.id.newmine_header_recycle, View.GONE);
//                    }else{

                    holder.setVisible(R.id.newmine_header, View.GONE);
                    if (userNewsList.size() == 0) {

                        holder.setVisible(R.id.newmine_header_recycle, View.GONE);

                    } else {
                        holder.setVisible(R.id.newmine_header_recycle, View.VISIBLE);
                    }
//                    }
                    commonAdapter1 = new CommonAdapter(NewMineActivity1.this, R.layout.usernews_item, userNewsList) {
                        @Override
                        protected void convert(ViewHolder holder, Object o, final int position) {

                            UIUtils.loadRoundImage((ImageView) holder.getView(R.id.usernews_img), 0, userNewsList.get(position).getCover(), RoundedCornersTransformation.CORNER_ALL);

                            if (type.equals("others")) {

                                holder.setVisible(R.id.usernews_isreview, View.GONE);
                                holder.setVisible(R.id.usernews_delete, View.GONE);

                            } else {
                                holder.setVisible(R.id.usernews_isreview, View.VISIBLE);
                                holder.setVisible(R.id.usernews_delete, View.VISIBLE);
                            }

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

                                    new AlertView("温馨提示", "您确定要删除？", "取消", new String[]{"确定"}, null, NewMineActivity1.this,
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

                            Intent intent = new Intent(NewMineActivity1.this, NewDetailsActivity.class);
                            intent.putExtra("uid", userNewsList.get(position).getUid());
                            intent.putExtra("relateid", userNewsList.get(position).getNewsid());
                            startActivity(intent);
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

                        }

                        @Override
                        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                            return false;
                        }
                    });
                    RecyclerView recyclerView1 = holder.getView(R.id.userpublish_recycleview);
                    recyclerView1.setLayoutManager(new LinearLayoutManager(NewMineActivity1.this));
                    recyclerView1.setAdapter(commonAdapter1);

                } else if (position == 2) {

//                    if(userTopicList.size() == 0){
//                        holder.setVisible(R.id.newmine_header, View.GONE);
//                        holder.setVisible(R.id.newmine_header_recycle, View.GONE);
//                        holder.setVisible(R.id.bgf6f6f6, View.GONE);
//                    }else{

                    holder.setVisible(R.id.newmine_header, View.GONE);
                    holder.setVisible(R.id.bgf6f6f6, View.GONE);
                    holder.setVisible(R.id.newmine_header_recycle, View.VISIBLE);
//                    }

                    if (userTopicList.size() == 0) {

                        holder.setVisible(R.id.newmine_header_recycle, View.GONE);

                    }else{
                        holder.setVisible(R.id.newmine_header_recycle, View.VISIBLE);
                    }

                    if(userNewsList.size() == 0&&userTopicList.size() == 0){
                        holder.setVisible(R.id.userpublish_item_nocontent,View.VISIBLE);
                        holder.setText(R.id.userpublish_item_nocontent,"TA还没有发布过此刻或笔记");
                    }

//                    holder.setVisible(R.id.newmine_header, View.GONE);
//                    holder.setVisible(R.id.bgf6f6f6, View.GONE);
//                    holder.setVisible(R.id.userpublish_lly, View.VISIBLE);
//                    holder.setVisible(R.id.userpublish_recycleview, View.VISIBLE);
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
//                            refreshData.refresh();
                        }
                    };

                    commonAdapter2 = new MultiItemTypeAdapter(NewMineActivity1.this, userTopicList);
                    commonAdapter2.addItemViewDelegate(new NowItemVIewZero(NewMineActivity1.this, type, commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewOne(NewMineActivity1.this, type, commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewTwo(NewMineActivity1.this, type, commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewThree(NewMineActivity1.this, type, commonAdapter2, userTopicList, noPublishinterface));
                    commonAdapter2.addItemViewDelegate(new NowItemVIewFour(NewMineActivity1.this, type, commonAdapter2, userTopicList, noPublishinterface));

                    commonAdapter2.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                            //TODO 详情
                            Intent intent = new Intent(NewMineActivity1.this, NowDetailsActivity.class);
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
                    recyclerView1.setLayoutManager(new LinearLayoutManager(NewMineActivity1.this));
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
        recyclerView.setLayoutManager(new LinearLayoutManager(NewMineActivity1.this));
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
//                            refreshData.refresh();
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
//                            if (userNewsList.size() != 0) {
//                                userNewsList.add(userNewsListJson.getData().getList().get(0));
//                            }
                            userNewsList.addAll(userNewsListJson.getData().getList());
//                            commonAdapter1.notifyDataSetChanged();
//                            mSmart.finishRefresh();
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
//                            commonAdapter2.notifyDataSetChanged();
                            mSmart.finishRefresh();
                            commonAdapter.notifyDataSetChanged();
                            if (userNewsList.size() == 0 && userTopicList.size() == 0) {

//                                recyclerView.setVisibility(View.GONE);
//                                notPublish.setVisibility(View.VISIBLE);

                            } else {
//                                recyclerView.setVisibility(View.VISIBLE);
//                                notPublish.setVisibility(View.GONE);
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

    private void getUserInfo() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getMineCountInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMineCountJson>() {
                    @Override
                    public void onNext(GetMineCountJson userInfoJson) {
                        super.onNext(userInfoJson);

                        if (null != userInfoJson && userInfoJson.getErrno() == 0) {

                            newUserInfo = userInfoJson;
                            userPublishList.clear();
                            if (index.equals("mine")) {
                                userPublishList.add("我发布的笔记");
                                userPublishList.add("我发布的笔记");
                                userPublishList.add("我发布的此刻");
                            } else {
                                userPublishList.add("TA发布的笔记");
                                userPublishList.add("TA发布的笔记");
                                userPublishList.add("TA发布的此刻");
                            }

                            getNews();

                            chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {

                                        connect();
                                    } else {

                                        RongIM.getInstance().startPrivateChat(NewMineActivity1.this, String.valueOf(uid), newUserInfo.getData().getUser().getNickname());

                                    }
                                }
                            });

                        } else if (null != userInfoJson && userInfoJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            cancelRefresh(0);
                        } else if (null != userInfoJson && userInfoJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(userInfoJson.getErrmsg()) ? "数据加载失败" : userInfoJson.getErrmsg());
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

    private void connect() {
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

                RongIM.getInstance().startPrivateChat(NewMineActivity1.this, String.valueOf(uid), newUserInfo.getData().getUser().getNickname());


            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("TAG", "--onSuccess" + errorCode);
                ToastUtils.getInstance().showToast("连接融云失败");
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
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!index.equals("mine")) {

                } else {

                    Intent intent = new Intent(NewMineActivity1.this, EditUserInfoActivity.class);
                    intent.putExtra("headericon", newUserInfo.getData().getUser().getAvatar());
                    intent.putExtra("nickname", newUserInfo.getData().getUser().getNickname());
                    intent.putExtra("autograph", newUserInfo.getData().getUser().getSignature());
                    intent.putExtra("sex", newUserInfo.getData().getUser().getGender());
                    intent.putExtra("city", newUserInfo.getData().getUser().getCity());
                    startActivityForResult(intent, 1);

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2) {

            getUserInfo();

        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        offSet = "";
        getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);

    }
}

