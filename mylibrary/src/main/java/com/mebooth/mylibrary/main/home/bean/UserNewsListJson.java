package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class UserNewsListJson {

    private int errno;
    private String errmsg;
    private UserNewsListData data;

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

    public UserNewsListData getData() {
        return data;
    }

    public void setData(UserNewsListData data) {
        this.data = data;
    }

    public class UserNewsListData {

        private ArrayList<UserNewsList> list;
        private double offset;

        public double getOffset() {
            return offset;
        }

        public void setOffset(double offset) {
            this.offset = offset;
        }

        public ArrayList<UserNewsList> getList() {
            return list;
        }

        public void setList(ArrayList<UserNewsList> list) {
            this.list = list;
        }

        public class UserNewsList {

            private int newsid;
            private int uid;
            private String title;
            private ArrayList<UserNewsContent> content;
            private String cover;
            private String location;
            private int replies;
            private int praises;
            private int watches;
            private String publish;
            private String addtime;
            private String describe;
            private boolean praised;

            public int getNewsid() {
                return newsid;
            }

            public void setNewsid(int newsid) {
                this.newsid = newsid;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public ArrayList<UserNewsContent> getContent() {
                return content;
            }

            public void setContent(ArrayList<UserNewsContent> content) {
                this.content = content;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
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

            public String getPublish() {
                return publish;
            }

            public void setPublish(String publish) {
                this.publish = publish;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getDescribe() {
                return describe;
            }

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public boolean isPraised() {
                return praised;
            }

            public void setPraised(boolean praised) {
                this.praised = praised;
            }

            public class UserNewsContent {

                private String type;
                private String image;
                private String video;
                private String content;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getVideo() {
                    return video;
                }

                public void setVideo(String video) {
                    this.video = video;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }
            }
        }
    }
}
