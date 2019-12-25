package com.mebooth.mylibrary.main.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.adapter.SharedAdapter;
import com.mebooth.mylibrary.main.home.bean.GetShareInfoJson;
import com.mebooth.mylibrary.main.utils.ActivityCollectorUtil;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import razerdp.basepopup.BasePopupWindow;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SharedActivity extends BasePopupWindow {


    private GridView gridShared;
    private TextView sharedCancel;
    private ArrayList<String> sharedStr = new ArrayList<>();
    private ArrayList<Integer> imgShared = new ArrayList<>();
    private SharedAdapter adapter;
    private Context context;
    private ProgressDialog dialog;
    private int relateid;
    private String scene;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    private Bitmap bitmap;

    public SharedActivity(Context context, int relateid, String scene) {
        super(context);
        this.context = context;
        this.relateid = relateid;
        this.scene = scene;
        setPopupGravity(Gravity.BOTTOM);
        bindEvent();
    }

    private void bindEvent() {

        myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

        dialog = new ProgressDialog(context);
        gridShared = (GridView) findViewById(R.id.grid_shared);
        sharedCancel = (TextView) findViewById(R.id.shared_cancel);

        sharedStr.add("微信好友");
        sharedStr.add("朋友圈");
//        sharedStr.add("QQ好友");
//        sharedStr.add("QQ空间");
//        sharedStr.add("新浪微博");
//        sharedStr.add("复制链接");

        imgShared.add(R.drawable.sharewechat);
        imgShared.add(R.drawable.sharefriends);
//        imgShared.add(R.drawable.qqfriend);
//        imgShared.add(R.drawable.qqkongjian);
//        imgShared.add(R.drawable.shareweibo);
//        imgShared.add(R.drawable.copyherf);

        adapter = new SharedAdapter(context, sharedStr, imgShared);
        gridShared.setAdapter(adapter);

        sharedCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        gridShared.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    getShareInfo("wechat_friend");

                } else if (position == 1) {
                    getShareInfo("wechat_timeline");
                }
//                else if (position == 2) {
////                    copy();
//                    getShareInfo("weibo");
////                    ToastUtils.getInstance().showToast("即将开放");
//                }
            }
        });
    }

    private void getShareInfo(final String type) {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getShareInfo(scene, relateid, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetShareInfoJson>() {
                    @Override
                    public void onNext(GetShareInfoJson getShareInfoJson) {
                        super.onNext(getShareInfoJson);

                        if (null != getShareInfoJson && getShareInfoJson.getErrno() == 0) {

                            if (type.equals("wechat_friend")) {
                                AppApplication.getInstance().setShare("WEIXIN", getShareInfoJson.getData().getShare_info().getUrl(), getShareInfoJson.getData().getShare_info().getImage(), getShareInfoJson.getData().getShare_info().getTitle(), getShareInfoJson.getData().getShare_info().getDescription());
                            } else if (type.equals("wechat_timeline")) {
                                AppApplication.getInstance().setShare("WEIXIN_CIRCLE", getShareInfoJson.getData().getShare_info().getUrl(), getShareInfoJson.getData().getShare_info().getImage(), getShareInfoJson.getData().getShare_info().getTitle(), getShareInfoJson.getData().getShare_info().getDescription());
                            }
//                            else if (type.equals("weibo")) {
//                                AppApplication.getInstance().setShare("SINA",getShareInfoJson.getData().getShare_info().getUrl(),getShareInfoJson.getData().getShare_info().getImage(),getShareInfoJson.getData().getShare_info().getTitle(), getShareInfoJson.getData().getShare_info().getDescription());
//
//                            }

                        } else if (null != getShareInfoJson && getShareInfoJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getShareInfoJson && getShareInfoJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getShareInfoJson.getErrmsg()) ? "数据加载失败" : getShareInfoJson.getErrmsg());
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

//    @SuppressLint("NewApi")
//    public void copy() {
//        myClip = ClipData.newPlainText("text", url);
//        myClipboard.setPrimaryClip(myClip);
//        Toast.makeText(context, "链接已复制到剪贴板",
//                Toast.LENGTH_SHORT).show();
//    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.shared_layout);
    }


}
