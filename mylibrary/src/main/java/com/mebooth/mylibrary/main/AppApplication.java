package com.mebooth.mylibrary.main;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mebooth.mylibrary.main.base.MeboothCallBack;
import com.mebooth.mylibrary.main.home.bean.GetRongIMTokenJson;
import com.mebooth.mylibrary.main.utils.YService;
import com.mebooth.mylibrary.net.CommonObserver;
import com.mebooth.mylibrary.net.ServiceFactory;
import com.mebooth.mylibrary.utils.ToastUtils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Request;

public abstract class AppApplication extends Application {

    private static String cookie;
    private MeboothCallBack meboothCallBack;
    public static AppApplication app;

    public MeboothCallBack getMeboothCallBack() {
        return meboothCallBack;
    }

    public void setMeboothCallBack(MeboothCallBack meboothCallBack) {
        this.meboothCallBack = meboothCallBack;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //融云
        RongIM.init(this,"8luwapkv8458l");
        Log.d("","111111");
        //获取融云token（）：
        getConnectToken();



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
    public void setLogin(){
        if(meboothCallBack != null) {
            meboothCallBack.setLogin();
        }
    };

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
