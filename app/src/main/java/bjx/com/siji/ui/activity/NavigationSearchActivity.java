package bjx.com.siji.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import bjx.com.siji.base.Constant;
import bjx.com.siji.utils.SPUtils;

import java.util.List;

public class NavigationSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    /**
     * =========导航搜索==========
     */
    public RelativeLayout mRLSearch;
    ListView mListView;
    SearchLocAdapter mAdapter;
    ImageView mIVBack;
    EditText mETSearch;
    TextView mTVSearch;
    TextView mTVLoadMore;

    PoiSearch mPoiSearch;
    String searchCity;
    private PoiInfo poiInfo; // 设置的导航终点poi

    int mPageNum = 0;
    int mPageCount = 10;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_navigation_search);

//        LayoutInflater inflater=(LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//        mContentView = inflater.inflate(R.layout.fragment_map2, null);

        mRLSearch = getViewById(R.id.main_rl_search);
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
                mRLSearch.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        poiInfo = mAdapter.getItem(position);

        if (poiInfo != null) {

            Intent intent = new Intent(NavigationSearchActivity.this, NavigationMapActivity.class);
            intent.putExtra(Constant.CONFIG_INTENT_ADDRESS_LAT, poiInfo.location.latitude);
            intent.putExtra(Constant.CONFIG_INTENT_ADDRESS_LNG, poiInfo.location.longitude);
            setResult(RESULT_OK, intent);
            NavigationSearchActivity.this.finish();

            showToast("成功设置终点位置，已可以开始导航");
         }

        //        setResult(200, new Intent().putExtra("endPoiInfo",poiInfo));
//        startActivity(new Intent(mApp, MainActivity.class)
//                .putExtra("tabindex", 2)
//                .putExtra("endPoiInfo", poiInfo));
//        finish();
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
                if (result.getCurrentPageNum() < result.getTotalPageNum() - 1) {
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
