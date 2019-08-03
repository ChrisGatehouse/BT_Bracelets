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

package no.nordicsemi.android.blinky;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import bt.bracelet.android.capstone.Settings_Screen;
import bt.bracelet.android.capstone.Timer_Screen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.blinky.adapter.DiscoveredBluetoothDevice;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;

@SuppressWarnings("ConstantConditions")
public class BlinkyActivity extends AppCompatActivity implements Parcelable {
	public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";

	public BlinkyViewModel mViewModel;

	@BindView(R.id.led_switch) Switch mLed;
	@BindView(R.id.button_state) TextView mButtonState;
	private Button controlBraceletButton;

	//@BindView(R.id.Control_Bracelet) Button button;


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blinky);
		ButterKnife.bind(this);

		final Intent intent = getIntent();
		final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
		final String deviceName = device.getName();
		final String deviceAddress = device.getAddress();

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(deviceName);
		getSupportActionBar().setSubtitle(deviceAddress);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Configure the view model
		mViewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
		mViewModel.connect(device);

		// Set up views
		final TextView ledState = findViewById(R.id.led_state);
		final LinearLayout progressContainer = findViewById(R.id.progress_container);
		final TextView connectionState = findViewById(R.id.connection_state);
		final View content = findViewById(R.id.device_container);
		final View notSupported = findViewById(R.id.not_supported);

		mLed.setOnCheckedChangeListener((buttonView, isChecked) -> mViewModel.toggleLED(isChecked));
		mViewModel.isDeviceReady().observe(this, deviceReady -> {
			progressContainer.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		});
		mViewModel.getConnectionState().observe(this, text -> {
			if (text != null) {
				progressContainer.setVisibility(View.VISIBLE);
				notSupported.setVisibility(View.GONE);
				connectionState.setText(text);
			}
		});
		mViewModel.isConnected().observe(this, this::onConnectionStateChanged);
		mViewModel.isSupported().observe(this, supported -> {
			if (!supported) {
				progressContainer.setVisibility(View.GONE);
				notSupported.setVisibility(View.VISIBLE);
			}
		});
		mViewModel.getLEDState().observe(this, isOn -> {
			ledState.setText(isOn ? R.string.turn_on : R.string.turn_off);
			mLed.setChecked(isOn);
		});
		mViewModel.getButtonState().observe(this,
				pressed -> mButtonState.setText(pressed ?
						R.string.button_pressed : R.string.button_released));

		controlBraceletButton = findViewById(R.id.Control_Bracelet);

		// listen for a click on the start button to start or pause the timer
		controlBraceletButton.setOnClickListener(view -> {
			Intent intent1 = new Intent(this, Timer_Screen.class);
			//TODO: THIS NEEDS TO CREATE A PARCEL THAT I BUNDLE AND SEND TO THE NEXT SCREEN ON START ACTIVITY!!!!!!!!
			//Bundle b = new Bundle(Blink);
			intent1.putExtra(EXTRA_DEVICE, device);
			//intent1.putExtra(,mViewModel.mBlinkyManager);
			startActivity(intent1);
		});
	}

	@OnClick(R.id.action_clear_cache)
	public void onTryAgainClicked() {
		mViewModel.reconnect();
	}

	private void onConnectionStateChanged(final boolean connected) {
		mLed.setEnabled(connected);
		if (!connected) {
			mLed.setChecked(false);
			mButtonState.setText(R.string.button_unknown);
		}
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.mViewModel, flags);
		dest.writeParcelable((Parcelable) this.mLed, flags);
		dest.writeParcelable((Parcelable) this.mButtonState, flags);
		dest.writeParcelable((Parcelable) this.controlBraceletButton, flags);
	}

	public BlinkyActivity() {
	}

	protected BlinkyActivity(Parcel in) {
		this.mViewModel = in.readParcelable(BlinkyViewModel.class.getClassLoader());
		this.mLed = in.readParcelable(Switch.class.getClassLoader());
		this.mButtonState = in.readParcelable(TextView.class.getClassLoader());
		this.controlBraceletButton = in.readParcelable(Button.class.getClassLoader());
	}

	public static final Parcelable.Creator<BlinkyActivity> CREATOR = new Parcelable.Creator<BlinkyActivity>() {
		@Override
		public BlinkyActivity createFromParcel(Parcel source) {
			return new BlinkyActivity(source);
		}

		@Override
		public BlinkyActivity[] newArray(int size) {
			return new BlinkyActivity[size];
		}
	};
}
