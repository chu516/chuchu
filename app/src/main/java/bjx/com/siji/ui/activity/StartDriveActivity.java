package bjx.com.siji.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.ChargeModel;
import bjx.com.siji.model.CurrentLocation;
import bjx.com.siji.model.LatLngDisTimeModel;
import bjx.com.siji.model.LatModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.utils.CommonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.MapUtil;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.ToastUtil;
import bjx.com.siji.utils.Util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/12/25.
 */

public class StartDriveActivity extends BaseActivity {

    TextView mTVNavTool; // 导航指引
    TextView mTVCallKehu; // 联系客户
    TextView mTVCallKefu; // 联系客服
    TextView mTVGO; // 开始代驾
    TextView mTVWait; // 开始等待
    TextView mTVStop; // 结算订单
    LinearLayout mLLFare; // 计价表
    Chronometer mCMWaitTime; // 等待用时
    Chronometer mCMDriveTime; // 行驶用时
    boolean isRunWaitCM; // 等待计时是否启动
    long driveSec, waitSec;// 记录用时 单位：秒
    TextView mTVDriveKM; // 行驶里程
    TextView mTVAmount; // 行驶金额
    Integer currentState;


    LBSTraceClient mTraceClient; // 百度鹰眼轨迹服务客户端
    Trace mTrace;  // 鹰眼轨迹服务
    HistoryTrackRequest historyTrackRequest;  // 鹰眼轨迹查询
    LocRequest locRequest;

    OrderList mOrderInfo; // 接收的客户订单
    ChargeModel chargeModel; // 收费标准

    LocationService locationService; // 定位service
    boolean isFirstLoc = true; // 是否首次定位
    double mCurrentLat;  // 用户当前坐标
    double mCurrentLon;
    String mCurrentAddr;

    boolean isGathering;//是否采集位置坐标中

    @Override
    protected void initView(Bundle savedInstanceState) {
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_startdrive);


        mOrderInfo = getIntent().getParcelableExtra("orderinfo");
        trace.add(new LatModel(mOrderInfo.getOrderLong, mOrderInfo.getOrderLat));

        LogsUtil.order("进入————StartDriveActivity————");

        mCurrentLon = getIntent().getDoubleExtra("lon", 0.0);
        mCurrentLat = getIntent().getDoubleExtra("lat", 0.0);


        mCurrentAddr = getIntent().getStringExtra("addr");

        mTVNavTool = getViewById(R.id.map_tv_navigation);
        Drawable mNavToolDrawable = getResources().getDrawable(R.mipmap.navigation);
        mNavToolDrawable.setBounds(0, 0, Util.dip2px(mApp, 20), Util.dip2px(mApp, 20));
        mTVNavTool.setCompoundDrawables(mNavToolDrawable, null, null, null);

        mTVCallKehu = getViewById(R.id.map_tv_callkehu);
        Drawable mCallKehuDrawable = getResources().getDrawable(R.mipmap.tel);
        mCallKehuDrawable.setBounds(0, 0, Util.dip2px(mApp, 20), Util.dip2px(mApp, 20));
        mTVCallKehu.setCompoundDrawables(mCallKehuDrawable, null, null, null);

        mTVCallKefu = getViewById(R.id.map_tv_callkefu);
        Drawable mCallKefuDrawable = getResources().getDrawable(R.mipmap.kefu);
        mCallKefuDrawable.setBounds(0, 0, Util.dip2px(mApp, 20), Util.dip2px(mApp, 20));
        mTVCallKefu.setCompoundDrawables(mCallKefuDrawable, null, null, null);

        mTVGO = getViewById(R.id.map_tv_go);
        mTVWait = getViewById(R.id.map_tv_wait);
        mTVStop = getViewById(R.id.map_tv_stop);
        mLLFare = getViewById(R.id.map_ll_fare);
        mCMWaitTime = getViewById(R.id.map_cm_waittime);
        mCMDriveTime = getViewById(R.id.map_cm_drivetime);
        mTVDriveKM = getViewById(R.id.map_tv_km);
        mTVAmount = getViewById(R.id.map_tv_amount);

    }


    @Override
    protected void setListener() {
        mTVNavTool.setOnClickListener(this);
        mTVCallKehu.setOnClickListener(this);
        mTVCallKefu.setOnClickListener(this);
        mTVGO.setOnClickListener(this);
        mTVWait.setOnClickListener(this);
        mTVStop.setOnClickListener(this);
        mCMDriveTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
//                driveSec++;
//                mCMDriveTime.setText(DateUtils.formatMiss(driveSec));

                long time = System.currentTimeMillis() - ch.getBase();
                time -= m;
                driveSec = time / 1000;
                //代驾时间time保存
                SPUtils.put(mApp, Constants.driveTime, new Long(time));


                Date d = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);

                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mCMDriveTime.setText(sdf.format(d));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault());

                String currentDateTime = format.format(new Date());
                SPUtils.put(mApp, Constants.driveCurrentDateTime, currentDateTime);

                SPUtils.put(mApp, Constants.driveCurrentOperationType, "drive");


                LogsUtil.startDrive("存储的的已行驶时间" + time + "当前时间currentDateTime" + currentDateTime
                        + "类型" + "drive"

                );

            }
        });

        mCMWaitTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
