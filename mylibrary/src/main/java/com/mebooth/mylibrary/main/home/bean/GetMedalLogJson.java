package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetMedalLogJson {

    private int errno;
    private String errmsg;
    private MedalLogData data;

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

    public MedalLogData getData() {
        return data;
    }

    public void setData(MedalLogData data) {
        this.data = data;
    }

    public class MedalLogData {

        private MedalLogUser user;
        private ArrayList<MedalLog> medals;
        private GetDecorationJson.DecorationData.DecorationView.DecorationStats stats;

        public GetDecorationJson.DecorationData.DecorationView.DecorationStats getStats() {
            return stats;
        }

        public void setStats(GetDecorationJson.DecorationData.DecorationView.DecorationStats stats) {
            this.stats = stats;
        }

        public MedalLogUser getUser() {
            return user;
        }

        public void setUser(MedalLogUser user) {
            this.user = user;
        }

        public ArrayList<MedalLog> getMedals() {
            return medals;
        }

        public void setMedals(ArrayList<MedalLog> medals) {
            this.medals = medals;
        }

        public class MedalLogUser {

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

        public class MedalLog {

            private String title;
            private String image;
            private String icon;
            private String restrict;
            private String addtime;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getRestrict() {
                return restrict;
            }

            public void setRestrict(String restrict) {
                this.restrict = restrict;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }
        }
    }
}
