package com.mebooth.mylibrary.main.home.bean;

public class PublicBean {

    private int errno;
    private String errmsg;

    public PublicBean(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public PublicBean() {
    }

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
}
