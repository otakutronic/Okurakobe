package mji.tapia.com.service.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import mji.tapia.com.service.R;
import mji.tapia.com.service.util.PreferenceUtils;
import mji.tapia.com.service.util.PreferenceUtilsImpl;

/**
 * Created by andy on 10/04/18
 */

public class AlarmNotificationService extends IntentService {
    private NotificationManager alarmNotificationManager;

    //Notification ID for Alarm
    public static final int NOTIFICATION_ID = 1;

    public static boolean isActive = false;

    public AlarmNotificationService() {
        super("AlarmNotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        //Send notification
        sendNotification("Wake Up!");
    }

    //handle notification
    private void sendNotification(String msg) {
        isActive = true;

        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PreferenceUtils sharedManager = PreferenceUtilsImpl.getInstance(this.getApplicationContext());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, sharedManager.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg).setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);

        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());
    }


}
