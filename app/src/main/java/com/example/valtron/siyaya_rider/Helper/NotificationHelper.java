package com.example.valtron.siyaya_rider.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.valtron.siyaya_rider.R;

public class NotificationHelper extends ContextWrapper {

    private static final String SIYAYA_CHANNEL_ID = "com.example.valtron.siyaya_rider.SIYAYA";
    private static final String SIYAYA_CHANNEL_NAME = "SIYAYA";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel siyayaChannels = new NotificationChannel(SIYAYA_CHANNEL_ID,
                SIYAYA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        siyayaChannels.enableLights(true);
        siyayaChannels.enableVibration(true);
        siyayaChannels.setLightColor(Color.GRAY);
        siyayaChannels.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(siyayaChannels);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getSiyayaNotification(String title, String content, PendingIntent contentIntent,
                                                                  Uri soundUri) {
        return new android.app.Notification.Builder(getApplicationContext(), SIYAYA_CHANNEL_ID)
                .setContentText(content)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_car);
    }
}
