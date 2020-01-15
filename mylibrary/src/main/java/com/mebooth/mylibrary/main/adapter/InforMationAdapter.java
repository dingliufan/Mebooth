package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.UIUtils;

import java.util.ArrayList;

public class InforMationAdapter extends RecyclerView.Adapter<InforMationAdapter.VH>{

    private Context context;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend;

    private OnItemClickListener mOnItemClickListener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }



    public InforMationAdapter(Context context, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend) {
        this.context = context;
        this.recommend = recommend;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.information_itemlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
//
//        RequestOptions roundOptions = new RequestOptions()
//                .placeholder(R.drawable.defaulticon)
//                .error(R.drawable.defaulticon)
//                .fallback(R.color.bg_ffffff)
//                .centerCrop()
//                .transform(new CircleCrop())
//                .priority(Priority.IMMEDIATE)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(context).load(recommend.get(position).getUser().getAvatar()).apply(roundOptions).into(holder.headerIcon);

//        GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), holder.headerIcon, GlideImageManager.TAG_ROUND);
        UIUtils.loadRoundImage(holder.headerIcon,50,recommend.get(position).getUser().getAvatar(), RoundedCornersTransformation.CORNER_ALL);

        holder.nickName.setText(recommend.get(position).getUser().getNickname());
        holder.content.setText(recommend.get(position).getFeed().getContent());

//        RequestOptions filletOptions = new RequestOptions()
//                .placeholder(R.drawable.errorimage)
//                .error(R.drawable.errorimage)
//                .fallback(R.color.bg_ffffff)
//                .centerCrop()
//                .transform(new RoundedCorners(8))
//                .priority(Priority.IMMEDIATE)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(context).load(recommend.get(position).getFeed().getImages().get(0)).apply(filletOptions).into(holder.img);
        UIUtils.loadRoundImage(holder.img,8,recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
//        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), holder.img, GlideImageManager.TAG_FILLET);
        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
        holder.time.setText((month) + "-" + date + " " + hour + ":" + minute);

        holder.browseCount.setText(String.valueOf(recommend.get(position).getFeed().getWatches()));
        holder.commentCout.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));

    }

    @Override
    public int getItemCount() {
        return recommend.size();
    }

    /**
     * 创建ViewHolder
     * 在Adapter中创建一个继承RecyclerView.ViewHolder的静态内部类
     * ViewHolder的实现和ListView的ViewHolder实现几乎一样
     */
    public class VH extends RecyclerView.ViewHolder {

        ImageView headerIcon;
        TextView nickName;
        TextView content;
        ImageView img;
        TextView time;
        TextView browseCount;
        TextView commentCout;

        public VH(View view) {
            super(view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerIcon = view.findViewById(R.id.recommenditem_headericon);
            nickName = view.findViewById(R.id.recommenditem_nickname);
            content = view.findViewById(R.id.recommenditem_content);
            img = view.findViewById(R.id.recommenditem_img);
            time = view.findViewById(R.id.recommenditem_time);
            browseCount = view.findViewById(R.id.recommenditem_browsecount);
            commentCout = view.findViewById(R.id.recommenditem_commentcount);
        }
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);

        clearViewResource(holder,holder.img);
        clearViewResource(holder,holder.headerIcon);

    }

    private void clearViewResource(VH holder, ImageView imageView) {
        if (imageView != null) {
            imageView.setImageDrawable(null);
            Glide.with(context).clear(imageView);
        }
    }

}
