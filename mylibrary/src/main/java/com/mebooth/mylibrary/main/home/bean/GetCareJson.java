package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetCareJson {

    private int errno;
    private String errmsg;
    private CareData data;

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

    public CareData getData() {
        return data;
    }

    public void setData(CareData data) {
        this.data = data;
    }

    public class CareData {

        private ArrayList<CareUser> users;
        private double offset;

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public ArrayList<CareUser> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<CareUser> users) {
            this.users = users;
        }

        public class CareUser {

            private int uid;
            private String nickname;
            private String avatar;
            private String mobile;
            private String addtime;

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }
        }
    }
}
