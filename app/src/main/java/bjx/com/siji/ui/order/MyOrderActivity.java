package bjx.com.siji.ui.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import bjx.com.siji.R;
import bjx.com.siji.adapter.MyOrderAdapter;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.MyOrderModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.activity.BaseActivity;
import bjx.com.siji.ui.activity.NavigationMapActivity;
import bjx.com.siji.ui.activity.RestartDriveActivity;
import bjx.com.siji.ui.activity.StartDriveActivity;
import bjx.com.siji.ui.activity.TallyOrderActivity;
import bjx.com.siji.utils.DensityUtil;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jqc on 2017/10/25.
 */

public class MyOrderActivity extends BaseActivity implements AdapterView.OnItemClickListener, BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener {

    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVEvent;

    BGARefreshLayout mRefreshLayout;
    ListView mListView;
    MyOrderAdapter mAdapter;
    RelativeLayout mRLDel;
    CheckBox mCBDelAll;
    TextView mTVDel;

    int mCurrentPage = 1;
    String pageCount = "10";

    LocationService locationService; // 定位service
    double mCurrentLat;  // 用户当前坐标
    double mCurrentLon;
    String mCurrentAddr;
    PopupWindow pop; //商品属性窗口


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_myorder);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("订单");
        mTVEvent = getViewById(R.id.event);
        mTVEvent.setText("编辑");
        mTVEvent.setVisibility(View.VISIBLE);

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

        mAdapter = new MyOrderAdapter(mApp, R.layout.lv_item_myorder);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
        mTVEvent.setOnClickListener(this);
        mRefreshLayout.setDelegate(this);
        mCBDelAll.setOnClickListener(this);
        mTVDel.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadOrderList();
    }

    List<String> delOrder = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event: // 编辑
                if (!mAdapter.isOpen()) {
                    mRefreshLayout.setPullDownRefreshEnable(false);
                    mRefreshLayout.setTag(true);
                    delOrder.clear();
                    mTVEvent.setText("完成");
                    mAdapter.switchDel(true);
                    mRLDel.setVisibility(View.VISIBLE);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CheckBox mCB = (CheckBox) view.findViewById(R.id.myorder_item_cb);
                            if (mCB.isChecked()) {
                                mCB.setChecked(false);
                                delOrder.remove(mAdapter.getItem(position).getOrder_id());
                            } else {
                                mCB.setChecked(true);
                                delOrder.add(mAdapter.getItem(position).getOrder_id());
                            }
                        }
                    });
                } else {
                    mRefreshLayout.setPullDownRefreshEnable(true);
                    mRefreshLayout.setTag(false);
                    mTVEvent.setText("编辑");
                    mAdapter.switchDel(false);
                    mRLDel.setVisibility(View.GONE);
                    mListView.setOnItemClickListener(this);
                }
                break;
            case R.id.myorder_cb_delall: // 全选
                if (mAdapter.isCheckAll()) {
                    mAdapter.switchCheck(false);
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        delOrder.remove(mAdapter.getData().get(i).getOrder_id());
                    }
                } else {
                    mAdapter.switchCheck(true);
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        delOrder.add(mAdapter.getData().get(i).getOrder_id());
                    }
                }
                break;
            case R.id.myorder_tv_del: // 删除所选
                if (delOrder.size() == 0) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                        .setMessage("确定要删除所选订单吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEngine.delOrder(fmtDelOrder(), userModel.getSj_id(),
                                        MD5.getMessageDigest((fmtDelOrder() + Constants.BASE_KEY + userModel.getSj_id()).getBytes())).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            String str = response.body().string();
                                            JSONObject jo = new JSONObject(str);
                                            int status = jo.getInt("status");
                                            String msg = jo.getString("msg");
                                            if (status == 200) {
                                                mRefreshLayout.setPullDownRefreshEnable(true);
                                                mRefreshLayout.setTag(false);
                                                mTVEvent.setText("编辑");
                                                mAdapter.switchDel(false);
                                                mRLDel.setVisibility(View.GONE);
                                                mListView.setOnItemClickListener(MyOrderActivity.this);
                                                loadOrderList();
                                                delOrder.clear();
                                                showToast("删除成功");
                                            } else if (status == 201) {
                                                showToast("用户不存在");
                                            } else if (status == 100) {
                                                showToast("删除失败");
                                            } else {
                                                showToast("删除异常");
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
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(mApp, OrderDetailActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT).putExtra("orderid", mAdapter.getItem(position).getOrder_id()));
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        switch (childView.getId()) {
            case R.id.myorder_item_cb: // 单选
                CheckBox mCB = ((CheckBox) childView);
                if (!mCB.isChecked()) {
                    mCB.setChecked(false);
                    delOrder.remove(mAdapter.getItem(position).getOrder_id());
                } else {
                    mCB.setChecked(true);
                    delOrder.add(mAdapter.getItem(position).getOrder_id());
                }
                break;

            case R.id.myorder_item_status: // 单选

                String orderStatus = mAdapter.getData().get(position).getOrder_status();
                String pay_status = mAdapter.getData().get(position).getPay_status();

                String orderStatusStr = "";
                if (orderStatus != null) {
                    if (orderStatus.equals("0")) {
                        orderStatusStr = "派发中";
                    } else if (orderStatus.equals("1")) {

                        orderStatusStr = "已接单";
                        //先选择是否达到位置
                        showNoticeDialog();

                    } else if (orderStatus.equals("2")) {
                        orderStatusStr = "已取消";
                    } else if (orderStatus.equals("3")) {
                        orderStatusStr = "进行中";
//
//                        startActivity(new Intent(mApp, OrderDetailActivity.class).
//                                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT).putExtra("orderid",
//                                mAdapter.getItem(position).getOrder_id()));

                        OrderList mOrderInfo = null;
                        String jsonOrder = PreferenceUtil.getOrderInfoPreference(MyOrderActivity.this,
                                Constant.CONFIG_INTENT_ORDER_INFO);
                        Gson gson = new Gson();

                        if (!TextUtils.isEmpty(jsonOrder)) {
                            mOrderInfo = gson.fromJson(jsonOrder, OrderList.class);
                        }
                        startActivity(new Intent(mApp, RestartDriveActivity.class).
                                putExtra("orderinfo", mOrderInfo).putExtra("lon", mCurrentLon).
                                putExtra("lat", mCurrentLat).putExtra("addr", mCurrentAddr));

                    } else if (orderStatus.equals("4")) {
                        if (pay_status.equals("0")) {
                            orderStatusStr = "未支付";

                            OrderList mOrderInfo = null;
                            String jsonOrder = PreferenceUtil.getOrderInfoPreference(MyOrderActivity.this,
                                    Constant.CONFIG_INTENT_ORDER_INFO);
                            Gson gson = new Gson();

                            if (!TextUtils.isEmpty(jsonOrder)) {
                                mOrderInfo = gson.fromJson(jsonOrder, OrderList.class);
                            }
                            startActivity(new Intent(mApp, TallyOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    .putExtra("orderinfo", mOrderInfo));
                        } else {
                            orderStatusStr = "已完成";
                        }
                    } else if (orderStatus.equals("9")) {
                        orderStatusStr = "待派发";
                    }
                }

                break;
            default:

                break;
        }
    }


    /**
     * 已接单 选择到达位置 还是百度导航
     */
    public void showNoticeDialog() {

        setBackgroundAlpha((float) 0.7);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_order_type_tip, null);

        Double popHeight = DensityUtil.getScreenHeight(this) * 0.4;

        pop = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        pop.update();
        pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);  //设置弹出菜单可输入
        pop.setTouchable(true);       //设置popupwindow可点击
        pop.setOutsideTouchable(false);//设置popupwindow外部可点击

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        pop.setFocusable(false);

        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 设置popWindow的显示和消失动画
        pop.setAnimationStyle(R.style.toastdig);

        // pop.setBackgroundDrawable(new BitmapDrawable());
        // 在底部显示
        pop.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView tv2 = (TextView) view.findViewById(R.id.cancel);
        TextView tv3 = (TextView) view.findViewById(R.id.ok);
        //
