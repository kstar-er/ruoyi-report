package com.ruoyi.colorfulfog.utils;

import com.ruoyi.colorfulfog.constant.enums.ExecutionTimeUnit;
import com.ruoyi.colorfulfog.model.entity.DateRange;
import io.swagger.models.auth.In;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {
    public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String YEAR_MONTH_PATTERN = "yyyy-MM";


    /**
     * 获得当天零时零分零秒
     * @return Long
     */
    public static Long initDateByDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime(); // 第一次getTime是Date，第二次是Date->Long
    }

    /**
     * 获得当月零时零分零秒
     * @return Long
     */
    public static Long initDateByMon(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime(); // 第一次getTime是Date，第二次是Date->Long
    }

    /**
     * 获得传入参数变动的月零时零分零秒
     * @return Long
     */
    public static Long initDateByMonCal(int move,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + move);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime(); // 第一次getTime是Date，第二次是Date->Long
    }

    /**
     * 获得传入参数变动的某日某时零分零秒
     * @return Long
     */
    public static Long initDateByDayCal(int move,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) );
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+move);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime(); // 第一次getTime是Date，第二次是Date->Long
    }

    public static Long getNowDateStamp(){
        return System.currentTimeMillis();
    }

    /**
     * 拿到今天的日期
     * @return yymmdd格式
     */
    public static String getToday(String pattern) {
        SimpleDateFormat formatter= new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String timeToTimeStr(Long time){
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Date date = new Date(time);
        return sdf.format(date);

    }

    /**
     * 获取date的月份的时间范围
     * @param date
     * @return
     */
    public static DateRange getMonthRange(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMaxTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }
    /**
     * 获取当前季度的时间范围
     * @return current quarter
     */
    public static DateRange getThisQuarter() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3) * 3);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3) * 3 + 2);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }

    /**
     * 获取昨天的时间范围
     * @return
     */
    public static DateRange getYesterdayRange() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DAY_OF_MONTH, -1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }

    /**
     * 获取当前月份的时间范围
     * @return
     */
    public static DateRange getThisMonth(){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }

    /**
     * 获取上个月的时间范围
     * @return
     */
    public static DateRange getLastMonth(){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH, -1);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }

    /**
     * 获取上个季度的时间范围
     * @return
     */
    public static DateRange getLastQuarter() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3 - 1) * 3);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.MONTH, ((int) endCalendar.get(Calendar.MONTH) / 3 - 1) * 3 + 2);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new DateRange(startCalendar.getTime(), endCalendar.getTime());
    }

    private static void setMinTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setMaxTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    public static String format(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    public static String format(long date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当前的小时是多少
     * @return
     */
    public static int getHour(){
        long time = System.currentTimeMillis();
        return (int) (time/(3600000)%24)+8;
    }

    /**
     * 获取当天是几号
     * @return
     */
    public static int getDay(){
        Date date = new Date();
        // 格式化date形式为yyyy-mm-dd返回字符串的形式
        String dateStr = format(date, "yyyy-MM-dd");
        //截取dateStr最后两个dd,转换成int格式
        return Integer.parseInt(dateStr.substring(dateStr.length() - 2));
    }
    public static String getMon(Long timeStamp){
        Date date = new Date(timeStamp);
        // 格式化date形式为yyyy-mm-dd返回字符串的形式
        String dateStr = format(date, "yyyy-MM-dd");
        //截取dateStr前7个字符
        return dateStr.substring(0,7);

    }

    /**
     * 传入自定义的时间公式,格式为mm.dd，返回时间戳
     * @param timeFormula
     * @return
     */
    public static Long transTimeFormula(String timeFormula, ExecutionTimeUnit executionTimeUnit){
        List<String> arr = Arrays.asList(timeFormula.split("\\."));
        if(executionTimeUnit.equals(ExecutionTimeUnit.DAILY)){
            return initDateByDayCal(Integer.parseInt(arr.get(0)),Integer.parseInt(arr.get(1)));
        }else if (executionTimeUnit.equals(ExecutionTimeUnit.MONTHLY)){
            return initDateByMonCal(Integer.parseInt(arr.get(0)),Integer.parseInt(arr.get(1)));
        }
        throw new RuntimeException("时间公式格式错误");
    }
    public static String transCostTermFormula(String costTermFormula){
        return getMon(initDateByMonCal(Integer.parseInt(costTermFormula),1));
    }
    // 测试
    public static void main(String[] args) {

        String mon = transCostTermFormula("1");
        System.out.println(mon);
    }
}
