package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class ProvincesListJson {

    private int errno;
    private String errmsg;
    private ProvincesData data;

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

    public ProvincesData getData() {
        return data;
    }

    public void setData(ProvincesData data) {
        this.data = data;
    }

    public class ProvincesData {

        private ArrayList<String> hots;
        private ArrayList<String> provinces;

        public ArrayList<String> getHots() {
            return hots;
        }

        public void setHots(ArrayList<String> hots) {
            this.hots = hots;
        }

        public ArrayList<String> getProvinces() {
            return provinces;
        }

        public void setProvinces(ArrayList<String> provinces) {
            this.provinces = provinces;
        }
    }
}
