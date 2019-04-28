package bjx.com.siji.base;


import com.baidu.location.BDLocation;

import java.util.ArrayList;

/**
 * Created by ssm on 2015/12/8.
 */
public class Constant {

//    public static final String BASE_PIC = "http://192.168.1.71/liuTai";   //图片地址前缀

//    public static final String YIHUIMALL_BASE_URL = "http://222.73.134.245:8080/xichenApp/";//商城官方服务器

    public static final String ALIPAY_ORDER_CALLBACK_URL = "common/payNotify";

    //支付宝支付后回调地址-充值
    public static final String ALIPAY_RECHARGER_CALLBACK_URL = "pay/alipaycallForRecharge";

    //支付宝支付后回调地址-消费
    public static final String ALIPAY_PAY_CALLBACK_URL = "pay/alipaycall";

    //支付宝支付后回调地址-转账
    public static final String ALIPAY_TRANSFER_CALLBACK_URL = "pay/alipaycallForTransfer";

    public static final String ALIPAY_RECHARGE_MOBILE_CALLBACK_URL = "pay/alipaycallForRechargeMobile";

    public static final String ALIPAY_RECHARGE_FLOW_CALLBACK_URL = "pay/alipaycallForRechargeFlow";

    public static final String ALIPAY_RECHARGE_OIL_CALLBACK_URL = "pay/alipaycallForRechargeOil";

    public static final String ALIPAY_USRER_LEVEL_CALLBACK_URL = "pay/alipaycallForUserLevel";

    public static final String ALIPAY_ONLINE_SHOP_CALLBACK_URL = "pay/alipaycallForApplyOnlineShop";


    //微信支付后回调地址-充值
    public static final String WEIXIN_RECHARGER_CALLBACK_URL = "pay/wxpaycallForRecharge";

    //微信支付后回调地址-消费
    public static final String WEIXIN_CALLBACK_URL = "pay/wxpaycall";

    public static final String WEIXIN_RECHARGE_MOBILE_CALLBACK_URL = "pay/wxpaycallForRechargeMobile";

    public static final String WEIXIN_RECHARGE_FLOW_CALLBACK_URL = "pay/wxpaycallForRechargeFlow";

    public static final String WEIXIN_RECHARGE_OIL_CALLBACK_URL = "pay/wxpaycallForRechargeOil";

    public static final String WEIXIN_RECHARGE_USER_LEVEL_URL = "pay/wxpaycallForUserLevel";

    public static final String WEIXIN_RECHARGE_ONLINE_SHOP_URL = "pay/wxpaycallForApplyOnlineShop";

    //保存开店申请的信息
    public static String[] shopApplyArray = new String[18];

    public static String TAG_EXIT = "exit";

    public static String CURRENT_ACCOUNT = "123456";  //用户的唯一账号,与服务器交互
    public static String CURRENT_PHONE = "1825183644";
    public static String CURRENT_OTHER = "1";
    public static String CURRENT_ORIGIN_PASS = "abcd1234";
    public static String CURRENT_ISFLAG = "true";
    public static String CURRENT_ADDRESS = "上海市秦淮区新街口2号站台";

    public static int CURRENT_REST_NUM = 0;

    public static String CURRENT_AVATAR = "";
//    public static String CURRENT_IMG = BASE_URL + CURRENT_AVATAR;

    public static Boolean IS_LOGIN = false;
    public static Boolean IS_FIXED_ADDRESS = false;//手动选择地址时，置为true,退出时置为false

    public static final int PHOTO_MAX_COUNT = 9;///////////最多选择几张图片

    public static String CONFIG_IS_LOGIN = "CONFIG_IS_LOGIN";  //记录登录状态
    public static String CONFIG_LOGIN_ACCOUTN = "CONFIG_LOGIN_ACCOUTN";
    public static String CONFIG_INTENT_ORDER_INFO = "CONFIG_INTENT_ORDER_INFO";

    public static String CONFIG_INTENT_ALREADY_DIS_TIME = "CONFIG_INTENT_ALREADY_DIS_TIME";
     public static String CONFIG_INTENT_LAT_LNG = "CONFIG_INTENT_LAT_LNG";


    public static String CONFIG_INTENT_RESTART_ALREADY_DIS_TIME = "CONFIG_INTENT_RESTART_ALREADY_DIS_TIME";
    public static String CONFIG_INTENT_RESTART_LAT_LNG = "CONFIG_INTENT_RESTART_LAT_LNG";


    public static String CONFIG_INTENT_BANNER_ID = "CONFIG_BANNER_ID";
    public static String CONFIG_INTENT_CATEGORY_ID = "CONFIG_INTENT_CATEGORY_ID"; //种类ID【上衣,大衣外套】
    public static String CONFIG_INTENT_CATEGORY_NAME = "CONFIG_INTENT_CATEGORY_NAME"; //种类【洗衣、洗鞋、洗家纺、奢侈品保养、精洗】
    public static String CONFIG_INTENT_BAIKE_CONTENT = "CONFIG_INTENT_BAIKE_CONTENT"; //百科