//                waitSec++;
//                mCMWaitTime.setText(DateUtils.formatMiss(waitSec));
                long time = System.currentTimeMillis() - ch.getBase();
                time -= n;
                waitSec = time / 1000;
                //等待时间time保存
                SPUtils.put(mApp, Constants.waitTime, new Long(time));
                LogsUtil.startDrive("存储的的已等待时间" + time);

                Date d = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mCMWaitTime.setText(sdf.format(d));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault());
                String currentDateTime = format.format(new Date());
                SPUtils.put(mApp, Constants.driveCurrentDateTime, currentDateTime);

                SPUtils.put(mApp, Constants.driveCurrentOperationType, "wait");


                LogsUtil.startDrive("存储的的已等待时间" + time + "当前时间currentDateTime" + currentDateTime
                        + "类型" + "wait"
                );
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // -----------location config ------------
        locationService = mApp.locationService;
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        initTrace();
        initTrack();

        if (mCurrentLat == 0) {
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            locationService.registerListener(mBDLocListener);
            locationService.start();// 定位SDK
        } else {
            startDrive();
        }

        chargeModel = (ChargeModel) SPUtils.readObject(mApp, "charge");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            //联系客户或客服返回
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_tv_navigation: // 导航指引
                startActivity(new Intent(mApp, MainActivity.class));
//                finish();
                break;
            case R.id.map_tv_callkehu: // 联系客户
                callTel2Return(mOrderInfo.getUse_mobile());
                //finish();
                break;
            case R.id.map_tv_callkefu: // 联系客服
                callTel2Return((String) SPUtils.get(mApp, Constants.KEFU, ""));
                break;
            case R.id.map_tv_go: // 开始出发

                // 启动行驶计时，停止等待计时
                startDriveCM();
                if (isRunWaitCM) {
                    stopWaitCM();
                }
                mTVWait.setVisibility(View.VISIBLE);
                mTVGO.setVisibility(View.GONE);

                // 开启采集
                startGather();
                currentState = 1;
                SPUtils.put(mApp, Constants.currentState, new Integer(currentState));
                break;

            case R.id.map_tv_wait: // 开始等待
                // 启动等待计时，停止行驶计时
                startWaitCM();
                stopDriveCM();
                if (!isRunWaitCM) {
                    isRunWaitCM = true;
                }
                mTVWait.setVisibility(View.GONE);
                mTVGO.setVisibility(View.VISIBLE);

                // 停止采集
