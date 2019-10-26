package com.example.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class OpenActivity extends AppCompatActivity {
    private final String[] days = {"Sunday", "Monday", "Tuesday", " Wednesday", "Thursday", "Friday", "Saturday"};
    private final String FINAL_DAY_MESSAGE = "GOOD NIGHT!";
    private final String PAUSE_MESSAGE = "ENJOY YOUR TIME!";
    private final String EMPTY_STRING = "";
    public int toDeleteActivityCode;
    private String scheduleName;
    private WeekSchedule schedule;
    private List<String> activities;
    private Calendar calendar;
    private ListView currentActivityListView;
    private ListView nextActivityListView;
    private DaySchedule currentDaySchedule;
    private NumberPicker startHour;
    private NumberPicker endHour ;
    private List<Activity> currentActivities = new ArrayList<>();
    private EditText activityName;
    private EditText location;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        Button selectDateButton = findViewById(R.id.chooseDateButton);
        Button newActivityButton = findViewById(R.id.newActivityButton);

        currentActivityListView = findViewById(R.id.currentActivityListView);
        nextActivityListView = findViewById(R.id.nextActivitiesListView);

        calendar = Calendar.getInstance();
        Intent intent = getIntent();
        scheduleName = intent.getStringExtra(MainActivity.EXTRA_SCHEDULE);
        loadSchedule();
        setDaySchedule(calendar);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(OpenActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_calendar, null);
                dialog.setView(mView);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

                CalendarView calendarView = mView.findViewById(R.id.calendarView);
                Button selectDate = mView.findViewById(R.id.selectDateButton);


                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        Date date  = new GregorianCalendar(year, month, dayOfMonth).getTime();
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                    }
                });

                selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDaySchedule(calendar);
                        alertDialog.dismiss();

                    }
                });

            }
        });

        newActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(OpenActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_add_activity, null);
                dialog.setView(mView);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

                startHour = mView.findViewById(R.id.startHour);
                AddActivity.setNumberPicker(startHour);

                endHour = mView.findViewById(R.id.endHour);
                AddActivity.setNumberPicker(endHour);

                Button addTemporaryButton = mView.findViewById(R.id.addTemporaryButton);
                Button addPermanentButton = mView.findViewById(R.id.addPermanentButton);
                ImageButton cancelButton  = mView.findViewById(R.id.cancelButton);

                activityName = mView.findViewById(R.id.activityName);
                location  = mView.findViewById(R.id.location);
                description = mView.findViewById(R.id.description);

                final RadioButton oddRButton = mView.findViewById(R.id.oddWeekButton);
                final RadioButton evenRButton = mView.findViewById(R.id.evenWeekButton);

                addPermanentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = 0;
                        if (evenRButton.isChecked()) {
                            type = 2;
                        } else if (oddRButton.isChecked()) {
                            type = 1;
                        }
                        if (!emptyFields()) {
                            final Activity newActivity = new Activity(activityName.getText().toString(), location.getText().toString(), description.getText().toString(),
                                    startHour.getValue(), endHour.getValue(), type, currentDaySchedule.getLength() + 1);
                            if (!currentDaySchedule.addActivity(newActivity)) {
                                Toast.makeText(getApplicationContext(),"Interval is overlapping! Try another interval!", Toast.LENGTH_LONG).show();
                            } else {
                                alertDialog.dismiss();
                                schedule.sortSchedule();
                                schedule.saveSchedule(getApplicationContext());
                                loadSchedule();
                                setDaySchedule(calendar);
                            }
                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    alertDialog.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                        builder.setMessage("Do you want to exit? (All data will be lost)").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });

            }
        });



    }
    private String setCurrentDate(int currentDay, Calendar calendar) {

        String date  = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
        return days[currentDay - 1] + " " + date;
    }
   private List<String> readData(String scheduleName) {
        List<String> activityJsons = new ArrayList<>();
        BufferedReader bufferReader = null;
        String fileName = scheduleName + ".json";
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            bufferReader = new BufferedReader(inputStreamReader);
            try {
                String line = bufferReader.readLine();
                while (line != null) {
                    activityJsons.add(line);
                    line = bufferReader.readLine();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  activityJsons;
    }
    private WeekSchedule convertJsonsToSchedule(List<String> activities, String scheduleName) {
        WeekSchedule currentSchedule = new WeekSchedule(scheduleName);
        ObjectMapper objectMapper = new ObjectMapper();
        int currentDay = -1;
        for (String line : activities) {
            if (line.compareTo("Start") == 0) {
                currentDay++;
            } else if(line.compareTo("End") == 0) {
                ;
            } else {
                try {
                    Activity newActivity = objectMapper.readValue(line, Activity.class);
                    currentSchedule.getDaySchedule(currentDay).addActivity(newActivity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentSchedule;
    }

    private void loadActivities(List<Activity> dayActivities) {
        clearActivityLists();
        if (dayActivities.size() == 0 ) {
            loadCurrentActivity(FINAL_DAY_MESSAGE);
            return;
        } else if(calendar.get(Calendar.HOUR_OF_DAY) < dayActivities.get(0).getStartHour()) {
            loadCurrentActivity(PAUSE_MESSAGE);
        } else {
            loadCurrentActivity(dayActivities.get(0).getName());
            currentActivities.add(dayActivities.get(0));
            dayActivities.remove(0);
        }
        loadNextActivities(dayActivities);


    }
    private void loadCurrentActivity(String currentActivity) {
        List<String> currentActivities = new ArrayList<>();
        currentActivities.add(currentActivity);
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, currentActivities);
            currentActivityListView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadNextActivities(List<Activity> dayActivities) {
        List<String> nextActivities = new ArrayList<>();
        for (int i = 0; i < dayActivities.size(); i++) {
            nextActivities.add(dayActivities.get(i).getName());
        }
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, nextActivities);
            nextActivityListView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDetails(final List<Activity> dayActivities, ListView currentActivityListView,
                            ListView nextActivityListVIew) {
        currentActivityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentActivities.size() > 0) {
                    Activity selectedActivity = currentActivities.get(position);
                    showScheduleDetails(selectedActivity);
                }
            }
        });
        nextActivityListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity selectedActivity = dayActivities.get(position);
                showScheduleDetails(selectedActivity);
            }
        });

    }

    private void showScheduleDetails(final Activity selectedActivity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(OpenActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.details_dialog, null);
        dialog.setView(mView);

        TextView nameTextView = mView.findViewById(R.id.currentActivityTextView);
        TextView startHourTextVIew = mView.findViewById(R.id.startHourTextView);
        TextView endHourTextView = mView.findViewById(R.id.endHourTextView);
        TextView locationTextView = mView.findViewById(R.id.locationTextView);
        TextView descriptionTextView = mView.findViewById(R.id.descriptionTextView);

        nameTextView.setText(selectedActivity.getName());
        startHourTextVIew.setText(Integer.toString(selectedActivity.getStartHour()));
        endHourTextView.setText(Integer.toString(selectedActivity.getEndHour()));
        locationTextView.setText(selectedActivity.getLocation());
        descriptionTextView.setText(selectedActivity.getDescription());

        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        Button editButton = mView.findViewById(R.id.editActivityButton);
        Button deleteButton = mView.findViewById(R.id.deleteActivityButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            currentDaySchedule.deleteActivity(toDeleteActivityCode);
                            schedule.sortSchedule();
                            schedule.saveSchedule(getApplicationContext());
                            loadSchedule();
                            setDaySchedule(calendar);
                            alertDialog.dismiss();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };


            @Override
            public void onClick(View v) {
                toDeleteActivityCode = selectedActivity.getCode();
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setMessage("Are you sure to delete").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(OpenActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_add_activity, null);
                dialog.setView(mView);

                final AlertDialog alertDialog = dialog.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

                startHour = mView.findViewById(R.id.startHour);
                AddActivity.setNumberPicker(startHour);

                endHour= mView.findViewById(R.id.endHour);
                AddActivity.setNumberPicker(endHour);


                activityName = mView.findViewById(R.id.activityName);
                location = mView.findViewById(R.id.location);
                description = mView.findViewById(R.id.description);

                final RadioButton weeklyRButton = mView.findViewById(R.id.weeklyButton);
                final RadioButton oddRButton = mView.findViewById(R.id.oddWeekButton);
                final RadioButton evenRButton = mView.findViewById(R.id.evenWeekButton);

                ImageButton cancelButton  = mView.findViewById(R.id.cancelButton);
                Button saveTemporaryButton = mView.findViewById(R.id.addTemporaryButton);
                Button savePermanentButton = mView.findViewById(R.id.addPermanentButton);

                saveTemporaryButton.setText("SAVE\n TEMPORARY");
                savePermanentButton.setText("SAVE\n PERMANENT");
                startHour.setValue(selectedActivity.getStartHour());
                endHour.setValue(selectedActivity.getEndHour());
                activityName.setText(selectedActivity.getName());
                location.setText(selectedActivity.getLocation());
                description.setText(selectedActivity.getDescription());

                if (selectedActivity.getType() == 0) {
                    weeklyRButton.setChecked(true);
                    oddRButton.setChecked(false);
                    evenRButton.setChecked(false);
                } else if(selectedActivity.getType() == 1) {
                    weeklyRButton.setChecked(false);
                    oddRButton.setChecked(true);
                    evenRButton.setChecked(false);
                } else {
                    weeklyRButton.setChecked(false);
                    oddRButton.setChecked(false);
                    evenRButton.setChecked(true);
                }

                savePermanentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = 0;
                        if (evenRButton.isChecked()) {
                            type = 2;
                        } else if (oddRButton.isChecked()) {
                            type = 1;
                        }
                        if (!emptyFields()) {
                            Activity editedActivity = new Activity(activityName.getText().toString(), location.getText().toString(),
                                    description.getText().toString(), startHour.getValue(), endHour.getValue(), type, selectedActivity.getCode());
                            int result = currentDaySchedule.isOverLapping(editedActivity);
                            if (result > 0 && result != selectedActivity.getCode()) {
                                Toast.makeText(getApplicationContext(), "Interval is overlapping! Try other interval!", Toast.LENGTH_LONG).show();
                            } else{
                                alertDialog.dismiss();
                                currentDaySchedule.deleteActivity(selectedActivity.getCode());
                                currentDaySchedule.addActivity(editedActivity);
                                schedule.sortSchedule();
                                schedule.saveSchedule(getApplicationContext());
                                loadSchedule();
                                setDaySchedule(calendar);

                            }
                        }
                    }
                });



                cancelButton.setOnClickListener(new View.OnClickListener() {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    alertDialog.dismiss();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };


                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                        builder.setMessage("Do you want to exit? (All data will be lost)").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                });


            }
        });

    }

    private void setDaySchedule(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        TextView currentDayTextView = findViewById(R.id.currentDay);
        currentDayTextView.setText(setCurrentDate(day, calendar));
        currentDaySchedule = schedule.getDaySchedule(day - 2);
        List<Activity> dayActivities = currentDaySchedule.getActivities();
        dayActivities = DaySchedule.filterByHour(dayActivities, hour);
        dayActivities = DaySchedule.filterByType(dayActivities,(week + 1) % 2 + 1);
        loadActivities(dayActivities);
        showDetails(dayActivities, currentActivityListView, nextActivityListView);
    }
    private void clearActivityLists() {
        currentActivityListView.setAdapter(null);
        nextActivityListView.setAdapter(null);
    }

    public boolean emptyPickNumbers() {
        if (startHour.getValue() >= endHour.getValue()) {
            Toast.makeText(getApplicationContext(),"Invalid hours!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public boolean emptyEditTexts() {
        if (activityName.getText().toString().compareTo(EMPTY_STRING) == 0) {
            Toast.makeText(getApplicationContext(),"Insert name of activity!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (location.getText().toString().compareTo(EMPTY_STRING) == 0) {
            Toast.makeText(getApplicationContext(),"Insert location of activity!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (description.getText().toString().compareTo(EMPTY_STRING) == 0) {
            Toast.makeText(getApplicationContext(),"Insert description of activity!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }
    public boolean emptyFields() {
        if(emptyPickNumbers() || emptyEditTexts()) {
            return true;
        }
        return false;
    }
    private void loadSchedule() {
        activities = readData(scheduleName);
        schedule =  convertJsonsToSchedule(activities, scheduleName);
    }

}
