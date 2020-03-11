package com.mebooth.mylibrary.main.home.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bigkoo.alertview.AlertView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.CommentExpandAdapter;
import com.mebooth.mylibrary.main.adapter.NewsAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.CommentOnJson;
import com.mebooth.mylibrary.main.home.bean.GetIsCollectJson;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetNewInfoJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.CommentExpandableListView;
import com.mebooth.mylibrary.main.view.GloriousRecyclerView;
import com.mebooth.mylibrary.main.view.SharedActivity;
import com.mebooth.mylibrary.main.view.SpacesItemDecoration;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.DateUtils;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.mebooth.mylibrary.main.home.fragment.ExperienceFragment.isExperienceRefresh;
import static com.mebooth.mylibrary.main.home.fragment.InformationFragment.isInformationRefresh;
import static com.mebooth.mylibrary.main.home.fragment.MeCollectFragment.isMeCollectRefresh;
import static com.mebooth.mylibrary.main.home.fragment.NowFragment.isNowRefresh;
import static com.mebooth.mylibrary.main.home.fragment.RecommendFragment.isRecommendRefresh;

public class NewDetailsActivity extends BaseTransparentActivity implements OnRefreshListener {

    private ImageView newdetailsImage;
    private ImageView newdetailsHeaderIcon;
    private TextView newdetailsTitle;
    private TextView newdetailsTime;
    private TextView newdetailsBrowse;
    private TextView newdetailsNickName;
    private TextView newdetailsFollow;
    //    private RecyclerView recyclerView;
    private GloriousRecyclerView recyclerView;
    private CommentExpandableListView expandableListView;
    private TextView commentEdit;
    private TextView newdetailsComment;
    private ImageView newdetailShare;
    private ImageView newdetailShare1;

    private int id;
    private String nickName;
    private int watchs;

    private NewsAdapter adapter;
    private ArrayList<GetNewInfoJson.NewInfoData.News.Content> content = new ArrayList<>();
    private int replies;
    private int praises;
    private int favorites;
    private int uid;

    private ArrayList<CommentOnJson.CommentData.CommentOnList> commentList = new ArrayList<>();

    private CommentExpandAdapter commentAdapter;

    private BottomSheetDialog dialog;

    private SharedActivity sharedPopup;
    private ImageView back;
    private ImageView back1;
    private TextView title;
    private TextView noComment;
    private FrameLayout newdetailsLly;

    private View header;
    private View footer;
    private String avatar;
    private LinearLayout headerLayout1;
    private LinearLayout headerLayout2;
    private TextView newdetailsCollect;
    private ImageView newdetailsCollectImg;

    private SmartRefreshLayout mSmart;
    private LinearLayout newdetailsTimeLLY;
    private LinearLayout newdetailsNameLLY;
    private LinearLayout newsdetailsCommentLLY;

    private int mDistance = 0;
    private int maxDistance = 255;//当距离在[0,255]变化时，透明度在[0,255之间变化]
    private TextView follow;

    private boolean isClick = true;
    private boolean isClick1 = true;
    private FrameLayout newDetailsFooterFrame;
    private LinearLayout commentLLY;
    private LinearLayout newdetailsHeaderLly;
    private ImageView newdetailsPraiseImg;
    private TextView newdetailsPraise;


    @Override
    protected int getContentViewId() {
        return R.layout.newdetails_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

    }

    @Override
    protected void initView() {
        super.initView();


        mSmart = findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


        header = LayoutInflater.from(NewDetailsActivity.this).inflate(R.layout.newestheader, null);
        footer = LayoutInflater.from(NewDetailsActivity.this).inflate(R.layout.newestfooter, null);

        footer.setVisibility(View.GONE);
        headerLayout1 = findViewById(R.id.newdetails_header);
        commentLLY = findViewById(R.id.newdetails_comment_lly);
        newsdetailsCommentLLY = findViewById(R.id.newsdetails_comment_lly);
        headerLayout2 = findViewById(R.id.newdetails_header1);
        headerLayout2.setFocusable(true);
        headerLayout2.setFocusableInTouchMode(true);
        headerLayout2.requestFocus();
        newdetailsHeaderLly = header.findViewById(R.id.newdetailsheader_lly);
        LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) newdetailsHeaderLly.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams1.width = UIUtils.getScreenWidth(this); // 当控件的高强制设成365象素
        newdetailsHeaderLly.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件aaa

