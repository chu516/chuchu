package bjx.com.siji.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.ChargeModel;
import bjx.com.siji.model.CurrentLocation;
import bjx.com.siji.model.DriverModel;
import bjx.com.siji.model.LatModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.overlayutil.DrivingRouteOverlay;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.activity.BNDemoGuideActivity;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.ui.activity.NearbySJListActivity;
import bjx.com.siji.ui.activity.StartDriveActivity;
import bjx.com.siji.ui.activity.TallyOrderActivity;
import bjx.com.siji.utils.CommonUtil;
import bjx.com.siji.utils.DateUtils;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.MapUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MapFragment extends BaseFragment implements BaiduMap.OnMapClickListener {

    TextView mTVNav; // 导航
    TextView mTVTitle; // 标题
    TextView mTVSDrive; // 开始代驾

    TextView mTVRedirect; // 转发


    ImageView mIVPosition; // 定位
    TextView mTVSetEndLoc; // 开始导航--设置目的地
    TextView mTVResetEndLoc; // 重置终点
    TextView mTVNavTool; // 导航指引
    TextView mTVCallKehu; // 联系客户
    TextView mTVCallKefu; // 联系客服
    TextView mTVGO; // 开始代驾
    TextView mTVWait; // 开始等待
    TextView mTVStop; // 结算订单
    RelativeLayout mRLNavTool; // 导航指引工具条
    LinearLayout mLLFare; // 计价表
    Chronometer mCMWaitTime; // 等待用时
    Chronometer mCMDriveTime; // 行驶用时
    boolean isRunWaitCM; // 等待计时是否启动
    long recordWaitTime, recordDriveTime; // 用于记录上次计时器的用时--- 其他计时方法变量，废弃
    int driveSec, waitSec;// 记录用时 秒
    TextView mTVDriveKM; // 行驶里程
    TextView mTVAmount; // 行驶金额

    TextureMapView mMapView;  // 地图view
    BaiduMap mBaiduMap;


    BitmapDescriptor mCurrentMarker; // 当前定位点
    BitmapDescriptor mStartCurrentMarker; // 出发点
    BitmapDescriptor mEndCurrentMarker; // 目标点
    LocationService locationService; // 定位service
    RoutePlanSearch mRoutePlanSearch; // 百度路径规划search

    LBSTraceClient mTraceClient; // 百度鹰眼轨迹服务客户端
    Trace mTrace;  // 鹰眼轨迹服务
    HistoryTrackRequest historyTrackRequest;  // 鹰眼轨迹查询
    LocRequest locRequest;

    boolean isFirstLoc = true; // 是否首次定位
    double mCurrentLat;  // 用户当前坐标
    double mCurrentLon;
    String mCurrentAddr;

    double mEndLat; // 用户目的地坐标
    double mEndLon;

    double mResetEndLat; // 用户重置的坐标
    double mResetEndLon;

    boolean isResetEndLoc = false;// 是否重置目的地

    OrderList mOrderInfo; // 接收的客户订单
    ChargeModel chargeModel; // 收费标准
    List<DriverModel> mDriverList = new ArrayList<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
//        setContentView(R.layout.fragment_map2);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.fragment_map2, null);

        //获取地图控件引用
        mMapView = getViewById(R.id.map_mapview);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);
        mBaiduMap = mMapView.getMap();
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo); // 设置自定义定位点
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
        mBaiduMap.setMyLocationEnabled(true);
        mTVNav = getViewById(R.id.map_tv_nav);
        mTVTitle = getViewById(R.id.map_title);
        mTVSDrive = getViewById(R.id.map_sdrive);

        mTVRedirect = getViewById(R.id.map_redirect);

        mIVPosition = getViewById(R.id.main_iv_position);
        mTVSetEndLoc = getViewById(R.id.map_tv_setendloc);
        mTVResetEndLoc = getViewById(R.id.map_tv_resetendloc);

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
        mRLNavTool = getViewById(R.id.map_rl_navtool);
        mLLFare = getViewById(R.id.map_ll_fare);
        mCMWaitTime = getViewById(R.id.map_cm_waittime);
        mCMDriveTime = getViewById(R.id.map_cm_drivetime);
        mTVDriveKM = getViewById(R.id.map_tv_km);
        mTVAmount = getViewById(R.id.map_tv_amount);
    }

    @Override
    protected void setListener() {
        mTVNav.setOnClickListener(this);
        mTVSDrive.setOnClickListener(this);

        mTVRedirect.setOnClickListener(this);

        mTVTitle.setOnClickListener(this);
        mIVPosition.setOnClickListener(this);
        mTVSetEndLoc.setOnClickListener(this);
        mTVResetEndLoc.setOnClickListener(this);
        mTVNavTool.setOnClickListener(this);
        mTVCallKehu.setOnClickListener(this);
        mTVCallKefu.setOnClickListener(this);
        mTVGO.setOnClickListener(this);
        mTVWait.setOnClickListener(this);
        mTVStop.setOnClickListener(this);
        mBaiduMap.setOnMapClickListener(this);
        mCMDriveTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                driveSec++;
                ch.setText(DateUtils.formatMiss(driveSec));
            }
        });
        mCMWaitTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                waitSec++;
                ch.setText(DateUtils.formatMiss(waitSec));
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // -----------location config ------------
        locationService = mApp.locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mBDLocListener);
        //注册监听
        int type = getActivity().getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK

//        initTrace(); // 初始化轨迹服务
//        initTrack(); // 初始化轨迹查询
        if (initDirs()) { // 初始化导航服务
            initNavi();
        }
        activityList.add(mActivity);

        // 初始化路径规划
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);

        // 设置起点、终点大头针
        mStartCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        mEndCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_en);

        chargeModel = (ChargeModel) SPUtils.readObject(mApp, "charge");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_tv_nav: // 导航  ---司机到客户
//                PlanNode startNode = PlanNode.withLocation(new LatLng(mCurrentLat, mCurrentLon));
//                PlanNode endNode = PlanNode.withLocation(new LatLng(new Double(mOrderInfo.getSt_lat()), new Double(mOrderInfo.getSt_long())));
//                mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
//                        .from(startNode)
//                        .to(endNode));

                //传入结束的订单 当前地址由定位得到的
                if (mOrderInfo != null)
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL,
                            new Double(mOrderInfo.getSt_long()),
                            new Double(mOrderInfo.getSt_lat()));
                else {
                }
                break;
            case R.id.map_sdrive: // 开始代驾
//                startDrive();

//                LogsUtil.normal("到达位置———MapFragment—————");
                startActivity(new Intent(mApp, StartDriveActivity.class).putExtra("orderinfo", mOrderInfo).
                        putExtra("lon", mCurrentLon).putExtra("lat", mCurrentLat).
                        putExtra("addr", mCurrentAddr));
                mTVNav.setVisibility(View.GONE);
                mRLNavTool.setVisibility(View.VISIBLE);

                break;

            case R.id.map_redirect: // 转发
//                startDrive();

                startActivity(new Intent(mApp, NearbySJListActivity.class).
                        putExtra("orderinfo", mOrderInfo).putExtra("lon", mCurrentLon).
                        putExtra("lat", mCurrentLat).putExtra("addr", mCurrentAddr));

                break;

            case R.id.main_iv_position: // 定位
                locationService.registerListener(mBDLocListener);
                locationService.start();
                break;
            case R.id.map_tv_setendloc: // 开始导航
//                PlanNode stNode = PlanNode.withLocation(new LatLng(mCurrentLat, mCurrentLon));
//                PlanNode enNode = PlanNode.withLocation(new LatLng(mResetEndLat, mResetEndLon));
//                mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
//                        .from(stNode)
//                        .to(enNode));
                if (mResetEndLon != 0)
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, mResetEndLon, mResetEndLat);
                else {
                    showToast("请先设置导航终点");
                }
                break;
            case R.id.map_tv_resetendloc: // 重置目的地
