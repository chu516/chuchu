package bjx.com.siji.contants;


public class Constants {


    /*兼容部分垃圾手机如OPPO锁屏后再次打开计时器页面时重新执行initView导致原有数据被清空的问题 */
    public final static String driveTime = "driveTime";//当前已行驶的时间
    public final static String waitTime = "waitTime";//当前已等待的时间

    //再中断之后重启保存行驶时间和等待时间
    public final static String restartDriveTime = "driveTime";//当前已行驶的时间
    public final static String restartwaitTime = "waitTime";//当前已等待的时间

    //中断时当前时间

    public final static String driveCurrentDateTime = "CurrentDateTime";//当前已行驶的时间

    public final static String driveCurrentOperationType = "CurrentOperationType";//当前已行驶的时间


    public final static String driveDistance = "driveDistance";//当前行驶的距离
    public final static String preLoc = "preLoc";//前一次轨迹定位时的坐标
    public final static String recordWaitTime = "recordWaitTime";
    public final static String recordDriveTime = "recordDriveTime";
    public final static String currentState = "currentState";

    // public final static String BASE_URL = "http://bjx.aqlinli.com/";
    public final static String BASE_URL = "http://bjx.niceicp.com/";

    public final static String ADVICE_URL = BASE_URL + "index.php/Api/Static/advice"; // 意见反馈
    public final static String LIC_URL = BASE_URL + "index.php/Api/Static/rule_android"; // 协议规则


    public final static String COMMENT_URL = BASE_URL + "index.php/Api/Static/comment"; // 评价
    public final static String CANCEL_URL = BASE_URL + "index.php/Api/Static/cancel"; // 取消原因

    /**
     * SharedPreferences usermodel key
     */
    public static final String USERMODEL = "usermodel";
    public static final String PWD = "password";
    public static final String CHANNELID = "channelid";
    public static final String FID = "fid";
    public static final String KEFU = "kefu";

    public static final String PUBLIC_PARAM1 = "abc"; // 随机数
    public static final String PUBLIC_PARAM2 = "fid"; // 加盟商
    public static final String PUBLIC_PARAM3 = "ver"; // 版本号
    public static final String PUBLIC_PARAM4 = "system"; // 平台类型 android/ios
    public static final String PUBLIC_PARAM5 = "uuid"; // 手机唯一标识码
    public static final String PUBLIC_PARAM6 = "device_token"; // 设备PUSH码

    public static final String BASE_KEY = "Yxdj!@#2468"; // 接口加密基数


    public static String CONFIG_INTENT_SEARCH_ADDRESS = "CONFIG_INTENT_SEARCH_ADDRESS";
    public static String CONFIG_INTENT_ADDRESS_NAME = "CONFIG_INTENT_ADDRESS_NAME";
    public static String CONFIG_INTENT_ADDRESS_PHONE = "CONFIG_INTENT_ADDRESS_PHONE";
    public static String CONFIG_INTENT_ADDRESS_DETAIL = "CONFIG_INTENT_ADDRESS_DETAIL";


    public static String CONFIG_INTENT_ADDRESS_LAT = "CONFIG_INTENT_ADDRESS_LAT";
    public static String CONFIG_INTENT_ADDRESS_LNG = "CONFIG_INTENT_ADDRESS_LNG";

    //Receipt
    public static String CONFIG_INTENT_RECEIPT = "CONFIG_INTENT_RECEIPT";


    /**
     * 微信支付业务 app_id
     */
    public static final String WX_APPID = "wx7088cf99c63b0988";


    /**
     * 百度鹰眼轨迹
     */
    public static final long BD_TRACE_SERVICEID = 210736;

    /**
     * 轨迹分析查询间隔时间（1分钟）
     */
    public static final int ANALYSIS_QUERY_INTERVAL = 60;

    /**
     * 停留点默认停留时间（1分钟）
     */
    public static final int STAY_TIME = 60;

    /**
     * 启动停留时间
     */
    public static final int SPLASH_TIME = 3000;

    /**
     * 默认采集周期
     */
    public static final int DEFAULT_GATHER_INTERVAL = 5;

    /**
     * 默认打包周期
     */
    public static final int DEFAULT_PACK_INTERVAL = 30;

    /**
     * 实时定位间隔(单位:秒)
     */
    public static final int LOC_INTERVAL = 10;

    /**
     * 最后一次定位信息
     */
    public static final String LAST_LOCATION = "last_location";

    public static final String TAG = "ejiuyuan";
}
