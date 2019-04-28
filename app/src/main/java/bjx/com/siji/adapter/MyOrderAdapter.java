package bjx.com.siji.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import bjx.com.siji.R;
import bjx.com.siji.model.MyOrderModel;
import bjx.com.siji.utils.DateUtils;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;


/**
 * Created by jqc on 2017/10/31.
 */

public class MyOrderAdapter extends BGAAdapterViewAdapter<MyOrderModel> {

    boolean isOpen; // 是否编辑
    boolean isCheckAll; // 是否全选

    public MyOrderAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, MyOrderModel model) {
        helper.setText(R.id.myorder_item_createTime, "下单时间：" + DateUtils.timet(model.getAdd_time()));
        helper.setText(R.id.myorder_item_startLoc, "出发地：" + model.getAddress_st());
        helper.setText(R.id.myorder_item_endLoc, "目的地：" + model.getAddress_end());
        String orderStatus = model.getOrder_status();
        String orderStatusStr = "";


        if (orderStatus != null) {
            if (orderStatus.equals("0")) {
                orderStatusStr = "派发中";
            } else if (orderStatus.equals("1")) {
                orderStatusStr = "已接单";

                helper.getTextView(R.id.myorder_item_status).setTextColor(mContext.getResources().getColor(R.color.yellow_one));
            } else if (orderStatus.equals("2")) {
                orderStatusStr = "已取消";
            } else if (orderStatus.equals("3")) {
                 orderStatusStr = "进行中";
                helper.getTextView(R.id.myorder_item_status).setTextColor(mContext.getResources().getColor(R.color.yellow_one));
            } else if (orderStatus.equals("4")) {
                if (model.getPay_status().equals("0")) {
                    orderStatusStr = "未支付";

                    helper.getTextView(R.id.myorder_item_status).setTextColor(mContext.getResources().getColor(R.color.yellow_one));

                } else {
                    orderStatusStr = "已完成";
                }

                helper.setTextColor(R.id.myorder_item_status, R.color.black);

            } else if (orderStatus.equals("9")) {
                orderStatusStr = "待派发";
            }
        }

        helper.setText(R.id.myorder_item_status, orderStatusStr);

        if (isOpen) {
            if (orderStatus.equals("2"))
                helper.setVisibility(R.id.myorder_item_cb, View.VISIBLE);
            else
                helper.setVisibility(R.id.myorder_item_cb, View.INVISIBLE);
        } else {
            helper.setVisibility(R.id.myorder_item_cb, View.GONE);
        }
        if (isCheckAll) {
            if (orderStatus.equals("2")) {
                ((CheckBox) helper.getView(R.id.myorder_item_cb)).setChecked(true);
            } else
                ((CheckBox) helper.getView(R.id.myorder_item_cb)).setChecked(false);
        } else {
            ((CheckBox) helper.getView(R.id.myorder_item_cb)).setChecked(false);
        }
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
        viewHolderHelper.setItemChildClickListener(R.id.myorder_item_cb);

        viewHolderHelper.setItemChildClickListener(R.id.myorder_item_status);

    }

    public void switchCheck(boolean isCheck) {
        this.isCheckAll = isCheck;
        notifyDataSetChanged();
    }

    public void switchDel(boolean isOpen) {
        this.isOpen = isOpen;
        notifyDataSetChanged();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isCheckAll() {
        return isCheckAll;
    }

    public void setCheckAll(boolean check) {
        isCheckAll = check;
    }

}
