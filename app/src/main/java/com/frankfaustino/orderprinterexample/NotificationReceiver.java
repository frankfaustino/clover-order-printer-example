package com.frankfaustino.orderprinterexample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clover.sdk.v1.app.AppNotification;
import com.clover.sdk.v1.app.AppNotificationReceiver;

public class NotificationReceiver extends AppNotificationReceiver {
    public final static String NOTIFICATION = "test_notification";

    public NotificationReceiver() {}

    @Override
    public void onReceive(Context context, AppNotification notification) {
        Log.d("👽", notification.appEvent);

        // if "event" value == "test_notification",
        // then we'll send the notification payload to MainActivity via Intent
        if (notification.appEvent.equals(NOTIFICATION)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.PAYLOAD, notification.payload);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
