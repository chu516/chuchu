package bjx.com.siji.ui.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;

import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.utils.CoordinateConverter;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.ChargeModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.overlayutil.DrivingRouteOverlay;
import bjx.com.siji.overlayutil.OverlayManager;
import bjx.com.siji.overlayutil.PoiOverlay;

import bjx.com.siji.search.RouteLineAdapter;

import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;


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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Administrator on 2017/11/27.
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 */

public class MMapFragment extends BaseFragment implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    //主体控件
    private TextView start_drive;
    private ImageView location_add;
    private TextView set_mubiao;
    private TextView find_rood;
    private TextureMapView bmapView;
    ///////////////////////
    //Chronometer
    private TextView start_navigation, call_customer, call_customer_service;//头
    private Chronometer wait_time;
    private Chronometer travel_time;
    private TextView mileage, driving_amount;//主体
    private TextView button_start_wait_end, button_order_end;//按钮

    long ctrl = 0;
    long dt = 0;
    long ctrl2 = 0;
    long dt2 = 0;
    long time1 = 0;
    long time2 = 0;
    boolean type = false;
    boolean onType = false;
    boolean ClickType = false;
    private android.view.View clock_time_layout;
    //////////////////////////////
    private Context mContext;
    private BaiduMap mBaiduMap;
    private  LatLng point;

    // 搜索周边相关
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;

    /**
     * 定位SDK的核心类
     */
    public LocationClient mLocationClient = null;

    /**
     * 当前标志
     */
    private Marker mCurrentMarker;
    // 定位图标描述
    private BitmapDescriptor currentMarker = null;

    public BDLocationListener myListener = new MyLocationListener();

//    private List<PoiInfo> dataList;
//    private ShowMapInfoActivity.ListAdapter adapter;



    private int locType;
    private float radius;// 定位精度半径，单位是米
    private String addrStr;// 反地理编码
    private String province;// 省份信息
    private String city;// 城市信息
    private String district;// 区县信息
    private float direction;// 手机方向信息
    private GeoCoder mGeoCoder;
    private int checkPosition;
    private String address= "";
    private  String backData;
    String str_latitude ;
    String str_longitude ;
    double latitude ;
    double longitude ;

    /**
     * 规划
     */
        private boolean  ghType = false;

    private boolean startDrive = false;
    private LatLng nowLatLng;
    private LatLng lastLatLng;

    private Double kili;


    /**
     * 定位数据
     * */
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    MassTransitRouteLine massroute = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null; // 泡泡view

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
//    MapView mMapView = null;    // 地图View
//    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    WalkingRouteResult nowResultwalk = null;
    BikingRouteResult nowResultbike = null;
    TransitRouteResult nowResultransit = null;
    DrivingRouteResult nowResultdrive = null;
    MassTransitRouteResult nowResultmass = null;

    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。

    String startNodeStr = "西二旗地铁站";
    String endNodeStr = "百度科技园";
    boolean hasShownDialogue = false;

    LatLng LatLng1 = new LatLng(39.922797,116.926634);
    LatLng LatLng2 = new LatLng(39.947246, 116.414977);

    private String si_id;

    private OrderList info;

    int ty = -1;
    private boolean isdrive = false;
    private boolean isdrive1 = false;

    private LatLng preLocationLatLng; // 上一次轨迹坐标
    private LatLng curlocationLatLng; // 当前轨迹坐标
    private List<LatLng> mLatList; // 运行轨迹坐标列表
    ChargeModel chargeModel;// 收费标准


    boolean isFirstLoc;

    private double total_price = 0;
    private double mileage_price = 0;

    double wait_price;


    @Override
    protected void initView(Bundle savedInstanceState) {
        LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView= inflater.inflate(R.layout.fragment_map,null);
        Toolbar toolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);
//        activityList.add(getActivity());
        findView();

        Intent oc = getActivity().getIntent();
        ty =  oc.getIntExtra("fragment",0);
        info= oc.getParcelableExtra("list");

        if(ty>0){
            start_drive.setVisibility(View.VISIBLE);
            userModel = (UserModel) SPUtils.readObject(getContext(), Constants.USERMODEL);
            si_id=userModel.getSj_id();

            // 地理编译  实例化一个地址编码查询对象
            mGeoCoder = GeoCoder.newInstance();
            // 地图点击事件处理
            mBaiduMap.setOnMapClickListener(this);
            // 初始化搜索模块，注册事件监听
            mSearch = RoutePlanSearch.newInstance();
            mSearch.setOnGetRoutePlanResultListener(this);

            LatLng1 =new LatLng( Double.parseDouble(info.getSt_lat()),Double.parseDouble(info.getSt_long()));
            LatLng2 =new LatLng( Double.parseDouble(info.getEnd_lat()),Double.parseDouble(info.getEnd_long()));

        }else {
            start_drive.setVisibility(View.GONE);
        }



        initLocation();
        setListener() ;


