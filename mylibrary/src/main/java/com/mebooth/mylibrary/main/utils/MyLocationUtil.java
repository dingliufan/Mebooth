package com.mebooth.mylibrary.main.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;


import com.mebooth.mylibrary.utils.ToastUtils;

import java.util.List;

import static com.mebooth.mylibrary.utils.UIUtils.getContext;

public class MyLocationUtil {
    private static String provider;

    public static Location getMyLocation() {
//        获取当前位置信息
        //获取定位服务
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(locationManager.GPS_PROVIDER)) {
//            GPS位置控制器
            provider = locationManager.GPS_PROVIDER;//GPS定位
        } else if (list.contains(locationManager.NETWORK_PROVIDER)) {
//            网络位置控制器
            provider = locationManager.NETWORK_PROVIDER;//网络定位
        }

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

            return lastKnownLocation;
        } else {
            ToastUtils.getInstance().showToast("请检查网络或GPS是否打开");
        }


        return null;
    }
}