        newdetailsImage = header.findViewById(R.id.newdetails_image);
        newdetailsTitle = header.findViewById(R.id.newdetails_title);
        newdetailsTimeLLY = header.findViewById(R.id.newsHeaderTime_lly);
        newdetailsNameLLY = header.findViewById(R.id.newsHeaderName_lly);
        newdetailsTime = header.findViewById(R.id.newdetails_time);
        newdetailsBrowse = header.findViewById(R.id.newdetails_browsecount);
        newdetailsCollect = findViewById(R.id.newdetails_collect);
        newdetailsPraise = findViewById(R.id.newdetails_praise);
        newdetailsCollectImg = findViewById(R.id.newdetails_collect_img);
        newdetailsPraiseImg = findViewById(R.id.newdetails_praise_img);
        newdetailsHeaderIcon = header.findViewById(R.id.recommenditem_headericon);
        newdetailsNickName = header.findViewById(R.id.recommenditem_nickname);
        follow = header.findViewById(R.id.recommenditem_follow);
//        follow.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.classify_recycle);
        expandableListView = footer.findViewById(R.id.detail_page_lv_comment);
        newDetailsFooterFrame = footer.findViewById(R.id.newsdetailsfooter_frame);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) newDetailsFooterFrame.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams.width = UIUtils.getScreenWidth(this); // 当控件的高强制设成365象素
        newDetailsFooterFrame.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa

        commentEdit = findViewById(R.id.detail_page_do_comment);
        newdetailsComment = findViewById(R.id.newdetails_comment);
        newdetailShare = findViewById(R.id.newdetails_share);
        newdetailShare.setVisibility(View.GONE);
        newdetailShare1 = findViewById(R.id.newdetails_share1);
        back = findViewById(R.id.public_back);
        back.setVisibility(View.GONE);
        back1 = findViewById(R.id.public_back1);
        setSystemBarAlpha(0);
//        title = findViewById(R.id.public_title);
        noComment = footer.findViewById(R.id.nwdetails_nocomment);
        newdetailsLly = findViewById(R.id.newdetails_lly);

        commentLLY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(adapter.getItemCount());
//                expandableListView.setFocusable(true);
//                expandableListView.setFocusableInTouchMode(true);
//                expandableListView.requestFocus();
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);


//        newdetailsLly.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        getWindow().setStatusBarColor(R.color.transparent);
        headerLayout1.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
        headerLayout2.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            try {
//                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
//                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
//                field.setAccessible(true);
//                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
//            } catch (Exception e) {}
//        }
//        title.setText("正文");

        id = getIntent().getIntExtra("relateid", 0);
        uid = getIntent().getIntExtra("uid", 0);
        nickName = getIntent().getStringExtra("nickname");
        avatar = getIntent().getStringExtra("image");
        watchs = getIntent().getIntExtra("browse", 0);
        replies = getIntent().getIntExtra("replies", 0);
        praises = getIntent().getIntExtra("praises", 0);
        favorites = getIntent().getIntExtra("favorites", 0);

        if (nickName == null) {

        } else {
            //头像昵称
            GlideImageManager.glideLoader(NewDetailsActivity.this, avatar, newdetailsHeaderIcon, GlideImageManager.TAG_ROUND);
            newdetailsNickName.setText(nickName);
            newdetailsBrowse.setText("" + watchs);
            newdetailsComment.setText("" + replies);
            newdetailsCollect.setText("" + favorites);
            newdetailsPraise.setText("" + praises);
        }


        sharedPopup = new SharedActivity(NewDetailsActivity.this, id, "news");


        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    showCommentDialog();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, content);
        recyclerView.setAdapter(adapter);
        recyclerView.addHeaderView(header);
        recyclerView.addFooterView(footer);
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//            }
//        });

        recyclerView.setItemViewCacheSize(50);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = dx + dy;

                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置
                    int position = GSYVideoManager.instance().getPlayPosition();
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().getPlayTag().equals(NewsAdapter.TAG)
                            && (position < dx || position > lastVisibleItem)) {
                        if (GSYVideoManager.isFullState(NewDetailsActivity.this)) {
                            return;
                        }
                        //如果滑出去了上面和下面就是否，和今日头条一样
                        GSYVideoManager.releaseAllVideos();
                        adapter.notifyDataSetChanged();
                    }
                }
                //是否为顶部
