package com.mebooth.mylibrary.main.home.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.mebooth.mylibrary.main.view.SharedActivity;
import com.mebooth.mylibrary.main.view.SpacesItemDecoration;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewDetailsActivity extends BaseTransparentActivity {

    private ImageView newdetailsImage;
    private ImageView newdetailsHeaderIcon;
    private TextView newdetailsTitle;
    private TextView newdetailsTime;
    private TextView newdetailsBrowse;
    private TextView newdetailsNickName;
    private TextView newdetailsFollow;
    private RecyclerView recyclerView;
    private CommentExpandableListView expandableListView;
    private TextView commentEdit;
    private TextView newdetailsComment;
    private ImageView newdetailShare;

    private int id;
    private String nickName;
    private int watchs;

    private NewsAdapter adapter;
    private ArrayList<GetNewInfoJson.NewInfoData.News.Content> content = new ArrayList<>();
    private int replies;
    private int praises;
    private int uid;


    private ArrayList<CommentOnJson.CommentData.CommentOnList> commentList = new ArrayList<>();

    private CommentExpandAdapter commentAdapter;

    private BottomSheetDialog dialog;

    private SharedActivity sharedPopup;
    private ImageView back;
    private TextView title;
    private TextView noComment;


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
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        newdetailsImage = findViewById(R.id.newdetails_image);
        newdetailsTitle = findViewById(R.id.newdetails_title);
        newdetailsTime = findViewById(R.id.newdetails_time);
        newdetailsBrowse = findViewById(R.id.newdetails_browsecount);
        recyclerView = findViewById(R.id.classify_recycle);
        expandableListView = findViewById(R.id.detail_page_lv_comment);
        commentEdit = findViewById(R.id.detail_page_do_comment);
        newdetailsComment = findViewById(R.id.newdetails_comment);
        newdetailShare = findViewById(R.id.newdetails_share);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        noComment = findViewById(R.id.nwdetails_nocomment);
        title.setText("正文");

        id = getIntent().getIntExtra("relateid", 0);
        uid = getIntent().getIntExtra("uid", 0);
        nickName = getIntent().getStringExtra("nickname");
        watchs = getIntent().getIntExtra("browse", 0);
        replies = getIntent().getIntExtra("replies", 0);
        praises = getIntent().getIntExtra("praises", 0);

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

        newdetailsBrowse.setText("" + watchs);
        newdetailsComment.setText("" + replies);

        recyclerView.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, content);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(20);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
            }
        });
        initExpandableListView(commentList);
        getNewDetails();
        getCommentList();
        expandableListView.setNestedScrollingEnabled(true);

        newdetailShare.setOnClickListener(new View.OnClickListener() {
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
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 2) {
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
        commentText.setText("回复 @" + commentList.get(position).getUser().getNickname());
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
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 2) {
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
        commentText.setText("回复 @" + commentList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname());
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

                    requestMessage(commentList.get(groupPosition).getReply().getRid(), commentText.getText().toString());
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
                if (!TextUtils.isEmpty(charSequence) && charSequence.length() > 2) {
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

                            if(commentList.size() == 0){
                                noComment.setVisibility(View.VISIBLE);
                            }else{
                                noComment.setVisibility(View.GONE);
                            }

                            commentAdapter = new CommentExpandAdapter(NewDetailsActivity.this, commentList);
                            expandableListView.setAdapter(commentAdapter);

                            for (int i = 0; i < commentList.size(); i++) {
                                expandableListView.expandGroup(i);
                            }

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
                .requestComment(id, pid, content,1)
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

    private void getNewDetails() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getNewInfo(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetNewInfoJson>() {
                    @Override
                    public void onNext(GetNewInfoJson getNewInfoJson) {
                        super.onNext(getNewInfoJson);

                        if (null != getNewInfoJson && getNewInfoJson.getErrno() == 0) {

                            GlideImageManager.glideLoader(NewDetailsActivity.this, getNewInfoJson.getData().getNews().getCover(), newdetailsImage, GlideImageManager.TAG_RECTANGLE);
                            newdetailsTitle.setText(getNewInfoJson.getData().getNews().getTitle());
                            newdetailsTime.setText(getNewInfoJson.getData().getNews().getAddtime());
                            adapter.addData(getNewInfoJson.getData().getNews().getContent());

                        } else if (null != getNewInfoJson && getNewInfoJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getNewInfoJson && getNewInfoJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNewInfoJson.getErrmsg()) ? "数据加载失败" : getNewInfoJson.getErrmsg());
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
    }

}
