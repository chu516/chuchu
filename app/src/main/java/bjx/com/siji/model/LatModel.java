package bjx.com.siji.model;

/**
 * Created by Administrator on 2017/12/19.
 */

public class LatModel extends BaseModel {

    String longitude;
    String latitude;

    public LatModel() {
    }

    public LatModel(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