//        Even();



//
//
//
//
//
//






//        Opentran();

    }

    private void Opentran() {
//        // 开启服务
//        mTraceClient.startTrace(mTrace, mTraceListener);
//        // 开启采集
//        mTraceClient.startGather(mTraceListener);
    }

    /**
     * 鹰眼
     * */

    private void Even() {
    // 轨迹服务ID
        long serviceId = 0;
    // 设备标识
        String entityName = "myTrace";

        UserModel currentDriver = (UserModel) SPUtils.readObject(App.getInstance(), Constants.USERMODEL);
        if (currentDriver != null) {
            entityName = currentDriver.getMobile();
        }

    // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
    // 初始化轨迹服务
        Trace mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
    // 初始化轨迹服务客户端
        LBSTraceClient mTraceClient = new LBSTraceClient(getContext());
        // 定位周期(单位:秒)
        int gatherInterval = 5;
    // 打包回传周期(单位:秒)
        int packInterval = 10;
    // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        // 初始化轨迹服务监听器
        OnTraceListener mTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {}
            // 开启服务回调
            @Override
            public void onStartTraceCallback(int status, String message) {}
            // 停止服务回调
            @Override
            public void onStopTraceCallback(int status, String message) {}
            // 开启采集回调
            @Override
            public void onStartGatherCallback(int status, String message) {}
            // 停止采集回调
            @Override
            public void onStopGatherCallback(int status, String message) {}
            // 推送回调
            @Override
            public void onPushCallback(byte messageNo, PushMessage message) {}

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };
    }








