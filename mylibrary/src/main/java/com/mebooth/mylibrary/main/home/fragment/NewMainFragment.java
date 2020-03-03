package com.mebooth.mylibrary.main.home.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.MineOrderPagerAdapter;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.MineActivity;
import com.mebooth.mylibrary.main.home.activity.NewsPublishActivity;
import com.mebooth.mylibrary.main.home.activity.PublishActivity;
import com.mebooth.mylibrary.main.home.bean.GetMyUserInfo;
import com.mebooth.mylibrary.main.utils.AnimUtil;
import com.mebooth.mylibrary.main.utils.TabLayoutUtil;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.ConfirmPopWindow;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class NewMainFragment extends BaseFragment {

    private ImageView publish;
    //    private TabLayout tabLayout;
    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView headerIcon;

    private ArrayList<String> mTitles = new ArrayList<>();
    private String mTitles1[] = {"推荐","此刻","笔记","资讯"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private RecommendFragment recommendFragment = new RecommendFragment();
    private NowFragment nowFragment = new NowFragment();
    private ExperienceFragment experienceFragment = new ExperienceFragment();
    private InformationFragment informationFragment = new InformationFragment();

    private MineOrderPagerAdapter mAdapter;
    private int uid;
    private String headerIconStr;
    private String nickName;

    private ImageView back;
    private TextView title;

    private PopupWindow mPopupWindow;

    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.7f;
    private static final float END_ALPHA = 1f;

    private AnimUtil animUtil;
    private float bgAlpha = 1f;
    private boolean bright = false;
    private View popupView;
    private LinearLayout news;
    private LinearLayout topic;
    private ImageView qrCode;

    public static NewMainFragment newInstance() {
        return new NewMainFragment();
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        StatusBarUtil.setLightMode(getActivity()); //黑色图标
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.moudle_activity_main;
    }

    @Override
    protected void initView(View view) {

        back = view.findViewById(R.id.public_back);
        title = view.findViewById(R.id.public_title);
        publish = view.findViewById(R.id.consult_publish);
        qrCode = view.findViewById(R.id.consult_qrcode);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);
        headerIcon = view.findViewById(R.id.userheadericon);

        if (AppApplication.getInstance().isShowBack()) {

            back.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            view.findViewById(R.id.main_logo).setVisibility(View.GONE);
//            view.findViewById(R.id.moudle_headertwo).setVisibility(View.GONE);
        } else {
            back.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            view.findViewById(R.id.main_logo).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.newmainheader).setVisibility(View.GONE);
//            view.findViewById(R.id.moudle_headertwo).setPadding(0, UIUtils.getStatusBarHeight(getActivity()), 0, 0);

        }
//        view.findViewById(R.id.newmainheader).setPadding(0, UIUtils.getStatusBarHeight(getActivity()), 0, 0);
//        view.findViewById(R.id.app_bar).setPadding(0, UIUtils.getStatusBarHeight(getActivity()), 0, 0);
        view.findViewById(R.id.coordinator).setPadding(0, UIUtils.getStatusBarHeight(getActivity()), 0, 0);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    AppApplication.getInstance().setScan(getActivity());
                }
            }
        });
        title.setText("发现");
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        mPopupWindow = new PopupWindow(getActivity());
        animUtil = new AnimUtil();

        mTitles.add("推荐");
        mTitles.add("此刻");
        mTitles.add("笔记");
        mTitles.add("资讯");

        mFragments.add(recommendFragment);
//        mFragments.add(FriendFragment.newInstance());
        mFragments.add(nowFragment);
        mFragments.add(experienceFragment);
        mFragments.add(informationFragment);

        mAdapter = new MineOrderPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), mFragments, mTitles);
        viewPager.setAdapter(mAdapter);

        tabLayout.setViewPager(viewPager, mTitles1);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setSelectedTabIndicatorHeight(0);

//        TabLayout.Tab tabAt = tabLayout.getTabAt(0);
//        TextView textView = new TextView(getActivity());
//        float selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 18, getResources().getDisplayMetrics());
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize);
//        textView.setTextColor(getResources().getColor(R.color.bg_000000));
//        textView.setText(tabAt.getText());
////        textView.setTranslationX(10);
//        tabAt.setCustomView(textView);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
////
//                TextView textView = new TextView(getActivity());
//                float selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 18, getResources().getDisplayMetrics());
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize);
//                textView.setTextColor(getResources().getColor(R.color.bg_000000));
//                textView.setText(tab.getText());
//
//
////                textView.setTranslationX(10);
//                tab.setCustomView(textView);
//                UIUtils.clearMemoryCache(getActivity());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.setCustomView(null);
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        tabLayout.getTabAt(0).select();
//        TabLayoutUtil.reflex(tabLayout);

        headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    Intent intent = new Intent(getActivity(), MineActivity.class);
