package bjx.com.siji.utils;

import android.util.Log;

public class LogsUtil {

    public static void normal(String content) {
        Log.i("TTSS", content);
    }

    public static void order(String content) {
        Log.i("ORDER_JIE", content);
    }

     public static void orderCharge(String content) {
        Log.i("ORDER_CHARGE", content);
    }

     public static void startDrive(String content) {
        Log.i("START_DRIVE", content);
    }


    public static void orderService(String content) {
        Log.i("ORDER_SERVICE", content);
    }

    public static void service(String content) {
        Log.i("ORDER_SERVICE", content);
    }

    public static void push(String content) {
        Log.i("PUSH", content);
    }

    public static void error(String content) {
        Log.e("ERROR", content);
    }

    public static void pay(String content) {
        Log.e("PAY", content);
    }

    public static void net(String content) {
        Log.i("AppClient", content);
    }
}
