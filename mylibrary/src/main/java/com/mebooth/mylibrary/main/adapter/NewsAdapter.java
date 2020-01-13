package com.mebooth.mylibrary.main.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.bean.GetNewInfoJson;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<GetNewInfoJson.NewInfoData.News.Content> datas;
    public static final String TAG = "ListNormalAdapter22";

    public NewsAdapter(Context context, ArrayList<GetNewInfoJson.NewInfoData.News.Content> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 5) {
            return new HeadViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newstext_layout, parent, false));
        } else if (viewType == 6) {
            return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newsimage_layout, parent, false));
        } else if (viewType == 7) {
            return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newsvideo_layout, parent, false));
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //文字
        if (holder instanceof HeadViewHolder) {
            ((HeadViewHolder) holder).recommenditem_content.setText(datas.get(position).getContent());

        }
        //图片
        else if (holder instanceof MessageViewHolder) {
            GlideImageManager.glideLoader(context, datas.get(position).getImage(), ((MessageViewHolder) holder).recommenditem_image1, GlideImageManager.TAG_FILLET);

        } else if (holder instanceof VideoViewHolder) {

            ((VideoViewHolder) holder).videoPlayer.setUpLazy(datas.get(position).getVideo(), true, null, null, "");
            ((VideoViewHolder) holder).videoPlayer.getTitleTextView().setVisibility(View.GONE);
            //内置封面可参考SampleCoverVideo
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(datas.get(position).getImage()).into(imageView);
            ((VideoViewHolder) holder).videoPlayer.setThumbImageView(imageView);
            //设置返回键
            ((VideoViewHolder) holder).videoPlayer.getBackButton().setVisibility(View.GONE);
            //设置全屏按键功能
            ((VideoViewHolder) holder).videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((VideoViewHolder) holder).videoPlayer.startWindowFullscreen(context, false, true);
                }
            });
            //防止错位设置
            ((VideoViewHolder) holder).videoPlayer.setPlayTag(TAG);
            ((VideoViewHolder) holder).videoPlayer.setPlayPosition(position);
            //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
            ((VideoViewHolder) holder).videoPlayer.setAutoFullWithSize(true);
            //音频焦点冲突时是否释放
            ((VideoViewHolder) holder).videoPlayer.setReleaseWhenLossAudio(false);
            //全屏动画
            ((VideoViewHolder) holder).videoPlayer.setShowFullAnimation(true);
            //小屏时不触摸滑动
            ((VideoViewHolder) holder).videoPlayer.setIsTouchWiget(false);

        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * w文字
     */
    public class HeadViewHolder extends RecyclerView.ViewHolder {

        public TextView recommenditem_content;

        public HeadViewHolder(View itemView) {
            super(itemView);
            recommenditem_content = (TextView) itemView.findViewById(R.id.newstext);
        }
    }

    /**
     * 图片
     */
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView recommenditem_image1;

        public MessageViewHolder(View itemView) {
            super(itemView);
            recommenditem_image1 = (ImageView) itemView.findViewById(R.id.newsimage);

        }
    }

    /**
     * 视频
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public StandardGSYVideoPlayer videoPlayer;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoPlayer = (StandardGSYVideoPlayer) itemView.findViewById(R.id.newsvideo);

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position != 0) {
            if (datas.get(position - 1).getType().equals("text")) {
                return 5;
            } else if (datas.get(position - 1).getType().equals("image")) {
                return 6;
            } else if (datas.get(position - 1).getType().equals("video")) {
                return 7;
            }
        }

        return 7;
    }

    /**
     * 添加数据
     */
    public void addData(ArrayList<GetNewInfoJson.NewInfoData.News.Content> data) {
        datas.clear();
        datas.addAll(data);
        notifyDataSetChanged();
    }

}
