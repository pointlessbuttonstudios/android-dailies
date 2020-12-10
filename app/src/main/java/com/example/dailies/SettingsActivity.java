package com.example.dailies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    public static String sharePrefFile = "com.example.dailies.sharedprefs";
    public static String DESC_SWITCH_TAG =  "showDescSwitch";

    private Switch showDesc;

    // REQUIRED FOR CREATING A NOTIFICATION

    // You are only required to create one channel for your app?
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    // Notification ID's should be unique to the notification
    private static final int NOTIFICATION_ID = 0;
    // REQUIRED FOR CREATING A NOTIFICATION

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setttings);

        showDesc = findViewById(R.id.hide_desc_switch);
        sharedPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);

        showDesc.setChecked(sharedPreferences.getBoolean(DESC_SWITCH_TAG,true));

        showDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.putBoolean(DESC_SWITCH_TAG, isChecked);
                preferencesEditor.apply();
            }
        });

        // REQUIRED FOR CREATING A NOTIFICATION

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // create notification channel
            notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Dailies Notification");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        // REQUIRED FOR CREATING A NOTIFICATION
    }
    // GETS THE NOTIFICATION TO HAPPEN
    public void TestButtonPressed(View view)
    {
        // This button should create a notification
        NotificationCompat.Builder notificationCompatBuilder = getNotificationBuilder();
        notificationManager.notify(NOTIFICATION_ID, notificationCompatBuilder.build());
    }
    // CALLED BY TESTBUTTONPRESSED
    private NotificationCompat.Builder getNotificationBuilder()
    {
        // CREATE AN INTENT FOR THANKS
        Intent intent = new Intent(this, DailyReceiver.class);
        intent.setAction(Intent.ACTION_DIAL);
        intent.putExtra("", 0);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // CREATE AN INTENT FOR ADJUST DAILY - WOULD HAVE TO KNOW THE NOTIFICATION ID IN ORDER TO OPEN UP THE RIGHT DAILY
        Bitmap notificationImage = BitmapFactory.decodeResource(getResources(), R.drawable.time_background);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID);
        notificationCompatBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notificationImage).setBigContentTitle("HELLO!"));
        notificationCompatBuilder.setContentText("This is your notification text");
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_schedule);
        notificationCompatBuilder.addAction(R.drawable.rounded_rectangle, "Thanks", snoozePendingIntent);
        notificationCompatBuilder.addAction(R.drawable.rounded_rectangle, "Adjust Daily", snoozePendingIntent);
        return notificationCompatBuilder;
    }

    //TODO
    /*
    1. Notification should have a button that is interactable - Something like "Doing it now", "Mark Completed"
    2. Just marks it as "Doing it now" when you swipe it away
    3. Tapping the notification should take you to the page to mark it as done
    4. Need to add notification ID's to notifications
    * */
}
