package bjx.com.siji.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.utils.ToastUtil;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/12/10.
 */

public class UpdatePasswordActivity extends BaseActivity {
    ImageView mIVBack;
    TextView mTVTitle;
    TextView mTVOldPwd;
    TextView mTVNewPwd;
    TextView mTVSubmit;
    TextView mTVConfirm;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_update_password);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mTVTitle.setText("修改密码");
        mTVOldPwd=getViewById(R.id.oldPassword);
        mTVNewPwd=getViewById(R.id.newPassword);
        mTVSubmit=getViewById(R.id.submit);
        mTVConfirm=getViewById(R.id.confirm);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
        mTVSubmit.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                updatePwd();
                break;
        }
    }
    private void updatePwd() {
        String oldPwd = mTVOldPwd.getText().toString().trim();
        String newPwd = mTVNewPwd.getText().toString().trim();
        String confirm = mTVConfirm.getText().toString().trim();
        if (oldPwd == null || "".equals(oldPwd)) {
            ToastUtil.show("请输入原密码");
            return;
        }
        if (newPwd == null || "".equals(newPwd)) {
            ToastUtil.show("请输入新密码");
            return;
        }
        if (confirm == null || "".equals(confirm)) {
            ToastUtil.show("请确认新密码");
            return;
        }
        if (!confirm.equals(newPwd)) {
            ToastUtil.show("新密码前后输入不一致");
            return;
        }

        UserModel currentDriver = (UserModel) SPUtils.readObject(this, Constants.USERMODEL);
        String sj_id = "";
        if (currentDriver != null) {
            sj_id = currentDriver.getSj_id();
        }
        mEngine.password_reset2(sj_id, oldPwd, newPwd, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + oldPwd + newPwd).getBytes()))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String str = response.body().string();
                            JSONObject jo = new JSONObject(str);
                            int status = jo.getInt("status");
                            if (status == 200) {
                                showToast("密码修改成功");
                                finish();
                            } else {
                                showToast("密码修改失败");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showToast("密码修改失败");
                    }
                });
    }
}
