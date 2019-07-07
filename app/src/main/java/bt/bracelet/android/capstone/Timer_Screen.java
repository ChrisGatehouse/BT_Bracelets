package bt.bracelet.android.capstone;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import no.nordicsemi.android.blinky.R;

public class Timer_Screen extends AppCompatActivity {

    private TextView countdown;
    private Button startButton;
    private Button resetButton;
    private Button reserved1Button;
    private Button reserved2Button;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = timeLeft;    // 10 seconds
    private boolean timerRunning;
    private static final int timeLeft = 10000;    // used to set timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer__screen);


        countdown = findViewById(R.id.text_view_countdown);
        startButton = findViewById(R.id.start);
        resetButton = findViewById(R.id.reset);
        reserved1Button = findViewById(R.id.reserved1);
        reserved2Button = findViewById(R.id.reserved2);

        // listen for a click on the start button to start or pause the timer
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStop();
            }
        });


        // listen for a click on the reset button to reset the timer
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });


        // listen for a click on the reserved1 button to do ??????????????
        //Amanda: Using this is a temporary button to get to settings currently, for testing.
        reserved1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do some stuff here
                Intent intent = new Intent(getApplicationContext(), Settings_Screen.class);
                startActivity(intent);
            }
        });


        // listen for a click on the reserved2 button to do ??????????????
        reserved2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do stuff
            }
        });
    }


    // check current state of the timer to determine action
    public void startStop(){
        if(timerRunning)
            stopTimer();
        else
            startTimer();
    }


    // handles start timer button
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        startButton.setText("pause");
        timerRunning = true;
    }


    public void stopTimer(){
        countDownTimer.cancel();
        startButton.setText("start");
        timerRunning = false;
    }


    public void updateTimer(){
        int minutes = (int) timeLeftInMilliseconds / 600000;
        int seconds = (int) timeLeftInMilliseconds % 600000 / 1000;
        String timeLeftText;

        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if(seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countdown.setText(timeLeftText);
    }


    public void resetTimer(){
        stopTimer();
        timeLeftInMilliseconds = timeLeft;
        updateTimer();
    }
}
