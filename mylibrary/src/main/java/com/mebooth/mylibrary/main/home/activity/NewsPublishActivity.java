package com.mebooth.mylibrary.main.home.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.NewsPublishAdapter.PublishAdapter;
import com.mebooth.mylibrary.main.NewsPublishAdapter.PublishHeaderAdapter;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewOne;
import com.mebooth.mylibrary.main.NowMultiItemView.NowItemVIewZero;
import com.mebooth.mylibrary.main.adapter.GridImageAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.entity.SimpleItemTouchCallBack;
import com.mebooth.mylibrary.main.entity.TouchCallBack;
import com.mebooth.mylibrary.main.home.bean.NewPublish;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;
import com.mebooth.mylibrary.main.utils.GlideEngine;
import com.mebooth.mylibrary.main.utils.NoPublish;
import com.mebooth.mylibrary.main.utils.PictureConfig;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.main.view.GloriousRecyclerView;
import com.mebooth.mylibrary.main.view.ToggleButton;
import com.mebooth.mylibrary.main.view.ToggleView;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.StringUtil;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.mebooth.mylibrary.main.NewsPublishAdapter.PublishHeaderAdapter.publishNewsTitle;

public class NewsPublishActivity extends BaseTransparentActivity {

    private ArrayList<String> comprossImg = new ArrayList<>();
    private List<String> resultImgPath = new ArrayList<>();
    private int position = 0;
    private String imgPathStr = "";

    private int chooseMode = PictureMimeType.ofImage();
    public static List<LocalMedia> selectList = new ArrayList<>();
    public static List<LocalMedia> selectList1 = new ArrayList<>();
    private int themeId = R.style.picture_default_style;
    //    private GloriousRecyclerView recycleView;
    private RecyclerView recycleView;
    private MultiItemTypeAdapter commonAdapter;

    public static ArrayList<NewPublish> newPublishesList = new ArrayList<>();
    private ImageView addText;
    private ImageView addImg;
    private final int CHOOSE_REQUEST1 = 189;

    private boolean isSending = true;
    private String fengMian = "";
    private TextView right;
    private ImageView back;
    private LinearLayout addLly;
    private ToggleButton newlyAddressDefault;
    private String reciverAddress = "";
    private TextView publishGPS;

    @Override
    protected int getContentViewId() {
        return R.layout.newspublish_layout;
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


        recycleView = findViewById(R.id.classify_recycle);
        addText = findViewById(R.id.newspublish_addtext);
        addImg = findViewById(R.id.newspublish_addimg);
        publishGPS = findViewById(R.id.publish_gps);
        right = findViewById(R.id.public_right);
        back = findViewById(R.id.public_back);
        addLly = findViewById(R.id.newspublishadd_lly);
        newlyAddressDefault = findViewById(R.id.newlyaddress_default);
        newlyAddressDefault.setOpen(true);

// 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
//            PictureFileUtils.deleteAllCacheDirFile(this);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }

//        findViewById(R.id.newspublishheader).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        selectList.clear();
        selectList1.clear();
        newPublishesList.clear();
        newPublishesList.add(new NewPublish());
        publishNewsTitle = "";
        initRecycle();

        reciverAddress = AppApplication.getInstance().getAddressStr();
        if (reciverAddress == null || reciverAddress.equals("")) {
            publishGPS.setText("暂无法定位到位置");
            reciverAddress = "";
        } else {
            publishGPS.setText(reciverAddress);
        }

        //设置是否显示地址
        newlyAddressDefault.setOnToggleListener(new ToggleView.OnToggleListener() {
            @Override
            public void onToggle(boolean isOpen) {

                if (isOpen) {

                    if (reciverAddress.equals("")) {
                        publishGPS.setText("暂无法定位到位置");
                    } else {
                        publishGPS.setText(reciverAddress);
                    }

                } else {
                    publishGPS.setText("不显示位置");
                }
            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPublish newPublish = new NewPublish();
                newPublish.setType("text");
                newPublish.setContent("");
                newPublishesList.add(newPublish);

//                mHandler.sendEmptyMessageDelayed(1, 1000);
                commonAdapter.notifyDataSetChanged();
                recycleView.smoothScrollToPosition(commonAdapter.getItemCount() - 1);


                // 拖拽移动和左滑删除
//                SimpleItemTouchCallBack callback = new SimpleItemTouchCallBack(commonAdapter,newPublishesList);
//                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//                itemTouchHelper.attachToRecyclerView(recycleView);

                //
//                SimpleItemTouchCallBack simpleItemTouchCallBack = new SimpleItemTouchCallBack(touchCallBack, newPublishesList.size());
//                // 要实现侧滑删除条目，把 false 改成 true 就可以了
//                simpleItemTouchCallBack.setmSwipeEnable(false);
//                ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallBack);
//                helper.attachToRecyclerView(recycleView);

            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPicClickListener1.onAddPicClick();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                publishNews();

            }
        });
    }

