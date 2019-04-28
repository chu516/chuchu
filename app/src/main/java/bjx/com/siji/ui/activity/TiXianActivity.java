package bjx.com.siji.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TiXianActivity extends BaseActivity {

    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVEvent;

    LinearLayout mll;
    TextView mTVMoney;
    TextView mTVTiXMoney1;
    TextView mTVTiXMoney2;
    TextView mTVTiXMoney3;
    TextView mTVTiXMoney4;
    TextView mTVTiXMoney5;
    TextView mTVTiXMoney6;
    EditText mETTiXMoney;
    EditText mETZFB;
    TextView mTVSub;

    int money = 0; // 提现金额
    int moneyIndex = -1;// 选择的金额下标

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tixian);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("提现");
        mTVEvent = getViewById(R.id.event);
        mTVEvent.setText("交易记录");
        mTVEvent.setVisibility(View.GONE);

        mll = getViewById(R.id.tixian_ll);
        mTVMoney = getViewById(R.id.tixian_tv_money);
        mTVTiXMoney1 = getViewById(R.id.tixian_tv_depomoney1);
        mTVTiXMoney2 = getViewById(R.id.tixian_tv_depomoney2);
        mTVTiXMoney3 = getViewById(R.id.tixian_tv_depomoney3);
        mTVTiXMoney4 = getViewById(R.id.tixian_tv_depomoney4);
        mTVTiXMoney5 = getViewById(R.id.tixian_tv_depomoney5);
        mTVTiXMoney6 = getViewById(R.id.tixian_tv_depomoney6);
        mETTiXMoney = getViewById(R.id.tixian_et_depomoney);
        mETZFB = getViewById(R.id.tixian_et_zfb);
        mTVSub = getViewById(R.id.tixian_tv_tixian);
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

        mTVTiXMoney1.setOnClickListener(this);
        mTVTiXMoney2.setOnClickListener(this);
        mTVTiXMoney3.setOnClickListener(this);
        mTVTiXMoney4.setOnClickListener(this);
        mTVTiXMoney5.setOnClickListener(this);
        mTVTiXMoney6.setOnClickListener(this);
        mTVSub.setOnClickListener(this);

        mETTiXMoney.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    selMoney(-1);
                } else {
                    String moneyStr = mETTiXMoney.getText().toString();
                    if(moneyStr != null && !moneyStr.equals("")) {
                        money = Integer.valueOf(moneyStr);
                    }
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userModel != null) {
            if(userModel.getAlipay() != null && !userModel.getAlipay().trim().equals("")) {
                mETZFB.setText(userModel.getAlipay());
                mETZFB.setEnabled(false);
            } else {
                mETZFB.setText("");
                mETZFB.setEnabled(true);
            }

            mEngine.siji_info_refresh(userModel.getSj_id(), MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + Util.getPhoneSign(mApp)).getBytes())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String str = response.body().string();
                        JSONObject jo = new JSONObject(str);
                        int status = jo.getInt("status");
                        String msg = jo.getString("msg");
                        if (status == 200) {
                            UserModel driver= GsonUtil.jsonToUserBean(jo.getString("result"));
                            mTVMoney.setText(driver.getUser_money());
                            if(!driver.getAlipay().trim().equals("")) {
                                mETZFB.setText(userModel.getAlipay());
                                mETZFB.setEnabled(false);
                            } else {
                                mETZFB.setText("");
                                mETZFB.setEnabled(true);
                            }
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

    @Override
    public void onClick(View v) {
        mll.requestFocus();

        switch (v.getId()) {
            case R.id.tixian_tv_depomoney1:
                selMoney(0);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_depomoney2:
                selMoney(1);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_depomoney3:
                selMoney(2);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_depomoney4:
                selMoney(3);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_depomoney5:
                selMoney(4);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_depomoney6:
                selMoney(5);
                mETTiXMoney.setText("");
                break;
            case R.id.tixian_tv_tixian:
                tixian(money, mETZFB.getText().toString().trim());
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

    private void selMoney(int index) {
        unSelMoney(moneyIndex);

        moneyIndex = index;
        switch (index) {
            case 0:
                money = 100;
                mTVTiXMoney1.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney1.setTextColor(Color.WHITE);
                break;
            case 1:
                money = 300;
                mTVTiXMoney2.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney2.setTextColor(Color.WHITE);
                break;
            case 2:
                money = 500;
                mTVTiXMoney3.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney3.setTextColor(Color.WHITE);
                break;
            case 3:
                money = 700;
                mTVTiXMoney4.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney4.setTextColor(Color.WHITE);
                break;
            case 4:
                money = 900;
                mTVTiXMoney5.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney5.setTextColor(Color.WHITE);
                break;
            case 5:
                money = 1000;
                mTVTiXMoney6.setBackgroundResource(R.drawable.bg_blue_smallradius);
                mTVTiXMoney6.setTextColor(Color.WHITE);
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
                mTVTiXMoney1.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney1.setTextColor(Color.BLACK);
                break;
            case 1:
                mTVTiXMoney2.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney2.setTextColor(Color.BLACK);
                break;
            case 2:
                mTVTiXMoney3.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney3.setTextColor(Color.BLACK);
                break;
            case 3:
                mTVTiXMoney4.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney4.setTextColor(Color.BLACK);
                break;
            case 4:
                mTVTiXMoney5.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney5.setTextColor(Color.BLACK);
                break;
            case 5:
                mTVTiXMoney6.setBackgroundResource(R.drawable.bg_white_grey);
                mTVTiXMoney6.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private void tixian(final int money, final String alipay) {
        if(money == 0) {
            showToast("金额不能为少于1元");
            return;
        }

        if (userModel == null) {
            return;
        }
        if (alipay.trim().equals("")) {
            showToast("请设置支付宝帐号");
            return;
        }

        String dialogMessage = "";
        if (userModel.getAlipay().equals("")) {
            dialogMessage = "确定要提现"+String.valueOf(money)+"元吗？\n(支付宝帐号一旦设定不可变更)";
        } else {
            dialogMessage = "确定要提现"+String.valueOf(money)+"元吗？";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage(dialogMessage)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEngine.tixian(userModel.getSj_id(), String.valueOf(money), alipay, MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + String.valueOf(money)).getBytes())).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String str = response.body().string();
                                    JSONObject jo = new JSONObject(str);
                                    int status = jo.getInt("status");
                                    String msg = jo.getString("msg");
                                    if (status == 200) {
                                        showToast("提现已申请");
                                        finish();
                                    } else if (status == 100) {
                                        showToast("提现失败");
                                    } else if (status == 400) {
                                        showToast("提现金额少于限定额度，无法提现");
                                    } else if (status == 401) {
                                        showToast("余额不足");
                                    } else {
                                        showToast("提现异常");
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
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }
}
