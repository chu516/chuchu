package bjx.com.siji.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import bjx.com.siji.model.ChargeModel;
import bjx.com.siji.model.CommentModel;
import bjx.com.siji.model.DriverModel;
import bjx.com.siji.model.DriverNearbyModel;
import bjx.com.siji.model.MyOrderModel;
import bjx.com.siji.model.OrderList;
import bjx.com.siji.model.SalaryDetailModel;
import bjx.com.siji.model.TradeHisModel;
import bjx.com.siji.model.UserModel;
import bjx.com.siji.model.WXPayModel;

import java.util.List;
import java.util.Map;

/**
 * Created by jqc on 2017/11/7.
 */

public class GsonUtil {

    /**
     * javabean to json
     * @return
     */
    public static String javabeanToJson(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }

    /**
     * list to json
     *
     * @param list
     * @return
     */
    public static String listToJson(List<Object> list) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * map to json
     *
     * @param map
     * @return
     */
    public static String mapToJson(Map<String, Object> map) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    /**
     * json字符串转List集合
     */

    public static List<Object> jsonToList(String json) {

        Gson gson = new Gson();
        List<Object> objs = gson.fromJson(json, new TypeToken<List<Object>>() {}.getType());//对于不是类的情况，用这个参数给出
        return objs;
    }

    public static Map<String, Object> jsonToMap(String json) {
        // TODO Auto-generated method stub
        Gson gson = new Gson();
        Map<String, Object> maps = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
        return maps;
    }

    // json To Userbean
    public static UserModel jsonToUserBean(String json) {
        Gson gson = new Gson();
        UserModel object = gson.fromJson(json, UserModel.class);//对于javabean直接给出class实例
        return object;
    }

    // json To OrderBean
    public static OrderList jsonOrderBean(String json) {
        Gson gson = new Gson();
        OrderList lisy = gson.fromJson(json, OrderList.class);//对于javabean直接给出class实例
        return lisy;
    }


    // json To Userbean
    public static ChargeModel jsonToModelBean(String json) {
        Gson gson = new Gson();
        ChargeModel object = gson.fromJson(json, ChargeModel.class);//对于javabean直接给出class实例
        return object;
    }

    // json To SalaryDetailBean
    public static SalaryDetailModel jsonSalaryDetailBean(String json) {
        Gson gson = new Gson();
        SalaryDetailModel lisy = gson.fromJson(json, SalaryDetailModel.class);//对于javabean直接给出class实例
        return lisy;
    }

    // json To SalaryDetailBean
    public static CommentModel jsonCommentBean(String json) {
        Gson gson = new Gson();
        CommentModel lisy = gson.fromJson(json, CommentModel.class);//对于javabean直接给出class实例
        return lisy;
    }


    public static WXPayModel jsonToWXPayBean(String json) {
        Gson gson = new Gson();
        WXPayModel object = gson.fromJson(json, WXPayModel.class);
        return object;
    }

    // json To TradeHisModel
    public static TradeHisModel jsonToTradeHisBean(String json) {
        Gson gson = new Gson();
        TradeHisModel object = gson.fromJson(json, TradeHisModel.class);
        return object;
    }

    public static MyOrderModel jsonToOrderBean(String json) {
        Gson gson = new Gson();
        MyOrderModel object = gson.fromJson(json, MyOrderModel.class);//对于javabean直接给出class实例
        return object;
    }

    public static DriverModel jsonToDriverBean(String json) {
        Gson gson = new Gson();
        DriverModel object = gson.fromJson(json, DriverModel.class);//对于javabean直接给出class实例
        return object;
    }

    public static DriverNearbyModel jsonToDriverNearbyBean(String json) {
        Gson gson = new Gson();
        DriverNearbyModel object = gson.fromJson(json, DriverNearbyModel.class);//对于javabean直接给出class实例
        return object;
    }
}
