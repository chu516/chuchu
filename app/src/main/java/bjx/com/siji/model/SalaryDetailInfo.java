package bjx.com.siji.model;

/**
 * 收入明细列表Item实体
 * Created by Administrator on 2017/11/29.
 */

public class SalaryDetailInfo extends BaseModel {
        public String orderNo;//订单编号
        public String orderType;//收入类型
        public String orderTotal;//收入金额
        public String orderTime;//收入时间
        public SalaryDetailInfo(String orderNo,String orderType,String orderTotal,String orderTime){
            this.orderNo=orderNo;
            this.orderType=orderType;
            this.orderTotal=orderTotal;
            this.orderTime=orderTime;
        }
}
