package bjx.com.siji.view;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bjx.com.siji.R;

/**
 * Created by Administrator on 2017/12/1.
 */

public class ChargingPopWin extends PopupWindow {

    private Context mContext;
    private View view;

    private TextView button_start_wait_end,button_order_end;
    private ImageView resulf;
    private TextView start_navigation,call_customer,call_customer_service;
    public TextView wait_time,mileage,travel_time,driving_amount;

    public ChargingPopWin(Activity mContext, View.OnClickListener itemsOnClick) {

        this.mContext = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.view = LayoutInflater.from(mContext).inflate(R.layout.start_drive_dialog, null);
        //初始化控件
        initView();

        // 设置按钮监听
       dealwith();

        // 设置外部可点击
        this.setOutsideTouchable(true);


        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);

        // 设置弹出窗体的宽和高
          /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = mContext.getWindow();

        WindowManager m = mContext.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth((int) (d.getWidth() * 0.8));

        // 设置弹出窗体可点击
        this.setFocusable(true);

    }

    private void dealwith() {
//        button_start_wait_end.setOnClickListener(MMapFragment.itemsOnClick);
//        button_order_end.setOnClickListener(itemsOnClick);

        ;
    }

    private void initView() {
        button_start_wait_end = (TextView) view.findViewById(R.id.button_start_wait_end);
        button_order_end = (TextView) view.findViewById(R.id.button_order_end);
        start_navigation = (TextView) view.findViewById(R.id.start_navigation);
        call_customer = (TextView) view.findViewById(R.id.call_customer);
        call_customer_service = (TextView) view.findViewById(R.id.call_customer_service);
        wait_time = (TextView) view.findViewById(R.id.wait_time);
        mileage = (TextView) view.findViewById(R.id.mileage);
        travel_time = (TextView) view.findViewById(R.id.travel_time);
        driving_amount = (TextView) view.findViewById(R.id.driving_amount);
    }
}
