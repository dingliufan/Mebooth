package com.mebooth.mylibrary.main.home.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.imagepicker.ImagePicker;
import com.mebooth.mylibrary.imagepicker.bean.ImageItem;
import com.mebooth.mylibrary.imagepicker.permission.MPermission;
import com.mebooth.mylibrary.imagepicker.permission.annotation.OnMPermissionGranted;
import com.mebooth.mylibrary.imagepicker.ui.ImageGridActivity;
import com.mebooth.mylibrary.imagepicker.view.CropImageView;
import com.mebooth.mylibrary.main.adapter.MineOrderPagerAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetMineCountJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;
import com.mebooth.mylibrary.main.home.fragment.MeCareFragment;
import com.mebooth.mylibrary.main.home.fragment.MeCollectFragment;
import com.mebooth.mylibrary.main.home.fragment.MePublishFragment;
import com.mebooth.mylibrary.main.utils.GlideLoader;
import com.mebooth.mylibrary.main.utils.TabLayoutUtil;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.EdiitNickName;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MineActivity extends BaseTransparentActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ImageView headerIcon;
    private TextView nickName;
    private TextView edit;

    private List<String> mTitles = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MeCareFragment meCareFragment = new MeCareFragment();
    private MeCollectFragment meCollectFragment = new MeCollectFragment();

    private MineOrderPagerAdapter mAdapter;


    private int uid;
    private ImageView back;
    private TextView title;

    private LinearLayout otherUserMedal;
    private ImageView otherUserMedal1;
    private ImageView otherUserMedal2;
    private ImageView otherUserMedal3;
    private ImageView otherUserMedal4;
    private ImageView otherUserMedal5;
    private ImageView otherUserMedal6;
    private TextView otherUserMedalCount;

    @Override
    protected int getContentViewId() {
        return R.layout.mine_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        headerIcon = findViewById(R.id.mine_headericon);
        nickName = findViewById(R.id.mine_nickname);
        edit = findViewById(R.id.mine_editor);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        otherUserMedal = findViewById(R.id.otheruser_medal);
        otherUserMedal1 = findViewById(R.id.otheruser_medal_1);
        otherUserMedal2 = findViewById(R.id.otheruser_medal_2);
        otherUserMedal3 = findViewById(R.id.otheruser_medal_3);
        otherUserMedal4 = findViewById(R.id.otheruser_medal_4);
        otherUserMedal5 = findViewById(R.id.otheruser_medal_5);
        otherUserMedal6 = findViewById(R.id.otheruser_medal_6);
        otherUserMedalCount = findViewById(R.id.otheruser_medal_count);

        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);


        GlideImageManager.glideLoader(MineActivity.this, getIntent().getStringExtra("headericon"), headerIcon, GlideImageManager.TAG_ROUND);
        nickName.setText(getIntent().getStringExtra("nickname"));
        uid = getIntent().getIntExtra("uid", 0);

        mFragments.add(MePublishFragment.getInstance(uid));
        mFragments.add(meCareFragment);
        mFragments.add(meCollectFragment);

        getCountInfo();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MineActivity.this, EditorActiviy.class);
                intent.putExtra("icon", getIntent().getStringExtra("headericon"));
                intent.putExtra("nickname", getIntent().getStringExtra("nickname"));
                startActivityForResult(intent, 1);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("个人中心");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 3) {
            String result = data.getStringExtra("name");
            String url = data.getStringExtra("headerurl");
            nickName.setText(result);
            if (!url.equals("")) {

                GlideImageManager.glideLoader(MineActivity.this, url, headerIcon, GlideImageManager.TAG_ROUND);

            }
        }
    }

    private void getCountInfo() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getMineCountInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMineCountJson>() {
                    @Override
                    public void onNext(GetMineCountJson getMineCountJson) {
                        super.onNext(getMineCountJson);

                        if (null != getMineCountJson && getMineCountJson.getErrno() == 0) {

                            mTitles.add(getMineCountJson.getData().getStats().getTopic() + "\n我发布的");
                            mTitles.add(getMineCountJson.getData().getStats().getFollowing() + "\n我的关注");
                            mTitles.add(getMineCountJson.getData().getStats().getPraise() + "\n我的收藏");


                            mAdapter = new MineOrderPagerAdapter(getSupportFragmentManager(), MineActivity.this, mFragments, mTitles);
                            viewPager.setAdapter(mAdapter);
                            tabLayout.setupWithViewPager(viewPager);
                            tabLayout.setSelectedTabIndicatorHeight(0);
                            tabLayout.getTabAt(0).select();
                            TabLayoutUtil.reflex(tabLayout);

                            LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
                            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                            linearLayout.setDividerDrawable(ContextCompat.getDrawable(MineActivity.this, R.drawable.tablayout_divider_vertical));
                            linearLayout.setDividerPadding(UIUtils.dp2px(MineActivity.this, 15));

                            if (getMineCountJson.getData().getUser().getMedals().size() == 0) {
                                otherUserMedal1.setVisibility(View.GONE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("0枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 1) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.GONE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("1枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 2) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.GONE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("2枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 3) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.GONE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("3枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 4) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.GONE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("4枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() == 5) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.VISIBLE);
                                otherUserMedal6.setVisibility(View.GONE);
                                otherUserMedalCount.setText("5枚勋章");
                            } else if (getMineCountJson.getData().getUser().getMedals().size() >= 6) {
                                otherUserMedal1.setVisibility(View.VISIBLE);
                                otherUserMedal2.setVisibility(View.VISIBLE);
                                otherUserMedal3.setVisibility(View.VISIBLE);
                                otherUserMedal4.setVisibility(View.VISIBLE);
                                otherUserMedal5.setVisibility(View.VISIBLE);
                                otherUserMedal6.setVisibility(View.VISIBLE);
                                otherUserMedalCount.setText(getMineCountJson.getData().getUser().getMedals().size() + "枚勋章");
                            }

                        } else if (null != getMineCountJson && getMineCountJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getMineCountJson && getMineCountJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMineCountJson.getErrmsg()) ? "数据加载失败" : getMineCountJson.getErrmsg());
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
