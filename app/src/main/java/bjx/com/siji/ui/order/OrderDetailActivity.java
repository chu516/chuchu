package bjx.com.siji.ui.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.model.WXPayModel;
import bjx.com.siji.pay.PayResult;
import bjx.com.siji.ui.activity.BaseActivity;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.ui.activity.WebViewActivity;
import bjx.com.siji.utils.DateUtils;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jqc on 2017/11/24.
 */

public class OrderDetailActivity extends BaseActivity {

    public static OrderDetailActivity mInstant;

    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVEvent;

    TextView mTVOrderTn;
    TextView mTVSTime;
    TextView mTVETime;
    TextView mTVSLoc;
    TextView mTVELoc;
    TextView mTVXSTime;
    TextView mTVXSDistance;
    TextView mTVWTime;
    TextView mTVWPrice;
    TextView mTVSafe;
    TextView mTVPrice;
    TextView mTVStatus;
    TextView mTVPay;
    TextView mTVComment;

    int mwhich = 0; // 支付选择下标 2微信 1支付宝 0余额
    private IWXAPI api;

    String orderid;
    Boolean isPush = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        // 测试用，使用沙箱支付宝
      //  EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);

        setContentView(R.layout.activity_orderdetail);
        mInstant = this;
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("订单详情");
        mTVEvent = getViewById(R.id.event);
        mTVEvent.setText("");

        mTVOrderTn = getViewById(R.id.orderdetail_orderid);
        mTVSTime = getViewById(R.id.orderdetail_stime);
        mTVETime = getViewById(R.id.orderdetail_etime);
        mTVSLoc = getViewById(R.id.orderdetail_sloc);
        mTVELoc = getViewById(R.id.orderdetail_eloc);
        mTVXSTime = getViewById(R.id.orderdetail_time);
        mTVXSDistance = getViewById(R.id.orderdetail_distance);
        mTVWTime = getViewById(R.id.orderdetail_waittime);
        mTVWPrice = getViewById(R.id.orderdetail_waitprice);
        mTVSafe = getViewById(R.id.orderdetail_safe);
        mTVPrice = getViewById(R.id.orderdetail_price);
        mTVStatus = getViewById(R.id.orderdetail_status);
        mTVPay = getViewById(R.id.orderdetail_pay);
        mTVComment = getViewById(R.id.orderdetail_comment);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
//        mTVEvent.setOnClickListener(this);
        mTVPay.setOnClickListener(this);
        mTVComment.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        String customContentString = getIntent().getStringExtra("custom_content");
        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("order_id")) {
                    orderid = customJson.getString("order_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            orderid = getIntent().getStringExtra("orderid");
        }
        isPush = getIntent().getBooleanExtra("ispush", false);

        UserModel uModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);

//        UserModel currentDriver = (UserModel) SPUtils.readObject(this, Constants.USERMODEL);

        if (uModel == null)
            return;
        String key = MD5.getMessageDigest((orderid + Constants.BASE_KEY + uModel.getSj_id()).getBytes());

        LogsUtil.order("请求订单详情"+"orderid"+orderid+"uModel.getSj_id()"+uModel.getSj_id()+"key"+key);

        mEngine.getOrderDetailInfo(uModel.getSj_id(),orderid, "1",key).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    LogsUtil.order("订单详情"+response);

                     String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