//        tv0.setText(R.string.soft_update_title);
//
//        tv2.setText(R.string.exit_account);
//
//        tv3.setText(R.string.soft_update_updatebtn);

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //百度导航
                 Intent intent=new Intent(MyOrderActivity.this,NavigationMapActivity.class);
                intent.putExtra(Constants.CONFIG_INTENT_RECEIPT,"receipt");
                startActivity(intent);

                 setBackgroundAlpha((float) 1.0);
                pop.dismiss();
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderList mOrderInfo = null;
                String jsonOrder = PreferenceUtil.getOrderInfoPreference(MyOrderActivity.this,
                        Constant.CONFIG_INTENT_ORDER_INFO);
                Gson gson = new Gson();

                if (!TextUtils.isEmpty(jsonOrder)) {
                    mOrderInfo = gson.fromJson(jsonOrder, OrderList.class);
                }
                startActivity(new Intent(mApp, StartDriveActivity.class).
                        putExtra("orderinfo", mOrderInfo).putExtra("lon", mCurrentLon).
                        putExtra("lat", mCurrentLat).putExtra("addr", mCurrentAddr));

                setBackgroundAlpha((float) 1.0);
                pop.dismiss();
            }
        });
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
        }
    };

    private void loadOrderList() {
        UserModel currentDriver = (UserModel) SPUtils.readObject(this, Constants.USERMODEL);

        LogsUtil.order("请参数数据" +
                "司机getSj_id" + currentDriver.getSj_id() +
                "页数 mCurrentPage" + String.valueOf(mCurrentPage)
                + "签名key" + MD5.getMessageDigest(("Yxdj!@#1357" + currentDriver.getSj_id()).getBytes()) +
                "数量 pageCount" + String.valueOf(pageCount));

        mEngine.loadOrderList(currentDriver.getSj_id(),
                MD5.getMessageDigest(("Yxdj!@#1357" + currentDriver.getSj_id()).getBytes()),
                String.valueOf(mCurrentPage), pageCount).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    LogsUtil.order("获取订单列表" + str);
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");

//                    String msg = jo.getString("msg");
//                    String fid = jo.getString("fid");

                    if (status == 200) {
                        JSONArray ja = jo.getJSONArray("result");
                        List<MyOrderModel> mOrderList = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            mOrderList.add(GsonUtil.jsonToOrderBean(ja.getJSONObject(i).toString()));
                        }
                        mAdapter.setData(mOrderList);
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
        mEngine.loadOrderList(userModel.getSj_id(), MD5.getMessageDigest((Constants.BASE_KEY + userModel.getSj_id()).getBytes()), String.valueOf(mCurrentPage), pageCount).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
//                    String msg = jo.getString("msg");
//                    String fid = jo.getString("fid");

                    if (status == 200) {
                        JSONArray ja = jo.getJSONArray("result");
                        List<MyOrderModel> mOrderList = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            mOrderList.add(GsonUtil.jsonToOrderBean(ja.getJSONObject(i).toString()));
                        }
                        mAdapter.setData(mOrderList);
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
        mEngine.loadOrderList(userModel.getSj_id(), MD5.getMessageDigest((Constants.BASE_KEY + userModel.getSj_id()).getBytes()), String.valueOf(mCurrentPage + 1), pageCount).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
//                    String msg = jo.getString("msg");
//                    String fid = jo.getString("fid");

                    if (status == 200) {
                        mCurrentPage++;
                        JSONArray ja = jo.getJSONArray("result");
                        List<MyOrderModel> mOrderList = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            mOrderList.add(GsonUtil.jsonToOrderBean(ja.getJSONObject(i).toString()));
                        }
                        mAdapter.addMoreData(mOrderList);
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
