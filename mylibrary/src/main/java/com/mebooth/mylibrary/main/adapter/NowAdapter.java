package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.mebooth.mylibrary.R;

import java.util.ArrayList;

public class NowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> datas = new ArrayList<>();

    private final int ZERO_ITEM = 0;
    private final int ONE_ITEM = 1;
    private final int TWO_ITEM = 2;
    private final int THREE_ITEM = 3;
    private final int OTHER_ITEM = 4;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        switch (i) {
            case ZERO_ITEM:

                return new ZeroViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recommenditem_zerolayout, viewGroup, false));
            case ONE_ITEM:

                return new OneViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recommenditem_onelayout, viewGroup, false));
            case TWO_ITEM:

                return new TwoViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recommenditem_twolayout, viewGroup, false));
            case THREE_ITEM:

                return new ThreeViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recommenditem_threelayout, viewGroup, false));
            case OTHER_ITEM:

                return new MoreViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recommenditem_fourlayout, viewGroup, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (datas.size() == 0) {

            return ZERO_ITEM;

        } else if (datas.size() == 1) {
            return ONE_ITEM;
        } else if (datas.size() == 2) {
            return TWO_ITEM;
        } else if (datas.size() == 3) {
            return THREE_ITEM;
        }
        return OTHER_ITEM;
    }

    /**
     * 无图
     */
    public class ZeroViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_headerIcon;
        public TextView recommenditem_nickName;
        public TextView recommenditem_follow;
        public TextView recommenditem_content;
        public TextView recommenditem_address;
        public TextView recommenditem_time;
        public TextView recommenditem_comment;
        public TextView recommenditem_collect;

        public ZeroViewHolder(View itemView) {
            super(itemView);
            recommenditem_headerIcon = (ImageView) itemView.findViewById(R.id.recommenditem_headericon);
            recommenditem_nickName = (TextView) itemView.findViewById(R.id.recommenditem_nickname);
            recommenditem_follow = (TextView) itemView.findViewById(R.id.recommenditem_follow);
            recommenditem_content = (TextView) itemView.findViewById(R.id.recommenditem_content);
            recommenditem_address = (TextView) itemView.findViewById(R.id.recommenditem_address);
            recommenditem_time = (TextView) itemView.findViewById(R.id.recommenditem_time);
            recommenditem_comment = (TextView) itemView.findViewById(R.id.recommenditem_comment);
            recommenditem_collect = (TextView) itemView.findViewById(R.id.recommenditem_collect);
        }
    }

    /**
     * 1图
     */
    public class OneViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_headerIcon;
        public TextView recommenditem_nickName;
        public TextView recommenditem_follow;
        public TextView recommenditem_content;
        public TextView recommenditem_address;
        public TextView recommenditem_time;
        public TextView recommenditem_comment;
        public TextView recommenditem_collect;
        public ImageView recommenditem_imgone;

        public OneViewHolder(View itemView) {
            super(itemView);
            recommenditem_headerIcon = (ImageView) itemView.findViewById(R.id.recommenditem_headericon);
            recommenditem_nickName = (TextView) itemView.findViewById(R.id.recommenditem_nickname);
            recommenditem_follow = (TextView) itemView.findViewById(R.id.recommenditem_follow);
            recommenditem_content = (TextView) itemView.findViewById(R.id.recommenditem_content);
            recommenditem_address = (TextView) itemView.findViewById(R.id.recommenditem_address);
            recommenditem_time = (TextView) itemView.findViewById(R.id.recommenditem_time);
            recommenditem_comment = (TextView) itemView.findViewById(R.id.recommenditem_comment);
            recommenditem_collect = (TextView) itemView.findViewById(R.id.recommenditem_collect);
            recommenditem_imgone = (ImageView) itemView.findViewById(R.id.recommenditem_imgone);
        }
    }

    /**
     * 2图
     */
    public class TwoViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_headerIcon;
        public TextView recommenditem_nickName;
        public TextView recommenditem_follow;
        public TextView recommenditem_content;
        public TextView recommenditem_address;
        public TextView recommenditem_time;
        public TextView recommenditem_comment;
        public TextView recommenditem_collect;
        public ImageView recommenditem_imgone;
        public ImageView recommenditem_imgtwo;

        public TwoViewHolder(View itemView) {
            super(itemView);
            recommenditem_headerIcon = (ImageView) itemView.findViewById(R.id.recommenditem_headericon);
            recommenditem_nickName = (TextView) itemView.findViewById(R.id.recommenditem_nickname);
            recommenditem_follow = (TextView) itemView.findViewById(R.id.recommenditem_follow);
            recommenditem_content = (TextView) itemView.findViewById(R.id.recommenditem_content);
            recommenditem_address = (TextView) itemView.findViewById(R.id.recommenditem_address);
            recommenditem_time = (TextView) itemView.findViewById(R.id.recommenditem_time);
            recommenditem_comment = (TextView) itemView.findViewById(R.id.recommenditem_comment);
            recommenditem_collect = (TextView) itemView.findViewById(R.id.recommenditem_collect);
            recommenditem_imgone = (ImageView) itemView.findViewById(R.id.recommenditem_imgone);
            recommenditem_imgtwo = (ImageView) itemView.findViewById(R.id.recommenditem_imgtwo);
        }
    }

    /**
     * 3图
     */
    public class ThreeViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_headerIcon;
        public TextView recommenditem_nickName;
        public TextView recommenditem_follow;
        public TextView recommenditem_content;
        public TextView recommenditem_address;
        public TextView recommenditem_time;
        public TextView recommenditem_comment;
        public TextView recommenditem_collect;
        public ImageView recommenditem_imgone;
        public ImageView recommenditem_imgtwo;
        public ImageView recommenditem_imgthree;

        public ThreeViewHolder(View itemView) {
            super(itemView);
            recommenditem_headerIcon = (ImageView) itemView.findViewById(R.id.recommenditem_headericon);
            recommenditem_nickName = (TextView) itemView.findViewById(R.id.recommenditem_nickname);
            recommenditem_follow = (TextView) itemView.findViewById(R.id.recommenditem_follow);
            recommenditem_content = (TextView) itemView.findViewById(R.id.recommenditem_content);
            recommenditem_address = (TextView) itemView.findViewById(R.id.recommenditem_address);
            recommenditem_time = (TextView) itemView.findViewById(R.id.recommenditem_time);
            recommenditem_comment = (TextView) itemView.findViewById(R.id.recommenditem_comment);
            recommenditem_collect = (TextView) itemView.findViewById(R.id.recommenditem_collect);
            recommenditem_imgone = (ImageView) itemView.findViewById(R.id.recommenditem_imgone);
            recommenditem_imgtwo = (ImageView) itemView.findViewById(R.id.recommenditem_imgtwo);
            recommenditem_imgthree = (ImageView) itemView.findViewById(R.id.recommenditem_imgthree);
        }
    }

    /**
     * 多图
     */
    public class MoreViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_headerIcon;
        public TextView recommenditem_nickName;
        public TextView recommenditem_follow;
        public TextView recommenditem_content;
        public TextView recommenditem_address;
        public TextView recommenditem_time;
        public TextView recommenditem_comment;
        public TextView recommenditem_collect;
        public ImageView recommenditem_imgone;
        public ImageView recommenditem_imgtwo;
        public ImageView recommenditem_imgthree;
        public ImageView recommenditem_imgfour;

        public MoreViewHolder(View itemView) {
            super(itemView);
            recommenditem_headerIcon = (ImageView) itemView.findViewById(R.id.recommenditem_headericon);
            recommenditem_nickName = (TextView) itemView.findViewById(R.id.recommenditem_nickname);
            recommenditem_follow = (TextView) itemView.findViewById(R.id.recommenditem_follow);
            recommenditem_content = (TextView) itemView.findViewById(R.id.recommenditem_content);
            recommenditem_address = (TextView) itemView.findViewById(R.id.recommenditem_address);
            recommenditem_time = (TextView) itemView.findViewById(R.id.recommenditem_time);
            recommenditem_comment = (TextView) itemView.findViewById(R.id.recommenditem_comment);
            recommenditem_collect = (TextView) itemView.findViewById(R.id.recommenditem_collect);
            recommenditem_imgone = (ImageView) itemView.findViewById(R.id.recommenditem_imgone);
            recommenditem_imgtwo = (ImageView) itemView.findViewById(R.id.recommenditem_imgtwo);
            recommenditem_imgthree = (ImageView) itemView.findViewById(R.id.recommenditem_imgthree);
            recommenditem_imgfour = (ImageView) itemView.findViewById(R.id.recommenditem_imgfour);
        }
    }

}