//                    Intent intent = new Intent(getActivity(), FriendFragment.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("headericon", headerIconStr);
                    intent.putExtra("nickname", nickName);
                    startActivity(intent);
                }
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {

                    showPop();
                    toggleBright();

                    //发布弹窗
//                    new ConfirmPopWindow(getActivity()).showAtBottom(publish);

                }

            }
        });

    }

    private void showPop() {
        popupView = LayoutInflater.from(getActivity()).inflate(R.layout.confirm_dialog, null);
        // 设置布局文件
        mPopupWindow.setContentView(popupView);
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
//        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
//        mPopupWindow.showAsDropDown(view, -300, 0);
        int[] loc = new int[]{1, 2};
        publish.getLocationInWindow(loc);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        loc[0] = UIUtils.getScreenWidth(activity) - UIUtils.dp2px(activity, 12) - popupView.getMeasuredWidth();
        loc[1] += publish.getHeight();
        mPopupWindow.showAtLocation(publish, Gravity.LEFT | Gravity.TOP, loc[0], loc[1]);

        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                toggleBright();
            }
        });

        news = mPopupWindow.getContentView().findViewById(R.id.ll_chat);
        topic = mPopupWindow.getContentView().findViewById(R.id.ll_friend);
//        tv_3 = mPopupWindow.getContentView().findViewById(R.id.tv_3);
//        tv_4 = mPopupWindow.getContentView().findViewById(R.id.tv_4);
//        tv_5 = mPopupWindow.getContentView().findViewById(R.id.tv_5);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
            }
        });
        topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                Intent intent = new Intent(getActivity(), NewsPublishActivity.class);
                startActivity(intent);

            }
        });

    }

    private void toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // 在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    private void getUserInfo() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMyUserInfo>() {
                    @Override
                    public void onNext(GetMyUserInfo getMyUserInfo) {
                        super.onNext(getMyUserInfo);

                        if (null != getMyUserInfo && getMyUserInfo.getErrno() == 0) {

                            GlideImageManager.glideLoader(getActivity(), getMyUserInfo.getData().getUser().getAvatar(), headerIcon, GlideImageManager.TAG_ROUND);
                            uid = getMyUserInfo.getData().getUser().getUid();
                            headerIconStr = getMyUserInfo.getData().getUser().getAvatar();
                            nickName = getMyUserInfo.getData().getUser().getNickname();
                            RongIM.getInstance().setCurrentUserInfo(new UserInfo(String.valueOf(uid), nickName, Uri.parse(headerIconStr)));
                            /**
                             * 设置消息体内是否携带用户信息。
                             * @param state 是否携带用户信息，true 携带，false 不携带。
                             */
                            RongIM.getInstance().setMessageAttachedUserInfo(true);
                            if (headerIconStr.equals("https://img.baojiawangluo.com/news/20191219160703313.jpg")) {
                                if (AppApplication.getInstance().isFirst) {

                                    try {
                                        new AlertView("设置头像或昵称", "您还没有设置头像或昵称，请先进行修改", "取消", new String[]{"确定"}, null, getActivity(),
                                                AlertView.Style.Alert, new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(Object o, int position) {
                                                if (position == 0) {
                                                    Intent intent = new Intent(getActivity(), MineActivity.class);
//                    Intent intent = new Intent(getActivity(), FriendFragment.class);
                                                    intent.putExtra("uid", uid);
                                                    intent.putExtra("headericon", headerIconStr);
                                                    intent.putExtra("nickname", nickName);
                                                    startActivity(intent);
                                                }
                                            }
                                        }).show();
                                    } catch (Exception e) {

                                    }

                                    AppApplication.getInstance().isFirst = false;
                                }

                            }

                        } else if (null != getMyUserInfo && getMyUserInfo.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                            Log.d("NewMainFragment", "token已被清空");
                        } else if (null != getMyUserInfo && getMyUserInfo.getErrno() != 200) {

//                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMyUserInfo.getErrmsg()) ? "数据加载失败" : getMyUserInfo.getErrmsg());
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

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPreferencesUtils.readString("token") != null) {

            getUserInfo();
        }

//        StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))||

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            StatusBarUtil.setLightMode(getActivity());

        }

    }
}
