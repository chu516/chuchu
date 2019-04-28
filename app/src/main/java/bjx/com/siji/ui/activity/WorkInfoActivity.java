package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.model.CommentModel;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.utils.DateUtils;
import bjx.com.siji.utils.GsonUtil;
import bjx.com.siji.utils.SPUtils;
import bjx.com.siji.view.SimpleFooter;
import bjx.com.siji.view.SimpleHeader;
import bjx.com.siji.view.ZrcListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WorkInfoActivity extends BaseActivity{
    private ZrcListView listView;
    private Handler handler;
    private List<CommentModel> data=new ArrayList<>();
    private int pageId = -1;
    private MyAdapter adapter;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_work_info);
        //Toolbar
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
       /* setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WorkInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WorkInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        initData();
        //ListView
        listView=(ZrcListView)findViewById(R.id.appraise_list);
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
    private void initData() {
        UserModel currentDriver = (UserModel) SPUtils.readObject(this, Constants.USERMODEL);
        if (currentDriver != null) {
            //司机姓名
            ((TextView)findViewById(R.id.driver_name)).setText(currentDriver.getName());
            //司机编号
            ((TextView)findViewById(R.id.driver_number)).setText(currentDriver.getNumber());
            //司机等级
            ((TextView)findViewById(R.id.driver_level)).setText(currentDriver.getLevel());
            DecimalFormat df=new DecimalFormat("0.00");
            //账户余额
            ((TextView)findViewById(R.id.user_money)).setText(currentDriver.getUser_money()!=null && !"".equals(currentDriver.getUser_money())?df.format(Double.parseDouble(currentDriver.getUser_money())):"0.00");
            //今天收入
            ((TextView)findViewById(R.id.text_salary_today)).setText(currentDriver.getToday()!=null && !"".equals(currentDriver.getToday())?df.format(Double.parseDouble(currentDriver.getToday())):"0.00");
            //本月收入
            ((TextView)findViewById(R.id.text_salary_month)).setText(currentDriver.getMonth()!=null && !"".equals(currentDriver.getMonth())?df.format(Double.parseDouble(currentDriver.getMonth())):"0.00");
            //星级
            RelativeLayout starRl=(RelativeLayout)findViewById(R.id.starRl);
            try{
                int star=Integer.parseInt(currentDriver.getStar());
                for(int i=0;i<star;i++){
                    ((ImageView)starRl.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.star));
                }
            }catch(Exception e){
                e.printStackTrace();
            }


            //司机头像
            ImageView targetImageView=(ImageView)findViewById(R.id.head_pic);
            String head_pic_url=currentDriver.getHead_pic();
            Glide
                    .with(this)
                    .load(Constants.BASE_URL+head_pic_url)
                    .placeholder(R.drawable.loading_blank)
                    .error(R.drawable.loading_blank)
                    .fallback(R.drawable.loading_blank)
                    .into(targetImageView);
        }
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    private void refresh(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageId = 1;
                UserModel currentDriver=(UserModel) SPUtils.readObject(WorkInfoActivity.this, Constants.USERMODEL);
                String sj_id="";
                if(currentDriver!=null){
                    sj_id=currentDriver.getSj_id();
                }
                mEngine.getComments(sj_id,pageId+"",10+"")
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String str = response.body().string();
                                    JSONObject jo = new JSONObject(str);
                                    int status = jo.getInt("status");
                                    if (status == 200) {
                                        JSONArray ja = jo.getJSONArray("result");
                                        List<CommentModel> mList = new ArrayList<>();
                                        for (int i = 0; i < ja.length(); i++) {
                                            mList.add(GsonUtil.jsonCommentBean(ja.getJSONObject(i).toString()));
                                        }
                                        data = mList;
                                        adapter.notifyDataSetChanged();
                                        listView.setRefreshSuccess("刷新成功"); // 通知刷新成功
                                        listView.startLoadMore(); // 开启LoadingMore功能
                                    }

//                                    data.clear();
//                                    adapter.notifyDataSetChanged();
//                                    listView.setRefreshFail("刷新失败");
//                                    listView.stopLoadMore();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        }, 1* 1000);
    }

    private void loadMore(){
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pageId++;
                                    UserModel currentDriver=(UserModel) SPUtils.readObject(WorkInfoActivity.this, Constants.USERMODEL);
                                    String sj_id="";
                                    if(currentDriver!=null){
                                        sj_id=currentDriver.getSj_id();
                                    }
                                    mEngine.getComments(sj_id,pageId+"",10+"")
                                            .enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    try {
                                                        String str = response.body().string();
                                                        JSONObject jo = new JSONObject(str);
                                                        int status = jo.getInt("status");
                                                        if (status == 200) {
                                                            JSONArray ja = jo.getJSONArray("result");
                                                            List<CommentModel> mList = new ArrayList<>();
                                                            for (int i = 0; i < ja.length(); i++) {
                                                                data.add(GsonUtil.jsonCommentBean(ja.getJSONObject(i).toString()));
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            listView.setLoadMoreSuccess();

                                                            if (ja.length() == 0) {
                                                                listView.stopLoadMore();
                                                            }

//                                                            JSONArray resultData = jo.getJSONArray("result");
//                                                            // if (resultData != null && resultData.length() > 0) {
//                                                            if(true){
//                                                                //测试代码start
//                                                                for (int i = (pageId - 1) * 10 + 1; i < (pageId - 1) * 10 + 1 + 10; i++) {
//                                                                    data.add(new AppraiseInfo(i + "", "", "2017-12-04 16:00", "好好好好好好好好好好好好好好好"));
//                                                                }
//                                                                //测试代码end
//                                                                adapter.notifyDataSetChanged();
//                                                                listView.setLoadMoreSuccess();
//                                                                return;
//                                                            }
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
                itemView = getLayoutInflater().inflate(R.layout.appraise_item, null);
            }else {
                itemView = convertView;
            }
//            ((TextView)itemView.findViewById(R.id.customer_phone)).setText(data.get(position).get);
            ((TextView)itemView.findViewById(R.id.appraise_time)).setText(DateUtils.time(data.get(position).getAdd_time()));
            ((TextView)itemView.findViewById(R.id.appraise_content)).setText(data.get(position).getContent());
            //星级
            RelativeLayout starRl=(RelativeLayout)itemView.findViewById(R.id.starRl);
            try{
                int star=(Integer.parseInt(data.get(position).getStar1()) + Integer.parseInt(data.get(position).getStar2()) + Integer.parseInt(data.get(position).getStar3()))/3;
                for(int i=0;i<star;i++){
                    ((ImageView)starRl.getChildAt(i)).setImageDrawable(getResources().getDrawable(R.drawable.star));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return itemView;
        }
    }
}
