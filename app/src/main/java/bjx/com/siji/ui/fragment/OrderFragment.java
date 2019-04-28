package bjx.com.siji.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import bjx.com.siji.R;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.activity.CreateOrderActivity;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.ui.order.MyOrderActivity;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * 两个界面在一个里面写的，根据后台返回数据进行切换，暂时显示的是接单界面，另一个界面通过gone隐藏
 */
public class OrderFragment extends BaseFragment implements View.OnClickListener {
    LocationService locationService; // 定位service
    private int getOrderPosition;
    /**
     * 订单界面
     */
    private Context mContext;
    private TextView create_order;


    private ListView order_list;
    private int pageId = -1;
    private ArrayList<OrderList> data = new ArrayList<OrderList>();
    private OrderListAdapter orderListAdapter;
    private String sj_id = "";

    private Handler handler = null;
    private TextView create_to;

    private ImageView refresh;

    private TextView refresh_order_list;
    private MainActivity mainActivity;

    //实时刷新订单，判断是否被客户取消
    public Timer queryOrderIsCancelTimer = new Timer();

    public TimerTask queryOrderIsCancelTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mainActivity.getOrderInfo() != null) {
                String orderId = mainActivity.getOrderInfo().getOrder_id();
                UserModel currentDriver = (UserModel) SPUtils.readObject(mainActivity, Constants.USERMODEL);
                String sj_id = currentDriver.getSj_id();
                String key = MD5.getMessageDigest((orderId + Constants.BASE_KEY + sj_id).getBytes());
                if (orderId != null && !"".equals(orderId.trim())) {
                    mEngine.getOrderInfo(sj_id, orderId, MD5.getMessageDigest((orderId + Constants.BASE_KEY + sj_id).getBytes())).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String str = response.body().string();
                                JSONObject jo = new JSONObject(str);
                                int status = jo.getInt("status");
                                String msg = jo.getString("msg");
                                if (status == 200) {
                                    JSONObject jsonObject = jo.getJSONObject("result");
                                    if (jsonObject.getString("order_status") != null && (jsonObject.getString("order_status").equals("2") || jsonObject.getString("order_status").equals("9")) || jsonObject.getString("order_status").equals("0")) {
                                        //客户已取消订单,或者被后台重派
                                        ((MainActivity) getActivity()).setOrderInfo(null);
                                        queryOrderIsCancelTimer.cancel();
                                        startActivity(new Intent(mApp, MainActivity.class)
                                                .putExtra("tabindex", 20));

                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }

            }
        }
    };


    @Override
    protected void initView(Bundle savedInstanceState) {
        locationService = mApp.locationService;
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.fragment_order, null);
        //Toolbar
        Toolbar toolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);
        this.mContext = getActivity();
        UserModel currentDriver = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        sj_id = "";
        if (currentDriver != null) {
            sj_id = currentDriver.getSj_id();
        }

        initView();
        refresh(false);
        dealWith();
    }

    private void refresh(final boolean repeat) {
        pageId = 1;
        Log.e("订单列表", sj_id);
        mEngine.getOrderList(sj_id, MD5.getMessageDigest((Constants.BASE_KEY + sj_id).getBytes()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            if (status == 200) {
                                JSONArray resultData = jo.getJSONArray("result");
                                List<OrderList> resultList = new ArrayList<OrderList>();
                                if (resultData != null && resultData.length() > 0) {
                                    for (int i = 0; i < resultData.length(); i++) {
                                        JSONObject obj = (JSONObject) resultData.get(i);
                                        OrderList order = new OrderList();
                                        order.setAdd_time(obj.getString("add_time"));
                                        order.setAddress_end(obj.getString("address_end"));
                                        order.setAddress_st(obj.getString("address_st"));
                                        order.setEnd_lat(obj.getString("end_lat"));
                                        order.setEnd_long(obj.getString("end_long"));
                                        order.setKh_ext(obj.getString("kh_ext"));
                                        order.setNegotiate(obj.getString("negotiate"));
                                        order.setOrder_id(obj.getString("order_id"));
                                        order.setReject_list(obj.getString("reject_list"));
                                        order.setOrder_sn(obj.getString("order_sn"));
                                        order.setOrder_status(obj.getString("order_status"));
                                        order.setSend_time(obj.getString("send_time"));
                                        order.setUse_mobile(obj.getString("use_mobile"));
                                        order.setUser_id(obj.getString("user_id"));
                                        String rest = obj.getString("rest");
                                        order.setRest(rest);
                                        order.setUse_name(obj.getString("use_name"));
                                        order.setSt_lat(obj.getString("st_lat"));
                                        order.setSt_long(obj.getString("st_long"));

                                        order.setLeftTime(rest);

                                        order.setPaidan(obj.getString("paidan"));

                                        resultList.add(order);
                                    }
                                } else {
                                    create_to.setText("");
                                    create_to.setVisibility(View.VISIBLE);
                                    create_order.performClick();
                                }
                                orderListAdapter = new OrderListAdapter(mApp, resultList);
                                handler = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (!orderListAdapter.startClock) {
                                            orderListAdapter.startClock = true;
                                        }
                                        if (orderListAdapter.canGetOrderSum <= 0) {
                                            return;
                                        }
                                        orderListAdapter.notifyDataSetChanged();
                                        handler.sendEmptyMessageDelayed(0, 1000);
                                    }
                                };
                                order_list.setAdapter(orderListAdapter);
                                if (!repeat) {
                                    //如果刷新订单列表，不需要重复倒计时
                                    handler.sendEmptyMessageDelayed(0, 1000);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            create_to.setText("");
                            create_to.setVisibility(View.VISIBLE);
                            create_order.performClick();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private void dealWith() {
        LogsUtil.normal("订单列表");
        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).getOrderInfo() != null) {
                    showToast("存在已接受的订单，无法创建新的订单");
                    return;
                }
                startActivity(new Intent(mApp, CreateOrderActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        });
    }

    private void initView() {
        userModel = (UserModel) SPUtils.readObject(mContext, Constants.USERMODEL);
        create_order = (TextView) mContentView.findViewById(R.id.create_order);//创建
        order_list = (ListView) mContentView.findViewById(R.id.order_list);
        create_to = (TextView) mContentView.findViewById(R.id.create_to);
        refresh = (ImageView) mContentView.findViewById(R.id.refresh);

        refresh_order_list = (TextView) mContentView.findViewById(R.id.refresh_order_list);

        //刷新订单列表
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** 设置旋转动画 */
                final RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);//设置动画持续时间
                refresh.setAnimation(animation);
                view.startAnimation(animation);
                refresh(true);
            }
        });

        //查看订单列表
        refresh_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }


    class OrderListAdapter extends BaseAdapter {
        private Context context;
        private List<OrderList> lists = new ArrayList<>();
        public int canGetOrderSum;
        private boolean startClock = false;
        private boolean remove = false;

        public OrderListAdapter(Context context, List<OrderList> OrderList) {
            this.context = context;
            if (OrderList != null) {
                this.lists = OrderList;
                canGetOrderSum = OrderList.size();
            }

        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

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


                showToast("接单成功");
                orderListAdapter.canGetOrderSum--;


 //                                            startActivity(new Intent(mApp, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//                                            Intent intent = new Intent(mActivity,MMapFragment.class);
//                                            intent.putExtra("type","2");
//                                            startActivity(intent);
//                                            mActivity.finish();

//                                            OrderList listt = GsonUtil.jsonListBean(jo.getString("result"));
                //开启线程，若订单被取消，返回主页面
                mainActivity = (MainActivity) getActivity();
                queryOrderIsCancelTimer.schedule(queryOrderIsCancelTimerTask
                        , 500, 500);

                OrderList order = lists.get(getOrderPosition);
                order.getOrderLat = location.getLatitude() + "";
                order.getOrderLong = location.getLongitude() + "";

                //清理上一次订单的全部数据

                SPUtils.put(mApp, Constants.driveTime, new Long(0));
                SPUtils.put(mApp, Constants.restartDriveTime, new Long(0));

                SPUtils.put(mApp, Constants.waitTime, new Long(0));
                SPUtils.put(mApp, Constants.restartwaitTime, new Long(0));

                PreferenceUtil.clearDriveDistanceTimeSharePreference(mContext,
                        Constant.CONFIG_INTENT_ALREADY_DIS_TIME);

                PreferenceUtil.clearLatlngSharePreference(mContext,
                        Constant.CONFIG_INTENT_LAT_LNG);

                PreferenceUtil.clearRestartDriveDistanceTimeSharePreference(mContext,
                        Constant.CONFIG_INTENT_RESTART_ALREADY_DIS_TIME);

                PreferenceUtil.clearRestartLatlngSharePreference(mContext,
                        Constant.CONFIG_INTENT_RESTART_LAT_LNG);

                startActivity(new Intent(mApp, MainActivity.class)
                        .putExtra("tabindex", 2)
                        .putExtra("orderinfo", order));

                LogsUtil.order("orderFragment 接单成功 发给MainActivity" + order.toString());

                // orderListAdapter.notifyDataSetChanged();
            }
        };


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_order_list, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final View currentView = convertView;
//            holder.clock.setText(lists.get(position).getAdd_time());
            holder.order_number.setText(lists.get(position).getOrder_sn());

            if (lists.get(position).getAdd_time() != null && lists.get(position).getAdd_time().length() > 0) {
//                TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
//                TimeZone.setDefault(tz);
                int timess = Integer.parseInt(lists.get(position).getAdd_time());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String times = format.format(new Date(timess * 1000L));
                holder.order_create_time.setText(times + "");
            }


            holder.mobile.setText(lists.get(position).getUse_mobile());
            holder.address_start.setText(lists.get(position).getAddress_st());
            holder.address_end.setText(lists.get(position).getAddress_end());
            if (lists.get(position).getNegotiate().equals("0.00")) {
                holder.money_use.setText("无");
            } else {
                holder.money_use.setText(lists.get(position).getNegotiate() + "元");
            }

            //倒计时处理
            long leftSeconds = 0;
            if (remove) {
                //用户拒接接单刷新订单列表时，计时器不更新
                remove = false;
                leftSeconds = Long.parseLong(lists.get(position).getLeftTime());
            } else {
                if (startClock) {
                    //计时器已开启，倒计时递减
                    leftSeconds = (Long.parseLong(lists.get(position).getLeftTime())) - 1;
                } else {
                    //计时器未开始（初次加载适配器数据）
                    leftSeconds = Long.parseLong(lists.get(position).getLeftTime());
                }
            }


            if (leftSeconds <= 0) {
                canGetOrderSum--;
                leftSeconds = 0;
                holder.new_order_cancel.performClick();
            } else {
                lists.get(position).setLeftTime(leftSeconds + "");
                holder.clock.setText(leftSeconds + "秒");
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("获取的数据", "获取的数据");
                }
            });

            holder.new_order_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((MainActivity) getActivity()).getOrderInfo() != null) {
                        showToast("已有接收的订单，无法继续接单");
                        return;
                    }

                    Log.e("订单接单", "订单接单");
                    String order_ids = lists.get(position).getOrder_id();
                    mEngine.ReceiveOrderList(sj_id, order_ids, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + order_ids).getBytes()))
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {

                                        String str = response.body().string();

                                        LogsUtil.order("接单数据返回==" + str);
                                        Log.e("订单接单+str--->", str);
                                        JSONObject jo = new JSONObject(str);
                                        int status = jo.getInt("status");
                                        Log.e("订单接单", "订单接单");
                                        if (status == 200) {
                                            //确定司机接单时的位置
                                            getOrderPosition = position;
                                            locationService.registerListener(mBDLocListener2);

                                            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                                            locationService.start();// 定位SDK

                                        } else if (status == 100) {
//                                            showToast("参数错误");
                                            Log.e("订单接单", "参数错误");
                                        } else if (status == 101) {
                                            Log.e("订单接单", "验证失败");
                                        } else if (status == 202) {
                                            Log.e("订单接单", "订单不存在");
                                        } else if (status == 203) {
                                            Log.e("订单接单", "订单状态不匹配");
                                        } else if (status == 204) {
                                            Log.e("订单接单", "司机不存在或状态不正常");
                                        } else if (status == 205) {
                                            Log.e("订单接单", "订单超时已取消");
                                        } else {
                                            Log.e("订单接单", "error");
                                        }
//                                        lists.remove(position);
//                                        orderListAdapter.notifyDataSetChanged();
//                                    listView.setRefreshFail("刷新失败");
//                                    listView.stopLoadMore();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });

                }
            });


            //删除订单
            holder.new_order_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String order_ids = lists.get(position).getOrder_id();
                    mEngine.RefuseOrderList(sj_id, order_ids, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + order_ids).getBytes()))
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        String str = response.body().string();
                                        JSONObject jo = new JSONObject(str);
                                        int status = jo.getInt("status");
                                        if (status == 200) {
                                            Log.e("拒绝接单", "拒绝成功");

                                        } else if (status == 101) {
//                                            showToast("参数错误");
                                            Log.e("拒绝接单", "验证失败");
                                        } else if (status == 203) {
                                            Log.e("拒绝接单", "失败，系统错误");
                                        } else {
                                            Log.e("拒绝接单", "error");
                                        }

