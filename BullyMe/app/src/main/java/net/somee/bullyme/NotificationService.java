package net.somee.bullyme;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.TypedValue;


public class NotificationService extends Service {

    //dont even fucking dream of messing with this
    NotificationReceiver alarm = new NotificationReceiver();
    public void onCreate()
    {
        super.onCreate();
    }

    public void onStart(Context context,Intent intent, int startId)
    {
        try {
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.notification_intervalInHours, outValue, true);
            double value = outValue.getFloat();

            alarm.SetAlarm(context, value);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
