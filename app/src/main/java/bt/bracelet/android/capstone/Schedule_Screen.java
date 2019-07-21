package bt.bracelet.android.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private NumberPicker hours;
    private NumberPicker minutes;
    private RadioGroup ampmSelect;
    private boolean isAM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_screen);

        settingButton = findViewById(R.id.reservedForSettingButton);
        remindOkButton = findViewById(R.id.remindOkButton);
        day0Button = findViewById(R.id.day0);
        day1Button = findViewById(R.id.day1);
        day2Button = findViewById(R.id.day2);
        day3Button = findViewById(R.id.day3);
        day4Button = findViewById(R.id.day4);
        day5Button = findViewById(R.id.day5);
        day6Button = findViewById(R.id.day6);
        timerButton = findViewById(R.id.TimerButton);
        pingButton = findViewById(R.id.PingButton);
        hours = findViewById(R.id.schedHours);
        minutes = findViewById(R.id.schedMins);
        ampmSelect = findViewById(R.id.ampmReserve);

        // Set isAM
        isAM = R.id.am == ampmSelect.getCheckedRadioButtonId();

        // Set picker parameters
        hours.setMaxValue(12);
        hours.setMinValue(0);
        hours.setValue(0);
        minutes.setMaxValue(59);
        minutes.setMinValue(0);
        minutes.setValue(0);

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
                //      1: Get time from selectors
                //      2: Get days selected
                //      3: Schedule (use alarms [https://developer.android.com/training/scheduling/alarms]?)
                if (remindOkButton.isChecked()) {

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

        ampmSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO: will be removed when changed to timepicker
            }
        });
    }
}
