package bt.bracelet.android.capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.Console;

public class AlarmReceiver extends BroadcastReceiver {
    private PowerManager.WakeLock wl;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: send alarm (timer done) to the bracelet
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":Tag");
        wl.acquire();

        // Put here YOUR code.

        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        wl.release();
    }

}
