package bjx.com.siji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.listener.AdapterRefreshListener;

/**
 * 增添下拉刷新、上拉加载更多功能的ListView
 * Created by Administrator on 2017/11/27.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener{
    private View refreshHeader;//下拉刷新头
    private View refreshFoot;//上拉加载更多

    private TextView refresh_text;//刷新提示文字
    private TextView refresh_foot_text;//加载更多提示文字
    private int refresh_height;//刷新头高度
    private int refresh_foot_height;//底部加载高度
    private Integer fixedFootTop;

    private Boolean isRefreshing=false;//当前是否在刷新
    private Boolean canScroll=false;//创建时禁止滚动
    private int currentState=STATE_NORMAL;//刷新状态

    public int adjustment;//下拉加载高度调整值

    public AdapterRefreshListener adapterRefreshListener;//刷新监听器，进行数据刷新

    public static  final int STATE_NORMAL=1;//普通状态
    public static  final int STATE_DOWN_TO_REFRESH=2;//下拉刷新中
    public static  final int STATE_DOWN_RELEASE=3;//下拉刷新放手
    public static  final int STATE_UP_TO_REFRESH=4;//上拉加载更多中
    public static  final int STATE_UP_RELEASE=5;//上拉加载更多放手

    public RefreshListView(Context context,AttributeSet attrs){
        super(context, attrs);
        init();
        setOnScrollListener(this);
    }
    public RefreshListView(Context context) {
        super(context);
    }
    private void init(){
        LayoutInflater inflater= LayoutInflater.from(getContext());
        //下拉刷新
        refreshHeader=inflater.inflate(R.layout.refresh_header,this,false);
        refresh_text=(TextView)refreshHeader.findViewById(R.id.refresh_text);
        refresh_height=refreshHeader.getLayoutParams().height;


        //上拉加载更多
        refreshFoot=inflater.inflate(R.layout.refresh_foot,this,false);
        refresh_foot_text=(TextView)refreshFoot.findViewById(R.id.refresh_foot_text);
        refresh_foot_height=refreshFoot.getLayoutParams().height;
        refreshFoot.setTop(0);
        refreshHeader.setBottom(0);
        initRefreshText();
        addHeaderView(refreshHeader,null,true);
        addFooterView(refreshFoot,null,true);
        setHeaderDividersEnabled(false);
        setFooterDividersEnabled(false);

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

    }
    public void initRefreshText(){
        refresh_text.setText("暂无数据");
        refresh_foot_text.setText("");
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
            //手动滚动后，才开启滚动事件
            if(!canScroll){
                canScroll=true;
            }
    }
    @Override
    public boolean removeHeaderView(View view){
        if(view!=null){
            super.removeHeaderView(view);
        }else{
            removeHeaderView(refreshHeader);
        }
        return true;
    }

    @Override
    public boolean removeFooterView(View view){
        if(view!=null){
            super.removeFooterView(view);
        }else{
            removeFooterView(refreshFoot);
        }
        return true;
    }

    @Override
    public void addFooterView(View view){
        if(view!=null){
            super.addFooterView(view);
        }else{
            addFooterView(refreshFoot);
        }

    }
    @Override
    public void addHeaderView(View view){
        if(view!=null){
            super.addHeaderView(view);
        }else{
            addHeaderView(refreshHeader);
        }

    }



    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            if(!canScroll){
                return;
            }
            //刷新状态下不响应
            if(isRefreshing){
                return;
            }
            int currentFreshHeight=refreshHeader.getBottom();
            if(currentFreshHeight >= refresh_height) {
                refresh_text.setText("松开以刷新");
                if(currentState != STATE_UP_RELEASE){
                    currentState = STATE_DOWN_RELEASE;
                }

            }else{
                refresh_text.setText("下拉刷新");
                currentState = STATE_DOWN_TO_REFRESH;
            }

            int currentFootTop=refreshFoot.getTop();
            if(currentFootTop>0) {
                if (fixedFootTop == null) {
                    fixedFootTop = currentFootTop;//上拉加载第一次出现时记下位置
                } else {
                    if (fixedFootTop - currentFootTop < refresh_foot_height-adjustment) {
                        refresh_foot_text.setText("上拉加载更多");
                        currentState = STATE_UP_TO_REFRESH;
                    } else if(fixedFootTop != currentFootTop){
                        refresh_foot_text.setText("松开以加载更多");
                        if(currentState != STATE_DOWN_RELEASE){
                            currentState = STATE_UP_RELEASE;
                        }

                    }
                }
            }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch(ev.getAction()){
            case MotionEvent.ACTION_UP:
                if(currentState == STATE_DOWN_RELEASE){
                        isRefreshing=true;
                        refresh(1);
                        currentState = STATE_NORMAL;
                        refreshHeader.setBottom(0);
                        isRefreshing=false;
                }else  if(currentState == STATE_UP_RELEASE){
                        isRefreshing=true;
                        refresh(2);
                        currentState = STATE_NORMAL;
                        refreshFoot.setTop(0);
                        isRefreshing=false;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    private void refresh(int type){
        //通知监听器刷新适配器数据
        adapterRefreshListener.refreshData(type);
    }
}
