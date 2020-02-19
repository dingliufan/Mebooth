package com.mebooth.mylibrary.main.home.bean;

public class FlushJson {

    private int errno;
    private String errmsg;
    private FlushData data;

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

    public FlushData getData() {
        return data;
    }

    public void setData(FlushData data) {
        this.data = data;
    }

    public class FlushData {

        private FlushConfig config;

        public FlushConfig getConfig() {
            return config;
        }

        public void setConfig(FlushConfig config) {
            this.config = config;
        }

        public class FlushConfig {

            private String image;
            private String title;
            private String nickname;
            private String avatar;
            private String addtime;
            private int newsid;

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

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public int getNewsid() {
                return newsid;
            }

            public void setNewsid(int newsid) {
                this.newsid = newsid;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }
    }
}
