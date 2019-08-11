package bt.bracelet.android.capstone;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.blinky.profile.callback.BlinkyButtonDataCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyLedDataCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyButtonCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyLedCallback;
import no.nordicsemi.android.blinky.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.blinky.BlinkyActivity;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.profile.BlinkyManager;
import no.nordicsemi.android.blinky.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.blinky.profile.callback.BlinkyButtonDataCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyLedDataCallback;
import no.nordicsemi.android.blinky.profile.data.BlinkyLED;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class Timer_Screen extends AppCompatActivity  {


    /** Nordic Blinky Service UUID. */
    public static UUID LBS_UUID_SERVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    /** BUTTON characteristic UUID. */
    private static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    /** LED characteristic UUID. */
    private static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
    /** Color characteristic UUID. */
    private static UUID COLOR_UUID = UUID.fromString("00001526-1212-efde-1523-785feabcd123");
    /** Vibrate characteristic UUID. */
    private static UUID VIBRATE_UUID = UUID.fromString("00001527-1212-efde-1523-785feabcd123");
    /** Timercharacteristic UUID. */
    private static UUID TIMER_UUID = UUID.fromString("00001528-1212-efde-1523-785feabcd123");


    private BluetoothGattCharacteristic mButtonCharacteristic, mLedCharacteristic, mColorCharacteristic, mVibrateCharacteristic, mTimerCharacteristic;
    private LogSession mLogSession;
    private boolean mSupported;
    private boolean mLedOn;

    public innter_timer_screen timer_screen;

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
    private long timeLeft;//= 10000;    // used to set timer
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private TextView textView;
    private FloatingActionButton settingsButton;

    // Flag that holds the on off state of the ping/vibrate. On is true, Off is False
    private final MutableLiveData<Boolean> mVIBRATEState = new MutableLiveData<>();

    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";

    //global time for the reset.
    private int hours;
    private int seconds;
    private int minutes;
    private String timeLeftText;

    private Context context;

    // public BlinkyManagerCallbacks callbacks;
    public innter_timer_screen innterTimerScreen;


    public class innter_timer_screen extends BleManager<BlinkyManagerCallbacks> implements BlinkyManagerCallbacks{


        public BluetoothDevice mDevice;
        public Application app;
        // Connection states Connecting, Connected, Disconnecting, Disconnected etc.
        private final MutableLiveData<String> mConnectionState = new MutableLiveData<>();

        // Flag to determine if the device is connected
        private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();

        // Flag to determine if the device has required services
        private final MutableLiveData<Boolean> mIsSupported = new MutableLiveData<>();

        // Flag to determine if the device is ready
        private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();

        // Flag that holds the on off state of the LED. On is true, Off is False
        private final MutableLiveData<Boolean> mLEDState = new MutableLiveData<>();

        // Flag that holds the pressed released state of the button on the devkit.
        // Pressed is true, Released is false
        private final MutableLiveData<Boolean> mButtonState = new MutableLiveData<>();

        public LiveData<Void> isDeviceReady() {
            return mOnDeviceReady;
        }

        /*
        private LiveData<String> getConnectionState() {
            return mConnectionState;
        }

        private LiveData<Boolean> isConnected() {
            return mIsConnected;
        }
*/
        private LiveData<Boolean> getButtonState() {
            return mButtonState;
        }

        public LiveData<Boolean> getLEDState() {
            return mLEDState;
        }

        public LiveData<Boolean> isSupported() {
            return mIsSupported;
        }

        //public static Context myContext;
        //public static Application myapp;

        /**
         * Connect to peripheral.
         */
        public void connect(@NonNull final DiscoveredBluetoothDevice device) {
            // Prevent from calling again when called again (screen orientation changed)
            setGattCallbacks(this);
            if (mDevice == null) {
                mDevice = device.getDevice();
                final LogSession logSession
                        = Logger.newSession(getApplication(), null, device.getAddress(), device.getName());
                setLogger(logSession);
                reconnect();
            }
        }

        /**
         * Disconnect from peripheral.
         */
//        public void disconnect() {
        //           mDevice = null;
        //           disconnect().enqueue();
        //       }

        public void toggleLED(final boolean isOn) {
            send(isOn);
            mLEDState.setValue(isOn);
        }


        protected void onCleared() {
            //super.onCleared();
            if (isConnected()) {
                //disconnect();
            }
        }

        @Override
        public void onButtonStateChanged(@NonNull final BluetoothDevice device, final boolean pressed) {
            mButtonState.postValue(pressed);
        }

        @Override
        public void onLedStateChanged(@NonNull final BluetoothDevice device, final boolean on) {
            mLEDState.postValue(on);
        }

        @Override
        public void onDeviceConnecting(@NonNull final BluetoothDevice device) {
            mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
        }

        @Override
        public void onDeviceConnected(@NonNull final BluetoothDevice device) {
            mIsConnected.postValue(true);
            mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
        }

        @Override
        public void onDeviceDisconnecting(@NonNull final BluetoothDevice device) {
            mIsConnected.postValue(false);
        }

        @Override
        public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
            mIsConnected.postValue(false);
        }

        @Override
        public void onLinkLossOccurred(@NonNull final BluetoothDevice device) {
            mIsConnected.postValue(false);
        }

        @Override
        public void onServicesDiscovered(@NonNull final BluetoothDevice device,
                                         final boolean optionalServicesFound) {
            mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
        }

        @Override
        public void onDeviceReady(@NonNull final BluetoothDevice device) {
            mIsSupported.postValue(true);
            mConnectionState.postValue(null);
            mOnDeviceReady.postValue(null);
        }

        @Override
        public void onBondingRequired(@NonNull final BluetoothDevice device) {
            // Blinky does not require bonding
        }

        @Override
        public void onBonded(@NonNull final BluetoothDevice device) {
            // Blinky does not require bonding
        }

        @Override
        public void onBondingFailed(@NonNull final BluetoothDevice device) {
            // Blinky does not require bonding
        }

        @Override
        public void onError(@NonNull final BluetoothDevice device,
                            @NonNull final String message, final int errorCode) {
            // TODO implement
        }

        @Override
        public void onDeviceNotSupported(@NonNull final BluetoothDevice device) {
            mConnectionState.postValue(null);
            mIsSupported.postValue(false);
        }



        /**
         * The manager constructor.
         * <p>
         * After constructing the manager, the callbacks object must be set with
         * {@link #setGattCallbacks(BleManagerCallbacks)}.
         * <p>
         * To connect a device, call {@link #connect(BluetoothDevice)}.
         *
         * @param context the context.
         */
        public innter_timer_screen(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        protected BleManagerGattCallback getGattCallback() {
            return mGattCallback;
        }



        public void connectToDevice(DiscoveredBluetoothDevice device)
        {
            //setGattCallbacks(getGattCallback());
            connect(device.getDevice());

        }


        /**
         * Sets the log session to be used for low level logging.
         * @param session the session, or null, if nRF Logger is not installed.
         */
        public void setLogger(@Nullable LogSession session) {
            mLogSession = session;
        }

        /**
         * Reconnects to previously connected device.
         * If this device was not supported, its services were cleared on disconnection, so
         * reconnection may help.
         */
        public void reconnect() {
            setGattCallbacks(this);
            if (mDevice != null) {
                connect(mDevice)
                        .retry(3, 100)
                        .useAutoConnect(false)
                        .enqueue();
            }
        }

        @Override
        public void log(int priority, @NonNull String message) {
            // The priority is a Log.X constant, while the Logger accepts it's log levels.
            Logger.log(mLogSession, LogContract.Log.Level.fromPriority(priority), message);
        }

        @Override
        protected boolean shouldClearCacheWhenDisconnected() {
            return !mSupported;
        }

        /**
         * The Button callback will be notified when a notification from Button characteristic
         * has been received, or its data was read.
         * <p>
         * If the data received are valid (single byte equal to 0x00 or 0x01), the
         * {@link BlinkyButtonDataCallback#onButtonStateChanged} will be called.
         * Otherwise, the {@link BlinkyButtonDataCallback#onInvalidDataReceived(BluetoothDevice, Data)}
         * will be called with the data received.
         */
        private BlinkyButtonDataCallback mButtonCallback = new BlinkyButtonDataCallback() {
            @Override
            public void onButtonStateChanged(@NonNull final BluetoothDevice device,
                                             final boolean pressed) {
                log(LogContract.Log.Level.APPLICATION, "Button " + (pressed ? "pressed" : "released"));
                mCallbacks.onButtonStateChanged(device, pressed);
            }

            @Override
            public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                              @NonNull final Data data) {
                log(Log.WARN, "Invalid data received: " + data);
            }
        };

        /**
         * The LED callback will be notified when the LED state was read or sent to the target device.
         * <p>
         * This callback implements both {@link no.nordicsemi.android.ble.callback.DataReceivedCallback}
         * and {@link no.nordicsemi.android.ble.callback.DataSentCallback} and calls the same
         * method on success.
         * <p>
         * If the data received were invalid, the
         * {@link BlinkyLedDataCallback#onInvalidDataReceived(BluetoothDevice, Data)} will be
         * called.
         */
        private BlinkyLedDataCallback mLedCallback = new BlinkyLedDataCallback() {
            @Override
            public void onLedStateChanged(@NonNull final BluetoothDevice device,
                                          final boolean on) {
                mLedOn = on;
                log(LogContract.Log.Level.APPLICATION, "LED " + (on ? "ON" : "OFF"));
                Log.d("ledcallback", "Callback from led");
                mCallbacks.onLedStateChanged(device, on);
            }

            @Override
            public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                              @NonNull final Data data) {
                // Data can only invalid if we read them. We assume the app always sends correct data.
                log(Log.WARN, "Invalid data received: " + data);
            }
        };

        /**
         * BluetoothGatt callbacks object.
         */
        private BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
            @Override
            protected void initialize() {
                setNotificationCallback(mButtonCharacteristic).with(mButtonCallback);
                readCharacteristic(mVibrateCharacteristic).with(mVibrateCallback).enqueue();
                readCharacteristic(mLedCharacteristic).with(mLedCallback).enqueue();
                readCharacteristic(mButtonCharacteristic).with(mButtonCallback).enqueue();
                enableNotifications(mButtonCharacteristic).enqueue();
            }

            @Override
            public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
                BluetoothGattService service = gatt.getService(LBS_UUID_SERVICE);
                //BluetoothGattService service2 = gatt.getService(VIBRATE_UUID);

                //mVibrateCharacteristic = service2.getCharacteristic(VIBRATE_UUID);

                if (service != null) {

                    mButtonCharacteristic = service.getCharacteristic(LBS_UUID_BUTTON_CHAR);
                    mLedCharacteristic = service.getCharacteristic(LBS_UUID_LED_CHAR);
                    mVibrateCharacteristic = service.getCharacteristic(VIBRATE_UUID);
                    mColorCharacteristic = service.getCharacteristic(COLOR_UUID);
                    mTimerCharacteristic = service.getCharacteristic(TIMER_UUID);

                }

                // TODO: Do we need to add this for each of our new characteristics?
                boolean writeRequest = false;
                if (mLedCharacteristic != null) {
                    final int rxProperties = mLedCharacteristic.getProperties();
                    writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                }

                boolean writeRequest2 = false;
                if (mVibrateCharacteristic != null) {
                    final int rxProperties2 = mVibrateCharacteristic.getProperties();
                    writeRequest2 = (rxProperties2 & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                }
                boolean writeRequest3 = false;
                if (mColorCharacteristic != null) {
                    final int rxProperties3 = mColorCharacteristic.getProperties();
                    writeRequest3 = (rxProperties3 & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                }

                service.addCharacteristic(mVibrateCharacteristic);
                service.addCharacteristic(mColorCharacteristic);
                service.addCharacteristic(mTimerCharacteristic);

                mSupported = mButtonCharacteristic != null && mLedCharacteristic != null && mVibrateCharacteristic != null  && mColorCharacteristic != null && writeRequest && writeRequest2 && writeRequest3;
                return mSupported;
            }

            @Override
            protected void onDeviceDisconnected() {
                mButtonCharacteristic = null;
                mLedCharacteristic = null;
                mTimerCharacteristic = null;
                mColorCharacteristic = null;
                mVibrateCharacteristic = null;
            }
        };

        /**
         * Sends a request to the device to turn the LED on or off.
         *
         * @param on true to turn the LED on, false to turn it off.
         */
        public void send(final boolean on) {
            // Are we connected?
            //if (mLedCharacteristic == null)
            //     return;

            // No need to change?
            // if (mLedOn == on)
            //     return;

            log(Log.VERBOSE, "Turning LED " + (on ? "ON" : "OFF") + "...");
            log(Log.DEBUG, "Turning vibration " + (on ? "ON" : "OFF"));
            Log.d("bla", "blabla send turn light on");
            writeCharacteristic(mLedCharacteristic, on ? BlinkyLED.turnOn() : BlinkyLED.turnOff())
                    .with(mLedCallback).enqueue();

        }

        // Sends a color characteristic to the micro-controller
        public void SendColor(int red, int green, int blue)
        {
            byte[] rgb = new byte[3];
            rgb[0] = (byte)blue;
            rgb[1] = (byte)green;
            rgb[2] = (byte)red;

            writeCharacteristic(mColorCharacteristic, rgb).with(mColorCharacteristicCallback).enqueue();
        }

        /// sends a vibrate on/off signal to the micro-controller
        public void SendVibrate(boolean on)
        {
            byte[] turningOn = new byte[1];
            turningOn[0] = 0x01;
            Log.d("bla", "blabla send vibrate");
            log(Log.DEBUG, "Turning vibration " + (on ? "ON" : "OFF"));
            writeCharacteristic(mVibrateCharacteristic, turningOn)
                    .with(mVibrateCallback).enqueue();
        }

        /**
         *        Takes an integer representing time in seconds and converts that value
         *         into a byte array and sends that byte array to the micro-controller
         *        */
        public void SendTimer(int time)
        {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.order(ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
            b.putInt(time);
            byte[] result = b.array();
            Log.i("timer", "Setting timer to: " + time);
            writeCharacteristic(mTimerCharacteristic, result).with(mTimerCharacteristicCallback).enqueue();
        }


        /**
         * The vibrate callback will be notified when the vibrate state was read or sent to the target device.
         * <p>
         * This callback implements both {@link no.nordicsemi.android.ble.callback.DataReceivedCallback}
         * and {@link no.nordicsemi.android.ble.callback.DataSentCallback} and calls the same
         * method on success.
         * <p>
         * If the data received were invalid, the
         * {@link BlinkyLedDataCallback#onInvalidDataReceived(BluetoothDevice, Data)} will be
         * called.
         */
        private BlinkyLedDataCallback mVibrateCallback = new BlinkyLedDataCallback() {
            @Override
            public void onLedStateChanged(@NonNull final BluetoothDevice device,
                                          final boolean on) {
                mLedOn = on;
                log(LogContract.Log.Level.APPLICATION, "Vibrate toggled");
                mCallbacks.onLedStateChanged(device, on);
            }

            @Override
            public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                              @NonNull final Data data) {
                // Data can only invalid if we read them. We assume the app always sends correct data.
                log(Log.WARN, "Invalid data received: " + data);
            }
        };

        private BlinkyLedDataCallback mColorCharacteristicCallback = new BlinkyLedDataCallback() {
            @Override
            public void onLedStateChanged(@NonNull final BluetoothDevice device,
                                          final boolean on) {
                log(Log.WARN, "Hello from color characteristic callback :] " );
            }

            @Override
            public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                              @NonNull final Data data) {
                // Data can only invalid if we read them. We assume the app always sends correct data.
                log(Log.WARN, "Invalid data received: " + data);
            }
        };

        private BlinkyLedDataCallback mTimerCharacteristicCallback = new BlinkyLedDataCallback() {
            @Override
            public void onLedStateChanged(@NonNull final BluetoothDevice device,
                                          final boolean on) {
                log(Log.WARN, "Hello from timer characteristic callback :] " );
            }

            @Override
            public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                              @NonNull final Data data) {
                // Data can only invalid if we read them. We assume the app always sends correct data.
                log(Log.WARN, "Invalid data received: " + data);
            }
        };

    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer__screen);
        innterTimerScreen = new innter_timer_screen(getApplicationContext());
        Intent btIntent = getIntent();
        DiscoveredBluetoothDevice device = btIntent.getParcelableExtra(EXTRA_DEVICE);
        innterTimerScreen.connect(device);

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
                if (timerRunning) {
                    stopTimer();//not currently used at all.. unless we want to
                    //keep the scrollable options up on the screen.
                }

                String message = timerString();
                textView.setText(message);
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (timerRunning) {
                    stopTimer();
                }

                String message = timerString();
                textView.setText(message);
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (timerRunning) {
                    stopTimer();
                }

                String message = timerString();
                textView.setText(message);
            }
        });


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
                intent.putExtra(EXTRA_DEVICE, device);
                startActivity(intent);
            }
        });


        // listen for a click on the schedule button
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Schedule_Screen.class);
                intent.putExtra(EXTRA_DEVICE, device);
                startActivity(intent);
            }
        });

        ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ping has been pressed", "ping has been pressed");
                //((innter_timer_screen) innter_timer_screen).SendVibrate(true);
                innterTimerScreen.send(true);

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
        innterTimerScreen.SendTimer(seconds_left);
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
        innterTimerScreen.SendTimer(0);

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

}

