package bt.bracelet.android.capstone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

import no.nordicsemi.android.blinky.R;

public class Schedule_Screen extends AppCompatActivity {
    private FloatingActionButton settingButton;
    private ToggleButton remindOkButton;
    private ToggleButton day0Button;
    private ToggleButton day1Button;
    private ToggleButton day2Button;
    private ToggleButton day3Button;
    private ToggleButton day4Button;
    private ToggleButton day5Button;
    private ToggleButton day6Button;
    private Button timerButton;
    private Button pingButton;
    private TimePicker time;
    private int hour;
    private int minute;
    private Calendar alarmCal;
    private AlarmManager alarmMgr;
    private PendingIntent[] alarmIntentArr;

    // Method to set alarm
    private void setAlarm(int dayOfWeek) {
        // Set the alarm to the alarmCal, per the docs
        alarmCal.setTimeInMillis(System.currentTimeMillis());
        alarmCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        alarmCal.set(Calendar.HOUR_OF_DAY, hour);
        alarmCal.set(Calendar.MINUTE, minute);

        // Set the alarm in the alarm manager, weekly. Each alarmIntentArr index is a day [0,6], dayOfWeek is [1,7]
        // Repeat weekly, until canceled.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(),
                7 * AlarmManager.INTERVAL_DAY, alarmIntentArr[dayOfWeek - 1]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_screen);

        // Initialize variables
        settingButton = findViewById(R.id.reservedForSettingButton);
        remindOkButton = findViewById(R.id.remindOkButton);
        pingButton = findViewById(R.id.PingButton);
        day0Button = findViewById(R.id.day0);
        day1Button = findViewById(R.id.day1);
        day2Button = findViewById(R.id.day2);
        day3Button = findViewById(R.id.day3);
        day4Button = findViewById(R.id.day4);
        day5Button = findViewById(R.id.day5);
        day6Button = findViewById(R.id.day6);
        timerButton = findViewById(R.id.TimerButton);
        time = findViewById(R.id.TimePicker);
        hour = minute = 0;
        alarmCal = Calendar.getInstance();
        alarmMgr =  (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        // Intent for the alarm receiver, needed for the alarmIntentArr
        Intent alarmRecIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        for (int i = 0; i < 7; i++){
            // Set the alarmIntentArr elements with requestCode [0,6] for each corresponding day. NEEDS TO BE UNIQUE
            alarmIntentArr[i] = PendingIntent.getBroadcast(getApplicationContext(), i, alarmRecIntent, 0);
        }

        /*TODO: AM/PM vs 24hr view

        * When Ian is done working on settings, we can add a toggle button to allow the user to choose
        * between 24hour and 12hr views. it'll need to:
        * Update preference file on toggle
        * Read shared preference file
        * set the view like below.*/
        //currently just setting to AM/PM
        time.setIs24HourView(false);


        // Static array of booleans to track what days are selected
        boolean[] daysSelected = new boolean[7];
        for (int i = 0; i < 7; i++){
            daysSelected[i] = false;
        }

        // OnClick listeners

        //    From Timer_Screen
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do some stuff here
                Intent intent = new Intent(getApplicationContext(), Settings_Screen.class);
                startActivity(intent);
            }
        });

        remindOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add functionality after scheduling is figured out
                // Steps:
                //      0: Determine state of button, if cancel, cancel. else:
                if (remindOkButton.isChecked()) {
                    //      1: Get time from selectors
                    hour = time.getHour();
                    minute = time.getMinute();
                    //      2: Get days selected
                    // Can be done by iterating through the array
                    for (int dayIndex = 0; dayIndex < 7; dayIndex++){
                        if (daysSelected[dayIndex]){
                            //      3: Schedule (use alarms [https://developer.android.com/training/scheduling/alarms]?)
                            //      YES. it works outside the app, so it seems we wont need to save settings if we choose this :D
                            // dayIndex is [0,6], Calendar expects days (Su-Sa) to be [1,7]. So add one to the argument.
                            setAlarm(dayIndex + 1);
                        }
                    }
                }
                else {
                    // Cancel the alarms
                    if (alarmMgr != null){
                        for (int alarmDayIndex = 0; alarmDayIndex < 7; alarmDayIndex++) {
                            alarmMgr.cancel(alarmIntentArr[alarmDayIndex]);
                        }
                    }
                }
            }
        });

        day0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daysSelected[0] = day0Button.isChecked();
            }
        });

        day1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[1] = day1Button.isChecked();
            }
        });

        day2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[2] = day2Button.isChecked();
            }
        });

        day3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[3] = day3Button.isChecked();
            }
        });

        day4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[4] = day4Button.isChecked();
            }
        });

        day5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[5] = day5Button.isChecked();
            }
        });

        day6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[6] = day6Button.isChecked();
            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Timer_Screen.class);
                startActivity(intent);
            }
        });

        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send signal to watch to vibrate the watch...
                /*
                what we can do, is send a signal to the watch to vibrate, and have the watch
                vibrate until button is reclicked... (much like the start->cancel->back to start functionality.
                on ping, set a bool annoyChild = true and change the text of the button to like.."stop ping"
                on button press again, check the bool, flip it, and send a signal back to the watch to stop vibrating
                and reset the text on the button
                 */
            }
        });

    }
}
