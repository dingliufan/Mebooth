package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class CustomizeJson {

    private int errno;
    private String errmsg;
    private CustomizeData data;

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

    public CustomizeData getData() {
        return data;
    }

    public void setData(CustomizeData data) {
        this.data = data;
    }

    public class CustomizeData {

        private CustomizeBanner banner;

        private ArrayList<CustomizeSubjects> subjects;

        public CustomizeBanner getBanner() {
            return banner;
        }

        public void setBanner(CustomizeBanner banner) {
            this.banner = banner;
        }

        public ArrayList<CustomizeSubjects> getSubjects() {
            return subjects;
        }

        public void setSubjects(ArrayList<CustomizeSubjects> subjects) {
            this.subjects = subjects;
        }

        public class CustomizeBanner {

            private String image;
            private String newsid;
            private String title;
            private String nickname;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getNewsid() {
                return newsid;
            }

            public void setNewsid(String newsid) {
                this.newsid = newsid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }
        }

        public class CustomizeSubjects {

            private String name;
            private String title;
            private int total;
            private ArrayList<GetRecommendJson.RecommendData.RecommendDataList> feeds;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public ArrayList<GetRecommendJson.RecommendData.RecommendDataList> getFeeds() {
                return feeds;
            }

            public void setFeeds(ArrayList<GetRecommendJson.RecommendData.RecommendDataList> feeds) {
                this.feeds = feeds;
            }
        }
    }
}
