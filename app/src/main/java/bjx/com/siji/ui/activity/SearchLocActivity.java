package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import bjx.com.siji.R;
import bjx.com.siji.adapter.SearchLocAdapter;
import bjx.com.siji.utils.SPUtils;

import java.util.List;


public class SearchLocActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    ListView mListView;
    SearchLocAdapter mAdapter;
    ImageView mIVBack;
    EditText mETSearch;
    TextView mTVSearch;
    TextView mTVLoadMore;

    PoiSearch mPoiSearch;
    String searchCity;

    int mPageNum = 0;
    int mPageCount = 10;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_searchloc);
        mIVBack = getViewById(R.id.searchloc_back);
        mETSearch = getViewById(R.id.searchloc_et_search);
        mTVSearch = getViewById(R.id.searchloc_tv_search);
        mListView = getViewById(R.id.searchloc_lv);
        mTVLoadMore = getViewById(R.id.searchloc_loadmore);

        mAdapter = new SearchLocAdapter(mApp, R.layout.lv_item_searchloc);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
        mTVSearch.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mTVLoadMore.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        List<PoiInfo> mData = getIntent().getParcelableArrayListExtra("list");
        mAdapter.setData(mData);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo poiInfo = mAdapter.getItem(position);
//        setResult(200, new Intent().putExtra("endPoiInfo",poiInfo));
        startActivity(new Intent(mApp, MainActivity.class)
                .putExtra("tabindex", 2)
                .putExtra("endPoiInfo", poiInfo));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchloc_tv_search:
                searchCity = SPUtils.get(mApp, "loccity", "中国").toString();
                mPageNum = 0;
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(mPageNum));
                break;
            case R.id.searchloc_loadmore:
                mTVLoadMore.setText("正在加载..");
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(++mPageNum));
                break;
            case R.id.searchloc_back:
                finish();
                break;
            default:break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result.getSuggestCityList() == null) {
                if (result.getCurrentPageNum() > 0) {
                    mAdapter.addMoreData(result.getAllPoi());
                } else {
                    mAdapter.setData(result.getAllPoi());
                }
                mPageNum = result.getCurrentPageNum();
                if (result.getCurrentPageNum() < result.getTotalPageNum() -1) {
                    mTVLoadMore.setVisibility(View.VISIBLE);
                } else {
                    mTVLoadMore.setVisibility(View.GONE);
                }
                mTVLoadMore.setText("加载更多..");
            } else {
                mPageNum = 0;
                searchCity = "中国";
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(searchCity)
                        .keyword(mETSearch.getText().toString().trim())
                        .pageCapacity(mPageCount)
                        .pageNum(mPageNum));

            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

}