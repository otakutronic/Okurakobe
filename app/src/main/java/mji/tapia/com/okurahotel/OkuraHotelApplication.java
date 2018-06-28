package mji.tapia.com.okurahotel;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.data.error_log.ErrorLogRepository;
import mji.tapia.com.okurahotel.dagger.ComponentFactory;
import mji.tapia.com.okurahotel.dagger.application.ApplicationComponent;
import mji.tapia.com.okurahotel.ui.splash.SplashActivity;
import mji.tapia.com.okurahotel.widget.ErrorDialog;
import mji.tapia.com.service.alarm.AlarmCallManager;
import mji.tapia.com.service.alarm.AlarmNotificationService;
import mji.tapia.com.service.error_manager.ErrorManager;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Sami on 9/22/2017.
 */

public class OkuraHotelApplication extends Application {

    private static final String TAG = "OkuraHotelApplication";

    @Inject
    IoTService iotService;

    @Inject
    AlarmCallManager alarmManager;

    @Inject
    ErrorManager errorManager;

    @Inject
    ErrorLogRepository errorLogRepository;

//    @Inject
//    ErrorLogRepository errorLogRepository;

    private ApplicationComponent applicationComponent;

    public static OkuraHotelApplication from(final Context context) {
        return (OkuraHotelApplication) context.getApplicationContext();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        applicationComponent = ComponentFactory.createApplicationComponent(this);
        applicationComponent.inject(this);

        iotService.start();

        alarmManager.resetAlarm();

        errorManager.getMainThreadExceptionObserver()
                .subscribeOn(Schedulers.newThread())
                .subscribe(throwable -> {
                    Log.e(TAG, "MAIN THREAD: ERROR");
                    throwable.printStackTrace();

                    ErrorLog errorLog = new ErrorLog();
                    errorLog.setTime(new Date());
                    errorLog.setName(throwable.getMessage());
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    String sStackTrace = sw.toString();
                    errorLog.setStack(sStackTrace);
                    errorLog.setType(ErrorLog.Type.RUNTIME);
                    errorLogRepository.addErrorLog(errorLog);

                    ErrorDialog errorDialog = new ErrorDialog(getApplicationContext());
                            errorDialog.setOnDismissListener(dialogInterface -> {
                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                //This will stop your application and take out from it.
                                System.exit(2);
                            });
//                    errorDialog.setMessage(throwable.getMessage());
                    errorDialog.show();
                    Looper.loop();

        });

        errorManager.getBackgroundThreadsExceptionObserver()
                .subscribeOn(Schedulers.newThread())
                .subscribe(throwable -> {
                    Log.e(TAG, "BACKGROUND THREAD: ERROR");
                    throwable.printStackTrace();

                    ErrorLog errorLog = new ErrorLog();
                    errorLog.setTime(new Date());
                    errorLog.setName(throwable.getMessage());
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    String sStackTrace = sw.toString();
                    errorLog.setStack(sStackTrace);
                    errorLog.setType(ErrorLog.Type.WARNING);
                    errorLogRepository.addErrorLog(errorLog);

                    ErrorDialog errorDialog = new ErrorDialog(getApplicationContext());
//                            errorDialog.setOnDismissListener(dialogInterface -> {
//
//                            });
//                    errorDialog.setMessage(throwable.getMessage());
                    errorDialog.show();
                    Looper.loop();
        });
    }

    public static double getVersion(){
        return Double.parseDouble(BuildConfig.VERSION_NAME);
    }

}
