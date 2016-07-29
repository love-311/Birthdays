package com.love311.www.birthdays.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.utils.ChineseCalendar;
import com.love311.www.birthdays.utils.Util;

import java.util.Calendar;


public class GregorianLunarCalendarView extends LinearLayout implements OnValueChangeListener{

	private static final int YEAR_START = 1901;
	private static final int YEAR_STOP = 2100;
	private static final int YEAR_SPAN = YEAR_STOP - YEAR_START + 1;
	
	private static final int MONTH_START = 1;
	private static final int MONTH_START_GREGORIAN = 1;
	private static final int MONTH_STOP_GREGORIAN = 12;
	private static final int MONTH_SPAN_GREGORIAN = MONTH_STOP_GREGORIAN - MONTH_START_GREGORIAN + 1;

	private static final int MONTH_START_LUNAR = 1;
	
	private static final int MONTH_START_LUNAR_NORMAL = 1;
	private static final int MONTH_STOP_LUNAR_NORMAL = 12;
	private static final int MONTH_SPAN_LUNAR_NORMAL = MONTH_STOP_LUNAR_NORMAL - MONTH_START_LUNAR_NORMAL + 1;
	
	private static final int MONTH_START_LUNAR_LEAP = 1;
	private static final int MONTH_STOP_LUNAR_LEAP = 13;
	private static final int MONTH_SPAN_LUNAR_LEAP = MONTH_STOP_LUNAR_LEAP - MONTH_START_LUNAR_LEAP + 1;
	
	private static final int DAY_START = 1;
	private static final int DAY_STOP = 30;
	
	private static final int DAY_START_GREGORIAN = 1;
	private static final int DAY_STOP_GREGORIAN = 31;
	private static final int DAY_SPAN_GREGORIAN = DAY_STOP_GREGORIAN - DAY_START_GREGORIAN + 1;
	
	private static final int DAY_START_LUNAR = 1;
	private static final int DAY_STOP_LUNAR = 30;
	private static final int DAY_SPAN_LUNAR = DAY_STOP_LUNAR - DAY_START_LUNAR + 1;
	
	private NumberPicker picker_year;
	private NumberPicker picker_month;
	private NumberPicker picker_day;
	
	private String[] displayYearsGregorian;
	private String[] displayMonthsGregorian;
	private String[] displayDaysGregorian;
	private String[] displayYearsLunar;
	private String[] displayMonthsLunar;//只是包含了从一月到十二月这十二个月
	private String[] displayDaysLunar;
	
	private String[] currDisplayMonthsLunar;//当前年份所包含的月份
	
	private boolean mIsGregorian = true;//是否是公历
	
	private View mContentView;
	
	public GregorianLunarCalendarView(Context context) {
		this(context, null);
	}
	
