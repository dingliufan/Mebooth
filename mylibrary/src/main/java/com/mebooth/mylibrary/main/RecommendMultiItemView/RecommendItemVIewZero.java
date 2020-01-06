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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendItemVIewZero implements ItemViewDelegate<GetRecommendJson.RecommendData.RecommendDataList> {

    private Context context;
    private int praises;
    private boolean follow;
    private boolean isPraised;

    public RecommendItemVIewZero(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommenditem_zerolayout;
    }

    @Override
    public boolean isForViewType(GetRecommendJson.RecommendData.RecommendDataList item, int position) {

        try{
            if (item.getFeed().getType() == 1) {

                if (item.getFeed().getImages().size() == 0) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        }catch (Exception e){
            return true;
        }
    }

    @Override
    public void convert(final ViewHolder holder, final GetRecommendJson.RecommendData.RecommendDataList recommendDataList, int position) {

        if(recommendDataList.getUser().getMedals().size() == 1){

            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.GONE);
            holder.setVisible(R.id.otheruser_medal_3, View.GONE);
            holder.setVisible(R.id.otheruser_medal_4, View.GONE);
            holder.setVisible(R.id.otheruser_medal_5, View.GONE);
            holder.setVisible(R.id.otheruser_medal_6, View.GONE);

        }else if(recommendDataList.getUser().getMedals().size() == 2){
            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(1).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_2), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_3, View.GONE);
            holder.setVisible(R.id.otheruser_medal_4, View.GONE);
            holder.setVisible(R.id.otheruser_medal_5, View.GONE);
            holder.setVisible(R.id.otheruser_medal_6, View.GONE);
        }else if(recommendDataList.getUser().getMedals().size() == 3){
            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(1).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_2), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_3, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(2).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_3), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_4, View.GONE);
            holder.setVisible(R.id.otheruser_medal_5, View.GONE);
            holder.setVisible(R.id.otheruser_medal_6, View.GONE);
        }else if(recommendDataList.getUser().getMedals().size() == 4){
            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(1).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_2), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_3, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(2).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_3), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_4, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(3).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_4), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_5, View.GONE);
            holder.setVisible(R.id.otheruser_medal_6, View.GONE);
        }else if(recommendDataList.getUser().getMedals().size() == 5){
            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(1).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_2), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_3, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(2).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_3), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_4, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(3).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_4), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_5, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(4).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_5), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_6, View.GONE);
        }else if(recommendDataList.getUser().getMedals().size() == 6){
            holder.setVisible(R.id.otheruser_medal_1, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(0).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_1), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_2, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(1).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_2), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_3, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(2).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_3), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_4, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(3).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_4), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_5, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(4).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_5), GlideImageManager.TAG_RECTANGLE);
            holder.setVisible(R.id.otheruser_medal_6, View.VISIBLE);
            GlideImageManager.glideLoader(context, recommendDataList.getUser().getMedals().get(5).getIcon(), (ImageView) holder.getView(R.id.otheruser_medal_6), GlideImageManager.TAG_RECTANGLE);

        }

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
//        GlideImageManager.glideLoader(context, recommendDataList.getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_img), GlideImageManager.TAG_FILLET);
        if (StringUtil.isEmpty(recommendDataList.getFeed().getLocation())) {
            holder.setVisible(R.id.recommenditem_address,View.GONE);
        } else {
            holder.setText(R.id.recommenditem_address, recommendDataList.getFeed().getLocation());
            holder.setVisible(R.id.recommenditem_address,View.VISIBLE);
        }
        int month = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(14, 16));
        int second = Integer.parseInt(recommendDataList.getFeed().getAddtime().substring(17, 19));
        holder.setText(R.id.recommenditem_time, (month + 1) + "-" + date + " " + hour + ":" + minute);


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

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
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
                                            recommendDataList.getUser().setFollowed(false);
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
                                            recommendDataList.getUser().setFollowed(true);
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
                    if (recommendDataList.getFeed().isPraised()) {
                        //取消收藏
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelPraises(recommendDataList.getFeed().getRelateid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            recommendDataList.getFeed().setPraised(false);
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
                                            recommendDataList.getFeed().setPraised(true);
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
                    intent.putExtra("uid", recommendDataList.getUser().getUid());
                    intent.putExtra("nickname", recommendDataList.getUser().getNickname());

                    context.startActivity(intent);
                }
            }
        });
    }
}