    public static String CONFIG_INTENT_ADDRESS_ID = "CONFIG_INTENT_ADDRESS_ID";
    public static String CONFIG_INTENT_ADDRESS_NAME = "CONFIG_INTENT_ADDRESS_NAME";
    public static String CONFIG_INTENT_ADDRESS_PHONE = "CONFIG_INTENT_ADDRESS_PHONE";
    public static String CONFIG_INTENT_ADDRESS_DETAIL = "CONFIG_INTENT_ADDRESS_DETAIL";


    public static String CONFIG_INTENT_ADDRESS_LAT = "CONFIG_INTENT_ADDRESS_LAT";
    public static String CONFIG_INTENT_ADDRESS_LNG = "CONFIG_INTENT_ADDRESS_LNG";

    public static String CONFIG_INTENT_ADDRESS_DISTRICT = "CONFIG_INTENT_ADDRESS_DISTRICT";
    public static String CONFIG_INTENT_ADDRESS_DISTRICTCODE = "CONFIG_INTENT_ADDRESS_DISTRICTCODE";

    public static String CONFIG_INTENT_PAY_CODE = "CONFIG_INTENT_PAY_CODE";
    public static String CONFIG_INTENT_ORDER_CODE = "CONFIG_INTENT_ORDER_CODE";


    public static String CONFIG_INTENT_MOBILE_ID = "CONFIG_INTENT_MOBILE_ID";

    public static String CONFIG_INTENT_RECHARGE_ORDER_TYPE = "CONFIG_INTENT_RECHARGE_ORDER_TYPE";

    public static String CONFIG_INTENT_RECHARGE_TYPE = "CONFIG_INTENT_RECHARGE_TYPE";

    //evaluate
    public static String CONFIG_INTENT_EVALUATE_FLAG = "CONFIG_INTENT_EVALUATE_FLAG";


    public static String CONFIG_INTENT_PAY_MONEY = "CONFIG_INTENT_PAY_MONEY";
    public static String CONFIG_INTENT_TYPE = "CONFIG_INTENT_TYPE";
    public static String CONFIG_PREFERENCE_GOODS_CATEGORY = "CONFIG_PREFERENCE_GOODS_CATEGORY";    //商品ID


    public static String CONFIG_INENT_MENU_CUR_SELECT = "CONFIG_INENT_MENU_CUR_SELECT";

    public static String CONFIG_INTENT_USER_LEVEL_TYPE = "CONFIG_INTENT_USER_LEVEL_TYPE";


    public static String CONFIG_INTENT_ORDER_GOODS_SPECES = "CONFIG_INTENT_ORDER_GOODS_SPECES";

    public static String CONFIG_INTENT_ORDER_ID = "CONFIG_INTENT_ORDER_ID";
    public static String CONFIG_INTENT_ORDER_NUMBER = "CONFIG_INTENT_ORDER_NUMBER";
    public static String CONFIG_INTENT_ORDER_PRICE = "CONFIG_INTENT_ORDER_PRICE";    //订单价格
    public static String CONFIG_INTENT_ORDER_FREIGHT = "CONFIG_INTENT_ORDER_FREIGHT";//物流价格
    public static String CONFIG_INTENT_ORDER_NAME = "CONFIG_INTENT_ORDER_NAME";
    public static String CONFIG_INTENT_ORDER_SUM = "CONFIG_INTENT_ORDER_SUM";
    public static String CONFIG_INTENT_ORDER_IS_COMPLETED = "CONFIG_INTENT_ORDER_IS_COMPLETED";
    public static String CONFIG_INTENT_ORDER_IS_FANXIAN = "CONFIG_INTENT_ORDER_IS_FANXIAN";


    public static String CONFIG_INTENT_ORDER_IS_FROM_ORDER = "CONFIG_INTENT_ORDER_IS_FROM_ORDER";

    //countdown
    public static String CONFIG_INTENT_COUNTDOWN_TIME = "CONFIG_INTENT_COUNTDOWN_TIME";

    //  账单类型
    public static String CONFIG_INTENT_INTEGRAL_TYPE = "CONFIG_INTENT_INTEGRAL_TYPE";


    public static String CONFIG_INTENT_USER_RECHARGE_MONEY = "CONFIG_INTENT_USER_RECHARGE_MONEY";
    public static String CONFIG_INTENT_RECHARGE_MONEY = "CONFIG_INTENT_RECHARGE_MONEY"; //充值金额

    public static String CONFIG_INTENT_BAIKE_DETAIL_URL = "CONFIG_INTENT_BAIKE_DETAIL_URL"; //百科详情

    public static String CONFIG_INTENT_COMPANY_DETAIL_URL = "CONFIG_INTENT_COMPANY_DETAIL_URL"; //百科详情

    public static String CONFIG_INTENT_COMPANY_NEWS_DETAIL = "CONFIG_INTENT_COMPANY_NEWS_DETAIL"; //百科详情

    public static String CONFIG_INTENT_BILL_TYPE = "CONFIG_INTENT_BILL_TYPE"; //百科详情


    public static String CONFIG_INTENT_BILL_DETAIL_DATA = "CONFIG_INTENT_BILL_DETAIL_DATA"; //百科详情

    //首页商品类别
    public static String CONFIG_INTENT_GOOGS_CATEGORY_ID = "CONFIG_INTENT_GOOGS_CATEGORY_ID"; //百科详情
    public static String CONFIG_INTENT_GOOGS_CATEGORY_NAME = "CONFIG_INTENT_GOOGS_CATEGORY_NAME"; //百科详情

