package com.mebooth.mylibrary.net;


import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;

public class CommonObserver<T> extends DefaultObserver<T> {


    @Override
    public void onNext(@NonNull T t) {
    }

    @Override
    public void onError(@NonNull Throwable e) {
        ApiErrorHelper.getInstance().handleCommonError(e, this);
    }

    @Override
    public void onComplete() {

    }
}