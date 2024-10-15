package com.ruoyi.colorfulfog.utils;

import com.ruoyi.colorfulfog.constant.enums.ExecutionTimeUnit;
import com.ruoyi.colorfulfog.constant.enums.TimeUnitEnum;
import com.ruoyi.colorfulfog.model.entity.DateRange;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtils {
    public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String YEAR_MONTH_PATTERN = "yyyy-MM";


    /**
     * 获得当天零时零分零秒
     *
     * @return Long
     */
    public static Long initDateByDay() {
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
     *
     * @return Long
     */
    public static Long initDateByMon() {
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
     *
     * @return Long
     */
    public static Long initDateByMonCal(int move, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return getMonCal(move, day, calendar);
    }

    public static Long initDateByMonCal(Long time, int move, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return getMonCal(move, day, calendar);
    }

    @NotNull
    private static Long getMonCal(int move, int day, Calendar calendar) {
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
     *
     * @return Long
     */
    public static Long initDateByDayCal(int move, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return getDayCal(move, hour, calendar);
    }

    public static Long initDateByDayCal(Long time, int move, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return getDayCal(move, hour, calendar);
    }

    @NotNull
    private static Long getDayCal(int move, int hour, Calendar calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + move);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime(); // 第一次getTime是Date，第二次是Date->Long
    }

    /**
     * 拿到今天的日期
     *
     * @return yymmdd格式
     */
    public static String getToday(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static String timeToTimeStr(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Date date = new Date(time);
        return sdf.format(date);

    }

    /**
     * 获取date的月份的时间范围
     *
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
     *
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
     *
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
     *
     * @return
     */
    public static DateRange getThisMonth() {
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
     *
     * @return
     */
    public static DateRange getLastMonth() {
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
     *
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

    private static void setMinTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setMaxTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String format(long date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当前的小时是多少
     *
     * @return
     */
    public static int getHour() {
        long time = System.currentTimeMillis();
        return (int) (time / (3600000) % 24) + 8;
    }

    /**
     * 获取当天是几号
     *
     * @return
     */
    public static int getDay() {
        Date date = new Date();
        // 格式化date形式为yyyy-mm-dd返回字符串的形式
        String dateStr = format(date, "yyyy-MM-dd");
        //截取dateStr最后两个dd,转换成int格式
        return Integer.parseInt(dateStr.substring(dateStr.length() - 2));
    }

    public static String getMon(Long timeStamp) {
        Date date = new Date(timeStamp);
        // 格式化date形式为yyyy-mm-dd返回字符串的形式
        String dateStr = format(date, "yyyy-MM-dd");
        //截取dateStr前7个字符
        return dateStr.substring(0, 7);

    }

    /**
     * 传入自定义的时间公式,格式为mm.dd，返回时间戳
     *
     * @param timeFormula
     * @return
     */
    public static Long transTimeFormula(String timeFormula, ExecutionTimeUnit executionTimeUnit) {
        List<String> arr = Arrays.asList(timeFormula.split("\\."));
        if (executionTimeUnit.equals(ExecutionTimeUnit.DAILY)) {
            return initDateByDayCal(Integer.parseInt(arr.get(0)), Integer.parseInt(arr.get(1)));
        } else if (executionTimeUnit.equals(ExecutionTimeUnit.MONTHLY)) {
            return initDateByMonCal(Integer.parseInt(arr.get(0)), Integer.parseInt(arr.get(1)));
        }
        throw new RuntimeException("时间公式格式错误");
    }

    public static String transCostTermFormula(String costTermFormula) {
        return getMon(initDateByMonCal(Integer.parseInt(costTermFormula), 1));
    }

    /**
     * 由于在时间范围中的落档中，范围是一个等差数列，所以传入目标值target，传入首项a,公差b，即可根据公式返回刚好比目标值小的数
     *
     * @return 转换后，时间戳所属范围的起始值
     */
    public static String transTimeToTimeRange(Integer target, Integer a, Integer b) {
        return String.valueOf(a + (target - a) / b * b);
    }

    public static String transTimeToTimeRange(Long target, Long a, Long b) {
        Long timeStamp = a + (target - a) / b * b;
        //转换时间格式:年-月-日
        return format(timeStamp, "yyyy-MM-dd HH:mm:ss");
//        return String.valueOf(timeStamp);
    }

    /**
     * 根据不同的时间单位，对数据进行转换
     * 如果传入的是年为单位，则目标值转换为年的单位，如1722388226266是在2024年，起始值是-5表示-5年，步长是1，表示统计的数据范围在2019-2024年之间
     * 如果是月则将目标值转换为月的单位，如1722388226266是2024年7月，起始值是-10表示-10月，步长是1，表示统计的数据范围在2023年10月之后
     * 如果是周、天、小时、分钟，操作都是一样，将其实值的公式和步长公式转换为时间戳，然后进行计算
     *
     * @param target
     * @param a
     * @param b
     * @param executionTimeUnit
     * @return
     */
    public static String transTimeToTimeRangeWithUnit(Long target, Integer a, Integer b, TimeUnitEnum executionTimeUnit, Date flagTime) {
        if (executionTimeUnit.equals(TimeUnitEnum.YEAR) || executionTimeUnit.equals(TimeUnitEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(target));
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(flagTime);
            calendar2.set(executionTimeUnit.getCalendarNum(), calendar.get(executionTimeUnit.getCalendarNum()) + a);
            return transTimeToTimeRange(calendar.get(executionTimeUnit.getCalendarNum()), a, b);
        } else if (executionTimeUnit.equals(TimeUnitEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(target));
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(flagTime);
            calendar2.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + a);
            return transTimeToTimeRange(calendar.get(Calendar.MONTH), calendar2.get(Calendar.MONTH), b);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flagTime);
            if (!executionTimeUnit.equals(TimeUnitEnum.HOUR)) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(executionTimeUnit.getCalendarNum(), calendar.get(executionTimeUnit.getCalendarNum()) + a);
            return transTimeToTimeRange(target, calendar.getTime().getTime(), b * executionTimeUnit.getTimeStamp());
        }
    }

    /**
     * 初始化收集数据的Map,按照等比数列进行初始化
     *
     * @param a 首项
     * @param b 公差
     * @param c 尾项
     * @return
     */
    public static void initCollectResultMap(Map<String, Map<String, Object>> collectGroupMap, Integer a, Integer b,
                                            Integer c, TimeUnitEnum timeUnit, Date flagTime) {
        if (!collectGroupMap.isEmpty()) {
            return;
        }
        if (timeUnit.equals(TimeUnitEnum.YEAR) || timeUnit.equals(TimeUnitEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flagTime);
            calendar.set(timeUnit.getCalendarNum(), calendar.get(timeUnit.getCalendarNum()) + a);
            Integer key = calendar.get(timeUnit.getCalendarNum());
            for (int i = a; i <= c; i += b) {
                Map<String, Object> collectResultMap = new HashMap<>();
                collectGroupMap.put(String.valueOf(key), collectResultMap);
                key+=b;
            }
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flagTime);
            if (!timeUnit.equals(TimeUnitEnum.HOUR)) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(timeUnit.getCalendarNum(), calendar.get(timeUnit.getCalendarNum()) + a);
            Long key = calendar.getTime().getTime();
            for (int i = a; i <= c; i += b) {
                Map<String, Object> collectResultMap = new HashMap<>();
                collectGroupMap.put(format(key, "yyyy-MM-dd HH:mm:ss"), collectResultMap);
                key+=b*timeUnit.getTimeStamp();
            }
        }
    }

    public static void initCollectResultMapDouble(Map<String, Map<String, Double>> collectGroupMap, Integer a, Integer b,
                                            Integer c, TimeUnitEnum timeUnit, Date flagTime) {
        if (!collectGroupMap.isEmpty()) {
            return;
        }
        if (timeUnit.equals(TimeUnitEnum.YEAR) || timeUnit.equals(TimeUnitEnum.MONTH)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flagTime);
            calendar.set(timeUnit.getCalendarNum(), calendar.get(timeUnit.getCalendarNum()) + a);
            Integer key = calendar.get(timeUnit.getCalendarNum());
            for (int i = a; i <= c; i += b) {
                Map<String, Double> collectResultMap = new HashMap<>();
                collectGroupMap.put(String.valueOf(key), collectResultMap);
                key+=b;
            }
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flagTime);
            if (!timeUnit.equals(TimeUnitEnum.HOUR)) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(timeUnit.getCalendarNum(), calendar.get(timeUnit.getCalendarNum()) + a);
            Long key = calendar.getTime().getTime();
            for (int i = a; i <= c; i += b) {
                Map<String, Double> collectResultMap = new HashMap<>();

                collectGroupMap.put(format(key, "yyyy-MM-dd HH:mm:ss"), collectResultMap);
                key+=b*timeUnit.getTimeStamp();
            }
        }
    }

    public static Long transFormalToLong(Integer formal, TimeUnitEnum timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (timeUnit.equals(TimeUnitEnum.DAY)) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        }
        return (long) formal;
    }

    // 测试
    public static void main(String[] args) {

        String mon = transCostTermFormula("1");
        System.out.println(mon);
    }
}
