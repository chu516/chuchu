package bjx.com.siji.model;

import java.util.List;

public class ResponseListModel<M> extends BaseModel {

    int s_status;
    String msg;
    List<M> result;

    public ResponseListModel() {
    }

    public int getS_status() {
        return s_status;
    }

    public void setS_status(int s_status) {
        this.s_status = s_status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<M> getResult() {
        return result;
    }

    public void setResult(List<M> result) {
        this.result = result;
    }
}
