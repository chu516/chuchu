package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.SalaryDetailModel;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.DateUtils;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.MD5;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.view.SimpleFooter;
import bjx.com.siji.view.SimpleHeader;
import bjx.com.siji.view.ZrcListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bjx.com.siji.R.id.orderTime;
import static bjx.com.siji.R.id.orderTotal;

/**
 * Created by Administrator on 2017/12/4.
 */

public class SalaryDetailActivity extends BaseActivity{
    private ZrcListView listView;
    private Handler handler;
    private List<SalaryDetailModel> data=new ArrayList<>();
    private int pageId = -1;
    private MyAdapter adapter;
    private String salaryType;
    private Boolean firstRefresh=true;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_salary_detail);
        //Toolbar
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
     /*   setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SalaryDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SalaryDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        TextView tv=(TextView)findViewById(R.id.toolbar_text);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            salaryType = extras.getString("type");
            if ("1".equals(salaryType)) {
                //今天收入
                tv.setText("今天收入明细");
            } else if ("2".equals(salaryType)) {
                //本月收入
                tv.setText("本月收入明细");
            } else if ("3".equals(salaryType)) {
                //全部收入
                tv.setText("收入明细");
            }
        }
        //ListView

        listView = (ZrcListView) findViewById(R.id.salary_list);
        handler = new Handler();

        // 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
//        float density = getResources().getDisplayMetrics().density;
//        listView.setFirstTopOffset((int) (50 * density));

        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(R.color.black);
        header.setCircleColor(R.color.black);
        listView.setHeadable(header);

        // 设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(this);
        footer.setCircleColor(R.color.black);
        listView.setFootable(footer);

        // 设置列表项出现动画（可选）
        //listView.setItemAnimForTopIn(R.anim.topitem_in);
        //listView.setItemAnimForBottomIn(R.anim.bottomitem_in);

        // 下拉刷新事件回调（可选）
        listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });

        // 加载更多事件回调（可选）
        listView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.refresh();
    }


    private void refresh(){
        int delayTime=1* 1000;
        if(firstRefresh!=null && firstRefresh){//首次立即刷新
            firstRefresh=false;
            delayTime=0;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    pageId = 1;
                    UserModel currentDriver=(UserModel) SPUtils.readObject(SalaryDetailActivity.this, Constants.USERMODEL);
                    String sj_id="";
                    if(currentDriver!=null){
                        sj_id=currentDriver.getSj_id();
                    }
                     mEngine.getRevenueList(sj_id,pageId+"",10+"",salaryType, MD5.getMessageDigest((sj_id+ Constants.BASE_KEY+salaryType).getBytes()))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String str = response.body().string();
                                    //str="{\"status\":200,\"msg\":\"Ok\",\"fid\":\"1\",\"result\":[{\"log_id\":22,\"money\":\"0.01\",\"add_time\":1512524267,\"order_sn\":null,\"order_id\":null},{\"log_id\":23,\"money\":\"0.01\",\"add_time\":1512524605,\"order_sn\":null,\"order_id\":null}]}";
                                    JSONObject jo = new JSONObject(str);
                                    int status = jo.getInt("status");

                                    if (status == 200) {
                                        JSONArray ja = jo.getJSONArray("result");
                                        List<SalaryDetailModel> mList = new ArrayList<>();
                                        for (int i = 0; i < ja.length(); i++) {
                                            mList.add(GsonUtil.jsonSalaryDetailBean(ja.getJSONObject(i).toString()));
                                        }
                                        data = mList;
                                        adapter.notifyDataSetChanged();
                                        listView.setRefreshSuccess("刷新成功"); // 通知刷新成功
                                        listView.startLoadMore(); // 开启LoadingMore功能

//                                        JSONArray resultData = jo.getJSONArray("result");
//                                        if (resultData != null && resultData.length() > 0) {
//                                            data.clear();
//                                            adapter.notifyDataSetChanged();
//                                            listView.setRefreshSuccess("刷新成功"); // 通知刷新成功
//                                            listView.startLoadMore(); // 开启LoadingMore功能
//                                            return;
//                                        }
//                                        data.clear();
//                                        adapter.notifyDataSetChanged();
//                                        listView.setRefreshFail("刷新失败");
//                                        listView.stopLoadMore();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        }, delayTime);
    }

    private void loadMore(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageId++;
                UserModel currentDriver=(UserModel) SPUtils.readObject(SalaryDetailActivity.this, Constants.USERMODEL);
                String sj_id="";
                if(currentDriver!=null){
                    sj_id=currentDriver.getSj_id();
                }
                mEngine.getRevenueList(sj_id, pageId + "", 10 + "", salaryType, MD5.getMessageDigest((sj_id + Constants.BASE_KEY + salaryType).getBytes()))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String str = response.body().string();
                                    JSONObject jo = new JSONObject(str);
                                    int status = jo.getInt("status");
                                    if (status == 200) {
                                        JSONArray ja = jo.getJSONArray("result");
                                        List<SalaryDetailModel> mList = new ArrayList<>();
                                        for (int i = 0; i < ja.length(); i++) {
                                            data.add(GsonUtil.jsonSalaryDetailBean(ja.getJSONObject(i).toString()));
                                        }
                                        adapter.notifyDataSetChanged();
                                        listView.setLoadMoreSuccess();

                                        if (ja.length() == 0) {
                                            listView.stopLoadMore();
                                        }

                                        //if (resultData != null && resultData.length() > 0) {
//                                         if(true){
//                                           //测试代码start
//                                            for (int i = (pageId - 1) * 10 + 1; i < (pageId - 1) * 10 + 1 + 10; i++) {
//                                                data.add(new SalaryDetailInfo(i + "", "司机提现", "+50.00", "2017-12-04 16:00"));
//                                            }
//                                            //测试代码end
//                                            adapter.notifyDataSetChanged();
//                                            listView.setLoadMoreSuccess();
//                                            return;
//                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                pageId--;
                            }
                        });
                }
            }
        , 1 * 1000);
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data==null ? 0 : data.size();
        }
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;
            if(convertView==null) {
                itemView = getLayoutInflater().inflate(R.layout.salary_detail_item, null);
            }else {
                itemView = convertView;
            }
            ((TextView)itemView.findViewById(R.id.orderNo)).setText("订单编号：" + data.get(position).getOrder_sn());
            ((TextView)itemView.findViewById(R.id.orderType)).setText("类型：" + "订单提成");
            ((TextView)itemView.findViewById(orderTotal)).setText(data.get(position).getMoney());
            ((TextView)itemView.findViewById(orderTime)).setText(DateUtils.time(data.get(position).getAdd_time()));
            return itemView;
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }


}
