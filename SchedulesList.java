package com.example.schedule;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_APPEND;

public class SchedulesList {
    private static final String SCHEDULE_NAMES = "schedules.txt";
    public static void addInScheduleList(Context context, String scheduleName) {
        FileOutputStream fileOutputStream  = null;
        try {
            fileOutputStream = context.openFileOutput(SCHEDULE_NAMES, MODE_APPEND);
            fileOutputStream.write((scheduleName + "\n").getBytes());
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static List<String> getScheduleList(Context context) {
        List<String> schedules = new ArrayList<>();
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = context.openFileInput(SCHEDULE_NAMES);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                schedules.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return schedules;
    }

    public static boolean isInLIst(List<String> scheduleTitles, String title) {
        for (String scheduleTitle : scheduleTitles) {
            if (scheduleTitle.compareTo(title) == 0) {
                return  true;
            }
        }
        return  false;
    }
}
