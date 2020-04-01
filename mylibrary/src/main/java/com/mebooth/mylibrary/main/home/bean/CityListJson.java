package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class CityListJson {

    private int errno;
    private String errmsg;
    private CityListData data;

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

    public CityListData getData() {
        return data;
    }

    public void setData(CityListData data) {
        this.data = data;
    }

    public class CityListData {

        private ArrayList<String> citys;

        public ArrayList<String> getCitys() {
            return citys;
        }

        public void setCitys(ArrayList<String> citys) {
            this.citys = citys;
        }
    }
}
