package bjx.com.siji.adapter;

import android.content.Context;

import bjx.com.siji.R;
import bjx.com.siji.model.TradeHisModel;
import bjx.com.siji.utils.DateUtils;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * Created by jqc on 2017/11/24.
 */

public class TradeHisAdapter extends BGAAdapterViewAdapter<TradeHisModel> {

    public TradeHisAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, TradeHisModel model) {
        String type = "";
        String money = "";
        if (model.getType().equals("1")) {
            type = "充值";
            money = "+ ";
        } else if (model.getType().equals("2")) {
            type = "消费";
            money = "- ";
        } else if (model.getType().equals("3")) {
            type = "提现";
            money = "- ";
        } else if (model.getType().equals("4")) {
            type = "佣金";
            money = "+ ";
        }
        helper.setText(R.id.tradehis_item_tv_msg, type);
        helper.setText(R.id.tradehis_item_tv_time, DateUtils.timedate(model.getAdd_time()));
        helper.setText(R.id.tradehis_item_tv_money, money += model.getUser_money());
    }
}
