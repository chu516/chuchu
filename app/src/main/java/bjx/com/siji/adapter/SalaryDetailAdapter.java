package bjx.com.siji.adapter;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.model.SalaryDetailInfo;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Created by Administrator on 2017/12/4.
 */

public class SalaryDetailAdapter  implements ListAdapter {
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
        LayoutInflater inflater= LayoutInflater.from(getContext());
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