    private void publishNews() {

//        newPublishesList
        if (StringUtil.isEmpty(fengMian)) {
            ToastUtils.getInstance().showToast("请选择封面");
            return;
        } else if (StringUtil.isEmpty(publishNewsTitle)) {
            ToastUtils.getInstance().showToast("请输入标题");
            return;
        } else if (newPublishesList.size() <= 1) {
            ToastUtils.getInstance().showToast("内容不能为空");
            return;
        } else {
            if (isSending) {
                isSending = false;
                right.setTextColor(getResources().getColor(R.color.bg_999999));
                String content = "";
                for (int i = 0; i < newPublishesList.size() - 1; i++) {

                    if (newPublishesList.get(i + 1).getType().equals("text")) {
                        String text = newPublishesList.get(i + 1).getContent();

                        String lines[] = text.split("\n");

                        String multi_text = "";
                        for (int m = 0; m < lines.length; m++) {
                            if (m == lines.length - 1) {

                                multi_text += lines[m];
                            } else {
                                multi_text += lines[m] + "\\n";
                            }

                        }
                        content += "[text]" + multi_text + "[/text]\n";
                    } else if (newPublishesList.get(i + 1).getType().equals("image")) {
                        content += "[image]" + newPublishesList.get(i + 1).getContent() + "[/image]\n";
                    } else if (newPublishesList.get(i + 1).getType().equals("video")) {
                        content += "[video]" + newPublishesList.get(i + 1).getContent() + "[/video]\n";
                    }

                }

                String location = "";

                if (!publishGPS.getText().toString().equals("不显示位置")) {
                    location = reciverAddress;
                } else {
                    location = "";
                }

                ServiceFactory.getNewInstance()
                        .createService(YService.class)
                        .publishNews(publishNewsTitle, fengMian, content, location)
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
                                    ToastUtils.getInstance().showToast("您的资讯已发布，审核通过后就可以与大家见面了");

                                } else if (null != publicBean && publicBean.getErrno() == 1101) {
                                    isSending = true;
                                    right.setTextColor(getResources().getColor(R.color.bg_E73828));
                                    SharedPreferencesUtils.writeString("token", "");
                                } else if (null != publicBean && publicBean.getErrno() != 200) {
                                    isSending = true;
                                    right.setTextColor(getResources().getColor(R.color.bg_E73828));
                                    ToastUtils.getInstance().showToast(TextUtils.isEmpty(publicBean.getErrmsg()) ? "数据加载失败" : publicBean.getErrmsg());
                                } else {
                                    isSending = true;
                                    right.setTextColor(getResources().getColor(R.color.bg_E73828));
                                    ToastUtils.getInstance().showToast("数据加载失败");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                isSending = true;
                                right.setTextColor(getResources().getColor(R.color.bg_E73828));
                                ToastUtils.getInstance().showToast("数据加载失败");
                            }
                        });
            }

        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(NewsPublishActivity.this)
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

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener1 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(NewsPublishActivity.this)
                    .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .imageSpanCount(4)// 每行显示个数
//                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
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
                    .selectionMedia(selectList1)// 是否传入已选图片
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
                    .forResult(CHOOSE_REQUEST1);//结果回调onActivityResult code

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
//                    GlideImageManager.glideLoader(NewsPublishActivity.this, selectList.get(0).getPath(), cover, GlideImageManager.TAG_RECTANGLE);
//                    Log.d("TAG",selectList.get(0).getCutPath());
                    getToken(selectList.get(0).getCompressPath(), "fengmian");
                    commonAdapter.notifyDataSetChanged();
                    /**
                     * homeSpecialRecyclerViewAdapter  RecyclerView 的适配器
                     * recyclerview                    RecyclerView 控件
                     */
                    break;

                case CHOOSE_REQUEST1:
                    // 图片选择结果回调
                    selectList1 = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    getToken(selectList1.get(0).getCompressPath(), "item");

                    break;
            }
        }
    }

    private void getToken(final String pathImage, final String index) {
//
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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onNext(UpdateHeaderFileJson updateHeaderFileJson) {
                        super.onNext(updateHeaderFileJson);

                        if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() == 0) {
//                            imagePath.add()

                            if (index.equals("fengmian")) {
                                fengMian = updateHeaderFileJson.getData().get(0);
                            } else {
                                NewPublish newPublish = new NewPublish();
                                newPublish.setType("image");
                                newPublish.setContent(updateHeaderFileJson.getData().get(0));
                                newPublishesList.add(newPublish);
                                commonAdapter.notifyDataSetChanged();
                                selectList1.clear();

                                recycleView.smoothScrollToPosition(commonAdapter.getItemCount() - 1);


                            }

                        } else if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() == 1101) {
                            isSending = true;
                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != updateHeaderFileJson && updateHeaderFileJson.getErrno() != 200) {
                            isSending = true;
                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(updateHeaderFileJson.getErrmsg()) ? "数据加载失败" : updateHeaderFileJson.getErrmsg());
                        } else {
                            isSending = true;
                            ToastUtils.getInstance().showToast("数据加载失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        isSending = true;
                        ToastUtils.getInstance().showToast("数据加载失败");
                    }
                });

//            stringArrayList.add(body);
    }

    private void initRecycle() {


        NoPublish noPublishinterface = new NoPublish() {
            @Override
            public void isPublish() {

//                getIsShowPublish();

            }

            @Override
            public void isCollect() {

            }

            @Override
            public void showAddButton() {
                addLly.setVisibility(View.VISIBLE);

            }
        };

        commonAdapter = new MultiItemTypeAdapter(NewsPublishActivity.this, newPublishesList);
        commonAdapter.addItemViewDelegate(new PublishHeaderAdapter(NewsPublishActivity.this, onAddPicClickListener, noPublishinterface));
        commonAdapter.addItemViewDelegate(new PublishAdapter(NewsPublishActivity.this, commonAdapter, noPublishinterface));

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(commonAdapter);
//        recycleView.addHeaderView(header);


    }

}
