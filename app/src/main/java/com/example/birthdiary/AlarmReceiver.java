package com.example.birthdiary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_FLAG = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String name;
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        if (intent.getAction().equals(GlobalValues.TIMER_ACTION_REPEATING)) {
            Log.e("alarm_receiver", "週期鬧鐘");
        } else if (intent.getAction().equals(GlobalValues.TIMER_ACTION)) {
            Log.e("alarm_receiver", "定時鬧鐘");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "text", NotificationManager.IMPORTANCE_HIGH);
            Notification.Builder builder = new Notification.Builder(context, "1");
//            Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
//            bigPictureStyle.setBigContentTitle("Photo");
//            bigPictureStyle.setSummaryText("SummaryText");
//            Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable((R.drawable.ic_launcher_foreground))).getBitmap();
//            bigPictureStyle.bigPicture(bitmap);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            builder.setContentTitle("Birthday");
            builder.setContentText(name);
            builder.setChannelId("1");
//            builder.setStyle(bigPictureStyle);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(0, builder.build());

        }
    }
}