package com.kronos.netdata.Activities.Notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationsChanels extends Application {

    public static final String NOTIFICATION_CHANEL = "NetData_Notification_Chanel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChanel();
    }

    private void createNotificationChanel() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channelNetData = new NotificationChannel(NOTIFICATION_CHANEL,NOTIFICATION_CHANEL
                    , NotificationManager.IMPORTANCE_HIGH);
            channelNetData.setDescription(NOTIFICATION_CHANEL);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelNetData);
        }

    }

}
