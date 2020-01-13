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
            private int newsid;

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
