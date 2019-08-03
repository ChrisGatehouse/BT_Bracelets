package bt.bracelet.android.capstone;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.blinky.BlinkyActivity;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.profile.BlinkyManager;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

public class Timer_Screen extends AppCompatActivity implements Parcelable {

    public BlinkyManager blinky1;
    private TextView countdown;
    private Button startButton;
    private Button resetButton;
    private Button reserved1Button;
    private Button schedule;
    private Button ping;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 0;//timeLeft;    // 10 seconds
    private boolean timerRunning;
    private  long timeLeft;//= 10000;    // used to set timer
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private TextView textView;
    private FloatingActionButton settingsButton;
    //public BlinkyManager blinky1;
    // Flag that holds the on off state of the ping/vibrate. On is true, Off is False
    private final MutableLiveData<Boolean> mVIBRATEState = new MutableLiveData<>();
    ////////ADDED FOR BT - JUST TYRING
    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
    //private BlinkyViewModel blinkyViewModel;
    public BluetoothDevice mDevice;

    //global time for the reset.
    private int hours;
    private int seconds;
    private int minutes;
    private String timeLeftText;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer__screen);

        Intent btIntent = getIntent();


        ////////JUST TYRING
        //final Intent btIntent = getIntent();
        DiscoveredBluetoothDevice device = btIntent.getParcelableExtra(EXTRA_DEVICE);
        //BlinkyActivity activity = new BlinkyActivity();
        //BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //BluetoothDevice devicel = new BluetoothDevice(this);
        //blinkyViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        //blinkyViewModel.mBlinkyManager.setGattCallbacks(this.blinkyViewModel);
        mDevice = device.getDevice();
        //if(device != null) {
         //   blinkyViewModel.connect(device);
       // }


        countdown = findViewById(R.id.text_view_countdown);
        startButton = findViewById(R.id.start);
        resetButton = findViewById(R.id.reset);
        settingsButton = findViewById(R.id.reserved1);//currently used to navigate to settings page
        schedule = findViewById(R.id.Schedule);
        ping = findViewById(R.id.Ping);



        //From timer scroll screen
        hourPicker = findViewById(R.id.numberPicker1);
        minutePicker = findViewById(R.id.numberPicker2);
        secondPicker = findViewById(R.id.numberPicker3);

        //outputs the "timer for....."
        textView = findViewById(R.id.textView1);

        //set number picker values
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        //if we scroll, obtain the value it was scrolled to.
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();//not currently used at all.. unless we want to
                    //keep the scrollable options up on the screen.
                }

                String message = timerString();
                textView.setText(message);
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public  void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();
                }

                String message = timerString();
                textView.setText(message);
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(timerRunning){
                    stopTimer();
                }

                String message = timerString();
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do some stuff here
                Intent intent = new Intent(getApplicationContext(), Settings_Screen.class);
                startActivity(intent);
            }
        });


        // listen for a click on the schedule button
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Schedule_Screen.class);
                startActivity(intent);
            }
        });

        ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Log.d("ping has been pressed", "ping has been pressed");
                 //send signal to watch to vibrate the watch...
                 //context = getApplicationContext();
                 //blinky1 = new BlinkyManager(context);
                 //mDevice = device.getDevice();
                 //blinky1.send(true);
                 blinky1.SendVibrate(true);
                // blinkyViewModel.disconnect();
                 //blinkyViewModel.mBlinkyManager.connect(mDevice);
                 //blinkyViewModel.reconnect();
                 //blinkyViewModel.toggleLED(true);
                 //blinkyViewModel.mBlinkyManager.send(true);
                 //blinkyViewModel.mBlinkyManager.SendVibrate(true);
                 //blinky1.setGattCallbacks(this.onClick(v));
                 //startActivity(btIntent);
                 //blinky1.send(true);
                 //blinky1.SendVibrate(true);
                 //mVIBRATEState.postValue(true);
                 //mVIBRATEState.setValue(true);
                 //blink1.writeChar

                //context = getApplicationContext();
               // activity.mViewModel = new BlinkyViewModel(context);
               // activity.mViewModel.mBlinkyManager = new BlinkyManager(context);
                //activity.mViewModel.mBlinkyManager.send(true);



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

    //obtains the current values that the number picker is scrolled through and will output
    //to user how long they are setting the timer for.
    public String timerString(){
        hours = hourPicker.getValue();
        minutes =  minutePicker.getValue();
        seconds = secondPicker.getValue();

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
            /*
            hours = hourPicker.getValue();
            minutes = minutePicker.getValue();
            seconds = secondPicker.getValue();*/

            textView.setVisibility(View.INVISIBLE);
            hourPicker.setVisibility(View.INVISIBLE);
            minutePicker.setVisibility(View.INVISIBLE);
            secondPicker.setVisibility(View.INVISIBLE);
            countdown.setVisibility(View.VISIBLE);

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
        int seconds_left = (int) TimeUnit.MILLISECONDS.toSeconds(timeLeftInMilliseconds);
        context = getApplicationContext();
        blinky1 = new BlinkyManager(context);
        blinky1.SendTimer(seconds_left);
        startButton.setText("cancel");
        timerRunning = true;
    }

