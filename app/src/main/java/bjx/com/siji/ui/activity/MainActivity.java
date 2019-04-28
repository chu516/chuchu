package bjx.com.siji.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.Ringtone;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import bjx.com.siji.R;
import bjx.com.siji.adapter.SearchLocAdapter;
import bjx.com.siji.application.App;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.ChargeModel;
import bjx.com.siji.model.EventBean;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.ResultModel;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.DriverService;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.fragment.MapFragment;
import bjx.com.siji.ui.fragment.MoreFragment;
import bjx.com.siji.ui.fragment.OrderFragment;
import bjx.com.siji.ui.fragment.StateFragment;
import bjx.com.siji.ui.update.OkGoUpdateHttpUtil;
import bjx.com.siji.utils.CProgressDialogUtils;
import bjx.com.siji.utils.DialogUtil;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.HProgressDialogUtils;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.PermissionUtils;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;
import com.lzy.okgo.OkGo;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.service.DownloadService;
import com.vector.update_app.utils.AppUpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jqc on 2017/9/12.
 */

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public boolean newDriver;
    private static Ringtone mRingtone;

    public static Handler mainHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 250) {//有新订单，跳到订单页面刷新
                getNewOrder((Context) msg.obj);

                LogsUtil.normal("跳到订单列表  MainActivity");
            } else if (msg.what == -250) {
                if (mRingtone != null)
                    mRingtone.stop();
            }
        }
    };

    private static LinearLayout mTabBtnState; // 状态
    private static LinearLayout mTabBtnOrder; // 订单
    private static LinearLayout mTabBtnMap; // 地图
    private static LinearLayout mTabBtnMore; // 更多

    private static FragmentManager fragmentManager;  // fragment管理
    private static StateFragment stateFragment;  // 状态fragment
    private static OrderFragment orderFragment; // 订单fragment
    public static MapFragment mapFragment; // 地图fragment
    private static MoreFragment moreFragment; // 更多fragment
    public int tabPosition;  // 当前停留的选项卡

    private LocationService locationService; // 百度定位service
    public double mCurretLon; // 用户当前经纬度
    public double mCurretLat;

    private OrderList orderInfo;    // 接收的订单
    private PoiInfo poiInfo; // 设置的导航终点poi


    /**
     * =========导航搜索==========
     */
    public RelativeLayout mRLSearch;
    ListView mListView;
    SearchLocAdapter mAdapter;
    ImageView mIVBack;
    EditText mETSearch;
    TextView mTVSearch;
    TextView mTVLoadMore;

    PoiSearch mPoiSearch;
    String searchCity;

    int mPageNum = 0;
    int mPageCount = 10;
    private boolean isShowDownloadProgress;
    private String mUpdateUrl = "http://bjx.aqlinli.com/index.php/Api/Api/update_siji?";

    public static void getNewOrder(Context context) {

        //打开订单页面并刷新
        // 重置按钮
        ((ImageButton) mTabBtnState.findViewById(R.id.btn_tab_bottom_state))
                .setImageResource(R.mipmap.un_state);
        ((ImageButton) mTabBtnOrder.findViewById(R.id.btn_tab_bottom_order))
                .setImageResource(R.mipmap.un_order);
        ((ImageButton) mTabBtnMap.findViewById(R.id.btn_tab_bottom_map))
                .setImageResource(R.mipmap.un_map);
        ((ImageButton) mTabBtnMore.findViewById(R.id.btn_tab_bottom_more))
                .setImageResource(R.mipmap.un_more);
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        if (stateFragment != null) {
            transaction.hide(stateFragment);
        }
        if (orderFragment != null) {
            transaction.hide(orderFragment);
        }
        if (mapFragment != null) {
            transaction.hide(mapFragment);
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment);
        }
        ((ImageButton) mTabBtnOrder.findViewById(R.id.btn_tab_bottom_order))
                .setImageResource(R.mipmap.order);
        //if (orderFragment == null) {
        orderFragment = new OrderFragment();
        transaction.add(R.id.content, orderFragment, orderFragment.getClass().getName());
        //} else {
        transaction.show(orderFragment);
        // }
        transaction.commitAllowingStateLoss();
    }


    @Override
    protected void initView(Bundle savedInstanceState) {

        int max_memory = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getLargeMemoryClass();
        System.out.println(max_memory);

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        mTabBtnState = getViewById(R.id.tab_bottom_state);
        mTabBtnOrder = getViewById(R.id.tab_bottom_order);
        mTabBtnMap = getViewById(R.id.tab_bottom_map);
        mTabBtnMore = getViewById(R.id.tab_bottom_more);


        mRLSearch = getViewById(R.id.main_rl_search);
        mIVBack = getViewById(R.id.searchloc_back);
        mETSearch = getViewById(R.id.searchloc_et_search);
        mTVSearch = getViewById(R.id.searchloc_tv_search);
        mListView = getViewById(R.id.searchloc_lv);
        mTVLoadMore = getViewById(R.id.searchloc_loadmore);

        mAdapter = new SearchLocAdapter(mApp, R.layout.lv_item_searchloc);
        mListView.setAdapter(mAdapter);
        fragmentManager = getFragmentManager();


        //申请app所需权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new PermissionUtils(this).requestLocationSDPermissions(200);
        }

        //判断是否开启了定位服务
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
//                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
                new PermissionUtils(this).requesLocationPermissions();

            } else {
                // 有权限了，去放肆吧。
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();

            // 去设置  EVENT_DISLOG_CONFIRM
            DialogUtil.showNetWorkDialog(this);
        }

        //检测更新
        diyUpdate();

    }

    @Override
    protected void setListener() {
        mTabBtnState.setOnClickListener(this);
        mTabBtnOrder.setOnClickListener(this);
        mTabBtnMap.setOnClickListener(this);
        mTabBtnMore.setOnClickListener(this);

        mIVBack.setOnClickListener(this);
        mTVSearch.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mTVLoadMore.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        UserModel userModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        if (userModel != null) {

            getPayList(userModel.getSj_id()); // 获取计费列表

            /*
            timer.schedule(task, 1000, 60000); // 每分钟刷新定位，给服务器定位信息
            timer.schedule(task, 1000, 600); // 每分钟刷新定位，给服务器定位信息
           */

            //登录并且已经上班开启后台服务
            //开启司机后台服务进程
            if ("2".equals(userModel.getWorkState())) {
                Intent intent = new Intent(this, DriverService.class);
                this.startService(intent);
            }
        }

        setTabSelection(tabPosition);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        List<PoiInfo> mData = getIntent().getParcelableArrayListExtra("list");
        mAdapter.setData(mData);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);// 必须要调用这句
        if (newDriver) {
            newDriver = false;
            tabPosition = 0;
            setTabSelection(-1);
        }

