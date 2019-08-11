package bt.bracelet.android.capstone;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;
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

public class Schedule_Screen extends AppCompatActivity implements Serializable {


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
    public innter_sched_screen innterSchedScreen;


    ////////JUST TRYING
    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
    private BlinkyViewModel blinkyViewModel;

    public class innter_sched_screen extends BleManager<BlinkyManagerCallbacks> implements BlinkyManagerCallbacks{


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
        public innter_sched_screen(@NonNull Context context) {
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

                service.addCharacteristic(mVibrateCharacteristic);
                service.addCharacteristic(mColorCharacteristic);
                service.addCharacteristic(mTimerCharacteristic);

                mSupported = mButtonCharacteristic != null && mLedCharacteristic != null && mVibrateCharacteristic != null && writeRequest && writeRequest2;
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

        /**
         * Writes to the vibrate characteristic, triggering vibrate on microcontroller
         * @param on
         */
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
        innterSchedScreen = new Schedule_Screen.innter_sched_screen(getApplicationContext());
        Intent btIntent = getIntent();
        DiscoveredBluetoothDevice device = btIntent.getParcelableExtra(EXTRA_DEVICE);
        innterSchedScreen.connect(device);



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
                intent.putExtra(EXTRA_DEVICE, device);
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
                intent.putExtra(EXTRA_DEVICE, device);
                startActivity(intent);
            }
        });

        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //innterSchedScreen.send(true);
                innterSchedScreen.SendVibrate(true);
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
