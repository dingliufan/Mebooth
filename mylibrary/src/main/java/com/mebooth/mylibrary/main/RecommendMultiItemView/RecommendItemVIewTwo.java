package com.mebooth.mylibrary.main.RecommendMultiItemView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendItemVIewTwo implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private int praises;

    public RecommendItemVIewTwo(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommenditem_twolayout;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {
        try{
            if (item.getFeed().getType() == 1) {

                if (item.getFeed().getImages().size() == 2) {
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
    }

    @Override
    public void convert(final ViewHolder holder, final GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        GlideImageManager.glideLoader(context, recommendDataList.getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
        holder.setText(R.id.recommenditem_nickname, recommendDataList.getUser().getNickname());

        if (recommendDataList.getUser().isFollowed()) {
            holder.setText(R.id.recommenditem_follow, "已关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
        } else {
            holder.setText(R.id.recommenditem_follow, "关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
        }

        holder.setText(R.id.recommenditem_content, recommendDataList.getFeed().getContent());
        GlideImageManager.glideLoader(context, recommendDataList.getFeed().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone), GlideImageManager.TAG_FILLET);
        GlideImageManager.glideLoader(context, recommendDataList.getFeed().getImages().get(1), (ImageView) holder.getView(R.id.recommenditem_imgtwo), GlideImageManager.TAG_FILLET);
        holder.setText(R.id.recommenditem_address, recommendDataList.getFeed().getLocation());
        holder.setText(R.id.recommenditem_time, recommendDataList.getFeed().getAddtime());

        if (recommendDataList.getFeed().isPraised()) {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
        } else {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
        }

        praises = recommendDataList.getFeed().getPraises();
        holder.setText(R.id.recommenditem_collect, String.valueOf(recommendDataList.getFeed().getPraises()));
        holder.setText(R.id.recommenditem_comment, String.valueOf(recommendDataList.getFeed().getReplies()));

        holder.setOnClickListener(R.id.recommenditem_follow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recommendDataList.getUser().isFollowed()) {
                    //取消关注
                    ServiceFactory.getNewInstance()
                            .createService(YService.class)
                            .cancelFollow(recommendDataList.getUser().getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CommonObserver<PublicBean>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onNext(PublicBean publicBean) {
                                    super.onNext(publicBean);

                                    if (null != publicBean && publicBean.getErrno() == 0) {

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
                            .addFollow(recommendDataList.getUser().getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CommonObserver<PublicBean>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onNext(PublicBean publicBean) {
                                    super.onNext(publicBean);

                                    if (null != publicBean && publicBean.getErrno() == 0) {

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
        });

        holder.setOnClickListener(R.id.recommenditem_collect_img, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recommendDataList.getFeed().isPraised()) {
                    //取消收藏
                    ServiceFactory.getNewInstance()
                            .createService(YService.class)
                            .addPraises(recommendDataList.getFeed().getRelateid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CommonObserver<PublicBean>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onNext(PublicBean publicBean) {
                                    super.onNext(publicBean);

                                    if (null != publicBean && publicBean.getErrno() == 0) {

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
                            .addPraises(recommendDataList.getFeed().getRelateid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CommonObserver<PublicBean>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onNext(PublicBean publicBean) {
                                    super.onNext(publicBean);

                                    if (null != publicBean && publicBean.getErrno() == 0) {

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
        });


    }
}
