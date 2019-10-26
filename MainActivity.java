package com.example.schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static  final String EXTRA_NAME = "com.example.schedule.EXTRA_NAME";
    public static final String EXTRA_SCHEDULE = "com.example.schedule.EXTRA_SCHEDULE";
    private static final String SCHEDULE_NAMES = "schedules.txt";
    private static final String EMPTY_STRING = "";
    private String selectedSchedule = EMPTY_STRING;
    private static String pathToFiles;
    private LineView[] linesView = new LineView[6];
    List<String> scheduleTitles;
    private String[] colors = {"#0070b8", "#0099cc", "#00c2e0", "#7333d9", "#7d5ce3", "#8785ed"};

    String currentWeek = new SimpleDateFormat("w").format(new java.util.Date());
    String currentTime = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());

    private int initialX = 605, initialY = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        scheduleTitles = SchedulesList.getScheduleList(getApplicationContext());

        linesView[0] =  findViewById(R.id.longULineView);

        linesView[1] =  findViewById(R.id.mediumULineView);

        linesView[2] =  findViewById(R.id.shortULineView);

        linesView[3] =  findViewById(R.id.longDLineView);

        linesView[4] =  findViewById(R.id.mediumDLineView);

        linesView[5] =  findViewById(R.id.shortDLineView);
        for (int i = 0; i < linesView.length / 2; i++) {

            linesView[i].setPointA(new PointF(0, initialY));

            linesView[i].setPointB(new PointF(initialX, initialY));

            linesView[i].setColor(colors[i]);

            initialX -= 200;

            initialY += 50;

            linesView[i].draw();
        }

        initialX = 430;
        initialY = 1470;
        for (int i = linesView.length / 2; i < linesView.length; i++) {

            linesView[i].setPointA(new PointF(initialX, initialY));

            linesView[i].setPointB(new PointF(1080, initialY));

            linesView[i].setColor(colors[i]);

            initialX += 200;

            initialY -= 50;

            linesView[i].draw();
        }
        Button newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  showBox();
            }
        });

        Button openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleTitles = SchedulesList.getScheduleList(getApplicationContext());
                showSchedulesList(0);

            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleTitles = SchedulesList.getScheduleList(getApplicationContext());
                showSchedulesList(1);
            }
        });

        pathToFiles = this.getFilesDir().toString();


    }
    public void showBox() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        final View mView = getLayoutInflater().inflate(R.layout.title_dialog, null);
        dialog.setView(mView);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Button cancelButton = mView.findViewById(R.id.titleCancelButton);
        Button setButton = mView.findViewById(R.id.setButton);



        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleText = mView.findViewById(R.id.titleInput);
                String title = titleText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please enter a valid name!", Toast.LENGTH_SHORT).show();
                } else
                    if (SchedulesList.isInLIst(scheduleTitles, title)) {
                        Toast.makeText(getApplicationContext(),"A schedule with this name is already present!", Toast.LENGTH_SHORT).show();
                    }else {
                    alertDialog.dismiss();
                    openAddActivity(title);
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
    private void showSchedulesList(int option) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.list_dialog, null);
        dialog.setView(mView);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        ListView schedules = mView.findViewById(R.id.schedulesList);
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, scheduleTitles);
            schedules.setAdapter(arrayAdapter);


        } catch(Exception e) {
            e.printStackTrace();
        }
        if (option == 0) {
            schedules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSchedule = scheduleTitles.get(position);
                    openSchedule(selectedSchedule);
                    alertDialog.dismiss();
                }
            });
        } else {
            schedules.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteSchedule(selectedSchedule);
                                alertDialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
//                                alertDialog.dismiss();
                                break;
                        }
                    }
                };
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedSchedule = scheduleTitles.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Do you want to delete this schedule?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });
        }
    }
    public void openAddActivity(String title) {
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra(EXTRA_NAME, title);
        startActivity(intent);
    }

    private void openSchedule(String scheduleName) {
        Intent intent = new Intent(this, OpenActivity.class);
        intent.putExtra(EXTRA_SCHEDULE, scheduleName);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void deleteSchedule(String scheduleName) {
        String pathToScheduleFile = createFullPath(scheduleName, "json");
        File scheduleFile = new File(pathToScheduleFile);
        boolean deleted = scheduleFile.delete();
        if (deleted == true) {
            Toast.makeText(getApplicationContext(), "Schedule was successfully deleted!", Toast.LENGTH_LONG).show();
            deleteScheduleFromList(scheduleName);

        } else {
            Toast.makeText(getApplicationContext(), "It occurs an error during delete process!", Toast.LENGTH_LONG).show();
        }

    }
    private String createFullPath(String scheduleName, String extension) {
        scheduleName = scheduleName + "." + extension;
        scheduleName = pathToFiles + "/" + scheduleName;
        return  scheduleName;
    }
    private void deleteScheduleFromList(String scheduleName) {
        for (String scheduleTitle : scheduleTitles) {
            System.out.println(scheduleTitle);
        }
        scheduleTitles.remove(scheduleName);
        String pathToSchedulesFile = pathToFiles + "/" + SCHEDULE_NAMES;
        try {
            PrintWriter writer = new PrintWriter(pathToSchedulesFile);
            writer.print("");
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        for (String scheduleTitle : scheduleTitles) {
            SchedulesList.addInScheduleList(getApplicationContext(), scheduleTitle);
        }

    }

}
