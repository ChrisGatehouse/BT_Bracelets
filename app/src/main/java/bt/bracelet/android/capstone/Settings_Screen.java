package bt.bracelet.android.capstone;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.ScannerActivity;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.profile.BlinkyManager;
import no.nordicsemi.android.blinky.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.blinky.profile.callback.BlinkyButtonDataCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyLedDataCallback;
import no.nordicsemi.android.blinky.profile.data.BlinkyLED;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

// adding for color sliders
import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

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
    private Button colorChange;
    private int red,green,blue = 1;
    // added for color sliders
    //Reference the seek bars
    //SeekBar SeekA;
    SeekBar SeekR;
    SeekBar SeekG;
    SeekBar SeekB;
    //Reference the TextView
    TextView ShowColor;
    //Context context = getApplicationContext();

    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
    /** Nordic Blinky Service UUID. */
    public static UUID LBS_UUID_SERVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
    /** BUTTON characteristic UUID. */
    private static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
    /** LED characteristic UUID. */
    //private static UUID COLOR_UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
    private static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
    /** Color characteristic UUID. */
    private static UUID COLOR_UUID = UUID.fromString("00001526-1212-efde-1523-785feabcd123");
    //private static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001526-1212-efde-1523-785feabcd123");
     /** Vibrate characteristic UUID. */
    private static UUID VIBRATE_UUID = UUID.fromString("00001527-1212-efde-1523-785feabcd123");
    /** Timercharacteristic UUID. */
    private static UUID TIMER_UUID = UUID.fromString("00001528-1212-efde-1523-785feabcd123");

    public innter_setting_screen innterSettingScreen;
    private BluetoothGattCharacteristic mButtonCharacteristic, mLedCharacteristic, mColorCharacteristic, mVibrateCharacteristic, mTimerCharacteristic;
    private LogSession mLogSession;
    private boolean mSupported;
    private boolean mLedOn;





    public class innter_setting_screen extends BleManager<BlinkyManagerCallbacks> implements BlinkyManagerCallbacks{


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
        public innter_setting_screen(@NonNull Context context) {
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
                readCharacteristic(mColorCharacteristic).with(mColorCharacteristicCallback).enqueue();
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

                mSupported = mButtonCharacteristic != null && mLedCharacteristic != null && mVibrateCharacteristic != null  && mColorCharacteristic != null&& writeRequest && writeRequest2 && writeRequest3;
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
         * Sends a color characteristic to the micro-controller
         * @param red
         * @param green
         * @param blue
         */
        public void SendColor(int red, int green, int blue)
        {

           /* ByteBuffer b = ByteBuffer.allocate(3);
            b.order(ByteOrder.LITTLE_ENDIAN);
            b.putInt(red);
            byte [] res = b.array();*/
            //red = 20;


            // If we do this we avoid negative values when converting to bytes...
            /*
            if (red > 127)
                red = 127;
            if (green > 127)
                green = 127;
            if (blue > 127)
                blue = 127;
              */


            // If we do this we will still get negative values when converting to bytes. This will have to be handled on microcontroller side
            if (red > 254)
                red = 254;
            if (green > 254)
                green = 254;
            if (blue > 254)
                blue = 254;

            if (red < 1)
                red = 1;
            if (green < 1)
                green = 1;
            if (blue < 1)
                blue = 1;

            byte[] rgb = new byte[3];

            //rgb[0] = (byte)(blue & 0xFF);
            //rgb[1] = (byte)(green & 0xFF);
            //rgb[2] = (byte)(red & 0xFF);
            rgb[0] = (byte)(blue);
            rgb[1] = (byte)(green);
            rgb[2] = (byte)(red);
            Log.i("array", ""+rgb);
            Log.d("array input", "b:"+ rgb[0] + "g:" + rgb[1] + "r:" + rgb[2]);
            writeCharacteristic(mColorCharacteristic, rgb).with(mColorCharacteristicCallback).enqueue();
        }

        /**
         * Writes to the vibrate characteristic
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
        colorButton = findViewById(R.id.confirm_color);

        innterSettingScreen = new innter_setting_screen(getApplicationContext());
        Intent btIntent = getIntent();
        DiscoveredBluetoothDevice device = btIntent.getParcelableExtra(EXTRA_DEVICE);
        innterSettingScreen.connect(device);

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
                intent.putExtra(EXTRA_DEVICE, device);
                //go to the activity
                startActivity(intent);
            }
        });

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innterSettingScreen.SendColor(red,green,blue);

            }
        });
    }

    // added for color wheel
    //Satisfy the implements
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

        //get current ARGB values
        //int A=SeekA.getProgress();
        int A = 200;
        red=SeekR.getProgress();
        green=SeekG.getProgress();
        blue=SeekB.getProgress();
        //Reference the value changing
        int id=seekBar.getId();
        //Get the chnaged value
        //if(id == R.id.seekA)
        //    A=progress;
        if(id == R.id.seekR)
            red=progress;
        else if(id == R.id.seekG)
            green=progress;
        else if(id == R.id.seekB)
            blue=progress;
        //Build and show the new color
        ShowColor.setBackgroundColor(Color.argb(A, red,green,blue));
        //innterSettingScreen.SendColor(0,78,200);

    };
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Only required due to implements
    };
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Only required due to implements
    }
}

