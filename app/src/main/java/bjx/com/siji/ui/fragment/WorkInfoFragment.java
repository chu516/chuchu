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
import bjx.com.siji.model.AppraiseInfo;
import bjx.com.siji.ui.activity.MainActivity;
import bjx.com.siji.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WorkInfoFragment extends BaseFragment implements AdapterRefreshListener {
    private AappraiseAdapter adapter=new AappraiseAdapter();
    private List<AppraiseInfo> data;
    private RefreshListView refreshListView;
    private int pageIndex=0;//客户评价当前第几页
    @Override
    protected void initView(Bundle savedInstanceState) {
        LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        mContentView= inflater.inflate(R.layout.fragment_work_info,null);
        refreshListView=(RefreshListView)mContentView.findViewById(R.id.appraise_list);
        refreshData(2);
        refreshListView.adapterRefreshListener=this;
        refreshListView.setAdapter(adapter);

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


    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    /**
     *ListView数据刷新，type=1表示上拉刷新，type=2表示下拉加载更多
     */
    public void refreshData(int type) {
        if(data==null){
            data=new ArrayList<AppraiseInfo>();
        }
        if(data.size()>0){
            data.clear();
        }
        // TODO Auto-generated method stub
        //请在此处更新适配器数据，以下为测试代码
        if(type==1){
            //下拉刷新
            for(int i=0;i<10;i++){
                data.add(new AppraiseInfo("刷新客户"+i+"",3+"","2017-11-27 15:30","好好好"));
            }
        }else  if(type==2){
            for(int i=0;i<10;i++){
                data.add(new AppraiseInfo("加载客户"+i+"",3+"","2017-11-27 15:30","好好好"));
            }
        }
        //测试代码结束
        adapter.data=data;
        refreshListView.setAdapter(adapter);
    }

    //客户评价列表适配器
    public class AappraiseAdapter implements ListAdapter {
        public List<AppraiseInfo> data=new ArrayList<AppraiseInfo>();
        public AappraiseAdapter(){

        }
        public  AappraiseAdapter(List<AppraiseInfo> data){
            this.data=data;
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
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater= LayoutInflater.from(mApp);
            View itemView=inflater.inflate(R.layout.appraise_item,null,false);
            ((TextView)itemView.findViewById(R.id.customer_phone)).setText(data.get(i).customer_phone);
            ((TextView)itemView.findViewById(R.id.appraise_time)).setText(data.get(i).appraise_time);
            ((TextView)itemView.findViewById(R.id.appraise_content)).setText(data.get(i).appraise_content);
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

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return false;
        }
    }
}
