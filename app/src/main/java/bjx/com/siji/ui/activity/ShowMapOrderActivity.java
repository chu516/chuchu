package bjx.com.siji.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import bjx.com.siji.R;
import bjx.com.siji.overlayutil.PoiOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/28.
 */

public class ShowMapOrderActivity extends AppCompatActivity  implements OnGetPoiSearchResultListener {
    private Context mContext;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private Button mCompleteButton;
    private TextView mCannelButton;
    private Button mRequestLocation;
    private TextView chat_publish_complete_title;
//    private ListView mListView;

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

    private List<PoiInfo> dataList;
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

    int whereto;

    int types =0;
    LatLng latlan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_map_order_info);
        mContext = this;

        types = Integer.parseInt(getIntent().getStringExtra("type"));
        latlan = new LatLng(Double.parseDouble(getIntent().getStringExtra("lat")),Double.parseDouble(getIntent().getStringExtra("lon")));


        initView();
        if(types == 1){
            chat_publish_complete_title.setText("出发地址");
        }else if(types == 2){
            chat_publish_complete_title.setText("终点地址");
        }


        mBaiduMap = mMapView.getMap();
        // 地理编译  实例化一个地址编码查询对象
        mGeoCoder = GeoCoder.newInstance();
        initEvent();

        initLocation();
    }

    private void initView(){
        dataList = new ArrayList<PoiInfo>();
        mMapView = (MapView) findViewById(R.id.bmapView);

        chat_publish_complete_title = (TextView) findViewById(R.id.chat_publish_complete_title);
        mCompleteButton = (Button) findViewById(R.id.chat_publish_complete_publish);
        mCannelButton = (TextView) findViewById(R.id.chat_publish_complete_cancle);
        mRequestLocation = (Button) findViewById(R.id.request);
//        mListView = (ListView) findViewById(R.id.lv_location_nearby);
        checkPosition=0;
//        adapter = new ListAdapter(0);
//        mListView.setAdapter(adapter);
    }

    /**
     * 事件初始化
     */
    private void initEvent(){
        currentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);

        mRequestLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),"正在定位。。。",Toast.LENGTH_SHORT).show();
                initLocation();
            }
        });

        mCompleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub



//
//                // 判断是否填写了内容
//                if (address==null || "".equals(address)){
//                    Toast.makeText(ShowMapOrderActivity.this, "请选择开始代驾地址", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                System.out.println("1=" + backData+","+ str_longitude+","+str_latitude+","+province+","+city+","+district);
//
//
//
//
////                 Log.i("mybaidumap", "province是："+province +" city是"+city +" 区县是: "+district);
//                // 设置Intent回调 并设置回调内容
//                String str_latitude = Double.toString(latitude);
//                String str_longitude = Double.toString(longitude);
//                Intent intent = new Intent();
//                intent.putExtra("data", address);
//                intent.putExtra("longitude", str_longitude);
//                intent.putExtra("latitude", str_latitude);
//                intent.putExtra("province", province);
//                intent.putExtra("city", city);
//                intent.putExtra("district", district);
//
//                if(whereto==1||whereto==2){
//
//                    Log.i("结果","1");
//                    setResult(2, intent);
//                }else if(whereto==3||whereto==4){
//                    Log.i("结果","3");
//                    setResult(4, intent);
//                }



                //  结束当前页面(关闭当前界面)
                finish();



            }
        });

        mCannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//
