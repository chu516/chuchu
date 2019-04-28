package bjx.com.siji.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/9.
 */

public class ChargeModel extends BaseModel implements Parcelable, Serializable {

    private String sprice;
    private String uprice;
    private String  kilometres;
    private String wait_time;
    private String wait_money;
    private String kf_tel;
    private String safe_kh;

    public ChargeModel() {

    }

    public ChargeModel(String sprice, String uprice, String kilometres, String wait_time, String wait_money, String kf_tel, String safe_kh) {
        this.sprice = sprice;
        this.uprice = uprice;
        this.kilometres = kilometres;
        this.wait_time = wait_time;
        this.wait_money = wait_money;
        this.kf_tel = kf_tel;
        this.safe_kh = safe_kh;
    }

    public String getSprice() {
        return sprice;
    }

    public void setSprice(String sprice) {
        this.sprice = sprice;
    }

    public String getUprice() {
        return uprice;
    }

    public void setUprice(String uprice) {
        this.uprice = uprice;
    }

    public String getKilometres() {
        return kilometres;
    }

    public void setKilometres(String kilometres) {
        this.kilometres = kilometres;
    }

    public String getWait_time() {
        return wait_time;
    }

    public void setWait_time(String wait_time) {
        this.wait_time = wait_time;
    }

    public String getWait_money() {
        return wait_money;
    }

    public void setWait_money(String wait_money) {
        this.wait_money = wait_money;
    }

    public String getKf_tel() {
        return kf_tel;
    }

    public void setKf_tel(String kf_tel) {
        this.kf_tel = kf_tel;
    }

    public String getSafe_kh() {
        return safe_kh;
    }

    public void setSafe_kh(String safe_kh) {
        this.safe_kh = safe_kh;
    }

    protected ChargeModel(Parcel in) {
    }

    @Override
    public String toString() {
        return "ChargeModel{" +
                "sprice=" + sprice +
                ", uprice=" + uprice +
                ", kilometres=" + kilometres +
                ", wait_time=" + wait_time +
                ", wait_money=" + wait_money +
                ", kf_tel=" + kf_tel +
                ", safe_kh=" + safe_kh +
                '}';
    }

    public static final Creator<ChargeModel> CREATOR = new Creator<ChargeModel>() {
        @Override
        public ChargeModel createFromParcel(Parcel in) {
            return new ChargeModel(in);
        }

        @Override
        public ChargeModel[] newArray(int size) {
            return new ChargeModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sprice);
        dest.writeString(uprice);
        dest.writeString(kilometres);
        dest.writeString(wait_time);
        dest.writeString(wait_money);
        dest.writeString(kf_tel);
        dest.writeString(safe_kh);
    }
}
