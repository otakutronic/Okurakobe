package mji.tapia.com.service.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
        sendNotification("Wake Up! Wake Up! Wake Up!");
    }

    //handle notification
    private void sendNotification(String msg) {

        isActive = true;

        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        /*Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        Dialog dialog = new Dialog(this);
        //builder1.setMessage("Write your message here.");
        dialog.setCanceledOnTouchOutside(true);

        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);*/

        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PreferenceUtils sharedManager = PreferenceUtilsImpl.getInstance(this.getApplicationContext());

        //get pending intent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, sharedManager.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg).setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());
    }


}
