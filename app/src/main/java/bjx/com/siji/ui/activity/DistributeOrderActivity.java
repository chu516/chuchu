package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bjx.com.siji.R;

/**
 * 订单推送
 * Created by Administrator on 2017/11/27.
 */

public class DistributeOrderActivity extends BaseActivity implements View.OnClickListener {

    private TextView clock;//倒计时
    private TextView address_start;//开始地址
    private Button new_order_submit,new_order_cancel;//接单/拒绝
    private TextView order_create_time;//下单年月日
    private TextView time_hours;//下单时间
    private TextView mobile;//客户手机号


    private TextView address_end;//结束地址
    private EditText beizhu;//备注
    private TextView order_number;//订单编号

    private int count = 60;
    private int COUNT_TIME = 0;


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.distribute_order);
        initView();

        handler.sendEmptyMessage(COUNT_TIME);

    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(count <= 0){
                count = 60;
                return;
            }
            count--;
            clock.setText(count+"秒");
            if(count<=0){
                new_order_submit.setEnabled(false);
                new_order_cancel.setEnabled(false);
                finish();
            }
            sendEmptyMessageDelayed(COUNT_TIME,1000);

        }
    };



    private void initView() {
        clock = (TextView) findViewById(R.id.clock);

        address_end = (TextView) findViewById(R.id.address_end);
        time_hours = (TextView) findViewById(R.id.time_hours);
        mobile = (TextView) findViewById(R.id.mobile);
        order_create_time = (TextView) findViewById(R.id.order_create_time);
        address_start = (TextView) findViewById(R.id.address_start);
        new_order_submit = (Button) findViewById(R.id.new_order_submit);
        new_order_cancel = (Button) findViewById(R.id.new_order_cancel);
        address_end = (TextView) findViewById(R.id.address_end);
        beizhu = (EditText) findViewById(R.id.beizhu);

        new_order_submit.setEnabled(true);
        new_order_cancel.setEnabled(true);
    }


    @Override
    protected void setListener() {

        new_order_submit.setOnClickListener(new View.OnClickListener() {//刷新
            @Override
            public void onClick(View v) {
                Toast.makeText(DistributeOrderActivity.this,"提交",Toast.LENGTH_SHORT).show();
//
//                Intent toover = new Intent(DistributeOrderActivity.this,ShowMapDemoActivity.class);

//                Intent toover = new Intent(DistributeOrderActivity.this,CustomerActivity.class);
//                Intent toover = new Intent(DistributeOrderActivity.this,CustomerMapActivity.class);
//                startActivity(toover);

//                =latitude: 30.55443797460148, longitude: 117.07241116163019

            }
        });
        new_order_cancel.setOnClickListener(new View.OnClickListener() {//刷新
            @Override
            public void onClick(View v) {
                Toast.makeText(DistributeOrderActivity.this,"取消",Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        address_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latLng ="30.55443797460148";//  LatLng LatLng = new LatLng(30.55443797460148,117.07241116163019);
                String londat= "117.07241116163019";//  LatLng LatLng = new LatLng(30.55443797460148,117.07241116163019);


                String addre = address_start.getText().toString().trim();

                Intent over = new Intent(DistributeOrderActivity.this,ShowMapOrderActivity.class);
                over.putExtra("lat",latLng);
                over.putExtra("type",1+"");
                over.putExtra("lon",londat);
                over.putExtra("Address",addre);
                startActivity(over);
            }
        });

        address_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latLng ="30.55443797460148";//  LatLng LatLng = new LatLng(30.55443797460148,117.07241116163019);
                String londat= "117.07241116163019";//  LatLng LatLng = new LatLng(30.55443797460148,117.07241116163019);


                String addre = address_start.getText().toString().trim();

                Intent over = new Intent(DistributeOrderActivity.this,ShowMapOrderActivity.class);
                over.putExtra("lat",latLng);
                over.putExtra("type",2+"");
                over.putExtra("lon",londat);
                over.putExtra("Address",addre);
                startActivity(over);
            }
        });

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }





}
