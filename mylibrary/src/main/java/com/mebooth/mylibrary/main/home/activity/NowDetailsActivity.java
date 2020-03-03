package com.mebooth.mylibrary.main.home.activity;

import android.graphics.Color;

import com.bigkoo.alertview.AlertView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
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
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.CommentExpandAdapter;
import com.mebooth.mylibrary.main.adapter.NewsAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.CommentOnJson;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetNowDetailsJson;
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

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.mebooth.mylibrary.main.home.fragment.ExperienceFragment.isExperienceRefresh;
import static com.mebooth.mylibrary.main.home.fragment.InformationFragment.isInformationRefresh;
import static com.mebooth.mylibrary.main.home.fragment.NowFragment.isNowRefresh;
import static com.mebooth.mylibrary.main.home.fragment.RecommendFragment.isRecommendRefresh;

public class NowDetailsActivity extends BaseTransparentActivity implements OnRefreshListener {

    private ImageView headerIcon;
    private TextView nickName;
    private TextView follow;
    private TextView content;
    private GloriousRecyclerView recyclerView;
    private CommentExpandableListView expandableListView;
    private TextView commentEdit;
    private ImageView collectimg;
    private ImageView share;
    private TextView sendComment;
    private int tid;

    private CommonAdapter commonAdapter;

    private ArrayList<String> list = new ArrayList<>();
    private int uid;

    private NewsAdapter adapter;

    private ArrayList<CommentOnJson.CommentData.CommentOnList> commentList = new ArrayList<>();

    private CommentExpandAdapter commentAdapter;

    private BottomSheetDialog dialog;

    private SharedActivity sharedPopup;
    private ImageView back;
    private TextView title;
    private TextView noCmment;
    private View header;
    private View footer;

    private SmartRefreshLayout mSmart;
    private TextView collectCount;
    private TextView commentCount;
    private LinearLayout nowdetailsCommentLLY;
    private TextView time;
    private boolean isClick = true;
    private FrameLayout nowDetailsFooterFrame;
    private LinearLayout nowDetailsHeaderrFrame;

