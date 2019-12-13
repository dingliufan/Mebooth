package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class UpdateHeaderFileJson {

    private int errno;
    private String errmsg;
    private ArrayList<String> data;

    public UpdateHeaderFileJson(int errno, String errmsg, ArrayList<String> data) {
        this.errno = errno;
        this.errmsg = errmsg;
        this.data = data;
    }

    public UpdateHeaderFileJson() {
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

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
