package bjx.com.siji.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/12/8.
 */

public class OrderList extends BaseModel implements Parcelable {
    /*司机确定接单位置*/
    public String getOrderLat;
    public String getOrderLong;


    private String add_time;
    private String address_end;
    private String address_st;
    private String end_lat;
    private String end_long;
    private String kh_ext;
    private String order_id;
    private String order_sn;
    private String negotiate;
    private String order_status;
    private String reject_list;
    private String st_lat;
    private String st_long;
    private String use_mobile;
    private String use_name;
    private String user_id;
    private String send_time;
    private String rest;
    private String leftTime;//倒计时剩余时间

    // 创建订单返回追加字段
    private String order_type;
    private String sj_id;
    private String udt;
    private String ext;
    private String f_id;
    private String pd_mode;
    private String order_system;
    private String user_mobile;
    private String username;

    // 获取订单详情返回追加
    private String pay_status;
    private String travel_time;
    private String mileage;
    private String st_time;
    private String end_time;
    private String mileage_price;
    private String safe_kh;
    private String wait_time;
    private String wait_price;
    private String total_price;

    private String paidan;

    public OrderList() {
    }

    public OrderList(String add_time, String address_end, String address_st, String end_lat,
                     String end_long, String kh_ext, String order_id, String order_sn,
                     String negotiate, String order_status, String reject_list, String st_lat,
                     String st_long, String use_mobile, String use_name, String user_id, String send_time,
                     String rest, String leftTime, String order_type, String sj_id,
                     String udt, String ext, String f_id, String pd_mode, String order_system,
                     String user_mobile, String username, String pay_status, String travel_time,
                     String mileage, String st_time, String end_time, String mileage_price,
                     String safe_kh, String wait_time, String wait_price, String total_price, String paidan) {
        this.add_time = add_time;
        this.address_end = address_end;
        this.address_st = address_st;
        this.end_lat = end_lat;
        this.end_long = end_long;
        this.kh_ext = kh_ext;
        this.order_id = order_id;
        this.order_sn = order_sn;
        this.negotiate = negotiate;
        this.order_status = order_status;
        this.reject_list = reject_list;
        this.st_lat = st_lat;
        this.st_long = st_long;
        this.use_mobile = use_mobile;
        this.use_name = use_name;
        this.user_id = user_id;
        this.send_time = send_time;
        this.rest = rest;
        this.leftTime = leftTime;
        this.order_type = order_type;
        this.sj_id = sj_id;
        this.udt = udt;
        this.ext = ext;
        this.f_id = f_id;
        this.pd_mode = pd_mode;
        this.order_system = order_system;
        this.user_mobile = user_mobile;
        this.username = username;
        this.pay_status = pay_status;
        this.travel_time = travel_time;
        this.mileage = mileage;
        this.st_time = st_time;
        this.end_time = end_time;
        this.mileage_price = mileage_price;
        this.safe_kh = safe_kh;
        this.wait_time = wait_time;
        this.wait_price = wait_price;
        this.total_price= total_price;

        this.paidan= paidan;



    }

    protected OrderList(Parcel in) {
        add_time = in.readString();
        address_end = in.readString();
        address_st = in.readString();
        end_lat = in.readString();
        end_long = in.readString();
        kh_ext = in.readString();
        order_id = in.readString();
        order_sn = in.readString();
        negotiate = in.readString();
        order_status = in.readString();
        reject_list = in.readString();
        st_lat = in.readString();
        st_long = in.readString();
        use_mobile = in.readString();
        use_name = in.readString();
        user_id = in.readString();
        send_time = in.readString();
        rest = in.readString();
        leftTime = in.readString();

        order_type = in.readString();
        sj_id = in.readString();
        udt = in.readString();
        ext = in.readString();
        f_id = in.readString();
        pd_mode = in.readString();
        order_system = in.readString();
        user_mobile = in.readString();
        username = in.readString();

        pay_status = in.readString();
        travel_time = in.readString();
        mileage = in.readString();
        st_time = in.readString();
        end_time = in.readString();
        mileage_price = in.readString();
        safe_kh = in.readString();
        wait_time = in.readString();
        wait_price = in.readString();
        total_price = in.readString();

        getOrderLat= in.readString();
        getOrderLong= in.readString();

        paidan= in.readString();

     }

    public static final Creator<OrderList> CREATOR = new Creator<OrderList>() {
        @Override
        public OrderList createFromParcel(Parcel in) {
            return new OrderList(in);
        }

        @Override
        public OrderList[] newArray(int size) {
            return new OrderList[size];
        }
    };

