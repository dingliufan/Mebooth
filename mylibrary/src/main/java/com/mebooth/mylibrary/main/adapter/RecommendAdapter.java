package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.activity.NewDetailsActivity;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.activity.QuicklyActivity;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.OnItemClickListener;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int HEADERVIEW = 0;
    private int RECOMMENDVIEW = 1;
    private int RECOMMENDONEVIEW = 2;
    private int RECOMMENDTWOVIEW = 3;
    private int RECOMMENDTHREEVIEW = 4;
    private int RECOMMENDFOURVIEW = 5;
    private int RECOMMENDZEROVIEW = 6;

    private Context context;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend = new ArrayList<>();
    private FlushJson bannerJson;

    private OnItemClickListener mOnItemClickListener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public RecommendAdapter(Context context, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend, FlushJson bannerJson) {
        this.context = context;
        this.recommend = recommend;
        this.bannerJson = bannerJson;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("onCreateViewHolder", "" + viewType);
        //这个函数是移除一个，会好很多。但是这个view我想想怎么搞
//for(int i=0;i<parent.getChildCount();i++){
//    View views=parent.getChildAt(i);
//    parent.removeView(views);
//}
//        parent.removeView(parent.get);
        if (viewType == 0) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_headerviewlayout, parent, false));
        } else if (viewType == 1) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommend_item, parent, false));
        } else if (viewType == 2) {
            return new OneViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_onelayout, parent, false));
        } else if (viewType == 3) {
            return new TwoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_twolayout, parent, false));
        } else if (viewType == 4) {
            return new ThreeViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_threelayout, parent, false));
        } else if (viewType == 5) {
            return new FourViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_fourlayout, parent, false));
        } else if (viewType == 6) {
            return new ZeorViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommenditem_zerolayout, parent, false));
        }

        return null;
    }

    private int praises;

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {


        //头部
        if (holder instanceof HeaderViewHolder) {
            Log.d("onBindViewHolder", "HeaderViewHolder");
            GlideImageManager.glideLoader(context, bannerJson.getData().getConfig().getImage(), ((HeaderViewHolder) holder).headerImg, GlideImageManager.TAG_FILLET);
            ((HeaderViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewDetailsActivity.class);
                    intent.putExtra("relateid", bannerJson.getData().getConfig().getNewsid());
                    context.startActivity(intent);
                }
            });

            ((HeaderViewHolder) holder).buyCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuicklyActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("title", "购车指南");
                    context.startActivity(intent);
                }
            });
            ((HeaderViewHolder) holder).publicUseCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuicklyActivity.class);
                    intent.putExtra("type", "2");
                    intent.putExtra("title", "公务用车");
                    context.startActivity(intent);
                }
            });
            ((HeaderViewHolder) holder).logisticsUseCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuicklyActivity.class);
                    intent.putExtra("type", "3");
                    intent.putExtra("title", "物流用车");
                    context.startActivity(intent);
                }
            });
        }
        //图片
        else if (holder instanceof ItemViewHolder) {
            Log.d("onBindViewHolder", "ItemViewHolder");
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((ItemViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);
            ((ItemViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());
            ((ItemViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), ((ItemViewHolder) holder).img, GlideImageManager.TAG_FILLET);
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));

            ((ItemViewHolder) holder).time.setText((month) + "-" + date + " " + hour + ":" + minute);
            ((ItemViewHolder) holder).browse.setText(String.valueOf(recommend.get(position).getFeed().getWatches()));
            ((ItemViewHolder) holder).comment.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));

        } else if (holder instanceof OneViewHolder) {
            Log.d("onBindViewHolder", "OneViewHolder");
            praises = 0;
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((OneViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);

            ((OneViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());

            if (recommend.get(position).getUser().isFollowed()) {
                ((OneViewHolder) holder).follow.setText("已关注");
                ((OneViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
            } else {
                ((OneViewHolder) holder).follow.setText("关注");
                ((OneViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
            }
            if (AppApplication.getInstance().userid != null) {
                if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                    ((OneViewHolder) holder).follow.setVisibility(View.GONE);
                } else {
                    ((OneViewHolder) holder).follow.setVisibility(View.VISIBLE);
                }
            }

            ((OneViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), ((OneViewHolder) holder).imgOne, GlideImageManager.TAG_FILLET);
            if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                ((OneViewHolder) holder).address.setVisibility(View.GONE);
            } else {
                ((OneViewHolder) holder).address.setText(recommend.get(position).getFeed().getLocation());
                ((OneViewHolder) holder).address.setVisibility(View.VISIBLE);
            }
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
            ((OneViewHolder) holder).time.setText((month + 1) + "-" + date + " " + hour + ":" + minute);


            if (recommend.get(position).getFeed().isPraised()) {
                ((OneViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
            } else {
                ((OneViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
            }
            praises = recommend.get(position).getFeed().getPraises();
            ((OneViewHolder) holder).collectText.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
            ((OneViewHolder) holder).commentCount.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
            ((OneViewHolder) holder).follow.setOnClickListener(new View.OnClickListener() {
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
                                                recommend.get(position).getUser().setFollowed(false);
                                                ToastUtils.getInstance().showToast("已取消关注");
                                                ((OneViewHolder) holder).follow.setText("关注");
                                                ((OneViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
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
                                                recommend.get(position).getUser().setFollowed(true);
                                                ToastUtils.getInstance().showToast("已关注");
                                                ((OneViewHolder) holder).follow.setText("已关注");
                                                ((OneViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
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

            ((OneViewHolder) holder).collectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        if (recommend.get(position).getFeed().isPraised()) {
                            //取消收藏
                            ServiceFactory.getNewInstance()
                                    .createService(YService.class)
                                    .cancelPraises(recommend.get(position).getFeed().getRelateid())
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
                                                ((OneViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
                                                praises = praises - 1;
                                                ((OneViewHolder) holder).collectText.setText(String.valueOf(praises));
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
                                    .addPraises(recommend.get(position).getFeed().getRelateid())
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

                                                ((OneViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
                                                praises = praises + 1;
                                                ((OneViewHolder) holder).collectText.setText(String.valueOf(praises));
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


            ((OneViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        Intent intent = new Intent(context, OtherUserActivity.class);
                        intent.putExtra("uid", recommend.get(position).getUser().getUid());
                        intent.putExtra("nickname", recommend.get(position).getUser().getNickname());

                        context.startActivity(intent);
                    }
                }
            });

        } else if (holder instanceof TwoViewHolder) {
            Log.d("onBindViewHolder", "TwoViewHolder");
            praises = 0;
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((TwoViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);

            ((TwoViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());

            if (recommend.get(position).getUser().isFollowed()) {
                ((TwoViewHolder) holder).follow.setText("已关注");
                ((TwoViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
            } else {
                ((TwoViewHolder) holder).follow.setText("关注");
                ((TwoViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
            }
            if (AppApplication.getInstance().userid != null) {
                if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                    ((TwoViewHolder) holder).follow.setVisibility(View.GONE);
                } else {
                    ((TwoViewHolder) holder).follow.setVisibility(View.VISIBLE);
                }
            }

            ((TwoViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), ((TwoViewHolder) holder).imgOne, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(1), ((TwoViewHolder) holder).imgTwo, GlideImageManager.TAG_FILLET);
            if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                ((TwoViewHolder) holder).address.setVisibility(View.GONE);
            } else {
                ((TwoViewHolder) holder).address.setText(recommend.get(position).getFeed().getLocation());
                ((TwoViewHolder) holder).address.setVisibility(View.VISIBLE);
            }
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
            ((TwoViewHolder) holder).time.setText((month + 1) + "-" + date + " " + hour + ":" + minute);


            if (recommend.get(position).getFeed().isPraised()) {
                ((TwoViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
            } else {
                ((TwoViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
            }
            praises = recommend.get(position).getFeed().getPraises();
            ((TwoViewHolder) holder).collectText.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
            ((TwoViewHolder) holder).commentCount.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
            ((TwoViewHolder) holder).follow.setOnClickListener(new View.OnClickListener() {
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
                                                recommend.get(position).getUser().setFollowed(false);
                                                ToastUtils.getInstance().showToast("已取消关注");
                                                ((TwoViewHolder) holder).follow.setText("关注");
                                                ((TwoViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
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
                                                recommend.get(position).getUser().setFollowed(true);
                                                ToastUtils.getInstance().showToast("已关注");
                                                ((TwoViewHolder) holder).follow.setText("已关注");
                                                ((TwoViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
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

            ((TwoViewHolder) holder).collectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        if (recommend.get(position).getFeed().isPraised()) {
                            //取消收藏
                            ServiceFactory.getNewInstance()
                                    .createService(YService.class)
                                    .cancelPraises(recommend.get(position).getFeed().getRelateid())
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
                                                ((TwoViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
                                                praises = praises - 1;
                                                ((TwoViewHolder) holder).collectText.setText(String.valueOf(praises));
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
                                    .addPraises(recommend.get(position).getFeed().getRelateid())
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

                                                ((TwoViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
                                                praises = praises + 1;
                                                ((TwoViewHolder) holder).collectText.setText(String.valueOf(praises));
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


            ((TwoViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        Intent intent = new Intent(context, OtherUserActivity.class);
                        intent.putExtra("uid", recommend.get(position).getUser().getUid());
                        intent.putExtra("nickname", recommend.get(position).getUser().getNickname());

                        context.startActivity(intent);
                    }
                }
            });

        } else if (holder instanceof ThreeViewHolder) {
            Log.d("onBindViewHolder", "ThreeViewHolder");
            praises = 0;
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((ThreeViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);

            ((ThreeViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());

            if (recommend.get(position).getUser().isFollowed()) {
                ((ThreeViewHolder) holder).follow.setText("已关注");
                ((ThreeViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
            } else {
                ((ThreeViewHolder) holder).follow.setText("关注");
                ((ThreeViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
            }
            if (AppApplication.getInstance().userid != null) {
                if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                    ((ThreeViewHolder) holder).follow.setVisibility(View.GONE);
                } else {
                    ((ThreeViewHolder) holder).follow.setVisibility(View.VISIBLE);
                }
            }

            ((ThreeViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), ((ThreeViewHolder) holder).imgOne, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(1), ((ThreeViewHolder) holder).imgTwo, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(2), ((ThreeViewHolder) holder).imgThree, GlideImageManager.TAG_FILLET);
            if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                ((ThreeViewHolder) holder).address.setVisibility(View.GONE);
            } else {
                ((ThreeViewHolder) holder).address.setText(recommend.get(position).getFeed().getLocation());
                ((ThreeViewHolder) holder).address.setVisibility(View.VISIBLE);
            }
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
            ((ThreeViewHolder) holder).time.setText((month + 1) + "-" + date + " " + hour + ":" + minute);


            if (recommend.get(position).getFeed().isPraised()) {
                ((ThreeViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
            } else {
                ((ThreeViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
            }
            praises = recommend.get(position).getFeed().getPraises();
            ((ThreeViewHolder) holder).collectText.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
            ((ThreeViewHolder) holder).commentCount.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
            ((ThreeViewHolder) holder).follow.setOnClickListener(new View.OnClickListener() {
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
                                                recommend.get(position).getUser().setFollowed(false);
                                                ToastUtils.getInstance().showToast("已取消关注");
                                                ((ThreeViewHolder) holder).follow.setText("关注");
                                                ((ThreeViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
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
                                                recommend.get(position).getUser().setFollowed(true);
                                                ToastUtils.getInstance().showToast("已关注");
                                                ((ThreeViewHolder) holder).follow.setText("已关注");
                                                ((ThreeViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
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

            ((ThreeViewHolder) holder).collectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        if (recommend.get(position).getFeed().isPraised()) {
                            //取消收藏
                            ServiceFactory.getNewInstance()
                                    .createService(YService.class)
                                    .cancelPraises(recommend.get(position).getFeed().getRelateid())
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
                                                ((ThreeViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
                                                praises = praises - 1;
                                                ((ThreeViewHolder) holder).collectText.setText(String.valueOf(praises));
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
                                    .addPraises(recommend.get(position).getFeed().getRelateid())
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

                                                ((ThreeViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
                                                praises = praises + 1;
                                                ((ThreeViewHolder) holder).collectText.setText(String.valueOf(praises));
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


            ((ThreeViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        Intent intent = new Intent(context, OtherUserActivity.class);
                        intent.putExtra("uid", recommend.get(position).getUser().getUid());
                        intent.putExtra("nickname", recommend.get(position).getUser().getNickname());

                        context.startActivity(intent);
                    }
                }
            });

        } else if (holder instanceof FourViewHolder) {
            Log.d("onBindViewHolder", "FourViewHolder");
            praises = 0;
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((FourViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);

            ((FourViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());

            if (recommend.get(position).getUser().isFollowed()) {
                ((FourViewHolder) holder).follow.setText("已关注");
                ((FourViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
            } else {
                ((FourViewHolder) holder).follow.setText("关注");
                ((FourViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
            }
            if (AppApplication.getInstance().userid != null) {
                if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                    ((FourViewHolder) holder).follow.setVisibility(View.GONE);
                } else {
                    ((FourViewHolder) holder).follow.setVisibility(View.VISIBLE);
                }
            }

            ((FourViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), ((FourViewHolder) holder).imgOne, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(1), ((FourViewHolder) holder).imgTwo, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(2), ((FourViewHolder) holder).imgThree, GlideImageManager.TAG_FILLET);
            GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(3), ((FourViewHolder) holder).imgFour, GlideImageManager.TAG_FILLET);
            if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                ((FourViewHolder) holder).address.setVisibility(View.GONE);
            } else {
                ((FourViewHolder) holder).address.setText(recommend.get(position).getFeed().getLocation());
                ((FourViewHolder) holder).address.setVisibility(View.VISIBLE);
            }
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
            ((FourViewHolder) holder).time.setText((month + 1) + "-" + date + " " + hour + ":" + minute);


            if (recommend.get(position).getFeed().isPraised()) {
                ((FourViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
            } else {
                ((FourViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
            }
            praises = recommend.get(position).getFeed().getPraises();
            ((FourViewHolder) holder).collectText.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
            ((FourViewHolder) holder).commentCount.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
            ((FourViewHolder) holder).follow.setOnClickListener(new View.OnClickListener() {
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
                                                recommend.get(position).getUser().setFollowed(false);
                                                ToastUtils.getInstance().showToast("已取消关注");
                                                ((FourViewHolder) holder).follow.setText("关注");
                                                ((FourViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
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
                                                recommend.get(position).getUser().setFollowed(true);
                                                ToastUtils.getInstance().showToast("已关注");
                                                ((FourViewHolder) holder).follow.setText("已关注");
                                                ((FourViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
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

            ((FourViewHolder) holder).collectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        if (recommend.get(position).getFeed().isPraised()) {
                            //取消收藏
                            ServiceFactory.getNewInstance()
                                    .createService(YService.class)
                                    .cancelPraises(recommend.get(position).getFeed().getRelateid())
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
                                                ((FourViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
                                                praises = praises - 1;
                                                ((FourViewHolder) holder).collectText.setText(String.valueOf(praises));
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
                                    .addPraises(recommend.get(position).getFeed().getRelateid())
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

                                                ((FourViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
                                                praises = praises + 1;
                                                ((FourViewHolder) holder).collectText.setText(String.valueOf(praises));
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


            ((FourViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        Intent intent = new Intent(context, OtherUserActivity.class);
                        intent.putExtra("uid", recommend.get(position).getUser().getUid());
                        intent.putExtra("nickname", recommend.get(position).getUser().getNickname());

                        context.startActivity(intent);
                    }
                }
            });

        } else if (holder instanceof ZeorViewHolder) {
            Log.d("onBindViewHolder", "ZeorViewHolder");
            praises = 0;
            GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), ((ZeorViewHolder) holder).headerImg, GlideImageManager.TAG_ROUND);

            ((ZeorViewHolder) holder).nickName.setText(recommend.get(position).getUser().getNickname());

            if (recommend.get(position).getUser().isFollowed()) {
                ((ZeorViewHolder) holder).follow.setText("已关注");
                ((ZeorViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
            } else {
                ((ZeorViewHolder) holder).follow.setText("关注");
                ((ZeorViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
            }
            if (AppApplication.getInstance().userid != null) {
                if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                    ((ZeorViewHolder) holder).follow.setVisibility(View.GONE);
                } else {
                    ((ZeorViewHolder) holder).follow.setVisibility(View.VISIBLE);
                }
            }

            ((ZeorViewHolder) holder).content.setText(recommend.get(position).getFeed().getContent());
            if (StringUtil.isEmpty(recommend.get(position).getFeed().getLocation())) {
                ((ZeorViewHolder) holder).address.setVisibility(View.GONE);
            } else {
                ((ZeorViewHolder) holder).address.setText(recommend.get(position).getFeed().getLocation());
                ((ZeorViewHolder) holder).address.setVisibility(View.VISIBLE);
            }
            int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
            int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
            int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
            int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
            int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
            ((ZeorViewHolder) holder).time.setText((month + 1) + "-" + date + " " + hour + ":" + minute);


            if (recommend.get(position).getFeed().isPraised()) {
                ((ZeorViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
            } else {
                ((ZeorViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
            }
            praises = recommend.get(position).getFeed().getPraises();
            ((ZeorViewHolder) holder).collectText.setText(String.valueOf(recommend.get(position).getFeed().getPraises()));
            ((ZeorViewHolder) holder).commentCount.setText(String.valueOf(recommend.get(position).getFeed().getReplies()));
            ((ZeorViewHolder) holder).follow.setOnClickListener(new View.OnClickListener() {
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
                                                recommend.get(position).getUser().setFollowed(false);
                                                ToastUtils.getInstance().showToast("已取消关注");
                                                ((ZeorViewHolder) holder).follow.setText("关注");
                                                ((ZeorViewHolder) holder).follow.setBackgroundResource(R.drawable.follow);
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
                                                recommend.get(position).getUser().setFollowed(true);
                                                ToastUtils.getInstance().showToast("已关注");
                                                ((ZeorViewHolder) holder).follow.setText("已关注");
                                                ((ZeorViewHolder) holder).follow.setBackgroundResource(R.drawable.nofollow);
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

            ((ZeorViewHolder) holder).collectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        if (recommend.get(position).getFeed().isPraised()) {
                            //取消收藏
                            ServiceFactory.getNewInstance()
                                    .createService(YService.class)
                                    .cancelPraises(recommend.get(position).getFeed().getRelateid())
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
                                                ((ZeorViewHolder) holder).collectImg.setImageResource(R.drawable.nocollect);
                                                praises = praises - 1;
                                                ((ZeorViewHolder) holder).collectText.setText(String.valueOf(praises));
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
                                    .addPraises(recommend.get(position).getFeed().getRelateid())
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

                                                ((ZeorViewHolder) holder).collectImg.setImageResource(R.drawable.collect);
                                                praises = praises + 1;
                                                ((ZeorViewHolder) holder).collectText.setText(String.valueOf(praises));
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


            ((ZeorViewHolder) holder).headerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                        AppApplication.getInstance().setLogin();

                    } else {
                        Intent intent = new Intent(context, OtherUserActivity.class);
                        intent.putExtra("uid", recommend.get(position).getUser().getUid());
                        intent.putExtra("nickname", recommend.get(position).getUser().getNickname());

                        context.startActivity(intent);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return recommend.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return HEADERVIEW;
        } else if (recommend.get(position).getFeed().getType() == 1) {

            if (recommend.get(position).getFeed().getImages().size() == 0) {
                return RECOMMENDZEROVIEW;
            } else if (recommend.get(position).getFeed().getImages().size() == 1) {
                return RECOMMENDONEVIEW;
            } else if (recommend.get(position).getFeed().getImages().size() == 2) {
                return RECOMMENDTWOVIEW;
            } else if (recommend.get(position).getFeed().getImages().size() == 3) {
                return RECOMMENDTHREEVIEW;
            } else if (recommend.get(position).getFeed().getImages().size() >= 4) {
                return RECOMMENDFOURVIEW;
            }
        } else {
            return RECOMMENDVIEW;
        }

//        else if (recommend.get(position).getFeed().getType() == 1) {
//
////            if (recommend.get(position).getFeed().getImages().size() == 1) {
////                return RECOMMENDONEVIEW;
////            }
//
//        } else if (recommend.get(position).getFeed().getType() == 1) {
//
////            if (recommend.get(position).getFeed().getImages().size() == 2) {
////                return RECOMMENDTWOVIEW;
////            }
//
//
//        } else if (recommend.get(position).getFeed().getType() == 1) {
//
////            if (recommend.get(position).getFeed().getImages().size() == 3) {
////                return RECOMMENDTHREEVIEW;
////            }
//
//        } else if (recommend.get(position).getFeed().getType() == 1) {
//
////            if (recommend.get(position).getFeed().getImages().size() >= 4) {
////                return RECOMMENDFOURVIEW;
////            }
//
//        } else if (recommend.get(position).getFeed().getType() == 1) {
//
//            return RECOMMENDVIEW;
//
//        }

        return 0;
    }

    /**
     * 头部
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public ImageView buyCar;
        public ImageView publicUseCar;
        public ImageView logisticsUseCar;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
            headerImg = itemView.findViewById(R.id.recommenditem_headerimg);
            buyCar = itemView.findViewById(R.id.buy_car);
            publicUseCar = itemView.findViewById(R.id.publicusecar);
            logisticsUseCar = itemView.findViewById(R.id.logisticsusecar);
        }
    }

    /**
     * itemrecommend
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView content;
        public ImageView img;
        public TextView time;
        public TextView browse;
        public TextView comment;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            content = itemView.findViewById(R.id.recommenditem_content);
            img = itemView.findViewById(R.id.recommenditem_img);
            time = itemView.findViewById(R.id.recommenditem_time);
            browse = itemView.findViewById(R.id.recommenditem_browsecount);
            comment = itemView.findViewById(R.id.recommenditem_commentcount);
        }
    }

    /**
     * itemrecommend
     */
    public class OneViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView follow;
        public ImageView delete;
        public TextView content;
        public ImageView imgOne;
        public TextView address;
        public TextView commentCount;
        public ImageView collectImg;
        public TextView collectText;
        public TextView time;

        public OneViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            follow = itemView.findViewById(R.id.recommenditem_follow);
            delete = itemView.findViewById(R.id.recommenditem_delete);
            content = itemView.findViewById(R.id.recommenditem_content);
            imgOne = itemView.findViewById(R.id.recommenditem_imgone);
            address = itemView.findViewById(R.id.recommenditem_address);
            time = itemView.findViewById(R.id.recommenditem_time);
            commentCount = itemView.findViewById(R.id.recommenditem_comment);
            collectImg = itemView.findViewById(R.id.recommenditem_collect_img);
            collectText = itemView.findViewById(R.id.recommenditem_collect);

        }
    }

    /**
     * itemrecommend
     */
    public class TwoViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView follow;
        public ImageView delete;
        public TextView content;
        public ImageView imgOne;
        public ImageView imgTwo;
        public TextView address;
        public TextView commentCount;
        public ImageView collectImg;
        public TextView collectText;
        public TextView time;
        public TextView comment;

        public TwoViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            follow = itemView.findViewById(R.id.recommenditem_follow);
            delete = itemView.findViewById(R.id.recommenditem_delete);
            content = itemView.findViewById(R.id.recommenditem_content);
            imgOne = itemView.findViewById(R.id.recommenditem_imgone);
            imgTwo = itemView.findViewById(R.id.recommenditem_imgtwo);
            address = itemView.findViewById(R.id.recommenditem_address);
            time = itemView.findViewById(R.id.recommenditem_time);
            commentCount = itemView.findViewById(R.id.recommenditem_comment);
            collectImg = itemView.findViewById(R.id.recommenditem_collect_img);
            collectText = itemView.findViewById(R.id.recommenditem_collect);

        }
    }

    /**
     * itemrecommend
     */
    public class ThreeViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView follow;
        public ImageView delete;
        public TextView content;
        public ImageView imgOne;
        public ImageView imgTwo;
        public ImageView imgThree;
        public TextView address;
        public TextView commentCount;
        public ImageView collectImg;
        public TextView collectText;
        public TextView time;
        public TextView comment;

        public ThreeViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            follow = itemView.findViewById(R.id.recommenditem_follow);
            delete = itemView.findViewById(R.id.recommenditem_delete);
            content = itemView.findViewById(R.id.recommenditem_content);
            imgOne = itemView.findViewById(R.id.recommenditem_imgone);
            imgTwo = itemView.findViewById(R.id.recommenditem_imgtwo);
            imgThree = itemView.findViewById(R.id.recommenditem_imgthree);
            address = itemView.findViewById(R.id.recommenditem_address);
            time = itemView.findViewById(R.id.recommenditem_time);
            commentCount = itemView.findViewById(R.id.recommenditem_comment);
            collectImg = itemView.findViewById(R.id.recommenditem_collect_img);
            collectText = itemView.findViewById(R.id.recommenditem_collect);

        }
    }

    /**
     * itemrecommend
     */
    public class FourViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView follow;
        public ImageView delete;
        public TextView content;
        public ImageView imgOne;
        public ImageView imgTwo;
        public ImageView imgThree;
        public ImageView imgFour;
        public TextView imgMore;
        public TextView address;
        public TextView commentCount;
        public ImageView collectImg;
        public TextView collectText;
        public TextView time;
        public TextView comment;

        public FourViewHolder(View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            follow = itemView.findViewById(R.id.recommenditem_follow);
            delete = itemView.findViewById(R.id.recommenditem_delete);
            content = itemView.findViewById(R.id.recommenditem_content);
            imgOne = itemView.findViewById(R.id.recommenditem_imgone);
            imgTwo = itemView.findViewById(R.id.recommenditem_imgtwo);
            imgThree = itemView.findViewById(R.id.recommenditem_imgthree);
            imgFour = itemView.findViewById(R.id.recommenditem_imgfour);
            imgMore = itemView.findViewById(R.id.recommenditem_imgmore);
            address = itemView.findViewById(R.id.recommenditem_address);
            time = itemView.findViewById(R.id.recommenditem_time);
            commentCount = itemView.findViewById(R.id.recommenditem_comment);
            collectImg = itemView.findViewById(R.id.recommenditem_collect_img);
            collectText = itemView.findViewById(R.id.recommenditem_collect);

        }
    }

    /**
     * itemrecommend
     */
    public class ZeorViewHolder extends RecyclerView.ViewHolder {

        public ImageView headerImg;
        public TextView nickName;
        public TextView follow;
        public ImageView delete;
        public TextView content;
        public TextView imgMore;
        public TextView address;
        public TextView commentCount;
        public ImageView collectImg;
        public TextView collectText;
        public TextView time;
        public TextView comment;

        public ZeorViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(getAdapterPosition());
                }
            });

            headerImg = itemView.findViewById(R.id.recommenditem_headericon);
            nickName = itemView.findViewById(R.id.recommenditem_nickname);
            follow = itemView.findViewById(R.id.recommenditem_follow);
            delete = itemView.findViewById(R.id.recommenditem_delete);
            content = itemView.findViewById(R.id.recommenditem_content);
            imgMore = itemView.findViewById(R.id.recommenditem_imgmore);
            address = itemView.findViewById(R.id.recommenditem_address);
            time = itemView.findViewById(R.id.recommenditem_time);
            commentCount = itemView.findViewById(R.id.recommenditem_comment);
            collectImg = itemView.findViewById(R.id.recommenditem_collect_img);
            collectText = itemView.findViewById(R.id.recommenditem_collect);

        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
//        GlideImageManager.

    }
}
