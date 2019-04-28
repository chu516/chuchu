package bjx.com.siji.ui.button;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.engine.Engine;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.DriverService;
import bjx.com.siji.service.LocationService;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
  滑动按钮，两种状态切换
 */
public class SlipButton extends View implements OnTouchListener {
    Timer timer = new Timer();
    private LocationService locationService;
    private double latitude = -1;//纬度
    private double longitude = -1;//经度
    private String province;
    private String city;
    private String district;
    private String sj_id;
    private Engine mEngine = App.getInstance().getEngine();
    ;
    private Context context;
    UserModel currentDriver;
    public boolean workState = false, lastWorkState = false;
    // workState记录当前工作状态,lastWorkState记录切换之前的状态，当状态改变时请求后台，true为上班，false为下班
    private Bitmap bg_on, bg_off;
    private Map<Integer, String> startWorkInfo = new HashMap<Integer, String>();
    //上班定位监听器
    private BDAbstractLocationListener workLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                latitude = bdLocation.getLatitude();
                longitude = bdLocation.getLongitude();
                province = bdLocation.getProvince();
                city = bdLocation.getCity();
                district = bdLocation.getDistrict();
                //发送上班请求
                requestChangeWorkState();
            }
        }
    };


    public SlipButton(Context context) {
        super(context);
        init(context);

    }

    public SlipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        timer.schedule(new NewOrderTask(), 5000, 5000);
        startWorkInfo.put(100, "参数错误");
        startWorkInfo.put(101, "参数验证错误");
        startWorkInfo.put(201, "账号不存在");
        startWorkInfo.put(202, "账号未通过审核，无法上班");
        startWorkInfo.put(203, "账号审核中，无法上班");
        startWorkInfo.put(204, "账号状态异常，无法上班，请与管理员联系");
        startWorkInfo.put(205, "账号已绑定其它设备，当前设备无法上班");
        startWorkInfo.put(400, "缺少参数");
        startWorkInfo.put(401, "当前地区未开通服务");
        startWorkInfo.put(402, "县区不存在");
        startWorkInfo.put(403, "城市不存在");
        startWorkInfo.put(405, "省份不存在");
        startWorkInfo.put(2041, "可用余额不足");

        currentDriver = (UserModel) SPUtils.readObject(context, Constants.USERMODEL);
        if (currentDriver != null) {
            sj_id = currentDriver.getSj_id();
            if ("2".equals(currentDriver.getWorkState())) {
                workState = true;
                lastWorkState = true;
            } else if ("1".equals(currentDriver.getWorkState())) {
                workState = false;
                lastWorkState = false;
            }
        }
        this.context = context;
        bg_on = BitmapFactory.decodeResource(getResources(),
                R.drawable.on);
        bg_off = BitmapFactory.decodeResource(getResources(),
                R.drawable.off);
        setOnTouchListener(this);
        locationService = new LocationService(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();


        if (workState) {
            canvas.drawBitmap(bg_on, matrix, paint);//上班
        } else {
            canvas.drawBitmap(bg_off, matrix, paint);// 下班

        }
    }


    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (bg_on.getWidth() / 2)) {
                workState = false;
            } else {
                workState = true;
            }
        }
        if (lastWorkState != workState) {


            changeWorkState();
        }
        return true;
    }

    //    private void initLocationPermissiion() {
//        PermissionUtils permissionUtils = new PermissionUtils(context);
//        if (permissionUtils.isRequesLocationPermissions()) {
//
//        } else {
//            permissionUtils.requesLocationPermissions();
//        }
//    }

    class NewOrderTask extends TimerTask {
        @Override
        public void run() {
            Integer newOrder = (Integer) SPUtils.get(context, "NEW_ORDER", new Integer(-1));
            if (newOrder != null && newOrder > 0) {
                Message message = new Message();
                message.what = 250;
                message.obj = context;
                MainActivity.mainHandler.sendMessage(message);
                LogsUtil.normal("订单从这里调过来 =NewOrderTask>>MainActivity");
            } else {
                Message message = new Message();
                message.what = -250;
                message.obj = context;
                MainActivity.mainHandler.sendMessage(message);
            }
        }
    }

    private void requestChangeWorkState() {
        mEngine.startWork(sj_id, 2 + "", MD5.getMessageDigest((sj_id + Constants.BASE_KEY + Util.getPhoneSign(context)).getBytes()),
                longitude + "", latitude + "", province, city, district).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    if (status == 200) {
                        invalidate();//重画控件
                        currentDriver.setWorkState("2");
                        SPUtils.saveObject(App.getInstance(), Constants.USERMODEL, currentDriver);
                        Toast.makeText(context, "已上班", Toast.LENGTH_SHORT).show();
                        lastWorkState = workState;

                        //开启司机后台服务进程
                        Intent intent = new Intent(context, DriverService.class);
                        context.startService(intent);
                    } else {
                        String msg = startWorkInfo.get(status);
                        if (msg == null) {
                            msg = "上班失败";
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        workState = !workState;

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    locationService.unregisterListener(workLocationListener);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                locationService.unregisterListener(workLocationListener);
            }
        });
        locationService.stop();
    }

    public void changeWorkState() {

        if (workState) {
            //上班
            locationService.registerListener(workLocationListener);
            locationService.start();
        } else {

            //下班
            mEngine.offWork(sj_id, 1 + "", MD5.getMessageDigest((sj_id + Constants.BASE_KEY + Util.getPhoneSign(context)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        if (status == 200) {
                            invalidate();//重画控件
                            currentDriver.setWorkState("1");
                            SPUtils.saveObject(App.getInstance(), Constants.USERMODEL, currentDriver);
                            Toast.makeText(context, "已下班", Toast.LENGTH_SHORT).show();
                            lastWorkState = workState;
                            timer.cancel();
                            Intent intent = new Intent(context, DriverService.class);
                            context.stopService(intent);

                        } else {
                            String msg = startWorkInfo.get(status);
                            if (msg == null) {
                                msg = "上班失败";
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            workState = !workState;
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