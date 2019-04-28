package bjx.com.siji.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.engine.Engine;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/1/23.
 * 司机后台进程，用于实时报告司机位置和刷新订单
 */

public class DriverService extends Service {
    private static Ringtone mRingtone;
    public double mCurretLon; // 用户当前经纬度
    public double mCurretLat;
    Timer timerOrder;
    Timer timerLocation;
    private String sj_id;
    private Engine mEngine = App.getInstance().getEngine();
    private Context context = this;
    private LocationService locationService; // 百度定位service
    TimerTask taskLocation = new TimerTask() {
        @Override
        public void run() {
            locationService.start();// 定位SDK
        }
    };
    private boolean hasNewOrder;
    TimerTask taskOrder = new TimerTask() {
        @Override
        public void run() {
            mEngine.getOrderList(sj_id, MD5.getMessageDigest((Constants.BASE_KEY + sj_id).getBytes()))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            System.out.println("servive运行中..刷新订单");

                            try {
                                String str = response.body().string();

                                JSONObject jo = new JSONObject(str);
                                int status = jo.getInt("status");
                                if (status == 200) {

                                    LogsUtil.orderService("订单信息" + str);

                                    SPUtils.put(context, "WORK_STATE", new Integer(1));
                                    JSONArray resultData = jo.getJSONArray("result");
                                    List<OrderList> resultList = new ArrayList<OrderList>();

                                    if (resultData != null && resultData.length() > 0) {
                                        boolean wrong = false;
                                        for (int i = 0; i < resultData.length(); i++) {
                                            JSONObject obj = (JSONObject) resultData.get(i);
                                            OrderList order = new OrderList();
                                            String orderId = obj.getString("order_id");
                                            if (orderId == null || orderId.trim().equals("")) {
                                                wrong = true;
                                                break;
                                            }
                                        }
                                        if (!wrong) {//有新订单
                                            hasNewOrder = true;
                                            //有新订单
                                            Log.e("订单列表new order", sj_id);
                                            //写入缓存，让应用启动时接收跳转到接单页面
                                            SPUtils.put(context, "NEW_ORDER", new Integer(1));
                                            //声音提醒
                                            //播放提示音
                                            if (mRingtone == null) {
                                                String uri = "android.resource://" + context.getPackageName() + "/" + R.raw.callisto;
                                                Uri no = Uri.parse(uri);
                                                mRingtone = RingtoneManager.getRingtone(context.getApplicationContext(), no);
                                            }
                                            if (!mRingtone.isPlaying()) {
                                                mRingtone.play();
                                            }
                                        } else {
                                            Log.e("订单列表no order", sj_id);
                                            if (mRingtone != null)
                                                mRingtone.stop();
                                            //如果从有订单变成无订单，要通知刷新订单列表一次，清除旧数据
                                            if (hasNewOrder) {
                                                hasNewOrder = false;
                                                //Thread.sleep(500);
                                            } else {
                                                //重置缓存数据，当前未有新订单
                                                SPUtils.put(context, "NEW_ORDER", new Integer(0));
                                            }
                                        }
                                    } else {
                                        Log.e("订单列表noorder", sj_id);
                                        if (mRingtone != null)
                                            mRingtone.stop();
                                        //如果从有订单变成无订单，要通知刷新订单列表一次，清除旧数据
                                        if (hasNewOrder) {
                                            hasNewOrder = false;
                                            //Thread.sleep(500);
                                        } else {
                                            //重置缓存数据，当前未有新订单
                                            SPUtils.put(context, "NEW_ORDER", new Integer(0));
                                        }
                                    }
                                } else {
                                    SPUtils.put(context, "WORK_STATE", new Integer(0));
                                    Log.e("订单列表noorder", sj_id);
                                    if (mRingtone != null)
                                        mRingtone.stop();
                                    //如果从有订单变成无订单，要通知刷新订单列表一次，清除旧数据
                                    if (hasNewOrder) {
                                        hasNewOrder = false;
                                        //Thread.sleep(1000);
                                    } else {
                                        //重置缓存数据，当前未有新订单
                                        SPUtils.put(context, "NEW_ORDER", new Integer(0));
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("订单列表noorder", sj_id);
                                if (mRingtone != null)
                                    mRingtone.stop();
                                //如果从有订单变成无订单，要通知刷新订单列表一次，清除旧数据
                                if (hasNewOrder) {
                                    hasNewOrder = false;

                                } else {
                                    //重置缓存数据，当前未有新订单
                                    SPUtils.put(context, "NEW_ORDER", new Integer(0));
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("订单列表onFailure", sj_id);
                        }

                    });
        }
    };
    /**
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                locationService.stop();
                mCurretLon = location.getLongitude();
                mCurretLat = location.getLatitude();
//                ToastUtil.show("DriverService 位置改变了");

//                LogsUtil.service("mListener servive运行中..定位已发送后台");

                giveLocation(sj_id, location.getLongitude(), location.getLatitude()); // 发送位置信息给服务器
            }
        }
    };

    public DriverService() {

    }

    /*--------------  每60s给服务器发送定位信息 begin---------- */
    private void giveLocation(String uId, double longitude, double latitude) {
        mEngine.pushLocation(uId, longitude, latitude).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("servive运行中..定位已发送后台");

                //订单刷新
//                LogsUtil.service("DriverService servive运行中..定位已发送后台");
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);

//                    LogsUtil.service("DriverService servive运行中..str" + str);

                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {

//                        LogsUtil.service("DriverService servive运行中.200.");

                    } else {

                    }
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                SPUtils.put(context, "WORK_STATE", new Integer(0));//下班状态保持
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UserModel currentDriver = (UserModel) SPUtils.readObject(this, Constants.USERMODEL);
        if (currentDriver != null) {
            this.sj_id = currentDriver.getSj_id();
        }
        locationService = ((App) getApplication()).locationService;
        locationService.registerListener(mListener);

        //  LocationClientOption option = locationService.getDefaultLocationClientOption();
//        option.setOpenAutoNotifyMode();

        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        if (timerLocation == null) {
            timerLocation = new Timer();
            //  timerLocation.schedule(taskLocation, 1000, 2 * 60 * 1000); // 每2分钟刷新定位，给服务器定位信息
            timerLocation.schedule(taskLocation, 1000, 60 * 1000); // 每2分钟刷新定位，给服务器定位信息
        }
        if (timerOrder == null) {
            timerOrder = new Timer();
            timerOrder.schedule(taskOrder, 1000, 500); // 每5秒获取最新的订单
        }

        //定义一个notification
        Notification notification = new Notification();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //把该service创建为前台service
        //startForeground(1, notification);

//        flags = START_FLAG_RETRY;

        startPlayMusicService();

        return super.onStartCommand(intent, flags, startId);
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(getApplication(), PlayerMusicService.class);
        stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(getApplication(), PlayerMusicService.class);
        startService(intent);
    }


    @Override
    public void onDestroy() {
        SPUtils.remove(context, "WORK_STATE");
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
