package com.mebooth.mylibrary.net;

import android.util.Log;

import com.google.gson.Gson;
import com.mebooth.mylibrary.net.converter.LenientGsonConverterFactory;
import com.mebooth.mylibrary.net.netutils.OkHttpProvider;

import java.lang.reflect.Field;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


public class ServiceFactory {

    private OkHttpClient mOkHttpClient;

    public ServiceFactory() {
        mOkHttpClient = OkHttpProvider.getDefaultOkHttpClient();
    }

    private static ServiceFactory INSTANCE;


    public static ServiceFactory getNoCacheInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceFactory();
        }
        INSTANCE.mOkHttpClient = OkHttpProvider.getNoCacheOkHttpClient();
        return INSTANCE;
    }

    public static ServiceFactory  getNewInstance(){

        if (INSTANCE == null) {
            INSTANCE = new ServiceFactory();
        }
        INSTANCE.mOkHttpClient = OkHttpProvider.getNewOkHttpClient();
        return INSTANCE;
    }

    public  <S> S createService(Class<S> serviceClass) {
        String baseUrl = "";
        try {
            Field field1 = serviceClass.getField("BASE_URL");
            baseUrl = (String) field1.get(serviceClass);
        } catch (NoSuchFieldException e) {
            if(null != e)
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            if(null != e){
                e.getMessage();
                e.printStackTrace();
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }


}
