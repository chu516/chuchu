package bjx.com.siji.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import bjx.com.siji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图显示
 * Created by Administrator on 2017/11/27.
 */

public class CreateOrderMapActivity extends AppCompatActivity  implements BDLocationListener, OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener, TextWatcher {


    private TextureMapView mMapView = null;
    private BaiduMap mBaiduMap;
    private ListView poisLL;

    private ImageView center_id;
    private ImageView main_iv_position;
    private ImageView back;

    private  LatLng point;

    /**
     * 定位模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode;
    /**
     * 定位端
     */
    private LocationClient mLocClient;
    /**
     * 是否是第一次定位
     */
    private boolean isFirstLoc = true;
    /**
     * 定位坐标
     */
    private LatLng locationLatLng;
    /**
     * 定位城市
     */
    private String city;
    /**
     * 定位省
     */
    private String Province;
    /**
     * 定位区
     */
    private String District;
    /**
     * 反地理编码
     */
    private GeoCoder geoCoder;
    /**
     * 界面上方布局
     */
//    private RelativeLayout topRL;
    /**
     * 搜索地址输入框
     */
//    private EditText searchAddress;
    /**
     * 搜索输入框对应的ListView
     */
//    private ListView searchPois;

    /**
     * 适配器
     * */
//    private PoiSearchAdapter poiSearchAdapter;
    /**
     * 当前标志
     */
    private Marker mCurrentMarker;

    private List<PoiInfo> dataList;

    private PoiAdapter poiAdapter;

    int whereto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现


        SDKInitializer.initialize(getApplicationContext());
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_order_map);





        initView();
        String sID=getIntent().getStringExtra("type");
        whereto=Integer.parseInt(sID);
        if(whereto == 1) {
            center_id.setImageResource(R.mipmap.icon_s);
        } else if (whereto == 3){
            center_id.setImageResource(R.mipmap.icon_e);
        } else {
            center_id.setImageResource(R.mipmap.baidumap_ico_poi_on);
        }


        dataList = new ArrayList<PoiInfo>();
        initEvent();

//        initLocation();
    }

    private void initView(){
        mMapView = (TextureMapView) findViewById(R.id.main_bdmap);
        mBaiduMap = mMapView.getMap();

        poisLL = (ListView) findViewById(R.id.main_pois);

        back = (ImageView) findViewById(R.id.back);
        main_iv_position = (ImageView) findViewById(R.id.main_iv_position);

        center_id = (ImageView) findViewById(R.id.center_id);

//        topRL = (RelativeLayout) findViewById(R.id.main_top_RL);
//
//        searchAddress = (EditText) findViewById(R.id.main_search_address);
//
//        searchPois = (ListView) findViewById(R.id.main_search_pois);

      initLocation();
    }

    private void initLocation() {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        //地图状态改变相关监听
        mBaiduMap.setOnMapStatusChangeListener(this);

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位图层显示方式
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        /**
         * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
         * customMarker用户自定义定位图标
         * enableDirection是否允许显示方向信息
         * locationMode定位图层显示方式
         */
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));

        //初始化定位
        mLocClient = new LocationClient(this);
        //注册定位监听
        mLocClient.registerLocationListener(this);

        //定位选项
        LocationClientOption option = new LocationClientOption();
        /**
         * coorType - 取值有3个：
         * 返回国测局经纬度坐标系：gcj02
         * 返回百度墨卡托坐标系 ：bd09
         * 返回百度经纬度坐标系 ：bd09ll
         */
        option.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        option.setIsNeedAddress(true);
        //设置是否需要返回位置语义化信息，可以在BDLocation.getLocationDescribe()中得到数据，ex:"在天安门附近"， 可以用作地址信息的补充
        option.setIsNeedLocationDescribe(true);
        //设置是否需要返回位置POI信息，可以在BDLocation.getPoiList()中得到数据
        option.setIsNeedLocationPoiList(true);
        /**
         * 设置定位模式
         * Battery_Saving
         * 低功耗模式
         * Device_Sensors
         * 仅设备(Gps)模式
         * Hight_Accuracy
         * 高精度模式
         */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置是否打开gps进行定位
        option.setOpenGps(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        option.setScanSpan(1000);

        //设置 LocationClientOption
        mLocClient.setLocOption(option);

        //开始定位
        mLocClient.start();
    }

    /**
     * 事件初始化
     */
    private void initEvent(){



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        main_iv_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoc = true;
                initLocation();
            }
        });
        //




//        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
        poisLL.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
//                checkPosition = position;
//                adapter.setCheckposition(position);
//                adapter.notifyDataSetChanged();
//                PoiInfo ad = (PoiInfo) poiAdapter.getItem(position);
//                Toast.makeText(CreateOrderMapActivity.this,"省："+Province+",市："+city+","+District+",地址："+ad.address+",坐标:"+ad.location,Toast.LENGTH_SHORT).show();
//                Log.i("----------","省："+Province+",市："+city+","+District+",地址："+ad.address+",坐标:"+ad.location);
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ad.location);
//                mBaiduMap.animateMapStatus(u);
//                mCurrentMarker.setPosition(ad.location);
            }
        });
