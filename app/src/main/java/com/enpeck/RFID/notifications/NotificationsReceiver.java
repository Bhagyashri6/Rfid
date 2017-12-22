package com.enpeck.RFID.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.enpeck.RFID.R;
import com.enpeck.RFID.common.Constants;
import com.enpeck.RFID.home.MainActivity;
import com.enpeck.RFID.settings.SettingsDetailActivity;

/**
 * This class acts as a receiver for the RFID Reader Events when the app is running in the background.
 * It adds the message as a notification.
 */
public class NotificationsReceiver extends BroadcastReceiver {

    public NotificationsReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = null;
        if (intent.getAction() != null && (intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_BATTERY_CRITICAL) || intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_AVAILABLE) || intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_CONN_FAILED) || intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_DISCONNECTED) || intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_CONNECTED)))
            resultIntent = new Intent(context, SettingsDetailActivity.class);
        else {
            resultIntent = new Intent(context, MainActivity.class);
            resultIntent.setAction(Intent.ACTION_MAIN);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        if (intent.getAction() != null && ((intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_BATTERY_CRITICAL)) || (intent.getAction().equalsIgnoreCase(Constants.ACTION_READER_BATTERY_LOW))))
            resultIntent.putExtra(Constants.SETTING_ITEM_ID, 8);
        else
            resultIntent.putExtra(Constants.SETTING_ITEM_ID, 1);
        resultIntent.putExtra(Constants.FROM_NOTIFICATION, true);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.ic_stat_notify_msg);
        contentView.setTextViewText(R.id.text, intent.getStringExtra(Constants.NOTIFICATIONS_TEXT));
        contentView.setTextViewText(R.id.title, context.getString(R.string.app_title));
        mBuilder.setContent(contentView);
        mBuilder.setSmallIcon(R.drawable.ic_stat_notify_msg);
        mBuilder.setAutoCancel(true);
        // build notification
        mBuilder.setContentIntent(resultPendingIntent);
        //Notify with the notification ID
        int DEFAULT_NOTIFICATION_ID = 1;
        mgr.notify(intent.getIntExtra(Constants.NOTIFICATIONS_ID, DEFAULT_NOTIFICATION_ID), mBuilder.build());
    }
}
