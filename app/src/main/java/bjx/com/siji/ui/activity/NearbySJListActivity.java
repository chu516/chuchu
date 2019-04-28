package bjx.com.siji.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import bjx.com.siji.R;
import bjx.com.siji.adapter.NearbySJGvAdapter;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.interfaces.ItemMultClickListener;
import bjx.com.siji.model.DriverNearbyModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jqc on 2017/10/25.
 */

public class NearbySJListActivity extends BaseActivity implements

        BGARefreshLayout.BGARefreshLayoutDelegate {

    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVEvent;

    BGARefreshLayout mRefreshLayout;
    ListView mListView;
    NearbySJGvAdapter mAdapter;
    RelativeLayout mRLDel;
    CheckBox mCBDelAll;
    TextView mTVDel;

    int mCurrentPage = 1;
    String pageCount = "10";

    LocationService locationService; // 定位service
    double mCurrentLat;  // 用户当前坐标
    double mCurrentLon;
    String mCurrentAddr;
    OrderList mOrderInfo; // 接收的客户订单


    List<DriverNearbyModel.ResultBean> mDriverNearList
            = new ArrayList<DriverNearbyModel.ResultBean>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nearby_sj_list);
        mIVBack = getViewById(R.id.back);

        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("附近司机列表");
        mTVEvent = getViewById(R.id.event);

        mRLDel = getViewById(R.id.myorder_rl_del);
        mCBDelAll = getViewById(R.id.myorder_cb_delall);
        mTVDel = getViewById(R.id.myorder_tv_del);

        mRefreshLayout = getViewById(R.id.rl_myorder_refresh);
        mListView = getViewById(R.id.myorder_lv);

        BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(mApp, true);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.red);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.custom_mooc_icon);
        moocStyleRefreshViewHolder.setRefreshViewBackgroundColorRes(R.color.grey_light);
        moocStyleRefreshViewHolder.setLoadMoreBackgroundColorRes(R.color.grey_light);
        moocStyleRefreshViewHolder.setSpringDistanceScale(0);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
        mRefreshLayout.setTag(false);


        mCurrentLon = getIntent().getDoubleExtra("lon", 0.0);
        mCurrentLat = getIntent().getDoubleExtra("lat", 0.0);

        mOrderInfo = getIntent().getParcelableExtra("orderinfo");

        if (mCurrentLon == 0.0) {
            locationService = mApp.locationService;
            //注册监听
            int type = getIntent().getIntExtra("from", 0);
            if (type == 0) {
                locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            } else if (type == 1) {
                locationService.setLocationOption(locationService.getOption());
            }
            locationService.registerListener(mBDLocListener);
            locationService.start();// 定位SDK
        } else {
            loadNearbySJListList();
        }
        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new NearbySJGvAdapter(mApp, mDriverNearList);
        mListView.setAdapter(mAdapter);

        mAdapter.setItemClickListener(new ItemMultClickListener() {
            @Override
            public void onItemClick(View view, int postion) {

                DriverNearbyModel.ResultBean bean =
                        (DriverNearbyModel.ResultBean) mAdapter.getItem(postion);

                //获取司机id
                UserModel currentDriver = (UserModel) SPUtils.readObject(NearbySJListActivity.this,
                        Constants.USERMODEL);

                 String orderId = mOrderInfo.getOrder_id();
                 if (TextUtils.isEmpty(orderId)) {
                    ToastUtil.show("没有获取到订单信息");
                    return;
                }
                LogsUtil.order("请参数数据" +
                        "司机getSj_id" + currentDriver.getSj_id()
                        + "转发给 getSj_id" + bean.getLdata().getSj_id()
                        + "订单id" + orderId);

                mEngine.getOrderForward(currentDriver.getSj_id(),
                        bean.getLdata().getSj_id() + "",
                        orderId).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            LogsUtil.order("转发订单" + str);

                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            String msg = jo.getString("msg");

                            if (status == 200) {
                                 if ("OK".equals(msg)){
                                    ToastUtil.show("转单成功");
                                }
                             } else if (status == 201) {
                                showToast("用户不存在");
                            } else if (status == 100) {
                                showToast("获取列表失败");
                            } else {
                                showToast("获取异常");
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


//                Intent intent = new Intent(this, GoodsDetailActivity.class);
//                intent.putExtra(Constant.CONFIG_INTENT_ID, bean.getId() + "");
//                intent.putExtra(Constant.CONFIG_INTENT_IMG, bean.getImg());
//                                intent.putExtra(Constant.CONFIG_INTENT_NAME, bean.getName());
//                startActivity(intent);
            }

            @Override
            public void onItemSubViewClick(int choice, int postion) {

            }
        });

    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
        mTVEvent.setOnClickListener(this);
        mRefreshLayout.setDelegate(this);
        mCBDelAll.setOnClickListener(this);
        mTVDel.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadNearbySJListList();
    }

    List<String> delOrder = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back:
                finish();
                break;
            default:
                break;
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

            loadNearbySJListList();
        }
    };

    private void loadNearbySJListList() {

        mEngine.moveLocation(String.valueOf(mCurrentLon), String.valueOf(mCurrentLat)).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String str = response.body().string();

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


                            LogsUtil.normal("大小--" + ja.length() + "status" + status);

                            DriverNearbyModel model = GsonUtil.jsonToDriverNearbyBean(str);

                            mAdapter.clearAll();
                            mAdapter.addAll(model.getResult());

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

    private String fmtDelOrder() {
        String delOrderStr = "";
        if (delOrder != null) {
            for (int i = 0; i < delOrder.size(); i++) {
                if (i == 0) {
                    delOrderStr += delOrder.get(i);
                } else {
                    delOrderStr += "," + delOrder.get(i);
                }
            }
        }
        return delOrderStr;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mCurrentPage = 1;

        mEngine.moveLocation(String.valueOf(mCurrentLon), String.valueOf(mCurrentLat)).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String str = response.body().string();

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


                            LogsUtil.normal("大小--" + ja.length() + "status" + status);

                            DriverNearbyModel model = GsonUtil.jsonToDriverNearbyBean(str);

                            mAdapter.clearAll();
                            mAdapter.addAll(model.getResult());

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

                        mRefreshLayout.endRefreshing();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mRefreshLayout.endRefreshing();

                    }
                });
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (((Boolean) refreshLayout.getTag()) == true) {
            return false;
        }

        mEngine.moveLocation(String.valueOf(mCurrentLon), String.valueOf(mCurrentLat)).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String str = response.body().string();

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


                            LogsUtil.normal("大小--" + ja.length() + "status" + status);

                            DriverNearbyModel model = GsonUtil.jsonToDriverNearbyBean(str);

                            mAdapter.clearAll();
                            mAdapter.addAll(model.getResult());

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
                        mRefreshLayout.endLoadingMore();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mRefreshLayout.endLoadingMore();

                    }
                });
        return true;
    }
}