//
//        mRequestLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
////                ToastUtil.show(this, "正在定位。。。");
//                Toast.makeText(getApplicationContext(),"正在定位。。。",Toast.LENGTH_SHORT).show();
//                initLocation();
//            }
//        });

//        mCompleteButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
////                ToastUtil.show(getApplicationContext(), "名称是: " + dataList.get(checkPosition).name+" 地址是："+dataList.get(checkPosition).address);
////                Toast.makeText(getApplicationContext(),"名称是: " + dataList.get(checkPosition).name+" 地址是："+dataList.get(checkPosition).address,Toast.LENGTH_SHORT).show();
//
//                //  获取页面输入的内容
////                backData = dataList.get(checkPosition).address.toString().trim();
//
//
//                // 判断是否填写了内容
//                if (address==null || "".equals(address)){
//                    Toast.makeText(CreateOrderMapActivity.this, "请选择开始代驾地址", Toast.LENGTH_SHORT).show();
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
//
//
//
//                //  结束当前页面(关闭当前界面)
//                finish();
//
//
//
//            }
//        });

//        mCannelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

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
//                       /*     private String addrStr;// 反地理编码
//                            private String province;// 省份信息
//                            private String city;// 城市信息
//                            private String district;// 区县信息
//                            private float direction;// 手机方向信息        private double longitude;// 精度
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

