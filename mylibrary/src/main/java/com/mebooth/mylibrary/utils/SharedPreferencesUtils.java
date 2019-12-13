package com.mebooth.mylibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;


import com.mebooth.mylibrary.main.AppApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author gumenghao .
 * @version v1.0 .
 * @date 2018/5/7.
 * @file SharedPreferencesUtils.java .
 * @brief SharedPreferencesUtils工具类 .
 */
public class SharedPreferencesUtils {

    public static final String CONFIG = "memberEntity";/// 登陆后的信息 .
    //    private static final String DATA = "data";
    private static final String SUB_TAG = SharedPreferencesUtils.class.getSimpleName();
    private static Context context = AppApplication.getInstance();

    private SharedPreferencesUtils(Context context) {
        SharedPreferencesUtils.context = context;
    }

    /**
     * write   data.
     *
     * @param map .
     */
    public void write(Map<String, Object> map) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share != null) {
            Editor editor = share.edit();
            Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                Object obj = entry.getValue();
                if (obj instanceof String) {
                    editor.putString(entry.getKey().toString(), entry.getValue().toString());
                } else if (obj instanceof Integer) {
                    editor.putInt(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
                } else if (obj instanceof Boolean) {
                    editor.putBoolean(entry.getKey().toString(), Boolean.getBoolean(entry.getValue().toString()));
                } else if (obj instanceof Float) {
                    editor.putFloat(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()));
                } else if (obj instanceof Long) {
                    editor.putLong(entry.getKey().toString(), Long.parseLong(entry.getValue().toString()));
                }
            }
            editor.commit();
        }
    }


    /**
     * write   String  data.
     *
     * @param key
     * @param value
     */
    public static void writeString(String key, String value) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share != null) {
            Editor editor = share.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    /**
     * write int  data..
     *
     * @param key
     * @param value
     */
    public static void writeInt(String key, int value) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share != null) {
            Editor editor = share.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static void writeBoolean(String key, Boolean value) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share != null) {
            Editor editor = share.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static String readString(String key) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return null;
        }
        return share.getString(key, null);
    }

    public static int readInt(String key) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return 0;
        }
        return share.getInt(key, 0);
    }

    public Long readLong(String key) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return null;
        }
        return share.getLong(key, 0l);
    }

    public static Boolean readBoolean(String key) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return false;
        }
        return share.getBoolean(key, false);
    }

    public static Boolean readBoolean(String key, boolean defaultValue) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return defaultValue;
        }
        return share.getBoolean(key, defaultValue);
    }

    public Float readFloat(String key) {
        SharedPreferences share = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return null;
        }
        return share.getFloat(key, 0f);
    }

    public static void writeString(String arg0, String key, String value) {
        SharedPreferences share = context.getSharedPreferences(arg0, Context.MODE_PRIVATE);
        if (share != null) {
            Editor editor = share.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public String readString(String arg0, String key) {
        SharedPreferences share = context.getSharedPreferences(arg0, Context.MODE_PRIVATE);
        if (share == null || !share.contains(key)) {
            return null;
        }
        return share.getString(key, null);
    }


    /**
     * 保存序列化对象serializable
     *
     * @param context
     * @param key
     * @param obj
     * @author liuhe
     */
    public static void saveObject(Context context, String key, Object obj) {
        try {
            Editor sharedata = context.getSharedPreferences(CONFIG, 0).edit();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            sharedata.putString(key, bytesToHexString);
            sharedata.commit();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "failed to save the object ");
        }
    }

    /**
     * get the saved objects
     *
     * @param context
     * @param key
     * @return
     * @author liuhe
     */
    public static Object getObject(Context context, String key) {
        try {
            SharedPreferences sharedata = context.getSharedPreferences(CONFIG, 0);
            if (sharedata.contains(key)) {
                String string = sharedata.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    /// To convert hexadecimal data array for deserialization .
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    /// Return the serialized objects .
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To convert bytes Hex
     *
     * @param bArray
     * @return modified:
     * @author liuhe
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        int length = bArray.length;
        for (int i = 0; i < length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * To convert hexadecimal data array
     *
     * @param data
     * @return modified:
     * @author liuhe
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  /// Two hexadecimal number after conversion of decimal number .
            char hex_char1 = hexString.charAt(i);
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   /// In the ASCII 0 corresponds to 48 .
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; /// In the ASCII A corresponds to 65 .
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); /// The second of two hexadecimal number .
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48);
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55;
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;/// Put the number of the converted into Byte .
        }
        return retData;
    }

    /**
     * @param context
     * @author liuhe
     * clear SharedPreferences  data.
     */
    public static void clear(Context context, String name) {
        SharedPreferences share = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        share.edit().clear().commit();
    }

    public static void delete(Context context, String key) {
        try {
            SharedPreferences share = context.getSharedPreferences(key, Context.MODE_PRIVATE);
            if (share != null) {
                share.edit().remove(key).commit();
            }
        } catch (Exception e) {
            Log.e(SUB_TAG, e.getMessage());
        }
    }


}