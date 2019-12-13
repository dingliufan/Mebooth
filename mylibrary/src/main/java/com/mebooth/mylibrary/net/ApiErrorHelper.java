package com.mebooth.mylibrary.net;


public class ApiErrorHelper {

    private static ApiErrorHelper sInstance;

    private ApiErrorHelper() {
    }

    public static ApiErrorHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ApiErrorHelper();
        }
        return sInstance;
    }


    public void handleCommonError(Throwable e, CommonObserver subscriber) {
        e.printStackTrace();
    }

    public static class LogoutEvent {
        public boolean conflict;

        public LogoutEvent(boolean conflict) {
            this.conflict = conflict;
        }
    }


}