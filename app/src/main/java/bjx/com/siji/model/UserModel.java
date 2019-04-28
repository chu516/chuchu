package bjx.com.siji.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class UserModel extends BaseModel implements Parcelable, Serializable {

    String sj_id;
    String mobile;
    String name;
    String nickname;
    String number;
    String level;
    String head_pic;
    String user_money;
    String points;
    String status;
    String imei;
    String shenhe;
    String f_id;
    String alipay;
    String today;
    String month;
    String workState;//上下班状态，用于activity切换时保持状态
    String star;
    public String getSj_id() {
        return sj_id;
    }

    public void setSj_id(String sj_id) {
        this.sj_id = sj_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getUser_money() {
        return user_money;
    }

    public void setUser_money(String user_money) {
        this.user_money = user_money;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getShenhe() {
        return shenhe;
    }

    public void setShenhe(String shenhe) {
        this.shenhe = shenhe;
    }

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Creator<UserModel>() {
        public UserModel createFromParcel(Parcel source) {
            UserModel model = new UserModel();
            model.sj_id = source.readString();
            model.mobile = source.readString();
            model.name = source.readString();
            model.nickname = source.readString();
            model.number = source.readString();
            model.level = source.readString();
            model.head_pic = source.readString();
            model.user_money = source.readString();
            model.points = source.readString();
            model.status = source.readString();
            model.imei = source.readString();
            model.shenhe = source.readString();
            model.f_id = source.readString();
            model.alipay = source.readString();
            model.today = source.readString();
            model.month = source.readString();
            model.workState = source.readString();
            model.star = source.readString();
            return model;
        }
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sj_id);
        dest.writeString(mobile);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeString(number);
        dest.writeString(level);
        dest.writeString(head_pic);
        dest.writeString(user_money);
        dest.writeString(points);
        dest.writeString(status);
        dest.writeString(imei);
        dest.writeString(shenhe);
        dest.writeString(f_id);
        dest.writeString(alipay);
        dest.writeString(today);
        dest.writeString(month);
        dest.writeString(workState);
        dest.writeString(star);
    }

    @Override
    public String toString() {
        return "UserModel [sj_id=" + sj_id
                + ", mobile=" + mobile
                + ", name=" + name
                + ", nickname=" + nickname
                + ", number=" + number
                + ", nickname=" + nickname
                + ", level=" + level
                + ", head_pic=" + head_pic
                + ", user_money=" + user_money
                + ", points=" + points
                + ", status=" + status
                + ", imei=" + imei
                + ", shenhe=" + shenhe
                + ", f_id=" + f_id
                + ", alipay=" + alipay
                + ", today=" + today
                + ", month=" + month
                + ", workState=" + workState
                + ", star=" + star
                + "]";
    }
}