    public static String CONFIG_INTENT_GOOGS_CATEGORY_IMG = "CONFIG_INTENT_GOOGS_CATEGORY_IMG"; //图片
    public static String CONFIG_INTENT_FRIST_CATEGORY_ID = "CONFIG_INTENT_FRIST_CATEGORY_ID"; //一级分类


    public static String CONFIG_INTENT_CARD_ID = "CONFIG_INTENT_CARD_ID"; //身份id
    public static String CONFIG_INTENT_CARD_NAME = "CONFIG_INTENT_CARD_NAME"; //身份姓名
    public static String CONFIG_INTENT_CARD_SUCESS = "CONFIG_INTENT_CARD_SUCESS"; //身份id


    public static String CONFIG_INTENT_PWD_TYPE_NAME = "CONFIG_INTENT_PWD_TYPE_NAME"; //百科详情


    public static String CONFIG_INTENT_GOODS_INFO_SERIAL = "CONFIG_INTENT_GOODS_INFO_SERIAL"; //商品详情

    public static String CONFIG_INTENT_SHOPCART_LIST = "CONFIG_INTENT_SHOPCART_LIST";
    public static String CONFIG_INTENT_SHOP_NAME = "CONFIG_INTENT_SHOP_NAME"; //商店名
    public static String CONFIG_INTENT_SHOP_ID = "CONFIG_INTENT_SHOP_ID";    //商店ID
    public static String CONFIG_INTENT_SHOP_PIC = "CONFIG_INTENT_SHOP_PIC";    //商店图片
    public static String CONFIG_INTENT_SHOP_ADDRESS = "CONFIG_INTENT_SHOP_ADDRESS";
    public static String CONFIG_INTENT_SHOP_RANGE = "CONFIG_INTENT_SHOP_RANGE";
    public static String CONFIG_INTENT_SHOP_DELIVERY = "CONFIG_INTENT_SHOP_DELIVERY";
    public static String CONFIG_INTENT_SHOP_PROMOTION1 = "CONFIG_INTENT_SHOP_PROMOTION1";
    public static String CONFIG_INTENT_SHOP_PROMOTION2 = "CONFIG_INTENT_SHOP_PROMOTION2";

    public static String CONFIG_INTENT_CAMPAIGN_ID = "CONFIG_INTENT_CAMPAIGN_ID";
    public static String CONFIG_INTENT_CAMPAIGN_PIC = "CONFIG_INTENT_CAMPAIGN_PIC";

    public static String CONFIG_INTENT_ID = "CONFIG_INTENT_ID";
    public static String CONFIG_INTENT_IMG = "CONFIG_INTENT_IMG";


    public static String CONFIG_INENT_PHONE = "CONFIG_INENT_PHONE";//是否返现

    public static String CONFIG_INENT_IMG_CODE = "CONFIG_INENT_IMG_CODE";//是否返现

    public static String CONFIG_INENT_UUID = "CONFIG_INENT_UUID";//是否返现


    //开店申请类型; 1-店铺、2-农场
    public static String CONFIG_INTENT_APPLY_TYPE = "CONFIG_INTENT_APPLY_TYPE";

    public static String CONFIG_INTENT_IS_REFUND = "CONFIG_INTENT_IS_REFUND";

    public static String CONFIG_INTENT_GENDER = "CONFIG_INTENT_GENDER";

    public static String CONFIG_IS_BACKTO_LIST = "CONFIG_IS_BACKTO_LIST";
    public static String CONFIG_INTENT_IS_FROM_SEARCH = "CONFIG_INTENT_IS_FROM_SEARCH";


    public static String CONFIG_INTENT_BUY_TYPE = "CONFIG_INTENT_BUY_TYPE";
    public static String UPLOAD_TYPE_AFTER_SALE = "after_sale";


    public static String CONFIG_PREFERENCE_BANNER = "CONFIG_PREFERENCE_BANNER";
    public static String CONFIG_PREFERENCE_BANNER_TYPE = "CONFIG_PREFERENCE_BANNER_FANXIAN";

    public static String CONFIG_PREFERENCE_CLOTH_TITLE = "CONFIG_PREFERENCE_CLOTH_TITLE";      //保存标题
    public static String CONFIG_PREFERENCE_SHOES_TITLE = "CONFIG_PREFERENCE_SHOES_TITLE";      //保存标题
    public static String CONFIG_PREFERENCE_ESSENCE_TITLE = "CONFIG_PREFERENCE_ESSENCE_TITLE";  //保存标题
    public static String CONFIG_PREFERENCE_TEXTITLES_TITLE = "CONFIG_PREFERENCE_TEXTITLES_TITLE";//保存标题
    public static String CONFIG_PREFERENCE_LUXURY_TITLE = "CONFIG_PREFERENCE_LUXURY_TITLE";    //保存标题
    public static String CONFIG_PREFERENCE_BAIKE_CONTENT = "CONFIG_PREFERENCE_BAIKE_CONTENT";    //百科内容
    public static String CONFIG_PREFERENCE_BANNER_CONTENT = "CONFIG_PREFERENCE_BANNER_CONTENT";  //Banner内容