//                    String msg = jo.getString("msg");

                    LogsUtil.order("订单详情"+str);
                    if (status == 200) {
                        JSONObject jsonObject = jo.getJSONObject("result");
                        mTVOrderTn.setText("订单号：" + jsonObject.getString("order_sn"));
                        mTVSTime.setText("订单开始地址：" + jsonObject.getString("address_st"));
                        mTVETime.setText("订单结束地址：" + jsonObject.getString("address_end"));
                        mTVSLoc.setText("订单开始时间：" + DateUtils.timedate(jsonObject.getString("st_time")));
                        mTVELoc.setText("订单结束时间：" + DateUtils.timedate(jsonObject.getString("end_time")));
                        mTVXSTime.setText("行驶用时：" + jsonObject.getString("travel_time"));
                        mTVXSDistance.setText("行驶里程：" + jsonObject.getString("mileage"));
                        mTVWTime.setText("等待用时：" + jsonObject.getString("wait_time"));
                        mTVWPrice.setText("等待费用：" + jsonObject.getString("wait_price"));
                        mTVSafe.setText("保险费用：" + jsonObject.getString("safe_kh"));
                        mTVPrice.setText("订单价：" + jsonObject.getString("total_price"));
                        int orderStatus = jsonObject.getInt("order_status");
                        String orderStatusStr = "";
                        if (orderStatus == 0) {
                            orderStatusStr = "派发中";
                            mTVEvent.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 1) {
                            orderStatusStr = "已接单";
                            mTVEvent.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 2) {
                            orderStatusStr = "已取消";
                        } else if (orderStatus == 3) {
                            orderStatusStr = "进行中";
                        } else if (orderStatus == 4) {
                            orderStatusStr = "已完成";
                            if (jsonObject.getString("pay_status").equals("0")) {
                                mTVPay.setVisibility(View.GONE);
                            }
                        } else if (orderStatus == 9) {
                            orderStatusStr = "待派发";
                            mTVEvent.setVisibility(View.VISIBLE);
                        }
                        int is_comment = jsonObject.getInt("is_comment");
                        if (is_comment == 0) {
                            mTVComment.setVisibility(View.GONE);
                        } else if (is_comment == 1) {
                            mTVComment.setText("添加评价");
                            mTVComment.setVisibility(View.VISIBLE);
                        } else if (is_comment == 2) {
                            mTVComment.setText("查看评价");
                            mTVComment.setVisibility(View.VISIBLE);
                        }
                        mTVStatus.setText("订单状态：" + orderStatusStr);
                    } else if (status == 100) {
                        showToast("获取订单失败");
                    } else if (status == 201) {
                        showToast("用户不存在");
                    } else if (status == 301) {
                        showToast("订单不存在");
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

        api = WXAPIFactory.createWXAPI(mApp, Constants.WX_APPID);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UserModel uModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        if (uModel == null)
            return;
        orderid = intent.getStringExtra("orderid");
        isPush = intent.getBooleanExtra("ispush", false);
        String key = MD5.getMessageDigest((orderid + Constants.BASE_KEY + uModel.getSj_id()).getBytes());
        mEngine.getOrderInfo(orderid, uModel.getSj_id(), key).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        JSONObject jsonObject = jo.getJSONObject("result");
                        mTVOrderTn.setText("订单号：" + jsonObject.getString("order_sn"));
                        mTVSTime.setText("订单开始地址：" + jsonObject.getString("address_st"));
                        mTVETime.setText("订单结束地址：" + jsonObject.getString("address_end"));
                        mTVSLoc.setText("订单开始时间：" + DateUtils.timedate(jsonObject.getString("st_time")));
                        mTVELoc.setText("订单结束时间：" + DateUtils.timedate(jsonObject.getString("end_time")));
                        mTVXSTime.setText("行驶用时：" + jsonObject.getString("travel_time"));
                        mTVXSDistance.setText("行驶里程：" + jsonObject.getString("mileage"));
                        mTVWTime.setText("等待用时：" + jsonObject.getString("wait_time"));
                        mTVWPrice.setText("等待费用：" + jsonObject.getString("wait_price"));
                        mTVSafe.setText("保险费用：" + jsonObject.getString("safe_kh"));
                        mTVPrice.setText("订单价：" + jsonObject.getString("total_price"));
                        int orderStatus = jsonObject.getInt("order_status");
                        String orderStatusStr = "";
                        if (orderStatus == 0) {
                            orderStatusStr = "派发中";
                            mTVEvent.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 1) {
                            orderStatusStr = "已接单";
                            mTVEvent.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 2) {
                            orderStatusStr = "已取消";
                        } else if (orderStatus == 3) {
                            orderStatusStr = "进行中";
                        } else if (orderStatus == 4) {
                            orderStatusStr = "已完成";
                            if (jsonObject.getString("pay_status").equals("0")) {
                                mTVPay.setVisibility(View.GONE);
                            }
                        } else if (orderStatus == 9) {
                            orderStatusStr = "待派发";
                            mTVEvent.setVisibility(View.VISIBLE);
                        }
                        int is_comment = jsonObject.getInt("is_comment");
                        if (is_comment == 0) {
                            mTVComment.setVisibility(View.GONE);
                        } else if (is_comment == 1) {
                            mTVComment.setText("添加评价");
                            mTVComment.setVisibility(View.VISIBLE);
                        } else if (is_comment == 2) {
                            mTVComment.setText("查看评价");
                            mTVComment.setVisibility(View.VISIBLE);
                        }
                        mTVStatus.setText("订单状态：" + orderStatusStr);
                    } else if (status == 100) {
                        showToast("获取订单失败");
                    } else if (status == 201) {
                        showToast("用户不存在");
                    } else if (status == 301) {
                        showToast("订单不存在");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event:
                cancelOrder();
                break;
            case R.id.orderdetail_comment:
                String mParam = "";
                if (userModel != null) {
                    mParam = "user_id=" + userModel.getSj_id() + "&order_id=" + orderid;
                }
                startActivity(new Intent(mApp, WebViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("title", "评价")
                        .putExtra("url", Constants.COMMENT_URL)
                        .putExtra("param", mParam));
                break;
            case R.id.back:
                if (isPush) {
                    startActivity(new Intent(mApp, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME));
                }
                finish();
                break;
            case R.id.orderdetail_pay:
                new  AlertDialog.Builder(this)
                        .setTitle("请选择支付方式" )
                        .setSingleChoiceItems(new  String[] {"余额", "支付宝", "微信"}, 0,
                                new  DialogInterface.OnClickListener() {
                                    public   void  onClick(DialogInterface dialog,  int  which) {
                                        mwhich = which;
                                    }
                                }
                        )
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mwhich == 0)
                                    pay(1);
                                else if (mwhich == 1)
                                    pay(2);
                                else if (mwhich == 2)
                                    pay(3);
                                dialog.dismiss();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mwhich = 0;
                            }
                        })
                        .show();
                break;
            default:break;
        }
    }

    private void cancelOrder() {
        mEngine.cancelOrder(orderid, userModel.getSj_id(), MD5.getMessageDigest((orderid + Constants.BASE_KEY + userModel.getSj_id()).getBytes())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        String mParam = "";
                        if (userModel != null) {
                            mParam = "user_id=" + userModel.getSj_id() + "&order_list=" + orderid + "&fid=" + SPUtils.get(mApp, "fid", "-1");
                        }
                        startActivity(new Intent(mApp, WebViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("title", "取消原因")
                                .putExtra("url", Constants.CANCEL_URL)
                                .putExtra("param", mParam));
                        finish();
                    } else if (status == 201) {
                        showToast("用户不存在");
                    } else if (status == 301) {
                        showToast("订单不存在");
                    } else if (status == 401) {
                        showToast("当前订单不能取消");
                    } else if (status == 100) {
                        showToast("取消失败");
                    } else {
                        showToast("取消异常");
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

    private void pay(final int paytype) {

            mEngine.reqOrderPay(userModel.getSj_id(), orderid, String.valueOf(paytype), MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + String.valueOf(orderid)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        String msg = jo.getString("msg");
                        if (status == 200) {
                            if (paytype == 1) {
                                showToast("支付成功");
                                String mParam="";
                                if (userModel != null) {
                                    mParam = "user_id=" + userModel.getSj_id() + "&order_id=" + orderid;
                                }
                                startActivity(new Intent(mApp, WebViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                        .putExtra("title", "评价")
                                        .putExtra("url", Constants.COMMENT_URL)
                                        .putExtra("param", mParam)
                                        .putExtra("backToMain", "1"));
                                finish();

                            } else if (paytype == 2) {
                                payZFB(jo.getString("result"));
                            } else if (paytype == 3) {
                                payWX(GsonUtil.jsonToWXPayBean(jo.getString("result")));
                            }
                        } else if (status == 201) {
                            showToast("用户不存在");
                        } else if (status == 301) {
                            showToast("订单已结算");
                        } else if (status == 302) {
                            showToast("余额不足");
                        } else if (status == 303 || status == 304 || status == 305) {
                            showToast("订单支付失败");
                        } else {
                            showToast("订单支付异常");
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
     * 微信支付业务
     */
    private void payWX(WXPayModel model) {
        PayReq req = new PayReq();
        req.appId = model.getAppid();
        req.partnerId = model.getPartnerid();
        req.prepayId = model.getPrepayid();
        req.nonceStr = model.getNoncestr();
        req.timeStamp = model.getTimestamp();
        req.packageValue = "Sign=WXPay";
        req.sign = model.getSign();
        req.extData = "pay:" + model.getOrder_id();
        api.sendReq(req);
    }

    /**
     * 支付宝支付业务
     */
    private void payZFB(final String orderInfo) {
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(Constants.ALI_APPID, true);
//
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCLXziM483ep0OEan8v/3nK6iuFSqkE0wz8zb6WeGRtQTt0qZA8q+wM1a2u8C9Eii4M7jx2fKDcYVvVIT3YZgyPJjfOc+GL3I7HMfAACwJiCCM/1EmDJ7xF+WcnqJtz/3ceso4+nr59iDMZC9aSFYGFxVGILf62fqYHK5JeJuCmkOaargzFGNSl93NDifdIAJqLWsYrQt3hK69HoDgg97pjQLK5uLv67soGeIPYBX+neHMnvsvktKkNLd90HoxMKFx0mUx0TrDfg8857NXRnTAq638zBS7E6uKTeJaWoG9PwiB/sLobLK2+oHXFHGj5QeE0JBfIpB6jpFFlW5ACrEgfAgMBAAECggEAFkSNfX08PFHPKM33Kk0QQYpuj6phHOM3lQCubc4ohYhBnp8k63ywh1Bwop2/f5zwRKKfyHKbJPrtOD1Ka1PKt+hsTDEZnUYNDeh9pxQbOlyAfUdGw4zKuQdjIuP+imcLujzfG0QzvUzQlTTeAJMtEZl4MGDg7HmJv2WHykE97X7G2e8E8AYkTg+C1hZZmeKJyM0CSzV57ScS0l1qPii+rd3xIYOHobvRJeVcvWp/kHBOxxDNGJ5U4HlO55WpHHmvWCOhn0++ioCMddWdRJ4uZXPIeoYSIyvRlIcOPt5mTA+K30Mq7yKxGj/Re25yP3SuHRGRaQqiKc14fNatzIhb2QKBgQDGnuCfYYBP/y9oLHUxgDKL6gf2MoUqezG1tuvDr/Hr/7wngtN6h7lVOzc1oSKj4ZHTtiDiEp8gdKFMrD+anN8hUiZuLdvcCLRPZuhCPTt7tERU24IBXYhL4qUealIBhw2vYSSEpwDX8p9QH8+qkIFeZQPLRZVble9Te/+E3FUWNQKBgQCzopGUoLcL2Lvxn+oqn+0Q3wMVZmXnKjMvV1fOAbWFd+rDUAMtUo8i2YVBugWf7LpqKVU/n+GFAbjQ26kK6Yb+BIY8vtktJBcJVm02pTD0TrMll2ICUdjaHHUHz0ngm3RjdRglpxUE1lLcRQxBrOqGI+WWHIUJ5QJdOoZAH9ufgwKBgQClMgdJCGMcJfZcAn4GicWAsUml7ybqykPyCw3UWEYyLyVjB2C1DKWMyg+FP1v8SeRr3ZND9JD9AYQWrTzJb6f1osnr4aB3RZYSZFjQnvUWUjxzB479a7msw/jpbyx9sMIlF/qzieMduOHgVlT2W/H9fK7HJjyvS+kd2ogPDOKH8QKBgDwN/4mm6UtGnB69jbXOXZ0kUY2X/D16OD59ftdabus+GfhOCaHfVgcanZhJrpO2uuO/CccJloW2+6inpy4EwKKvtIkebNDQnfr7L+vy8hjbLr1EjPmlj0/r75w6ysISdqjBft8nqrlBRdZqJxzVyFosKhczVa7nJxEv/a32eKAtAoGBALyLaUKcj2mBICPhAK3SpT6ff445Mdhoicwbg2rDHi2CKcYh6wdnmu6v9SnZhok9jWAoNM0bAp6qK9Mbko8DnvcsDh2zptwqKe/3A9N3I6gAT0F0yM1HAnAzlC8gxm5VoBPwnKV5ZjlMtU9EgXvF5tXPz9qsgnz6GUgYLdY4m6Nw";
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, true);
//        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderDetailActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        String out_trade_no = "";
                        try {
                            JSONObject jsonObject = new JSONObject(resultInfo);
                            out_trade_no = jsonObject.getJSONObject("alipay_trade_app_pay_response").getString("out_trade_no");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        queryForZFB(out_trade_no);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    int queryCi = 0; // 异常订单查询3次
    private void queryForZFB(final String tradeNo) {
        mEngine.reqOrderPayQueryForZFB(userModel.getSj_id(), tradeNo, MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + tradeNo).getBytes())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        showToast("支付成功");
                        finish();
                        String mParam="";
                        if (userModel != null) {
                            mParam = "user_id=" + userModel.getSj_id() + "&order_id=" + orderid;
                        }
                        startActivity(new Intent(mApp, WebViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("title", "评价")
                                .putExtra("url", Constants.COMMENT_URL)
                                .putExtra("param", mParam)
                                .putExtra("backToMain", "1"));
                    } else if (status == 503) {
                        showToast("订单支付失败");
                    } else if (status == 501) {
                        if (queryCi < 3) {
                            queryCi++;
                            queryForZFB(tradeNo);
                        } else {
                            showToast("支付出现问题，请联系客服");
                        }
                    } else if (status == 100) {
                        showToast("请求失败");
                    } else {
                        showToast("请求异常");
                        queryCi = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                queryCi = 0;
            }
        });
    }

}
