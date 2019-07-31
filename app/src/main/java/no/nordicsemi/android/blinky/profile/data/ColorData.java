package no.nordicsemi.android.blinky.profile.data;

import no.nordicsemi.android.ble.data.Data;

public final class ColorData {

        public int rgb[] = new int[3];

        public int[] sendData()
        {
           return rgb;
        }




}
