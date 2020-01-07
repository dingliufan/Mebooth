package com.mebooth.mylibrary.main.home.bean;

public class GetConfigJson {

    private int errno;
    private String errmsg;
    private ConfigData data;

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

    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }

    public class ConfigData {

        private QrCodeConfig config;

        public QrCodeConfig getConfig() {
            return config;
        }

        public void setConfig(QrCodeConfig config) {
            this.config = config;
        }

        public class QrCodeConfig {

            private String qrcode;

            public String getQrcode() {
                return qrcode;
            }

            public void setQrcode(String qrcode) {
                this.qrcode = qrcode;
            }
        }
    }
}