//                stopGather();
                currentState = 2;
                SPUtils.put(mApp, Constants.currentState, new Integer(currentState));
                break;
            case R.id.map_tv_stop: // 结算订单
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("确定要结算订单吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopTrace(); // 停止轨迹服务
                                //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的

                                //重新定位 结算订单
                                locationService.registerListener(mBDLocListener2);
                                //注册监听
                                int type = getIntent().getIntExtra("from", 0);
                                if (type == 0) {
                                    locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                                } else if (type == 1) {
                                    locationService.setLocationOption(locationService.getOption());
                                }
                                locationService.start();// 定位SDK
//                                endDrive();
//                                quertHisTrack(System.currentTimeMillis()/1000 - driveSec, System.currentTimeMillis()/1000);
//                                startActivity(new Intent(mApp, TallyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
                break;
            default:
                break;
        }
    }

    // 开始代驾/开始出发
    private void startDrive() {
        mEngine.startDrive(userModel.getSj_id(), mOrderInfo.getOrder_id(), mCurrentAddr, String.valueOf(mCurrentLon), String.valueOf(mCurrentLat),
                MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + mOrderInfo.getOrder_id() + mCurrentLon).getBytes())).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    LogsUtil.order("点击到达位置，进入开始代驾-开始代驾/开始出发接口返回-" + str);
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");

                    if (status == 200) {

                        ToastUtil.show("开始代驾");

                        // 开启轨迹服务
                        startTrace();
                        mLLFare.setVisibility(View.VISIBLE); // 显示计价器
                    } else if (status == 202) {
                        showToast("订单不存在");
                    } else if (status == 203) {
//                        showToast("订单存在异常");
                    } else if (status == 100 || status == 101) {
                        showToast("请求失败");
                    } else {
                        showToast("请求异常");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    // 结算订单接口
    private void endDrive() {
        String sjId = userModel.getSj_id();
        String orderId = mOrderInfo.getOrder_id();
        String testTrace = "['117.0644939269,30.5529376046','117.0642793502,30.5518657840','117.0646870460,30.5508678714','117.0663919474,30.5502973980']";
        testTrace = "[";
        for (int i = 0; i < trace.size(); i++) {
            if (i != 0) {
                testTrace += ",'" + trace.get(i).getLongitude() + "," + trace.get(i).getLatitude() + "'";
            } else {
                testTrace += "'" + trace.get(i).getLongitude() + "," + trace.get(i).getLatitude() + "'";
            }
        }
        testTrace += "]";
        if (totalPrice == 0) {
            drivePrice = new Double(chargeModel.getSprice());
            totalPrice = drivePrice;
        }
//        totalPrice += new Double(chargeModel.getSafe_kh()).doubleValue(); // 加上客户保险
        String key = MD5.getMessageDigest((sjId + Constants.BASE_KEY + orderId + totalPrice).getBytes());// (sj_id."Yxdj!@#2468".order_id .total_price))

        LogsUtil.order("userModel.getSj_id()" + userModel.getSj_id()

                + "-- chargeModel.getKilometres()" + chargeModel.getKilometres()
                + "--chargeModel.getSprice()" + chargeModel.getSprice()

                + "--mOrderInfo.getOrder_id()" + mOrderInfo.getOrder_id()
                + "--mCurrentAddr" + mCurrentAddr
                + "--mCurrentLon" + mCurrentLon

                + "--mCurrentLat" + mCurrentLat
                + "--totalPrice" + totalPrice

                + "--driveSec" + driveSec
                + "--driveDistance" + driveDistance

                + "--drivePrice" + drivePrice

                + "--waitSec" + waitSec
                + "--waitPrice" + waitPrice

                + "--testTrace" + testTrace
                + "--key" + key
        );

        mEngine.EndDrivr(userModel.getSj_id(), chargeModel.getKilometres(), chargeModel.getSprice(),

                mOrderInfo.getOrder_id(),
                mCurrentAddr, String.valueOf(mCurrentLon), String.valueOf(mCurrentLat), String.valueOf(totalPrice),
                String.valueOf(driveSec), String.valueOf(driveDistance), String.valueOf(drivePrice), String.valueOf(waitSec),
                String.valueOf(waitPrice), chargeModel.getSafe_kh(), testTrace, key).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");

                    if (status == 200) {
                        startActivity(new Intent(mApp, TallyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT).putExtra("orderinfo", mOrderInfo));
                        if (MainActivity.mapFragment != null)
                            MainActivity.mapFragment.resetMap();

                        MainActivity.mapFragment.nearbySj(mCurrentLon, mCurrentLat);

                        finish();

                        //resetMap(); // 重置地图界面
                    } else if (status == 202) {
                        showToast("订单不存在");
                    } else if (status == 203) {
                        showToast("订单异常");
                    } else if (status == 101) {
                        showToast("结算订单失败");
                    } else {
                        showToast("结算订单异常");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    /*--------------计时器方法 begin-------------------*/
    int i = 0;
    int j = 0;
    long m = 0;
    long n = 0;
    long recordWaitTime, recordDriveTime; // 用于记录上次计时器的用时--- 其他计时方法变量，废弃

    // 启动等待用时
    private void startWaitCM() {
//        long hour = (int) ((SystemClock.elapsedRealtime() - mCMWaitTime.getBase()) / 1000 / 60);
//        hour = (SystemClock.elapsedRealtime() - recordWaitTime) / 60*60*1000;
//        mCMWaitTime.setFormat("0" + String.valueOf(0)+":%s");

        if (i == 0) {
            mCMWaitTime.setBase(System.currentTimeMillis());
            recordWaitTime = System.currentTimeMillis();

            //等待开始时间保存
            SPUtils.put(mApp, Constants.recordWaitTime, new Long(recordWaitTime));
//            mCMWaitTime.setBase(SystemClock.elapsedRealtime());
//            mCMWaitTime.setFormat("0" + String.valueOf(0)+":%s");
            i++;
        } else {
            n += System.currentTimeMillis() - recordWaitTime;
        }
        mCMWaitTime.start();
    }

    // 启动行驶用时
    private void startDriveCM() {
//        long hour = (int) ((SystemClock.elapsedRealtime() - mCMDriveTime.getBase()) / 1000 / 60);
//        hour = (SystemClock.elapsedRealtime() - recordDriveTime) / 60*60*1000;
//        mCMDriveTime.setFormat("0" + String.valueOf(0)+":%s");

        if (j == 0) {
            mCMDriveTime.setBase(System.currentTimeMillis());
            recordDriveTime = System.currentTimeMillis();

            //代驾开始时间保存
            SPUtils.put(mApp, Constants.recordDriveTime, new Long(recordDriveTime));

//            mCMDriveTime.setBase(SystemClock.elapsedRealtime());
//            mCMDriveTime.setFormat("0" + String.valueOf(0)+":%s");
            j++;
        } else {
            m += System.currentTimeMillis() - recordDriveTime;
        }
        mCMDriveTime.start();
    }

    // 停止等待用时
    private void stopWaitCM() {
        mCMWaitTime.stop();
        recordWaitTime = System.currentTimeMillis(); // 保存这次等待的时间
    }

    // 停止行驶用时
    private void stopDriveCM() {
        mCMDriveTime.stop();
        recordDriveTime = System.currentTimeMillis(); // 保存这次行驶的时间
    }
    /*--------------计时器方法 end-------------------*/


    /**
     * -----------------鹰眼轨迹begin--------------------
     */
    // 初始化百度鹰眼服务
    private void initTrace() {
        // 设备标识
        String entityName = "myTrace";

        UserModel currentDriver = (UserModel) SPUtils.readObject(App.getInstance(), Constants.USERMODEL);
        if (currentDriver != null) {
            entityName = currentDriver.getMobile();
        }

        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        mTrace = new Trace(Constants.BD_TRACE_SERVICEID, entityName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(mApp);
        mTraceClient.setOnTraceListener(mTraceListener);

        // 定位周期(单位:秒)
        int gatherInterval = 5;
        // 打包回传周期(单位:秒)
        int packInterval = 10;
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        locRequest = new LocRequest(Constants.BD_TRACE_SERVICEID);
    }

    // 开启服务   开启鹰眼服务，启动鹰眼 service
    private void startTrace() {
        //开始轨迹采集
        mTraceClient.startTrace(mTrace, null);
    }

    // 开启采集
    private void startGather() {
        if (!isGathering) {
            isGathering = true;
            mTraceClient.startGather(null);
        }

    }

    // 停止采集
    private void stopGather() {
        mTraceClient.stopGather(mTraceListener);
    }

    // 停止服务
    private void stopTrace() {
        mTraceClient.stopTrace(mTrace, null);
    }

    // 初始化轨迹服务监听器
    OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            SPUtils.put(mApp, "is_trace_started", true);
        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            SPUtils.put(mApp, "is_trace_started", false);
            SPUtils.put(mApp, "is_gather_started", false);
            stopTimer();
        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            SPUtils.put(mApp, "is_gather_started", true);
            startTimer();
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            SPUtils.put(mApp, "is_gather_started", false);
            stopTimer();
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
        }

        @Override
        public void onInitBOSCallback(int i, String s) {
        }
    };

    // 初始化鹰眼轨迹查询
    private void initTrack() {
        // 请求标识
        int tag = 1;
        // 设备标识
        String entityName = "myTrace";

        UserModel currentDriver = (UserModel) SPUtils.readObject(App.getInstance(), Constants.USERMODEL);
        if (currentDriver != null) {
            entityName = currentDriver.getMobile();
        }

        // 创建历史轨迹请求实例
        historyTrackRequest = new HistoryTrackRequest(tag, Constants.BD_TRACE_SERVICEID, entityName);

        //设置轨迹查询起止时间
        // 开始时间(单位：秒)
        long startTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;
        // 结束时间(单位：秒)
        long endTime = System.currentTimeMillis() / 1000;
        // 设置开始时间
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);
    }

    /**
     * 获取当前位置
     */
//    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
//        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
//        if (NetUtil.isNetworkAvailable(mApp)
//                && (Boolean) SPUtils.get(mApp, "is_trace_started", false)
//                && (Boolean) SPUtils.get(mApp, "is_gather_started", false)) {
//            LatestPointRequest request = new LatestPointRequest(getBDTag(), Constants.BD_TRACE_SERVICEID, "myTrace");
//            ProcessOption processOption = new ProcessOption();
//            processOption.setNeedDenoise(true);
//            processOption.setRadiusThreshold(100);
//            request.setProcessOption(processOption);
//            mTraceClient.queryLatestPoint(request, trackListener);
//        } else {
//            mTraceClient.queryRealTimeLoc(locRequest, entityListener);
//        }
//    }

    // 查询历史轨迹
    private void quertHisTrack(long stime, long etime) {
        historyTrackRequest.setStartTime(stime);
        historyTrackRequest.setEndTime(etime);
        mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
    }

    private void quertLatestPointTrack() {
        String entityName = "myTrace";

        UserModel currentDriver = (UserModel) SPUtils.readObject(App.getInstance(), Constants.USERMODEL);
        if (currentDriver != null) {
            entityName = currentDriver.getMobile();
        }
        mTraceClient.queryLatestPoint(new LatestPointRequest(2, Constants.BD_TRACE_SERVICEID, entityName), mTrackListener);
    }

    List<LatModel> trace = new ArrayList<>();

    // 初始化轨迹监听器
    OnTrackListener mTrackListener = new OnTrackListener() {
        // 历史轨迹回调
        @Override
        public void onHistoryTrackCallback(HistoryTrackResponse response) {
            showToast("查询历史轨迹成功");
            String trace = response.getTrackPoints().toString();
//            endDrive(trace);
        }

        // 查询最新轨迹点回调接口
        @Override
        public void onLatestPointCallback(LatestPointResponse response) {


            if (StatusCodes.SUCCESS != response.getStatus()) {
                return;
            }

            LatestPoint point = response.getLatestPoint();
            if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                    .getLongitude())) {
                return;
            }

            LatLng currentLatLng = MapUtil.convertTrace2Map(point.getLocation());
            if (null == currentLatLng) {
                return;
            }
            CurrentLocation.locTime = point.getLocTime();
            CurrentLocation.latitude = currentLatLng.latitude;
            CurrentLocation.longitude = currentLatLng.longitude;
//
//            ToastUtil.show("当前点坐标currentLatLng" + "currentLatLng-latitude" +
//                    currentLatLng.latitude + "currentLatLng-longitude" +
//                    currentLatLng.longitude + "");

            comMonalr(currentLatLng);

//            if (null != mapUtil) {
//                mapUtil.updateStatus(currentLatLng, true);
//            }
        }
    };
    /**
     * -----------------鹰眼轨迹end--------------------
     */

    // 查询轨迹最后位置
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                quertLatestPointTrack();
            }
            super.handleMessage(msg);
        }

        ;
    };

    boolean isFirstQueryTrace = true; // 是否首次查询轨迹
    LatLng preLat; // 轨迹查询上一次保存的经纬度
    double driveDistance = 0; // 行驶里程 单位：m
    double waitPrice = 0;  // 等待价格
    double drivePrice = 0; // 行驶价格
    double totalPrice = 0; // 总价格
    int traceLocateTime = 0;//轨迹定位次数
    int cancelPoint = 0;

    private void comMonalr(LatLng currentLat) {
        traceLocateTime++;
        if (traceLocateTime % 180 == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("当前已超过15分钟未做任何操作，点击确定继续代驾")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.create().show();
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat df2 = new DecimalFormat("#0.0");
        double currentDistance = 0;

        if (isFirstQueryTrace) { //第一次起点和终点一样
            preLat = currentLat;
            driveDistance = DistanceUtil.getDistance(currentLat, currentLat);
            isFirstQueryTrace = false;
        } else {
            currentDistance = DistanceUtil.getDistance(currentLat, preLat);
            driveDistance += currentDistance;
        }

        //当前行驶时间最大行驶距离判断 （w）车速过快 距离会有偏差
        double maxS = driveSec * ((70 * 1000) / 3600);

        if (driveDistance >= maxS) {
            driveDistance -= currentDistance;//还原
            //行驶里程保存
            SPUtils.put(mApp, Constants.driveDistance, new Double(driveDistance));
            if (traceLocateTime > 5) {
                return;//前5次定位误差大，重新定位确定出发点位置
            }
        }

//         driveDistance = 5 * 1000;
        //行驶里程保存
        SPUtils.put(mApp, Constants.driveDistance, new Double(driveDistance));

        LatLngDisTimeModel latLngDisTimeModel = new LatLngDisTimeModel(driveDistance, "");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(latLngDisTimeModel); //将对象转换成Json
        PreferenceUtil.putDriveDistanceTimeSharePreference(StartDriveActivity.this,
                Constant.CONFIG_INTENT_ALREADY_DIS_TIME, jsonStr);

        preLat = currentLat;

        //轨迹点保存
        SPUtils.saveObject(mApp, Constants.preLoc, preLat);

        Gson gsonLat = new Gson();
        String gsonLatStr = gsonLat.toJson(preLat); //将对象转换成Json
        PreferenceUtil.putLatlngPreference(StartDriveActivity.this,
                Constant.CONFIG_INTENT_LAT_LNG, gsonLatStr);

        //这里的轨迹点 没有
        if (traceLocateTime >= 5) {//保留开始代驾后的第5个定位坐标点作为代驾起始点
            if (trace.size() < 2) {
                trace.add(new LatModel(String.valueOf(currentLat.longitude), String.valueOf(currentLat.latitude)));
            }
        }

        double driveDisKm = driveDistance / 1000;
        double chaoChuDriveDisKm = driveDisKm - new Double(chargeModel.getKilometres()).doubleValue();

//        chaoChuDriveDisKm = chaoChuDriveDisKm / 5;  //按每5公里收费

        // 计算行驶价格
        if (chaoChuDriveDisKm <= 0) {
            drivePrice = new Double(chargeModel.getSprice()).doubleValue();
        } else {
            double chaochuPrice = Math.ceil(chaoChuDriveDisKm) * (new Double(chargeModel.getUprice()).doubleValue());
            drivePrice = chaochuPrice + new Double(chargeModel.getSprice()).doubleValue();
        }

        // 计算等待价格
        long chaochuWait = waitSec - Integer.valueOf(chargeModel.getWait_time()) * 60;
        if (chaochuWait > 0) {
            waitPrice = 0;
            do {
                waitPrice += Integer.valueOf(chargeModel.getWait_money());
                chaochuWait -= Integer.valueOf(chargeModel.getWait_time()) * 60;
            } while (chaochuWait > 0);
        }

        double bx = Double.parseDouble(chargeModel.getSafe_kh());
        totalPrice = Double.parseDouble(df.format(waitPrice + drivePrice + bx));

        //公里数超出8公里 加价30%
        if (driveDisKm > 8) {
            totalPrice = totalPrice * 1.3;
        }

        mTVAmount.setText(df.format(totalPrice));
        mTVDriveKM.setText(df2.format(driveDisKm) + "km");

    }

    Timer mTimer; // 定时器，查询鹰眼轨迹
    TimerTask mTimerTask;

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 1000, 5000); // 每5秒执行任务
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    // 百度定位监听 开始代驾
    private BDAbstractLocationListener mBDLocListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            locationService.unregisterListener(mBDLocListener); //注销掉监听
            locationService.stop();// 停止定位
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                showToast("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                showToast("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                showToast("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                showToast("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                return;
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                showToast("网络不通导致定位失败，请检查网络是否通畅");
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                showToast("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                return;
            }

            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAddr = location.getAddrStr();

            startDrive();

        }
    };

    // 百度定位监听 结算订单
    private BDAbstractLocationListener mBDLocListener2 = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                locationService.unregisterListener(mBDLocListener2); //注销掉监听
                locationService.stop();// 停止定位
            } catch (Exception e) {
                showToast(e.toString());
            }

            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                showToast("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                showToast("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                showToast("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                showToast("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                return;
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                showToast("网络不通导致定位失败，请检查网络是否通畅");
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                showToast("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                return;
            }

            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAddr = location.getAddrStr();
            trace.add(new LatModel(mCurrentLon + "", mCurrentLat + ""));//结束代驾位置

            endDrive();
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        // startActivity(new Intent(mApp, MainActivity.class));
    }

    @Override
    public void onStop() {
        locationService.unregisterListener(mBDLocListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTrace();
    }
}
