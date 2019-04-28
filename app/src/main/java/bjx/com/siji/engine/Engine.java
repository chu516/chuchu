package bjx.com.siji.engine;


import bjx.com.siji.model.BaseModel;
import bjx.com.siji.model.ResponseListModel;
import bjx.com.siji.model.ResponseModel;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface Engine {

    @Multipart
    @POST("index.php?s=/home/File/uploadPicture")
        // 上传图片
    Call<ResponseModel<String>> test(@Part MultipartBody.Part image);

    @POST("index.php?s=/home/Article/index")
    Call<ResponseListModel<BaseModel>> test2(@Query("uid") String uid);

    @GET("index.php/Api/Siji/login")
        // 登录 mobile+base+passwd
    Call<ResponseBody> login(@Query("mobile") String mobile, @Query("passwd") String passwd, @Query("key") String key);


    // 定位上传 mobile+base+passwd
    @GET("/index.php/Api/Index/location_sj")
    Call<ResponseBody> pushLocation(@Query("sj_id") String sj_id, @Query("longitude") double longitude, @Query("latitude") double latitude);

    @GET("/index.php/Api/Api/getCharge")
        // 收费列表 mobile+base+passwd
    Call<ResponseBody> gaveMoneylist(@Query("sj_id") String sj_id);

    @GET("index.php/Api/Siji/send_sms_find_pswd")
        // 密码重置(发送验证码) mobile+base+code
    Call<ResponseBody> sendIdenCodeForResetPwd(@Query("code") String code, @Query("mobile") String mobile, @Query("key") String key);

    @GET("index.php/Api/Siji/password_reset")
        // 密码重置(重置密码) mobile+base+code+passwd
    Call<ResponseBody> resetPwd(@Query("mobile") String mobile, @Query("passwd") String passwd, @Query("code") String code, @Query("key") String key);

    @GET("/index.php/Api/Siji/users_comments_lists")
// 客户评价司机的列表(sj_id+page+count)
    Call<ResponseBody> getComments(@Query("sj_id") String sj_id, @Query("page") String page, @Query("count") String count);

    @GET("/index.php/Api/Siji/push_order_list")
// 订单列表(sj_id+key)
    Call<ResponseBody> getOrderList(@Query("sj_id") String sj_id, @Query("key") String key);


    @GET("/index.php/Api/Siji/start_driving")
// 开始代驾(sj_id+key)
    Call<ResponseBody> startDrive(@Query("sj_id") String sj_id, @Query("order_id") String order_id, @Query("address_st") String address_st,
                                  @Query("st_long") String st_long, @Query("st_lat") String st_lat, @Query("key") String key);


    @GET("/index.php/Api/Siji/accept_order")
// 接单(sj_id+key)
    Call<ResponseBody> ReceiveOrderList(@Query("sj_id") String sj_id, @Query("order_id") String order_id, @Query("key") String key);

    @GET("/index.php/Api/Siji/reject_order")
// 拒绝订单(sj_id+key)
    Call<ResponseBody> RefuseOrderList(@Query("sj_id") String sj_id, @Query("order_id") String order_id, @Query("key") String key);

    @GET("/index.php/Api/Siji/order_settlement")
// 订单结算(sj_id+key)
    Call<ResponseBody> payMoneyWay(@Query("sj_id") String sj_id, @Query("order_id") String order_id, @Query("pay_type") String pay_type, @Query("key") String key);

    @GET("/index.php/Api/Siji/add_order")
        // 创建订单 sj_id+mobile+address_st+st_long+st_lat+ext+province+city+district+address_end+end_long
    Call<ResponseBody> create_order(@Query("sj_id") String sj_id, @Query("mobile") String phone, @Query("address_st") String address_st,
                                    @Query("st_long") String st_long, @Query("st_lat") String st_lat, @Query("address_end") String address_end,
                                    @Query("end_long") String end_long, @Query("end_lat") String end_lat, @Query("ext") String ext, @Query("province") String province,
                                    @Query("city") String city, @Query("district") String district, @Query("key") String key);


    @GET("/index.php/Api/Siji/end_driving")
        // 完成订单 sj_id+mobile+address_st+st_long+st_lat+ext+province+city+district+address_end+end_long
    Call<ResponseBody> EndDrivr(@Query("sj_id") String sj_id,  @Query("start_mileage") String start_mileage,@Query("start_price") String start_price,
                                @Query("order_id") String order_id, @Query("address_end") String address_end,
                                @Query("end_long") String end_long, @Query("end_lat") String end_lat,
                                @Query("total_price") String total_price, @Query("travel_time") String travel_time,
                                @Query("mileage") String mileage, @Query("mileage_price") String mileage_price,
                                @Query("wait_time") String wait_time, @Query("wait_price") String wait_price,
                                @Query("safe_kh") String safe_kh, @Query("trajectory") String trajectory,
                                @Query("key") String key);


    @GET("index.php/Api/Kehu/order_del")
        // 客户删除订单 order_list+"Yxdj!@#1357" + user_id
    Call<ResponseBody> delOrder(@Query("order_list") String order_list, @Query("user_id") String user_id, @Query("key") String key);

    @GET("index.php/Api/Siji/order_list")
        // 订单列表 "Yxdj!@#1357" + user_id
    Call<ResponseBody> loadOrderList(@Query("sj_id") String sj_id, @Query("key") String key,
                                     @Query("page") String page, @Query("count") String count);

    @GET("/index.php/Api/Siji/start_off_work")
// 下班(sj_id+type 1 下班   2上班 +key)

//    @GET("/index.php/Api/Siji/start_off_work")// 下班(sj_id+type 1 下班   2上班 +key)

//    @GET("/index.php/Api/Siji/start_off_work")
// 下班(sj_id+type 1 下班   2上班 +key)

    Call<ResponseBody> offWork(@Query("sj_id") String sj_id, @Query("type") String type, @Query("key") String key);

    @GET("/index.php/Api/Siji/start_off_work")
// 上班(sj_id+type 1 下班   2上班 +key)
    Call<ResponseBody> startWork(@Query("sj_id") String sj_id, @Query("type") String type, @Query("key") String key,
                                 @Query("longitude") String longitude, @Query("latitude") String latitude,
                                 @Query("province") String province,
                                 @Query("city") String city,
                                 @Query("district") String district);


    /*
       司机收入明细[实际收入，非订单价格]
       sj_id 司机ID
       page  页码   默认为1
       count 每次返回数量 默认10
       type  1 当天   2本月    3全部
    */
    @GET("/index.php/Api/Siji/getRevenueList")
    Call<ResponseBody> getRevenueList(@Query("sj_id") String sj_id, @Query("page") String page, @Query("count") String count, @Query("type") String type, @Query("key") String key);


    @Multipart
    @POST("/index.php/Api/Api/upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part img);


    @GET("index.php/Api/Pay2/weixin_pay")
        // 微信充值 user_id+base+money
    Call<ResponseBody> reqPayForWX(@Query("sj_id") String sj_id, @Query("money") String money, @Query("key") String key);

    @GET("index.php/Api/Pay2/weixin_dep")
        // 微信充值订单查询 user_id+base+pay_sn
    Call<ResponseBody> reqPayQueryForWX(@Query("sj_id") String sj_id, @Query("pay_sn") String pay_sn, @Query("key") String key);

    @GET("index.php/Api/Pay2/siji_ali_pay")
        // 支付宝充值 user_id+base+money
    Call<ResponseBody> reqPayForZFB(@Query("sj_id") String sj_id, @Query("money") String money, @Query("key") String key);

    @GET("index.php/Api/Pay2/deposit_status")
        // 支付宝充值订单查询 user_id+base+pay_sn
    Call<ResponseBody> reqPayQueryForZFB(@Query("sj_id") String sj_id, @Query("pay_sn") String pay_sn, @Query("key") String key);

    @GET("index.php/Api/Kehu/get_user_info")
        // 信息获取 user_id+base
    Call<ResponseBody> getUserMoney(@Query("user_id") String user_id, @Query("key") String key);

    @GET("index.php/Api/Siji/get_cash")
        // 提现 user_id+base+money
    Call<ResponseBody> tixian(@Query("sj_id") String sj_id, @Query("money") String money, @Query("alipay") String alipay, @Query("key") String key);

    @GET("index.php/Api/Siji/order_detail")
        // 订单详细 order_id+"Yxdj!@#2468" + sj_id
    Call<ResponseBody> getOrderInfo(@Query("sj_id") String sj_id, @Query("order_id") String order_id,
                                    @Query("key") String key);

    // 订单详细 order_id+"Yxdj!@#2468" + sj_id
//    @GET("index.php/Api/Siji/order_detail")

    @GET("index.php/Api/Siji/order_detail")
     Call<ResponseBody> getOrderDetailInfo(@Query("sj_id") String sj_id,
                                          @Query("order_id") String order_id,
                                          @Query("start_s") String start_s,
                                          @Query("key") String key);

    @GET("index.php/Api/Kehu/order_cancel")
        // 客户取消订单 order_list+"Yxdj!@#1357" + user_id
    Call<ResponseBody> cancelOrder(@Query("order_list") String order_list, @Query("user_id") String user_id, @Query("key") String key);

    @GET("index.php/Api/Pay/kehu_order_pay")
        // 订单支付 type  //1 余额  2支付宝  3微信3微信
    Call<ResponseBody> reqOrderPay(@Query("user_id") String user_id, @Query("order_id") String order_id, @Query("type") String type, @Query("key") String key);

    @GET("index.php/Api/Pay/deposit_alipay_status")
        // 支付宝支付订单查询 user_id+"Yxdj!@#1357" +order_sn
    Call<ResponseBody> reqOrderPayQueryForZFB(@Query("user_id") String user_id, @Query("order_sn") String order_sn, @Query("key") String key);


    @GET("index.php/Api/Siji/siji_info_refresh")
        // 登录信息重新获取
        /*
          sj_id="司机ID"
          key=md5(sj_id+"Yxdj!@#2468"+uuid)
        */
    Call<ResponseBody> siji_info_refresh(@Query("sj_id") String sj_id, @Query("key") String key);


    @GET("/index.php/Api/Siji/clear_uuid")
    /*  清除唯一码
        si_id 司机ID
        key=md5(si_id+"Yxdj!@#2468" + uuid)
     */
    Call<ResponseBody> clear_uuid(@Query("sj_id") String sj_id, @Query("key") String key);

    @GET("/index.php/Api/Siji/password_reset2")
    /*
    参数
    si_id 司机ID
    old_passwd  旧密码
    new_passwd  新密码
    key=md5(si_id+"Yxdj!@#2468" + old_passwd + new_passwd)
    返回
    {"status": "100" , "msg":"error" , "fid":"" , "result":""]}
    status:100 参数错误   101 参数验证错误     201账号不存在  202  账号绑定设备不匹配  301 系统错误,修改失败      200  发送成功
     */
    Call<ResponseBody> password_reset2(@Query("sj_id") String sj_id, @Query("old_passwd") String old_passwd, @Query("new_passwd") String new_passwd
            , @Query("key") String key);


    @GET("index.php/Api/Siji/fujin_siji_list")
        // 移动地图定位
    Call<ResponseBody> moveLocation(@Query("m_lng") String longitude, @Query("m_lat")
            String latitude);

    //www.shiwudj.cn/Api/Siji/zhuandan

    @GET("index.php/Api/Siji/zhuandan")
    Call<ResponseBody> getOrderForward(@Query("sj_id") String sj_id, @Query("new_sjid") String new_sjid,
                                          @Query("order_id") String order_id);

}