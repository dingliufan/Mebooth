package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetNewInfoJson {

    private int errno;
    private String errmsg;
    private NewInfoData data;

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

    public NewInfoData getData() {
        return data;
    }

    public void setData(NewInfoData data) {
        this.data = data;
    }

    public class NewInfoData {
        private News news;
        private NewsUser user;

        public NewsUser getUser() {
            return user;
        }

        public void setUser(NewsUser user) {
            this.user = user;
        }

        public News getNews() {
            return news;
        }

        public void setNews(News news) {
            this.news = news;
        }

        public class News {

            private int newsid;
            private String title;
            private ArrayList<Content> content;
            private String cover;
            private int replies;
            private int watches;
            private String addtime;


            public int getReplies() {
                return replies;
            }

            public void setReplies(int replies) {
                this.replies = replies;
            }

            public int getWatches() {
                return watches;
            }

            public void setWatches(int watches) {
                this.watches = watches;
            }

            public int getNewsid() {
                return newsid;
            }

            public void setNewsid(int newsid) {
                this.newsid = newsid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public ArrayList<Content> getContent() {
                return content;
            }

            public void setContent(ArrayList<Content> content) {
                this.content = content;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public class Content {

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

        public class NewsUser {

            private String nickname;
            private String avatar;

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
