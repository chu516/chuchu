package bjx.com.siji.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.ProcessOption;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.engine.Engine;
import bjx.com.siji.model.ItemInfo;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.activity.TracingActivity;
import bjx.com.siji.utils.NetUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;
import com.lzy.okgo.OkGo;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static App sInstance;
    private Engine mEngine;

    public LocationService locationService;
    public Vibrator mVibrator;
    public SharedPreferences trackConf = null;

    public boolean isRegisterReceiver = false;
    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;
    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;
    public List<ItemInfo> itemInfos = new ArrayList<>();
    private Notification notification = null;

    /**
     * 轨迹服务
     */
    public Trace mTrace = null;
    /**
     * Entity标识
     */
    public String   entityName = "myTrace";
    /**
     * 轨迹服务ID
     */
    public long serviceId = 210736;
    private LocRequest locRequest = null;
    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        CrashHandler crashHandler  = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext()); //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器

        mEngine = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Engine.class);

        /***
         * 初始化定位sdk，建议在Application中创建
         */

        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

//        initNotification();
//        mClient = new LBSTraceClient(this);
//        mTrace = new Trace(serviceId, entityName);
//        mTrace.setNotification(notification);
//
//        locRequest = new LocRequest(serviceId);
//        trackConf = getSharedPreferences("track_conf", MODE_PRIVATE);


        OkHttpUtils.getInstance()
                .init(this)
                .debug(true, "okHttp")
                .timeout(20 * 1000);
        OkGo.getInstance().init(this);
    }

    public static App getInstance() {
        return sInstance;
    }

    public Engine getEngine() {
        return mEngine;
    }

    /**
     * 封装公共参数
     * <p>
     */
    public class CommonInterceptor implements Interceptor {

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request oldRequest = chain.request();

            String random = String.valueOf(new Random().nextInt(10000));
            // 添加新的参数
            HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
//                    .addQueryParameter(Constants.PUBLIC_PARAM1, random)
                    .addQueryParameter(Constants.PUBLIC_PARAM2, String.valueOf(SPUtils.get(getInstance(), "fid", "-1")))
                    .addQueryParameter(Constants.PUBLIC_PARAM3, Util.getVersion(getInstance()))
                    .addQueryParameter(Constants.PUBLIC_PARAM4, "android")
                    .addQueryParameter(Constants.PUBLIC_PARAM5, Util.getPhoneSign(getInstance()))
                    .addQueryParameter(Constants.PUBLIC_PARAM6, String.valueOf(SPUtils.get(getInstance(), Constants.CHANNELID, "-1")));

            // 新的请求
            Request newRequest = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(authorizedUrlBuilder.build())
                    .build();

            return chain.proceed(newRequest);
        }
    }

    public OkHttpClient getOkHttpClient() {
        Interceptor interceptor = new CommonInterceptor();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .build();
        return client;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, TracingActivity.class);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.icon_tracing);

        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setLargeIcon(icon)  // 设置下拉列表中的图标(大图标)
                .setContentTitle("百度鹰眼") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.icon_tracing) // 设置状态栏内的小图标
                .setContentText("服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        UserModel currentDriver = (UserModel) SPUtils.readObject(App.getInstance(), Constants.USERMODEL);
        if (currentDriver != null) {
            entityName = currentDriver.getMobile();
        }

        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(this)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }


    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public void clear() {
        itemInfos.clear();
    }
}