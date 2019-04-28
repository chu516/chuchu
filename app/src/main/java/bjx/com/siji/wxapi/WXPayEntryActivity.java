package bjx.com.siji.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.ui.activity.BaseActivity;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	private IWXAPI api;



	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constants.WX_APPID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void processLogic(Bundle savedInstanceState) {

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if(resp.errCode == -2) {
				ToastUtil.show("取消支付");
			} else if(resp.errCode == 0) {
				String tradeNo = ((PayResp) resp).extData;
				mEngine.reqPayQueryForWX(userModel.getSj_id(), tradeNo, MD5.getMessageDigest((userModel.getSj_id() + Constants.BASE_KEY + tradeNo).getBytes())).enqueue(new Callback<ResponseBody>() {
					@Override
					public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
						try {
							String str = response.body().string();
							JSONObject jo = new JSONObject(str);
							int status = jo.getInt("status");
							String msg = jo.getString("msg");
							if (status == 200) {
								ToastUtil.show("支付成功");
							} else if (status == 500 || status == 501) {
								ToastUtil.show("订单支付失败");
							} else if (status == 502) {
								ToastUtil.show("订单异常");
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
			} else if(resp.errCode == -1) {
				ToastUtil.show("支付失败");
			}
			finish();
		}
	}
}