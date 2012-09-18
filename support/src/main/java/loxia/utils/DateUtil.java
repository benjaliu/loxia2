/*
 * Copyright (c) 1999-2002 Erry Network Technology, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Erry
 * Network Technology, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Erry.
 *
 * ERRY MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. ERRY SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * $Archive: /src/work/com/erry/its/model/tools/DateUtil.java $ $Revision: 1.2 $
 */

package loxia.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provide conversion between Date and String
 *
 * @author $Author: benjamin $
 * @version $Date: 2008/04/16 11:11:44 $
 */

public class DateUtil {
	private static Map<String,DateFormat> dateFormatCache = new HashMap<String, DateFormat>();
    private static String defaultPattern;
    
    public static void applyPattern(String pattern){
    	defaultPattern = pattern;
    }
    /**
     * get the current time
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static Date today() {
    	return roundDate(now());
    }

    public static String format(Date date){
    	if(defaultPattern == null) throw new RuntimeException("Default Pattern is not initiated.");
    	return format(date, defaultPattern);
    }
    
    public static String format(Date date, String pattern) {
    	DateFormat df = dateFormatCache.get(pattern);
    	if(df == null){
    		df = new SimpleDateFormat(pattern);
    		dateFormatCache.put(pattern, df);
    	}
        return df.format(date);
    }   
    
    public static Date parse(String str) throws ParseException{
    	if(defaultPattern == null) throw new RuntimeException("Default Pattern is not initiated.");
    	return parse(str, defaultPattern);
    }
    
    public static Date parse(String str, String pattern) throws ParseException {
    	DateFormat df = dateFormatCache.get(pattern);
    	if(df == null){
    		df = new SimpleDateFormat(pattern);
    		dateFormatCache.put(pattern, df);
    	}
    	return df.parse(str);
    }
    
    public static Date addYear(Date date, int num) {
        return addTimeIntervals(date, Calendar.YEAR, num);
    }
    
    public static Date addMonths(Date date, int num) {
        return addTimeIntervals(date, Calendar.MONTH, num);
    }

    /**
     * Add specified number of days to a date.
     */
    public static Date addDays(Date date, int num) {
        return addTimeIntervals(date, Calendar.DATE, num);
    }
    
    public static Date addHours(Date date, int num){
    	return addTimeIntervals(date, Calendar.HOUR, num);
    }
    
    public static Date addMinutes(Date date, int num){
    	return addTimeIntervals(date, Calendar.MINUTE, num);
    }
    
    public static Date addSeconds(Date date, int num){
    	return addTimeIntervals(date, Calendar.SECOND, num);
    }
    
    private static Date addTimeIntervals(Date date, int type, int num){
    	if(date == null) throw new IllegalArgumentException();
    	Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(type, num);
        return c.getTime();
    }

    /**
     * Set hour, minute, second, millisecond of _c to 0.
     */
    public static Calendar roundCalendar(Calendar _c) {
        _c.set(Calendar.HOUR_OF_DAY, 0);
        _c.set(Calendar.MINUTE, 0);
        _c.set(Calendar.SECOND, 0);
        _c.set(Calendar.MILLISECOND, 0);
        return _c;
    }

    /**
     * Set hour, minute, second, millisecond of _c to 0.
     */
    public static Date roundDate(Date _d) {
        Calendar c = Calendar.getInstance();
        c.setTime(_d);
        return roundCalendar(c).getTime();
    }

    /**
     * convert to the sql date
     */
    public static java.sql.Date toSQLDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * convert to the utility date
     */
    public static Date toUtilDate(java.sql.Date date) {
        return new Date(date.getTime());
    }


    /**
     * Get the interval days of the two date.If return result is less than 0,
     * date _one is before _two; if the return result is greater than 0,
     * date _one is after _two.
     */
    public static int getInterval(Date _one, Date _two) {
        Calendar one = Calendar.getInstance();
        one.setTime(_one);
        Calendar two = Calendar.getInstance();
        two.setTime(_two);
        //System.out.println("One date is " + _one.toString() + ", and two date is " + _two.toString());

        int yearOne = one.get(Calendar.YEAR);
        int yearTwo = two.get(Calendar.YEAR);
        int dayOne = one.get(Calendar.DAY_OF_YEAR);
        int dayTwo = two.get(Calendar.DAY_OF_YEAR);

        //System.out.println("One date is the number " + dayOne + " of " + yearOne);
        //System.out.println("Two date is the number " + dayTwo + " of " + yearTwo);

        if (yearOne == yearTwo) {
            return dayOne - dayTwo;
        } else if (yearOne < yearTwo) {
            int yearDays = 0;
            while (yearOne < yearTwo) {
                if (isLeapyear(yearOne)) {
                    yearDays += 366;
                } else {
                    yearDays += 365;
                }

                yearOne += 1;
            }
            return dayOne - yearDays - dayTwo;
        } else {
            int yearDays = 0;
            while (yearTwo < yearOne) {
                if (isLeapyear(yearTwo)) {
                    yearDays += 366;
                } else {
                    yearDays += 365;
                }

                yearTwo += 1;
            }
            return dayOne - dayTwo + yearDays;
        }
    }

    public static int getMonthInterval(Date _one, Date _two){
    	Calendar one = Calendar.getInstance();
        one.setTime(_one);
        Calendar two = Calendar.getInstance();
        two.setTime(_two);
        int yearInt = two.get(Calendar.YEAR) - one.get(Calendar.YEAR);
        int monthInt = two.get(Calendar.MONTH) - one.get(Calendar.MONTH);
        int dayInt = two.get(Calendar.DAY_OF_MONTH) - one.get(Calendar.DAY_OF_MONTH);
        return yearInt*12+monthInt+(dayInt>0 ? 1:0);
    }


    public static boolean isLeapyear(Date _date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(_date);
        int year = calendar.get(Calendar.YEAR);
        return isLeapyear(year);
    }


    public static boolean isLeapyear(int _year) {
        if (_year % 4 == 0) {
            if (_year % 100 == 0) {
                if (_year % 400 == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}