package com.mebooth.text;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.base.MeboothCallBack;
import com.mebooth.mylibrary.main.home.bean.UserTokenJson;

import java.lang.reflect.Method;

import okhttp3.Request;


public class Application extends AppApplication {

    public static Application app;
    private static String cookie;
    private AppApplication appApplication = getInstance();


    @Override
    public void onCreate() {
        super.onCreate();
        //传递用户信息
        UserTokenJson userTokenJson = new UserTokenJson();
        //冬shan
        userTokenJson.setUserid("13009809");
//        userTokenJson.setUserid("13122934");
        //我的
//        userTokenJson.setUserid("12282731");
        userTokenJson.setFrom("14");
        //东珊
        userTokenJson.setToken("c2zmlZrtaegNbhG1");
        //我的
//        userTokenJson.setToken("HalTTthItkVmoLnh");
        userTokenJson.setVersion("4.8.0");
        this.setUserTokenJson(userTokenJson);
        this.setShowBack(false);

        if (appApplication != null){
            appApplication.onCreate();//用于执行module的一些自定义初始化操作999
        }
        MeboothCallBack meboothCallBack = new MeboothCallBack() {
            @Override
            public void setLogin() {

                Log.d("AppApplication","-------------收到点击事件了");

            }

            @Override
            public void setIntentScan(Activity activity) {

                Intent intent = new Intent(activity,MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void setShare(String way, String url, Bitmap imgoricon, String title, String description) {




            }


        };
        this.setMeboothCallBack(meboothCallBack);

    }

    @Override
    public Request.Builder addOkHttpAddHeader(Request.Builder builder) {
        if (cookie != null) {
            return builder.addHeader("Cookie", cookie);
        }

        return null;
    }
//
//    @Override
//    public Request.Builder addOkHttpAddHeader(Request.Builder builder) {
//        return null;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        appApplication = getModuleApplicationInstance(this);
        try {
            //通过反射调用moduleApplication的attach方法
            Method method = Application.class.getDeclaredMethod("attach", Context.class);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(appApplication, getBaseContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //映射获取ModuleApplication
    private AppApplication getModuleApplicationInstance(Context paramContext) {
        try {
            if (appApplication == null) {
                ClassLoader classLoader = paramContext.getClassLoader();
                if (classLoader != null) {
                    Class<?> mClass = classLoader.loadClass(AppApplication.class.getName());
                    if (mClass != null)
                        appApplication = AppApplication.getInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appApplication;
    }
}
