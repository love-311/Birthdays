package com.love311.www.birthdays.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.widget.Toast;

public class OtherUtil {

	private static long exitTime = 0;
	public static void ExitApp(Context context) {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			ActivityMeg.getInstance().exit();
		}

	}

	/***
	 *
	 * 得到天干地支
	 *
	 * @return
	 */
	public static String[] getYera() {
		String str[] = new String[107];

		int index1 = 1942;
		// int num = year - 1900 + 36;
		for (int i = 0; i < str.length; i++) {
			str[i] = cyclicalm(index1 - 1900 + 36)+"年("+index1+")";
			index1++;
		}
		return str;
	}

	// ====== 传入 月日的offset 传回干支, 0=甲子
	public static String cyclicalm(int num) {
		final String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己", "庚",
				"辛", "壬", "癸" };
		final String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳", "午",
				"未", "申", "酉", "戌", "亥" };
		return (Gan[num % 10] + Zhi[num % 12]);
	}

	/***
	 * 返回离生日还有多少天
	 *
	 * @param year
	 * @param mouth
	 * @param date
	 * @return
	 */
	public static long getForMyBrithday(String year, String mouth, String date) {
		int month = Integer.parseInt(mouth);
		int date1 = Integer.parseInt(date);
		int year1 = Integer.parseInt(year);
		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.setTime(new Date());

		long now = calendar.getTimeInMillis();
		if (calendar.get(Calendar.MONTH) + 1 > month) {
			calendar.add(Calendar.YEAR, 1);
		}
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, date1);
		long nextBirthMillionSeconds = calendar.getTimeInMillis();
		long kk = now + nextBirthMillionSeconds;
		Date date2 = new Date();
		date2.setTime(kk);

		long julianDay = (nextBirthMillionSeconds - now)
				/ (1000 * 60 * 60 * 24);
		if (julianDay < 0) {
			if (isGregorianLeapYear(year1)) {
				return julianDay + 366;
			} else {
				return julianDay + 365;
			}
		}
		return julianDay;
	}

	/***
	 * 返回多少天后的具体日期
	 *
	 * @return
	 */
	public static String getForMyBrithdayToDate(long day) {

		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.setTime(new Date());

		long now = calendar.getTimeInMillis();

		long nextBirthMillionSeconds = day * 24 * 60 * 60 * 1000;
		long kk = now + nextBirthMillionSeconds;
		Date date2 = new Date();
		date2.setTime(kk);
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, date2.getYear() + 1900);
		ca.set(Calendar.MONTH, date2.getMonth() + 1 - 1);// 也可以填数字，0-11,一月为0
		ca.set(Calendar.DAY_OF_MONTH, date2.getDay() + 1);
		Lunner kkq = new Lunner(ca);
		String str = (date2.getYear() + 1900) + "年" + (date2.getMonth() + 1)
				+ "月" + (date2.getDay() + 1) + "日("+kkq.toString()+")";
		return str;
	}

	/***
	 * 返回生日那天是星期几
	 *
	 * @param year
	 * @param mouth
	 * @param date
	 * @return
	 */
	public static String getDateIsweekDay(String year, String mouth, String date) {
		int month = Integer.parseInt(mouth);
		int date1 = Integer.parseInt(date);
		int year1 = Integer.parseInt(year);
		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.set(Calendar.YEAR, year1);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, date1);
		String weekDay = new SimpleDateFormat("E").format(calendar.getTime());
		return weekDay;
	}

	// 判断是否是闰年
	public static boolean isGregorianLeapYear(int year) {
		boolean isLeap = false;
		if (year % 4 == 0)
			isLeap = true;
		if (year % 100 == 0)
			isLeap = false;
		if (year % 400 == 0)
			isLeap = true;
		return isLeap;
	}

	/***
	 *
	 * 通过出生日期算星座
	 *
	 * @param mouth
	 * @param date
	 * @return
	 */
	public static String getconstellation(int mouth, int date) {
		String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
		int arr[] = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
		if (mouth <= 0 || date <= 0) {
			return "";
		}
		int index = 2 * mouth - (date < arr[mouth - 1] ? 2 : 0);
		String str = s.substring(index, 2 + index);
		return str + "座";
	}

	/***
	 * 根据出生年份算出本年的生肖
	 *
	 * @param year
	 * @return
	 */
	public static String getAnimals(int year) {
		String s = "猪鼠牛虎兔龙蛇马羊猴鸡狗猪";
		int index = (year % 12) - 3;

		if (index < 0) {
			return Character.toString(s.charAt(index + 12));
		} else {
			return Character.toString(s.charAt(index));
		}
	}

	/***
	 *
	 * 返回今年的年份
	 *
	 * @return
	 */
	public static int getCurYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

}
