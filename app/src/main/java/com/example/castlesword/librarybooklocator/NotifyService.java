package com.example.castlesword.librarybooklocator;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;



/**
 * This service is started when an Alarm has been raised
 *
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @author paul.blundell
 */
public class NotifyService extends BroadcastReceiver {

  /*  public void onCreate() {
        Log.i("NotifyService", "onCreate()");

        //mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }*/

    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notification = new Notification.Builder(context).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Book Renew").setContentText("Notificaxtion Text").setAutoCancel(true).setContentIntent(contentIntent);
        notificationManager.notify(100, notification.build());
    }

}