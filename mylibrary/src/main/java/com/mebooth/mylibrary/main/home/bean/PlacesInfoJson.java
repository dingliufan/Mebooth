package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class PlacesInfoJson {

    private int errno;
    private String errmsg;
    private PlacesData data;

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

    public PlacesData getData() {
        return data;
    }

    public void setData(PlacesData data) {
        this.data = data;
    }

    public class PlacesData {

        private ArrayList<PlacesList> places;

        public ArrayList<PlacesList> getPlaces() {
            return places;
        }

        public void setPlaces(ArrayList<PlacesList> places) {
            this.places = places;
        }

        public class PlacesList {

            private String name;
            private String address;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }
}
