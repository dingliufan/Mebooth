package com.mebooth.mylibrary.main.home.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaeger.library.StatusBarUtil;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.baseadapter.CommonAdapter;
import com.mebooth.mylibrary.baseadapter.MultiItemTypeAdapter;
import com.mebooth.mylibrary.baseadapter.base.ViewHolder;
import com.mebooth.mylibrary.main.adapter.DecorationGridAdapter;
import com.mebooth.mylibrary.main.base.BaseTransparentActivity;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetDecorationJson;
import com.mebooth.mylibrary.main.home.bean.GetMedalLogJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.GlideImageManager;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;
import com.mebooth.mylibrary.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DecorationActivity extends BaseTransparentActivity {
    private int uid;

    private CommonAdapter commonAdapter;
    private RecyclerView recyclerView;
    private ImageView back;
    private ImageView right;
    private TextView decorationCount;
    private ImageView decorationUserIcon;
    private TextView decorationRank;
    private TextView decorationFirst;

    private DecorationGridAdapter adapter;

    private ArrayList<GetDecorationJson.DecorationData.DecorationView.DecorationGroup> group = new ArrayList<>();
    private LinearLayout decorationHeader1;
    private NestedScrollView decorationScroll;
    private ImageView back1;
    private ImageView right1;
    private LinearLayout ll_poster;

    @Override
    protected int getContentViewId() {
        return R.layout.drcoration_layout;
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        StatusBarUtil.setLightMode(this); //黑色图标
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        super.initView();
        recyclerView = findViewById(R.id.decoration_recycle);
        back = findViewById(R.id.public_back);
        back1 = findViewById(R.id.public_back1);
        right = findViewById(R.id.public_right);
        right1 = findViewById(R.id.public_right1);
        decorationCount = findViewById(R.id.decoration_count);
        decorationUserIcon = findViewById(R.id.decoration_usericon);
        decorationRank = findViewById(R.id.decoration_rank);
        decorationFirst = findViewById(R.id.decoration_first);
        decorationHeader1 = findViewById(R.id.decorationheader1);
        decorationScroll = findViewById(R.id.decoration_scrollview);
        decorationHeader1.setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);
        findViewById(R.id.decorationheader).setPadding(0, UIUtils.getStatusBarHeight(this), 0, 0);

        decorationScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > UIUtils.getStatusBarHeight(DecorationActivity.this)) {
                    decorationHeader1.setVisibility(View.VISIBLE);
                    findViewById(R.id.decorationheader).setVisibility(View.GONE);
                } else {
                    decorationHeader1.setVisibility(View.GONE);
                    findViewById(R.id.decorationheader).setVisibility(View.VISIBLE);
                }

            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPicture(ll_poster);
            }
        });

        right1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPicture(ll_poster);
            }
        });

    }

    private void getShareDecorationInfo() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getMedalLog()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetMedalLogJson>() {
                    @Override
                    public void onNext(GetMedalLogJson getMedalLogJson) {
                        super.onNext(getMedalLogJson);

                        if (null != getMedalLogJson && getMedalLogJson.getErrno() == 0) {

                            saveImage(getMedalLogJson);

                        } else if (null != getMedalLogJson && getMedalLogJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getMedalLogJson && getMedalLogJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getMedalLogJson.getErrmsg()) ? "数据加载失败" : getMedalLogJson.getErrmsg());
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
    protected void initData() {
        super.initData();

        uid = getIntent().getIntExtra("uid", 0);
        getShareDecorationInfo();
        initRecycle();
        getDecoration();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getDecoration() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getDecorationInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetDecorationJson>() {
                    @Override
                    public void onNext(GetDecorationJson getDecorationJson) {
                        super.onNext(getDecorationJson);

                        if (null != getDecorationJson && getDecorationJson.getErrno() == 0) {

                            decorationCount.setText(getDecorationJson.getData().getView().getStats().getTotal() + "枚");
                            decorationRank.setText("前" + getDecorationJson.getData().getView().getStats().getRatio() + "%");
                            GlideImageManager.glideLoader(DecorationActivity.this, getDecorationJson.getData().getUser().getAvatar(), decorationUserIcon, GlideImageManager.TAG_ROUND);


                            group.clear();
                            group.addAll(getDecorationJson.getData().getView().getGroup());

                            if (group.size() != 0) {

                                decorationFirst.setText("- " + group.get(0).getType().getName() + " -");

                            }
                            commonAdapter.notifyDataSetChanged();

                        } else if (null != getDecorationJson && getDecorationJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getDecorationJson && getDecorationJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getDecorationJson.getErrmsg()) ? "数据加载失败" : getDecorationJson.getErrmsg());
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

    private void initRecycle() {

        commonAdapter = new CommonAdapter(this, R.layout.decoration_item, group) {
            @Override
            protected void convert(ViewHolder holder, Object o, final int position) {

                if (position == 0) {
                    holder.setVisible(R.id.decoration_itembar, View.GONE);
                } else {
                    holder.setVisible(R.id.decoration_itembar, View.VISIBLE);
                }

                holder.setText(R.id.decoration_itembar, "- " + group.get(position).getType().getName() + " -");
                adapter = new DecorationGridAdapter(DecorationActivity.this, group.get(position).getList());
//                GridView gridView = new GridView(DecorationActivity.this);
                GridView gridView = holder.getView(R.id.decoration_item_gridview);
                gridView.setFocusable(false);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    }
                });

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //TODO 订单详情

//                Intent intent = new Intent(getActivity(), OtherUserActivity.class);
//                intent.putExtra("uid", users.get(position).getUid());
//                intent.putExtra("nickname", users.get(position).getNickname());
//                getActivity().startActivity(intent);
//                RongIM.getInstance().startPrivateChat(getActivity(), "10001", "标题");

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commonAdapter);

    }

    // 1. 初始化布局：
    private void saveImage(GetMedalLogJson medalLogJson) {
        LayoutInflater from = LayoutInflater.from(this);
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        View viewById = from.inflate(R.layout.drcoration_sharelayout, null);
        layoutView(viewById, width, height);//去到指定view大小的函数
        ll_poster = viewById.findViewById(R.id.decoration_lly);
        viewById.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        viewById.layout(0, 0, viewById.getMeasuredWidth(), viewById.getMeasuredHeight());
        TextView count = viewById.findViewById(R.id.decoration_count);
        TextView rank = viewById.findViewById(R.id.decoration_rank);
        ImageView userIcon = viewById.findViewById(R.id.decoration_usericon);
        TextView decorationTime1 = viewById.findViewById(R.id.decoration_time1);
        TextView decorationTime2 = viewById.findViewById(R.id.decoration_time2);
        TextView decorationTime3 = viewById.findViewById(R.id.decoration_time3);
        TextView decorationTime4 = viewById.findViewById(R.id.decoration_time4);
        TextView decorationLine1 = viewById.findViewById(R.id.decoration_line1);
        TextView decorationLine2 = viewById.findViewById(R.id.decoration_time2);
        TextView decorationLine3 = viewById.findViewById(R.id.decoration_time3);

        count.setText(medalLogJson.getData().getStats().getTotal() + "枚");
        rank.setText("前" + medalLogJson.getData().getStats().getRatio() + "%");
        GlideImageManager.glideLoader(DecorationActivity.this, medalLogJson.getData().getUser().getAvatar(), userIcon, GlideImageManager.TAG_ROUND);


    }

    private void layoutView(View view, int width, int height) {
        // 指定整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    /**
     * 生成一张图片
     *
     * @param v
     * @return
     */
//生成图片
    public Bitmap createViewBitmap(View v) {
        //如果上面那个布局不在屏幕上找到的话，这里就会报，宽高为0的异常
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void createPicture(View view) {
        Bitmap bitmap = createViewBitmap(view);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        //3.bitmap存本地
        String strPath = "/testSaveView/" + UUID.randomUUID().toString() + ".png";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            FileOutputStream fos = null;
            try {
                File file = new File(sdCardDir, strPath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);

                //当指定压缩格式为PNG时保存下来的图片显示正常
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Log.e("MainActivity", "图片生成：" + file.getAbsolutePath());
                //当指定压缩格式为JPEG时保存下来的图片背景为黑色
//				 bitmap.compress(CompressFormat.JPEG, 100, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
