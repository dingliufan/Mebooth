package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetNowJson {

    private int errno;
    private String errmsg;
    private NowData data;

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

    public NowData getData() {
        return data;
    }

    public void setData(NowData data) {
        this.data = data;
    }

    public class NowData {

        private ArrayList<NowDataList> list;
        private double offset;

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public ArrayList<NowDataList> getList() {
            return list;
        }

        public void setList(ArrayList<NowDataList> list) {
            this.list = list;
        }

        public class NowDataList {

            private NowTopic topic;
            private NowUser user;

            public NowTopic getTopic() {
                return topic;
            }

            public void setTopic(NowTopic topic) {
                this.topic = topic;
            }

            public NowUser getUser() {
                return user;
            }

            public void setUser(NowUser user) {
                this.user = user;
            }

            public class NowTopic {

                private int tid;
                private int uid;
                private String location;
                private String content;
                private ArrayList<String> images;
                private int praises;
                private int watches;
                private int replies;
                private String addtime;
                private boolean praised;
                private int type;

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public boolean isPraised() {
                    return praised;
                }

                public void setPraised(boolean praised) {
                    this.praised = praised;
                }

                public String getAddtime() {
                    return addtime;
                }

                public void setAddtime(String addtime) {
                    this.addtime = addtime;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public ArrayList<String> getImages() {
                    return images;
                }

                public void setImages(ArrayList<String> images) {
                    this.images = images;
                }

                public int getTid() {
                    return tid;
                }

                public void setTid(int tid) {
                    this.tid = tid;
                }

                public int getUid() {
                    return uid;
                }

                public void setUid(int uid) {
                    this.uid = uid;
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public int getPraises() {
                    return praises;
                }

                public void setPraises(int praises) {
                    this.praises = praises;
                }

                public int getWatches() {
                    return watches;
                }

                public void setWatches(int watches) {
                    this.watches = watches;
                }

                public int getReplies() {
                    return replies;
                }

                public void setReplies(int replies) {
                    this.replies = replies;
                }

            }

            public class NowUser {

                private int uid;
                private String nickname;
                private String avatar;
                private boolean followed;
                private String employee;

                public String getEmployee() {
                    return employee;
                }

                public void setEmployee(String employee) {
                    this.employee = employee;
                }

                public boolean isFollowed() {
                    return followed;
                }

                public void setFollowed(boolean followed) {
                    this.followed = followed;
                }

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
}
