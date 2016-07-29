package com.love311.www.birthdays.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ActivityMeg extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityMeg instance;

	private ActivityMeg() {
	}

	public static ActivityMeg getInstance() {
		if (null == instance) {
			instance = new ActivityMeg();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		for (int i = 0; i < activityList.size(); i++) {
			if (activityList.get(i) == activity) {
				activityList.remove(i);
			}
		}
	}


	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	public List<Activity> getActivitiesList() {
		return activityList;
	}

	public void removeAll() {
		for (int i = 0; i < activityList.size(); i++) {
			activityList.remove(i);
		}
	}

}
