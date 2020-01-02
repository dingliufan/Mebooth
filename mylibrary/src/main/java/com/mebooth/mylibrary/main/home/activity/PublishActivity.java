package com.mebooth.mylibrary.main.home.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.R2;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.GridImageAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;
import com.mebooth.mylibrary.main.utils.ActivityCollectorUtil;
import com.mebooth.mylibrary.main.utils.BroadcastAction;
import com.mebooth.mylibrary.main.utils.BroadcastManager;
import com.mebooth.mylibrary.main.utils.GlideEngine;
import com.mebooth.mylibrary.main.utils.GridSpacingItemNotBothDecoration;
import com.mebooth.mylibrary.main.utils.MyLocationUtil;
import com.mebooth.mylibrary.main.utils.PictureConfig;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.FullyGridLayoutManager;
import com.mebooth.mylibrary.main.view.ToggleButton;
import com.mebooth.mylibrary.main.view.ToggleView;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PublishActivity extends BaseTransparentActivity {
    private static final String TAG = PublishActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextView publishGPS;

    private ImageView back;
    private TextView title;
    private TextView right;
    private ToggleButton newlyAddressDefault;

    private EditText content;

    private int maxCount = 9;

    private Location location;
    private List<Address> addresses = new ArrayList<>();

    private ArrayList<String> comprossImg = new ArrayList<>();
    private List<String> resultImgPath = new ArrayList<>();
    private int position = 0;
    private String imgPathStr = "";

    private int chooseMode = PictureMimeType.ofImage();

    public static List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int themeId = R.style.picture_default_style;
    ;

    private String gpsStr;
    private String reciverAddress = "";

    @Override
    protected int getContentViewId() {
        return R.layout.publish_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            publishGPS.setText((CharSequence) msg.obj);
            gpsStr = String.valueOf(msg.obj);
        }
    };

    @Override
    protected void initData() {
        super.initData();

        recyclerView = findViewById(R.id.recycler);
        publishGPS = findViewById(R.id.publish_gps);
        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        right = findViewById(R.id.public_right);
        content = findViewById(R.id.et_moment_add_content);
        newlyAddressDefault = findViewById(R.id.newlyaddress_default);
        newlyAddressDefault.setOpen(true);

        findViewById(R.id.publishheader).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);


        title.setText("此刻");

        right.setVisibility(View.VISIBLE);
        right.setText("发布");

        selectList.clear();

        reciverAddress = AppApplication.getInstance().getAddressStr();
        if(reciverAddress == null || reciverAddress.equals("")){
            publishGPS.setText("暂无法定位到位置");
            reciverAddress = "";
        }else{
            publishGPS.setText(reciverAddress);
        }

//        location = MyLocationUtil.getMyLocation();
//        try {
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//
//                    addresses = getAddress(location);
//                    Message msg = new Message();
//                    msg.obj = addresses.get(0).getAdminArea() + addresses.get(0).getFeatureName();
//                    handler.sendMessage(msg);
//                }
//            }.start();
//        } catch (Exception e) {
//
//            ToastUtils.getInstance().showToast("请查看定位是否开启");
//
//        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(content.getText().toString()) || selectList.size() == 0) {

                    ToastUtils.getInstance().showToast("请输入内容或选择图片");

                } else {

                    for (LocalMedia media : selectList) {
                        Log.i(TAG, "压缩---->" + media.getCompressPath());
                        Log.i(TAG, "原图---->" + media.getPath());

                        Log.i(TAG, "裁剪---->" + media.getCutPath());
                        comprossImg.add(media.getCompressPath());
                    }

                    getToken(comprossImg);
                }
            }
        });
        //设置是否显示地址
        newlyAddressDefault.setOnToggleListener(new ToggleView.OnToggleListener() {
            @Override
            public void onToggle(boolean isOpen) {

                if (isOpen) {

                    if(reciverAddress.equals("")){
                        publishGPS.setText("暂无法定位到位置");
                    }else{
                        publishGPS.setText(reciverAddress);
                    }


                } else {
                    publishGPS.setText("不显示位置");
                }
            }
        });

        initRecycle();
    }

    private void initRecycle() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new GridSpacingItemNotBothDecoration(4,
                ScreenUtils.dip2px(this, 8), true, false));
        adapter = new GridImageAdapter(PublishActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxCount);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
//                int mimeType = media.getMimeType();

                    // 预览图片 可自定长按保存路径
//                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
//                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
//                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
                    PictureSelector.create(PublishActivity.this)
                            .themeStyle(themeId) // xml设置主题
                            .openExternalPreview(position, selectList);
                    //.setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                    //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
//                        .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
//                        .openExternalPreview(position, selectList);
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
//            PictureFileUtils.deleteAllCacheDirFile(this);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }

        // 注册外部预览图片删除按钮回调
        BroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);
    }


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(PublishActivity.this)
                    .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(9)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .isGif(true)// 是否显示gif图片
                    .selectionMedia(selectList)// 是否传入已选图片
                    //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled(true) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    /**
                     * homeSpecialRecyclerViewAdapter  RecyclerView 的适配器
                     * recyclerview                    RecyclerView 控件
                     */
                    break;
            }
        }
    }


    private void getToken(final ArrayList<String> pathImage) {
        ArrayList<MultipartBody.Part> stringArrayList = new ArrayList<MultipartBody.Part>();
//
        for (String imgStr : pathImage) {
            File file = new File(imgStr);
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onNext(UpdateHeaderFileJson updateHeaderFileJson) {
                            super.onNext(updateHeaderFileJson);

                            if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() == 0) {
//                            imagePath.add()

                                if (comprossImg.size() != 0) {
                                    resultImgPath.add(updateHeaderFileJson.getData().get(0));
                                    if (position == comprossImg.size() - 1) {

                                        for (int i = 0; i < resultImgPath.size(); i++) {
                                            imgPathStr += "\n" + resultImgPath.get(i);
                                        }

                                        publishTopic(imgPathStr);

                                    }
                                }

                                position++;

                            } else if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() == 1101) {

                                SharedPreferencesUtils.writeString("token", "");
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

//            stringArrayList.add(body);
        }
    }

    private void publishTopic(String imgPathStr) {

        String location = "";

        if (!publishGPS.getText().toString().equals("不显示位置")) {
                location = reciverAddress;
        } else {
            location = "";
        }
        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .publishTopic(content.getText().toString(), location, imgPathStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<PublicBean>() {
                    @Override
                    public void onNext(PublicBean publicBean) {
                        super.onNext(publicBean);

                        if (null != publicBean && publicBean.getErrno() == 0) {

//                            Intent intent = new Intent(PublishActivity.this, NewMainActivity.class);
//                            startActivity(intent);
//                            ActivityCollectorUtil.finishAllActivity();
                            finish();
                            ToastUtils.getInstance().showToast("您的帖子已发布，审核通过后就可以与大家见面了");

                        } else if (null != publicBean && publicBean.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras;
            switch (action) {
                case BroadcastAction.ACTION_DELETE_PREVIEW_POSITION:
                    // 外部预览删除按钮回调
                    extras = intent.getExtras();
                    int position = extras.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION);
                    ToastUtils.getInstance().showToast("delete image index:" + position);
                    if (position < adapter.getItemCount()) {
                        selectList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            BroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver,
                    BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);
        }
    }
}
