package bjx.com.siji.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.engine.Engine;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.ToastUtil;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected App mApp;
    protected Engine mEngine;
    protected UserModel userModel;
    protected PowerManager.WakeLock wakeLock;
    protected  ContentResolver mContentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 禁止横屏
        mApp = App.getInstance();
        mEngine = mApp.getEngine();
        userModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        mContentResolver = getContentResolver();
        setLockPatternEnabled(false);

    }

    public void setLockPatternEnabled(boolean enabled) {
        setBoolean(android.provider.Settings.System.LOCK_PATTERN_ENABLED,
                enabled);
    }
    public void setBoolean(String systemSettingKey, boolean enabled) {
        android.provider.Settings.System.putInt(mContentResolver,
                systemSettingKey, enabled ? 1 : 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        userModel = (UserModel) SPUtils.readObject(mApp, Constants.USERMODEL);
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, "wakeLock");
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }



    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    public void showToast(String text) {
        ToastUtil.show(text);
    }

    public void showToast(int resid) {
        ToastUtil.show(resid);
    }

    public void showNetErrToast() {
        ToastUtil.show(R.string.net_err);
    }


    /**
     * 调用拨号界面
     * @param phone 电话号码
     */
    public void callTel(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    /**
     * 调用拨号界面，拨号完毕返回原页面
     * @param phone 电话号码
     */
    public void callTel2Return(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,999);
    }
}