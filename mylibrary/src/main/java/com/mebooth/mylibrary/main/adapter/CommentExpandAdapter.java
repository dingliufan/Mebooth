package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.bean.CommentOnJson;
import com.mebooth.mylibrary.utils.DateUtils;
import com.mebooth.mylibrary.utils.GlideImageManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private ArrayList<CommentOnJson.CommentData.CommentOnList> commentBeanList;
    private ArrayList<CommentOnJson.CommentData.CommentOnList.Reply.Replies> replyBeanList;
    private Context context;
    private int pageIndex = 1;

    public CommentExpandAdapter(Context context, ArrayList<CommentOnJson.CommentData.CommentOnList> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    @Override
    public int getGroupCount() {
        return commentBeanList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (commentBeanList.get(i).getReply().getReplies() == null) {
            return 0;
        } else {
            return commentBeanList.get(i).getReply().getReplies().size() > 0 ? commentBeanList.get(i).getReply().getReplies().size() : 0;
        }

    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).getReply().getReplies().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    boolean isLike = false;

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
//        Glide.with(context).load().into();
        GlideImageManager.glideLoader(context, commentBeanList.get(groupPosition).getUser().getAvatar(), groupHolder.logo, GlideImageManager.TAG_ROUND);

        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).getUser().getNickname());
//        int month = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(5, 7)) - 1;
//        int date = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(8, 10));
//        int hour = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(11, 13));
//        int minute = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(14, 16));
//        int second = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(17, 19));

//        Date time = DateUtils.parseDate(commentBeanList.get(groupPosition).getReply().getAddtime(), "yyyy-MM-dd HH:mm:ss");
//        int month = DateUtils.getMonth(time);
//        int day = DateUtils.getDay(time);
//        int hour = DateUtils.getHour(time);
//        int minute = DateUtils.getMinute(time);


        Date date = DateUtils.parseDate(commentBeanList.get(groupPosition).getReply().getAddtime(), "yyyy-MM-dd HH:mm:ss");

        long diff = new Date().getTime() - date.getTime();
        long r = (diff / (60 * (60 * 1000)));

        if (r > 12) {
            int month = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(5, 7)) - 1;
            int date1 = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(commentBeanList.get(groupPosition).getReply().getAddtime().substring(14, 16));
            groupHolder.tv_time.setText((month + 1) + "-" + date1);
        } else {
            String time = DateUtils.getTimeFormatText(date);
            groupHolder.tv_time.setText(time);
        }

//
//        Date date = DateUtils.parseDate(, "yyyy-MM-dd HH:mm:ss");
//        String time = DateUtils.getTimeFormatText(date);
//        .setText(time);
        groupHolder.tv_content.setText(commentBeanList.get(groupPosition).getReply().getContent());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        String replyUser = commentBeanList.get(groupPosition).getReply().getReplies().get(childPosition).getUser().getNickname();


        String str = commentBeanList.get(groupPosition).getReply().getReplies().get(childPosition).getReply().getContent();

        if (str.substring(0, 2).equals("回复")) {
            if (!TextUtils.isEmpty(replyUser)) {
                childHolder.tv_name.setText(replyUser);
            } else {
                childHolder.tv_name.setText("无名");
            }

            String[] all = str.split("：");

            String all0 = all[0];


//            childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReply().getReplies().get(childPosition).getReply().getContent());
            if (all.length <= 1) {
//                SpannableStringBuilder spannableString = new SpannableStringBuilder(all0 + "：");
                SpannableStringBuilder spannableString = new SpannableStringBuilder(replyUser+all0 + "：");

//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 2, all[0].length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 0, replyUser.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), replyUser.length()+2, (replyUser.length()+all[0].length()), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                childHolder.tv_content.setText(spannableString);
            } else {
//                SpannableStringBuilder spannableString = new SpannableStringBuilder(all0 + "：" + all[1]);
                SpannableStringBuilder spannableString = new SpannableStringBuilder(replyUser+all0 + "：" + all[1]);

//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 2, all[0].length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 0, replyUser.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), replyUser.length()+2, (replyUser.length()+all[0].length()), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                childHolder.tv_content.setText(spannableString);
            }

        } else {
            if (!TextUtils.isEmpty(replyUser)) {
                childHolder.tv_name.setText(replyUser);
            } else {
                childHolder.tv_name.setText("无名");
            }
            SpannableStringBuilder spannableString = new SpannableStringBuilder(replyUser+commentBeanList.get(groupPosition).getReply().getReplies().get(childPosition).getReply().getContent() + "：");

//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 2, all[0].length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#826428")), 0, replyUser.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

//            childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReply().getReplies().get(childPosition).getReply().getContent());
            childHolder.tv_content.setText(spannableString);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private ImageView logo;
        private TextView tv_name, tv_content, tv_time;

        //        private ImageView iv_like;
        public GroupHolder(View view) {
            logo = (ImageView) view.findViewById(R.id.comment_item_logo);
            tv_content = (TextView) view.findViewById(R.id.comment_item_content);
            tv_name = (TextView) view.findViewById(R.id.comment_item_userName);
            tv_time = (TextView) view.findViewById(R.id.comment_item_time);
        }
    }

    private class ChildHolder {
        private TextView tv_name, tv_content;

        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
        }
    }


    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     *
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(CommentOnJson.CommentData.CommentOnList commentDetailBean) {
        if (commentDetailBean != null) {

            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:回复成功后插入一条数据
     *
     * @param replyDetailBean 新的回复数据
     */
    public void addTheReplyData(CommentOnJson.CommentData.CommentOnList.Reply.Replies replyDetailBean, int groupPosition) {
        if (replyDetailBean != null) {
            Log.e(TAG, "addTheReplyData: >>>>该刷新回复列表了:" + replyDetailBean.toString());
            if (commentBeanList.get(groupPosition).getReply().getReplies() != null) {
                commentBeanList.get(groupPosition).getReply().getReplies().add(replyDetailBean);
            } else {
                ArrayList<CommentOnJson.CommentData.CommentOnList.Reply.Replies> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).getReply().setReplies(replyList);
            }
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:添加和展示所有回复
     *
     * @param replyBeanList 所有回复数据
     * @param groupPosition 当前的评论
     */
    private void addReplyList(ArrayList<CommentOnJson.CommentData.CommentOnList.Reply.Replies> replyBeanList, int groupPosition) {
        if (commentBeanList.get(groupPosition).getReply().getReplies() != null) {
            commentBeanList.get(groupPosition).getReply().getReplies().clear();
            commentBeanList.get(groupPosition).getReply().getReplies().addAll(replyBeanList);
        } else {

            commentBeanList.get(groupPosition).getReply().setReplies(replyBeanList);
        }

        notifyDataSetChanged();
    }

}
