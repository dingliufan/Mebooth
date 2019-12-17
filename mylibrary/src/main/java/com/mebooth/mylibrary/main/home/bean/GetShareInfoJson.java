package com.mebooth.mylibrary.main.home.bean;

public class GetShareInfoJson {

    private int errno;
    private String errmsg;
    private ShareData data;

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

    public ShareData getData() {
        return data;
    }

    public void setData(ShareData data) {
        this.data = data;
    }

    public class ShareData {

        private ShareInfo share_info;

        public ShareInfo getShare_info() {
            return share_info;
        }

        public void setShare_info(ShareInfo share_info) {
            this.share_info = share_info;
        }

        public class ShareInfo {

            private String title;
            private String description;
            private String image;
            private String icon;
            private String url;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
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

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
