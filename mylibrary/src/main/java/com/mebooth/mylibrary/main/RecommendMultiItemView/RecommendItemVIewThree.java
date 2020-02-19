package com.mebooth.mylibrary.main.RecommendMultiItemView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;


import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendItemVIewThree implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend;
    private int praises;
    private boolean follow;
    private boolean isPraised;

    public RecommendItemVIewThree(Context context, ArrayList<GetRecommendJson.RecommendData.RecommendDataList> recommend) {
        this.context = context;
        this.recommend = recommend;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommenditem_threelayout;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {

        if(position!=0){
            try {
                if (recommend.get(position).getFeed().getType() == 1) {

                    if (recommend.get(position).getFeed().getImages().size() == 3) {
                        return true;
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            }catch (Exception e){
                return false;
            }
        }else{
            return false;
        }
        
    }

    @Override
    public void convert(final ViewHolder holder, final GetRecommendJson.RecommendData.RecommendDataList recommendDataList, final int position) {
        GlideImageManager.glideLoader(context, recommend.get(position).getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
        holder.setText(R.id.recommenditem_nickname, recommend.get(position).getUser().getNickname());

        if (recommend.get(position).getUser().isFollowed()) {
            holder.setText(R.id.recommenditem_follow, "已关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
        } else {
            holder.setText(R.id.recommenditem_follow, "关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
        }

        if (AppApplication.getInstance().userid != null) {
            if (AppApplication.getInstance().userid.equals(String.valueOf(recommend.get(position).getUser().getUid()))) {
                holder.setVisible(R.id.recommenditem_follow, View.GONE);
            } else {
                holder.setVisible(R.id.recommenditem_follow, View.VISIBLE);
            }
        }

        holder.setText(R.id.recommenditem_content, recommend.get(position).getFeed().getContent());
        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone), GlideImageManager.TAG_FILLET);
        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo), GlideImageManager.TAG_FILLET);
        GlideImageManager.glideLoader(context, recommend.get(position).getFeed().getImages().get(2), (ImageView) holder.getView(R.id.recommenditem_imgthree), GlideImageManager.TAG_FILLET);
        if (StringUtil.isEmpty(recommendDataList.getFeed().getLocation())) {
            holder.setVisible(R.id.recommenditem_address,View.GONE);
        } else {
            holder.setText(R.id.recommenditem_address, recommend.get(position).getFeed().getLocation());
            holder.setVisible(R.id.recommenditem_address,View.VISIBLE);
        }
        int month = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(14, 16));
        int second = Integer.parseInt(recommend.get(position).getFeed().getAddtime().substring(17, 19));
        holder.setText(R.id.recommenditem_time, (month + 1) + "-" + date + " " + hour + ":" + minute);

        if (recommend.get(position).getFeed().isPraised()) {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
        } else {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
        }
        praises = recommend.get(position).getFeed().getPraises();
        holder.setText(R.id.recommenditem_collect, String.valueOf(recommend.get(position).getFeed().getPraises()));
        holder.setText(R.id.recommenditem_comment, String.valueOf(recommend.get(position).getFeed().getReplies()));

        holder.setOnClickListener(R.id.recommenditem_follow, new View.OnClickListener() {
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
                                            holder.setText(R.id.recommenditem_follow, "关注");
                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
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
                                            holder.setText(R.id.recommenditem_follow, "已关注");
                                            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
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

        holder.setOnClickListener(R.id.recommenditem_collect_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    if (recommend.get(position).getFeed().isPraised()) {
                        //取消收藏
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelPraises(recommend.get(position).getFeed().getRelateid(),0)
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
                                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
                                            praises = praises - 1;
                                            holder.setText(R.id.recommenditem_collect, String.valueOf(praises));
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
                                .addPraises(recommend.get(position).getFeed().getRelateid(),0)
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
                                            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
                                            praises = praises + 1;
                                            holder.setText(R.id.recommenditem_collect, String.valueOf(praises));
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
        holder.setOnClickListener(R.id.recommenditem_headericon, new View.OnClickListener() {
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
