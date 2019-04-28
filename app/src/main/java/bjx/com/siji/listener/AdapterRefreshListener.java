package bjx.com.siji.listener;

/**
 * Created by Administrator on 2017/11/27.
 */

public interface AdapterRefreshListener {
   //ListView数据刷新，type=1表示上拉刷新，type=2表示下拉加载更多
    public void refreshData(int type);
}
