package com.mebooth.mylibrary.main.home.bean;

public class GetMyUserInfo {

    private int errno;
    private String errmsg;
    private UserData data;

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

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public class UserData {

        private UserInfo user;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        public class UserInfo {

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
