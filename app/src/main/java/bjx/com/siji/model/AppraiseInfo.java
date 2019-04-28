package bjx.com.siji.model;
/**
 * 客户评价信息封装实体类
 * Created by Administrator on 2017/11/27.
 */

public class AppraiseInfo  extends BaseModel{
    public  String customer_phone;//客户手机号码
    public String grade;//等级
    public String appraise_time;//评价时间
    public String appraise_content;//评价内容
    public AppraiseInfo(String customer_phone,String grade,String appraise_time,String appraise_content){
        this.customer_phone=customer_phone;
        this.grade=grade;
        this.appraise_time=appraise_time;
        this.appraise_content=appraise_content;
    }

}
