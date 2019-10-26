package com.example.schedule;

import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
class ChronoComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity a, Activity b) {
        return a.getStartHour() < b.getStartHour() ? -1 : a.getStartHour() == b.getStartHour() ? 0 : 1;
    }
}
public class DaySchedule {
    private List<Activity> activities = new ArrayList<>();

    public Activity getActivity(int i) {
        return activities.get(i);
    }
    public void changeActivity(int activityNumber, Activity newActivity) {
        activities.set(activityNumber, newActivity);
    }
    public boolean addActivity(Activity newActivity) {

        if (isOverLapping(newActivity) == 0) {
            activities.add(new Activity(newActivity));
            return true;
        }
        return false;
    }
    public void putChrono() {
        Collections.sort(activities, new ChronoComparator());
    }
    public void reset() {
        activities.clear();
    }
    public int isOverLapping(Activity newActivity) {
        for(Activity activity : activities) {
            if (newActivity.getStartHour() < activity.getEndHour() && newActivity.getEndHour() > activity.getStartHour()) {
                return  activity.getCode();

            }
        }
        return 0;
    }
    public int getLength() {
        return  activities.size();
    }
    public void saveDay(FileOutputStream fileOutputStream) {
        writeToFile(fileOutputStream, "Start\n");
        for (Activity activity : activities) {
            String jsonActivity = activity.ActivityToJson();
            writeToFile(fileOutputStream, jsonActivity + "\n");

        }
        writeToFile(fileOutputStream, "End\n");
    }
    private  void writeToFile(FileOutputStream fileOutputStream, String jsonActivity) {
        try {
            fileOutputStream.write(jsonActivity.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sort() {
        ChronoComparator comparator = new ChronoComparator();
        Collections.sort(activities, comparator);
    }
    public static List<Activity> filterByHour(List<Activity> activities, int hour) {
        List<Activity> tmpActivities = new ArrayList<>();
        for(Activity currentActivity : activities) {
            if (currentActivity.getEndHour() > hour) {
                tmpActivities.add(currentActivity);
            }
        }
        return tmpActivities;
    }
    public static List<Activity> filterByType(List<Activity> activities, int type) {
        List<Activity> tmpActivities = new ArrayList<>();
        for(Activity currentActivity : activities) {
            if (currentActivity.getType() == 0 || currentActivity.getType() == type) {
                tmpActivities.add(currentActivity);
            }
        }
        return tmpActivities;
    }
    List<Activity> getActivities() {
        List<Activity> activitiesCopy = new ArrayList<>();
        for (Activity currentActivity : activities) {
            activitiesCopy.add(currentActivity);
        }
        return  activitiesCopy;
    }
    public void deleteActivity(int code) {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getCode() == code) {
                activities.remove(i);
                return;
            }
        }
    }
}
