package bjx.com.siji.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import bjx.com.siji.R;
import bjx.com.siji.application.App;
import bjx.com.siji.base.Constant;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.engine.Engine;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.service.DriverService;
import bjx.com.siji.ui.activity.LoginActivity;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.ui.activity.NoticeActivity;
import bjx.com.siji.ui.activity.UpdatePasswordActivity;
import bjx.com.siji.utils.LogsUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.PreferenceUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.Util;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Administrator on 2017/11/24.
 */

public class MoreFragment extends BaseFragment {
    private Engine mEngine = App.getInstance().getEngine();


    @Override
    protected void initView(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.fragment_more, null);
        //Toolbar
        Toolbar toolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        //公告
        mContentView.findViewById(R.id.more_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, NoticeActivity.class);
                startActivity(intent);
            }
        });
        //客服
        mContentView.findViewById(R.id.more_kf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + SPUtils.get(mApp, Constants.KEFU, "")));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //密码
        mContentView.findViewById(R.id.more_updatePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
                startActivity(new Intent(mActivity, UpdatePasswordActivity.class));
            }
        });

        //缓存
        mContentView.findViewById(R.id.more_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("已清除缓存");
            }
        });
        mContentView.findViewById(R.id.more_clear2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel currentDriver = (UserModel) SPUtils.readObject(mActivity, Constants.USERMODEL);
                String sj_id = "";
                if (currentDriver != null) {
                    sj_id = currentDriver.getSj_id();
                }
                mEngine.clear_uuid(sj_id, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + Util.getPhoneSign(mActivity)).getBytes()))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String str = response.body().string();
                                    JSONObject jo = new JSONObject(str);
                                    int status = jo.getInt("status");
                                    if (status == 200) {
                                        showToast("已清除唯一码");
                                    } else {
                                        showToast("清除失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showToast("清除失败");
                            }
                        });
            }
        });

        mContentView.findViewById(R.id.more_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity).setTitle("确认退出吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //请求下线

                                UserModel currentDriver = (UserModel) SPUtils.readObject(getActivity(), Constants.USERMODEL);
                                if (currentDriver != null) {
                                    String sj_id = currentDriver.getSj_id();
                                    if ("2".equals(currentDriver.getWorkState())) {
                                        //在上班 下线之后推出

                                        //下班
                                        mEngine.offWork(sj_id, 1 + "", MD5.getMessageDigest((sj_id + Constants.BASE_KEY + Util.getPhoneSign(getActivity())).getBytes())).enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                try {
                                                    String str = response.body().string();
                                                    JSONObject jo = new JSONObject(str);
                                                    int status = jo.getInt("status");
                                                    if (status == 200) {
                                                        // SPUtils.saveObject(App.getInstance(), Constants.USERMODEL, currentDriver);
                                                        LogsUtil.normal("下班成功，接下来退出登录");
                                                        exit();
                                                    } else {

                                                        Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                    } else {
                                        exit();
                                    }
                                }

                                //mActivity.finish();

                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“返回”后的操作,这里不设置没有任何操作
                            }
                        }).show();
            }
        });

    }

    public void exit() {

        PreferenceUtil.clearBooleanSharePreference(getActivity(), Constant.CONFIG_IS_LOGIN);

        // 点击“确认”后的操作
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);

        Intent intent2 = new Intent(mActivity.getApplicationContext(), DriverService.class);
        mActivity.getApplicationContext().stopService(intent2);

        //新人上场
        ((MainActivity) mActivity).newDriver = true;
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }


}
