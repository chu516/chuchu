package bjx.com.siji.model;


public class ResponseModel<M> extends BaseModel {

    int s_status;
    String msg;
    M result;

    public ResponseModel() {
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

    public void setMsg(String s_msg) {
        this.msg = msg;
    }

    public M getResult() {
        return result;
    }

    public void setResult(M result) {
        this.result = result;
    }
}
