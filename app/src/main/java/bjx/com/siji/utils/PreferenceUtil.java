package bjx.com.siji.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {

    public static void putOrderInfoPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static String getOrderInfoPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static String getSharePreference(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public static void clearOrderInfoPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("orderInfo", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.remove(key);
        localEditor.commit();
    }


    //////////////////////////////////////////////////////////////  第一次中断时已行驶的距离
    public static void putDriveDistanceTimeSharePreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("latLngDisTimeModel", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static String getDriveDistanceTimeSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("latLngDisTimeModel", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

     public static void clearDriveDistanceTimeSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("latLngDisTimeModel", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
//        localEditor.remove(key);
         localEditor.putString(key, "");

         localEditor.commit();
    }

    //////////////////////////////////////////////////////////////  第一次中断时已行驶的距离
    public static void putRestartDriveDistanceTimeSharePreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("restartDisTimeModel", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static String getRestartDriveDistanceTimeSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("restartDisTimeModel", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void clearRestartDriveDistanceTimeSharePreference(Context context, String key ) {
        SharedPreferences settings = context.getSharedPreferences("restartDisTimeModel", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
//        localEditor.remove(key);
        localEditor.putString(key, "");
        localEditor.commit();
    }

      //////////////////////////////////////////////////////////////

    public static String getLatlngSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("latLng", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void putLatlngPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("latLng", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }


    public static void clearLatlngSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("latLng", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
//        localEditor.remove(key);
        localEditor.putString(key, "");

        localEditor.commit();
    }

    //////////////////////////////////////////////////////////////
     public static String getRestartLatlngSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("restartLatLng", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void putRestartLatlngPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("restartLatLng", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static void clearRestartLatlngSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("restartLatLng", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
//        localEditor.remove(key);
        localEditor.putString(key, "");

        localEditor.commit();
    }

    //////////////////////////////////////////////////////////////////////////
    /**
     * @return
     */
    public static void putBooleanSharePreference(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static boolean getBooleanSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    //退出登录清除此条数据
    public static void clearBooleanSharePreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * @return
     */
    public static boolean getBooleanSharePreference(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    ///////////////////////////////////////////////////
    public static String getGoodsIDPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("goods_id", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void putGoodsIDPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("goods_id", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }


///////////////////
    public static String getShopDetailDesPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("shop_des", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void putShopDetailDesPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("shop_des", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static String getGoodsCategoryPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("goods_category", Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void putGoodsCategoryPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences("goods_category", Context.MODE_PRIVATE);
        Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }
}
