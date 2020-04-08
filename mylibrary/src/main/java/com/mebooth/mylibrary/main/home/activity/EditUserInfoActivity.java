package com.mebooth.mylibrary.main.home.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.imagepicker.ImagePicker;
import com.mebooth.mylibrary.imagepicker.bean.ImageItem;
import com.mebooth.mylibrary.imagepicker.permission.MPermission;
import com.mebooth.mylibrary.imagepicker.permission.annotation.OnMPermissionGranted;
import com.mebooth.mylibrary.imagepicker.ui.ImageGridActivity;
import com.mebooth.mylibrary.imagepicker.view.CropImageView;
import com.mebooth.mylibrary.main.adapter.GridImageAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;
import com.mebooth.mylibrary.main.utils.GlideEngine;
import com.mebooth.mylibrary.main.utils.GlideLoader;
import com.mebooth.mylibrary.main.utils.PictureConfig;
import com.mebooth.mylibrary.main.utils.ResourcseMessage;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.RoundedCornersTransformation;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.MainActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.mebooth.mylibrary.main.home.activity.UserCarCityActivity.chooseCity;

public class EditUserInfoActivity extends BaseTransparentActivity {
    private TextView city;
    private ImageView headerIcon;
    private EditText nickName;
    private ImageView nickNameDelete;
    private EditText autoGraph;
    private ImageView autoGraphDelete;
    private RadioGroup sexRadioGroup;
    private RadioButton sexRadioButtonMan;
    private RadioButton sexRadioButtonWoMan;

    private String headerIconStr;
    private String nickNameStr;
    private String autoGraphStr;
    private String sexStr = "";
    private String cityStr;
    private ImageView back;
    private TextView title;
    private TextView right;

    private int chooseMode = PictureMimeType.ofImage();
    public List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.edituserinfo_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }

    @Override
    protected void initView() {
        super.initView();

        city = findViewById(R.id.edituserinfo_city);
        headerIcon = findViewById(R.id.edituserinfo_headericon);
        nickName = findViewById(R.id.edituserinfo_nickname);
        nickNameDelete = findViewById(R.id.edituserinfo_nickname_delete);
        autoGraph = findViewById(R.id.edituserinfo_autograph);
        autoGraphDelete = findViewById(R.id.edituserinfo_autograph_delete);
        sexRadioGroup = findViewById(R.id.edituserinfo_rg);
        sexRadioButtonMan = findViewById(R.id.edituserinfo_rb_man);
        sexRadioButtonWoMan = findViewById(R.id.edituserinfo_rb_woman);

        sexRadioButtonMan.setButtonDrawable(ResourcseMessage.getEditMineSexBg());
        sexRadioButtonWoMan.setButtonDrawable(ResourcseMessage.getEditMineSexBg());

        back = findViewById(R.id.public_back);
        title = findViewById(R.id.public_title);
        title.setText("个人信息");
        right = findViewById(R.id.public_right);
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        right.setTextColor(getResources().getColor(R.color.bg_E73828));

        findViewById(R.id.public_header).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        headerIconStr = getIntent().getStringExtra("headericon");
        nickNameStr = getIntent().getStringExtra("nickname");
        autoGraphStr = getIntent().getStringExtra("autograph");
        sexStr = getIntent().getStringExtra("sex");
        cityStr = getIntent().getStringExtra("city");

        GlideImageManager.glideLoader(EditUserInfoActivity.this, headerIconStr, headerIcon, GlideImageManager.TAG_ROUND);

        nickName.setText(nickNameStr);
        autoGraph.setText(autoGraphStr);

        if (sexStr.equals("男")) {
            sexRadioButtonMan.setChecked(true);
        } else if (sexStr.equals("女")) {
            sexRadioButtonWoMan.setChecked(true);
        }

        city.setText(cityStr);

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
//            PictureFileUtils.deleteAllCacheDirFile(this);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }


    }

    @Override
    protected void initListener() {
        super.initListener();

        sexRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.edituserinfo_rb_man) {

                    sexStr = "男";

                } else if (checkedId == R.id.edituserinfo_rb_woman) {
                    sexStr = "女";
                }

            }
        });
        //昵称后删除按钮
        nickNameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName.setText("");
            }
        });
        //简介后删除按钮
        autoGraphDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoGraph.setText("");
            }
        });

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditUserInfoActivity.this, UserCarCityActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        headerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPicClickListener.onAddPicClick();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sexRadioButtonMan.isChecked()){

                    sexStr = "男";

                }
                if(sexRadioButtonWoMan.isChecked()){
                    sexStr = "女";
                }


                if (nickName.getText().toString().isEmpty()) {

                    ToastUtils.getInstance().showToast("请输入您的昵称");

                } else {
                    ServiceFactory.getNewInstance()
                            .createService(YService.class)
                            .setUserInfo(headerIconStr, nickName.getText().toString(), sexStr, city.getText().toString(), autoGraph.getText().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CommonObserver<PublicBean>() {
                                @Override
                                public void onNext(PublicBean publicBean) {
                                    super.onNext(publicBean);

                                    if (null != publicBean && publicBean.getErrno() == 0) {

                                        ToastUtils.getInstance().showToast("保存成功");
                                        Intent intent = new Intent();
                                        setResult(2, intent);
                                        finish();

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

            }
        });

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(EditUserInfoActivity.this)
                    .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
//                    .maxSelectNum(1)// 最大图片选择数量
//                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
//                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .withAspectRatio(1, 1)
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(true)// 是否裁剪
                    .circleDimmedLayer(true)//是否圆形裁剪
//                    .cutOutQuality(150)
                    .compress(true)// 是否压缩
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .isGif(true)// 是否显示gif图片
                    .selectionMedia(selectList)// 是否传入已选图片
                    .isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
//                    .cropCompressQuality(200)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
//                    .cropWH(360,360)// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    .rotateEnabled(false) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
//                    GlideImageManager.glideLoader(NewsPublishActivity.this, selectList.get(0).getPath(), cover, GlideImageManager.TAG_RECTANGLE);
//                    Log.d("TAG",selectList.get(0).getCutPath());
                    getToken(selectList.get(0).getCompressPath());
                    /**
                     * homeSpecialRecyclerViewAdapter  RecyclerView 的适配器
                     * recyclerview                    RecyclerView 控件
                     */
                    break;

            }
        }
//
//        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
//            if (data != null && requestCode == 1) {
//                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                if (images.size() > 0) getToken(images.get(0).path);
//            }
//        }
    }

    private void getToken(final String pathImage) {

//        GlideImageManager.glideLoader(this, pathImage, ivHeader, GlideImageManager.TAG_RECTANGLE);
        File file = new File(pathImage);
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

                            headerIconStr = updateHeaderFileJson.getData().get(0);
                            GlideImageManager.glideLoader(EditUserInfoActivity.this, headerIconStr, headerIcon, GlideImageManager.TAG_ROUND);


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

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null == chooseCity || chooseCity.isEmpty()) {

        } else {

            city.setText(chooseCity);

        }

    }
}