    @Override
    public String toString() {
        return "OrderList{" +
                "add_time='" + add_time + '\'' +
                ", address_end='" + address_end + '\'' +
                ", address_st='" + address_st + '\'' +
                ", end_lat='" + end_lat + '\'' +
                ", end_long='" + end_long + '\'' +
                ", kh_ext='" + kh_ext + '\'' +
                ", order_id='" + order_id + '\'' +
                ", order_sn='" + order_sn + '\'' +
                ", negotiate='" + negotiate + '\'' +
                ", order_status='" + order_status + '\'' +
                ", reject_list='" + reject_list + '\'' +
                ", st_lat='" + st_lat + '\'' +
                ", st_long='" + st_long + '\'' +
                ", use_mobile='" + use_mobile + '\'' +
                ", use_name='" + use_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", send_time='" + send_time + '\'' +
                ", rest='" + rest + '\'' +
                ", leftTime='" + leftTime + '\'' +

                ", order_type='" + order_type + '\'' +
                ", sj_id='" + sj_id + '\'' +
                ", udt='" + udt + '\'' +
                ", ext='" + ext + '\'' +
                ", f_id='" + f_id + '\'' +
                ", pd_mode='" + pd_mode + '\'' +
                ", order_system='" + order_system + '\'' +
                ", user_mobile='" + user_mobile + '\'' +
                ", username='" + username + '\'' +

                ", pay_status='" + pay_status + '\'' +
                ", travel_time='" + travel_time + '\'' +
                ", mileage='" + mileage + '\'' +
                ", st_time='" + st_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", mileage_price='" + mileage_price + '\'' +
                ", safe_kh='" + safe_kh + '\'' +
                ", wait_time='" + wait_time + '\'' +
                ", wait_price='" + wait_price + '\'' +
                ", total_price='" + total_price + '\'' +
                ", getOrderLat='" + getOrderLat + '\'' +
                ", getOrderLong='" + getOrderLong + '\'' +

                ", paidan='" + paidan + '\'' +

                '}';
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getAddress_end() {
        return address_end;
    }

    public void setAddress_end(String address_end) {
        this.address_end = address_end;
    }

    public String getAddress_st() {
        return address_st;
    }

    public void setAddress_st(String address_st) {
        this.address_st = address_st;
    }

    public String getEnd_lat() {
        return end_lat;
    }

    public void setEnd_lat(String end_lat) {
        this.end_lat = end_lat;
    }

    public String getEnd_long() {
        return end_long;
    }

    public void setEnd_long(String end_long) {
        this.end_long = end_long;
    }

    public String getKh_ext() {
        return kh_ext;
    }

    public void setKh_ext(String kh_ext) {
        this.kh_ext = kh_ext;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getNegotiate() {
        return negotiate;
    }

    public void setNegotiate(String negotiate) {
        this.negotiate = negotiate;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getReject_list() {
        return reject_list;
    }

    public void setReject_list(String reject_list) {
        this.reject_list = reject_list;
    }

    public String getSt_lat() {
        return st_lat;
    }

    public void setSt_lat(String st_lat) {
        this.st_lat = st_lat;
    }

    public String getSt_long() {
        return st_long;
    }

    public void setSt_long(String st_long) {
        this.st_long = st_long;
    }

    public String getUse_mobile() {
        return use_mobile;
    }

    public void setUse_mobile(String use_mobile) {
        this.use_mobile = use_mobile;
    }

    public String getUse_name() {
        return use_name;
    }

    public void setUse_name(String use_name) {
        this.use_name = use_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    public String getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(String leftTime) {
        this.leftTime=leftTime;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getSj_id() {
        return sj_id;
    }

    public void setSj_id(String sj_id) {
        this.sj_id = sj_id;
    }

    public String getUdt() {
        return udt;
    }

    public void setUdt(String udt) {
        this.udt = udt;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getPd_mode() {
        return pd_mode;
    }

    public void setPd_mode(String pd_mode) {
        this.pd_mode = pd_mode;
    }

    public String getOrder_system() {
        return order_system;
    }

    public void setOrder_system(String order_system) {
        this.order_system = order_system;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getTravel_time() {
        return travel_time;
    }

    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getSt_time() {
        return st_time;
    }

    public void setSt_time(String st_time) {
        this.st_time = st_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getMileage_price() {
        return mileage_price;
    }

    public void setMileage_price(String mileage_price) {
        this.mileage_price = mileage_price;
    }

    public String getSafe_kh() {
        return safe_kh;
    }

    public void setSafe_kh(String safe_kh) {
        this.safe_kh = safe_kh;
    }

    public String getWait_time() {
        return wait_time;
    }

    public void setWait_time(String wait_time) {
        this.wait_time = wait_time;
    }

    public String getWait_price() {
        return wait_price;
    }

    public void setWait_price(String wait_price) {
        this.wait_price = wait_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getPaidan() {
        return paidan;
    }

    public void setPaidan(String paidan) {
        this.paidan = paidan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(add_time);
        dest.writeString(address_end);
        dest.writeString(address_st);
        dest.writeString(end_lat);
        dest.writeString(end_long);
        dest.writeString(kh_ext);
        dest.writeString(order_id);
        dest.writeString(order_sn);
        dest.writeString(negotiate);
        dest.writeString(order_status);
        dest.writeString(reject_list);
        dest.writeString(st_lat);
        dest.writeString(st_long);
        dest.writeString(use_mobile);
        dest.writeString(use_name);
        dest.writeString(user_id);
        dest.writeString(send_time);
        dest.writeString(rest);
        dest.writeString(leftTime);

        dest.writeString(order_type);
        dest.writeString(sj_id);
        dest.writeString(udt);
        dest.writeString(ext);
        dest.writeString(f_id);
        dest.writeString(pd_mode);
        dest.writeString(order_system);
        dest.writeString(user_mobile);
        dest.writeString(username);

        dest.writeString(pay_status);
        dest.writeString(travel_time);
        dest.writeString(mileage);
        dest.writeString(st_time);
        dest.writeString(end_time);
        dest.writeString(mileage_price);
        dest.writeString(safe_kh);
        dest.writeString(wait_time);
        dest.writeString(wait_price);
        dest.writeString(total_price);
        dest.writeString(getOrderLat);
        dest.writeString(getOrderLong);

        dest.writeString(paidan);

    }
}
