package com.mebooth.mylibrary.main.utils;

import com.mebooth.mylibrary.R;
import com.mebooth.mylibrary.main.AppApplication;

public class ResourcseMessage {

    //不同的包不同的字体颜色
    public static int getFontColor() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.color.bg_FED130;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.color.bg_FED130;
        } else {
            return R.color.bg_E73828;
        }
    }

    //根据包名选择不同的关注按钮
    public static int getFollowBackground() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.xiaomifollow;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.xiaomifollow;
        } else {
            return R.drawable.follow;
        }
    }

    //不同的包名 不同的logo
    public static int getAppLogo() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.mifeng_pluginlogo;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.mebike_pluginlogo;
        } else {
            return R.drawable.people_pluginlogo;
        }
    }

    //不同的包名 不同的收藏collect
    public static int getCollectRes() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.mebikecollect;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.mebikecollect;
        } else {
            return R.drawable.collect;
        }
    }

    //不同的包名 不同的gpsres
    public static int getGpsRes() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.mebikegpsimgred;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.mebikegpsimgred;
        } else {
            return R.drawable.gpsimgred;
        }
    }

    //不同的包名 不同的点赞
    public static int getPraiseRes() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.mebikepraise;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.mebikepraise;
        } else {
            return R.drawable.praise;
        }
    }

    //员工标记区别
    public static int getIsStaffRes() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.staff_tab_xiaomi;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.staff_tab_xiaomi;
        } else {
            return R.drawable.staff_tab;
        }
    }
    //个人中心背景
    public static int getMineBg() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.xiaomi_newminebg;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.xiaomi_newminebg;
        } else {
            return R.drawable.newminebg;
        }
    }
    //编辑个人中心男女选择背景
    public static int getEditMineSexBg() {

        if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.mmuu.travel.client")) {

            return R.drawable.xiaomi_edituserinfo_choosesex;

        } else if (AppApplication.getInstance().getApplicationInfo().processName.equals("com.baojia.mebike")) {
            return R.drawable.xiaomi_edituserinfo_choosesex;
        } else {
            return R.drawable.edituserinfo_choosesex;
        }
    }

}