//        LogsUtil.normal("启动主MainActivity");
        if (intent != null) {

            int tabIndex = intent.getIntExtra("tabindex", 0);
            if (tabIndex != 0) {
                if (tabIndex == 20) { //在OrderFragment里面查单 客户取消订单
                    orderInfo = null;
                    tabPosition = 2;
                    setTabSelection(20);
                    //                    LogsUtil.normal("启动主MainActivity20");
                } else {
                    tabPosition = tabIndex;
                    setTabSelection(tabPosition);
                }

            }

            if (intent.hasExtra("orderinfo")) {
                orderInfo = intent.getParcelableExtra("orderinfo"); // 接收的订单

                Gson gson = new Gson();
                String jsonStr = gson.toJson(orderInfo); //将对象转换成Json
                PreferenceUtil.putOrderInfoPreference(MainActivity.this,
                        Constant.CONFIG_INTENT_ORDER_INFO, jsonStr);
            }
            if (intent.hasExtra("endPoiInfo")) {
                poiInfo = intent.getParcelableExtra("endPoiInfo");
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        tabPosition = savedInstanceState.getInt("position");

        setTabSelection(tabPosition);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", tabPosition);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_bottom_state:  // 点击状态tab
                tabPosition = 0;
                setTabSelection(tabPosition);
                break;
            case R.id.tab_bottom_order: //  点击订单tab
                tabPosition = 1;
                setTabSelection(tabPosition);
                break;
            case R.id.tab_bottom_map: // 点击地图tab
                tabPosition = 2;
                setTabSelection(tabPosition);
                break;
            case R.id.tab_bottom_more:  // 点击更多tab
                tabPosition = 3;
                setTabSelection(tabPosition);
                break;

            case R.id.searchloc_tv_search:
                searchCity = SPUtils.get(mApp, "loccity", "中国").toString();
                mPageNum = 0;
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(mPageNum));
                break;
            case R.id.searchloc_loadmore:
                mTVLoadMore.setText("正在加载..");
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(++mPageNum));
                break;
            case R.id.searchloc_back:
                mRLSearch.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        poiInfo = mAdapter.getItem(position);
        if (mapFragment != null) {
            mapFragment.setmResetEndLon(poiInfo.location.longitude);
            mapFragment.setmResetEndLat(poiInfo.location.latitude);
            mRLSearch.setVisibility(View.GONE);
            showToast("成功设置终点位置，已可以开始导航");
        }