    public static String CONFIG_PREFERENCE_SEARCH_HISTORY = "CONFIG_PREFERENCE_SEARCH_HISTORY";  //搜索历史
    public static String CONFIG_PREFERENCE_SEARCH_LAST_CONTENT = "CONFIG_PREFERENCE_SEARCH_LAST_CONTENT";  //最后一次搜索内容
    public static String CONFIG_PREFERENCE_IS_SEARCH_DIRECT = "CONFIG_PREFERENCE_IS_SEARCH_DIRECT";  //是否直接显示搜索内容

    public static String CONFIG_PREFERENCE_COMPANY_INFO_CONTENT = "CONFIG_PREFERENCE_COMPANY_INFO_CONTENT";    //百科内容


    public static String CONFIG_PREFERENCE_GOODS_ID = "CONFIG_PREFERENCE_GOODS_ID";    //商品ID

    public static String CONFIG_PREFERENCE_GOODS_DES = "CONFIG_PREFERENCE_GOODS_DES";    //商品ID


    public static String CONFIG_PREFERENCE_ORDER_ID = "CONFIG_PREFERENCE_ORDER_ID";    //订单ID
    public static String CONFIG_PREFERENCE_CROWDFUNDING_ID = "CONFIG_PREFERENCE_CROWDFUNDING_ID";    //商品ID

    public static String CONFIG_PREFERENCE_CROWDFUNDING_MONEY = "CONFIG_PREFERENCE_CROWDFUNDING_MONEY";    //商品ID


    public static String CONFIG_PREFERENCE_FLAG_ID = "CONFIG_PREFERENCE_FLAG_ID";    //商品ID


     public static String CONFIG_INENT_ORDER_ATTR = "CONFIG_INENT_ORDER_ATTR";//商品属性
    public static String CONFIG_INENT_ORDER_SUM_PRICE = "CONFIG_INENT_ORDER_SUM_PRICE";//订单总价(不含运费)
    public static String CONFIG_INENT_ORDER_SPECES = "CONFIG_INENT_ORDER_SPECES";//商品规格


    public static String CONFIG_INENT_ORDER_ATTRS = "CONFIG_INENT_ORDER_ATTRS";//商品属性
    public static String CONFIG_INENT_ORDER_BEAN_LIST = "CONFIG_INENT_ORDER_BEAN_LIST";//订单信息

    public static String CONFIG_INENT_IS_CASH_BACK = "CONFIG_INENT_IS_CASH_BACK";//是否返现

    public static String CONFIG_INENT_IS_BUY_NOW = "CONFIG_INENT_IS_BUY_NOW";//是否来自返现商品详情


    public static String CONFIG_INENT_IS_PAY_PWD = "CONFIG_INENT_IS_PAY_PWD";//是否支付密码


    public static String CONFIG_INENT_IS_ANOTHER_ONE = "CONFIG_INENT_IS_ANOTHER_ONE";//是否再来一单

    public static String CONFIG_INTENT_IS_CROWDFUNDING = "CONFIG_INTENT_IS_CROWDFUNDING";//是否返现


    public static String CONFIG_INTENT_LOGIN_FLAG = "CONFIG_INTENT__LOGIN_FLAG";//


    public static final int REQUESTCODE_NORMAL = 0x85;//保存标题
    public static final int REQUESTCODE_REFRESH = 0x86;//刷新当前界面

    public static final int RESULTCODE_LOGIN = 0x88; //登录
    public static final int RESULTCODE_VOUCHER = 0x89; //优惠卷
    public static final int RESULTCODE_PAY_SUCCESS = 0x90; //支付成功
    public static final int RESULTCODE_REFRESH = 0x91; //刷新列表
    public static final int REQUESTCODE_GO_DETAIL = 0x92; //去详情

    public static String CONFIG_INTENT_SELECT_POSITION = "CONFIG_INTENT_SELECT_POSITION";


    public static final int RESULTCODE_COMPAY_NEWS_LOGIN = 0x93; //登录


    public static String CONFIG_INTENT_RECHARG_MONEY_NUMBER = "CONFIG_INTENT_RECHARG_MONEY_NUMBER";

    public static String CONFIG_INTENT_TRANSFER_MONEY = "CONFIG_INTENT_TRANSFER_MONEY";

    public static String CONFIG_INTENT_TRANSFER_ACCOUNT = "CONFIG_INTENT_TRANSFER_ACCOUNT";

    //    Upgrade
    public static String CONFIG_INTENT_UPGRADE_TYPE = "CONFIG_INTENT_UPGRADE_TYPE";


    //上传图片类型
    public static String UPLOAD_TYPE_GOOD_DESIGN = "UPLOAD_TYPE_GOOD_DESIGN";
    public static String UPLOAD_TYPE_USER_AVATOR = "UPLOAD_TYPE_USER_AVATOR";

    public static String CONFIG_INTENT_IS_MODIFY = "CONFIG_INTENT_IS_MODIFY";   //查看\修改

    public static String UPLOAD_TYPE_SHOP_APPLY = "shop_apply";

    public static Double CURRENT_CURREENT_LAT = 0.0;
    public static Double CURRENT_CURREENT_LNG = 0.0;
    public static String CURRENT_CURREENT_ADDR = "";


