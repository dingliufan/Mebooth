package com.mebooth.mylibrary.main.home.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.MineOrderPagerAdapter;
import com.mebooth.mylibrary.main.base.BaseFragment;
import com.mebooth.mylibrary.main.home.activity.FriendActivity;
import com.mebooth.mylibrary.main.home.activity.MineActivity;
import com.mebooth.mylibrary.main.home.activity.PublishActivity;
import com.mebooth.mylibrary.main.home.bean.GetMyUserInfo;
import com.mebooth.mylibrary.main.home.bean.UserTokenJson;
import com.mebooth.mylibrary.main.utils.TabLayoutUtil;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.EdiitNickName;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class NewMainFragment extends BaseFragment {

    private ImageView publish;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView headerIcon;

    private List<String> mTitles = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private RecommendFragment recommendFragment = new RecommendFragment();
    private NowFragment nowFragment = new NowFragment();
    private ExperienceFragment experienceFragment = new ExperienceFragment();
    private InformationFragment informationFragment = new InformationFragment();

    private MineOrderPagerAdapter mAdapter;
    private int uid;
    private String headerIconStr;
    private String nickName;
    private boolean isFirst = true;

    public static NewMainFragment newInstance() {
        return new NewMainFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.moudle_activity_main;
    }

    @Override
    protected void initView(View view) {

        publish = (ImageView) view.findViewById(R.id.consult_publish);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);
        headerIcon = view.findViewById(R.id.userheadericon);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mFragments.add(recommendFragment);
//        mFragments.add(FriendActivity.newInstance());
        mFragments.add(nowFragment);
//        mFragments.add(experienceFragment);
        mFragments.add(informationFragment);
        mTitles.add("推荐");
        mTitles.add("此刻");
//        mTitles.add("体验");
        mTitles.add("资讯");

        mAdapter = new MineOrderPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), mFragments, mTitles);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);

        TabLayout.Tab tabAt = tabLayout.getTabAt(0);
        TextView textView = new TextView(getActivity());
        float selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 18, getResources().getDisplayMetrics());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize);
        textView.setTextColor(getResources().getColor(R.color.bg_000000));
        textView.setText(tabAt.getText());
//        textView.setTranslationX(10);
        tabAt.setCustomView(textView);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//
                TextView textView = new TextView(getActivity());
                float selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 18, getResources().getDisplayMetrics());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize);
                textView.setTextColor(getResources().getColor(R.color.bg_000000));
                textView.setText(tab.getText());


//                textView.setTranslationX(10);
                tab.setCustomView(textView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
        TabLayoutUtil.reflex(tabLayout);

        headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(SharedPreferencesUtils.readString("token"))) {

                    AppApplication.getInstance().setLogin();

                } else {
                    Intent intent = new Intent(getActivity(), MineActivity.class);
//                    Intent intent = new Intent(getActivity(), FriendActivity.class);
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
                    Intent intent = new Intent(getActivity(), PublishActivity.class);
                    startActivity(intent);
                }

            }
        });

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
                            RongIM.getInstance().setCurrentUserInfo(new UserInfo(String.valueOf(uid),nickName,Uri.parse(headerIconStr)));
                            /**
                             * 设置消息体内是否携带用户信息。
                             * @param state 是否携带用户信息，true 携带，false 不携带。
                             */
                            RongIM.getInstance().setMessageAttachedUserInfo(true);
                            if(headerIconStr.equals("https://img.baojiawangluo.com/news/20191219160703313.jpg")){
                                if(isFirst){
                                    new AlertView("设置头像或昵称", "您还没有设置头像或昵称，请先进行修改", "取消", new String[]{"确定"}, null, getActivity(),
                                            AlertView.Style.Alert, new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            if (position == 0) {
                                                Intent intent = new Intent(getActivity(), MineActivity.class);
//                    Intent intent = new Intent(getActivity(), FriendActivity.class);
                                                intent.putExtra("uid", uid);
                                                intent.putExtra("headericon", headerIconStr);
                                                intent.putExtra("nickname", nickName);
                                                startActivity(intent);
                                            }
                                        }
                                    }).show();
                                    isFirst = false;
                                }

                            }

                        } else if (null != getMyUserInfo && getMyUserInfo.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getMyUserInfo && getMyUserInfo.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMyUserInfo.getErrmsg()) ? "数据加载失败" : getMyUserInfo.getErrmsg());
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
        getUserInfo();

    }
}