    @Override
    protected int getContentViewId() {
        return R.layout.nowdetails_layout;
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

        mSmart = findViewById(R.id.classify_smart);
        mSmart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(false)
                .setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color))); //设置刷新为官方推介
        mSmart.setEnableHeaderTranslationContent(false);//刷新时和官方一致   内容不随刷新动
        mSmart.setPrimaryColorsId(R.color.main_color, R.color.main_color, R.color.main_color); //圈圈颜色


        header = LayoutInflater.from(NowDetailsActivity.this).inflate(R.layout.nowestheader, null);
        footer = LayoutInflater.from(NowDetailsActivity.this).inflate(R.layout.nowestfooter, null);

        nowDetailsFooterFrame = footer.findViewById(R.id.nowdetails_footer_frame);
        nowDetailsHeaderrFrame = header.findViewById(R.id.nowdetails_framheader);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) nowDetailsFooterFrame.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams.width = UIUtils.getScreenWidth(this); //
        nowDetailsFooterFrame.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa

        footer.setVisibility(View.GONE);
        headerIcon = header.findViewById(R.id.recommenditem_headericon);
        nickName = header.findViewById(R.id.recommenditem_nickname);
        time = header.findViewById(R.id.recommenditem_time);
        follow = header.findViewById(R.id.recommenditem_follow);
        follow.setVisibility(View.GONE);
        content = header.findViewById(R.id.nowdetails_content);

        LinearLayout.LayoutParams linearParamsHeader = (LinearLayout.LayoutParams) nowDetailsHeaderrFrame.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParamsHeader.width = UIUtils.getScreenWidth(this); //
        nowDetailsHeaderrFrame.setLayoutParams(linearParamsHeader); // 使设置好的布局参数应用到控件aaa

        recyclerView = findViewById(R.id.classify_recycle);
        expandableListView = footer.findViewById(R.id.detail_page_lv_comment);
        commentEdit = findViewById(R.id.detail_page_do_comment);
        collectimg = findViewById(R.id.newdetails_collectimg);
        collectCount = findViewById(R.id.newdetails_collect);
        commentCount = findViewById(R.id.newdetails_comment);
        share = findViewById(R.id.newdetails_share);
        sendComment = findViewById(R.id.newdetails_sendcomment);
        back = findViewById(R.id.public_back);
        nowdetailsCommentLLY = findViewById(R.id.nowdetails_comment_lly);
        title = findViewById(R.id.public_title);
        noCmment = footer.findViewById(R.id.nowdetails_nocomment);

        findViewById(R.id.nowdetails_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
//        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

    }

    @Override
    protected void initListener() {
        super.initListener();

        mSmart.setOnRefreshListener(this);

    }

    @Override
    protected void initData() {
        super.initData();


        title.setText("此刻");

        tid = getIntent().getIntExtra("relateid", 0);
        uid = getIntent().getIntExtra("uid", 0);

        sharedPopup = new SharedActivity(NowDetailsActivity.this, tid, "topic");


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

        initExpandableListView(commentList);

        initRecycle();
        mSmart.autoRefresh();
        expandableListView.setNestedScrollingEnabled(true);

        share.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(NowDetailsActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NowDetailsActivity.this, "点击了回复", Toast.LENGTH_SHORT).show();
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
        commentText.setHint("回复 @" + commentList.get(position).getUser().getNickname());

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

                    requestMessage(commentList.get(position).getReply().getRid(), replyContent);

//                    Toast.makeText(mContext,"回复成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NowDetailsActivity.this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
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

                    requestMessage(commentList.get(groupPosition).getReply().getRid(), "回复" + commentList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname() + "：" + replyContent);
//                    list.get(position).getReply().getRid()
                    Toast.makeText(NowDetailsActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NowDetailsActivity.this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
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
                .getCommentInfo(tid, 2, 1)
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
                                noCmment.setVisibility(View.VISIBLE);
                            } else {
                                noCmment.setVisibility(View.GONE);
                            }
                            commentAdapter = new CommentExpandAdapter(NowDetailsActivity.this, commentList);
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
                .requestComment(tid, pid, content, 2)
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


    private void getNowDetails() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getNow(tid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNowDetailsJson>() {
                    @Override
                    public void onNext(final GetNowDetailsJson getNowDetailsJson) {
                        super.onNext(getNowDetailsJson);

                        if (null != getNowDetailsJson && getNowDetailsJson.getErrno() == 0) {

                            footer.setVisibility(View.VISIBLE);
                            nowdetailsCommentLLY.setVisibility(View.VISIBLE);

                            if (getNowDetailsJson.getData().getTopic().getReplies() != 0) {
                                getCommentList();
                            } else {
//                                noCmment.setVisibility(View.VISIBLE);
                            }

                            GlideImageManager.glideLoader(NowDetailsActivity.this, getNowDetailsJson.getData().getUser().getAvatar(), headerIcon, GlideImageManager.TAG_ROUND);
                            nickName.setText(getNowDetailsJson.getData().getUser().getNickname());

                            Date date = DateUtils.parseDate(getNowDetailsJson.getData().getTopic().getAddtime(), "yyyy-MM-dd HH:mm:ss");
                            if (date == null) {
                                return;
                            }
                            long diff = new Date().getTime() - date.getTime();
                            long r = (diff / (60 * (60 * 1000)));

                            if (r > 12) {
                                int month = Integer.parseInt(getNowDetailsJson.getData().getTopic().getAddtime().substring(5, 7)) - 1;
                                int date1 = Integer.parseInt(getNowDetailsJson.getData().getTopic().getAddtime().substring(8, 10));
                                int hour = Integer.parseInt(getNowDetailsJson.getData().getTopic().getAddtime().substring(11, 13));
                                int minute = Integer.parseInt(getNowDetailsJson.getData().getTopic().getAddtime().substring(14, 16));
                                time.setText((month + 1) + "-" + date1);
                            } else {
                                String time1 = DateUtils.getTimeFormatText(date);
                                time.setText("" + time1);
                            }

                            content.setText(getNowDetailsJson.getData().getTopic().getContent());
                            list.clear();
                            list.addAll(getNowDetailsJson.getData().getTopic().getImages());
                            commonAdapter.notifyDataSetChanged();

                            collectCount.setText("" + getNowDetailsJson.getData().getTopic().getPraises());
                            commentCount.setText("" + getNowDetailsJson.getData().getTopic().getReplies());

                            if (getNowDetailsJson.getData().getTopic().isPraised()) {
                                collectimg.setImageResource(R.drawable.collect);

                            } else {
                                collectimg.setImageResource(R.drawable.nocollect);
                            }

                            if(isClick){
                                collectimg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isClick = false;
                                        if (getNowDetailsJson.getData().getTopic().isPraised()) {
                                            //取消收藏
                                            ServiceFactory.getNewInstance()
                                                    .createService(YService.class)
                                                    .cancelPraises(getNowDetailsJson.getData().getTopic().getTid(), 0)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new CommonObserver<PublicBean>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                                        @Override
                                                        public void onNext(PublicBean publicBean) {
                                                            super.onNext(publicBean);

                                                            if (null != publicBean && publicBean.getErrno() == 0) {
                                                                isClick = true;
                                                                getNowDetailsJson.getData().getTopic().setPraised(false);
                                                                ToastUtils.getInstance().showToast("已取消收藏");
                                                                collectimg.setImageResource(R.drawable.nocollect);
                                                                getNowDetailsJson.getData().getTopic().setPraises(getNowDetailsJson.getData().getTopic().getPraises() - 1);
                                                                collectCount.setText("" + getNowDetailsJson.getData().getTopic().getPraises());
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
                                                    .addPraises(getNowDetailsJson.getData().getTopic().getTid(), 0)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new CommonObserver<PublicBean>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                                        @Override
                                                        public void onNext(PublicBean publicBean) {
                                                            super.onNext(publicBean);

                                                            if (null != publicBean && publicBean.getErrno() == 0) {
                                                                isClick = true;
                                                                getNowDetailsJson.getData().getTopic().setPraised(true);
                                                                ToastUtils.getInstance().showToast("已收藏");
                                                                collectimg.setImageResource(R.drawable.collect);
                                                                getNowDetailsJson.getData().getTopic().setPraises(getNowDetailsJson.getData().getTopic().getPraises() + 1);
                                                                collectCount.setText("" + getNowDetailsJson.getData().getTopic().getPraises());
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
                            }

                            UIUtils.clearMemoryCache(NowDetailsActivity.this);
                            mSmart.finishRefresh();
                        } else if(null != getNowDetailsJson && getNowDetailsJson.getErrno() == 9002){

                            new AlertView("温馨提示", "您访问的内容不存在或已被删除", null, new String[]{"确定"}, null, NowDetailsActivity.this,
                                    AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position == 0) {

                                        finish();
                                    }
                                }
                            }).show();

                        }else if (null != getNowDetailsJson && getNowDetailsJson.getErrno() == 1101) {
                            cancelRefresh();
                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getNowDetailsJson && getNowDetailsJson.getErrno() != 200) {
                            cancelRefresh();
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNowDetailsJson.getErrmsg()) ? "数据加载失败" : getNowDetailsJson.getErrmsg());
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
                            cancelRefresh();
                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getIsFollowJson && getIsFollowJson.getErrno() != 200) {
                            cancelRefresh();
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getIsFollowJson.getErrmsg()) ? "数据加载失败" : getIsFollowJson.getErrmsg());
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

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.nowitem_layout, list) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {

                GlideImageManager.glideLoader(NowDetailsActivity.this, list.get(position - 1), (ImageView) holder.getView(R.id.nowitem_img), GlideImageManager.TAG_FILLET);

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 详情


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commonAdapter);
        recyclerView.addHeaderView(header);
        recyclerView.addFooterView(footer);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UIUtils.releaseImageViewResource(headerIcon);
        UIUtils.releaseImageViewResource(collectimg);
        dialog = null;
        sharedPopup = null;
        UIUtils.clearMemoryCache(this);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getNowDetails();
        getIsFollow();
    }
}