    public static int IO_BUFFER_SIZE = 2*1024;



    //上传图片的类型
    public static String UPLOAD_TYPE_ORDER_REVIEW = "order_review";

    public static final String SHARE_URL = "http://www.simingtang.com/";
    public static final String SHARE_CONENT = "视力守护者——保护您的视力！";
    public static final String SHARE_IMG = "http://img5.imgtn.bdimg.com/it/u=854871891,2103213146&fm=21&gp=0.jpg";

    ///////第三方应用的id
    public static final String QQ_ID = "1104985857";
    public static final String QQ_KEY = "mBzPMmiB6cpWihDD";
    public static final String WEXIN_ID = "wx69fdab9ebe3cb62a";
    public static final String WEXIN_KEY = "42526baf438398d356cd08cd14052863";
    //    public static final String WEXIN_SECRET = "444202b5a5e843d0e42e6fb324deeef7";
    public static String WEXIN_RETURN_CODE = "12324"; //微信返回code,通过此code获取token
    public static String CONFIG_JUMP_FROM_REPORT = "CONFIG_JUMP_FROM_REPORT";

    public static final int PAY_BALANCE = 0x1561;
    public static final int PAY_ALIPAY = 0x1562;
    public static final int PAY_WENXIN = 0x1563;
    public static final int PAY_UNIONPAY = 0x1564;

    public static final int BALANCE_INTENT = 825;
    public static final int BALANCE_SUCCESS = BALANCE_INTENT + 1;


    public static final String TOAST_MEG_SHOP_NAME = "请填写店铺名称!";

    public static final String TOAST_MEG_ERROR_PHONE = "请填写正确的手机号!";
    public static final String TOAST_MEG_ERROR_PARENT_PHONE = "请在上面填写父母正确的手机号!";
    public static final String TOAST_MEG_STUDENT_REGISTER_TYPE = "请先确认注册类型!";
    public static final String TOAST_MEG_ERROR_EMPTY = "填写信息不能为空!";//点击选择地址
    public static final String TOAST_MEG_LOCATION_PLEASE = "请点击选择地址!";//


    public static final String TOAST_MEG_ERROR_PAY_PWD = "请输入6位数纯数字格式支付密码!";

    public static final String TOAST_MEG_ERROR_CONFIRMPAY_PWD = "两次密码输入不一致!";

    public static final String TOAST_MEG_DIFFERENT_PASS = "两次密码不一致!";



    public static final String TOAST_MEG_PASS_LENGTH = "密码长度6-18位!";
    public static final String TOAST_MEG_ERROR_LOGIN_PWD = "请输入6-8位数纯数字或字母密码!";
    public static final String TOAST_MEG_ERROR_CODE = "请输入短信验证码!";


    public static final String TOAST_MEG_ERROR_PERSON_NAME = "请输入您的姓名!";
    public static final String TOAST_MEG_ERROR_PERSON_PHONE = "请输入您的手机号!";
    public static final String TOAST_MEG_ERROR_PERSON_NUMBER = "请正确填写您的身份证号码!";

    public static final String TOAST_MEG_NETCONNECT_ERROR = "当前网络未连接!";

    public static final String TOAST_MEG_REGISTER_SUCCESS = "注册成功,即将跳转!";
    public static final String TOAST_MEG_REGISTER_FAILURE = "注册失败!";
    public static final String TOAST_MEG_PHONE_CODE = "请求成功,请稍等!";//验证码获取成功!验证码为:
    public static final String TOAST_MEG_PHONE_CODE_ERROR = "请求失败!";//验证码获取成功!验证码为:
    public static final String TOAST_MEG_LOGIN_SUCCESS = "登录成功!";
    public static final String TOAST_MEG_LOGIN_AND_JUMP = "登录成功,直接跳转!";
    public static final String TOAST_MEG_LOGIN_TOURIST = "游客登录!";
    public static final String TOAST_MEG_LOGIN_FAILURE = "登录失败!";
    public static final String TOAST_MEG_LOGIN_ERROR = " 登录失败!用户名或密码错误! ";
    public static final String TOAST_MEG_LOGIN_FIRST = " 请先登录! ";

    public static final String TOAST_MEG_NETWORK_NULL = "当前网络未连接!";
    public static final String TOAST_MEG_NETWORK_ERROR = "网络请求异常!";
    public static final String TOAST_MEG_ANALYZE_ERROR = "数据解析异常!";
    public static final String TOAST_MEG_REBACK_ERROR = "返回内容异常!";
    public static final String TOAST_MEG_EMPTY_CONTENT = "无返回内容!";
    public static final String TOAST_MEG_LOADING_FINISH = "无更多数据!";
    public static final String TOAST_MEG_MESSAGE_ERROR = "错误的返回内容!";

    public static final String TOAST_MEG_ERROR_CITY = "当前城市尚未开通服务!";
    public static final String TOAST_DEFAULT_ADDRESS_SUCCESS = "默认地址设置成功!";
    public static final String TOAST_DELETE_SUCCESS = "删除地址成功!";

