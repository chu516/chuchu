package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import bjx.com.siji.R;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.listener.MyDigitsKeyListener;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.DensityUtil;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    public static LoginActivity mInstants;

    EditText mETUName;
    EditText mETUPwd;
    CheckBox mCBRemember;
    TextView mTVForgetPwd;
    TextView mTVLogin;
    PopupWindow pop; //商品属性窗口

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        mInstants = this;

        mETUName = getViewById(R.id.login_et_uname);
        Drawable drawable = getResources().getDrawable(R.mipmap.user);
        drawable.setBounds(0, 0, Util.dip2px(mApp, 20), Util.dip2px(mApp, 20));
        mETUName.setCompoundDrawables(drawable, null, null, null);
        mETUPwd = getViewById(R.id.login_et_upwd);
        Drawable drawable2 = getResources().getDrawable(R.mipmap.pwd);
        drawable2.setBounds(0, 0, Util.dip2px(mApp, 20), Util.dip2px(mApp, 20));
        mETUPwd.setCompoundDrawables(drawable2, null, null, null);
        mCBRemember = getViewById(R.id.login_cb_remember);
        mTVForgetPwd = getViewById(R.id.login_tv_fpwd);
        mTVLogin = getViewById(R.id.login_tv_login);

        if (PreferenceUtil.getBooleanSharePreference(LoginActivity.this, Constant.CONFIG_IS_LOGIN, false)) {
            jumpToMain();
        }
    }

    @Override
    protected void setListener() {
        mTVForgetPwd.setOnClickListener(this);
        mTVLogin.setOnClickListener(this);
        mETUPwd.setKeyListener(new MyDigitsKeyListener(mApp));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
//        ！！请将AndroidManifest.xml api_key 字段值修改为自己的 api_key 方可使用 ！！
//        ！！ATTENTION：You need to modify the value of api_key to your own in AndroidManifest.xml to use this Demo !!
        // 启动百度push
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                Util.getMetaValue(this, "api_key"));
        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        /**
         * 以下通知栏设置2选1。使用默认通知时，无需添加以下设置代码。
         */

        // 1.默认通知
        // 若您的应用需要适配Android O（8.x）系统，且将目标版本targetSdkVersion设置为26及以上时：
        // SDK提供设置Android O（8.x）新特性---通知渠道的设置接口。
        // 若不额外设置，SDK将使用渠道名默认值"Push"；您也可以仿照以下3行代码自定义channelId/channelName。
        // 注：非targetSdkVersion 26的应用无需以下调用且不会生效
//        BasicPushNotificationBuilder bBuilder = new BasicPushNotificationBuilder();
//        bBuilder.setChannelId("testDefaultChannelId");
//        bBuilder.setChannelName("testDefaultChannelName");
        // PushManager.setDefaultNotificationBuilder(this, bBuilder); //使自定义channel生效

        // 2.自定义通知
        // 设置自定义的通知样式，具体API介绍见用户手册
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
//                R.layout.notification_custom_builder,
//                R.id.notification_icon,
//                R.id.notification_title,
//                R.id.notification_text);
//
//        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
//        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//        cBuilder.setLayoutDrawable(R.drawable.simple_notification_icon);
//        cBuilder.setNotificationSound(Uri.withAppendedPath(
//                Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        // 若您的应用需要适配Android O（8.x）系统，且将目标版本targetSdkVersion设置为26及以上时：
        // 可自定义channelId/channelName, 若不设置则使用默认值"Push"；
        // 注：非targetSdkVersion 26的应用无需以下2行调用且不会生效
//        cBuilder.setChannelId("testId");
//        cBuilder.setChannelName("testName");
        // 推送高级设置，通知栏样式设置为下面的ID，ID应与server下发字段notification_builder_id值保持一致
