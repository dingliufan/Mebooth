package com.mebooth.mylibrary.main.home.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.R2;
import com.mebooth.mylibrary.imagepicker.ImagePicker;
import com.mebooth.mylibrary.imagepicker.bean.ImageItem;
import com.mebooth.mylibrary.imagepicker.permission.MPermission;
import com.mebooth.mylibrary.imagepicker.permission.annotation.OnMPermissionGranted;
import com.mebooth.mylibrary.imagepicker.ui.ImageGridActivity;
import com.mebooth.mylibrary.imagepicker.view.CropImageView;
import com.mebooth.mylibrary.main.adapter.MineOrderPagerAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
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
import com.mebooth.mylibrary.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
    private ImageView edit;

    private List<String> mTitles = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MeCareFragment meCareFragment = new MeCareFragment();
    private MeCollectFragment meCollectFragment = new MeCollectFragment();

    private MineOrderPagerAdapter mAdapter;

    private int PermissionType;//1。单图  2。多图片
    private final int BASIC_PERMISSION_REQUEST_CODE = 1;//单图

    private EdiitNickName ediitNickName;
    //基本权限管理
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private int uid;

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
        edit = findViewById(R.id.mine_edit);

        GlideImageManager.glideLoader(MineActivity.this, getIntent().getStringExtra("headericon"), headerIcon, GlideImageManager.TAG_ROUND);
        nickName.setText(getIntent().getStringExtra("nickname"));
        uid = getIntent().getIntExtra("uid",0);

        ediitNickName = new EdiitNickName(this,getIntent().getStringExtra("nickname"));

        mFragments.add(MePublishFragment.getInstance(uid));
        mFragments.add(meCareFragment);
        mFragments.add(meCollectFragment);
        mTitles.add("我发布的");
        mTitles.add("我的关注");
        mTitles.add("我的收藏");

        initSingleImagerPicker();

        mAdapter = new MineOrderPagerAdapter(getSupportFragmentManager(), this, mFragments, mTitles);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).select();
        TabLayoutUtil.reflex(tabLayout);

        headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBasicPermission(1);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ediitNickName.showPopupWindow();

            }
        });

    }

    private void initSingleImagerPicker() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setMultiMode(false);
//        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(width - 20);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(width - 20);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        if (PermissionType == 1) {//跳转到单图

            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, 1);
        }
    }


    private void requestBasicPermission(int type) {
        PermissionType = type;
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 1) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) getToken(images.get(0).path);
            }
        }
    }

    private void getToken(final String pathImage) {

//        GlideImageManager.glideLoader(this, pathImage, ivHeader, GlideImageManager.TAG_RECTANGLE);
        File file = new File(pathImage);
//        String path ="/image/desk/product";
//        Gson gson = new Gson();
//        JsonObject jObj = new JsonObject();
//        jObj.addProperty("name","20171108105505.jpg");
//        jObj.addProperty("path",path);

//        JsonArray jsonArray = new JsonArray();
//        jsonArray.add(jObj);
//        RequestBody bodyjson = RequestBody.create(ApiHelper.mediaType, gson.toJson(jsonArray));
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .updateRepairFile(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<UpdateHeaderFileJson>() {
                    @Override
                    public void onNext(UpdateHeaderFileJson updateHeaderFileJson) {
                        super.onNext(updateHeaderFileJson);

                        if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() == 0) {
                            updateHeaderIcon(updateHeaderFileJson.getData().get(0));

                        } else if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(updateHeaderFileJson.getErrmsg()) ? "数据加载失败" : updateHeaderFileJson.getErrmsg());
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

    private void updateHeaderIcon(final String headerUrl) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .udateHeaderIcon(headerUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PublicBean>() {
                    @Override
                    public void onNext(PublicBean publicBean) {
                        super.onNext(publicBean);

                        if (null != publicBean && publicBean.getErrno() == 0) {

                            ToastUtils.getInstance().showToast("头像上传成功");
                            GlideImageManager.glideLoader(MineActivity.this, headerUrl, headerIcon, GlideImageManager.TAG_ROUND);

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
