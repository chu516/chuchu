package bjx.com.siji.model;

import java.util.List;

/**
 * Created by jqc on 2017/11/7.
 */

public class DriverNearbyModel extends BaseModel {


    /**
     * status : 200
     * msg : Ok
     * fid : 2
     * result : [{"distance":1776,"ldata":{"sj_id":1,"number":"FP1","mobile":"18365143899","sb_long":"117.01063","sb_lat":"30.61365","name":"李建","head_pic":"","nickname":"","sbst":2,"dj_status":"busy"}}]
     */

    private int status;
    private String msg;
    private String fid;
    private List<ResultBean> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * distance : 1776
         * ldata : {"sj_id":1,"number":"FP1","mobile":"18365143899","sb_long":"117.01063","sb_lat":"30.61365","name":"李建","head_pic":"","nickname":"","sbst":2,"dj_status":"busy"}
         */

        private int distance;
        private LdataBean ldata;

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public LdataBean getLdata() {
            return ldata;
        }

        public void setLdata(LdataBean ldata) {
            this.ldata = ldata;
        }

        public static class LdataBean {
            /**
             * sj_id : 1
             * number : FP1
             * mobile : 18365143899
             * sb_long : 117.01063
             * sb_lat : 30.61365
             * name : 李建
             * head_pic :
             * nickname :
             * sbst : 2
             * dj_status : busy
             */

            private int sj_id;
            private String number;
            private String mobile;
            private String sb_long;
            private String sb_lat;
            private String name;
            private String head_pic;
            private String nickname;
            private int sbst;
            private String dj_status;

            public int getSj_id() {
                return sj_id;
            }

            public void setSj_id(int sj_id) {
                this.sj_id = sj_id;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
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

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getSbst() {
                return sbst;
            }

            public void setSbst(int sbst) {
                this.sbst = sbst;
            }

            public String getDj_status() {
                return dj_status;
            }

            public void setDj_status(String dj_status) {
                this.dj_status = dj_status;
            }
        }
    }
}
