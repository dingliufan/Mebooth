package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class EntranceJson {

    private int errno;
    private String errmsg;
    private EntranceData data;

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

    public EntranceData getData() {
        return data;
    }

    public void setData(EntranceData data) {
        this.data = data;
    }

    public class EntranceData {

        private ArrayList<EntranceConfig> config;

        public ArrayList<EntranceConfig> getConfig() {
            return config;
        }

        public void setConfig(ArrayList<EntranceConfig> config) {
            this.config = config;
        }

        public class EntranceConfig {

            private String name;
            private String image;
            private String target;
            private String foward;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getTarget() {
                return target;
            }

            public void setTarget(String target) {
                this.target = target;
            }

            public String getFoward() {
                return foward;
            }

            public void setFoward(String foward) {
                this.foward = foward;
            }
        }
    }
}
