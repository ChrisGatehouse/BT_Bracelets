/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.blinky.profile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.blinky.profile.callback.BlinkyButtonDataCallback;
import no.nordicsemi.android.blinky.profile.callback.BlinkyLedDataCallback;
import no.nordicsemi.android.blinky.profile.data.BlinkyLED;
import no.nordicsemi.android.blinky.profile.data.CustomData;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class BlinkyManager extends BleManager<BlinkyManagerCallbacks> {

	/** Nordic Blinky Service UUID. */
	public final static UUID LBS_UUID_SERVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123");
	/** BUTTON characteristic UUID. */
	private final static UUID LBS_UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
	/** LED characteristic UUID. */
	private final static UUID LBS_UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
	/** Color characteristic UUID. */
	private final static UUID COLOR_UUID = UUID.fromString("00001526-1212-efde-1523-785feabcd123");
	/** Vibrate characteristic UUID. */
	private final static UUID VIBRATE_UUID = UUID.fromString("00001527-1212-efde-1523-785feabcd123");
	/** Timercharacteristic UUID. */
	private final static UUID TIMER_UUID = UUID.fromString("00001528-1212-efde-1523-785feabcd123");

	private BluetoothGattCharacteristic mButtonCharacteristic, mLedCharacteristic, mColorCharacteristic, mVibrateCharacteristic, mTimerCharacteristic;
	private LogSession mLogSession;
	private boolean mSupported;
	private boolean mLedOn;

	public BlinkyManager(@NonNull final Context context) {
		super(context);
	}


	@NonNull
	@Override
	protected BleManagerGattCallback getGattCallback() {
		return mGattCallback;
	}

	/**
	 * Sets the log session to be used for low level logging.
	 * @param session the session, or null, if nRF Logger is not installed.
	 */
	public void setLogger(@Nullable final LogSession session) {
		this.mLogSession = session;
	}

	@Override
	public void log(final int priority, @NonNull final String message) {
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
	private	final BlinkyButtonDataCallback mButtonCallback = new BlinkyButtonDataCallback() {
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
	private final BlinkyLedDataCallback mLedCallback = new BlinkyLedDataCallback() {
		@Override
		public void onLedStateChanged(@NonNull final BluetoothDevice device,
									  final boolean on) {
			mLedOn = on;
			log(LogContract.Log.Level.APPLICATION, "LED " + (on ? "ON" : "OFF"));
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
	private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
		@Override
		protected void initialize() {
			setNotificationCallback(mButtonCharacteristic).with(mButtonCallback);
			//readCharacteristic(mVibrateCharacteristic).with(mVibrateCallback).enqueue();
			readCharacteristic(mLedCharacteristic).with(mLedCallback).enqueue();
			readCharacteristic(mButtonCharacteristic).with(mButtonCallback).enqueue();
			enableNotifications(mButtonCharacteristic).enqueue();
		}

		@Override
		public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(LBS_UUID_SERVICE);
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

			mSupported = mButtonCharacteristic != null && mLedCharacteristic != null && writeRequest;
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
		if (mLedCharacteristic == null)
			return;

		// No need to change?
		if (mLedOn == on)
			return;

		log(Log.VERBOSE, "Turning LED " + (on ? "ON" : "OFF") + "...");
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
		log(Log.VERBOSE, "Turning vibration " + (on ? "ON" : "OFF") + "...");
		writeCharacteristic(mVibrateCharacteristic, on ? BlinkyLED.turnOn() : BlinkyLED.turnOff())
				.with(mVibrateCallback).enqueue();
	}

	// Takes an integer representing time in seconds and converts that value
	// into a byte array and sends that byte array to the micro-controller
	public void SendTimer(int time)
	{
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(time);
		byte[] result = b.array();
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
	private final BlinkyLedDataCallback mVibrateCallback = new BlinkyLedDataCallback() {
		@Override
		public void onLedStateChanged(@NonNull final BluetoothDevice device,
									  final boolean on) {
			//mLedOn = on;
			log(LogContract.Log.Level.APPLICATION, "Vibrate toggled");
			//mCallbacks.onLedStateChanged(device, on);
		}

		@Override
		public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
										  @NonNull final Data data) {
			// Data can only invalid if we read them. We assume the app always sends correct data.
			log(Log.WARN, "Invalid data received: " + data);
		}
	};

	private final BlinkyLedDataCallback mColorCharacteristicCallback = new BlinkyLedDataCallback() {
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

	private final BlinkyLedDataCallback mTimerCharacteristicCallback = new BlinkyLedDataCallback() {
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