//                resetEndLoc(true);
//                showToast("请在地图上点击您的目的地");
//                startActivity(new Intent(getActivity(), SearchLocActivity.class));
                ((MainActivity) mActivity).mRLSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.map_tv_navigation: // 导航指引
                mRLNavTool.setVisibility(View.VISIBLE);
                break;
            case R.id.map_tv_callkehu: // 联系客户
                mActivity.callTel(mOrderInfo.getUse_mobile());
                break;
            case R.id.map_tv_callkefu: // 联系客服
                mActivity.callTel((String) SPUtils.get(mApp, Constants.KEFU, ""));
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
                stopGather();
                break;
            case R.id.map_tv_stop: // 结算订单
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle("提示")
                        .setMessage("确定要结算订单吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopTrace(); // 停止轨迹服务
                                endDrive();
//                                quertHisTrack(System.currentTimeMillis()/1000 - driveSec, System.currentTimeMillis()/1000);
//                                startActivity(new Intent(mApp, TallyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.map_title: // 联系客户
                if (mOrderInfo != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mOrderInfo.getUse_mobile()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        // 开启轨迹服务
                        startTrace();
                        mLLFare.setVisibility(View.VISIBLE); // 显示计价器
                        System.out.println("代驾显示计价器");
                        mTVSDrive.setVisibility(View.GONE);
                    } else if (status == 202) {
                        showToast("订单不存在");
                    } else if (status == 203) {
                        showToast("订单存在异常");
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
        String key = MD5.getMessageDigest((sjId + Constants.BASE_KEY + orderId + totalPrice).getBytes());// (sj_id."Yxdj!@#2468".order_id .total_price))

        LogsUtil.order("userModel.getSj_id()" + userModel.getSj_id()

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
        mEngine.EndDrivr(userModel.getSj_id(), chargeModel.getKilometres(),chargeModel.getSprice(),


                mOrderInfo.getOrder_id(), mCurrentAddr,
                String.valueOf(mCurrentLon), String.valueOf(mCurrentLat), String.valueOf(totalPrice),
                String.valueOf(driveSec), String.valueOf(driveDistance), String.valueOf(drivePrice),
                String.valueOf(waitSec), String.valueOf(waitPrice), "1", testTrace, key).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");

                    LogsUtil.order("后台结算返回数据 str" + str);

                    if (status == 200) {

                        startActivity(new Intent(mApp, TallyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("orderinfo", mOrderInfo));

                        resetMap(); // 重置地图界面
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

    // 结束代驾重置地图 订单结算的时候重置了 地图
    public void resetMap() {
        mOrderInfo = null;
        ((MainActivity) mActivity).setOrderInfo(null);
//        stopTrace();
        mLLFare.setVisibility(View.GONE); // 隐藏计价器
        mRLNavTool.setVisibility(View.GONE);
        mTVSDrive.setVisibility(View.GONE);

        mTVRedirect.setVisibility(View.GONE);

        resetEndLoc(false);
        mResetEndLat = 0;
        mResetEndLon = 0;

//        stopWaitCM();
//        stopDriveCM();
        driveSec = 0;
        waitSec = 0;
//        mCMWaitTime.setText(DateUtils.formatMiss(waitSec));
//        mCMDriveTime.setText(DateUtils.formatMiss(driveSec));

        isRunWaitCM = false;
        mTVWait.setVisibility(View.GONE);
        mTVGO.setVisibility(View.VISIBLE);

        isFirstQueryTrace = true; // 是否首次查询轨迹
        preLat = null; // 轨迹查询上一次保存的经纬度
        driveDistance = 0; // 行驶里程 单位：m
        waitPrice = 0;  // 等待价格
        drivePrice = 0; // 行驶价格
        totalPrice = 0; // 总价格
//        trace.clear();
        mBaiduMap.clear();
        mTVAmount.setText("0.00");
        mTVDriveKM.setText("0.00km");
        mTVTitle.setVisibility(View.GONE);
     }

    // 设置当前是否为重置目的地状态
    private void resetEndLoc(boolean isReset) {
        isResetEndLoc = isReset;
    }


    /*--------------计时器方法 begin-------------------*/
    // 启动等待用时
    private void startWaitCM() {
//        mCMWaitTime.setBase(SystemClock.elapsedRealtime() - recordWaitTime);
//        long hour = (int) ((SystemClock.elapsedRealtime() - mCMWaitTime.getBase()) / 1000 / 60);
//        hour = (SystemClock.elapsedRealtime() - recordWaitTime) / 60*60*1000;
//        mCMWaitTime.setFormat(String.valueOf(hour)+":%s");
        mCMWaitTime.start();
    }

    // 启动行驶用时
    private void startDriveCM() {
//        mCMDriveTime.setBase(SystemClock.elapsedRealtime() - recordDriveTime);
//        long hour = (int) ((SystemClock.elapsedRealtime() - mCMDriveTime.getBase()) / 1000 / 60);
//        hour = (SystemClock.elapsedRealtime() - recordWaitTime) / 60*60*1000;
//        mCMDriveTime.setFormat("0" + String.valueOf(hour)+":%s");
//        mCMDriveTime.start(); // 此处计时其他方法 还有问题
        mCMDriveTime.start();
    }

    // 停止等待用时
    private void stopWaitCM() {
        mCMWaitTime.stop();
//        recordWaitTime = SystemClock.elapsedRealtime() - mCMWaitTime.getBase(); // 保存这次等待的时间
    }

    // 停止行驶用时
    private void stopDriveCM() {
        mCMDriveTime.stop();
//        recordDriveTime = SystemClock.elapsedRealtime() - mCMDriveTime.getBase(); // 保存这次行驶的时间
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

    // 开启服务
    private void startTrace() {
        mTraceClient.startTrace(mTrace, null);
    }

    // 开启采集
    private void startGather() {
        mTraceClient.startGather(null);
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

//    /**
//     * 获取当前位置
//     */
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

    private void comMonalr(LatLng currentLat) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (isFirstQueryTrace) {
            preLat = currentLat;
            driveDistance = DistanceUtil.getDistance(currentLat, currentLat);
            isFirstQueryTrace = false;
        } else {
            driveDistance += DistanceUtil.getDistance(currentLat, preLat);
            preLat = currentLat;
        }
        trace.add(new LatModel(String.valueOf(currentLat.longitude), String.valueOf(currentLat.latitude)));

        double driveDisKm = driveDistance / 1000;
        double chaoChuDriveDisKm = driveDisKm - new Double(chargeModel.getKilometres()).doubleValue();

        // 计算行驶价格
        if (chaoChuDriveDisKm <= 0) {
            drivePrice = new Double(chargeModel.getSprice()).doubleValue();
        } else {
            double chaochuPrice = chaoChuDriveDisKm * (new Double(chargeModel.getUprice()).doubleValue());
            drivePrice = chaochuPrice + new Double(chargeModel.getSprice()).doubleValue();
        }

        // 计算等待价格
        double chaochuWait = waitSec - Integer.valueOf(chargeModel.getWait_time());
        while (chaochuWait > 0) {
            waitPrice += Integer.valueOf(chargeModel.getWait_money());
            chaochuWait -= Integer.valueOf(chargeModel.getWait_time());
        }

        totalPrice = Double.parseDouble(df.format(waitPrice + drivePrice));
        mTVAmount.setText(df.format(totalPrice));
        mTVDriveKM.setText(df.format(driveDisKm) + "km");
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

    // ---------导航begin----------
    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String APP_FOLDER_NAME = "BNSDKYiXiang";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";

    private final static String authBaseArr[] =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    private final static String authComArr[] = {Manifest.permission.READ_PHONE_STATE};
    private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;
    private String mSDCardPath = null;
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    private BNRoutePlanNode.CoordinateType mCoordinateType = null;
    String authinfo = null;

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }


        BaiduNaviManager.getInstance().init(mActivity, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
            }

            public void initSuccess() {
//                showToast("百度导航引擎初始化成功");
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
//                showToast("百度导航引擎初始化开始");
            }

            public void initFailed() {
//                showToast("百度导航引擎初始化失败");
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        BNaviSettingManager.setIsAutoQuitWhenArrived(true);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "15878470");  //"10419989"
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub
        PackageManager pm = mActivity.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, mActivity.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, double lon, double lat) {
        mCoordinateType = coType;
        if (!hasInitSuccess) {
            showToast("还未初始化");
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    showToast("没有完备的权限!");
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(mCurrentLon, mCurrentLat, null, null, coType);
                eNode = new BNRoutePlanNode(lon, lat, null, null, coType);
                break;
            }
            default:
                break;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);

            // 开发者可以使用旧的算路接口，也可以使用新的算路接口,可以接收诱导信息等
            // BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
            BaiduNaviManager.getInstance().launchNavigator(mActivity, list, 1, true, new DemoRoutePlanListener(sNode),
                    eventListerner);
        }
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub
        PackageManager pm = mActivity.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, mActivity.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(mApp, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            showToast("算路失败");
        }
    }

    BaiduNaviManager.NavEventListener eventListerner = new BaiduNaviManager.NavEventListener() {

        @Override
        public void onCommonEventCall(int what, int arg1, int arg2, Bundle bundle) {
//            BNEventHandler.getInstance().handleNaviEvent(what, arg1, arg2, bundle);
        }
    };

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    showToast("缺少导航基本的权限!");
                    return;
                }
            }
            initNavi();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            routeplanToNavi(mCoordinateType, mResetEndLon, mResetEndLat);
        }
    }

    /**
     * --------------- 百度导航END ---------------------
     */


    // 百度定位监听回调
    private BDAbstractLocationListener mBDLocListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            locationService.unregisterListener(mBDLocListener); //注销掉监听
            locationService.stop();// 停止定位

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
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

            SPUtils.put(mApp, "loccity", location.getCity());
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAddr = location.getAddrStr();
            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            MapStatus.Builder builder = new MapStatus.Builder();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            builder.target(ll);
            if (isFirstLoc) {
                isFirstLoc = false;
                builder.zoom(12.8f);
            }
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            nearbySj(location.getLongitude(), location.getLatitude());
        }
    };

     //获取附近的司机
    public void nearbySj(double mCurrentLon, double mCurrentLat) {

        //获取附近司机
        mEngine.moveLocation(String.valueOf(mCurrentLon), String.valueOf(mCurrentLat)).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String str = response.body().string();
//
                            LogsUtil.normal("初始数据"+str);
                            str = str.replace("null", "[\n" +
                                    "    {\n" +
                                    "      \"distance\": 0,\n" +
                                    "      \"ldata\": {\n" +
                                    "        \"sj_id\": 0,\n" +
                                    "        \"number\": \"0\",\n" +
                                    "        \"mobile\": \"0\",\n" +
                                    "        \"sb_long\": \"0\",\n" +
                                    "        \"sb_lat\": \"0\",\n" +
                                    "        \"name\": \"\",\n" +
                                    "        \"head_pic\": \"\",\n" +
                                    "        \"nickname\": \"\"\n" +
                                    "      }\n" +
                                    "    }\n" +
                                    "  ]");
                            LogsUtil.normal("response--" + str);

                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            String msg = jo.getString("msg");
                            String fid = jo.getString("fid");
                            JSONArray ja = jo.getJSONArray("result");
                            mDriverList.clear();

                            mBaiduMap.clear();

                            LogsUtil.normal("大小--" + ja.length() + "status" + status);

                            for (int i = 0; i < ja.length(); i++) {
                                DriverModel model = GsonUtil.jsonToDriverBean(ja.getJSONObject(i).getString("ldata"));
                                mDriverList.add(model);

                                LatLng latLng = new LatLng(Double.valueOf(model.getSb_lat()).doubleValue(), Double.valueOf(model.getSb_long()).doubleValue());
                                View view = LayoutInflater.from(mApp).inflate(R.layout.view_descriptor_driver, null); // 司机渲染

                                LinearLayout driver_des_bg = view.findViewById(R.id.driver_des_bg);
                                if ("normal".equals(model.getDj_status())) {
                                    driver_des_bg.setBackground(getResources().getDrawable(R.drawable.bg_white_green_bigradius));
                                } else {
                                    driver_des_bg.setBackground(getResources().getDrawable(R.drawable.bg_white_red_bigradius));
                                }

                                Glide.with(mApp).load(Constants.BASE_URL + model.getHead_pic())
                                        .placeholder(R.mipmap.icon_default_avatar)
                                        .error(R.mipmap.icon_default_avatar)
                                        .fallback(R.mipmap.icon_default_avatar)
                                        .into((ImageView) view.findViewById(R.id.view_iv_head));

                                ((TextView) view.findViewById(R.id.view_tv_nickname)).setText(model.getName());
//                                    ((TextView) view.findViewById(R.id.view_tv_nickname)).setText(model.getSj_id());

                                MarkerOptions mo = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromView(view)).zIndex(9);
                                mBaiduMap.addOverlay(mo);
                            }

                            if (status == 200) {
                            } else if (status == 105) {
                                showToast("暂不提供跨地区服务");
                            } else if (status == 104) {
                                showToast("当前地区未开通服务");
                            } else if (status == 101) {
                                showToast("未找到匹配的省份");
                            } else if (status == 102) {
                                showToast("未找到匹配的城市");
                            } else if (status == 103) {
                                showToast("未找到匹配的县区");
                            } else {
                                showToast("定位异常");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogsUtil.error("获取附近司机信息" + e);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogsUtil.error("获取附近司机信息" + e);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isResetEndLoc) {
            mResetEndLat = latLng.latitude;
            mResetEndLon = latLng.longitude;

            mBaiduMap.clear();
            // 构建MarkerOption，用于在地图上添加Marker
//            MarkerOptions stOptions = new MarkerOptions().position(new LatLng(Double.valueOf(mOrderInfo.getSt_lat()), Double.valueOf(mOrderInfo.getSt_long())))
//                    .icon(mStartCurrentMarker);
            MarkerOptions endOptions = new MarkerOptions().position(latLng)
                    .icon(mEndCurrentMarker);
            // 在地图上添加Marker，并显示
//            mBaiduMap.addOverlay(stOptions);
            mBaiduMap.addOverlay(endOptions);
            resetEndLoc(false);
            showToast("成功设置终点位置，已可以开始导航");
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //获取驾车线路规划结果
            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                showToast("未找到合适路径");
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                return;
            }

            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                if (drivingRouteResult.getRouteLines().size() > 0) {
                    DrivingRouteOverlay overlay = new MapFragment.MyDrivingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
//                    overlay.zoomToSpan();
//                    resetEndLoc(false);
//                    mRLNavTool.setVisibility(View.GONE);
                } else {
                    return;
                }
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 200) {
                resetMap();
            }
        } else if (requestCode == 1001) {
            if (resultCode == 200) {
                PoiInfo poiInfo = data.getParcelableExtra("endPoiInfo");
                mResetEndLat = poiInfo.location.latitude;
                mResetEndLon = poiInfo.location.longitude;

                mBaiduMap.clear();
                // 构建MarkerOption，用于在地图上添加Marker
//            MarkerOptions stOptions = new MarkerOptions().position(new LatLng(Double.valueOf(mOrderInfo.getSt_lat()), Double.valueOf(mOrderInfo.getSt_long())))
//                    .icon(mStartCurrentMarker);
                MarkerOptions endOptions = new MarkerOptions().position(poiInfo.location)
                        .icon(mEndCurrentMarker);
                // 在地图上添加Marker，并显示
//            mBaiduMap.addOverlay(stOptions);
                mBaiduMap.addOverlay(endOptions);
                showToast("成功设置终点位置，已可以开始导航");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

        // 是否接收了订单
        if (mOrderInfo == null) {
            mOrderInfo = ((MainActivity) mActivity).getOrderInfo();
            if (mOrderInfo != null) {
                mTVNav.setVisibility(View.VISIBLE);
                mTVSDrive.setVisibility(View.VISIBLE);
                mTVTitle.setVisibility(View.VISIBLE);
                mTVTitle.setText("联系客户");

                //转单
                if ("1".equals(mOrderInfo.getPaidan())) {//派单模式
                    mTVRedirect.setVisibility(View.VISIBLE);
                } else {
                    mTVRedirect.setVisibility(View.GONE);
                }

                LogsUtil.order("mOrderInfo.getPaidan()" + mOrderInfo.getPaidan());

                // 构建MarkerOption，用于在地图上添加Marker
                MarkerOptions stOptions = new MarkerOptions().position(new LatLng(Double.valueOf(mOrderInfo.getSt_lat()), Double.valueOf(mOrderInfo.getSt_long())))
                        .icon(mStartCurrentMarker);
                mBaiduMap.addOverlay(stOptions);

                if (mOrderInfo.getEnd_lat() != null && !mOrderInfo.getEnd_lat().equals("0.0") && !mOrderInfo.getEnd_lat().equals("")) {
                    MarkerOptions endOptions = new MarkerOptions().position(new LatLng(Double.valueOf(mOrderInfo.getEnd_lat()), Double.valueOf(mOrderInfo.getEnd_long())))
                            .icon(mEndCurrentMarker);
                    mBaiduMap.addOverlay(endOptions);
                }

                if (mCurrentLat == 0) {
                    mCurrentLat = ((MainActivity) mActivity).mCurretLat;
                    mCurrentLon = ((MainActivity) mActivity).mCurretLon;
                }
                PlanNode startNode = PlanNode.withLocation(new LatLng(mCurrentLat, mCurrentLon));
                PlanNode endNode = PlanNode.withLocation(new LatLng(new Double(mOrderInfo.getSt_lat()), new Double(mOrderInfo.getSt_long())));
                mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(startNode)
                        .to(endNode));

                nearbySj(mCurrentLon, mCurrentLat);

             }
        }

//        PoiInfo poiInfo = ((MainActivity) mActivity).getPoiInfo();
//        if (poiInfo != null) {
//            mResetEndLat = poiInfo.location.latitude;
//            mResetEndLon = poiInfo.location.longitude;
//
//
//            showToast("成功设置终点位置，已可以开始导航");
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
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
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        mRoutePlanSearch.destroy();

//        stopTrace();
    }

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getBDTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public double getmResetEndLat() {
        return mResetEndLat;
    }

    public void setmResetEndLat(double mResetEndLat) {
        this.mResetEndLat = mResetEndLat;
    }

    public double getmResetEndLon() {
        return mResetEndLon;
    }

    public void setmResetEndLon(double mResetEndLon) {
        this.mResetEndLon = mResetEndLon;
    }
}