//                                        data.clear();

                                        //删除动画效果
                                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1.0f,
                                                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0f);
                                        translateAnimation.setDuration(200);
                                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                lists.remove(position);
                                                orderListAdapter.remove = true;
                                                orderListAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                        currentView.startAnimation(translateAnimation);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });


                }
            });


            return convertView;
        }


        class ViewHolder {
            TextView clock;
            TextView order_number;
            TextView order_create_time;

            TextView mobile;
            TextView address_start;
            TextView address_end;
            TextView money_use;
            TextView new_order_submit;
            TextView new_order_cancel;

            public ViewHolder(View view) {
                clock = (TextView) view.findViewById(R.id.clock);
                order_number = (TextView) view.findViewById(R.id.order_number);
                order_create_time = (TextView) view.findViewById(R.id.order_create_time);

                mobile = (TextView) view.findViewById(R.id.mobile);
                address_start = (TextView) view.findViewById(R.id.address_start);
                address_end = (TextView) view.findViewById(R.id.address_end);
                money_use = (TextView) view.findViewById(R.id.money_use);
                new_order_submit = (Button) view.findViewById(R.id.new_order_submit);
                new_order_cancel = (TextView) view.findViewById(R.id.new_order_cancel);
            }
        }
    }

}

/*

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            */
/***
 * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
 *//*

            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			*/
/*
 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
 *//*

            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }



    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }*/
