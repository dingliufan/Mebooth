package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.activity.NewsOtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.DateUtils;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InforMationAdapter extends RecyclerView.Adapter<InforMationAdapter.VH> {

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
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
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
        UIUtils.loadRoundImage(holder.headerIcon, 50, recommend.get(position).getUser().getAvatar(), RoundedCornersTransformation.CORNER_ALL);

        if (AppApplication.getInstance().userid != null) {
            if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                holder.follow.setVisibility(View.GONE);
            } else {
                holder.follow.setVisibility(View.VISIBLE);
            }
        }

        if (recommend.get(position).getUser().isFollowed()) {
            holder.follow.setText("已关注");
            holder.follow.setTextColor(context.getResources().getColor(R.color.bg_999999));
            holder.follow.setBackgroundResource(R.drawable.nofollow);
        } else {
            holder.follow.setText("关注");
            holder.follow.setTextColor(context.getResources().getColor(R.color.bg_E73828));
            holder.follow.setBackgroundResource(R.drawable.follow);
        }


        if (recommend.get(position).getFeed().isPraised()) {

            holder.collectImg.setImageResource(R.drawable.collect);

        } else {
            holder.collectImg.setImageResource(R.drawable.nocollect);
        }

        holder.nickName.setText(recommend.get(position).getUser().getNickname());
        holder.content.setText(recommend.get(position).getFeed().getContent());
        holder.zhaiYao.setText(recommend.get(position).getFeed().getDescribe().replace("\\n", "\n"));

        if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
            holder.address.setVisibility(View.GONE);
        } else {
            holder.address.setText(recommend.get(position).getFeed().getLocation());
            holder.address.setVisibility(View.VISIBLE);
        }

//        RequestOptions filletOptions = new RequestOptions()
//                .placeholder(R.drawable.errorimage)
//                .error(R.drawable.errorimage)
//                .fallback(R.color.bg_ffffff)
//                .centerCrop()
//                .transform(new RoundedCorners(8))
//                .priority(Priority.IMMEDIATE)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(context).load(recommend.get(position).getFeed().getImages().get(0)).apply(filletOptions).into(holder.img);
        UIUtils.loadRoundImage(holder.img, 8, recommend.get(position).getFeed().getImages().get(0), RoundedCornersTransformation.CORNER_ALL);
//        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), holder.img, GlideImageManager.TAG_FILLET);
//        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
//        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
//        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
//        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
//        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));

        Date date = DateUtils.parseDate(recommend.get(position).getFeed().getAddtime(), "yyyy-MM-dd HH:mm:ss");
        if (date == null) {
            return;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = (diff / (60 * (60 * 1000)));

        if (r > 12) {
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date1 = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            holder.time.setText((month + 1) + "-" + date1);
        } else {
            String time = DateUtils.getTimeFormatText(date);
            holder.time.setText(time);
        }

//        Date date = DateUtils.parseDate(, "yyyy-MM-dd HH:mm:ss");
//        String time = DateUtils.getTimeFormatText(date);
//        holder..setText(time);


        holder.browseCount.setText(String.valueOf(recommend.get(position).getFeed().getWatches()));
        holder.commentCout.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
        holder.collect.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    if (recommend.get(position).getUser().isFollowed()) {
                        //取消关注
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelFollow(recommend.get(position).getUser().getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                    recommendDataList.getUser().setFollowed(false);
                                                }
                                            }
                                            notifyDataSetChanged();
//                                            recommend.get(position).getUser().setFollowed(false);
                                            ToastUtils.getInstance().showToast("已取消关注");
//                                            holder.follow.setText("关注");
//                                            holder.follow.setBackgroundResource(R.drawable.follow);
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
                                .addFollow(recommend.get(position).getUser().getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            for (GetRecommendJson.RecommendData.RecommendDataList recommendDataList : recommend) {
                                                if (recommendDataList.getUser().getUid() == recommend.get(position).getUser().getUid()) {

                                                    recommendDataList.getUser().setFollowed(true);
                                                }
                                            }
                                            notifyDataSetChanged();
//                                            recommend.get(position).getUser().setFollowed(true);
                                            ToastUtils.getInstance().showToast("已关注");
//                                            holder.follow.setText("已关注");
//                                            holder.follow.setBackgroundResource(R.drawable.nofollow);
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
        });

        holder.collectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    if (recommend.get(position).getFeed().isPraised()) {
                        //取消收藏
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelPraises(recommend.get(position).getFeed().getRelateid(), 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            recommend.get(position).getFeed().setPraised(false);
                                            ToastUtils.getInstance().showToast("已取消收藏");
                                            holder.collectImg.setImageResource(R.drawable.nocollect);
                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() - 1);
                                            holder.collect.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
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
                                .addPraises(recommend.get(position).getFeed().getRelateid(), 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            recommend.get(position).getFeed().setPraised(true);
                                            ToastUtils.getInstance().showToast("已收藏");
                                            holder.collectImg.setImageResource(R.drawable.collect);
                                            recommend.get(position).getFeed().setPraises(recommend.get(position).getFeed().getPraises() + 1);
                                            holder.collect.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
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
        });
        holder.headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {

                    Intent intent = new Intent(context, NewsOtherUserActivity.class);
                    intent.putExtra("uid", recommend.get(position).getUser().getUid());
                    intent.putExtra("nickname", recommend.get(position).getUser().getNickname());
                    context.startActivity(intent);
                }
            }
        });


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
        TextView follow;
        TextView zhaiYao;
        ImageView collectImg;
        TextView collect;
        TextView address;

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
            follow = view.findViewById(R.id.recommenditem_follow);
            zhaiYao = view.findViewById(R.id.recommenditem_zhaiyao);
            collectImg = view.findViewById(R.id.recommenditem_collect_img);
            collect = view.findViewById(R.id.recommenditem_collect);
            address = view.findViewById(R.id.recommenditem_address);
        }
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);

        clearViewResource(holder, holder.img);
        clearViewResource(holder, holder.headerIcon);

    }

    private void clearViewResource(VH holder, ImageView imageView) {
        if (imageView != null) {
            imageView.setImageDrawable(null);
            Glide.with(context).clear(imageView);
        }
    }

}
