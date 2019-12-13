package com.mebooth.mylibrary.net.netutils;

import android.text.TextUtils;

import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpProvider {

    private final static long DEFAULT_TIMEOUT = 10;

    public static OkHttpClient getDefaultOkHttpClient() {
        return getOkHttpClient(new CacheControlInterceptor());
    }

    public static OkHttpClient getNoCacheOkHttpClient() {
        return getOkHttpClient(new FromNetWorkControlInterceptor());
    }

    public static OkHttpClient getNewOkHttpClient() {
        return getNewOkHttpClient(new NewInterceptor());
    }

    private static OkHttpClient okHttpClient;

    private static OkHttpClient getOkHttpClient(Interceptor cacheControl) {
        if (null == okHttpClient) {

            //定制OkHttp
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            //设置超时时间
            httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            httpClientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            //设置缓存
            File httpCacheDirectory = new File(AppApplication.getInstance().getCacheDir(), "OkHttpCache");
            httpClientBuilder.cache(new Cache(httpCacheDirectory, 100 * 1024 * 1024));
            //设置拦截器
            httpClientBuilder.addInterceptor(cacheControl);
            httpClientBuilder.addNetworkInterceptor(cacheControl);
            httpClientBuilder.addInterceptor(new UserAgentInterceptor("Android Device"));
            okHttpClient = httpClientBuilder.build();
        }
        return okHttpClient;
    }

    private static OkHttpClient getNewOkHttpClient(Interceptor cacheControl) {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //设置缓存
        File httpCacheDirectory = new File(AppApplication.getInstance().getCacheDir(), "OkHttpCache");
        httpClientBuilder.cache(new Cache(httpCacheDirectory, 100 * 1024 * 1024));
        //设置拦截器
        httpClientBuilder.addInterceptor(cacheControl);
        httpClientBuilder.addNetworkInterceptor(cacheControl);
        httpClientBuilder.addInterceptor(new UserAgentInterceptor("Android Device"));
        okHttpClient = httpClientBuilder.build();
        return okHttpClient;
    }

    private static class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isConnected(AppApplication.getInstance())) {
                Request.Builder builder = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE);
                if (null != builder)
                    request = builder.build();
            }

            Response response = chain.proceed(request);

            if (NetworkUtil.isConnected(AppApplication.getInstance())) {
                int maxAge = 60 * 30;//默认缓存半小时
                String cacheControl = request.cacheControl().toString();
                if (TextUtils.isEmpty(cacheControl)) {
                    cacheControl = "public, max-age=" + maxAge;
                }
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", cacheControl)
                        .build();

            } else {
                int maxStale = 60 * 60 * 24 * 30;
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

    private static class NewInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = SharedPreferencesUtils.readString("token");
//            String token = "3";
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            builder.cacheControl(CacheControl.FORCE_NETWORK);
            if (!TextUtils.isEmpty(token)) {
                builder.addHeader("Cookie", "token="+token);
            }
            request = builder.build();
//            request = request.newBuilder()
//                    .cacheControl(CacheControl.FORCE_NETWORK)
//                    .addHeader("token", token)
//                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    }

    private static class FromNetWorkControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();

            Response response = chain.proceed(request);
            return response;
        }
    }

    private static class UserAgentInterceptor implements Interceptor {
        private static final String USER_AGENT_HEADER_NAME = "User-Agent";
        private final String userAgentHeaderValue;

        UserAgentInterceptor(String userAgentHeaderValue) {
            this.userAgentHeaderValue = Preconditions.checkNotNull(userAgentHeaderValue, "userAgentHeaderValue = null");
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder()
                    .removeHeader(USER_AGENT_HEADER_NAME)
                    .addHeader(USER_AGENT_HEADER_NAME, userAgentHeaderValue);
            Request.Builder builder1 = AppApplication.getInstance().addOkHttpAddHeader(builder);
            Request requestWithUserAgent = null;
            if (null != builder1)
                requestWithUserAgent = builder1.build();
            else
                requestWithUserAgent = originalRequest;
            return chain.proceed(requestWithUserAgent);
        }
    }

}