    public static final String TOAST_MEG_PHONE_NUMBER_ERROR = "图片最多4张,可长按删除多余图片!";
    public static final String TOAST_MEG_GET_EMPTY_DATA = "当前无数据~~";
    public static final String TOAST_MEG_REBACK_EMPTY = "还没有数据哦~~!";


    public static String CONFIG_INTENT_NICK_NAME = "CONFIG_INTENT_NICK_NAME";

    public static String CONFIG_INTENT_GOODS_ID = "CONFIG_INTENT_GOODS_ID";

    public final static int API_ERROR_REBACK = 405;//错误的返回内容
    public final static int API_REQUEST_FAILURE = 404;

    public final static int API_REQUEST_SUCCESS = 200;
    public final static int API_ANALYZE_SUCCESS = 201;
    public final static int API_ANALYZE_FAILURE = 202;
    public final static int API_GET_RELUS_SUCCESS = 203;

    public static int API_GET_PHONE_CODE_SUCCESS = 203;
    public static int API_POST_CODE_LOGIN_SUCCESS = 204;
    public final static int API_GET_CITY_LIST_SUCCESS = 205;
    public final static int API_GET_BANNER_LIST_SUCCESS = 206;
    public final static int API_POST_REGISTER_SUCCESS = 207;
    public final static int API_GET_MODULE_TITLE_SUCCESS = 208;//获取标题相关
    public final static int API_GET_CLOTHES_LIST_SUCCESS = 209;//获取衣服列表
    public final static int API_POST_SET_DEFAULT_ADDRESS = 210;//设置默认地址
    public final static int API_POST_DELETE_ADDRESS = 211;     //删除地址
    public final static int API_GET_WASH_BOOKING_TIME = 212;   //获取预约时间
    public final static int API_GET_CURRENT_TIME = 213;        //获取当前时间
    public final static int API_GET_UNCOMPLETED_ORDER = 214;   //未完成订单
    public final static int API_GET_COMPLETED_ORDER = 215;     //已完成订单

    public final static int API_POST_PAY_BEFORE_SUCCESS = 216;    //支付前
    public final static int API_POST_PAY_AFTER_SUCCESS = 217;     //支付后
    public final static int API_POST_CANCEL_ORDER_SUCCESS = 218;  //支付后
    public final static int API_GET_USER_BALANCE_SUCCESS = 219;       //获取用户当前余额
    public final static int API_GET_BAIKE_SUCCESS = 220;       //洗衣百科
    public final static int API_POST_UPLOAD_AVATAR_SUCCESS = 221; //上传头像
    public final static int API_POST_NICKNAME_SUCCESS = 222; //修改昵称
    public final static int API_POST_MY_RECOMMEND_SUCCESS = 221;       //获取用户总奖励额
    public final static int API_POST_COMMISSION_SUCCESS = 222;       //获取我推荐的人总奖励额明细

    public final static int API_POST_RECOMMEND_ONE_SUCCESS = 223;       //获取我推荐的人

    public final static int API_POST_RECOMMEND_TWO_SUCCESS = 225;       //获取我推荐的人


    public final static int API_GET_USER_INFO_SUCCESS = 224;       //用户基本信息


    public final static int API_GET_USER_VOUCHER_COUNT_SUCCESS = 288;       //用户基本信息


    public final static int API_GET_VOUCHER_CARD_SUCCESS = 225;    //充值卡信息

    public final static int API_POST_VOICHER_BEFORE_SUCCESS = 226; //在线充值前
    public final static int API_POST_VOICHER_AFTER_SUCCESS = 227;  //在线充值后
    public final static int API_POST_REVERT_VOUCHER_SUCCESS = 228;  //还原优惠券

    public final static int API_POST_BALANCE_AFTER_SUCCESS = 229;  //
    public final static int API_POST_FIND_BACK_PWD_SUCCESS = 230;  //
    public final static int API_POST_LOGOUT_SUCCESS = 231;  //

    public final static int API_GET_GOODS_LIST_CATEGORIES_SUCCESS = 232;  //
    public final static int API_GET_GOODS_CATEGORIES_SUCCESS = 233;  //

    public final static int API_GET_GOODS_CATEGORIES_THREE_SUCCESS = 434;  //


    public final static int API_GET_GOODS_GROUP_SUCCESS = 234;  //

    public final static int API_GET_CATEGORY_LIST_SUCCESS = 233;  //
    public final static int API_GET_GOODS_LIST_DETAIL_SUCCESS = 235;  //

    public final static int API_POST_IS_MY_FAVOURITES_SUCCESS = 236;  //
    public final static int API_POST_ADD_FAVOURITES_SUCCESS = 237;  //

    public final static int API_POST_MY_FAVOURITES_SUCCESS = 238;  //
    public final static int API_POST_MY_SHOP_FAVOURITES_SUCCESS = 239;  //


    public final static int API_ADD_TO_CARSHOP_SUCCESS = 238;  //


    public final static int API_GET_CARSHOP_LIST_SUCCESS = 239;  //


    public final static int API_POST_CREATE_ORDER_SUCCESS = 256;


    public static int API_POST_DEL_SHOP_CART_SUCCESS = 240;

    public final static int API_GET_VOICHER_BEFORE_SUCCESS = 241; //在线充值前

    public final static int API_POST_UPDATE_GOODS_NUM_SUCCESS = 242;

