package bjx.com.siji.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import bjx.com.siji.R;
import bjx.com.siji.contants.Constants;
import bjx.com.siji.interfaces.ItemMultClickListener;
import bjx.com.siji.model.DriverNearbyModel;

import java.util.List;


/*
 * 附近司机
 */
 public class NearbySJGvAdapter extends BaseAdapter {
    private Context mContext;
    private List<DriverNearbyModel.ResultBean> listData;
    private LayoutInflater layoutInflater;
    private ItemMultClickListener mItemClickListener;
    private Boolean isShowEvaluate = false;

    private int mEvaluate_flag = 0;


    public NearbySJGvAdapter(Context context, List<DriverNearbyModel.ResultBean> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.listData = list;
    }

    /*public GoodsItemLvAdapter(Context context, List<OrderDetailBean.DataEntity.GoodsEntity> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.listData = list;
    }*/

    public void addAll(List<DriverNearbyModel.ResultBean> info) {
        listData.addAll(info);
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemMultClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void clearAll() {
        listData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData != null ? listData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_index_favorite_product, null);

            holder.item_favorite_iv = (ImageView) convertView.findViewById(R.id.item_favorite_iv);

            holder.item_favorite_title_iv = (TextView) convertView.findViewById(R.id.item_favorite_title_iv);
            holder.item_favorite_mobile_iv = (TextView) convertView.findViewById(R.id.item_favorite_mobile_iv);
            holder.item_status_tv = (TextView) convertView.findViewById(R.id.item_status_tv);

            holder.item_favorite_distance_iv = (TextView) convertView.findViewById(R.id.item_favorite_distance_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String url = listData.get(position).getLdata().getHead_pic();
        if (TextUtils.isEmpty(url)) {
            url = "";
        } else {
            url = listData.get(position).getLdata().getHead_pic();
        }

        Glide.with(mContext).load(Constants.BASE_URL + url)
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .fallback(R.mipmap.icon_default_avatar)
                .into(holder.item_favorite_iv);

        holder.item_favorite_title_iv.setText(listData.get(position).getLdata().getName());
        holder.item_favorite_mobile_iv.setText(listData.get(position).getLdata().getMobile());

        holder.item_favorite_distance_iv.setText("距离" + listData.get(position).getDistance() + "公里");

        if ("normal".equals(listData.get(position).getLdata().getDj_status())) {
            holder.item_status_tv.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.item_status_tv.setText("等待中");
        } else {
            holder.item_status_tv.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.item_status_tv.setText("代驾中");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder.item_favorite_title_iv, position);
                }
            }
        });

        return convertView;
    }

    public class ViewHolder {

        public ImageView item_favorite_iv;
        public TextView item_favorite_title_iv;
        public TextView item_favorite_mobile_iv;
        public TextView item_status_tv;
        public TextView item_favorite_distance_iv;
    }

}