//
//    static class RealTimeHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }
//
//    /**
//     * 实时定位任务
//     *
//     * @author baidu
//     */
//    class RealTimeLocRunnable implements Runnable {
//
//        private int interval = 0;
//
//        public RealTimeLocRunnable(int interval) {
//            this.interval = interval;
//        }
//
//        @Override
//        public void run() {
//            if (isRealTimeRunning) {
//                trackApp.getCurrentLocation(entityListener, trackListener);
//                realTimeHandler.postDelayed(this, interval * 1000);
//            }
//        }
//    }
//
//    private void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
//
//    }


    /**
     * 鹰眼
     */




    /**
     * 路径规划代码
     * */

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        route = null;
//        mBtnPre.setVisibility(View.INVISIBLE);
//        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", endNodeStr);


        // 实际使用中请对起点终点城市进行正确的设定

        if (v.getId() == R.id.mass) {
            PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
            PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");

            mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
            nowSearchType = 0;
        } else if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 1;
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode).city("北京").to(enNode));
            nowSearchType = 2;
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 3;
        } else if (v.getId() == R.id.bike) {
            mSearch.bikingSearch((new BikingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 4;
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;

        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城综合交通
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        } else if (nowSearchType == 0) {
            // 跨城综合交通  综合跨城公交的结果判断方式不一样


            if (massroute == null || massroute.getNewSteps() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            boolean isSamecity = nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId();
            int size = 0;
            if (isSamecity) {
                size = massroute.getNewSteps().size();
            } else {
                for (int i = 0; i < massroute.getNewSteps().size(); i++) {
                    size += massroute.getNewSteps().get(i).size();
                }
            }

            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < size - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            if (isSamecity) {
                // 同城
                step = massroute.getNewSteps().get(nodeIndex).get(0);
            } else {
                // 跨城
                int num = 0;
                for (int j = 0; j < massroute.getNewSteps().size(); j++) {
                    num += massroute.getNewSteps().get(j).size();
                    if (nodeIndex - num < 0) {
                        int k = massroute.getNewSteps().get(j).size() + nodeIndex - num;
                        step = massroute.getNewSteps().get(j).get(k);
                        break;
                    }
                }
            }

            nodeLocation = ((MassTransitRouteLine.TransitStep) step).getStartLocation();
            nodeTitle = ((MassTransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(getContext());
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(getContext(),
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(getContext(),
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    //步行
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    //乘车
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    //跨城
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    //驾车
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;


            if (drivingRouteResult.getRouteLines().size() > 1) {
                nowResultdrive = drivingRouteResult;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(getContext(),
                            drivingRouteResult.getRouteLines(),
                            RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultdrive.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultdrive.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (drivingRouteResult.getRouteLines().size() == 1) {
                route = drivingRouteResult.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
//                mBtnPre.setVisibility(View.VISIBLE);
//                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    //
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    //骑行
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }






    /**
     * 常用代码
     * */



    @Override
    protected void setListener() {


        currentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);

        start_drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == false) {
                    clock_time_layout.setVisibility(View.VISIBLE);
                    type = true;
                    if(onType == false){
                        start_drive.setText("开始代驾");
                    }else {
                        start_drive.setText("隐藏计费");
                    }
                } else {
                    clock_time_layout.setVisibility(View.GONE);
                    type = false;
                    if(onType == false){
                        start_drive.setText("开始代驾");
                    }else {
                        start_drive.setText("显示计费");
                    }
                }

//                Intent oac = new Intent(getContext(),ShowMapshowActivity.class);
//                startActivity();

            }
        });


        button_start_wait_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isdrive==false){
                    isdrive = true;
                    StarDri();
                }
                if(isdrive1 = false){
                    isdrive1 = true;
                    timer.schedule(task, 1000, 5000); // 1s后执行task,经过1s再次执行
                }



                start_drive.setVisibility(View.GONE);
               Paymoney();
            }
        });

        //结算按钮
        button_order_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait_time.stop();
                travel_time.stop();
                time1 = SystemClock.elapsedRealtime() - wait_time.getBase();    // 保存这次记录了的时间
                time2 = SystemClock.elapsedRealtime() - travel_time.getBase();    // 保存这次记录了的时间
                ctrl = 0;
                dt = 0;
                dt2 = 0;
                type = true;
                onType = false;
                ClickType = false;
                wait_time.setText("00:00");
                travel_time.setText("00:00");
                button_start_wait_end.setText("开始代驾");
                start_drive.setText("开始代驾");

                DealDate();


            }
        });

        set_mubiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickType == false){
                    ClickType = true;
                }else {
                    ClickType = false;
                }
            }
        });


        location_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                ToastUtil.show(this, "正在定位。。。");
                Toast.makeText(getContext(),"正在定位。。。",Toast.LENGTH_SHORT).show();
                ClickType = false;
                initLocation();
            }
        });


        //添加目的地
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            //此方法就是点击地图监听
            public void onMapClick(LatLng latLng) {
                //获取经纬度
               setNewBiao(latLng);
            }

            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }
        });


        find_rood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng from = LatLng1;
                LatLng dest = LatLng2;
                //设置起终点信息，对于tranist search 来说，城市名无意义
                PlanNode stNode = PlanNode.withLocation(from);
                PlanNode enNode = PlanNode.withLocation(dest);
                // 实际使用中请对起点终点城市进行正确的设定
                if(ty>-1){
                    mSearch.drivingSearch((new DrivingRoutePlanOption())
                            .from(stNode)
                            .to(enNode));
                }


//                mSearch.drivingSearch((new DrivingRoutePlanOption())
//                        .from(stNode)
//                        .to(enNode));

            }
        });

        call_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent ods = new Intent(getContext(), ShowTranActivity.class);
//                startActivity(ods);


            }
        });

        call_customer_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent oasd = new Intent(getContext(),TracingActivity.class);
//                startActivity(oasd);


            }
        });

        start_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ghType==false){
                    set_mubiao.setVisibility(View.VISIBLE);
                    find_rood.setVisibility(View.VISIBLE);
                }else {
                    set_mubiao.setVisibility(View.GONE);
                    find_rood.setVisibility(View.GONE);
                }



