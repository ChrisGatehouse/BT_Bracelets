package bt.bracelet.android.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;

import no.nordicsemi.android.blinky.R;

import no.nordicsemi.android.blinky.R;

public class Bluetooth extends AppCompatActivity implements Serializable {

    private Switch enable_bt;//enable bluetooth switch
    private SharedPreferences preferences;
    private boolean bluetoothState = true;//holds current state of the bluetooth switch
    private SharedPreferences.Editor editor;
    private ListView pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //set pereferences for the switch
        preferences = getSharedPreferences("PREFS",0);
        bluetoothState = preferences.getBoolean("enable_bt", true);
        enable_bt = findViewById(R.id.enable_bt);
        enable_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change the state of the switch,
                bluetoothState = !bluetoothState;
                //check switch
                enable_bt.setChecked(bluetoothState);
                //edit preferences
                editor = preferences.edit();
                editor.putBoolean("enable_bt",bluetoothState);
                editor.apply();
                editor.commit();
            }
        });

        //back to home button
        FloatingActionButton fab = findViewById(R.id.back_arrow);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), Settings_Screen.class);
                startActivity(intent);
            }
        });

        //Paired devices

        pairedDevices = findViewById(R.id.pairedDevices);


    }

}

