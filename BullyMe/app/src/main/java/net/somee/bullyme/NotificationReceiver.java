package net.somee.bullyme;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    public void SetAlarm(Context context, double intervalInHours)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (long)(1000 * 60 * 60 * intervalInHours), pi); // Millisec * Second * Minute * Hour
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "bullyMe:wakeLockTag");
        wl.acquire(10000);

        //Dynamically generate a notification

        String title = "";
        String content = "";

        //Choose a notification
        createNotificationChannel(context);
        String[] notifInfo = getNotificationInformation(context);

        title = notifInfo[0];
        content = notifInfo[1];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_app_transparent)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Use the characters in the title to generate a unique ID based off the MD5 hash of the title
        int sum = 0;
        for (char var : getMD5(title).toCharArray()){
            sum += var;
        }

        //Build and send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(sum, builder.build());

        //End Event
        wl.release();
    }

    public static String[] getNotificationInformation(Context context) {
        //Notification array comes in semi-colon separated
        String[] notifications = context.getResources().getStringArray(R.array.Notifications);
        List<String> personalizedNotifications = new ArrayList<>();
        List<String> genericNotifications = new ArrayList<>();
        String[] notificationToSend;

        //Split up all lines into personalized and generic based on if they have a replacement character in it
        for (String notification: notifications) {
            if (notification.contains("{")){
                personalizedNotifications.add(notification);
            } else {
                genericNotifications.add(notification);
            }
        }

        //Grab our list of answers to see if we even have any
        ArrayList<String> answers = new ArrayList<>();

        try {
            FileInputStream in = context.openFileInput("answers");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                answers.add(line);
            }

            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //50 - 50 shot of getting a personalized or generic notification
        Random rand = new Random();
        int random = rand.nextInt(2);

        if (answers.size() > 0) {
            if (random == 0) {
                //Grab the personalized version of the notification as a string array { title, content}
                String notification = personalizedNotifications.get(personalizedNotifications.size() - 1);
                notificationToSend = getPersonalizedNotification(context, notification).split(";", -1);
            } else {
                //Grab the generic version as a string array { title, content}
                notificationToSend = genericNotifications.get(rand.nextInt(genericNotifications.size())).split(";", -1);
            }
        } else {
            notificationToSend = genericNotifications.get(rand.nextInt(genericNotifications.size())).split(";", -1);
        }

        //Return the notification as {title, content}
        return notificationToSend;
    }

    public static String getPersonalizedNotification(Context context, String notification){
        String[] parts = notification.split("\\{", -1);
        String key = "{";

        for (char c : parts[1].toCharArray()){
            key += c;
            if (c == '}'){
                break;
            }
        }

        //Grab the answers from the resources file stored as "key;value"

        ArrayList<String> answers = new ArrayList<>();

        try {
            FileInputStream in = context.openFileInput("answers");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                answers.add(line);
            }

            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String replacement : answers){
            if (replacement.contains(key)){
                String[] values = replacement.split(";", -1);
                notification = notification.replace(key, values[1]);
                break;
            }
        }

        return notification;
    }

    public static String getMD5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.channel_id), name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