//                Intent ire = new Intent(getContext(), DistributeOrderActivity.class);
//                startActivity(ire);

            }
        });

    }

    private void DealDate() {
        String order_id=info.getOrder_id() ;
        String address_end = address;
        String end_long = longitude+"";
        String end_lat = latitude+"";

        long travel_times = time2;
        long wait_times = time1;
        List trajectory = mLatList;

        double mileagesa = kili/1000;



//        mEngine.EndDrivr(si_id,order_id, address_end,end_long,end_lat,total_price,travel_times,mileagesa,mileage_price,wait_times,wait_price,1,trajectory,
//                MD5.getMessageDigest((si_id + Constants.BASE_KEY + order_id+ total_price).getBytes())).enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String str = response.body().string();
//                    JSONObject jo = new JSONObject(str);
//                    int status = jo.getInt("status");
//                    String msg = jo.getString("msg");
//
//                    Log.e("TAG",status+"");
//                    if (status == 200) {
//                        Toast.makeText(getContext(), "开始代驾了", Toast.LENGTH_SHORT).show();
////                        Intent tomap = new Intent(CreateOrderActivity.this, MMapFragment.class);
////                        startActivity(tomap);
//                        Intent tot = new Intent(getContext(), TallyOrderActivity.class);
//                        startActivity(tot);
//
//                    } else if (status == 100 ) {
////                        showToast("参数错误");
//                        Toast.makeText(getContext(), "参数错误", Toast.LENGTH_SHORT).show();
//                    } else if (status == 101) {
//                        Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show();
//                    } else if (status == 105||status == 105||status ==106||status ==107||status ==108) {
//                        Toast.makeText(getContext(), "不在服务区", Toast.LENGTH_SHORT).show();
//                    } else if (status == 102) {
//                        Toast.makeText(getContext(), "创建用户失败", Toast.LENGTH_SHORT).show();
//                    } else if (status == 104 ) {
//                        Toast.makeText(getContext(), "当前司机状态不正常", Toast.LENGTH_SHORT).show();
//                    }else if (status == 202 ) {
//                        Toast.makeText(getContext(), "订单不存在", Toast.LENGTH_SHORT).show();
//                    } else if (status == 203 ) {
//                        Toast.makeText(getContext(), "订单状态不是等待代驾或接单司ID不是当前司机", Toast.LENGTH_SHORT).show();
//                    }  else {
//                        Toast.makeText(getContext(), "创建失败,请稍候再试", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(getContext(), "网络连接异常", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void StarDri() {
        String order_id=info.getOrder_id() ;
        address = info.getAddress_st();
        str_longitude = info.getEnd_long();
        str_latitude = info.getEnd_lat();
        mEngine.startDrive(si_id,order_id, address,str_longitude,str_latitude,
                MD5.getMessageDigest((si_id + Constants.BASE_KEY + order_id+ str_longitude).getBytes())).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e("TAG","kaishidaijia");
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");

                    Log.e("TAG",status+"");
                    if (status == 200) {
                        Toast.makeText(getContext(), "开始代驾了", Toast.LENGTH_SHORT).show();
//                        Intent tomap = new Intent(CreateOrderActivity.this, MMapFragment.class);
//                        startActivity(tomap);


                    } else if (status == 100 ) {
//                        showToast("参数错误");
                        Toast.makeText(getContext(), "参数错误", Toast.LENGTH_SHORT).show();
                    } else if (status == 101) {
                        Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show();
                    } else if (status == 105||status == 105||status ==106||status ==107||status ==108) {
                        Toast.makeText(getContext(), "不在服务区", Toast.LENGTH_SHORT).show();
                    } else if (status == 102) {
                        Toast.makeText(getContext(), "创建用户失败", Toast.LENGTH_SHORT).show();
                    } else if (status == 104 ) {
                        Toast.makeText(getContext(), "当前司机状态不正常", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "创建失败,请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "网络连接异常", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
 * 测试区域
 */


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                initLocation();
                comMonalr();
            }
            super.handleMessage(msg);
        };
    };

    private void comMonalr() {

//                curlocationLatLng =new LatLng(latitude,longitude);
//
//                if(startDrive==true) {//存储数据
//
////                SPUtils.put(getContext(),"Lanlng",LatLng1);
////                Log.i("获取的数据",(int) DistanceUtil.getDistance(locationLatLng, LatLng1)+"米");
//                    if (isFirstLoc) {
//                        preLocationLatLng = curlocationLatLng;
//                        kili = DistanceUtil.getDistance(curlocationLatLng, LatLng1);
//                    } else {
//                        kili += DistanceUtil.getDistance(preLocationLatLng, curlocationLatLng);
//                        preLocationLatLng = curlocationLatLng;
//                    }
//                    mLatList.add(curlocationLatLng);
//
//                    double kilis = kili/1000;
//                    double chaoChuKilis = kilis - chargeModel.getKilometres();
//                    if (chaoChuKilis < 0) {
////                    driving_amount.setText(chargeModel.getSprice());
//                        driving_amount.setText(chargeModel.getSprice()+"");
//                    } else {
////                    driving_amount.setText(chargeModel.getUprice() + chargeModel.getSprice());
//                        BigDecimal upa = chargeModel.getUprice();
//                        BigDecimal spa = chargeModel.getSprice();
//                        total_price = upa.add(spa).doubleValue();
//                        wait_price = Integer.parseInt(upa+"");
//                        mileage_price = Integer.parseInt(spa+"");
//                        driving_amount.setText(total_price+"");
//                    }
//                } else {
//                }
//                mileage.setText(String.valueOf(kili/1000));


    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    /**
     *测试区域
     */










    @Override
    protected void processLogic(Bundle savedInstanceState) {
        givepaylist();
    }





































    ////////////////////////////////////////定位分界线\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private void setNewBiao(LatLng latLng) {
        Toast.makeText(getContext(),"ClickType。。。"+ClickType,Toast.LENGTH_SHORT).show();

        if(ClickType == true){
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            str_latitude = Double.toString(latitude);
            str_longitude = Double.toString(longitude);

//                //先清除图层
            mBaiduMap.clear();
            // 定义Maker坐标点
            point = new LatLng(latitude, longitude);
            // 构建MarkerOption，用于在地图上添加Marker
            MarkerOptions options = new MarkerOptions().position(point)
                    .icon(currentMarker);
            // 在地图上添加Marker，并显示

            mBaiduMap.addOverlay(options);

            //设置反地理编码位置坐标
            ReverseGeoCodeOption op = new ReverseGeoCodeOption();
            op.location(latLng);
            //发起反地理编码请求(经纬度->地址信息)
            mGeoCoder.reverseGeoCode(op);
            mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    //获取点击的坐标地址
                    if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        // 没有找到检索结果
                    } else {
                        // 获取反向地理编码结果
                        Log.e("获取反向地理编码结果 :", reverseGeoCodeResult.getAddress());

//                        endAddre = reverseGeoCodeResult.getAddress();
                        System.out.println("点击信息--///-》");
                        System.out.println("1=" + reverseGeoCodeResult.getAddressDetail().city );
                        System.out.println("1=" + reverseGeoCodeResult.getAddress() + ",2=" + reverseGeoCodeResult.getAddressDetail().district);
                        System.out.println("1=" + reverseGeoCodeResult.getAddressDetail().province );

                        System.out.println("坐标=" + reverseGeoCodeResult.getLocation() );
                        LatLng2 = reverseGeoCodeResult.getLocation();
                        System.out.println("点击信息--///-》");
                        province = reverseGeoCodeResult.getAddressDetail().province;
                        city = reverseGeoCodeResult.getAddressDetail().city ;// 城市信息
                        district = reverseGeoCodeResult.getAddressDetail().district;// 区县信息
                        address = reverseGeoCodeResult.getAddress();// 区县信息

                        LatLng ll = new LatLng(latitude,longitude);

                    }
                }

                public void onGetGeoCodeResult(GeoCodeResult arg0) {

                }
            });
        }
    }

    private void Paymoney() {
        onType = true;
//        start_drive.setText("隐藏计费");
        if (ctrl == 0) {
            travel_time.setBase(SystemClock.elapsedRealtime());                            //初始化时间
            travel_time.start();
            button_start_wait_end.setText("开始等待");
            ctrl = 1;
        } else if (ctrl == 1) {                                                    //暂停计时
            //1停止
            travel_time.stop();
            dt = SystemClock.elapsedRealtime() - travel_time.getBase();    // 保存这次记录了的时间
            ctrl = 2;
            button_start_wait_end.setText("继续行驶");
            //2开始计时
            wait_time.setBase(SystemClock.elapsedRealtime() - dt2 + 1);    // 跳过已经记录了的时间
            wait_time.start();
        } else if (ctrl == 2) {                                                    //继续计时
            //1开始计时
            travel_time.setBase(SystemClock.elapsedRealtime() - dt + 1);    // 跳过已经记录了的时间
            travel_time.start();
            ctrl = 1;
            //2暂停
            wait_time.stop();
            dt2 = SystemClock.elapsedRealtime() - wait_time.getBase();    // 保存这次记录了的时间
            button_start_wait_end.setText("开始等待");
        }
    }

    private void findView() {

        find_rood= (TextView) mContentView.findViewById(R.id.find_rood);

        set_mubiao = (TextView) mContentView.findViewById(R.id.set_mubiao);
        location_add = (ImageView) mContentView.findViewById(R.id.location_add);
        bmapView = (TextureMapView) mContentView.findViewById(R.id.bmapView);
        start_drive = (TextView) mContentView.findViewById(R.id.start_drive);

        clock_time_layout = mContentView.findViewById(R.id.clock_time_layout);
        button_start_wait_end = (TextView) mContentView.findViewById(R.id.button_start_wait_end);
        button_order_end = (TextView) mContentView.findViewById(R.id.button_order_end);

        start_navigation = (TextView) mContentView.findViewById(R.id.start_navigation);
        call_customer = (TextView) mContentView.findViewById(R.id.call_customer);
        call_customer_service = (TextView) mContentView.findViewById(R.id.call_customer_service);
        wait_time = (Chronometer) mContentView.findViewById(R.id.wait_time);
        travel_time = (Chronometer) mContentView.findViewById(R.id.travel_time);
        driving_amount = (TextView) mContentView.findViewById(R.id.driving_amount);
        mileage = (TextView) mContentView.findViewById(R.id.mileage);

        mBaiduMap = bmapView.getMap();
    }

    private void givepaylist() {//
        mEngine.gaveMoneylist(si_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");

                    if (status == 200) {
                        chargeModel = GsonUtil.jsonToModelBean(jo.getString("result"));




//                        showToast("登录成功");
                        Log.e("TAG","登陆成功");

                    } else if (status == 100) {
                        Log.e("TAG","参数错误");
//                        showToast("参数错误");
                    } else if (status == 201) {
                        Log.e("TAG","账号不存在");
//                        showToast("账号不存在");
                    } else if (status ==301) {
                        Log.e("TAG","更新司机信息失败");
//                        showToast("更新司机信息失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showNetErrToast();
            }
        });
    }



    /**
     * 定位
     */
    private void initLocation(){
        //重新设置
        checkPosition = 0;
        mBaiduMap.clear();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));   // 设置级别

        // 定位初始化
        mLocationClient = new LocationClient(getContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener);// 注册定位监听接口

        /**
         * 设置定位参数
         */
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
//		option.setScanSpan(5000);// 设置发起定位请求的间隔时间,ms
        option.setNeedDeviceDirect(true);// 设置返回结果包含手机的方向
        option.setOpenGps(true);
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.disableCache(true);//禁止启用缓存定位
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start(); // 调用此方法开始定位
        mLocationClient.requestLocation();
    }



    /**
     * 定位SDK监听函数
     *
     * @author
     *
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || bmapView == null) {
                return;
            }

            locType = location.getLocType();
            Log.i("mybaidumap", "当前定位的返回值是："+locType);

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            if (location.hasRadius()) {// 判断是否有定位精度半径
                radius = location.getRadius();
            }

            if(ty>0){
                curlocationLatLng =new LatLng(latitude,longitude);

                if(startDrive==true){//存储数据

//                SPUtils.put(getContext(),"Lanlng",LatLng1);
//                Log.i("获取的数据",(int) DistanceUtil.getDistance(locationLatLng, LatLng1)+"米");
                    if (isFirstLoc) {
                        preLocationLatLng = curlocationLatLng;
                        kili = DistanceUtil.getDistance(curlocationLatLng, LatLng1);
                    } else {
                        kili += DistanceUtil.getDistance(preLocationLatLng, curlocationLatLng);
                        preLocationLatLng = curlocationLatLng;
                    }
                    mLatList.add(curlocationLatLng);

                    double kilis = kili/1000;
//                    double chaoChuKilis = kilis - chargeModel.getKilometres();
//                    if (chaoChuKilis < 0) {
////                    driving_amount.setText(chargeModel.getSprice());
//                        driving_amount.setText(chargeModel.getSprice()+"");
//                    } else {
//                    driving_amount.setText(chargeModel.getUprice() + chargeModel.getSprice());
//                        BigDecimal upa = chargeModel.getUprice();
//                        BigDecimal spa = chargeModel.getSprice();
//                        double bc = upa.add(spa).doubleValue();
////                        driving_amount.setText(bc+"");
//                    }
                } else {
                }
                mileage.setText(String.valueOf(kili/1000));

            }



            if (locType == BDLocation.TypeNetWorkLocation) {
                addrStr = location.getAddrStr();// 获取反地理编码(文字描述的地址)
                Log.i("mybaidumap", "当前定位的地址是："+addrStr);
            }

            direction = location.getDirection();// 获取手机方向，【0~360°】,手机上面正面朝北为0°
            province = location.getProvince();// 省份
            city = location.getCity();// 城市
            district = location.getDistrict();// 区县
            address = location.getAddrStr();



            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            Log.i("当前坐标是", ll+"");
            LatLng1 = ll;


//            //将当前位置加入List里面
//            PoiInfo info = new PoiInfo();
//            info.address = location.getAddrStr();
//            info.city = location.getCity();
//            info.location = ll;
//            info.name = location.getAddrStr();
//            dataList.add(info);
//            adapter.notifyDataSetChanged();
            Log.i("mybaidumap", "province是："+province +" city是"+city +" 区县是: "+district);

//            starAddre = location.getAddrStr();


            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            //画标志
            CoordinateConverter converter = new CoordinateConverter();
            converter.coord(ll);
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng convertLatLng = converter.convert();
            mBaiduMap.clear();


            List<OverlayOptions> options = new ArrayList<OverlayOptions>();

            OverlayOptions start,end;
            Log.e("TAG",ty+"");
            if(ty>1){
                 start = new MarkerOptions().position(LatLng1).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_start)).zIndex(9).draggable(true);

                 end = new MarkerOptions().position(LatLng2).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_end)).zIndex(9).draggable(true);

                options.add(start);
                options.add(end);
            }
            OverlayOptions ooA = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_point)).zIndex(9).draggable(true);
//            mCurrentMarker = (Marker) mBaiduMap.addOverlay(ooA);
            options.add(ooA);
            //在地图上批量添加
            mBaiduMap.addOverlays(options);

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(u);


            //画当前定位标志
            MapStatusUpdate uc= MapStatusUpdateFactory.newLatLng(ll);//中心点位置
            mBaiduMap.animateMapStatus(uc);

            bmapView.showZoomControls(true);
            //poi 搜索周边
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Looper.prepare();

                    Looper.loop();
                }
            }).start();


        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }





    /**
     * 用于显示poi的overly
     * @author Administrator
     *
     */
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(index);
//            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
//                    .poiUid(poiInfo.uid));
            return true;
        }
    }


        @Override
        public void onDestroy() {
            // 退出时销毁定位
            if (mLocationClient != null) {
                mLocationClient.stop();
            }

            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
    //        mPoiSearch.destroy();
            super.onDestroy();
            // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
            bmapView.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
            bmapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
            bmapView.onPause();
        }


    }















