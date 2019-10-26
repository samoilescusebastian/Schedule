package com.example.schedule;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WeekSchedule {

    private static final String EMPTY_STRING = "";
    final private static int daysNumber = 7;
    private DaySchedule[] days = new DaySchedule[daysNumber];

    private String scheduleName;
    WeekSchedule(String scheduleName) {
        this.scheduleName = scheduleName;
        for (int i = 0; i < daysNumber; i++) {
            days[i] = new DaySchedule();
        }
    }
    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public DaySchedule getDaySchedule(int day) {
        if (day == -1) {
            day = 6;
        }
        return days[day];
    }

    public void changeSchedule(int dayIndex, DaySchedule newSchedule) {
        days[dayIndex] = newSchedule;
    }
    public int getLength() {
        return  daysNumber;
    }
    public void saveSchedule(Context context) {
        List<String> schedulesTitles = SchedulesList.getScheduleList(context);
        if(!SchedulesList.isInLIst(schedulesTitles, scheduleName)) {
            SchedulesList.addInScheduleList(context, scheduleName);
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(scheduleName + ".json", MODE_PRIVATE);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                for (int i = 0; i < getLength(); i++) {
                    DaySchedule currentDaySchedule = days[i];
                    currentDaySchedule.saveDay(fileOutputStream);
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }



    public void sortSchedule() {
        for (int i = 0; i < daysNumber; i++) {
            days[i].sort();
        }
    }


}
