package bjx.com.siji.model;

/**
 * EventBus相关
 * Created by ssm on 2015/12/30.
 */
public class EventBean {

    public String action;
    public String msg;

    public int orderId;
    public double orderPrice;
    private int orderSum;

    public static final String EVENT_END = "EVENT_END";
    public static final String EVENT_EMPTY = "EVENT_EMPTY";
    public static final String EVENT_REFRESH_UI = "EVENT_REFRESH_UI";
    public static final String EVENT_GET_DATA = "EVENT_GET_DATA";



    //对话框
    public static final String EVENT_DISLOG_CANCLE = "EVENT_DISLOG_CANCLE";
     public static final String EVENT_DISLOG_CONFIRM = "EVENT_DISLOG_CONFIRM";



    public static final String EVENT_CONFIRM_DELETE = "EVENT_CONFIRM_DELETE";
    public static final String EVENT_CONFIRM_DELETE_ORDER = "EVENT_CONFIRM_DELETE_ORDER";


    public static final String EVENT_PAY_SUCCESS = "EVENT_PAY_SUCCESS";

    public static final String EVENT_SET_PAY_PWD_SUCCESS = "EVENT_SET_PAY_PWD_SUCCESS";

    public static final String EVENT_WXPAY_SUCCESS = "EVENT_WXPAY_SUCCESS";
    public static final String EVENT_PAY_ERROR = "EVENT_PAY_ERROR";

    public static final String EVENT_GET_CACHE_CONTENT = "EVENT_GET_CACHE_CONTENT";

    public static final String REPORT_WRITE_END = "REPORT_WRITE_END";
    public static final String GET_BAIKE_INFO_END = "GET_BAIKE_INFO_END";
    public static final String WXENTRY_END = "WXENTRY_END";
    public static final String WXENTRY_CANCEL = "WXENTRY_CANCEL";
    public static final String WXENTRY_REFUSE = "WXENTRY_REFUSE";
    public static final String EVENT_WX_SHARE_SUCCESS = "EVENT_WX_SHARE_SUCCESS";
    public static final String EVENT_WAIT_FINISH = "EVENT_WAIT_FINISH";
    public static final String EVENT_OPEN_CAMERE = "EVENT_OPEN_CAMERE";

    //退出登录  exitAccountDialog
    public static final String EVENT_EXIT_ACCOUNT = "EVENT_EXIT_ACCOUNT";


    //确认删除购物车

    public static final String CONFIRM_ORDER_DELETE = "CONFIRM_ORDER_DELETE";
    public static final String CONFIRM_ORDER_CANCEL = "CONFIRM_ORDER_CANCEL";
    public static final String CONFIRM_ORDER_RECEPT = "CONFIRM_ORDER_RECEPT";



    //提现成功
    public static final String EVENT_CONFIRM_WITHDRAW_INFO = "EVENT_CONFIRM_TRANSFER_ACCOUNT_INFO";//充值卡充值成功

    public static final String EVENT_CONFIRM_GO_UPGRADE = "EVENT_CONFIRM_GO_UPGRADE";

    public static final String EVENT_CONFIRM_GO_SET_PERMISSION = "EVENT_CONFIRM_GO_SET_PERMISSION";

    public static final String EVENT_CONFIRM_APPLY_AGAIN = "EVENT_CONFIRM_APPLY_AGAIN";

    public static final String EVENT_CONFIRM_APPLY_REPEAL = "EVENT_CONFIRM_APPLY_REPEAL";


    public static final String EVENT_UPLOAD_FINISH = "EVENT_UPLOAD_FINISH";
    public static final String EVENT_CLOSE_WAIT_DIALOG = "EVENT_CLOSE_WAIT_DIALOG";

    public EventBean() {
    }

    public EventBean(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public double getOrderPrice() {
        return orderPrice;
    }

    public int getOrderSum() {
        return orderSum;
    }

    public int getOrderId() {
        return orderId;
    }


    public EventBean(String action, String msg) {
        this.action = action;
        this.msg = msg;
    }


    public EventBean(String action, int orderId, double orderPrice, int orderSum) {
        this.action = action;
        this.orderId = orderId;
        this.orderPrice = orderPrice;
        this.orderSum = orderSum;
    }

 }