///////////////////////////////////////////////////////////////三个标注定位显示///////////////////////////////////////////////////



//implements View.OnClickListener, BaiduMap.OnMapClickListener {
//
//private TextView back;
//private MapView mMapView;
//private BaiduMap baiduMap;
//
//private LocationClient mLocationClient;         // 定位
//
//private MyLocationListener mBdLocationListener; // 定位监听
//private GeoCoder mGeoCoder;
//private PoiSearch mPoiSearch;
//
//        LatLng LatLng1 = new LatLng(39.922797,116.926634);
//        LatLng LatLng2 = new LatLng(39.947246, 116.414977);
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customer);
//
//
//
//        initView();
//        initMap();
////        Toast.makeText(ShowMapOrderActivity.this,Address,Toast.LENGTH_SHORT).show();
//        setListener();
//        }
//
//private void setListener() {
//        back.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        finish();
//        }
//        });
//
//        }
//
//
//
//private void initMap() {
//        baiduMap = mMapView.getMap();
//        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  // 普通地图
//
//        // 定位
//        getLocation();
//        }
//
//
//
//
//
//
//private void initView() {
//        back = (TextView) findViewById(R.id.back);
//        // 获取地图控件引用
//        mMapView = (MapView) findViewById(R.id.mv_main_mapView);
//        mMapView.showZoomControls(true);//放大缩小控制器
//
//        }
//
//@Override
//public void onClick(View v) {
//
//        }
//
//@Override
//public void onMapClick(LatLng latLng) {
//
//        }
//
//@Override
//public boolean onMapPoiClick(MapPoi mapPoi) {
//        return false;
//        }
//
///**
// * 地图操作
// * ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// * 以下
// * */
//
//
//// 获取定位
//private void getLocation() {
//        mLocationClient = new LocationClient(getApplicationContext());
//        mBdLocationListener = new MyLocationListener();
//        mLocationClient.registerLocationListener(mBdLocationListener);  // 注册监听者
//        initLocation(mLocationClient);  // client 设置参数
//        mLocationClient.start();
//        }
//
//// 定位设置
//public void initLocation(LocationClient locationClient) {
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);   // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");   // 可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 0;
//        option.setScanSpan(span);       // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);  // 可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);        // 可选，默认false,设置是否使用gps
//        option.setLocationNotify(true); // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true); // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);  // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);     // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);  // 可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);     // 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        locationClient.setLocOption(option);    // 设置到咱们的客户端
//        }
//
//// 定位类
//class MyLocationListener implements BDLocationListener {
//    //经纬度信息
//    private double dLatitude;
//    private double dLongitude;
//    //城市与地址信息
//    private String sCityStr;
//    private String sAddStr;
//
//    // 当开启定位的时候,走这个方法
//    @Override
//    public void onReceiveLocation(BDLocation location) {
//        if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//            dLatitude = location.getLatitude();     // 纬度
//            dLongitude = location.getLongitude();   // 经度
//            sCityStr = location.getCity();          // 城市
//            sAddStr = location.getAddrStr();        // 地址
//
//            LatLng latLng = new LatLng(dLatitude, dLongitude);
//            setMap(latLng);  // 设置中心点  设置Marker
//
//
//            runOnUiThread(new TimerTask() {
//                @Override
//                public void run() {
////                        mFindContent.setText(sAddStr.isEmpty() ? "" : StringUtil.getLaterString(sAddStr, "省")); // 这里自己的工具类
//                    if (sAddStr.isEmpty()) {
////                            ToastUtil.showShortToast("抱歉为获取到信息！");   // Toast工具类
//                        Toast.makeText(CustomerActivity.this,"抱歉未获取到信息",Toast.LENGTH_SHORT).show();
//                    }
//                    mLocationClient.stop();
//                }
//            });
//        }
//    }
//}
//
//
//
//    // 设置中心点  设置Marker
//    private void setMap(LatLng latLng) {
//        if (latLng != null) {
//            if (baiduMap != null) {
//                baiduMap.clear();
//            }
//
////            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 17);//定位获取的数据----中心点数据
//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(LatLng1, 17);//传递过来的数据----中心点数据
//            if (update != null) {
//                baiduMap.setMapStatus(update);  // 设置中心点
//
//
//                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_en); // 添加覆盖物------地图上的现实标记
//                BitmapDescriptor bitmapDescriptores = BitmapDescriptorFactory.fromResource(R.drawable.icon_st); // 添加覆盖物------地图上的现实标记
//                BitmapDescriptor bitmapDescriptoress = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 添加覆盖物------地图上的现实标记
//
//                List<OverlayOptions> options = new ArrayList<OverlayOptions>();//---------多标记数组
//
//
//
//                /**
//                 * 标记绑定数据
//                 */
//
//                OverlayOptions option1 =  new MarkerOptions()
//                        .position(LatLng1)
//                        .icon(bitmapDescriptor);//传递的数据
//                OverlayOptions option2 =  new MarkerOptions()
//                        .position(latLng)
//                        .icon(bitmapDescriptores);//获取的数据
//                OverlayOptions option3 =  new MarkerOptions()
//                        .position(LatLng2)
//                        .icon(bitmapDescriptoress);//获取的数据
//                options.add(option1);
//                options.add(option2);
//                options.add(option3);
//
//
////                Marker marker = (Marker) baiduMap.addOverlays(options);  // 添加到地图中 并获取返回值 Marker具体的覆盖物
//                baiduMap.addOverlays(options);  // 添加到地图中 并获取返回值 Marker具体的覆盖物
//            }
//        }
//    }
//
//}
//

///////////////////////////////////////////////////////////////三个标注定位显示///////////////////////////////////////////////////










//基础定位
/*
    */
/**
     * 显示请求字符串
     *
     * @param str
     *//*

    public void logMsg(String str) {
        final String s = str;
        try {
            if (LocationResult != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocationResult.post(new Runnable() {
                            @Override
                            public void run() {
                                LocationResult.setText(s);
                            }
                        });

                    }
                }).start();
            }
            //LocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/***
     * Stop location service
     *//*

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
//        locationService = ((App)getActivity.getApplication()).locationService;
//        getActivity().getApplication()
        locationService = ((App)getActivity().getApplication()).locationService;


        //
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
//        int type = getIntent().getIntExtra("from", 0);
        int type = 0;
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        startLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
                    locationService.start();// 定位SDK
                    // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
                    startLocation.setText(getString(R.string.stoplocation));
                } else {
                    locationService.stop();
                    startLocation.setText(getString(R.string.startlocation));
                }
            }
        });
    }


    */
/*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     *//*

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                */
/**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 *//*

                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                logMsg(sb.toString());
            }
        }

    };
*/
