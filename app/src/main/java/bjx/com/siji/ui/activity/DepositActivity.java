package bjx.com.siji.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.model.WXPayModel;
import bjx.com.siji.pay.PayResult;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 充值界面
 * @author wang
 * @time 2018/10/22 12:50
 * @method
 */
public class DepositActivity extends BaseActivity {

    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVEvent;

    LinearLayout mll;
    TextView mTVMoney;
    TextView mTVDepoMoney1;
    TextView mTVDepoMoney2;
    TextView mTVDepoMoney3;
    TextView mTVDepoMoney4;
    TextView mTVDepoMoney5;
    TextView mTVDepoMoney6;
    EditText mETDepoMoney;
    TextView mTVSub;

    int money = 0; // 充值金额
    int moneyIndex = -1;// 选择的金额下标
    int mwhich = 0; // 支付选择下标 0微信 1支付宝
    private IWXAPI api;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_depoist);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("充值");
        mTVEvent = getViewById(R.id.event);
        mTVEvent.setText("交易记录");
        mTVEvent.setVisibility(View.GONE);

        mll = getViewById(R.id.deposit_ll);
        mTVMoney = getViewById(R.id.deposit_tv_money);
        mTVDepoMoney1 = getViewById(R.id.deposit_tv_depomoney1);
        mTVDepoMoney2 = getViewById(R.id.deposit_tv_depomoney2);
        mTVDepoMoney3 = getViewById(R.id.deposit_tv_depomoney3);
        mTVDepoMoney4 = getViewById(R.id.deposit_tv_depomoney4);
        mTVDepoMoney5 = getViewById(R.id.deposit_tv_depomoney5);
        mTVDepoMoney6 = getViewById(R.id.deposit_tv_depomoney6);
        mETDepoMoney = getViewById(R.id.deposit_et_depomoney);
        mTVSub = getViewById(R.id.deposit_tv_deposit);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
//        mTVEvent.setOnClickListener(this);

        mll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mll.requestFocus();
                return false;
            }
        });
        mTVDepoMoney1.setOnClickListener(this);
        mTVDepoMoney2.setOnClickListener(this);
        mTVDepoMoney3.setOnClickListener(this);
        mTVDepoMoney4.setOnClickListener(this);
        mTVDepoMoney5.setOnClickListener(this);
        mTVDepoMoney6.setOnClickListener(this);
        mTVSub.setOnClickListener(this);

        mETDepoMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    selMoney(-1);
                } else {
                    String moneyStr = mETDepoMoney.getText().toString();
                    if(moneyStr != null && !moneyStr.equals("")) {
                        money = Integer.valueOf(moneyStr);
                    }
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(mApp, Constants.WX_APPID);
    }

    @Override
    public void onClick(View v) {
        mll.requestFocus();

        switch (v.getId()) {
            case R.id.deposit_tv_depomoney1:
                selMoney(0);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_depomoney2:
                selMoney(1);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_depomoney3:
                selMoney(2);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_depomoney4:
                selMoney(3);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_depomoney5:
                selMoney(4);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_depomoney6:
                selMoney(5);
                mETDepoMoney.setText("");
                break;
            case R.id.deposit_tv_deposit: // 立即充值
                new AlertDialog.Builder(this)
                        .setTitle("请选择支付方式" )
                        .setSingleChoiceItems(new  String[] {"微信", "支付宝"},  0,
                                new  DialogInterface.OnClickListener() {
                                    public   void  onClick(DialogInterface dialog,  int  which) {
                                        mwhich = which;
                                    }
                                }
                        )
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mwhich == 1)
                                    deposit(1, money);
                                else if (mwhich == 0)
                                    deposit(0, money);
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
            case R.id.event: // 交易记录
//                startActivity(new Intent(mApp, TradeHisActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            case R.id.back:
                finish();
                break;
            default:break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userModel != null) {
            mEngine.siji_info_refresh(userModel.getSj_id(), MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + Util.getPhoneSign(mApp)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        String msg = jo.getString("msg");
                        if (status == 200) {
                            UserModel driver = GsonUtil.jsonToUserBean(jo.getString("result"));
                            mTVMoney.setText(driver.getUser_money());
                        } else {
                            showToast("获取信息失败");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showNetErrToast();
                }
            });
        }
    }

    private void selMoney(int index) {
        unSelMoney(moneyIndex);

        moneyIndex = index;
        switch (index) {
            case 0:
                money = 100;
                mTVDepoMoney1.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney1.setTextColor(Color.WHITE);
                break;
            case 1:
                money = 300;
                mTVDepoMoney2.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney2.setTextColor(Color.WHITE);
                break;
            case 2:
                money = 500;
                mTVDepoMoney3.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney3.setTextColor(Color.WHITE);
                break;
            case 3:
                money = 700;
                mTVDepoMoney4.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney4.setTextColor(Color.WHITE);
                break;
            case 4:
                money = 900;
                mTVDepoMoney5.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney5.setTextColor(Color.WHITE);
                break;
            case 5:
                money = 1000;
                mTVDepoMoney6.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVDepoMoney6.setTextColor(Color.WHITE);
                break;
            case -1:
                money = 0;
                break;
            default:
                break;
        }
    }

    private void unSelMoney(int index) {
        switch (index) {
            case 0:
                mTVDepoMoney1.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney1.setTextColor(Color.BLACK);
                break;
            case 1:
                mTVDepoMoney2.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney2.setTextColor(Color.BLACK);
                break;
            case 2:
                mTVDepoMoney3.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney3.setTextColor(Color.BLACK);
                break;
            case 3:
                mTVDepoMoney4.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney4.setTextColor(Color.BLACK);
                break;
            case 4:
                mTVDepoMoney5.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney5.setTextColor(Color.BLACK);
                break;
            case 5:
                mTVDepoMoney6.setBackgroundResource(R.drawable.bg_white_grey);
                mTVDepoMoney6.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private void deposit(int paytype, int money) {
        if(money == 0) {
            showToast("金额不能为少于1元");
            return;
        }
//        if (paytype == 0) {
//            showToast("请设置支付方式");
//            return;
//        }

        if(paytype == 0) { // 微信
            mEngine.reqPayForWX(userModel.getSj_id(), String.valueOf(money), MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + String.valueOf(money)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        String msg = jo.getString("msg");
                        if (status == 200) {
                            payWX(GsonUtil.jsonToWXPayBean(jo.getString("result")));
                        } else if (status == 500) {
                            showToast("创建订单失败");
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
                    showNetErrToast();
                }
            });
        } else if(paytype == 1) { // 支付宝
            mEngine.reqPayForZFB(userModel.getSj_id(), String.valueOf(money), MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + String.valueOf(money)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        String msg = jo.getString("msg");
                        if (status == 200) {
                            payZFB(jo.getString("result"));
                        } else if (status == 500) {
                            showToast("订单创建失败,请稍候再试");
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
                    showNetErrToast();
                }
            });
        }
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
        req.extData = model.getOrder_id();
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
                PayTask alipay = new PayTask(DepositActivity.this);
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
        mEngine.reqPayQueryForZFB(userModel.getSj_id(), tradeNo, MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + tradeNo).getBytes())).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        showToast("支付成功");
                    } else if (status == 500 || status == 503) {
                        showToast("订单支付失败");
                    } else if (status == 501 || status == 502) {
                        if (queryCi < 3) {
                            queryCi++;
                            queryForZFB(tradeNo);
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
                showToast("实际到账可能会有延迟");
                queryCi = 0;
            }
        });
    }
}
