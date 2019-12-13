package com.mebooth.mylibrary.net.netutils;

public interface JsDownloadListener {
    void onStartDownload();

    void onProgress(int progress, long size, long total);

    void onFinishDownload();

    void onFail(String errorInfo);
}
