package bt.bracelet.android.capstone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.Console;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.profile.BlinkyManager;

public class AlarmReceiver extends BroadcastReceiver {
    private PowerManager.WakeLock wl;
    private NotificationManager manager;
   // private NotificationChannel channel;
    private static final String channel_id = "minimeID";
    private static final CharSequence channel_name = "android channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: send alarm (timer done) to the bracelet
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":Tag");
        wl.acquire(15000);

        createNotificationChannel(context);
        sendNotification(context);

        BlinkyManager blinky1 = new BlinkyManager(context);
        blinky1.SendVibrate(true);

        // Put here YOUR code.
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        wl.release();
    }
    private void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_timer_black)
                .setContentTitle("Mini Me Caller")
                .setContentText("Your alarm has gone off. Your spawn has been summoned")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Log.i("tag:", "sending notification!!");
        Intent intent = new Intent(context, Schedule_Screen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 666, intent, 0);
        builder.setContentIntent(pendingIntent);
        //manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(667,builder.build());
    }

}
