package bjx.com.siji.adapter;

import android.content.Context;

import com.baidu.mapapi.search.core.PoiInfo;
import bjx.com.siji.R;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * Created by jqc on 2017/10/27.
 */

public class SearchLocAdapter extends BGAAdapterViewAdapter<PoiInfo> {

    public SearchLocAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, PoiInfo model) {
        helper.setText(R.id.item_tv_name, model.name);
        helper.setText(R.id.item_tv_address, model.address);
    }
}
