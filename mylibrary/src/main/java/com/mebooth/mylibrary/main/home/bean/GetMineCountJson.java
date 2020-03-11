package com.mebooth.mylibrary.main.home.bean;

public class GetMineCountJson {

    private int errno;
    private String errmsg;
    private CountData data;

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

    public CountData getData() {
        return data;
    }

    public void setData(CountData data) {
        this.data = data;
    }

    public class CountData {

        private CountUser user;
        private CountStats stats;

        public CountUser getUser() {
            return user;
        }

        public void setUser(CountUser user) {
            this.user = user;
        }

        public CountStats getStats() {
            return stats;
        }

        public void setStats(CountStats stats) {
            this.stats = stats;
        }

        public class CountUser {
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

        public class CountStats {

            private int topic;
            private int following;
            private int praise;
            private int favorite;

            public int getFavorite() {
                return favorite;
            }

            public void setFavorite(int favorite) {
                this.favorite = favorite;
            }

            public int getTopic() {
                return topic;
            }

            public void setTopic(int topic) {
                this.topic = topic;
            }

            public int getFollowing() {
                return following;
            }

            public void setFollowing(int following) {
                this.following = following;
            }

            public int getPraise() {
                return praise;
            }

            public void setPraise(int praise) {
                this.praise = praise;
            }
        }
    }
}
