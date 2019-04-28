package bjx.com.siji.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.ui.activity.DepositActivity;
import bjx.com.siji.ui.activity.NoticeActivity;
import bjx.com.siji.ui.activity.SalaryDetailActivity;
import bjx.com.siji.ui.activity.TiXianActivity;
import bjx.com.siji.ui.activity.WorkInfoActivity;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;

import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Administrator on 2017/11/24.
 */

public class StateFragment extends BaseFragment {
    public boolean newDriver;
    ImageView refreshIv;
    RelativeLayout mRLDeposit;
    RelativeLayout mRLTiXian;

    @Override
    public void onResume() {
        super.onResume();
        initData(mContentView, null);
        if (newDriver) {
            newDriver = false;
            bjx.com.siji.ui.button.SlipButton slipBtn = (bjx.com.siji.ui.button.SlipButton) mContentView.findViewById(R.id.on);
            slipBtn.workState = false;
            slipBtn.lastWorkState = false;
            slipBtn.invalidate();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.fragment_state, null);
        LinearLayout ll = (LinearLayout) mContentView.findViewById(R.id.my_work_info);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.my_work_info:
                        Intent intent = new Intent(mActivity, WorkInfoActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        LinearLayout sd = (LinearLayout) mContentView.findViewById(R.id.salary_detail);
        sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SalaryDetailActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
            }
        });

        mContentView.findViewById(R.id.new_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, NoticeActivity.class);
                startActivity(intent);
            }
        });

        mContentView.findViewById(R.id.account_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SalaryDetailActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
            }
        });
        mContentView.findViewById(R.id.today_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SalaryDetailActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });

        mContentView.findViewById(R.id.month_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, SalaryDetailActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
            }
        });


        refreshIv = (ImageView) mContentView.findViewById(R.id.refresh);
        final View context = mContentView;
        refreshIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** 设置旋转动画 */
                final RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);//设置动画持续时间
                refreshIv.setAnimation(animation);
                view.startAnimation(animation);
                //重新获取司机信息
                UserModel currentDriver = (UserModel) SPUtils.readObject(mActivity, Constants.USERMODEL);
                String sj_id = currentDriver.getSj_id();
                final String currentWorkState = currentDriver.getWorkState();

                final String head_pic = currentDriver.getHead_pic();
                mEngine.siji_info_refresh(sj_id, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + Util.getPhoneSign(mApp)).getBytes()))
                        .enqueue(new Callback<ResponseBody>() {
                                     @Override
                                     public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                         try {
                                             String str = response.body().string();

                                             LogsUtil.net(str);
                                             JSONObject jo = new JSONObject(str);
                                             int status = jo.getInt("status");
                                             String msg = jo.getString("msg");
                                             if (status == 200) {
                                                 UserModel driver = GsonUtil.jsonToUserBean(jo.getString("result"));
                                                 driver.setWorkState(currentWorkState);
                                                 driver.setHead_pic(head_pic);
                                                 SPUtils.saveObject(mApp, Constants.USERMODEL, driver);
                                                 initData(mContentView, true);
                                             }

                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<ResponseBody> call, Throwable t) {

                                     }

                                 }
                        );
            }
        });

        mRLDeposit = getViewById(R.id.status_deposit);
        mRLTiXian = getViewById(R.id.status_tixian);
        //初始化司机信息
        initData(mContentView, null);

    }

    private void initData(View view, Boolean refresh) {
        UserModel currentDriver = (UserModel) SPUtils.readObject(mActivity, Constants.USERMODEL);
        if (currentDriver != null) {
            //司机姓名
            ((TextView) view.findViewById(R.id.driver_name)).setText(currentDriver.getName());
            //司机编号
            ((TextView) view.findViewById(R.id.driver_number)).setText(currentDriver.getNumber());
            //司机等级
            ((TextView) view.findViewById(R.id.driver_level)).setText(currentDriver.getLevel());
            DecimalFormat df = new DecimalFormat("0.00");
            //账户余额
            ((TextView) view.findViewById(R.id.user_money)).setText(currentDriver.getUser_money() != null && !"".equals(currentDriver.getUser_money()) ? df.format(Double.parseDouble(currentDriver.getUser_money())) : "0.00");
            //今天收入
            ((TextView) view.findViewById(R.id.today)).setText(currentDriver.getToday() != null && !"".equals(currentDriver.getToday()) ? df.format(Double.parseDouble(currentDriver.getToday())) : "0.00");
            //本月收入
            ((TextView) view.findViewById(R.id.month)).setText(currentDriver.getMonth() != null && !"".equals(currentDriver.getMonth()) ? df.format(Double.parseDouble(currentDriver.getMonth())) : "0.00");
            //当前积分
            ((TextView) view.findViewById(R.id.points)).setText(currentDriver.getPoints() != null && !"".equals(currentDriver.getPoints()) ? currentDriver.getPoints() : "0");
            //状态
            TextView statusTV = (TextView) view.findViewById(R.id.status_tv);
            if ("1".equals(currentDriver.getStatus())) {
                statusTV.setText("正常");
            } else if ("2".equals(currentDriver.getStatus())) {
                statusTV.setText("停用");
            } else if ("3".equals(currentDriver.getStatus())) {
                statusTV.setText("余额不足");
            } else if ("4".equals(currentDriver.getStatus())) {
                statusTV.setText("驾照过期");
            } else if ("5".equals(currentDriver.getStatus())) {
                statusTV.setText("屏蔽");
            }
            //星级
            RelativeLayout starRl = (RelativeLayout) mContentView.findViewById(R.id.starRl);
            try {
                int star = Integer.parseInt(currentDriver.getStar());
                for (int i = 0; i < star; i++) {
                    ((ImageView) starRl.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.star));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //司机头像
            ImageView targetImageView = (ImageView) view.findViewById(R.id.head_pic);
            String head_pic_url = currentDriver.getHead_pic();
            Glide
                    .with(this)
                    .load(Constants.BASE_URL + head_pic_url)
                    .placeholder(R.drawable.loading_blank)
                    .error(R.drawable.loading_blank)
                    .fallback(R.drawable.loading_blank)
                    .into(targetImageView);
        }
        if (refresh != null && refresh) {
            Toast.makeText(mActivity, "已刷新", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void setListener() {
        mRLDeposit.setOnClickListener(this);
        mRLTiXian.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_deposit: // 充值
                startActivity(new Intent(mApp, DepositActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            case R.id.status_tixian: // 提现
                startActivity(new Intent(mApp, TiXianActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            default:
                break;
        }
    }
}
