package bt.bracelet.android.capstone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.ScannerActivity;
import no.nordicsemi.android.blinky.profile.BlinkyManager;

// adding for color sliders
import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.Serializable;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class Settings_Screen extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, Serializable {

    private Button bluetooth;//button that takes user to bluetooth page
    private SwitchCompat switch_2; //currently unused
    private RadioGroup colors; // radio group for the color options
    private RadioButton colorOption; //radio button of the currently checked color
    private Button colorButton; // button to change the color of the watch LED's on-click
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button logout;//button to log the user out of the app
    private int selectedColor;
    private int defaultColor;
    private static final String preferenceFile = "bt.bracelet.android.capstone";
    // added for color sliders
    //Reference the seek bars
    //SeekBar SeekA;
    SeekBar SeekR;
    SeekBar SeekG;
    SeekBar SeekB;
    //Reference the TextView
    TextView ShowColor;
    //Context context = getApplicationContext();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__screen);
        preferences = getSharedPreferences(preferenceFile, MODE_PRIVATE);
        //PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences(preferenceFile,MODE_PRIVATE);
        //Get a reference to the seekbars
        //SeekA = findViewById(R.id.seekA);
        SeekR = findViewById(R.id.seekR);
        SeekG = findViewById(R.id.seekG);
        SeekB = findViewById(R.id.seekB);
        //Reference the TextView
        ShowColor = findViewById(R.id.textView);
        //This activity implements SeekBar OnSeekBarChangeListener
        //SeekA.setOnSeekBarChangeListener(this);
        SeekR.setOnSeekBarChangeListener(this);
        SeekG.setOnSeekBarChangeListener(this);
        SeekB.setOnSeekBarChangeListener(this);

        /*
        colors = findViewById(R.id.colors);

        defaultColor = colors.getCheckedRadioButtonId();
        colorButton = findViewById(R.id.colorButton); // gets the id of the change color button
        selectedColor = preferences.getInt("color",defaultColor);
        colorOption = findViewById(selectedColor);

        colorOption.setChecked(true);


*/
        //bluetooth menu screen
        bluetooth = findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //STILL NEEDED OR INTEGRATE WITH BLINKY?

                //choose the activity to go to
                Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
                //go to the activity
                startActivity(intent);

            }
        });

        //on-click for the back to home button
        FloatingActionButton fab = findViewById(R.id.back_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //choose the activity to go to
                Intent intent = new Intent(getApplicationContext(), Timer_Screen.class);
                //go to the activity
                startActivity(intent);
            }
        });
    }

        // added for color wheel
        //Satisfy the implements
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

            //get current ARGB values
            //int A=SeekA.getProgress();
            int A = 200;
            int red=SeekR.getProgress();
            int G=SeekG.getProgress();
            int B=SeekB.getProgress();
            //Reference the value changing
            int id=seekBar.getId();
            //Get the chnaged value
            //if(id == R.id.seekA)
            //    A=progress;
            if(id == R.id.seekR)
                red=progress;
            else if(id == R.id.seekG)
                G=progress;
            else if(id == R.id.seekB)
                B=progress;
            //Build and show the new color
            ShowColor.setBackgroundColor(Color.argb(A, red,G,B));
            Context context = getApplicationContext();
            BlinkyManager blinky1 = new BlinkyManager(context);
            blinky1.SendColor(1,2,3);

        };
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Only required due to implements
        };
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Only required due to implements
        }
}

