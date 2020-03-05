package com.mebooth.mylibrary.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chenyf on 2017/7/1.
 */

public class StringUtil {
    /**
     * 判断字符串是否为空，null，空串均为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        if (s == null || "".equals(s)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空，null，空串，空白符（空格）均为空
     *
     * @param s
     * @return
     */
    public static  boolean isEmptyWithTrim(String s) {
        if (s == null || "".equals(s.trim())) {
            return true;
        }
        return false;
    }
    /**
     * 判断字符串是否为空，null，"null",空串，空白符（空格）均为空
     */
    public static  boolean isNull(String s) {
        if (s == null || "".equals(s.trim())||s.toLowerCase().trim().equals("null")) {
            return true;
        }
        return false;
    }

    private static final boolean DEBUG=true;
    public static void  toastDebug(Context context, String msg){
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if(!DEBUG)
            return;
        if(isEmpty(msg)){
            Toast.makeText(context,"Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void  toastInfo(Context context, String msg){
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if(isEmpty(msg)){
            Toast.makeText(context,"Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
