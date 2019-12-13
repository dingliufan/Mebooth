package com.mebooth.mylibrary.main.home.bean;

public class GetRongIMTokenJson {

    private int errno;
    private String errmsg;
    private RongData data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public RongData getData() {
        return data;
    }

    public void setData(RongData data) {
        this.data = data;
    }

    public class RongData {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