//            public boolean onMapPoiClick(MapPoi arg0) {
//                arg0.getName(); //名称
//                arg0.getPosition(); //坐标
//                String name = arg0.getName();
//                LatLng zuobiao = arg0.getPosition();
//                System.out.println("点击信息-------------------------------------------------------》");
//                System.out.println("地址="+name+",坐标"+zuobiao);
//
//                return false;
//            }
//        });

    }

    /**
     * 定位监听
     *
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        //如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
        if (bdLocation == null || mBaiduMap == null) {
            return;
        }

        //定位数据
        MyLocationData data = new MyLocationData.Builder()
                //定位精度bdLocation.getRadius()
                .accuracy(bdLocation.getRadius())
                //此处设置开发者获取到的方向信息，顺时针0-360
                .direction(bdLocation.getDirection())
                //经度
                .latitude(bdLocation.getLatitude())
                //纬度
                .longitude(bdLocation.getLongitude())
                //构建
                .build();

        //设置定位数据
        mBaiduMap.setMyLocationData(data);

        //是否是第一次定位
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 18);
            mBaiduMap.animateMapStatus(msu);
        }

        //获取坐标，待会用于POI信息点与定位的距离
        locationLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        //获取城市，待会用于POISearch
        city = bdLocation.getCity();
        Province = bdLocation.getProvince();
        District = bdLocation.getDistrict();

        //文本输入框改变监听，必须在定位完成之后
//        searchAddress.addTextChangedListener(this);

        //创建GeoCoder实例对象
        geoCoder = GeoCoder.newInstance();
        //发起反地理编码请求(经纬度->地址信息)
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        //设置反地理编码位置坐标
        reverseGeoCodeOption.location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        //获取选择的定位地址后加载到数据中，位置不可动，必须在设置查询结果监听者之前
        geoCoder.reverseGeoCode(reverseGeoCodeOption);
        //设置查询结果监听者
        geoCoder.setOnGetGeoCodeResultListener(this);



    }

    //地理编码查询结果回调函数
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    //反地理编码查询结果回调函数
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList();
        poiAdapter = new PoiAdapter(CreateOrderMapActivity.this, poiInfos);
        poisLL.setAdapter(poiAdapter);
    }


    /**
     * 手势操作地图，设置地图状态等操作导致地图状态开始改变
     *
     * @param mapStatus 地图状态改变开始时的地图状态
     */
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    /**
     * 地图状态变化中
     *
     * @param mapStatus 当前地图状态
     */
    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    /**
     * 地图状态改变结束
     *
     * @param mapStatus 地图状态改变结束后的地图状态
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        //地图操作的中心点
        LatLng cenpt = mapStatus.target;
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
    }

    /**
     * 输入框监听---输入之前
     *
     * @param s     字符序列
     * @param start 开始
     * @param count 总计
     * @param after 之后
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * 输入框监听---正在输入
     *
     * @param s      字符序列
     * @param start  开始
     * @param before 之前
     * @param count  总计
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * 输入框监听---输入完毕
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
//        if (s.length() == 0 || "".equals(s.toString())) {
//            searchPois.setVisibility(View.GONE);
//        } else {
//            //创建PoiSearch实例
//            PoiSearch poiSearch = PoiSearch.newInstance();
//            //城市内检索
//            PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
//            //关键字
//            poiCitySearchOption.keyword(s.toString());
//            //城市
//            poiCitySearchOption.city(city);
//            //设置每页容量，默认为每页10条
//            poiCitySearchOption.pageCapacity(10);
//            //分页编号
//            poiCitySearchOption.pageNum(1);
//            poiSearch.searchInCity(poiCitySearchOption);
//            //设置poi检索监听者
//            poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
//                //poi 查询结果回调
//                @Override
//                public void onGetPoiResult(PoiResult poiResult) {
//                    List<PoiInfo> poiInfos = poiResult.getAllPoi();
//                    poiSearchAdapter = new PoiSearchAdapter(MainActivity.this, poiInfos, locationLatLng);
//                    searchPois.setVisibility(View.VISIBLE);
//                    searchPois.setAdapter(poiSearchAdapter);
//
//                }
//
//                //poi 详情查询结果回调
//                @Override
//                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//                }
//            });
//        }
    }


    //回退键
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //退出时停止定位
        mLocClient.stop();
        //退出时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();

        //释放资源
        if (geoCoder != null) {
            geoCoder.destroy();
        }

        mMapView = null;
    }



  /*  class ListAdapter extends BaseAdapter implements android.widget.ListAdapter {

        private int checkPosition;

        public ListAdapter(int checkPosition){
            this.checkPosition = checkPosition;
        }

        public void setCheckposition(int checkPosition){
            this.checkPosition = checkPosition;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if(convertView == null){
                holder = new CreateOrderMapActivity.ViewHolder();
                convertView = LayoutInflater.from(CreateOrderMapActivity.this).inflate(R.layout.list_item, null);

                holder.textView = (TextView) convertView.findViewById(R.id.text_name);
                holder.textAddress = (TextView) convertView.findViewById(R.id.text_address);
                holder.imageLl = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);

            }else{
                holder = (CreateOrderMapActivity.ViewHolder)convertView.getTag();
            }
            Log.i("mybaidumap", "name地址是："+dataList.get(position).name);
            Log.i("mybaidumap", "address地址是："+dataList.get(position).address);

            holder.textView.setText(dataList.get(position).name);
            holder.textAddress.setText(dataList.get(position).address);
            if(checkPosition == position){
                holder.imageLl.setVisibility(View.VISIBLE);
            }else{
                holder.imageLl.setVisibility(View.GONE);
            }



            return convertView;
        }

    }*/

    class PoiAdapter extends BaseAdapter implements android.widget.ListAdapter{
        private Context context;
        private List<PoiInfo> pois;
        private LinearLayout linearLayout;


        PoiAdapter(Context context, List<PoiInfo> pois) {
            this.context = context;
            this.pois = pois;
        }

        @Override
        public int getCount() {
            return pois.size();
        }

        @Override
        public Object getItem(int position) {
            return pois.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.locationpois_item, null);
                linearLayout = (LinearLayout) convertView.findViewById(R.id.locationpois_linearlayout);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0 && linearLayout.getChildCount() < 2) {
                ImageView imageView = new ImageView(context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(32, 32);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                imageView.setImageResource(R.mipmap.baidumap_ico_poi_on);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageView, 0, params);
                holder.locationpoi_name.setTextColor(Color.parseColor("#FF5722"));
            }
            final PoiInfo poiInfo = pois.get(position);
            holder.locationpoi_name.setText(poiInfo.name);
            holder.locationpoi_address.setText(poiInfo.address);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Toast.makeText(CreateOrderMapActivity.this,"省："+Province+",市："+city+","+District+",地址："+poiInfo.address+",坐标:"+poiInfo.location,Toast.LENGTH_SHORT).show();
                    String str_latitude = Double.toString(poiInfo.location.latitude);
                    String str_longitude = Double.toString(poiInfo.location.longitude);
                    Intent intent = new Intent();
                    intent.putExtra("data", poiInfo.address);
                    intent.putExtra("longitude", str_longitude);
                    intent.putExtra("latitude", str_latitude);
                    intent.putExtra("province", Province);
                    intent.putExtra("city", city);
                    intent.putExtra("name",poiInfo.name);
                    intent.putExtra("district", District);

                    if(whereto==1||whereto==2){

                        Log.i("结果","1");
                        setResult(2, intent);
                    }else if(whereto==3||whereto==4){
                        Log.i("结果","3");
                        setResult(4, intent);
                    }



                    //  结束当前页面(关闭当前界面)
                    finish();
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView locationpoi_name;
            TextView locationpoi_address;

            ViewHolder(View view) {
                locationpoi_name = (TextView) view.findViewById(R.id.locationpois_name);
                locationpoi_address = (TextView) view.findViewById(R.id.locationpois_address);
            }
        }
    }





















//    class ViewHolder{
//        TextView textView;
//        TextView textAddress;
//        ImageView imageLl;
//    }



}