//stopTimer will cancel the current timer, and set the number scrolls back to visible.
    //Timer scrolls will also be reset to default '0' values. Countdown timer will disappear.
    public void stopTimer(){
        //cancel current timer
        countDownTimer.cancel();
        //update button
        startButton.setText("start");

        //reset the number pickers to default value.
        hourPicker.setValue(0);
        minutePicker.setValue(0);
        secondPicker.setValue(0);
        timerRunning = false;

        // added for stop timer from blinky manager
        context = getApplicationContext();
        blinky1 = new BlinkyManager(context);
        blinky1.SendTimer(0);

        //set visibility of scroll and countdown timer
        textView.setVisibility(View.VISIBLE);
        countdown.setVisibility(View.INVISIBLE);
        hourPicker.setVisibility(View.VISIBLE);
        minutePicker.setVisibility(View.VISIBLE);
        secondPicker.setVisibility(View.VISIBLE);

        textView.setText("");
    }

//updates the timer each second.
    public void updateTimer(){
        timeLeftText = "";
        //convert milliseconds to seconds,mins,hours.
        long newseconds = (timeLeftInMilliseconds / 1000) % 60 ;
        long newminutes = ((timeLeftInMilliseconds / (1000*60)) % 60);
        long newhours   = ((timeLeftInMilliseconds / (1000*60*60)) % 24);

        //ensure that each time will have a leading 0 if shorter than 10, for a nicer view.
        if(newhours < 10)
            timeLeftText="0";
        timeLeftText += newhours+":";

        if(newminutes<10)
            timeLeftText+="0";
        timeLeftText+=newminutes;

        timeLeftText += ":";
        if(newseconds < 10) timeLeftText += "0";
        timeLeftText += newseconds;

        countdown.setText(timeLeftText);
    }

//resets the current timer.
    public void resetTimer(){
        onreset();
        //reset the timer to the original time.
        timeLeftInMilliseconds = timeLeft;
        startButton.setText("start");
        updateTimer();
    }

    //used on timer reset so that the scrollers arent pulled back up. This
    //will reset the countdown timer to the original time the scroll was set to.

    public void onreset(){
        //cancel currently running timer
        if(countDownTimer != null)
            countDownTimer.cancel();

        //output how long timer will be set to
        if(timerRunning) {
            String message = timerString();
            textView.setText(message);
        }
        timerRunning=false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable((Parcelable) this.blinky1, flags);
        dest.writeParcelable((Parcelable) this.countdown, flags);
        dest.writeParcelable((Parcelable) this.startButton, flags);
        dest.writeParcelable((Parcelable) this.resetButton, flags);
        dest.writeParcelable((Parcelable) this.reserved1Button, flags);
        dest.writeParcelable((Parcelable) this.schedule, flags);
        dest.writeParcelable((Parcelable) this.ping, flags);
        dest.writeParcelable((Parcelable) this.countDownTimer, flags);
        dest.writeLong(this.timeLeftInMilliseconds);
        dest.writeByte(this.timerRunning ? (byte) 1 : (byte) 0);
        dest.writeLong(this.timeLeft);
        dest.writeParcelable((Parcelable) this.hourPicker, flags);
        dest.writeParcelable((Parcelable) this.minutePicker, flags);
        dest.writeParcelable((Parcelable) this.secondPicker, flags);
        dest.writeParcelable((Parcelable) this.textView, flags);
        dest.writeParcelable((Parcelable) this.settingsButton, flags);
        dest.writeParcelable((Parcelable) this.mVIBRATEState, flags);
        dest.writeParcelable(this.mDevice, flags);
        dest.writeInt(this.hours);
        dest.writeInt(this.seconds);
        dest.writeInt(this.minutes);
        dest.writeString(this.timeLeftText);
        dest.writeParcelable((Parcelable) this.context, flags);
    }

    public Timer_Screen() {
    }

    /**
     * @param in
     */
    protected Timer_Screen(Parcel in) {
        this.blinky1 = new BlinkyManager(in.readParcelable(BlinkyManager.class.getClassLoader()));
        this.countdown = in.readParcelable(TextView.class.getClassLoader());
        this.startButton = in.readParcelable(Button.class.getClassLoader());
        this.resetButton = in.readParcelable(Button.class.getClassLoader());
        this.reserved1Button = in.readParcelable(Button.class.getClassLoader());
        this.schedule = in.readParcelable(Button.class.getClassLoader());
        this.ping = in.readParcelable(Button.class.getClassLoader());
        this.countDownTimer = in.readParcelable(CountDownTimer.class.getClassLoader());
        this.timeLeftInMilliseconds = in.readLong();
        this.timerRunning = in.readByte() != 0;
        this.timeLeft = in.readLong();
        this.hourPicker = in.readParcelable(NumberPicker.class.getClassLoader());
        this.minutePicker = in.readParcelable(NumberPicker.class.getClassLoader());
        this.secondPicker = in.readParcelable(NumberPicker.class.getClassLoader());
        this.textView = in.readParcelable(TextView.class.getClassLoader());
        this.settingsButton = in.readParcelable(FloatingActionButton.class.getClassLoader());
        //this.mVIBRATEState = in.readParcelable(MutableLiveData<Boolean>.class.getClassLoader());
        this.mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.hours = in.readInt();
        this.seconds = in.readInt();
        this.minutes = in.readInt();
        this.timeLeftText = in.readString();
        this.context = in.readParcelable(Context.class.getClassLoader());
    }

    public static final Parcelable.Creator<Timer_Screen> CREATOR = new Parcelable.Creator<Timer_Screen>() {
        @Override
        public Timer_Screen createFromParcel(Parcel source) {
            return new Timer_Screen(source);
        }

        @Override
        public Timer_Screen[] newArray(int size) {
            return new Timer_Screen[size];
        }
    };
}
