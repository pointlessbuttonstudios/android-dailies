package com.example.dailies;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class DailyReceiver extends BroadcastReceiver
{
    private Utility.ReceiverCommand DoCommand = null;

    private String LOG = "------> DailyReceiver";
    private static final String NOTIFICATION_CHANNEL_ID = "daily_notification_channel";
    private NotificationManager notificationManager;

    private Context context;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.intent = intent;
        DoCommand = Utility.convertFromIntToReceiverCommand(intent.getIntExtra("BROADCAST_COMMAND", 0));
        switch(DoCommand)
        {
            case DEFAULT:
                Log.d("LOG", "DoCommand received as DEFAULT");
                break;
            case ANNOUNCE:
                Log.d("LOG", "DoCommand received as ANNOUNCE");
                Daily dailyToAnnounce = (Daily) intent.getExtras().getSerializable("DAILY_TO_ANNOUNCE");
                NotificationCompat.Builder notificationCompatBuilder = getNotificationBuilder(context, dailyToAnnounce);
                notificationManager.notify(dailyToAnnounce.channelID, notificationCompatBuilder.build());
                break;
            case DISMISS:
                // THIS DISMISSES A NOTIFICATION WITH A UNIQUE ID
                Log.d("LOG", "DoCommand received as DISMISS");
                int notification_id = intent.getIntExtra("NOTIFICATION_ID", 0);
                Log.d(LOG, "About to dismiss daily with ID:" + notification_id);
                notificationManager.cancel(notification_id);
                break;
            case CANCEL:
                Log.d("LOG", "DoCommand received as CANCEL");
                Daily dailyToCancel = (Daily) intent.getExtras().getSerializable("DAILY_TO_CANCEL");
                CancelAllPendingIntents(dailyToCancel.channelID);
                break;
        }
    }
    private NotificationCompat.Builder getNotificationBuilder(Context context, Daily daily)
    {
        // DISMISS
        Intent thanksIntent = new Intent(context, DailyReceiver.class);
        thanksIntent.putExtra("BROADCAST_COMMAND", Utility.convertFromReceiverCommandToInt(Utility.ReceiverCommand.DISMISS));
        thanksIntent.putExtra("NOTIFICATION_ID", daily.channelID);
        PendingIntent thanksPendingIntent = PendingIntent.getBroadcast(context, 0, thanksIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // ADJUST DAILY
        Intent editDailyIntent = new Intent(context, DailyActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("DAILY_FROM_NOTIFICATION", daily);

        bundle.putBoolean(DailyActivity.EDIT_TAG, false);
        bundle.putBoolean(DailyActivity.ADJUST_DAILY_FROM_NOTIFICATION, true);

        editDailyIntent.putExtras(bundle);

        PendingIntent adjustDailyPendingIntent = PendingIntent.getActivity(context, 0, editDailyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(LOG, "Daily thats setup with adjustdailyintent:\n" + daily.toString());

        Bitmap notificationImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.time_background);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context, MainActivity.PRIMARY_CHANNEL_ID);
        notificationCompatBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notificationImage).setBigContentTitle("HELLO!"));
        notificationCompatBuilder.setContentText("daily ID: " + daily.channelID
                + "\n" + daily.getDescription());
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_schedule);

        notificationCompatBuilder.addAction(R.drawable.rounded_rectangle, "Thanks", thanksPendingIntent);
        notificationCompatBuilder.addAction(R.drawable.rounded_rectangle, "Adjust Daily" , adjustDailyPendingIntent);

        notificationCompatBuilder.setAutoCancel(true);

        return notificationCompatBuilder;
    }
    private void CancelAllPendingIntents(int dailyChannelID)
    {
        PendingIntent.getBroadcast(this.context, dailyChannelID, this.intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }
}
