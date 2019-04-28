package bjx.com.siji.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.model.EventBean;

import de.greenrobot.event.EventBus;

/**
 * Created by 作者wang on 2018/11/28.
 */
public class DialogUtil {


    /*
     * 网络连接提示
     */
    public static void showNetWorkDialog(final Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.MyDialog).create();

        //设置外部不可以点击
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置


        View view = View.inflate(context, R.layout.dialog_middle_login, null);
        window.setContentView(view);

        TextView tv0 = (TextView) view.findViewById(R.id.title_tv);
        TextView tv1 = (TextView) view.findViewById(R.id.middle_tv);
        TextView tv2 = (TextView) view.findViewById(R.id.cancel);
        TextView tv3 = (TextView) view.findViewById(R.id.ok);

        tv0.setText(R.string.dialog_network_tip);
        tv1.setText(R.string.dialog_network_start_location);
        tv2.setText(R.string.basic_cancel);
        tv3.setText(R.string.dialog_network_set);

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new EventBean(EventBean.EVENT_DISLOG_CANCLE));

                dialog.dismiss();
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new EventBean(EventBean.EVENT_DISLOG_CONFIRM));

                dialog.dismiss();
            }
        });
    }

}