//        setResult(200, new Intent().putExtra("endPoiInfo",poiInfo));
//        startActivity(new Intent(mApp, MainActivity.class)
//                .putExtra("tabindex", 2)
//                .putExtra("endPoiInfo", poiInfo));
//        finish();
    }


    public void onEventMainThread(EventBean bean) {

        //取消按钮
        if (bean.getAction().equals(EventBean.EVENT_DISLOG_CANCLE)) {

        }

        //确定去设置按钮
        if (bean.getAction().equals(EventBean.EVENT_DISLOG_CONFIRM)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }

    }


    //软件更新用这个
    private void diyUpdate() {

        isShowDownloadProgress = true;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        Map<String, String> params = new HashMap<String, String>();
//
//        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f");
        params.put("version", AppUpdateUtils.getVersionName(this));

//        params.put("key1", "value2");
//        params.put("key2", "value3");

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(mUpdateUrl)

                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(false)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")

                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(jsonObject.optString("update"))
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.optString("new_version"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.optString("apk_file_url"))

                                    //（必须）更新内容
                                    .setUpdateLog(jsonObject.optString("update_log"))

                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(jsonObject.optString("target_size"))
                                    //是否强制更新，可以不设置
                                    .setConstraint(false);
                            //设置md5，可以不设置
//                                    .setNewMd5(jsonObject.optString("new_md51"))

//                            updateAppBean.setApkFileUrl("http:\\/\\/120.27.233.26\\/TaobaoDemoApp\\/src\\/app\\/app_v1.0.apk");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    /**
                     * 有新版本
                     *
                     * @param updateApp        新版本信息
                     * @param updateAppManager app更新管理器
                     */
                    @Override
                    public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        //强制更新，
                        if (updateApp.isConstraint()) {

                        } else {

                        }
                        //自定义对话框
                        showDiyDialog(updateApp, updateAppManager);
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp(String error) {
                        Toast.makeText(MainActivity.this, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 自定义对话框
     *
     * @param updateApp
     * @param updateAppManager
     */
    private void showDiyDialog(final UpdateAppBean updateApp, final UpdateAppManager updateAppManager) {
        String targetSize = updateApp.getTargetSize();
        String updateLog = updateApp.getUpdateLog();

        String msg = "";

        if (!TextUtils.isEmpty(targetSize)) {
            msg = "新版本大小：" + targetSize + "\n\n";
        }

        if (!TextUtils.isEmpty(updateLog)) {
            msg += updateLog;
        }

        new AlertDialog.Builder(this)
                .setTitle(String.format("是否升级到%s版本？", updateApp.getNewVersion()))
                .setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //显示下载进度
                        if (isShowDownloadProgress) {
                            updateAppManager.download(new DownloadService.DownloadCallback() {
                                @Override
                                public void onStart() {
                                    HProgressDialogUtils.showHorizontalProgressDialog(MainActivity.this, "下载进度", false);
                                }

                                /**
                                 * 进度
                                 *
                                 * @param progress  进度 0.00 -1.00 ，总大小
                                 * @param totalSize 总大小 单位B
                                 */
                                @Override
                                public void onProgress(float progress, long totalSize) {
                                    HProgressDialogUtils.setProgress(Math.round(progress * 100));
                                }

                                /**
                                 *
                                 * @param total 总大小 单位B
                                 */
                                @Override
                                public void setMax(long total) {

                                }


                                @Override
                                public boolean onFinish(File file) {
                                    HProgressDialogUtils.cancel();
                                    return true;
                                }

                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    HProgressDialogUtils.cancel();

                                }
                            });
                        } else {
                            //不显示下载进度
                            updateAppManager.download();
                        }


                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    /*--------------  每60s给服务器发送定位信息 begin---------- */
    private void giveLocation(String uId, double longitude, double latitude) {
        mEngine.pushLocation(uId, longitude, latitude).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    LogsUtil.service("Main servive运行中..刷新订单");

                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                    } else if (status == 201) {
                        showToast("账号不存在");
                    } else if (status == 301) {
                        //showToast("更新司机信息失败");
                    } else if (status == 100) {
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getLocation();
            }
            super.handleMessage(msg);
        }
    };
    Timer timer = new Timer();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    /*--------------  每60s给服务器发送定位信息 end---------- */

    // 启动百度定位
    private void getLocation() {
        locationService = ((App) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            LocationClientOption option = locationService.getDefaultLocationClientOption();
            option.setOpenAutoNotifyMode();
            locationService.setLocationOption(option);

//            locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
    }

    // 获取收费列表
    private void getPayList(String uId) {//
        mEngine.gaveMoneylist(uId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);

                    LogsUtil.net("获取收费列表" + str);

                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        ChargeModel model = GsonUtil.jsonToModelBean(jo.getString("result"));
                        SPUtils.put(mApp, Constants.KEFU, model.getKf_tel());
                        SPUtils.saveObject(mApp, "charge", model);
                    } else if (status == 100) {
                        showToast("获取服务信息失败");
                    } else {
                        showToast("获取服务信息异常");
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

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    private void setTabSelection(int index) {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case -1:
                ((ImageButton) mTabBtnState.findViewById(R.id.btn_tab_bottom_state))
                        .setImageResource(R.mipmap.state);
                if (stateFragment != null) {
                    transaction.remove(stateFragment);
                }
                stateFragment = new StateFragment();
                transaction.add(R.id.content, stateFragment, stateFragment.getClass().getName());
                transaction.show(stateFragment);

                break;

            case 0:
                ((ImageButton) mTabBtnState.findViewById(R.id.btn_tab_bottom_state))
                        .setImageResource(R.mipmap.state);
                if (stateFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    stateFragment = new StateFragment();

                    transaction.add(R.id.content, stateFragment, stateFragment.getClass().getName());
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(stateFragment);
                }
                break;
            case 1:

                ((ImageButton) mTabBtnOrder.findViewById(R.id.btn_tab_bottom_order))
                        .setImageResource(R.mipmap.order);
                //if (orderFragment == null) {
                orderFragment = new OrderFragment();
                transaction.add(R.id.content, orderFragment, orderFragment.getClass().getName());
                // } else {
                transaction.show(orderFragment);
                //  }
                break;

            case 2:
                ((ImageButton) mTabBtnMap.findViewById(R.id.btn_tab_bottom_map))
                        .setImageResource(R.mipmap.map);
                if (mapFragment == null) {
                    mapFragment = new MapFragment();
                    transaction.add(R.id.content, mapFragment, mapFragment.getClass().getName());
                } else {
                    transaction.show(mapFragment);
                }
                break;
            case 20:
                ((ImageButton) mTabBtnMap.findViewById(R.id.btn_tab_bottom_map))
                        .setImageResource(R.mipmap.map);
                if (mapFragment != null) {
                    transaction.remove(mapFragment);
                }
                mapFragment = new MapFragment();
                transaction.add(R.id.content, mapFragment, mapFragment.getClass().getName());

                transaction.show(mapFragment);

                break;
            case 3:
                ((ImageButton) mTabBtnMore.findViewById(R.id.btn_tab_bottom_more))
                        .setImageResource(R.mipmap.more);
                if (moreFragment == null) {
                    moreFragment = new MoreFragment();
                    transaction.add(R.id.content, moreFragment, moreFragment.getClass().getName());
                } else {
                    transaction.show(moreFragment);
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 清除bottombar所有的选中状态。
     */
    private void resetBtn() {
        ((ImageButton) mTabBtnState.findViewById(R.id.btn_tab_bottom_state))
                .setImageResource(R.mipmap.un_state);
        ((ImageButton) mTabBtnOrder.findViewById(R.id.btn_tab_bottom_order))
                .setImageResource(R.mipmap.un_order);
        ((ImageButton) mTabBtnMap.findViewById(R.id.btn_tab_bottom_map))
                .setImageResource(R.mipmap.un_map);
        ((ImageButton) mTabBtnMore.findViewById(R.id.btn_tab_bottom_more))
                .setImageResource(R.mipmap.un_more);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (stateFragment != null) {
            transaction.hide(stateFragment);
        }
        if (orderFragment != null) {
            transaction.hide(orderFragment);
        }
        if (mapFragment != null) {
            transaction.hide(mapFragment);
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment);
        }
    }

    /**
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                locationService.stop();
                mCurretLon = location.getLongitude();
                mCurretLat = location.getLatitude();
                giveLocation(userModel.getSj_id(), location.getLongitude(), location.getLatitude()); // 发送位置信息给服务器
            }
        }
    };

    public OrderList getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderList orderInfo) {
        this.orderInfo = orderInfo;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
//        locationService.unregisterListener(mListener); //注销掉监听
//        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mRLSearch.getVisibility() == View.VISIBLE) {
                    mRLSearch.setVisibility(View.GONE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("确定要退出吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result.getSuggestCityList() == null) {
                if (result.getCurrentPageNum() > 0) {
                    mAdapter.addMoreData(result.getAllPoi());
                } else {
                    mAdapter.setData(result.getAllPoi());
                }
                mPageNum = result.getCurrentPageNum();
                if (result.getCurrentPageNum() < result.getTotalPageNum() - 1) {
                    mTVLoadMore.setVisibility(View.VISIBLE);
                } else {
                    mTVLoadMore.setVisibility(View.GONE);
                }
                mTVLoadMore.setText("加载更多..");
            } else {
                mPageNum = 0;
                searchCity = "中国";
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(mPageNum));
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };


}
