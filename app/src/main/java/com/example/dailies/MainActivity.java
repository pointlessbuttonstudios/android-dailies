package com.example.dailies;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private Menu menu;
    private RecyclerView dailiesRecyclerView;
    private LinearLayout noDailiesView;

    private DailyViewModel dailyViewModel;
    private DailyListAdapter dailyListAdapter;

    public static final int CREATE_DAILY = 10;
    private String LOG = "------> MainActivity";

    private List<Integer> dailyNotificationIDs = new ArrayList<>();

    private boolean allDailiesChecked = true;

    public static final String PREFFERNECES_NAME = "com.example.dailies.sharedprefs";

    // RELATED TO NOTIFICATIONS ONLY

    public static final String PRIMARY_CHANNEL_ID = "primary_notificiation_channel";

    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    // RELATED TO NOTIFICATIONS ONLY

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        final Intent newDailyIntent = new Intent(this, DailyActivity.class);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(newDailyIntent, CREATE_DAILY);

                /*Snackbar.make(view, "Add Daily", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        Utility.sharedPreferences = getSharedPreferences(PREFFERNECES_NAME, MODE_PRIVATE);

        dailiesRecyclerView = findViewById(R.id.recyclerView);
        noDailiesView = findViewById(R.id.no_dailies_view);

        // setup daily ListAdapter
        dailyListAdapter = new DailyListAdapter(this);
        dailiesRecyclerView.setAdapter(dailyListAdapter);
        dailiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // setup daily ViewModel
        dailyViewModel = ViewModelProviders.of(this).get(DailyViewModel.class);
        dailyViewModel.getAllDailies().observe(this, new Observer<List<Daily>>() {
            @Override
            public void onChanged(@Nullable final List<Daily> dailies)
            {
                // Update the cached copy of the words in the adapter.
                dailyListAdapter.setDailies(dailies);
                if(dailyListAdapter.getItemCount() > 0)
                {
                    dailiesRecyclerView.setVisibility(View.VISIBLE);
                    noDailiesView.setVisibility(View.GONE);
                }
                else
                {
                    dailiesRecyclerView.setVisibility(View.GONE);
                    noDailiesView.setVisibility(View.VISIBLE);
                }
            }
        });
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // create notification channel
            notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "PRIMARY CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("PRIMARY CHANNEL");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(LOG, "onResume() called");
        if(getIntent()!= null && getIntent().getExtras()!= null)
        {
            Intent justGotAnUpdateIntent = getIntent();

            Daily toUpdate = (Daily) justGotAnUpdateIntent.getExtras().getSerializable("DAILY");
            boolean isInEditMode = justGotAnUpdateIntent.getExtras().getBoolean(DailyActivity.EDIT_TAG);

            Log.d(LOG, "RECEIVED isInEditMode as: " + isInEditMode);

            if(isInEditMode)
            {
                Log.d(LOG, "updating existing view model\n" + toUpdate.toString());
                dailyViewModel.update(toUpdate);
                SetupAlarmScheduler(toUpdate);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            if(getTitle().equals("Delete dailies"))
            {
                setTitle("Dailies");

                menu.findItem(R.id.delete_dailies).setVisible(false);
                menu.findItem(R.id.select_all_dailies).setVisible(false);

                List<Daily> currentDailies = dailyListAdapter.getDailies();
                for(int i = 0; i < currentDailies.size(); i++)
                {
                    currentDailies.get(i).setCheckBoxVisible(false);
                    currentDailies.get(i).setCheckBoxChecked(false);
                }
                dailyListAdapter.setDailies(currentDailies);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            }
        }
        else if (id == R.id.action_settings)
        {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        else if(id == R.id.delete_dailies) // with trashcan icon
        {
            List<Daily> dailiesToRemove = new ArrayList<>();
            List<Daily> currentDailies = dailyListAdapter.getDailies();
            for(int i = 0; i < currentDailies.size(); i++)
            {
                if (currentDailies.get(i).isCheckBoxVisible()
                    && currentDailies.get(i).isCheckBoxChecked())
                {
                    dailiesToRemove.add(currentDailies.get(i));
                    //dailyViewModel.deleteDaily(currentDailies.get(i));
                }
            }
            if(dailiesToRemove.size() > 0)
            {
                for(int i = 0; i < dailiesToRemove.size(); i++)
                {
                    dailyViewModel.deleteDaily(dailiesToRemove.get(i));
                }
                // hide the trash can
                menu.findItem(R.id.delete_dailies).setVisible(false);
                menu.findItem(R.id.select_all_dailies).setVisible(false);

                setTitle("Dailies");

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                if(dailyListAdapter.getDailies().size() == 0)
                {
                    dailiesRecyclerView.setVisibility(View.GONE);
                    noDailiesView.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                Utility.Toast(this, "Please select a daily to delete");
            }
        }
        else if(id == R.id.select_all_dailies)
        {
            List<Daily> currentDailies = dailyListAdapter.getDailies();

            for(int i = 0; i < currentDailies.size(); i++)
            {
                currentDailies.get(i).setCheckBoxVisible(true);
                currentDailies.get(i).setCheckBoxChecked(allDailiesChecked);
            }
            dailyListAdapter.setDailies(currentDailies);
            allDailiesChecked = !allDailiesChecked;
        }
        return super.onOptionsItemSelected(item);
    }
    public void deleteADaily()
    {
        List<Daily> dailies = dailyListAdapter.getDailies();
        for(int i = 0; i< dailies.size(); i++)
        {
            dailies.get(i).setCheckBoxVisible(true);
        }
        dailyListAdapter.setDailies(dailies);
        setTitle("Delete dailies");

        menu.findItem(R.id.delete_dailies).setVisible(true);
        menu.findItem(R.id.select_all_dailies).setVisible(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG, "\nresult code -1 expected\nrequestcode: " + requestCode + "\nresultcode: "  + resultCode);
        if(requestCode == CREATE_DAILY && resultCode == RESULT_OK)
        {
            Daily dailyReceived = (Daily) data.getExtras().getSerializable("DAILY");
            Log.d(LOG, "New Daily to insert is\n" + dailyReceived.toString());
            if(dailyReceived != null)
            {
                dailiesRecyclerView.setVisibility(View.VISIBLE);
                noDailiesView.setVisibility(View.GONE);

                Log.d(LOG, "inserting new daily into the view model");
                dailyViewModel.insert(dailyReceived);
                SetupAlarmScheduler(dailyReceived);
            }
            else
            {
                Log.d(LOG, "onActivityResult - daily was null");
            }
        }
        else
        {
            Log.d(LOG, "onActivityResult - result was not ok");
        }
    }
    public void SetupAlarmScheduler(Daily daily)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // ! ! ! this may need to be created fresh each time ! ! !
        Intent broadcastOut = new Intent(this, DailyReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("DAILY_TO_ANNOUNCE", daily);
        broadcastOut.putExtra("BROADCAST_COMMAND", Utility.convertFromReceiverCommandToInt(Utility.ReceiverCommand.ANNOUNCE));
        broadcastOut.putExtras(bundle);

        PendingIntent dailyIntent = PendingIntent.getBroadcast(this, daily.getChannelID(), broadcastOut, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmManager != null)
        {
            // 0 1 2 3 4 5 6
            // M T W T F S S

            // 00  01  02  03  04  05  06  07  08  09  10  11  AM
            // 12  13  14  15  16  17  18  19  20  21  22  23  PM

            for (int i = 0; i < daily.getRemindOnDays().size(); i++) // go through the days this daily has been set
            {
                calendar.set(Calendar.DAY_OF_WEEK, i); // setting it to happen everyday that's been set

                for (int j = 0; i < daily.getRemindAtTimes().size(); j++) // go through the times this daily has been set
                {
                    if (daily.getRemindAtTimes().get(j).getIsTime()) // daily @ time type
                    {
                        calendar.set(Calendar.HOUR_OF_DAY, daily.getRemindAtTimes().get(j).getHour());
                        calendar.set(Calendar.MINUTE, daily.getRemindAtTimes().get(j).getMinute());

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, dailyIntent);
                    }
                    else // daily @ interval type
                    {
                        calendar.set(Calendar.HOUR_OF_DAY, 0); // starting at 12 am
                        switch (daily.getRemindAtTimes().get(j).getIntervalType())
                        {
                            case ONE_HR:
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, dailyIntent);
                                break;
                            case THREE_HR:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 3, dailyIntent);
                                break;
                            case SIX_HR:
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, dailyIntent);
                                break;
                            default:
                                Log.e(LOG, "Interval type not recognized");
                                break;
                        }
                    }
                }
            }
        }
    }
    public void CancelPendingIntents(Daily daily)
    {
        Intent cancelDailiesIntents = new Intent(this, DailyReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("DAILY_TO_CANCEL", daily);

        cancelDailiesIntents.putExtra("BROADCAST_COMMAND", Utility.convertFromReceiverCommandToInt(Utility.ReceiverCommand.CANCEL));
        cancelDailiesIntents.putExtras(bundle);

        try
        {
            PendingIntent.getBroadcast(this, daily.getChannelID(), cancelDailiesIntents, PendingIntent.FLAG_UPDATE_CURRENT).send();
        }
        catch(PendingIntent.CanceledException e)
        {
            e.printStackTrace();
            Log.e(LOG, e.toString());
        }
    }
}