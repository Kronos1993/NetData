package com.kronos.netdata.Activities.Notifications;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.kronos.netdata.R;

public class NetDataNotification {

        public static void createNotification(String title,String description,NotificationsId notificationsId,Context context){

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            Notification notification = new NotificationCompat.Builder(context,NotificationsChanels.NOTIFICATION_CHANEL)
                    .setSmallIcon(R.drawable.app_icon_drawer)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(description)
                            .setBigContentTitle(title)
                    )
                    .setContentText(description.substring(0,description.toCharArray().length/2))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setGroup("NetDataGroup")
                    .build();

            notificationManager.notify(notificationsId.ordinal(),notification);
        }


        public static void createProgressBarNotification(int max,int progress,NotificationsId notificationsId, Context context){

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,NotificationsChanels.NOTIFICATION_CHANEL)
                .setContentTitle(context.getString(R.string.downloading))
                .setContentText(context.getString(R.string.downloading_progess))
                .setProgress(max,progress,false)
                .setSmallIcon(R.drawable.app_icon_drawer)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        notificationManager.notify(notificationsId.ordinal(),notification.build());
    }

    public static void hideNotification(NotificationsId notificationsId, Context context){

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationsId.ordinal());
    }

}
