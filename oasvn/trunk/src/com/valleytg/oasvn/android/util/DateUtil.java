package com.valleytg.oasvn.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;

public class DateUtil {
	  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	  public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM-dd h:mm a");
	  

	  public static String getStringNow() {
		  Calendar cal = Calendar.getInstance();
		  Date date = new Date();
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  return sdf.format(date);

	  }
	  
	  public static String getString(Date date) {
		  Calendar cal = Calendar.getInstance();
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  return sdf.format(date);
	  }
	  
	  public static String getGMTStringNow() {
		  Calendar cal = Calendar.getInstance();
		  Date date = new Date();
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		  return sdf.format(date);
	  }
	  
	  public static Date getGMTNow() {
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  try {
			return sdf.parse(getGMTStringNow());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	  }
	  
	  public static String getGMTString(Date date) {
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		  return sdf.format(date);
	  }
	  
	  public static Date getDateNow() {
		  Calendar cal = Calendar.getInstance();
		  return cal.getTime();
	  }
	  
	  public static Date toDate(String strDate) {
		  Calendar cal = Calendar.getInstance();
		  SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		  try {
			  return sdf.parse(strDate);
		  }
		  catch(ParseException ex) {
			  return cal.getTime();
		  }
	  }
	  
	  /**
	   * <p>Returns a string for the date and time (simple format) that is localized to the 
	   * users current timezone.  It also compensates for the offset from UTC that the
	   * timestamps are stored in.</p>
	   * 
	   * <p>It should be used to format any time needing this format that will be output 
	   * to the view layer.</p>
	   * 
	   * @param date
	   * @param context
	   * @return
	   */
	  public static String getLocalizedSimpleDateTime(Date date, Context context) {
		  java.text.DateFormat df = DateFormat.getDateFormat(context);
		  java.text.DateFormat tf = DateFormat.getTimeFormat(context);
		  TimeZone tz = TimeZone.getDefault();
		  Date modTime = new Date(date.getTime() + tz.getOffset(date.getTime()));
		  
		  return df.format(modTime) + " " + tf.format(modTime);
	  }
	  
	  public static String getLocalizedShortSimpleDateTime(Date date, Context context) {
		  java.text.DateFormat df = DateFormat.getDateFormat(context);
		  java.text.DateFormat tf = DateFormat.getTimeFormat(context);
		  TimeZone tz = TimeZone.getDefault();
		  Date modTime = new Date(date.getTime() + tz.getOffset(date.getTime()));
		  
		  String strModTime = shortDateFormat.format(modTime);
		  
		  strModTime = strModTime.substring(0, 2) + "/" + strModTime.substring(3);
		  
		  return strModTime;
	  }
	  
	  /**
	   * <p>Returns a Date object for the date and time (simple format) that is localized to the 
	   * users current timezone.  It also compensates for the offset from UTC that the
	   * timestamps are stored in.</p>
	   * 
	   * <p>It should be used to format any time needing manipulation that will be output 
	   * to the view layer.</p>
	   * 
	   * @param date
	   * @param context
	   * @return Date
	   */
	  public static Date getLocalizedDateTime(Date date, Context context) {
		  java.text.DateFormat df = DateFormat.getDateFormat(context);
		  java.text.DateFormat tf = DateFormat.getTimeFormat(context);
		  TimeZone tz = TimeZone.getDefault();
		  Date modTime = new Date(date.getTime() + tz.getOffset(date.getTime()));
		  return modTime;
	  }
	  
	  /**
	   * Returns the date and time in a simple format.  This is non-localized
	   * @param date
	   * @param context
	   * @return
	   */
	  public static String getSimpleDateTime(Date date, Context context) {
		  java.text.DateFormat df = DateFormat.getDateFormat(context);
		  java.text.DateFormat tf = DateFormat.getTimeFormat(context);
		  
		  Date modTime = new Date(date.getTime());
		  
		  String strModTime = shortDateFormat.format(modTime);
		  strModTime = strModTime.substring(0, 2) + "/" + strModTime.substring(3);
		  
		  return df.format(modTime) + " " + tf.format(modTime);
	  }
	 
}
