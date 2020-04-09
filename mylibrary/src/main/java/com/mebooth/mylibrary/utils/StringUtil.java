package com.mebooth.mylibrary.utils;

import android.content.Context;
import android.widget.Toast;

import java.math.BigDecimal;

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
    public static boolean isEmptyWithTrim(String s) {
        if (s == null || "".equals(s.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空，null，"null",空串，空白符（空格）均为空
     */
    public static boolean isNull(String s) {
        if (s == null || "".equals(s.trim()) || s.toLowerCase().trim().equals("null")) {
            return true;
        }
        return false;
    }

    private static final boolean DEBUG = true;

    public static void toastDebug(Context context, String msg) {
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if (!DEBUG)
            return;
        if (isEmpty(msg)) {
            Toast.makeText(context, "Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void toastInfo(Context context, String msg) {
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if (isEmpty(msg)) {
            Toast.makeText(context, "Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static String formatBigNum(String num) {
        if (num.isEmpty()) {
            // 数据为空直接返回0
            return "0";
        }
        try {
            StringBuffer sb = new StringBuffer();
//            if (!num.()) {
//                // 如果数据不是数字则直接返回0
//                return "0";
//            }


            BigDecimal b0 = new BigDecimal("1000");
            BigDecimal b1 = new BigDecimal("10000");
            BigDecimal b2 = new BigDecimal("100000000");
            BigDecimal b3 = new BigDecimal(num);

            String formatedNum = "";//输出结果
            String unit = "";//单位


            if (b3.compareTo(b0) == -1) {
                sb.append(b3.toString());
            } else if ((b3.compareTo(b0) == 0 && b3.compareTo(b0) == 1)
                    || b3.compareTo(b1) == -1) {
//                formatedNum = b3.divide(b0).toString();
//                unit = "k";
                sb.append(b3.toString());
            } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)
                    || b3.compareTo(b2) == -1) {
                formatedNum = b3.divide(b1).toString();
                unit = "w";
            } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
                formatedNum = b3.divide(b2).toString();
                unit = "亿";
            }
            if (!"".equals(formatedNum)) {
                int i = formatedNum.indexOf(".");
                if (i == -1) {
                    sb.append(formatedNum).append(unit);
                } else {
                    i = i + 1;
                    String v = formatedNum.substring(i, i + 1);
//                    if (!v.equals("0")) {
                        sb.append(formatedNum.substring(0, i + 1)).append(unit);
//                    } else {
//                        sb.append(formatedNum.substring(0, i - 1)).append(unit);
//                    }
                }
            }
            if (sb.length() == 0)
                return "0";
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return num;
        }
    }

}
