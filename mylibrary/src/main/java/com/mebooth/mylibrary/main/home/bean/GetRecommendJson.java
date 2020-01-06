package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetRecommendJson {

    private int errno;
    private String errmsg;
    private RecommendData data;

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

    public RecommendData getData() {
        return data;
    }

    public void setData(RecommendData data) {
        this.data = data;
    }

    public class RecommendData {

        private ArrayList<RecommendDataList> list;
        private double offset;

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public ArrayList<RecommendDataList> getList() {
            return list;
        }

        public void setList(ArrayList<RecommendDataList> list) {
            this.list = list;
        }

        public class RecommendDataList {

            private RecommendUser user;
            private Recommendfeed feed;

            public RecommendUser getUser() {
                return user;
            }

            public void setUser(RecommendUser user) {
                this.user = user;
            }

            public Recommendfeed getFeed() {
                return feed;
            }

            public void setFeed(Recommendfeed feed) {
                this.feed = feed;
            }

            public class Recommendfeed {
                private int type;
                private int relateid;
                private String content;
                private ArrayList<String> images;
                private double score;
                private int praises;
                private int watches;
                private int replies;
                private boolean praised;
                private String addtime;
                private String location;

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getAddtime() {
                    return addtime;
                }

                public void setAddtime(String addtime) {
                    this.addtime = addtime;
                }

                public double getScore() {
                    return score;
                }

                public void setScore(double score) {
                    this.score = score;
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

                public boolean isPraised() {
                    return praised;
                }

                public void setPraised(boolean praised) {
                    this.praised = praised;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getRelateid() {
                    return relateid;
                }

                public void setRelateid(int relateid) {
                    this.relateid = relateid;
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

            }

            public class RecommendUser {

                private int uid;
                private String nickname;
                private String avatar;
                private boolean followed;
                private ArrayList<GetMineCountJson.CountData.CountUser.MineMedals> medals;

                public ArrayList<GetMineCountJson.CountData.CountUser.MineMedals> getMedals() {
                    return medals;
                }

                public void setMedals(ArrayList<GetMineCountJson.CountData.CountUser.MineMedals> medals) {
                    this.medals = medals;
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
