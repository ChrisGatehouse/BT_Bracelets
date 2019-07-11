package bt.bracelet.android.capstone;

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

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class Settings_Screen extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__screen);
        preferences = getSharedPreferences(preferenceFile,MODE_PRIVATE);
        //PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences(preferenceFile,MODE_PRIVATE);


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
                //Intent intent = new Intent(getApplicationContext(),Bluetooth.class);
                //go to the activity
                //startActivity(intent);

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

        //color options

/*
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search the group for the ID of the button checked
                selectedColor = colors.getCheckedRadioButtonId();
                //find the button via the ID returned
                colorOption.setChecked(false);
                colorOption = findViewById(selectedColor);
                colorOption.setChecked(true);
                editor = preferences.edit();
                editor.putInt("color", selectedColor);
                editor.apply();
                editor.commit();

                //set the color of the watch LED's here to specified color
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logic to log the user out...
                //should preserve the apps current state.
            }
        });
*/



    }
}

