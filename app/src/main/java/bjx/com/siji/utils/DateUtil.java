package bjx.com.siji.utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 用于将String转化为时间
 * Created by zhaokaiqiang on 15/4/9.
 */
public class DateUtil {
    private static SimpleDateFormat sfd;

    /**
     * 转换日期格式到用户体验好的时间格式
     *
     * @param time 2015-04-11 12:45:06
     * @return
     */
    public static String second2Date(long time) {
        String text = "";
        if (sfd == null) {
            sfd = new SimpleDateFormat("yyyyMMdd");
        }
        long created = time * 1000;
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        //获取传入的日期
        int currentdate = Integer.parseInt(sfd.format(created));
        //获取今年第一天的日期
        int thisyear = calendar.get(Calendar.YEAR) * 10000 + 101;
        //获取昨天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int yesterday = Integer.parseInt(sfd.format(calendar.getTime()));
        //获取2天前的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int twoDayAgo = Integer.parseInt(sfd.format(calendar.getTime()));
        //获取3天前的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int threeDayAgo = Integer.parseInt(sfd.format(calendar.getTime()));
        Log.i("threeDayAgo", threeDayAgo + "");
        Log.i("twoDayAgo", twoDayAgo + "");
        Log.i("oneDayAgo", yesterday + "");
        Log.i("currentDate", currentdate + "");
        if (currentdate >= threeDayAgo) {
            if (currentdate == twoDayAgo) {
                text = "2天前";
            } else if (currentdate == yesterday) {
                text = "1天前";
            } else if (currentdate == threeDayAgo) {
                text = "3天前";
            } else {
                long difference = now - created;
                text = (/*difference >= 0 && */difference <= DateUtils.MINUTE_IN_MILLIS) ?
                        "刚刚" : DateUtils.getRelativeTimeSpanString(
                        created,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            }
        } else if (currentdate >= thisyear) {
            text = (currentdate + "").substring(4, 6) + "月" + (currentdate + "").substring(6) + "日";
        } else {
            text = (currentdate + "").substring(0, 4) + "年" + (currentdate + "").substring(4, 6) + "月" + (currentdate + "").substring(6) + "日";
        }
        return text;
    }


    /**
     * 将长时间格式字符串转换为时间 yyyy年MM月dd日 HH时mm分
     */
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分 ");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy年MM月dd日
     */
    public static String getDateYMD(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String second2FormatDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000));
    }
    /**
     * 得到当前时间
     * @param dateFormat 时间格式
     * @return 转换后的时间格式
     */
    public static String getStringToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将字符串型日期转换成日期
     * @param dateStr 字符串型日期
     * @param dateFormat 日期格式
     * @return
     */
    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 日期转字符串
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    /**
     * 日期转字符串
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(String date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    /**
     * 两个时间点的间隔时长（分钟）
     * @param before 开始时间
     * @param after 结束时间
     * @return 两个时间点的间隔时长（分钟）
     */
    public static long compareMin(Date before, Date after) {
        if (before == null || after == null) {
            return 0l;
        }
        long dif = 0;
        if(after.getTime() >= before.getTime()) {
            dif = after.getTime() - before.getTime();
        }else if(after.getTime() < before.getTime()){
            dif = after.getTime() + 86400000 - before.getTime();
        }
        dif = Math.abs(dif);
        return dif  / 60000;
    }


    public static long dateDiff(String startTime, String endTime,
                                String format, String str) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
                    + (min - day * 24 * 60) + "分钟" + sec + "秒。");
            System.out.println("hour=" + hour + ",min=" + min);

            if (str.equalsIgnoreCase("h")) {
                return hour;
            } else if (str.equalsIgnoreCase("m")){
                return min;
            }else if (str.equalsIgnoreCase("s")){
                  return sec;
            }



        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("h")) {
            return hour;
        } else {
            return min;
        }
    }


    /**
     * 获取指定时间间隔分钟后的时间
     * @param date 指定的时间
     * @param min 间隔分钟数
     * @return 间隔分钟数后的时间
     */
    public static Date addMinutes(Date date, int min) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    /**
     * 根据时间返回指定术语，自娱自乐，可自行调整
     * @param hourday 小时
     * @return
     */
    public static String showTimeView(int hourday) {
        if(hourday >= 22 && hourday <= 24){
            return "晚上";
        }else if(hourday >= 0 && hourday <= 6 ){
            return  "凌晨";
        }else if(hourday > 6 && hourday <= 12){
            return "上午";
        }else if(hourday >12 && hourday < 22){
            return "下午";
        }
        return null;
    }

    //获取指定时间的前一天Date 使用set()方法回滚  使用 backTwo("20151231")
    private static void backTwo(String date) throws ParseException {
        //解析指定Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date parseDate = simpleDateFormat.parse(date);

        //设置Calendar
        Calendar calendarDate = GregorianCalendar.getInstance();
        calendarDate.setTime(parseDate);

        int day = calendarDate.get(Calendar.DATE);
        calendarDate.set(Calendar.DATE, day - 1);
    }


}
