package bjx.com.siji.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.PoiSearch;
//import com.baidu.navisdk.adapter.BNRoutePlanNode;
//import com.baidu.navisdk.adapter.BaiduNaviManager;
import bjx.com.siji.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
/**=====================================*/


/**=====================================*/

/**
 * Created by Administrator on 2017/11/29.
 * 导航
 * */


public class CustomerActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMapClickListener {

    private TextView back;
    private MapView mMapView;
    private BaiduMap baiduMap;
    private ImageView navigation;

    private LocationClient mLocationClient;         // 定位

    private MyLocationListener mBdLocationListener; // 定位监听
    private GeoCoder mGeoCoder;
    private PoiSearch mPoiSearch;

    LatLng LatLng1 = new LatLng(39.922797,116.926634);
    LatLng LatLng2 = new LatLng(39.947246, 116.414977);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        initView();
        initMap();
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"显示",Toast.LENGTH_SHORT).show();
//                Intent oco = new Intent(CustomerActivity.this,BNDemoGuideActivity.class);
//                startActivity(oco);
            }
        });

    }

    private void initMap() {
        baiduMap = mMapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  // 普通地图
        // 定位
        getLocation();
    }

    private void initView() {
        back = (TextView) findViewById(R.id.back);
        navigation = (ImageView) findViewById(R.id.navigation);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mv_main_mapView);
        mMapView.showZoomControls(true);//放大缩小控制器

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }








/**
 * 地图操作
 * ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 * 以下
 * */


    // 获取定位
    private void getLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mBdLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mBdLocationListener);  // 注册监听者
        initLocation(mLocationClient);  // client 设置参数
        mLocationClient.start();
    }

    // 定位设置
    public void initLocation(LocationClient locationClient) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);   // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");   // 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);       // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);  // 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);        // 可选，默认false,设置是否使用gps
        option.setLocationNotify(true); // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true); // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);  // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);     // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);  // 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);     // 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);    // 设置到咱们的客户端
    }

    // 定位类
    class MyLocationListener implements BDLocationListener {
        //经纬度信息
        private double dLatitude;
        private double dLongitude;
        //城市与地址信息
        private String sCityStr;
        private String sAddStr;

        // 当开启定位的时候,走这个方法
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                dLatitude = location.getLatitude();     // 纬度
                dLongitude = location.getLongitude();   // 经度
                sCityStr = location.getCity();          // 城市
                sAddStr = location.getAddrStr();        // 地址

                LatLng latLng = new LatLng(dLatitude, dLongitude);
                setMap(latLng);  // 设置中心点  设置Marker


                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
//                        mFindContent.setText(sAddStr.isEmpty() ? "" : StringUtil.getLaterString(sAddStr, "省")); // 这里自己的工具类
                        if (sAddStr.isEmpty()) {
//                            ToastUtil.showShortToast("抱歉为获取到信息！");   // Toast工具类
                            Toast.makeText(CustomerActivity.this,"抱歉未获取到信息",Toast.LENGTH_SHORT).show();
                        }
                        mLocationClient.stop();
                    }
                });
            }
        }

    }



    // 设置中心点  设置Marker
    private void setMap(LatLng latLng) {
        if (latLng != null) {
            if (baiduMap != null) {
                baiduMap.clear();
            }

//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 17);//定位获取的数据----中心点数据
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(LatLng1, 17);//传递过来的数据----中心点数据
            if (update != null) {
                baiduMap.setMapStatus(update);  // 设置中心点


                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_en); // 添加覆盖物------地图上的现实标记
                BitmapDescriptor bitmapDescriptores = BitmapDescriptorFactory.fromResource(R.drawable.icon_st); // 添加覆盖物------地图上的现实标记
                BitmapDescriptor bitmapDescriptoress = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 添加覆盖物------地图上的现实标记

                List<OverlayOptions> options = new ArrayList<OverlayOptions>();//---------多标记数组



                /**
                 * 标记绑定数据
                 */

                OverlayOptions option1 =  new MarkerOptions()
                        .position(LatLng1)
                        .icon(bitmapDescriptor);//传递的数据
                OverlayOptions option2 =  new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptores);//获取的数据
                OverlayOptions option3 =  new MarkerOptions()
                        .position(LatLng2)
                        .icon(bitmapDescriptoress);//获取的数据
                options.add(option1);
                options.add(option2);
                options.add(option3);


//                Marker marker = (Marker) baiduMap.addOverlays(options);  // 添加到地图中 并获取返回值 Marker具体的覆盖物
                baiduMap.addOverlays(options);  // 添加到地图中 并获取返回值 Marker具体的覆盖物
            }
        }
    }

}
