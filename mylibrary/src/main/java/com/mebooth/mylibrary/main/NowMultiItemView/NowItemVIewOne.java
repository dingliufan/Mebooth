package com.mebooth.mylibrary.main.NowMultiItemView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ItemViewDelegate;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.activity.OtherUserActivity;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.utils.NoPublish;
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

public class NowItemVIewOne implements ItemViewDelegate<GetNowJson.NowData.NowDataList> {

    private Context context;
    private int praises;
    private String type = "";
    private MultiItemTypeAdapter adapter;
    private ArrayList<GetNowJson.NowData.NowDataList> list;
    private NoPublish noPublish;

    public NowItemVIewOne(Context context, String type, MultiItemTypeAdapter adapter, ArrayList<GetNowJson.NowData.NowDataList> list, NoPublish noPublish) {
        this.context = context;
        this.type = type;
        this.adapter = adapter;
        this.list = list;
        this.noPublish = noPublish;
    }

    public NowItemVIewOne(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.recommenditem_onelayout;
    }

    @Override
    public boolean isForViewType(GetNowJson.NowData.NowDataList item, int position) {
        if (item.getTopic().getImages().size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void convert(final ViewHolder holder, final GetNowJson.NowData.NowDataList nowDataList, final int position) {

        if (type.equals("minepublic")) {
            holder.setVisible(R.id.recommenditem_follow, View.GONE);
            holder.setVisible(R.id.recommenditem_delete, View.VISIBLE);
        } else {
            holder.setVisible(R.id.recommenditem_follow, View.VISIBLE);
            holder.setVisible(R.id.recommenditem_delete, View.GONE);
        }

        holder.setOnClickListener(R.id.recommenditem_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ServiceFactory.getNewInstance()
                        .createService(YService.class)
                        .deleteTopic(nowDataList.getTopic().getTid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CommonObserver<PublicBean>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onNext(PublicBean publicBean) {
                                super.onNext(publicBean);

                                if (null != publicBean && publicBean.getErrno() == 0) {

                                    ToastUtils.getInstance().showToast("已删除该话题");
                                    list.remove(position);
                                    if (type.equals("minepublic")) {
                                        if (list.size() == 0) {
                                            noPublish.isPublish();
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

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
        });

        GlideImageManager.glideLoader(context, nowDataList.getUser().getAvatar(), (ImageView) holder.getView(R.id.recommenditem_headericon), GlideImageManager.TAG_ROUND);
        holder.setText(R.id.recommenditem_nickname, nowDataList.getUser().getNickname());
        if (nowDataList.getUser().isFollowed()) {
            holder.setText(R.id.recommenditem_follow, "已关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.nofollow);
        } else {
            holder.setText(R.id.recommenditem_follow, "关注");
            holder.setBackgroundRes(R.id.recommenditem_follow, R.drawable.follow);
        }

        if (AppApplication.getInstance().userid != null) {
            if (AppApplication.getInstance().userid.equals(nowDataList.getUser().getUid())) {
                holder.setVisible(R.id.recommenditem_follow, View.GONE);
            } else {
                holder.setVisible(R.id.recommenditem_follow, View.VISIBLE);
            }
        }

        holder.setText(R.id.recommenditem_content, nowDataList.getTopic().getContent());
        GlideImageManager.glideLoader(context, nowDataList.getTopic().getImages().get(0), (ImageView) holder.getView(R.id.recommenditem_imgone), GlideImageManager.TAG_FILLET);
        if (StringUtil.isEmpty(nowDataList.getTopic().getLocation())) {
            holder.setVisible(R.id.recommenditem_address, View.GONE);
        } else {
            holder.setText(R.id.recommenditem_address, nowDataList.getTopic().getLocation());
            holder.setVisible(R.id.recommenditem_address, View.VISIBLE);
        }
        int month = Integer.parseInt(nowDataList.getTopic().getAddtime().substring(5, 7)) - 1;
        int date = Integer.parseInt(nowDataList.getTopic().getAddtime().substring(8, 10));
        int hour = Integer.parseInt(nowDataList.getTopic().getAddtime().substring(11, 13));
        int minute = Integer.parseInt(nowDataList.getTopic().getAddtime().substring(14, 16));
        int second = Integer.parseInt(nowDataList.getTopic().getAddtime().substring(17, 19));
        holder.setText(R.id.recommenditem_time, (month + 1) + "-" + date + " " + hour + ":" + minute);

        if (nowDataList.getTopic().isPraised()) {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.collect);
        } else {
            holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
        }
        praises = nowDataList.getTopic().getPraises();
        holder.setText(R.id.recommenditem_collect, String.valueOf(nowDataList.getTopic().getPraises()));
        holder.setText(R.id.recommenditem_comment, String.valueOf(nowDataList.getTopic().getReplies()));

        holder.setOnClickListener(R.id.recommenditem_follow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    if (nowDataList.getUser().isFollowed()) {
                        //取消关注
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelFollow(nowDataList.getUser().getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            nowDataList.getUser().setFollowed(false);
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
                                .addFollow(nowDataList.getUser().getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            nowDataList.getUser().setFollowed(true);
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
                    if (nowDataList.getTopic().isPraised()) {
                        //取消收藏
                        ServiceFactory.getNewInstance()
                                .createService(YService.class)
                                .cancelPraises(nowDataList.getTopic().getTid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            if (type.equals("minecollect")) {
                                                ToastUtils.getInstance().showToast("已取消收藏");
                                                list.remove(position);
                                                if (list.size() == 0) {
                                                    noPublish.isCollect();
                                                }
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                nowDataList.getTopic().setPraised(false);
                                                ToastUtils.getInstance().showToast("已取消收藏");
                                                holder.setImageResource(R.id.recommenditem_collect_img, R.drawable.nocollect);
                                                praises = praises - 1;
                                                holder.setText(R.id.recommenditem_collect, String.valueOf(praises));

                                            }
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
                                .addPraises(nowDataList.getTopic().getTid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CommonObserver<PublicBean>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onNext(PublicBean publicBean) {
                                        super.onNext(publicBean);

                                        if (null != publicBean && publicBean.getErrno() == 0) {
                                            nowDataList.getTopic().setPraised(true);
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
                    intent.putExtra("uid", nowDataList.getUser().getUid());
                    intent.putExtra("nickname", nowDataList.getUser().getNickname());
                    context.startActivity(intent);
                }

            }
        });

    }

}
