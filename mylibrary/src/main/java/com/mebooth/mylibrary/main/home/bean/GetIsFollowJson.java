package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetIsFollowJson {

    private int errno;
    private String errmsg;
    private FollowData data;

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

    public FollowData getData() {
        return data;
    }

    public void setData(FollowData data) {
        this.data = data;
    }

    public class FollowData {

        private ArrayList<FollowUser> users;

        public ArrayList<FollowUser> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<FollowUser> users) {
            this.users = users;
        }

        public class FollowUser {

            private int uid;
            private boolean followed;

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public boolean isFollowed() {
                return followed;
            }

            public void setFollowed(boolean followed) {
                this.followed = followed;
            }
        }
    }
}
