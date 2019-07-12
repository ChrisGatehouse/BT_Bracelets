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
                // TODO: change AM/PM selection logic based on new selection
            }
        });
    }
}