    public final static int API_POST_SHOP_CART_CALC_SUCCESS = 243;
    public final static int API_GET_FREIGHT_PRICE_INFO_SUCCESS = 244;

     public static int REQUEST_TYPE_UPLOAD_PICS = 245;


    public final static int API_GET_ORDER_LIST_SUCCESS = 246;
    public final static int API_GET_ORDER_CANCEL_SUCCESS = 247;
    public final static int API_GET_ORDER_CONFIRM_SUCCESS = 248;

    public final static int API_GET_ORDER_DELETE_SUCCESS = 249;


    public final static int API_POST_RECOMMEND_AWARDS_SUCCESS = 251;       //获取我推荐的人总奖励额明细

    public final static int API_GET_CASHBACK_INTRODUCTION_SUCCESS = 252;

    public final static int API_GET_CASHBACK_RULES_SUCCESS = 253;

     public final static int API_GET_LOGISTICS_INFORMATION_SUCCESS = 254;
    public final static int API_POST_GOODS_SEARCH_INFO_SUCCESS = 255;

    public final static int API_POST_CANCLE_FAVOURITES_SUCCESS = 256;  //

    public final static int API_GET_GOODS_DETAIL_SUCCESS = 257;

     public final static int API_GET_IS_CAN_BUY_SUCCESS = 259;  //
    public final static int API_GET_REFUND_LIST_SUCCESS = 260;  //
    public final static int API_POST_REFUND_APPLY_SUCCESS = 261;  //
    public final static int API_POST_REFUND_APPLY_PASS_SUCCESS = 262;  //
    public final static int API_POST_IS_REFUND_APPLY_SUCCESS = 263;  //

    public final static int API_POST_IS_RETURN_OF_GOODS_APPLY_SUCCESS = 264;  //

    public final static int API_POST_AFTER_SALES_APPLY_SUCCESS = 264;  //
    public final static int API_GET_AFTER_SALES_LIST_SUCCESS = 265;  //


    public final static int API_GET_GOODS_REST_NUMBER_SUCCESS = 266;  //
    public final static int API_GET_GOOD_ATTR_INFO_SUCCESS = 267;  //
    public final static int API_POST_ATTR_GET_RESTNUM_SUCCESS = 268;  //


    public final static int API_POST_PAY_PWD_SUCCESS = 267;  //

    public static int API_GET_PAY_PWD_PHONE_CODE_SUCCESS = 268;

    public final static int API_POST_UPLOAD_PICTURE_SUCCESS = 269; //上传图片

    public final static int API_GET_CATEGORY_BRAND_SUCCESS = 270;  //
    public final static int API_GET_CATEGORY_LABEL_SUCCESS = 271;  //


    public final static int API_GET_CERTIFICATION_STATUS_SUCCESS = 274;       //身份认证审核状态

    public final static int API_GET_WITHDRAW_STATUS_SUCCESS = 275;       //身份认证审核状态

    public final static int API_GET_ORDER_LOCK = 276;     //支付时订单上锁
    public final static int API_GET_ORDER_UNLOCKING = 277;     //支付时订单上锁

    public final static int API_POST_IS_REFUND_APPLY_STATUS_SUCCESS = 278;  //申请退款的状态
    public final static int API_POST_IS_RETURN_OF_GOODS_APPLY_STATUS_SUCCESS = 279;  //申请退货/退款的状态


    public final static int API_POST_IS_REFUND_APPLY_SURE_SUCCESS = 280;  //再次申请退款

    public final static int API_POST_IS_REFUND_APPLY_REPEAL_SUCCESS = 281;  //撤销申请退款

    //收件人信息 consignee
    public final static int API_GET_CONSIGNEE_INFO_SUCCESS = 282;  //再次申请退款


    //新品
    public final static int API_GET_NEW_GOODS_LIST_SUCCESS = 283;  //

    //平台快讯
    public final static int API_GET_NOTICE_LIST_SUCCESS = 284;  //


    //新品
    public final static int API_GET_NEW_GUESS_LIKE_SUCCESS = 285;  //



    public final static int API_GET_NEW_GUESS_LIKE_SUCCESS_MORE = 287;  //


    public final static int API_POST_PASSWORD_LOGIN_SUCCESS = 286;

    public final static int API_GET_KEFU_PHONE_NUMBER_SUCCESS = 288;

    public final static int API_GET_TEAM_PERSONS_LIST_SUCCESS = 290;

    public final static int API_GET_BONUS_DETAIL_LIST_SUCCESS = 289;

    public final static int API_POST_IMMEDIATE_BUT_GET_ORDERID_SUCCESS = 291;

    public final static int API_POST_TRANSFER_ACCOUNT_SUCCESS = 292;

    public final static int API_POST_TRANSFER_CODE_SUCCESS = 293;
    public final static int API_POST_TRANSFER_BACK_SUCCESS = 294;

    public final static int API_POST_TRANSFER_RECORD_SUCCESS = 295;

    public final static int API_POST_RECHARGE_CODE_SUCCESS = 296;

    public final static int API_POST_RECHARGE_MOBILE_ORDER_SUCCESS = 297;

