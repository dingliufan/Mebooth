package com.mebooth.mylibrary.main;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.base.MeboothCallBack;
import com.mebooth.mylibrary.main.home.bean.GetRongIMTokenJson;
import com.mebooth.mylibrary.main.home.bean.UserTokenJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;
import com.mebooth.mylibrary.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import okhttp3.Request;

public abstract class AppApplication extends Application {

    private static String cookie;
    private MeboothCallBack meboothCallBack;
    public static AppApplication app;
    public String userid;

    private UserTokenJson userTokenJson;
    public boolean isFirst = true;

    public UserTokenJson getUserTokenJson() {
        return userTokenJson;
    }

    //是否显示返回按钮
    private boolean isShowBack;

    private TextMessage rongMsg;

    public boolean isShowBack() {
        return isShowBack;
    }

    public void setShowBack(boolean showBack) {
        isShowBack = showBack;
    }

    public void setUserTokenJson(UserTokenJson userTokenJson) {
        this.userTokenJson = userTokenJson;
        userid = userTokenJson.getUserid();
        Gson gson = new Gson();

        String msg = gson.toJson(userTokenJson);
        SharedPreferencesUtils.writeString("token", msg);
        //获取融云token（）：
        getConnectToken();
    }

    public MeboothCallBack getMeboothCallBack() {
        return meboothCallBack;
    }

    public void setMeboothCallBack(MeboothCallBack meboothCallBack) {
        this.meboothCallBack = meboothCallBack;
    }

    //传回定位地址
    private String addressStr;

    public String getAddressStr() {
        return addressStr;
    }

    public void setAddressStr(String addressStr) {
        this.addressStr = addressStr;
    }


    public void setLogOut(boolean isLogOut) {
        if (isLogOut) {

            SharedPreferencesUtils.writeString("token", "");

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        //融云测试
//        RongIM.init(this, "8luwapkv8458l");
        //融云线上
        RongIM.init(this, "8brlm7uf8qp83");

        final RongIM.MessageInterceptor messageInterceptor = new RongIM.MessageInterceptor() {
            @Override
            public boolean intercept(Message message) {
                return true;
            }
        };
        /**
         * 设置接收消息的监听器。
         *
         * 所有接收到的消息、通知、状态都经由此处设置的监听器处理。包括私聊消息、群组消息、聊天室消息以及各种状态。
         */
        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageWrapperListener() {
            @Override
            public boolean onReceived(final Message message, final int left, boolean hasPackage, boolean offline) {

                if (message.getSenderUserId().equals("12358336")) {

                    messageInterceptor.intercept(message);

                }


                return false;
            }
        });

    }

    public static AppApplication getInstance() {
        return app;
    }

    private void getConnectToken() {

        ServiceFactory.getNewInstance()
                .createService(YService.class)
                .getRongTokenInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonObserver<GetRongIMTokenJson>() {
                    @Override
                    public void onNext(GetRongIMTokenJson getRongIMTokenJson) {
                        super.onNext(getRongIMTokenJson);

                        if (null != getRongIMTokenJson && getRongIMTokenJson.getErrno() == 0) {

                            connect(getRongIMTokenJson.getData().getToken());

                        } else if (null != getRongIMTokenJson && getRongIMTokenJson.getErrno() == 1101) {

                            SharedPreferencesUtils.writeString("token", "");
                        } else if (null != getRongIMTokenJson && getRongIMTokenJson.getErrno() != 200) {

                            ToastUtils.getInstance().showToast(TextUtils.isEmpty(getRongIMTokenJson.getErrmsg()) ? "数据加载失败" : getRongIMTokenJson.getErrmsg());
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

    private void connect(String rongToken) {

        RongIM.connect(rongToken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                getConnectToken();
            }

            @Override
            public void onSuccess(String userid) {
                Log.d("TAG", "--onSuccess" + userid);
//                ToastUtils.getInstance().showToast("已连接融云");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("TAG", "--onSuccess" + errorCode);
                ToastUtils.getInstance().showToast("连接融云失败");
            }
        });

    }

    //
//    @Override
//    public Request.Builder addOkHttpAddHeader(Request.Builder builder) {
//        if (cookie != null) {
//            return builder.addHeader("Cookie", cookie);
//        }
//
//        return null;
//    }
    //登陆
    public void setLogin() {
        if (meboothCallBack != null) {
            meboothCallBack.setLogin();
        }
    }

    ;

    //分享
    public void setShare(String way, String url, Bitmap image, String title, String description) {
        if (meboothCallBack != null) {
            meboothCallBack.setShare(way, url, image, title, description);
        }
    }

    ;

    @Override
    public File getCacheDir() {
        //缓存路径
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cacheDir = getExternalCacheDir();
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir;
            }
        }
        return super.getCacheDir();
    }

    public abstract Request.Builder addOkHttpAddHeader(Request.Builder builder);
}
