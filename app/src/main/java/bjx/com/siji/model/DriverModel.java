package bjx.com.siji.model;

/**
 * Created by jqc on 2017/11/7.
 */

public class DriverModel extends BaseModel {

    //师傅ID，师傅呢称，师傅头像，师傅的经度，纬度
    String sj_id;
    String name;

    String head_pic;
    String sb_long;
    String sb_lat;

    String number;

    String dj_status;

    public String getDj_status() {
        return dj_status;
    }

    public void setDj_status(String dj_status) {
        this.dj_status = dj_status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSj_id() {
        return sj_id;
    }

    public void setSj_id(String sj_id) {
        this.sj_id = sj_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getSb_long() {
        return sb_long;
    }

    public void setSb_long(String sb_long) {
        this.sb_long = sb_long;
    }

    public String getSb_lat() {
        return sb_lat;
    }

    public void setSb_lat(String sb_lat) {
        this.sb_lat = sb_lat;
    }
}
