package com.mebooth.mylibrary.main.home.bean;

public class GetIsCollectJson {

    private int errno;
    private String errmsg;
    private IsCollectData data;

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

    public IsCollectData getData() {
        return data;
    }

    public void setData(IsCollectData data) {
        this.data = data;
    }

    public class IsCollectData {

        private boolean praised;

        public boolean isPraised() {
            return praised;
        }

        public void setPraised(boolean praised) {
            this.praised = praised;
        }
    }
}
