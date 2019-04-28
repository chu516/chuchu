package bjx.com.siji.ui.fragment;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.listener.AdapterRefreshListener;
import bjx.com.siji.model.SalaryDetailInfo;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * 收入明细fragment
 * Created by Administrator on 2017/11/29.
 */

public class SalaryDetailFragment extends BaseFragment implements  AdapterRefreshListener {
    private SalaryDetailAdapter adapter=new SalaryDetailAdapter();
    private List<SalaryDetailInfo> data;
    private RefreshListView refreshListView;
    private int pageIndex=0;//收入明细当前第几页
    @Override
    protected void initView(Bundle savedInstanceState) {
        LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView= inflater.inflate(R.layout.fragment_salary_detail,null);
        //Toolbar
        Toolbar toolbar =(Toolbar)mContentView.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, MainActivity.class);
                startActivity(intent);
            }
        });
        refreshListView=(RefreshListView)mContentView.findViewById(R.id.salary_list);
        ;
        refreshListView.adapterRefreshListener=this;
        refreshListView.setAdapter(adapter);
        refreshData(2);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void refreshData(int type) {
        if(data==null){
            data=new ArrayList<SalaryDetailInfo>();
        }
        if(data.size()>0){
            data.clear();
        }
        // TODO Auto-generated method stub
        //请在此处更新适配器数据，以下为测试代码
        if(type==1){
            //下拉刷新
            for(int i=0;i<10;i++){
                data.add(new SalaryDetailInfo("刷新订单号："+i,"司机提成","+50.00","2017-11-27 15:30"));
            }
        }else  if(type==2){
            for(int i=0;i<10;i++){
                data.add(new SalaryDetailInfo("加载订单号："+i,"司机提成","+50.00","2017-11-27 15:30"));
            }
        }
        //测试代码结束
        adapter.data=data;
        refreshListView.setAdapter(adapter);
    }

    //收入明细列表适配器
    public class SalaryDetailAdapter implements ListAdapter {
        public List<SalaryDetailInfo> data=new ArrayList<SalaryDetailInfo>();
        public SalaryDetailAdapter(){

        }
        public  SalaryDetailAdapter(List<SalaryDetailInfo> data){
            this.data=data;
        }
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater= LayoutInflater.from(mApp);
            View itemView=inflater.inflate(R.layout.salary_detail_item,null,false);
            ((TextView)itemView.findViewById(R.id.orderNo)).setText(data.get(i).orderNo);
            ((TextView)itemView.findViewById(R.id.orderType)).setText(data.get(i).orderType);
            ((TextView)itemView.findViewById(R.id.orderTotal)).setText(data.get(i).orderTotal);
            ((TextView)itemView.findViewById(R.id.orderTime)).setText(data.get(i).orderTime);
            return itemView;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }



}
