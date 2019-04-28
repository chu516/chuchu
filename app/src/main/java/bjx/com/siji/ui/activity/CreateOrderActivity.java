package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.AccountValidatorUtil;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.model.OrderList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/12/7.
 */

public class CreateOrderActivity extends BaseActivity implements OnGetGeoCoderResultListener {


    private EditText type_phone;
    private Button create_btn;
    private ImageView end_mai;
    private ImageView start_mai;
    private TextView type_address;//地址信息
    private TextView type_address_end;//地址信息
    private EditText type_car;//备注信息

    //自动定位坐标
    private double startlatitude;
    private double startlongitude;


    private double longitude;
    private double latitude;
    private String backData = null;
    private String backData1 = null;

    private String province = null;
    private String city = null;
    private String district = null;

    private UserModel userModel;
    private String si_id;
    private String mobile;//手机号
    private String address;
    private String addressend;
    private String info;
    private String end_longitude;//longitude
    private String end_latitude;//latitude
    private String str_longitude;//longitude
    private String str_latitude;//latitude
    private String exit;//latitude

    private ImageView back;
    private GeoCoder geoCoder = null;


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_order);
        initView();
        userModel = (UserModel) SPUtils.readObject(CreateOrderActivity.this, Constants.USERMODEL);
        si_id = userModel.getSj_id();
    }

    @Override
    protected void setListener() {
        create_btn.setOnClickListener(this);
        end_mai.setOnClickListener(this);
        start_mai.setOnClickListener(this);
        type_address.setOnClickListener(this);
        type_address_end.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_btn:
                mobile = type_phone.getText().toString().trim();//手机号
                address = type_address.getText().toString().trim();
                addressend = type_address_end.getText().toString().trim();
                exit = type_car.getText().toString().trim();
                if (mobile == null) {
                    Toast.makeText(CreateOrderActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (mobile.length() != 11) {
                    Toast.makeText(CreateOrderActivity.this, "手机号码长度为11数字", Toast.LENGTH_SHORT).show();
                } else if (!AccountValidatorUtil.isMobile(mobile)) {
                    Toast.makeText(CreateOrderActivity.this, "手机号码输入不正确", Toast.LENGTH_SHORT).show();
                } else if (address.equals(null)) {
                    Toast.makeText(CreateOrderActivity.this, "请选择地址", Toast.LENGTH_SHORT).show();
                } else {
                    CreateOrder();
                }
                break;
            case R.id.start_mai:
            case R.id.type_address:
//                Intent over = new Intent(CreateOrderActivity.this, ShowMapInfoActivity.class);
//                over.putExtra("type",1+"");
//                startActivityForResult(over, 1);
                Intent over = new Intent(CreateOrderActivity.this, CreateOrderMapActivity.class);
                over.putExtra("type", 1 + "");
                startActivityForResult(over, 1);
                break;

            case R.id.end_mai:
            case R.id.type_address_end:
                Intent overs = new Intent(CreateOrderActivity.this, CreateOrderMapActivity.class);
                overs.putExtra("type", 3 + "");
                startActivityForResult(overs, 3);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }


    private void CreateOrder() {
//                Toast.makeText(CreateOrderActivity.this,MD5.getMessageDigest((si_id + Constants.BASE_KEY + phone).getBytes())+"",Toast.LENGTH_LONG).show();
        System.out.println("---------------------=" + MD5.getMessageDigest((si_id + Constants.BASE_KEY + mobile).getBytes()));


        Log.e("创建订单","订单信息>>>" + ",司机ID：" + si_id
                + ",手机号：" + mobile + ",开始地址："
                + address + ",精度：" + str_longitude
                + ",维度：" + str_latitude + ",结束地址："
                + addressend + ",结束精度：" + end_longitude
                + ",结束维度：" + end_longitude + ",省份："
                + province + ",城市：" + city + ",区：" + district);

        mEngine.create_order(si_id, mobile, address, str_longitude, str_latitude, addressend, end_longitude, end_latitude, exit, province, city, district,
                MD5.getMessageDigest((si_id + Constants.BASE_KEY + mobile).getBytes())).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");


                    Log.e("TAG", status + "");
                    if (status == 200) {

                        Toast.makeText(CreateOrderActivity.this, "订单创建成功", Toast.LENGTH_SHORT).show();

                        OrderList orderinfo = GsonUtil.jsonOrderBean(jo.getString("result"));
                        startActivity(new Intent(mApp, MainActivity.class)
                                .putExtra("tabindex", 2)
                                .putExtra("orderinfo", orderinfo));

                        cleanData();
                        finish();
                    } else if (status == 100) {
//                        showToast("参数错误");
                        Toast.makeText(CreateOrderActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                    } else if (status == 101) {
                        Toast.makeText(CreateOrderActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    } else if (status == 105 || status == 105 || status == 106 || status == 107 || status == 108) {
                        Toast.makeText(CreateOrderActivity.this, "不在服务区", Toast.LENGTH_SHORT).show();
                    } else if (status == 102) {
                        Toast.makeText(CreateOrderActivity.this, "创建用户失败", Toast.LENGTH_SHORT).show();
                    } else if (status == 104) {
                        Toast.makeText(CreateOrderActivity.this, "当前司机状态不正常", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateOrderActivity.this, "创建失败,请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateOrderActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cleanData() {
        si_id = null;
        mobile = null;
        address = null;
        str_longitude = null;
        str_latitude = null;
        exit = null;
        province = null;
        city = null;
        district = null;
        type_phone.setText("");
        type_address.setText(R.string.fragment_order_creat_start_address_1);
        type_car.setText("");
    }

    private void initView() {
        type_phone = (EditText) findViewById(R.id.type_phone);//手机号
        create_btn = (Button) findViewById(R.id.create_btn);//提交按钮
        type_address = (TextView) findViewById(R.id.type_address);//地址
        type_car = (EditText) findViewById(R.id.type_car);//
        type_address_end = (TextView) findViewById(R.id.type_address_end);//地址
        back = (ImageView) findViewById(R.id.back);//地址

        end_mai = (ImageView) findViewById(R.id.end_mai);
        start_mai = (ImageView) findViewById(R.id.end_mai);

        //自动定位显示开始地址
        autoStartAddress(type_address);
    }

    //自动定位显示开始地址
    public void autoStartAddress(final TextView addressTV) {
        //获取当前经纬度
        SDKInitializer.initialize(getApplicationContext());
        //初始化定位
        final LocationClient mLocClient = new LocationClient(this);
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
        mLocClient.setLocOption(option);
        mLocClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                    startlatitude = bdLocation.getLatitude();
                    startlongitude = bdLocation.getLongitude();
                    str_longitude = startlongitude + "";
                    str_latitude = startlatitude + "";
                    city = bdLocation.getCity();
                    province = bdLocation.getProvince();
                    district = bdLocation.getDistrict();
                    //根据当前经纬度获取地理位置信息
                    mLocClient.stop();
                    setAddress(startlatitude, startlongitude, addressTV);
                }
            }
        });
        //开始定位
        mLocClient.start();
    }

    private void setAddress(Double latitude, Double longitude, final TextView addressTV) {
        //创建GeoCoder实例对象
        geoCoder = GeoCoder.newInstance();
        //发起反地理编码请求(经纬度->地址信息)
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        //设置反地理编码位置坐标
        reverseGeoCodeOption.location(new LatLng(latitude, longitude));
        //设置查询结果监听者
        geoCoder.setOnGetGeoCodeResultListener(this);
        geoCoder.reverseGeoCode(reverseGeoCodeOption);
    }


    /**
     * 设置跳转  接受返回数据
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("接收", requestCode + "," + resultCode + "," + data);
        //  如果请求码与返回码等于预期设置的值  则进行后续操作
        if (requestCode == 1 && resultCode == 2) {
            // 获取返回的数据
            backData = data.getStringExtra("data");
            str_longitude = data.getStringExtra("longitude");
            str_latitude = data.getStringExtra("latitude");
            province = data.getStringExtra("province");
            city = data.getStringExtra("city");
            district = data.getStringExtra("district");
            String name = data.getStringExtra("name");
            // 设置给页面的文本TextView显示
            if (!name.equals("")) {
                type_address.setText(name);
            } else {
                type_address.setText(backData);
            }

        } else if (requestCode == 3 && resultCode == 4) {
            backData1 = data.getStringExtra("data");
            end_longitude = data.getStringExtra("longitude");
            end_latitude = data.getStringExtra("latitude");
            province = data.getStringExtra("province");
            city = data.getStringExtra("city");
            district = data.getStringExtra("district");
            // 设置给页面的文本TextView显示
            String name1 = data.getStringExtra("name");
            if (!name1.equals("")) {
                type_address_end.setText(name1);
            } else {
                type_address_end.setText(backData1);
            }
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    //反地理编码查询结果回调函数
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList();
        //取第一个地址信息
        if (poiInfos != null && poiInfos.size() > 0) {
            PoiInfo address = poiInfos.get(0);
            type_address.setText(address.name);
            str_longitude = address.location.longitude + "";
            str_latitude = address.location.latitude + "";
        }
        geoCoder.destroy();
    }
}

