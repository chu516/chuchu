package bjx.com.siji.model;

/**
 * Created by Administrator on 2017/12/19.
 */

public class LatLngDisTimeModel extends BaseModel {

    double driveDistance;
    String currentTime;


    public LatLngDisTimeModel(double driveDistance, String currentTime) {
        this.driveDistance = driveDistance;
        this.currentTime = currentTime;
    }

    public double getDriveDistance() {
        return driveDistance;
    }

    public void setDriveDistance(double driveDistance) {
        this.driveDistance = driveDistance;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