//        PushManager.setNotificationBuilder(this, 1, cBuilder);

        UserModel userModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        if (userModel != null) {
            mETUName.setText(userModel.getMobile());
            mETUPwd.setText((String) SPUtils.get(mApp, Constants.PWD, ""));
            mETUPwd.setSelection(mETUPwd.getText().length());
            mETUPwd.requestFocus();
        }

    }

    /**
     * 登录跳转 解决数据刷新问题 在我的最新消息进入登录界面 登录成功后 会刷新购物车列表
     */
    private void jumpToMain() {

//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra(Constant.CONFIG_LOGIN_ACCOUTN, account);
//         startActivity(intent);
//        this.finish();


        //之前的登录代码移到这里
        startActivity(new Intent(mApp, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        finish();

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_tv_fpwd:
                startActivity(new Intent(mApp, ForgetPwdActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;

            case R.id.login_tv_login:

                String username = mETUName.getText().toString().trim();
                final String password = mETUPwd.getText().toString().trim();

                if(username == null || username.equals("")) {
                    showToast("用户名不能为空");
                    mETUName.requestFocus();
                    return;
                }
                if(password == null || password.equals("")) {
                    showToast("密码不能为空");
                    mETUPwd.requestFocus();
                    return;
                }
                if(password.length() < 5) {
                    showToast("密码长度必须是5-12位");
                    mETUPwd.requestFocus();
                    return;
                }

                showNoticeDialog();

                break;
            default:
                break;
        }
    }



    private  void login(){
        String username = mETUName.getText().toString().trim();
        final String password = mETUPwd.getText().toString().trim();

        if(username == null || username.equals("")) {
            showToast("用户名不能为空");
            mETUName.requestFocus();
            return;
        }
        if(password == null || password.equals("")) {
            showToast("密码不能为空");
            mETUPwd.requestFocus();
            return;
        }
        if(password.length() < 5) {
            showToast("密码长度必须是5-12位");
            mETUPwd.requestFocus();
            return;
        }

        mEngine.login(username, password, MD5.getMessageDigest((username + Constants.BASE_KEY + password).getBytes())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jo = new JSONObject(str);
                    int status = jo.getInt("status");
                    String msg = jo.getString("msg");
                    if (status == 200) {

                        showToast("登录成功");
//                                LogsUtil.normal("登录成功"+str);

                        UserModel driver= GsonUtil.jsonToUserBean(jo.getString("result"));
                        SPUtils.put(mApp, Constants.FID, driver.getF_id());

                        driver.setWorkState("1");//默认下班状态
                        SPUtils.saveObject(mApp, Constants.USERMODEL,driver);

                        if (mCBRemember.isChecked()) {
                            SPUtils.put(mApp, Constants.PWD, password);
                        } else {
                            SPUtils.remove(mApp, Constants.PWD);
                        }

                        PreferenceUtil.putBooleanSharePreference(LoginActivity.this, Constant.CONFIG_IS_LOGIN, true);

                        jumpToMain();

                    } else if (status == 201) {
                        showToast("账号不存在");
                    } else if (status == 202) {
                        showToast("账号未通过审核，请与管理员联系");
                    } else if (status == 203) {
                        showToast("账号审核中，暂时无法登录");
                    } else if (status == 204) {
                        showToast("账号已被屏蔽，请与管理员联系");
                    } else if (status == 205) {
                        showToast("账号已绑定其它设备，当前手机无法登录");
                    } else if (status == 206) {
                        showToast("账号或密码不正确");
                    } else {
                        showToast("登录失败,请稍候再试");
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






    /**
     * 显示软件更新对话框
     */
    public void showNoticeDialog() {

        setBackgroundAlpha((float) 0.7);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_agreement_tip, null);

        Double popHeight = DensityUtil.getScreenHeight(this) * 0.8;

        pop = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                popHeight.intValue());

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
        final WebView attr_descrip_tv = (WebView) view.findViewById(R.id.attr_descrip_tv);

        WebSettings settings = attr_descrip_tv.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);//支持js
        //支持双击-前提是页面要支持才显示
//        webSettings.setUseWideViewPort(true);

        //支持缩放按钮-前提是页面要支持才显示
        settings.setBuiltInZoomControls(true);

        //设置客户端-不跳转到默认浏览器中
        attr_descrip_tv.setWebViewClient(new WebViewClient());

        attr_descrip_tv.loadUrl(Constants.LIC_URL);

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAlpha((float) 1.0);
                pop.dismiss();
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundAlpha((float) 1.0);
                login();
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
}
