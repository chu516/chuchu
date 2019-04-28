package bjx.com.siji.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.DateUtils;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/12/9.
 */

public class TallyOrderActivity extends BaseActivity {
    private TextView order_sn, add_time, mobile, uname;
    private TextView start_addre, end_addre, start_time, end_time;
    private TextView wait_time, wait_money, drive_time, drive_keli;
    private TextView drive_money, kuhu_bx, total_money;

    private Button now_money, pingtai;

    OrderList mOrder;
    OrderList orderInfo;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tally_order);
        addView();
        doDate();
    }

    private void doDate() {
        order_sn.setText("");
        add_time.setText("");
        uname.setText("");
        mobile.setText("");
        start_addre.setText("");
        end_addre.setText("");
        start_time.setText("");
        end_time.setText("");

        wait_time.setText("");
        wait_money.setText("");
        drive_time.setText("");
        drive_keli.setText("");

        drive_money.setText("");
        kuhu_bx.setText("");
        total_money.setText("");

        now_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tijiao("1");
            }
        });
        pingtai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tijiao("2");
            }
        });
    }

    private void tijiao(String paytype) {

        LogsUtil.order("平台结算数据" + userModel.getSj_id()
                + "--mOrderInfo.getOrder_id()" + mOrder.getOrder_id()
                + "--paytype" + paytype);

        mEngine.payMoneyWay(userModel.getSj_id(), mOrder.getOrder_id(),
                paytype, MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + mOrder.getOrder_id() + paytype).
                        getBytes()))

                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            LogsUtil.order("后台结算成功sucess 返回数据 str" + str);

                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            if (status == 200) {


                                showToast("结算成功");
                                finish();


                            } else if (status == 100) {
                                showToast("结算失败");
                            } else if (status == 101) {
                                showToast("结算失败");
                            } else if (status == 202) {
                                showToast("订单不存在");
                            } else if (status == 203) {
                                showToast("订单异常");
                            } else {
                                showToast("结算异常");
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

    private void addView() {
        order_sn = (TextView) findViewById(R.id.order_sn);
        add_time = (TextView) findViewById(R.id.add_time);
        uname = (TextView) findViewById(R.id.endorder_uname);
        mobile = (TextView) findViewById(R.id.mobile);
        start_addre = (TextView) findViewById(R.id.start_addre);
        end_addre = (TextView) findViewById(R.id.end_addre);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);

        wait_time = (TextView) findViewById(R.id.wait_time);
        wait_money = (TextView) findViewById(R.id.wait_money);
        drive_time = (TextView) findViewById(R.id.drive_time);
        drive_keli = (TextView) findViewById(R.id.drive_keli);

        drive_money = (TextView) findViewById(R.id.drive_money);
        kuhu_bx = (TextView) findViewById(R.id.kuhu_bx);
        total_money = (TextView) findViewById(R.id.total_money);

        now_money = (Button) findViewById(R.id.now_money);
        pingtai = (Button) findViewById(R.id.pingtai);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mOrder = getIntent().getParcelableExtra("orderinfo");
        getOrderInfo();
    }

    private void getOrderInfo() {
        UserModel userModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        //订单详情
        mEngine.getOrderInfo(userModel.getSj_id(), mOrder.getOrder_id(),
                MD5.getMessageDigest((mOrder.getOrder_id() + Constants.BASE_KEY + userModel.getSj_id()).getBytes())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();

                    LogsUtil.order("TallyOrderActivity 订单详情" + str);

                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {
                        orderInfo = GsonUtil.jsonOrderBean(jo.getString("result"));

                         order_sn.setText(orderInfo.getOrder_sn());
                        add_time.setText(DateUtils.timet(orderInfo.getAdd_time()));
                        uname.setText(orderInfo.getUse_name());
                        mobile.setText(orderInfo.getUse_mobile());
                        start_addre.setText(orderInfo.getAddress_st());
                        end_addre.setText(orderInfo.getAddress_end());
                        start_time.setText(DateUtils.timet(orderInfo.getSt_time()));
                        end_time.setText(DateUtils.timet(orderInfo.getEnd_time()));
                        wait_time.setText(orderInfo.getWait_time() + "秒");
                        wait_money.setText(orderInfo.getWait_price() + "元");
                        drive_time.setText(orderInfo.getTravel_time() + "秒");
                        DecimalFormat df = new DecimalFormat("#0.0");
                        double mileage = Double.valueOf(orderInfo.getMileage()) / 1000;
                        drive_keli.setText(df.format(mileage) + "公里");
                        drive_money.setText(orderInfo.getMileage_price() + "元");
                        kuhu_bx.setText(orderInfo.getSafe_kh() + "元");

                        if (orderInfo.getNegotiate() != null) {
                            if (new Double(orderInfo.getNegotiate()) != 0.0) {
                                total_money.setText(orderInfo.getNegotiate() + "元");
                            } else {
                                total_money.setText(orderInfo.getTotal_price() + "元");
                            }
                        } else {
                            total_money.setText(orderInfo.getTotal_price() + "元");
                        }
                        if (orderInfo.getOrder_system().equals("1") || orderInfo.getOrder_system().equals("2") || orderInfo.getOrder_system().equals("3")) {
                            pingtai.setVisibility(View.VISIBLE);
                        } else {
                            pingtai.setVisibility(View.GONE);
                        }

                    } else if (status == 201) {
                        showToast("账号不存在");
                    } else if (status == 301) {
                        showToast("订单不存在");
                    } else if (status == 1001) {
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
}
