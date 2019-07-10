package bt.bracelet.android.capstone;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.blinky.R;

public class Timer_Screen extends AppCompatActivity {

    private TextView countdown;
    private Button startButton;
    private Button resetButton;
    private Button reserved1Button;
    private Button reserved2Button;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 0;//timeLeft;    // 10 seconds
    private boolean timerRunning;
    private  long timeLeft;//= 10000;    // used to set timer
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private TextView textView;

    private int hours;
    private int seconds;
    private int minutes;
    private String timeLeftText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer__screen);


        //countdown = findViewById(R.id.text_view_countdown);
        startButton = findViewById(R.id.start);
        resetButton = findViewById(R.id.reset);
        reserved1Button = findViewById(R.id.reserved1);
        reserved2Button = findViewById(R.id.reserved2);


        //From timer scroll screen
        hourPicker = findViewById(R.id.numberPicker1);
        minutePicker = findViewById(R.id.numberPicker2);
        secondPicker = findViewById(R.id.numberPicker3);

        textView = findViewById(R.id.textView1);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();
                }
                int hours = hourPicker.getValue();
                int minutes =  minutePicker.getValue();
                int seconds = secondPicker.getValue();

                String message = timerString(hours,minutes,seconds);
                textView.setText(message);
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public  void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();
                }
                //int minutes = hourPicker.getValue() * 60 + newVal;
               // int seconds = secondPicker.getValue();
                int hours = hourPicker.getValue();
                int minutes =  minutePicker.getValue();
                int seconds = secondPicker.getValue();

                String message = timerString(hours,minutes,seconds);
                textView.setText(message);
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();
                }
                int hours = hourPicker.getValue();
                int minutes =  minutePicker.getValue();
                int seconds = secondPicker.getValue();

                String message = timerString(hours,minutes,seconds);
                textView.setText(message);
            }
        });

        //

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

    public String timerString(int hours, int minutes, int seconds){
        String message = "Timer for ";//"Timer for " + minutes + " minutes and " + seconds + " seconds";
        if(hours > 0) {
            message += hours + " hours ";
        }
        if (minutes > 0)
            message += minutes + " minutes ";
        if(seconds > 0)
            message += seconds + " seconds ";

        return message;
    }


    // check current state of the timer to determine action
    public void startStop(){
        if(timerRunning) {
            stopTimer();
        }
        else {

            hours = hourPicker.getValue();
            minutes = minutePicker.getValue();
            seconds = secondPicker.getValue();

            timeLeft= TimeUnit.HOURS.toMillis(hours)+TimeUnit.MINUTES.toMillis(minutes)+TimeUnit.SECONDS.toMillis(seconds);
            timeLeftInMilliseconds = timeLeft;
            startTimer();

        }
    }


    // handles start timer button
    public void startTimer(){

        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {

                timeLeftInMilliseconds = l;
                //timeLeftInMilliseconds = TimeUnit.HOURS.toMillis(hours)+TimeUnit.MINUTES.toMillis(minutes)+TimeUnit.SECONDS.toMillis(seconds);
                updateTimer();
            }

            @Override
            public void onFinish() {
                textView.setText("DONE");
                startButton.setText("STOP");

            }
        }.start();

        startButton.setText("cancel");
        timerRunning = true;
    }


    public void stopTimer(){
        countDownTimer.cancel();
        startButton.setText("start");
        hourPicker.setValue(0);
        minutePicker.setValue(0);
        secondPicker.setValue(0);
        timerRunning = false;
        textView.setText("");
    }


    public void updateTimer(){
        timeLeftText = "";//"time left" + timeLeftInMilliseconds;
        long newseconds = (timeLeftInMilliseconds / 1000) % 60 ;
        long newminutes = ((timeLeftInMilliseconds / (1000*60)) % 60);
        long newhours   = ((timeLeftInMilliseconds / (1000*60*60)) % 24);

        if(newhours < 10)
            timeLeftText="0";
        timeLeftText += newhours+":";

        if(newminutes<10)
            timeLeftText+="0";
        timeLeftText+=newminutes;

        timeLeftText += ":";
        if(newseconds < 10) timeLeftText += "0";
        timeLeftText += newseconds;

        /*countdown.setText(timeLeftText);*/
        textView.setText(timeLeftText);
    }


    public void resetTimer(){
        stopTimer();
        timeLeftInMilliseconds = timeLeft;
        hourPicker.setValue(hours);
        minutePicker.setValue(minutes);
        secondPicker.setValue(seconds);
        updateTimer();
    }
}
