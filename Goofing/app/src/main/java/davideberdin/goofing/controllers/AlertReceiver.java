package davideberdin.goofing.controllers;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import davideberdin.goofing.LoginActivity;
import davideberdin.goofing.utilities.Constants;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, Constants.NOTIFICATION_TITLE, Constants.NOTIFICATION_MESSAGE, Constants.NOTIFICATION_ALARM_TEXT);
    }

    private void createNotification(Context context, String title, String message, String messageAlert) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), 0);
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(messageAlert)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        notificationBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