    public final static int API_POST_RECHARGE_FLOW_CHECK_SUCCESS = 298;
    public final static int API_POST_RECHARGE_LIMIT_SUCCESS = 299;

    public final static int API_GET_BAIDU_CITY_SUCCESS = 232;

    public final static int API_POST_OPEN_SHOP_APPLY_SUCCESS = 233;

    public final static int API_GET_GOODS_LIST_STATUS_SUCCESS = 234;
    public final static int API_GET_FRUIT_CATEGORIES_SUCCESS = 235;
    public final static int API_POST_GOODS_RELEASE_SUCCESS = 236;
    public final static int API_POST_GOODS_MODIFY_SUCCESS = 237;
    public final static int API_GET_SHOP_GOODS_LIST_SUCCESS = 238;
    public final static int API_POST_GOODS_EDIT_STATUS_SUCCESS = 239;

    public final static int API_GET_SHOP_DETAIL_INFO_SUCCESS = 240;    //店铺详情
    public final static int API_GET_ORDER_LOGIS_INFO_SUCCESS = 241;

    public final static int API_GET_ORDER_LOGIS_PROGRESS_SUCCESS = 242;
    public final static int API_GET_SHOP_ORDER_LIST_SUCCESS = 243;
    public final static int API_GET_ORDER_REVIEW_INFO_SUCCESS = 244;
    public final static int API_POST_MODIFY_ORDER_LOGIS_SUCCESS = 245;
    public final static int API_GET_SHOP_REFUND_LIST_SUCCESS = 246;
    public final static int API_POST_SHOP_RETURN_REFUND_SUCCESS = 247;
    public final static int API_POST_SHOP_REFUND_STATUS_SUCCESS = 259;


    public final static int API_GET_LINE_GUESS_LIKE_SUCCESS = 248;  //

    public final static int API_GET_LINE_GUESS_LIKE_SUCCESS_MORE = 249;  //
    public final static int API_GET_ALIPAY_PAY_PARMS_SUCCESS = 288;
    public final static int API_GET_ALIPAY_RECHARGE_PARMS_SUCCESS = 289;
    public final static int API_GET_WX_PAY_PARMS_SUCCESS = 304;
    public final static int API_GET_WX_RECHARGE_PARMS_SUCCESS = 305;


    //个人中心-新增地址
    public static final int AD_MANAGE_SUCCESS = 820;
    public static final String SHARE_PIC_URL = "/src/img/logo02.png";
    public static final String VERSION = "version_code";

    public static int SCREEN_WIDTH = 480;
    public static int SCREEN_HEIGHT = 800;
    public static float SCREEN_DENSITY = (float) 1.0;

    //商品详情中多张图片
    public static ArrayList<String> goodPhotos = new ArrayList<String>();

    public static final String[] picArray = new String[]{
            "http://a2.att.hudong.com/68/74/05300439213381133523747871500.jpg",
            "http://pic29.nipic.com/20130509/8821914_195618165136_2.jpg",
            "http://pic19.nipic.com/20120214/8876334_135409288484_2.png",
            "http://img.taopic.com/uploads/allimg/120502/128211-12050210202398.jpg",
            "http://img05.tooopen.com/images/20140428/sy_59947414213.jpg",
            "http://pic36.nipic.com/20131128/9885883_155339596000_2.jpg",
            "http://www.ymcft.com/uploads/allimg/120620/10-120620141134959.jpg",
            "http://pic47.nipic.com/20140827/2531170_080552898000_2.jpg",
            "http://pic37.nipic.com/20140107/2531170_132216182000_2.jpg",
            "http://pica.nipic.com/2008-01-19/200811991458734_2.jpg",
            "http://pic55.nipic.com/file/20141213/9885883_133900133000_2.jpg",
            "http://pic21.nipic.com/20120613/6397017_105420072000_2.jpg",
    };

    public static final String[] shopArray = new String[]{
            "http://www.xianzhi.net/uploads/allimg/150625/44_150625115306_1.jpg",
            "http://www.tjbyd.net.cn/uploadfile/2015/32457204896/231656116827.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2407898116,4014132316&fm=21&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=2000490357,2497819532&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3407517595,75597964&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3294064505,1323516250&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=746051593,339974018&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=813234889,2517611342&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2968102474,1024693654&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4288506851,2070300285&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2410580889,3669618478&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3381068122,1973625466&fm=21&gp=0.jpg",
    };

    public static final String[] campaginPic = new String[]{
            "http://img1.imgtn.bdimg.com/it/u=394163392,885989187&fm=21&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1078103741,154370341&fm=21&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2350441597,670810031&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2595301828,2018249847&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3827410525,1125436988&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3334481374,4052210734&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1519903863,1033057476&fm=21&gp=0.jpg"
    };


    //保存私人订制的信息
    public static String[] personalTailorArray = new String[18];

    //修改店铺信息
    public static String[] LineShopInfoTailArray = new String[7];


    /*
     0-农场名称
     1-手机号码
     2-联系人
     3-定位地址
     4-经度
     5-纬度
     6-真实姓名
     7-身份证号
     8-身份证正面-照片
     9-身份证反面-照片
     10-农场基地-照片
     11-项目局部-照片
     12-证明材料-照片
     13-城市
     14-省份
     */
    public static BDLocation mLocation = null;

}
