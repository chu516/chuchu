package bjx.com.siji.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPwdActivity extends BaseActivity {

    ImageView mIVBack;
    TextView mTVTitle;

    EditText mETTel;
    EditText mETIdenCode;
    TextView mTVSendIdenCode;
    EditText mETPwd;
    TextView mTVSub;

    TimeCount time = new TimeCount(60000, 1000);

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forgetpwd);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("忘记密码");

        mETTel = getViewById(R.id.fgtpwd_et_tel);
        mETIdenCode = getViewById(R.id.fgtpwd_et_idencode);
        mTVSendIdenCode = getViewById(R.id.fgtpwd_tv_sendidencode);
        mETPwd = getViewById(R.id.fgtpwd_et_pwd);
        mTVSub = getViewById(R.id.fgtpwd_tv_sub);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
        mTVSendIdenCode.setOnClickListener(this);
        mTVSub.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fgtpwd_tv_sendidencode:
                String tel2 = mETTel.getText().toString().trim();
                if(!Util.isMobileNO(tel2)) {
                    showToast("手机号不正确");
                    mETTel.requestFocus();
                    return;
                }

                String code = String.valueOf(new Random().nextInt(9000) + 1000);
                time.start();

                mEngine.sendIdenCodeForResetPwd(code, tel2, MD5.getMessageDigest((tel2 + Constants.BASE_KEY + code).getBytes())).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            String msg = jo.getString("msg");
                            if (status == 200) {
                                showToast("验证码已发送");
                            } else if (status == 201) {
                                showToast("帐号不存在");
                            } else if (status == 202) {
                                showToast("帐号与绑定设备不匹配");
                            } else if (status == 301) {
                                showToast("发送过频繁");
                            } else {
                                showToast("发送失败");
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
                break;
            case R.id.fgtpwd_tv_sub:
                String tel = mETTel.getText().toString().trim();
                String pwd = mETPwd.getText().toString().trim();
                String idenCode = mETIdenCode.getText().toString().trim();
                String key = MD5.getMessageDigest((tel + Constants.BASE_KEY + idenCode + pwd).getBytes());

                if(!Util.isMobileNO(tel)) {
                    showToast("手机号不正确");
                    mETTel.requestFocus();
                    return;
                }

                if (pwd == null || pwd.equals("")) {
                    showToast("密码不能为空");
                    mETPwd.requestFocus();
                    return;
                }

                mEngine.resetPwd(tel, pwd, idenCode, key).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            String msg = jo.getString("msg");

                            if (status == 200) {
                                showToast("密码已重置");
                                SPUtils.remove(mApp, Constants.PWD);
                                finish();
                            } else if (status == 201) {
                                showToast("帐号不存在");
                            } else if (status == 202) {
                                showToast("账号绑定设备不匹配");
                            } else if (status == 301) {
                                showToast("请先获取验证码");
                            } else if (status == 302) {
                                showToast("验证码已失效");
                            } else if (status == 304) {
                                showToast("短信发送失败，请重新获取");
                            } else {
                                showToast("密码重置失败");
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
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTVSendIdenCode.setClickable(false);
            mTVSendIdenCode.setText(""+millisUntilFinished / 1000 +"s");
        }

        @Override
        public void onFinish() {
            mTVSendIdenCode.setText("重新获取");
            mTVSendIdenCode.setClickable(true);
        }
    }
}
