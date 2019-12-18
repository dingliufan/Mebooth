package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetIMUserInfoJson {

    private int errno;
    private String errmsg;
    private IMUserData data;

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

    public IMUserData getData() {
        return data;
    }

    public void setData(IMUserData data) {
        this.data = data;
    }

    public class IMUserData {

        private ArrayList<IMUser> users;

        public ArrayList<IMUser> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<IMUser> users) {
            this.users = users;
        }

        public class IMUser {

            private int uid;
            private String nickname;
            private String avatar;

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
        }
    }
}
