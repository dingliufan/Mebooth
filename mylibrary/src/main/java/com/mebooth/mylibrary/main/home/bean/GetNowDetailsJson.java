package com.mebooth.mylibrary.main.home.bean;

public class GetNowDetailsJson {

    private int errno;
    private String errmsg;
    private NowDetailsData data;

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

    public NowDetailsData getData() {
        return data;
    }

    public void setData(NowDetailsData data) {
        this.data = data;
    }

    public class NowDetailsData {

        private GetNowJson.NowData.NowDataList.NowUser user;
        private GetNowJson.NowData.NowDataList.NowTopic topic;

        public GetNowJson.NowData.NowDataList.NowTopic getTopic() {
            return topic;
        }

        public void setTopic(GetNowJson.NowData.NowDataList.NowTopic topic) {
            this.topic = topic;
        }

        public GetNowJson.NowData.NowDataList.NowUser getUser() {
            return user;
        }

        public void setUser(GetNowJson.NowData.NowDataList.NowUser user) {
            this.user = user;
        }
    }
}
