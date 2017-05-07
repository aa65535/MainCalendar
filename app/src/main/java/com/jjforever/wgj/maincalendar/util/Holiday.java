package com.jjforever.wgj.maincalendar.util;

import com.jjforever.wgj.maincalendar.Model.ICalendarRecord;
import com.jjforever.wgj.maincalendar.Model.LunarHoliday;
import com.jjforever.wgj.maincalendar.Model.SolarHoliday;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Wgj on 2016/8/16.
 * 本类定义了一些常用的公历及农历节日
 * 不常用的就不做定义了。。。
 */
public final class Holiday {
    private static final Normal[] solarHolidays = {
            new Normal("元旦", 1, 1),
            new Normal("情人节", 2, 14),
            new Normal("妇女节", 3, 8),
            new Normal("植树节", 3, 12),
            new Normal("消费者权益日", 3, 15),
            new Normal("愚人节", 4, 1),
            new Normal("劳动节", 5, 1),
            new Normal("青年节", 5, 4),
            new Normal("儿童节", 6, 1),
            new Normal("建党节", 7, 1),
            new Normal("建军节", 8, 1),
            new Normal("教师节", 9, 10),
            new Normal("国庆节", 10, 1),
            new Normal("平安夜", 12, 24),
            new Normal("圣诞节", 12, 25),
    };

    private static final Normal[] lunarHolidays = {
            new Normal("春节", 1, 1),
            new Normal("元宵节", 1, 15),
            new Normal("龙抬头", 2, 2),
            new Normal("端午节", 5, 5),
            new Normal("七夕", 7, 7),
            new Normal("中元节", 7, 15),
            new Normal("中秋节", 8, 15),
            new Normal("重阳节", 9, 9),
            new Normal("腊八节", 12, 8),
            new Normal("小年", 12, 23),
    };

    private static final Weekly[] weekHolidays = {
            new Weekly("母亲节", 5, 2, 1),
            new Weekly("父亲节", 6, 3, 1),
            new Weekly("感恩节", 11, 4, 5),
    };

    /**
     * 根据日期获取阳历假日信息
     *
     * @param date 日历信息
     * @return 如果有阳历假日信息返回对应字符串，没有返回null
     */
    public static SolarHoliday getSolarHoliday(LunarCalendar date) {
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);

        for (Normal holiday : solarHolidays) {
            if (holiday.month == month && holiday.day == day) {
                return new SolarHoliday(holiday.name);
            }
        }

        return null;
    }

    /**
     * 根据日期获取农历假日信息
     *
     * @param date 日历信息
     * @return 如果有农历假日信息则返回对应字符串，没有返回null
     */
    public static LunarHoliday getLunarHoliday(LunarCalendar date) {
        int month = date.get(LunarCalendar.LUNAR_MONTH);
        int day = date.get(LunarCalendar.LUNAR_DATE);
        if (month < 0) {
            // 小于0为闰月，无节日
            return null;
        }

        for (Normal holiday : lunarHolidays) {
            if (holiday.month == month && holiday.day == day) {
                return new LunarHoliday(holiday.name);
            }
        }

        // 计算除夕
        if (month == 12) {
            if (day == LunarCalendar.daysInLunarMonth(date.get(LunarCalendar.LUNAR_YEAR), month)) {
                // 12月的最后一天为除夕
                return new LunarHoliday("除夕");
            }
        }

        return null;
    }

    /**
     * 根据日期返回按星期计算的阳历节日
     *
     * @param date 日历信息
     * @return 节日字符串
     */
    public static SolarHoliday getWeekHoliday(LunarCalendar date) {
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_WEEK);
        //  不能用WEEK_IN_MONTH获取
        int weeks = date.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        for (Weekly holiday : weekHolidays) {
            if (month == holiday.month && weeks == holiday.weeks
                    && day == holiday.day) {
                return new SolarHoliday(holiday.name);
            }
        }

        return null;
    }

    /**
     * 根据日历信息获取节假日信息
     *
     * @param date 日历信息
     * @return 节假日信息，多个使用/分割
     */
    public static ArrayList<ICalendarRecord> getHolidays(LunarCalendar date) {
        ArrayList<ICalendarRecord> tmpLst = new ArrayList<>();

        // 农历节日
        ICalendarRecord tmpRecord = getLunarHoliday(date);
        if (tmpRecord != null) {
            tmpLst.add(tmpRecord);
        }
        // 阳历节日
        tmpRecord = getSolarHoliday(date);
        if (tmpRecord != null) {
            tmpLst.add(tmpRecord);
        }
        // 星期节日
        tmpRecord = getWeekHoliday(date);
        if (tmpRecord != null) {
            tmpLst.add(tmpRecord);
        }

        // 没有则返回空
        return tmpLst.isEmpty() ? null : tmpLst;
    }

    static class Normal {
        String name;
        int month; // 月份
        int day; // 日期

        public Normal(String name, int month, int day) {
            this.name = name;
            this.month = month;
            this.day = day;
        }
    }

    static class Weekly {
        String name;
        int month; // 月份
        int weeks; // 第几周
        int day; // 周几

        public Weekly(String name, int month, int weeks, int day) {
            this.name = name;
            this.month = month;
            this.weeks = weeks;
            this.day = day;
        }
    }
}
