package bigscreen.hubpd.com.utils;

import com.sun.xml.internal.ws.util.UtilException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间处理工具类
 * Created by ceek on 2018-08-09 22:37.
 */
public class DateUtils {

    /**
     * 获取前/后多少天的时间字符串
     *
     * @param sourceDate  初始时间
     * @param endGap      前后多少
     * @param datePattern 时间字符串返回模式(yyyy-MM-dd)
     * @return
     */
    public static String getBeforeDateStrByDateAndPattern(Date sourceDate, Integer endGap, String datePattern) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        Calendar c = Calendar.getInstance();
        c.setTime((Date) sourceDate.clone());
        c.add(Calendar.DATE, endGap);
        String endtimeStr = dateFormat.format(c.getTime());
        return endtimeStr;
    }

    /**
     * 获取指定日期指定格式的字符串日期
     *
     * @param date    日期
     * @param pattern 指定格式
     * @return
     */
    public static String getDateStrByDate(Date date, String pattern) {
        String formatDateStr = new SimpleDateFormat(pattern).format(date);
        return formatDateStr;
    }

    /**
     * 得到两个日期相隔天数
     *
     * @param firstDate  第一个时间
     * @param secondDate 第二个时间
     * @return
     */
    public static long getNumByTwoDate(Date firstDate, Date secondDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long firstTime = df.parse(df.format(firstDate)).getTime();
            long secondTime = df.parse(df.format(secondDate)).getTime();
            if (firstTime >= secondTime) {
                return (firstTime - secondTime) / (1000 * 60 * 60 * 24);
            } else {
                return (secondTime - firstTime) / (1000 * 60 * 60 * 24);
            }
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }

    /**
     * 获取两个日期之间的所有日期
     * @param startTime     开始日期
     * @param endTime       结束日期
     * @return
     */
    public static List<String> getDuringDaysString(String startTime, String endTime, String datePattern) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     * 获取指定格式的当前时间的变量count小时
     * @param beforeHourCount
     * @param datePattern
     * @return
     */
    public static String getDayAndHourTimeOffset(int beforeHourCount, String datePattern){
        String returnstr = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + beforeHourCount);
        SimpleDateFormat df = new SimpleDateFormat(datePattern);
        returnstr = df.format(calendar.getTime());
        return returnstr;
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(DateUtils.getBeforeDateStrByDateAndPattern(new Date(), -10, "yyyy-MM-dd"));
        System.out.println(DateUtils.getDuringDaysString("2019-04-01", "2019-04-08", "yyyy-MM-dd"));
//        System.out.println(DateUtils.getDayAndHourTimeOffset(-24, "yyyyMMdd HH"));
//        System.out.println(DateUtils.getDayAndHourTimeOffset(0, "yyyyMMdd HH"));
        String startDay = DateUtils.getBeforeDateStrByDateAndPattern(new Date(), -2, "yyyyMMdd");
        String endDay = DateUtils.getBeforeDateStrByDateAndPattern(new Date(), -1, "yyyyMMdd");
        System.out.println(DateUtils.getDuringDaysString(startDay, endDay, "yyyyMMdd"));
    }
}