//                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 :
//                        recyclerView.getChildAt(0).getTop();
//                if (topRowVerticalPosition >= 0) {
//                    //滑动到顶部
//                    headerLayout1.setVisibility(View.GONE);
////                    headerLayout1.setAlpha(0);
//                    headerLayout2.setVisibility(View.VISIBLE);
//                } else {
//                    headerLayout1.setVisibility(View.VISIBLE);
//                    headerLayout2.setVisibility(View.GONE);
//                }

                mDistance += dy;
                float percent = mDistance * 1f / maxDistance;//百分比
                int alpha = (int) (percent * 255);
//            int argb = Color.argb(alpha, 57, 174, 255);
                setSystemBarAlpha(alpha);

            }
        });

        initExpandableListView(commentList);

        mSmart.autoRefresh();
        expandableListView.setNestedScrollingEnabled(true);

        newdetailShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPopup.showPopupWindow();

            }
        });
        newdetailShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPopup.showPopupWindow();

            }
        });

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
                            follow.setVisibility(View.VISIBLE);
                            if (AppApplication.getInstance().userid != null) {
                                Log.d("TAG", AppApplication.getInstance().userid);
                                if (AppApplication.getInstance().userid.equals(String.valueOf(uid))) {
                                    follow.setVisibility(View.GONE);
                                } else {
                                    follow.setVisibility(View.VISIBLE);
                                }
                            }
                            if (getIsFollowJson.getData().getUsers().get(0).isFollowed()) {
                                follow.setText("已关注");
                                follow.setTextColor(getResources().getColor(R.color.bg_999999));
                                follow.setBackgroundResource(R.drawable.nofollow);
                            } else {
                                follow.setText("关注");
                                follow.setTextColor(getResources().getColor(R.color.bg_E73828));
                                follow.setBackgroundResource(R.drawable.follow);
                            }

                            follow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getIsFollowJson.getData().getUsers().get(0).isFollowed()) {
                                        isRecommendRefresh = true;
                                        isNowRefresh = true;
                                        isExperienceRefresh = true;
                                        isInformationRefresh = true;
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
                                                            follow.setText("关注");
                                                            follow.setBackgroundResource(R.drawable.follow);
                                                            follow.setTextColor(getResources().getColor(R.color.bg_E73828));
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
                                                            follow.setText("已关注");
                                                            follow.setBackgroundResource(R.drawable.nofollow);
                                                            follow.setTextColor(getResources().getColor(R.color.bg_999999));
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

    /**
     * 设置标题栏背景透明度
     *
     * @param alpha 透明度
     */
    private void setSystemBarAlpha(int alpha) {
        if (alpha > 255) {
            alpha = 255;
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            headerLayout1.getBackground().mutate().setAlpha(alpha);
        } else {
            //标题栏渐变。a:alpha透明度 r:红 g：绿 b蓝
//        titlebar.setBackgroundColor(Color.rgb(57, 174, 255));//没有透明效果
//        titlebar.setBackgroundColor(Color.argb(alpha, 57, 174, 255));//透明效果是由参数1决定的，透明范围[0,255]
//            headerLayout1.setBackgroundColor(Color.alpha(alpha));
            if (alpha <= 50) {
                back.setVisibility(View.GONE);
                newdetailShare.setVisibility(View.GONE);
            } else {
                back.setVisibility(View.VISIBLE);
                newdetailShare.setVisibility(View.VISIBLE);
            }
            headerLayout1.getBackground().mutate().setAlpha(alpha);
        }
    }

    /**
     * by moos on 2018/04/20
     * func:弹出评论框
     */
    private void showCommentDialog() {
        dialog = new BottomSheetDialog(this, R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout, null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0, 0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if (!TextUtils.isEmpty(commentContent)) {

                    //commentOnWork(commentContent);
                    dialog.dismiss();
                    requestMessage(0, commentContent);

                } else {
                    Toast.makeText(NewDetailsActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 0) {
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                } else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(final ArrayList<CommentOnJson.CommentData.CommentOnList> commentList) {
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复


//        expandableListView.setPullRefreshEnabled(true);
//        expandableListView.setLoadingMoreEnabled(true);

//        expandableListView.setmLoadingListener(new SExpandableListView.LoadingListener() {
//            @Override
//            public void onLoadMore() {
//                isPull = false;
//                getComment(isPull);
////                Log.e("TAG---HANDLER:", loadCount + "-->");
//            }
//
//            @Override
//            public void onRefresh() {
//                isPull = true;
//                offset = 0;
//                getComment(isPull);
////                Log.e("TAG---HANDLER:", loadCount + "-->");
//            }
//        });

        for (int i = 0; i < commentList.size(); i++) {
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    showReplyDialog(groupPosition);
                }
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(NewDetailsActivity.this, "点击了回复", Toast.LENGTH_SHORT).show();
//                showReplyDialog(childPosition);
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    showReplyTwoDialog(groupPosition, childPosition);
                }

                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //toast("展开第"+groupPosition+"个分组");

            }
        });

    }

    /**
     * by moos on 2018/04/20
     * func:弹出回复框
     */
    private void showReplyDialog(final int position) {
        dialog = new BottomSheetDialog(this, R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout, null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setHint("回复@" + commentList.get(position).getUser().getNickname());
        commentText.setSelection(commentText.getText().toString().length());
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if (!TextUtils.isEmpty(replyContent)) {

                    dialog.dismiss();
//                    CommentOnJson.CommentData.CommentOnList.Reply.Replies detailBean = new CommentOnJson.CommentData.CommentOnList.Reply.Replies();
//
//                    adapter.addTheReplyData(detailBean, position);
//                    expandableListView.expandGroup(position);

                    requestMessage(commentList.get(position).getReply().getRid(), commentText.getText().toString());

//                    Toast.makeText(mContext,"回复成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewDetailsActivity.this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 0) {
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                } else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    /**
     * by moos on 2018/04/20
     * func:弹出回复框
     */
    private void showReplyTwoDialog(final int groupPosition, final int childPosition) {
        dialog = new BottomSheetDialog(this, R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout, null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setHint("回复 @" + commentList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname());
        commentText.setSelection(commentText.getText().toString().length());
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if (!TextUtils.isEmpty(replyContent)) {

                    dialog.dismiss();
//                    CommentOnJson.CommentData.CommentOnList.Reply.Replies detailBean = new CommentOnJson.CommentData.CommentOnList.Reply.Replies();
//
//                    adapter.addTheReplyData(detailBean, position);
//                    expandableListView.expandGroup(position);

                    requestMessage(commentList.get(groupPosition).getReply().getRid(), "回复" + commentList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname() + "：" + commentText.getText().toString());
//                    list.get(position).getReply().getRid()
                    Toast.makeText(NewDetailsActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewDetailsActivity.this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 0) {
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                } else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void getCommentList() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getCommentInfo(id, 1, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<CommentOnJson>() {
                    @Override
                    public void onNext(CommentOnJson commentOnJson) {
                        super.onNext(commentOnJson);

                        if (null != commentOnJson && commentOnJson.getErrno() == 0) {

                            commentList.clear();
                            commentList.addAll(commentOnJson.getData().getList());

                            if (commentList.size() == 0) {
                                noComment.setVisibility(View.VISIBLE);
                            } else {
                                noComment.setVisibility(View.GONE);
                            }

                            commentAdapter = new CommentExpandAdapter(NewDetailsActivity.this, commentList);
                            expandableListView.setAdapter(commentAdapter);

                            for (int i = 0; i < commentList.size(); i++) {
                                expandableListView.expandGroup(i);
                            }

                            mSmart.finishRefresh();

                        } else if (null != commentOnJson && commentOnJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != commentOnJson && commentOnJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(commentOnJson.getErrmsg()) ? "数据加载失败" : commentOnJson.getErrmsg());
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

    private void requestMessage(int pid, String content) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .requestComment(id, pid, content, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PublicBean>() {
                    @Override
                    public void onNext(PublicBean publicBean) {
                        super.onNext(publicBean);

                        if (null != publicBean && publicBean.getErrno() == 0) {

//                            adapter.notifyDataSetChanged();
                            ToastUtils.getInstance().showToast("已发送评论");
//                            mSmart.autoRefresh();
                            replies += 1;
                            newdetailsComment.setText("" + replies);
                            getCommentList();
                            initExpandableListView(commentList);


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

    private void getNewDetails() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getNewInfo(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNewInfoJson>() {
                    @Override
                    public void onNext(final GetNewInfoJson getNewInfoJson) {
                        super.onNext(getNewInfoJson);

                        if (null != getNewInfoJson && getNewInfoJson.getErrno() == 0) {

                            uid = getNewInfoJson.getData().getUser().getUid();
                            getIsFollow();

                            newdetailsTitle.setVisibility(View.VISIBLE);
                            newdetailsTimeLLY.setVisibility(View.VISIBLE);
                            newdetailsNameLLY.setVisibility(View.VISIBLE);
                            footer.setVisibility(View.VISIBLE);
                            newsdetailsCommentLLY.setVisibility(View.VISIBLE);
                            newdetailsImage.setVisibility(View.VISIBLE);

                            GlideImageManager.glideLoader(NewDetailsActivity.this, getNewInfoJson.getData().getNews().getCover(), newdetailsImage, GlideImageManager.TAG_RECTANGLE);
                            newdetailsTitle.setText(getNewInfoJson.getData().getNews().getTitle());
//                            int month = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(5, 7)) - 1;
//                            int date = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(8, 10));
//                            int hour = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(11, 13));
//                            int minute = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(14, 16));

                            Date date = DateUtils.parseDate(getNewInfoJson.getData().getNews().getAddtime(), "yyyy-MM-dd HH:mm:ss");
                            if (date == null) {
                                return;
                            }
                            long diff = new Date().getTime() - date.getTime();
                            long r = (diff / (60 * (60 * 1000)));

                            if (r > 12) {
                                int month = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(5, 7)) - 1;
                                int date1 = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(8, 10));
                                int hour = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(11, 13));
                                int minute = Integer.parseInt(getNewInfoJson.getData().getNews().getAddtime().substring(14, 16));

                                if (month < 10 && date1 < 10) {

                                    newdetailsTime.setText("0" + (month + 1) + "-0" + date1);
                                } else if (month < 10) {
                                    newdetailsTime.setText("0" + (month + 1) + "-" + date1);
                                } else if (date1 < 10) {
                                    newdetailsTime.setText((month + 1) + "-0" + date1);
                                }

//                                newdetailsTime.setText((month + 1) + "-" + date1);
                            } else {
                                String time = DateUtils.getTimeFormatText(date);
                                newdetailsTime.setText(time);
                            }

//                            Date date = DateUtils.parseDate(, "yyyy-MM-dd HH:mm:ss");
//                            String time = DateUtils.getTimeFormatText(date);
//                            .setText(time);

//                            newdetailsTime.setText(getNewInfoJson.getData().getNews().getAddtime());
                            adapter.addData(getNewInfoJson.getData().getNews().getContent());
                            headerLayout2.setFocusable(true);
                            //头像昵称
                            GlideImageManager.glideLoader(NewDetailsActivity.this, getNewInfoJson.getData().getUser().getAvatar(), newdetailsHeaderIcon, GlideImageManager.TAG_ROUND);
                            newdetailsNickName.setText(getNewInfoJson.getData().getUser().getNickname());
                            newdetailsBrowse.setText(getNewInfoJson.getData().getNews().getWatches() + "人浏览");
                            newdetailsComment.setText("" + getNewInfoJson.getData().getNews().getReplies());

                            praises = getNewInfoJson.getData().getNews().getPraises();
                            favorites = getNewInfoJson.getData().getNews().getFavorites();
                            if (getNewInfoJson.getData().getNews().getReplies() != 0) {
                                getCommentList();
                            }


                            if (getNewInfoJson.getData().getNews().isPraised()) {

                                newdetailsPraiseImg.setImageResource(R.drawable.praise);
                                newdetailsPraise.setText(String.valueOf(getNewInfoJson.getData().getNews().getPraises()));
                            } else {
                                newdetailsPraiseImg.setImageResource(R.drawable.nopraise);
                                newdetailsPraise.setText(String.valueOf(getNewInfoJson.getData().getNews().getPraises()));
                            }
                            if (getNewInfoJson.getData().getNews().isFavorited()) {

                                newdetailsCollectImg.setImageResource(R.drawable.collect);
                                newdetailsCollect.setText(String.valueOf(getNewInfoJson.getData().getNews().getFavorites()));
                            } else {
                                newdetailsCollectImg.setImageResource(R.drawable.nocollect);
                                newdetailsCollect.setText(String.valueOf(getNewInfoJson.getData().getNews().getFavorites()));
                            }

                            //收藏
                            newdetailsCollectImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                        AppApplication.getInstance().setLogin();

                                    } else {
                                        if (isClick1) {
                                            isClick1 = false;
                                            if (getNewInfoJson.getData().getNews().isFavorited()) {
                                                //取消收藏
                                                ServiceFactory.getNewInstance()
                                                        .createService(YService.class)
                                                        .cancelFavorite(getNewInfoJson.getData().getNews().getNewsid())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CommonObserver<PublicBean>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                                            @Override
                                                            public void onNext(PublicBean publicBean) {
                                                                super.onNext(publicBean);

                                                                if (null != publicBean && publicBean.getErrno() == 0) {
                                                                    isMeCollectRefresh = true;
                                                                    isClick1 = true;
                                                                    getNewInfoJson.getData().getNews().setFavorited(false);
                                                                    ToastUtils.getInstance().showToast("已取消收藏");
                                                                    newdetailsCollectImg.setImageResource(R.drawable.nocollect);
                                                                    favorites = favorites - 1;
                                                                    newdetailsCollect.setText(String.valueOf(favorites));
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
                                                        .addFavorite(getNewInfoJson.getData().getNews().getNewsid())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CommonObserver<PublicBean>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                                            @Override
                                                            public void onNext(PublicBean publicBean) {
                                                                super.onNext(publicBean);

                                                                if (null != publicBean && publicBean.getErrno() == 0) {
                                                                    isMeCollectRefresh = true;
                                                                    isClick1 = true;
                                                                    getNewInfoJson.getData().getNews().setFavorited(true);
                                                                    ToastUtils.getInstance().showToast("已收藏");
                                                                    newdetailsCollectImg.setImageResource(R.drawable.collect);
                                                                    favorites = favorites + 1;
                                                                    newdetailsCollect.setText(String.valueOf(favorites));
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
                                }
                            });


                            //点赞
                            newdetailsPraiseImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                                        AppApplication.getInstance().setLogin();

                                    } else {
                                        if (isClick) {
                                            isClick = false;
                                            if (getNewInfoJson.getData().getNews().isPraised()) {
                                                //取消点赞
                                                ServiceFactory.getNewInstance()
                                                        .createService(YService.class)
                                                        .cancelPraises(getNewInfoJson.getData().getNews().getNewsid(), 1)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CommonObserver<PublicBean>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                                            @Override
                                                            public void onNext(PublicBean publicBean) {
                                                                super.onNext(publicBean);

                                                                if (null != publicBean && publicBean.getErrno() == 0) {
                                                                    isClick = true;
                                                                    getNewInfoJson.getData().getNews().setPraised(false);
                                                                    ToastUtils.getInstance().showToast("已取消点赞");
                                                                    newdetailsPraiseImg.setImageResource(R.drawable.nopraise);
                                                                    praises = praises - 1;
                                                                    newdetailsPraise.setText(String.valueOf(praises));

                                                                    Intent intent = new Intent("dataRefresh");
                                                                    intent.putExtra("index", "cancel");
                                                                    intent.putExtra("type", 2);
                                                                    intent.putExtra("id", getNewInfoJson.getData().getNews().getNewsid());
                                                                    intent.putExtra("isPraise", false);
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

                                                //添加点赞
                                                ServiceFactory.getNewInstance()
                                                        .createService(YService.class)
                                                        .addPraises(getNewInfoJson.getData().getNews().getNewsid(), 1)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CommonObserver<PublicBean>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                                            @Override
                                                            public void onNext(PublicBean publicBean) {
                                                                super.onNext(publicBean);

                                                                if (null != publicBean && publicBean.getErrno() == 0) {
                                                                    isClick = true;
                                                                    getNewInfoJson.getData().getNews().setPraised(true);
                                                                    ToastUtils.getInstance().showToast("已点赞");
                                                                    newdetailsPraiseImg.setImageResource(R.drawable.praise);
                                                                    praises = praises + 1;
                                                                    newdetailsPraise.setText(String.valueOf(praises));

                                                                    Intent intent = new Intent("dataRefresh");
                                                                    intent.putExtra("index", "add");
                                                                    intent.putExtra("type", 2);
                                                                    intent.putExtra("id", getNewInfoJson.getData().getNews().getNewsid());
                                                                    intent.putExtra("isPraise", true);
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
                                    }

                                }
                            });


                            UIUtils.clearMemoryCache(NewDetailsActivity.this);
                            mSmart.finishRefresh();
                        } else if (null != getNewInfoJson && getNewInfoJson.getErrno() == 9003) {

                            new AlertView("温馨提示", "您访问的内容不存在或已被删除", null, new String[]{"确定"}, null, NewDetailsActivity.this,
                                    AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position == 0) {

                                        finish();
                                    }
                                }
                            }).show();


                        } else if (null != getNewInfoJson && getNewInfoJson.getErrno() == 1101) {
                            cancelRefresh();
                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getNewInfoJson && getNewInfoJson.getErrno() != 200) {
                            cancelRefresh();
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNewInfoJson.getErrmsg()) ? "数据加载失败" : getNewInfoJson.getErrmsg());
                        } else {
                            cancelRefresh();
                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        cancelRefresh();
                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });
    }

    private void cancelRefresh() {

        if (mSmart != null) {
            mSmart.finishRefresh();
        }

    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        UIUtils.releaseImageViewResource(newdetailsImage);
        dialog = null;
        sharedPopup = null;
        UIUtils.clearMemoryCache(NewDetailsActivity.this);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (replies != 0) {
            getCommentList();
        }
        getNewDetails();
    }
}
