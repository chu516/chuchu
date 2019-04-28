package bjx.com.siji.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import bjx.com.siji.R;
import bjx.com.siji.model.ResultModel;
import bjx.com.siji.utils.LogsUtil;
import com.lzy.okgo.OkGo;

import java.util.Random;

/**
 * Created by Administrator on 2018/1/22.
 */

public class LaunchActivity  extends BaseActivity {



    /**
     * 初始化布局以及View控件
     */
    protected void initView(Bundle savedInstanceState){
        setContentView(R.layout.activity_launch);

         Integer time = 50;    //设置等待时间，单位为毫秒 之前的5000
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                LaunchActivity.this.finish();
            }
        }, time);
    };

    /**
     * 给View控件添加事件监听器
     */
    protected void setListener(){};

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected   void processLogic(Bundle savedInstanceState){};

}
