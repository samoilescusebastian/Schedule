package com.example.schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {
    private static final String EMPTY_STRING = "";

    String[] days = {"Monday","Tuesday", " Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


    private int activityCode = 0;
    int currentDay = 0;
    final int lastDay = 7;
    int[] activityIndexes = new int[lastDay];

    Activity currentActivity;

    public TextView displayedDay;
    public TextView displayedActivity;
    public NumberPicker startHour;
    public NumberPicker endHour;

    public EditText activityName;
    public EditText location;
    public EditText description;

    public Button nextActivity;
    public Button nextDayButton;
    public Button prevDayButton;
    public ImageButton cancelButton;

    public RadioButton weeklyRBUtton;
    public RadioButton oddRButton;
    public RadioButton evenRButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        Intent intent = getIntent();
        final String scheduleName = intent.getStringExtra(MainActivity.EXTRA_NAME);

        final WeekSchedule currentSchedule = new WeekSchedule(scheduleName);
        initializeIndexes();

        displayedDay = findViewById(R.id.currentDay);
        displayedActivity = findViewById(R.id.currentActivity);

        startHour = findViewById(R.id.startHour);
        setNumberPicker(startHour);

        endHour = findViewById(R.id.endHour);
        setNumberPicker(endHour);

        nextActivity = findViewById(R.id.nextActivityButton);
        nextDayButton = findViewById(R.id.nextButton);
        prevDayButton = findViewById(R.id.prevButton);
        cancelButton  = findViewById(R.id.cancelButton);

        activityName = findViewById(R.id.activityName);
        location  = findViewById(R.id.location);
        description = findViewById(R.id.description);

        weeklyRBUtton = findViewById(R.id.weeklyButton);
        oddRButton = findViewById(R.id.oddWeekButton);
        evenRButton = findViewById(R.id.evenWeekButton);


        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!emptyFields()) {
                    setCurrentActivity();
                    if (!currentSchedule.getDaySchedule(currentDay).addActivity(currentActivity)) {
                        Toast.makeText(getApplicationContext(),"Interval is overlapping! Try other interval!", Toast.LENGTH_LONG).show();
                    } else {
                        resetLayer();
                        activityIndexes[currentDay]++;
                        displayedActivity.setText("Activity no: " + activityIndexes[currentDay]);
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
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setMessage("Do you want to exit? (All data will be lost)").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                resetLayer();
                if (prevDayButton.isEnabled() == false) {
                    prevDayButton.setEnabled(true);
                }
                if (currentDay == lastDay - 1) {
                    try {
                        currentSchedule.sortSchedule();
                        currentSchedule.saveSchedule(getApplicationContext());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Schedule was successfully saved!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentDay++;
                    setDay(currentDay);
                    if (currentDay == lastDay - 1) {
                        nextDayButton.setText("Save Timetable");
                    }
                }
            }
        });

        prevDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nextDayButton.getText() == "Save Timetable") {

                    nextDayButton.setText("Next Day");
                }
                if (currentDay == lastDay) {
                    currentDay--;
                }
                currentDay--;
                setDay(currentDay);
                if (currentDay == 0) {
                    prevDayButton.setEnabled(false);
                }

            }
        });

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

    public void resetLayer() {
        startHour.setValue(0);
        endHour.setValue(0);
        activityName.setText(EMPTY_STRING);
        location.setText(EMPTY_STRING);
        description.setText(EMPTY_STRING);
        weeklyRBUtton.setChecked(true);
        evenRButton.setChecked(false);
        oddRButton.setChecked(false);
    }

    private void setCurrentActivity() {
        int type = 0;
        if (evenRButton.isChecked()) {
            type = 1;
        } else if (oddRButton.isChecked()) {
            type = 2;
        }
        currentActivity = new Activity(activityName.getText().toString(), location.getText().toString(), description.getText().toString(),
                                       startHour.getValue(), endHour.getValue(), type, activityIndexes[currentDay]);

    }

    public void setDay(int currentDay) {
        displayedDay.setText(days[currentDay]);
        displayedActivity.setText("Activity no: " + activityIndexes[currentDay]);
    }

    public static void setNumberPicker(NumberPicker numberPicker) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(23);
    }

    private void initializeIndexes() {
        for (int i = 0; i < lastDay; i++) {
            activityIndexes[i] = 1;
        }
    }




}
