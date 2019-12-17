package com.mebooth.mylibrary.main.home.activity;

import android.graphics.Color;
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
import com.mebooth.mylibrary.main.view.SharedActivity;
import com.mebooth.mylibrary.main.view.SpacesItemDecoration;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NowDetailsActivity extends BaseTransparentActivity {

    private ImageView headerIcon;
    private TextView nickName;
    private TextView follow;
    private TextView content;
    private RecyclerView recyclerView;
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
    protected void initData() {
        super.initData();

        headerIcon = findViewById(R.id.recommenditem_headericon);
        nickName = findViewById(R.id.recommenditem_nickname);
        follow = findViewById(R.id.recommenditem_follow);
        content = findViewById(R.id.nowdetails_content);
        recyclerView = findViewById(R.id.classify_recycle);
        expandableListView = findViewById(R.id.detail_page_lv_comment);
        commentEdit = findViewById(R.id.detail_page_do_comment);
        collectimg = findViewById(R.id.newdetails_collectimg);
        share = findViewById(R.id.newdetails_share);
        sendComment = findViewById(R.id.newdetails_sendcomment);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);

        title.setText("话题详情");

        tid = getIntent().getIntExtra("relateid",0);
        uid = getIntent().getIntExtra("uid",0);

        sharedPopup = new SharedActivity(NowDetailsActivity.this, tid, "news");


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
        getNowDetails();
        getIsFollow();
        getCommentList();
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
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this,R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    //commentOnWork(commentContent);
                    dialog.dismiss();
                    requestMessage(0,commentContent);

                }else {
                    Toast.makeText(NowDetailsActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
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
    private void initExpandableListView(final ArrayList<CommentOnJson.CommentData.CommentOnList> commentList){
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

        for(int i = 0; i<commentList.size(); i++){
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
                Toast.makeText(NowDetailsActivity.this,"点击了回复",Toast.LENGTH_SHORT).show();
//                showReplyDialog(childPosition);
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    showReplyTwoDialog(groupPosition,childPosition);
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
    private void showReplyDialog(final int position){
        dialog = new BottomSheetDialog(this,R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setHint("回复 @" + commentList.get(position).getUser().getNickname());

        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(replyContent)){

                    dialog.dismiss();
//                    CommentOnJson.CommentData.CommentOnList.Reply.Replies detailBean = new CommentOnJson.CommentData.CommentOnList.Reply.Replies();
//
//                    adapter.addTheReplyData(detailBean, position);
//                    expandableListView.expandGroup(position);

                    requestMessage(commentList.get(position).getReply().getRid(),commentText.getText().toString());

//                    Toast.makeText(mContext,"回复成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NowDetailsActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
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
    private void showReplyTwoDialog(final int groupPosition,final int childPosition){
        dialog = new BottomSheetDialog(this,R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setText("回复 @" + commentList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname());
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(replyContent)){

                    dialog.dismiss();
//                    CommentOnJson.CommentData.CommentOnList.Reply.Replies detailBean = new CommentOnJson.CommentData.CommentOnList.Reply.Replies();
//
//                    adapter.addTheReplyData(detailBean, position);
//                    expandableListView.expandGroup(position);

                    requestMessage(commentList.get(groupPosition).getReply().getRid(),commentText.getText().toString());
//                    list.get(position).getReply().getRid()
                    Toast.makeText(NowDetailsActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NowDetailsActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
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
                .getCommentInfo(tid,1,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<CommentOnJson>() {
                    @Override
                    public void onNext(CommentOnJson commentOnJson) {
                        super.onNext(commentOnJson);

                        if (null != commentOnJson && commentOnJson.getErrno() == 0) {

                            commentList.clear();
                            commentList.addAll(commentOnJson.getData().getList());
                            commentAdapter = new CommentExpandAdapter(NowDetailsActivity.this, commentList);
                            expandableListView.setAdapter(commentAdapter);

                            for(int i = 0; i<commentList.size(); i++){
                                expandableListView.expandGroup(i);
                            }

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

    private void requestMessage(int pid,String content) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .requestComment(tid,pid,content,2)
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


                        }  else if (null != publicBean && publicBean.getErrno() != 200) {

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
                    public void onNext(GetNowDetailsJson getNowDetailsJson) {
                        super.onNext(getNowDetailsJson);

                        if (null != getNowDetailsJson && getNowDetailsJson.getErrno() == 0) {

                            GlideImageManager.glideLoader(NowDetailsActivity.this, getNowDetailsJson.getData().getUser().getAvatar(), headerIcon, GlideImageManager.TAG_ROUND);
                            nickName.setText(getNowDetailsJson.getData().getUser().getNickname());
                            content.setText(getNowDetailsJson.getData().getTopic().getContent());
                            list.addAll(getNowDetailsJson.getData().getTopic().getImages());
                            commonAdapter.notifyDataSetChanged();

                            if(getNowDetailsJson.getData().getTopic().isPraised()){
                                collectimg.setImageResource(R.drawable.collect);

                            }else{
                                collectimg.setImageResource(R.drawable.nocollect);
                            }
                        } else if (null != getNowDetailsJson && getNowDetailsJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getNowDetailsJson.getErrmsg()) ? "数据加载失败" : getNowDetailsJson.getErrmsg());
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

    private void getIsFollow() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getIsFollow(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetIsFollowJson>() {
                    @Override
                    public void onNext(GetIsFollowJson getIsFollowJson) {
                        super.onNext(getIsFollowJson);

                        if (null != getIsFollowJson && getIsFollowJson.getErrno() == 0) {

                            if(getIsFollowJson.getData().getUsers().get(0).isFollowed()){
                                follow.setText("已关注");
                                follow.setBackgroundResource(R.drawable.nofollow);
                            }else{
                                follow.setText("关注");
                                follow.setBackgroundResource(R.drawable.follow);
                            }

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

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.nowitem_layout, list) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {

                GlideImageManager.glideLoader(NowDetailsActivity.this, list.get(position), (ImageView) holder.getView(R.id.nowitem_img), GlideImageManager.TAG_FILLET);

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

    }

}