//
//
//            //此方法就是点击地图监听
//            public void onMapClick(LatLng latLng) {
//                //获取经纬度
//                latitude = latLng.latitude;
//                longitude = latLng.longitude;
//                System.out.println("点击信息--===-》");
//                System.out.println("latitude=" + latitude + ",longitude=" + longitude);
//                longitude = longitude;// 精度
//                latitude = latitude;// 维度
//                str_latitude = Double.toString(latitude);
//                str_longitude = Double.toString(longitude);
//
//                System.out.println("点击信息--===-》");
//                //先清除图层
//                mBaiduMap.clear();
//                // 定义Maker坐标点
//                point = new LatLng(latitude, longitude);
//                // 构建MarkerOption，用于在地图上添加Marker
//                MarkerOptions options = new MarkerOptions().position(point)
//                        .icon(currentMarker);
//                // 在地图上添加Marker，并显示
//                mBaiduMap.addOverlay(options);
//                //实例化一个地理编码查询对象
////                GeoCoder geoCoder = GeoCoder.newInstance();
//                //设置反地理编码位置坐标
//                ReverseGeoCodeOption op = new ReverseGeoCodeOption();
//                op.location(latLng);
//                //发起反地理编码请求(经纬度->地址信息)
//                mGeoCoder.reverseGeoCode(op);
//                mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//
//                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                        //获取点击的坐标地址
//                        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//                            // 没有找到检索结果
//                        } else {
//                            // 获取反向地理编码结果
//                            Log.e("获取反向地理编码结果 :", reverseGeoCodeResult.getAddress());
//
//                            System.out.println("点击信息--///-》");
//                            System.out.println("1=" + reverseGeoCodeResult.getAddressDetail().city );
//                            System.out.println("1=" + reverseGeoCodeResult.getAddress() + ",2=" + reverseGeoCodeResult.getAddressDetail().district);
//                            System.out.println("1=" + reverseGeoCodeResult.getAddressDetail().province );
//                            System.out.println("点击信息--///-》");
//                            province = reverseGeoCodeResult.getAddressDetail().province;
//                            city = reverseGeoCodeResult.getAddressDetail().city ;// 城市信息
//                            district = reverseGeoCodeResult.getAddressDetail().district;// 区县信息
//                            address = reverseGeoCodeResult.getAddress();// 区县信息
//
//
//
//                            LatLng ll = new LatLng(latitude,longitude);
//
//                            //将当前位置加入List里面
//                            PoiInfo info = new PoiInfo();
//                            info.address = reverseGeoCodeResult.getAddress();
//                            info.city = city;
//                            info.location = ll;
//                            info.name = backData;
//                            dataList.add(info);
//
//
//
//
//                    /*
//                            private String addrStr;// 反地理编码
//                            private String province;// 省份信息
//                            private String city;// 城市信息
//                            private String district;// 区县信息
//                            private float direction;// 手机方向信息*//*        private double longitude;// 精度
//                            private double latitude;// 维度
//                            private float radius;// 定位精度半径，单位是米
//                            private String addrStr;// 反地理编码
//                            private String province;// 省份信息
//                            private String city;// 城市信息
//                            private String district;// 区县信息
//                            private float direction;// 手机方向信息*/
//
////                    mFindContent.setText(StringUtil.getLaterString(reverseGeoCodeResult.getAddress(), "省"));
////                            mFindContent.setSelection(mFindContent.length());
//                        }
//                    }
//
//                    public void onGetGeoCodeResult(GeoCodeResult arg0) {
//
//                    }
//                });
//            }
//
//            public boolean onMapPoiClick(MapPoi arg0) {
////                arg0.getName(); //名称
////                arg0.getPosition(); //坐标
////                String name = arg0.getName();
////                LatLng zuobiao = arg0.getPosition();
////                System.out.println("点击信息-------------------------------------------------------》");
////                System.out.println("地址="+name+",坐标"+zuobiao);
//
//                return false;
//            }
//        });

    }

    /**
     * 定位
     */
    private void initLocation(){
        //重新设置
        checkPosition = 0;
//        adapter.setCheckposition(0);

//        mBaiduMap = mMapView.getMap();


        mBaiduMap.clear();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));   // 设置级别

        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
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
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start(); // 调用此方法开始定位
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
            if (location == null || mMapView == null) {
                return;
            }

            locType = location.getLocType();
            Log.i("mybaidumap", "当前定位的返回值是："+locType);

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (location.hasRadius()) {// 判断是否有定位精度半径
                radius = location.getRadius();
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

            //将当前位置加入List里面
            PoiInfo info = new PoiInfo();
            info.address = location.getAddrStr();
            info.city = location.getCity();
            info.location = ll;
            info.name = location.getAddrStr();
            dataList.add(info);
//            adapter.notifyDataSetChanged();
            Log.i("mybaidumap", "province是："+province +" city是"+city +" 区县是: "+district);


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
            if(types == 1){
                OverlayOptions start_end = new MarkerOptions().position(latlan).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_start)).zIndex(9).draggable(true);
                OverlayOptions me = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_point)).zIndex(9).draggable(true);

                options.add(start_end);
                options.add(me);
                //在地图上批量添加
                mBaiduMap.addOverlays(options);
            }else if(types == 2){
                OverlayOptions start_end = new MarkerOptions().position(latlan).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_end)).zIndex(9).draggable(true);
                OverlayOptions me = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_point)).zIndex(9).draggable(true);


                options.add(start_end);
                options.add(me);
                //在地图上批量添加
                mBaiduMap.addOverlays(options);
            }


//            mCurrentMarker = (Marker) mBaiduMap.addOverlays(ooA);

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(u);


            //画当前定位标志
            MapStatusUpdate uc= MapStatusUpdateFactory.newLatLng(latlan);//中心点位置
            mBaiduMap.animateMapStatus(uc);

            mMapView.showZoomControls(true);
            //poi 搜索周边
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Looper.prepare();
//                    searchNeayBy();
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

  /*  */

    @Override
    public void onGetPoiResult(PoiResult poiResult) {

    }



    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

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
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(poiInfo.uid));
            return true;
        }
    }




    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
//        mPoiSearch.destroy();
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

        mGeoCoder.destroy();    // 释放地理编译实例

        mMapView.onDestroy();   // MapView也进行销毁
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
