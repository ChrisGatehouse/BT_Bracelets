/*
 * Copyright (c) 2019, CapstoneB
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
package bt.bracelet.android.capstone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;
import no.nordicsemi.android.blinky.R;


public class TimerScrollActivity extends Activity {
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    NumberPicker secondPicker;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_timer);

        hourPicker = (NumberPicker)findViewById(R.id.numberPicker1);
        minutePicker = (NumberPicker)findViewById(R.id.numberPicker2);
        secondPicker = (NumberPicker)findViewById(R.id.numberPicker3);

        textView = (TextView)findViewById(R.id.textView1);

        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int minutes = newVal * 60 + minutePicker.getValue();
                int seconds = secondPicker.getValue();
                textView.setText("Timer for " + minutes + " minutes and " + seconds + " seconds");
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public  void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int minutes = hourPicker.getValue() * 60 + newVal;
                int seconds = secondPicker.getValue();
                textView.setText("Timer for " + minutes + " minutes and " + seconds + " seconds");
            }
        });
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int minutes = hourPicker.getValue() * 60 + minutePicker.getValue();
                int seconds = newVal;
                textView.setText("Timer for " + minutes + " minutes and " + seconds + " seconds");
            }
        });
    }
}
