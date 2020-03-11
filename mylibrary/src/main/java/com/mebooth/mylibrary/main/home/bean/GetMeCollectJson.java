package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetMeCollectJson {

    private int errno;
    private String errmsg;
    private MeCollectData data;

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

    public MeCollectData getData() {
        return data;
    }

    public void setData(MeCollectData data) {
        this.data = data;
    }

    public class MeCollectData {

        private ArrayList<MeCollectList> list;
        private double offset;

        public ArrayList<MeCollectList> getList() {
            return list;
        }

        public void setList(ArrayList<MeCollectList> list) {
            this.list = list;
        }

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public class MeCollectList {

            private MeCollectNews news;
            private MeCollectUser user;

            public MeCollectNews getNews() {
                return news;
            }

            public void setNews(MeCollectNews news) {
                this.news = news;
            }

            public MeCollectUser getUser() {
                return user;
            }

            public void setUser(MeCollectUser user) {
                this.user = user;
            }

            public class MeCollectNews {

                private int newsid;
                private String short_title;
                private String describe;
                private String title;
                private String cover;
                private int replies;
                private int praises;
                private int watches;
                private int favorites;

                public String getDescribe() {
                    return describe;
                }

                public void setDescribe(String describe) {
                    this.describe = describe;
                }

                public int getNewsid() {
                    return newsid;
                }

                public void setNewsid(int newsid) {
                    this.newsid = newsid;
                }

                public String getShort_title() {
                    return short_title;
                }

                public void setShort_title(String short_title) {
                    this.short_title = short_title;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getReplies() {
                    return replies;
                }

                public void setReplies(int replies) {
                    this.replies = replies;
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

                public int getFavorites() {
                    return favorites;
                }

                public void setFavorites(int favorites) {
                    this.favorites = favorites;
                }
            }

            public class MeCollectUser {

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
}
