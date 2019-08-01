package bt.bracelet.android.capstone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.profile.BlinkyManager;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

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
    private PendingIntent[] alarmIntentArr = new PendingIntent[7];
    boolean[] daysSelected = new boolean[7];
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String preferenceFile = "bt.bracelet.android.capstone";


    ////////JUST TRYING
    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
    private BlinkyViewModel blinkyViewModel;


    // Method to set alarm
    private void setAlarm(int dayOfWeek) {
        // Set the alarm to the alarmCal, per the docs
        alarmCal.setTimeInMillis(System.currentTimeMillis());
        alarmCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        alarmCal.set(Calendar.HOUR_OF_DAY, hour);
        alarmCal.set(Calendar.MINUTE, minute);

        //handles if the timer is set for a day in the past( ie. if you set the alarms on wed, but the alarm is set
        //for sunday, add a week to the timer, to set for this coming sunday rather than last sunday...
        //did this as timer was going off immediately for the days in the past.
        if(System.currentTimeMillis() > alarmCal.getTimeInMillis()) {
            long setNextWeek = alarmCal.getTimeInMillis()+604800000; //add a week to the time.
            alarmCal.setTimeInMillis(setNextWeek);
            Log.i("tag", "timer for " + dayOfWeek + " is set in past. setting for future.");
        }
        Log.i("tag:", "timer is being set for "+alarmCal.getTimeInMillis() + " current time: "+System.currentTimeMillis());
        // Set the alarm in the alarm manager, weekly. Each alarmIntentArr index is a day [0,6], dayOfWeek is [1,7]
        // Repeat weekly, until canceled.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(),
                604800000, alarmIntentArr[dayOfWeek - 1]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_screen);

        //load shared preferences for the app
        preferences = getSharedPreferences(preferenceFile, MODE_PRIVATE);
        editor = preferences.edit();

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


        /////////JUST TRYING
        final Intent btIntent = getIntent();
        final DiscoveredBluetoothDevice device = btIntent.getParcelableExtra(EXTRA_DEVICE);
        blinkyViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        if(device != null) {
            blinkyViewModel.connect(device);
        }


        //get all stored preferences
        daysSelected[0] = preferences.getBoolean("Sun", false);
        daysSelected[1] = preferences.getBoolean("Mon", false);
        daysSelected[2] = preferences.getBoolean("Tue", false);
        daysSelected[3] = preferences.getBoolean("Wed", false);
        daysSelected[4] = preferences.getBoolean("Thu", false);
        daysSelected[5] = preferences.getBoolean("Fri", false);
        daysSelected[6] = preferences.getBoolean("Sat", false);
        int savedHour = preferences.getInt("hour",-1);
        int savedMinute = preferences.getInt("minute",-1);

        //im saving hour and minute to be sure. setting the timer to reflect the time of the current alarm if it is set.
        if(savedHour !=-1 && savedMinute!=-1){
            hour = savedHour;
            minute = savedMinute;
            time.setHour(hour);
            time.setMinute(minute);
        }

        //apply the saved settings
        day0Button.setChecked(daysSelected[0]);
        day1Button.setChecked(daysSelected[1]);
        day2Button.setChecked(daysSelected[2]);
        day3Button.setChecked(daysSelected[3]);
        day4Button.setChecked(daysSelected[4]);
        day5Button.setChecked(daysSelected[5]);
        day6Button.setChecked(daysSelected[6]);
        remindOkButton.setChecked(preferences.getBoolean("reminderOn", false));


        alarmCal = Calendar.getInstance();
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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

                    //save the time in the preference file
                    editor.putInt("hour", hour);
                    editor.putInt("minute", minute);
                    editor.apply();



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
                    //store the state of the reminder button
                    editor.putBoolean("reminderOn",remindOkButton.isChecked());
                    editor.apply();
                }
                else {
                    // Cancel the alarms
                    if (alarmMgr != null){
                        for (int alarmDayIndex = 0; alarmDayIndex < 7; alarmDayIndex++) {
                            alarmMgr.cancel(alarmIntentArr[alarmDayIndex]);
                            //update and store state of reminder button
                            editor.putBoolean("reminderOn",remindOkButton.isChecked());
                            editor.apply();
                        }
                    }
                }
            }
        });

        day0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daysSelected[0] = day0Button.isChecked();
                editor.putBoolean("Sun", daysSelected[0]);
                editor.apply();
            }
        });

        day1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[1] = day1Button.isChecked();
                editor.putBoolean("Mon", daysSelected[1]);
                editor.apply();
            }
        });

        day2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[2] = day2Button.isChecked();
                editor.putBoolean("Tue", daysSelected[2]);
                editor.apply();
            }
        });

        day3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[3] = day3Button.isChecked();
                editor.putBoolean("Wed", daysSelected[3]);
                editor.apply();
            }
        });

        day4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[4] = day4Button.isChecked();
                editor.putBoolean("Thu", daysSelected[4]);
                editor.apply();
            }
        });

        day5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[5] = day5Button.isChecked();
                editor.putBoolean("Fri", daysSelected[5]);
                editor.apply();
            }
        });

        day6Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelected[6] = day6Button.isChecked();
                editor.putBoolean("Sat", daysSelected[6]);
                editor.apply();
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

                Context context = getApplicationContext();
                BlinkyManager blinky1 = new BlinkyManager(context);
                blinky1.SendVibrate(true);
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