	public GregorianLunarCalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public GregorianLunarCalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContentView = inflate(context, R.layout.view_gregorian_lunar_calendar, this);
        initViews(mContentView);
//        initConfigs(new ChineseCalendar());
	}
	
	public void initConfigs(Calendar c, boolean isGregorian){
		//超出范围处理
		if(!checkCalendarAvailable(c, YEAR_START, YEAR_STOP, isGregorian)){
			c = adjustCalendarByLimit(c, YEAR_START, YEAR_STOP, isGregorian);
		}
		mIsGregorian = isGregorian;
		ChineseCalendar cc = null;;
		if(c instanceof ChineseCalendar){
			cc = (ChineseCalendar)c;
		}else{
			cc = new ChineseCalendar(c);
		}
    	setDisplayValuesForAll(cc, mIsGregorian);
    }
	
	private Calendar adjustCalendarByLimit(Calendar c, int yearStart, int yearStop, boolean isGregorian){
		int yearGrego = c.get(Calendar.YEAR);
		if(isGregorian){
			if(yearGrego < yearStart){
				c.set(Calendar.YEAR, yearStart);
				c.set(Calendar.MONTH, MONTH_START_GREGORIAN);
				c.set(Calendar.DAY_OF_MONTH, DAY_START_GREGORIAN);
			}
			if(yearGrego > yearStop){
				c.set(Calendar.YEAR, yearStop);
				c.set(Calendar.MONTH, MONTH_STOP_GREGORIAN - 1);
				int daySway = Util.getSumOfDayInMonthForGregorianByMonth(yearStop, MONTH_STOP_GREGORIAN);
				c.set(Calendar.DAY_OF_MONTH, daySway);
			}
		}else{
			if(Math.abs(yearGrego - yearStart) < Math.abs(yearGrego - yearStop)){
				c = new ChineseCalendar(true, yearStart, MONTH_START_LUNAR, DAY_START_LUNAR);
			}else{
				int daySway = Util.getSumOfDayInMonthForLunarByMonthLunar(yearStop, MONTH_STOP_LUNAR_NORMAL);
				c = new ChineseCalendar(true, yearStop, MONTH_STOP_LUNAR_NORMAL, daySway);
			}
		}
		return c;
	}
	
    private void initViews(View contentView){
    	
    	picker_year  = (NumberPicker)contentView.findViewById(R.id.picker_year);
    	picker_month = (NumberPicker)contentView.findViewById(R.id.picker_month);
    	picker_day   = (NumberPicker)contentView.findViewById(R.id.picker_day);
    	
    	picker_year.setOnValueChangedListener(this); 
    	picker_month.setOnValueChangedListener(this);
    	picker_day.setOnValueChangedListener(this); 
    }

    private void setDisplayValuesForAll(ChineseCalendar cc, boolean isGregorian){
        setDisplayData(isGregorian);
        initValuesForY(cc, isGregorian);
        initValuesForM(cc, isGregorian);
        initValuesForD(cc, isGregorian);
    }
	
    /**
     * 
     * @param isGregorian
     * 			是否是公历，true为公历，false为农历
     */
    private void setDisplayData(boolean isGregorian){
    	
    	if(isGregorian){
	    	if(displayYearsGregorian == null){
	    		displayYearsGregorian = new String[YEAR_SPAN];
	         	for(int i = 0; i < YEAR_SPAN; i++){
	         		displayYearsGregorian[i] = String.valueOf(YEAR_START + i) + "年";
	         	}
	    	}
	    	
	    	if(displayMonthsGregorian == null){
	    		displayMonthsGregorian = new String[MONTH_SPAN_GREGORIAN];
	    		for(int i = 0; i < MONTH_SPAN_GREGORIAN; i++){
	    			displayMonthsGregorian[i] = String.valueOf(MONTH_START_GREGORIAN + i) + "月";
	         	}
	    	}
	    	
	    	if(displayDaysGregorian == null){
	    		displayDaysGregorian = new String[DAY_SPAN_GREGORIAN];
	    		for(int i = 0; i < DAY_SPAN_GREGORIAN; i++){
	    			displayDaysGregorian[i] = String.valueOf(DAY_START_GREGORIAN + i) + "日";
	         	}
	    	}
    	}else{

    		if(displayYearsLunar == null){
    			displayYearsLunar = new String[YEAR_SPAN];
	         	for(int i = 0; i < YEAR_SPAN; i++){
	         		displayYearsLunar[i] = Util.getLunarNameOfYear(i + YEAR_START);
	         	}
	    	}
	    	
	    	if(displayMonthsLunar == null){
	    		displayMonthsLunar = new String[MONTH_SPAN_GREGORIAN];
	    		for(int i = 0; i < MONTH_SPAN_GREGORIAN; i++){
	    			displayMonthsLunar[i] = Util.getLunarNameOfMonth(i + 1);
	         	}
	    	}
	    	
	    	if(displayDaysLunar == null){
	    		displayDaysLunar = new String[DAY_SPAN_LUNAR];
	    		for(int i = 0; i < DAY_SPAN_LUNAR; i++){
	    			displayDaysLunar[i] = Util.getLunarNameOfDay(i + 1);
	         	}
	    	}
    	}
    }
    
    private void initValuesForY(ChineseCalendar cc, boolean isGregorian){
    	if(isGregorian){
    		int yearSway = cc.get(Calendar.YEAR);
    		setValuesForPickerView(picker_year, yearSway, YEAR_START, YEAR_STOP, displayYearsGregorian);
    	}else{
    		int yearSway = cc.get(ChineseCalendar.CHINESE_YEAR);
    		setValuesForPickerView(picker_year, yearSway, YEAR_START, YEAR_STOP, displayYearsLunar);
    	}
    	picker_year.setWrapSelectorWheel(false);
    }
    
    private void initValuesForM(ChineseCalendar cc, boolean isGregorian){
    	
    	int monthStart = 0;
    	int monthStop = 0;
    	int monthSway = 0;
    	String[] newDisplayedVales = null;
    	
    	if(isGregorian){
        	monthStart = MONTH_START_GREGORIAN;
        	monthStop = MONTH_STOP_GREGORIAN;
        	monthSway = cc.get(Calendar.MONTH) + 1;
        	newDisplayedVales = displayMonthsGregorian;
    	}else{
    		int monthLeap = Util.getMonthLeapByYear(cc.get(ChineseCalendar.CHINESE_YEAR));
    		if(monthLeap == 0){
    			monthStart = MONTH_START_LUNAR_NORMAL;
    			monthStop = MONTH_STOP_LUNAR_NORMAL;
    			monthSway = cc.get(ChineseCalendar.CHINESE_MONTH);
    			newDisplayedVales = displayMonthsLunar;
    		}else{
    			monthStart = MONTH_START_LUNAR_LEAP;
    			monthStop = MONTH_STOP_LUNAR_LEAP;
    			monthSway = Util.convertMonthLunarToMonthSway(cc.get(ChineseCalendar.CHINESE_MONTH), monthLeap);
    			newDisplayedVales = Util.getLunarMonthsNamesWithLeap(monthLeap);
    		}
    	}
    	setValuesForPickerView(picker_month, monthSway, monthStart, monthStop, newDisplayedVales);
    }
    
    private void initValuesForD(ChineseCalendar cc, boolean isGregorian){
    	if(isGregorian){
    		int dayStart = DAY_START_GREGORIAN;
    		int dayStop = Util.getSumOfDayInMonthForGregorianByMonth(cc.get(Calendar.YEAR), cc.get(Calendar.MONTH) + 1);
        	int daySway = cc.get(Calendar.DAY_OF_MONTH);
        	setValuesForPickerView(picker_day, daySway, dayStart, dayStop, displayDaysGregorian);
    	}else{
    		int dayStart = DAY_START_LUNAR;
    		int dayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(cc.get(ChineseCalendar.CHINESE_YEAR), cc.get(ChineseCalendar.CHINESE_MONTH));
    		int daySway = cc.get(ChineseCalendar.CHINESE_DATE);
    		setValuesForPickerView(picker_day, daySway, dayStart, dayStop, displayDaysLunar);
    	}
    }
    
    private void setValuesForPickerView(NumberPicker pickerView, int newSway, int newStart, int newStop, String[] newDisplayedVales){
    	
    	if(newDisplayedVales == null){
    		throw new IllegalArgumentException("newDisplayedVales should not be null.");
    	}else if(newDisplayedVales.length == 0){
    		throw new IllegalArgumentException("newDisplayedVales's length should not be 0.");
    	}
    	int newSpan = newStop - newStart + 1;
    	if(newDisplayedVales.length < newSpan){
    		throw new IllegalArgumentException("newDisplayedVales's length should not be less than newSpan.");
    	}
    	
    	int oldStart = pickerView.getMinValue();
    	int oldStop = pickerView.getMaxValue();
    	int oldSpan = oldStop - oldStart + 1;
    	pickerView.setMinValue(newStart);
    	
    	if(newSpan > oldSpan){
    		pickerView.setDisplayedValues(newDisplayedVales);
    		pickerView.setMaxValue(newStop); 
    	}else{
    		pickerView.setMaxValue(newStop); 
    		pickerView.setDisplayedValues(newDisplayedVales);
    	}
    	pickerView.setValue(newSway);
    }

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		switch(picker.getId()){
		case R.id.picker_year:
			passiveUpdateMonthAndDay(oldVal, newVal, mIsGregorian);
			break;
		case R.id.picker_month:
			int fixYear = picker_year.getValue();
			passiveUpdateDay(fixYear, fixYear, oldVal, newVal, mIsGregorian);
			break;
		case R.id.picker_day:
			break;
		}
	}
	
	private void passiveUpdateMonthAndDay(int oldYearFix, int newYearFix, boolean isGregorian){
		
		int oldMonthSway = picker_month.getValue();
		int oldDaySway = picker_day.getValue();
		
		if(isGregorian){
			int newMonthSway = oldMonthSway;

			int oldDayStop = Util.getSumOfDayInMonth(oldYearFix, oldMonthSway, true);
			int newDayStop = Util.getSumOfDayInMonth(newYearFix, newMonthSway, true);
			
			if(oldDayStop == newDayStop){
				return;
			}
			int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
			setValuesForPickerView(picker_day, newDaySway, DAY_START, newDayStop, displayDaysGregorian);
			return;
		}else{
			int newMonthSway = 0;
			
			int newYearFixMonthLeap = Util.getMonthLeapByYear(newYearFix);//1.计算当前year是否有闰月
			int oldYearFixMonthLeap = Util.getMonthLeapByYear(oldYearFix);//2.计算之前year是否有闰月
			
			if(newYearFixMonthLeap == oldYearFixMonthLeap){
				//月视图不更新，直接更新日视图
				newMonthSway = oldMonthSway;

				int oldMonthLunar = Util.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
				int newMonthLunar = Util.convertMonthSwayToMonthLunar(newMonthSway, newYearFixMonthLeap);
				int oldDayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(oldYearFix, oldMonthLunar);
				int newDayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(newYearFix, newMonthLunar);
				
				if(oldDayStop == newDayStop){
					return;//不需要更新
				}else{
					int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
					setValuesForPickerView(picker_day, newDaySway, DAY_START, newDayStop, displayDaysLunar);
					return;
				}
			}else{
				//由于月视图需要更新，也就是产生或者消失了闰月，更新的是newMonthSway，如果要保证不产生视觉上的变化，那么这里的newMonthSway就需要稍作改动
				//月视图需要更新
				currDisplayMonthsLunar = Util.getLunarMonthsNamesWithLeap(newYearFixMonthLeap);
				
				//优化方案
				int oldMonthLunar = Util.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
				int oldMonthLunarAbs = Math.abs(oldMonthLunar);
				newMonthSway = Util.convertMonthLunarToMonthSway(oldMonthLunarAbs, newYearFixMonthLeap);
				setValuesForPickerView(picker_month, newMonthSway, MONTH_START_LUNAR, 
						newYearFixMonthLeap == 0 ? MONTH_STOP_LUNAR_NORMAL : MONTH_STOP_LUNAR_LEAP, currDisplayMonthsLunar);
				
				//原本方案
				/*
				if(newYearFixMonthLeap == 0){//newYearMonthLeap == 0 && oldYearMonthLeap != 0
					newMonthSway = (oldMonthSway <= MONTH_STOP_LUNAR_NORMAL) ? oldMonthSway : MONTH_STOP_LUNAR_NORMAL;
					setValuesForPickerView(picker_month, newMonthSway, MONTH_START_LUNAR_NORMAL, MONTH_STOP_LUNAR_NORMAL, currDisplayMonthsLunar);
				}else{
					newMonthSway = (oldMonthSway <= MONTH_STOP_LUNAR_LEAP) ? oldMonthSway : MONTH_STOP_LUNAR_LEAP;
					setValuesForPickerView(picker_month, newMonthSway, MONTH_START_LUNAR_LEAP, MONTH_STOP_LUNAR_LEAP, currDisplayMonthsLunar);
				}
				*/
				//日视图需要更新
				int oldDayStop = Util.getSumOfDayInMonth(oldYearFix, oldMonthSway, false);
				int newDayStop = Util.getSumOfDayInMonth(newYearFix, newMonthSway, false);
				if(oldDayStop == newDayStop){
					return;//不需要更新
				}else{
					int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
					setValuesForPickerView(picker_day, newDaySway, DAY_START, newDayStop, displayDaysLunar);
					return;
				}
			}
		}
	}
	
	private void passiveUpdateDay(int oldYear, int newYear, int oldMonth, int newMonth, boolean isGregorian){
		
		int oldDaySway = picker_day.getValue();
		
		int oldDayStop = Util.getSumOfDayInMonth(oldYear, oldMonth, isGregorian);
		int newDayStop = Util.getSumOfDayInMonth(newYear, newMonth, isGregorian);
		
		if(oldDayStop == newDayStop){
			return;//不需要更新
		}else{
			int newDaySway = oldDaySway <= newDayStop ? oldDaySway : newDayStop;
			setValuesForPickerView(picker_day, newDaySway, DAY_START, newDayStop, isGregorian ? displayDaysGregorian : displayDaysLunar);
			return;
		}
	}
	
	public void setGregorian(boolean isGregorian){
		if(mIsGregorian == isGregorian){
			return;
		}
		
		ChineseCalendar cc = (ChineseCalendar)getCalendarData().getCalendar();//根据mIsGregorian收集数据
		if(!checkCalendarAvailable(cc, YEAR_START, YEAR_STOP, isGregorian)){
			cc = (ChineseCalendar)adjustCalendarByLimit(cc, YEAR_START, YEAR_STOP, isGregorian);//调整改变后的界面的显示
		}
		mIsGregorian = isGregorian;//改变mIsGregorian的数值
		initConfigs(cc, isGregorian);//重新更新界面数据
	}
	
	private boolean checkCalendarAvailable(Calendar cc, int yearStart, int yearStop, boolean isGregorian){
		int year = isGregorian ? cc.get(Calendar.YEAR) : ((ChineseCalendar)cc).get(ChineseCalendar.CHINESE_YEAR);
		return (yearStart <= year) && (year <= yearStop);
	}
	
	public View getNumberPickerYear(){
		return picker_year;
	}
	
	public View getNumberPickerMonth(){
		return picker_month;
	}
	
	public View getNumberPickerDay(){
		return picker_day;
	}
	
	public void setNumberPickerYearVisibility(int visibility){
		setNumberPickerVisibility(picker_year, visibility);
	}
	
	public void setNumberPickerMonthVisibility(int visibility){
		setNumberPickerVisibility(picker_month, visibility);
	}
	
	public void setNumberPickerDayVisibility(int visibility){
		setNumberPickerVisibility(picker_day, visibility);
	}
	
	public void setNumberPickerVisibility(NumberPicker view, int visibility){
		if(view.getVisibility() == visibility){
			return;
		}else if(visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE){
			view.setVisibility(visibility);
		}
	}
	
	public boolean getIsGregorian(){
		return mIsGregorian;
	}
	
	public CalendarData getCalendarData(){
		int pickedYear = picker_year.getValue();
		int pickedMonthSway = picker_month.getValue();
		int pickedDay = picker_day.getValue();
		return new CalendarData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
	}
	
	public static class CalendarData{
		public boolean isGregorian = false;		
		public int pickedYear;
		public int pickedMonthSway;
		public int pickedDay;
		public ChineseCalendar chineseCalendar;
		
		/**
		 * model类的构造方法
		 * @param pickedYear
		 * 			年
		 * @param pickedMonthSway
		 * 			月，公历农历均从1开始。农历如果有闰年，按照实际的顺序添加
		 * @param pickedDay
		 * 			日，从1开始，日期在月份中的显示数值
		 * @param isGregorian
		 * 			是否为公历
		 */
		public CalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean isGregorian) {
			this.pickedYear = pickedYear;
			this.pickedMonthSway = pickedMonthSway;
			this.pickedDay = pickedDay;
			this.isGregorian = isGregorian;
			initChineseCalendar();
		}
		
		/**
		 * 初始化成员变量chineseCalendar，用来记录当前选中的时间。此成员变量同时存储了农历和公历的信息
		 */
		private void initChineseCalendar(){
			if(isGregorian){
				chineseCalendar = new ChineseCalendar(pickedYear, pickedMonthSway - 1, pickedDay);//公历日期构造方法
			}else{
				int y = pickedYear;
				int m = Util.convertMonthSwayToMonthLunarByYear(pickedMonthSway, pickedYear);
				int d = pickedDay;
				
				chineseCalendar = new ChineseCalendar(true, y, m, d);
			}
		}
		
		public Calendar getCalendar(){
			return chineseCalendar;
		}
	}
}
