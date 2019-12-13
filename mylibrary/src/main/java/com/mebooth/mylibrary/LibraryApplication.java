package com.mebooth.mylibrary;

import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.io.File;

import okhttp3.Request;

public abstract class LibraryApplication extends MultiDexApplication {


    private static LibraryApplication mInstance;

    public static LibraryApplication getInstance() {

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        MultiDex.install(this);
    }

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
