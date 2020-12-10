package com.example.dailies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DailyActivity extends AppCompatActivity
{
    private String LOG = "------> DailyActivity";

    public static String EDIT_TAG = "isInEditMode";
    public static String ADJUST_DAILY_FROM_NOTIFICATION = "adjustingFromNot";

    //private DailyTimeViewModel dailyTimeViewModel;
    public boolean isInEditMode = false;

    private int numOfDaysSelected = 0;
    private int numOfTimesCreated = 0;

    private int selectedColorWhiteID;
    private int unselectedColorBlackID;

    private Drawable selected;
    private Drawable unselected;

    private EditText titleEditText, descEditText;

    public static RecyclerView timesRecyclerView;
    public static TextView noTimesSetTextview;

    private DailyTimeListAdapter dailyTimesListAdapter;

    private List<Button> Day_Buttons = new ArrayList<>();
    private List<DailyTime> dailyTimes = new ArrayList<>();

    private Daily editableDaily;

    private Menu menu;

    private boolean allTimesChecked = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // layout setup
        setContentView(R.layout.activity_add_dailly);

        titleEditText = findViewById(R.id.title_edittext);
        descEditText = findViewById(R.id.desc_edittext);

        timesRecyclerView = findViewById(R.id.times_list);
        noTimesSetTextview = findViewById(R.id.no_reminder_times);

        // setup timesList adapter
        dailyTimesListAdapter = new DailyTimeListAdapter(this);
        dailyTimesListAdapter.setDailyTimes(dailyTimes); // this is an empty list at this point

        timesRecyclerView.setAdapter(dailyTimesListAdapter);
        timesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // REMOVED VIEW MODEL STUFF FROM dailyTimes
        // setup dailyTime ViewModel
        /*dailyTimeViewModel = ViewModelProviders.of(this).get(DailyTimeViewModel.class);
        dailyTimeViewModel.getAllDailyTimes().observe(this, new Observer<List<DailyTime>>()
        {
            @Override
            public void onChanged(@Nullable final List<DailyTime> dailyTimes)
            {
                adapter.setDailyTimes(dailyTimes);
            }
        });*/

        Button monButton, tueButton, wedButton, thursButton, friButton, satButton, sunButton;

        monButton = findViewById(R.id.monButton);
        tueButton = findViewById(R.id.tueButton);
        wedButton = findViewById(R.id.wedButton);
        thursButton = findViewById(R.id.thurButton);
        friButton = findViewById(R.id.friButton);
        satButton = findViewById(R.id.satButton);
        sunButton = findViewById(R.id.sunButton);

        Day_Buttons.add(monButton);
        Day_Buttons.add(tueButton);
        Day_Buttons.add(wedButton);
        Day_Buttons.add(thursButton);
        Day_Buttons.add(friButton);
        Day_Buttons.add(satButton);
        Day_Buttons.add(sunButton);

        selected = getResources().getDrawable(R.drawable.weekday_selected);
        unselected = getResources().getDrawable(R.drawable.weekday_unselected);
        selectedColorWhiteID = getResources().getColor(R.color.colorWhite);
        unselectedColorBlackID = getResources().getColor(R.color.colorBlack);

        // make sure all buttons are unselected (clean slate)
        for(int i = 0; i < Day_Buttons.size();i++)
        {
            Day_Buttons.get(i).setBackground(unselected);
        }
        // EDITING ZONE
        if(getIntent() != null && getIntent().getExtras() != null)
        {
            // todo determine if you're receiving an intent from a notification or from a regular action

            boolean receivedEditFromNot = getIntent().getExtras().getBoolean(ADJUST_DAILY_FROM_NOTIFICATION);
            isInEditMode = getIntent().getExtras().getBoolean(EDIT_TAG);
            if(receivedEditFromNot)
            {
                editableDaily = (Daily) getIntent().getExtras().getSerializable("DAILY_FROM_NOTIFICATION");
                // DISMISS the notification once they've hit "adjust daily"
                Intent thanksIntent = new Intent(this, DailyReceiver.class);
                thanksIntent.putExtra("BROADCAST_COMMAND", Utility.convertFromReceiverCommandToInt(Utility.ReceiverCommand.DISMISS));
                thanksIntent.putExtra("NOTIFICATION_ID", editableDaily.channelID);

                PendingIntent thanksPendingIntent = PendingIntent.getBroadcast(this, 0, thanksIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try
                {
                    thanksPendingIntent.send(this, 0, thanksIntent);
                }
                catch(PendingIntent.CanceledException CancelledException)
                {
                    Log.e(LOG, CancelledException.toString());
                }
            }
            else if(isInEditMode)
            {
                editableDaily = (Daily) getIntent().getExtras().getSerializable("DAILY");
            }
            Log.d(LOG, "onCreate - EDITING ZONE - isInEditMode: " + isInEditMode);
            Log.d(LOG, "onCreate - EDITING ZONE - receivedEditFromNot: " + receivedEditFromNot);

            Log.d(LOG, "editable daily:" + editableDaily.toString());

            setTitle(R.string.title_edit_daily);
            titleEditText.setText(editableDaily.title);
            descEditText.setText(editableDaily.description);

            Log.d(LOG, "onCreate - editableDaily.getReminderAtTimes().size is: "+ editableDaily.getRemindAtTimes().size());
            if(editableDaily.getRemindAtTimes().size() > 0)
            {
                timesRecyclerView.setVisibility(View.VISIBLE);
                noTimesSetTextview.setVisibility(View.GONE);

                dailyTimes = editableDaily.getRemindAtTimes();
                dailyTimesListAdapter.setDailyTimes(dailyTimes);

                numOfTimesCreated = editableDaily.getRemindAtTimes().size();
            }
            List<Integer> days = editableDaily.getRemindOnDays();
            for (int i = 0; i < days.size(); i++)
            {
                //numOfDaysSelected++;
                Utility.Day day = Utility.convertFromIntToDay(days.get(i));
                switch (day)
                {
                    case Monday:
                        onButtonPressed(monButton);
                        break;
                    case Tuesday:
                        onButtonPressed(tueButton);
                        break;
                    case Wednesday:
                        onButtonPressed(wedButton);
                        break;
                    case Thursday:
                        onButtonPressed(thursButton);
                        break;
                    case Friday:
                        onButtonPressed(friButton);
                        break;
                    case Saturday:
                        onButtonPressed(satButton);
                        break;
                    case Sunday:
                        onButtonPressed(sunButton);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    public void onButtonPressed(View view)
    {
        Button button = (Button) view;

        // is currently selected -> make it unselected
        if(button.getBackground() == selected)
        {
            button.setBackground(unselected);
            button.setTextColor(unselectedColorBlackID);
            numOfDaysSelected--;
        }
        // is currently not selected -> make it selected
        else if(button.getBackground() == unselected)
        {
            button.setBackground(selected);
            button.setTextColor(selectedColorWhiteID);
            numOfDaysSelected++;
        }
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(descEditText.getWindowToken(), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_add_daily, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home)
        {
            if(getTitle().equals("Delete times"))
            {
                if(isInEditMode)
                {
                    setTitle("Edit daily");
                }
                else
                {
                    setTitle("Add daily");
                }

                List<DailyTime> currentDailyTimes = dailyTimesListAdapter.getDailyTimes();
                for(int i = 0; i < currentDailyTimes.size(); i++)
                {
                    currentDailyTimes.get(i).setCheckBoxVisible(false);
                    currentDailyTimes.get(i).setCheckBoxChecked(false);
                }
                dailyTimesListAdapter.setDailyTimes(currentDailyTimes);

                menu.findItem(R.id.add_daily).setVisible(true);
                menu.findItem(R.id.select_all_times).setVisible(false);
                menu.findItem(R.id.delete_times).setVisible(false);
                return true;
            }
        }
        else if (id == R.id.add_daily)
        {
            List<Integer> daysSelected = new ArrayList<>();
            boolean canCreateDaily = false;
            String errorMessage = "Complete all fields";

            if (titleEditText.getText().toString().length() > 0)
            {
                for (int i = 0; i < Day_Buttons.size(); i++)
                {
                    if (Day_Buttons.get(i).getBackground() == selected)
                    {
                        daysSelected.add(i);
                    }
                }
                if (numOfDaysSelected > 0)
                {
                    if (numOfTimesCreated > 0)
                    {
                        canCreateDaily = true;
                        for(int i = 0; i < dailyTimes.size(); i++)
                        {
                            dailyTimes.get(i).setCheckBoxVisible(false);
                        }
                        dailyTimesListAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        errorMessage += "\nSelect a time";
                    }
                }
                else
                {
                    errorMessage += "\nSelect a Day";
                }
            }
            else
            {
                errorMessage += "\nAdd a title";
            }

            if (canCreateDaily)
            {
                Daily dailyToSendAccross = null;

                if (isInEditMode)
                {
                    editableDaily.setTitle(titleEditText.getText().toString());
                    editableDaily.setDescription(descEditText.getText().toString());
                    editableDaily.setRemindOnDays(daysSelected);
                    Log.d(LOG, "onDone pressed, editable daily has time count: " + dailyTimes.size());
                    editableDaily.setRemindAtTimes(dailyTimes);
                    dailyToSendAccross = editableDaily;
                }
                else
                {
                    int newChannelID = Utility.createNewChannelID();
                    if(newChannelID != -1)
                    {
                        dailyToSendAccross = new Daily
                        (
                            titleEditText.getText().toString(),
                            descEditText.getText().toString(),
                            daysSelected,
                            dailyTimes,
                            newChannelID
                        );
                    }
                    else
                    {
                        Utility.Toast(this, "Could not create a new channel ID properly");
                        Log.d(LOG, "Could not create a new channel ID properly");
                    }
                }

                Intent returnIntent = new Intent(this, MainActivity.class);

                Bundle bundle = new Bundle();
                if(dailyToSendAccross != null)
                {
                    bundle.putSerializable("DAILY", dailyToSendAccross);
                    bundle.putBoolean(EDIT_TAG, isInEditMode);

                    returnIntent.putExtras(bundle);

                    if (!isInEditMode)
                    {
                        Log.d(LOG, "NEW DAILY CREATED!");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                    else
                    {
                        Log.d(LOG, "EXISTING DAILY ADJUSTED!");
                        startActivity(returnIntent);
                        finish();
                    }
                }
                else
                {
                    Utility.Toast(this, "dailyToSendAccross came back as null");
                    Log.e(LOG, "dailyToSendAccross came back as null");
                }
                return true;
            }
            else
            {
                Utility.Toast(this, errorMessage);
            }
        }
        else if (id == R.id.clear_daily)
        {
            clearDaily();
        }
        else if (id == R.id.reset_days)
        {
            resetDays();
        }
        else if(id == R.id.select_all_times)
        {
            List<DailyTime> currentDailyTimes = dailyTimesListAdapter.getDailyTimes();

            for(int i = 0; i < currentDailyTimes.size(); i++)
            {
                Log.d(LOG, "daily time @ " + i);

                currentDailyTimes.get(i).setCheckBoxVisible(true);
                currentDailyTimes.get(i).setCheckBoxChecked(allTimesChecked);
            }
            dailyTimesListAdapter.setDailyTimes(currentDailyTimes);
            allTimesChecked = !allTimesChecked;
        }
        else if(id == R.id.delete_times)
        {
            List<DailyTime> dailyTimesToRemove = new ArrayList<>();
            List<DailyTime> currentDailyTimes = dailyTimesListAdapter.getDailyTimes();

            for(int i = 0; i < currentDailyTimes.size(); i++)
            {
                if (currentDailyTimes.get(i).isCheckBoxChecked()
                    &&
                        currentDailyTimes.get(i).isCheckBoxVisible())
                {
                    dailyTimesToRemove.add(currentDailyTimes.get(i));
                }
            }
            if(dailyTimesToRemove.size() > 0)
            {
                for(int i = 0; i < dailyTimesToRemove.size(); i++)
                {
                    currentDailyTimes.remove(dailyTimesToRemove.get(i));
                    numOfTimesCreated--;
                }
                dailyTimesListAdapter.setDailyTimes(currentDailyTimes);

                for(int i = 0; i < currentDailyTimes.size(); i++)
                {
                    currentDailyTimes.get(i).setCheckBoxChecked(false);
                    currentDailyTimes.get(i).setCheckBoxVisible(false);
                }
                dailyTimesListAdapter.setDailyTimes(currentDailyTimes);

                if(isInEditMode)
                {
                    setTitle("Edit daily");
                }
                else
                {
                    setTitle("Add daily");
                }

                menu.findItem(R.id.select_all_times).setVisible(false);
                menu.findItem(R.id.delete_times).setVisible(false);
                menu.findItem(R.id.add_daily).setVisible(true);

                if(currentDailyTimes.size() == 0)
                {
                    //graphics
                    noTimesSetTextview.setVisibility(View.VISIBLE);
                    timesRecyclerView.setVisibility(View.GONE);
                }
            }
            else
            {
                Utility.Toast(this, "Please select times to delete");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTime()
    {
        for(int i = 0; i < dailyTimes.size(); i++)
        {
            dailyTimes.get(i).setCheckBoxVisible(true);
        }
        dailyTimesListAdapter.notifyDataSetChanged();

        setTitle("Delete times");

        menu.findItem(R.id.add_daily).setVisible(false);
        menu.findItem(R.id.delete_times).setVisible(true);
        menu.findItem(R.id.select_all_times).setVisible(true);
    }
    public void clearDaily()
    {
        titleEditText.setText("");
        descEditText.setText("");

        resetDays();
        resetTimes();
    }
    public void resetDays()
    {
        // data
        numOfDaysSelected = 0;

        // graphics
        for(int i = 0; i < Day_Buttons.size();i++)
        {
            Day_Buttons.get(i).setBackground(unselected);
            Day_Buttons.get(i).setTextColor(unselectedColorBlackID);
        }
    }
    public void resetTimes()
    {
        // data
        numOfTimesCreated = 0;
        dailyTimes.clear();
        dailyTimesListAdapter.setDailyTimes(dailyTimes);

        //dailyTimeViewModel.deleteAll();

        //graphics
        noTimesSetTextview.setVisibility(View.VISIBLE);
        timesRecyclerView.setVisibility(View.GONE);
    }

    public void pickTimes(View view)
    {
        DialogFragment dialogFragment = new TimePickerFragment(this);
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickIntervals(final View view)
    {
        Log.d(LOG, "got in here");
        AlertDialog.Builder intervalDialogBuilder = new AlertDialog.Builder(this);
        intervalDialogBuilder.setTitle("Pick a reminder interval");
        // index                    0                        1                 2
        String [] choices = {"One hour intervals", "Three hour intervals", "Six hour intervals"};
        intervalDialogBuilder.setItems(choices, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d(LOG, "Interval onClicked with: " + which);
                switch(which)
                {
                    case 0:
                        Log.d(LOG, "one hr interval pressed");
                        addDailyTime(Utility.IntervalType.ONE_HR, false, -1, -1);
                        break;
                    case 1:
                        Log.d(LOG, "three hr interval pressed");
                        addDailyTime(Utility.IntervalType.THREE_HR, false, -1, -1);
                        break;
                    case 2:
                        Log.d(LOG, "six hr interval pressed");
                        addDailyTime(Utility.IntervalType.SIX_HR, false, -1, -1);
                        break;
                    default:
                        break;
                }
            }
        });
        Dialog intervalDialog = intervalDialogBuilder.create();
        intervalDialog.show();
    }
    public void addDailyTime(Utility.IntervalType intervalType, boolean isTime, int hourOfDay, int minute)
    {
        // TODO DO NOT ALLOW DUPLICATES TO BE ADDED
        // TODO ALSO THE TIMES LIST NEEDS TO BE SORTED AND REDISPLAYED AFTER A NEW TIME IS ADDED

        // TODO A NEW DAILY TIME

        //Log.d(LOG, "onTimeSet reached");

        noTimesSetTextview.setVisibility(View.GONE);
        timesRecyclerView.setVisibility(View.VISIBLE);

        DailyTime dailyTime = new DailyTime(intervalType, isTime ,hourOfDay, minute, true, false, false);
        //Log.d(LOG, dailyTime.toString());

        boolean canAddDaily = true;

        if(dailyTimes.size() > 0)
        {
            if(isTime) // YOU'RE TRYING TO ADD A TIME TYPE
            {
                for(int i = 0; i < dailyTimes.size(); i++)
                {
                    if(dailyTimes.get(i).getIsTime())
                    {
                        // EXISTING TIME FOUND
                        if(dailyTimes.get(i).getMinute() == dailyTime.getMinute()
                                &&
                            dailyTimes.get(i).getHour() == dailyTime.getHour())
                        {
                            Utility.Toast(this, "This time already exists");
                            canAddDaily = false;
                            break;
                        }
                    }
                    else // you're comparing against an existing interval
                    {
                        switch(dailyTimes.get(i).getIntervalType())
                        {
                            case ONE_HR:
                                if(dailyTime.getMinute() == 0)
                                {
                                    Utility.Toast(this, "This time already exists in an interval you previously created");
                                    canAddDaily = false;
                                }
                                break;
                            case THREE_HR:
                                // 00  01  02  03  04  05  06  07  08  09  10  11  AM
                                // 12  13  14  15  16  17  18  19  20  21  22  23  PM

                                // 0 1 2 3 4 5 6
                                // M T W T F S S
                                if(dailyTime.getMinute() == 0
                                &&
                                (
                                    dailyTime.getHour() == 12 ||
                                    dailyTime.getHour() == 15 ||
                                    dailyTime.getHour() == 18 ||
                                    dailyTime.getHour() == 21 ||
                                    dailyTime.getHour() == 0 ||
                                    dailyTime.getHour() == 3 ||
                                    dailyTime.getHour() == 6 ||
                                    dailyTime.getHour() == 9
                                ))
                                Utility.Toast(this, "This time already exists in an interval you previously created");
                                canAddDaily = false;
                                break;
                            case SIX_HR:
                                if(dailyTime.getMinute() == 0
                                &&
                                (
                                        dailyTime.getHour() == 12 ||
                                        dailyTime.getHour() == 18 ||
                                        dailyTime.getHour() == 0 ||
                                        dailyTime.getHour() == 6
                                ))
                                Utility.Toast(this, "This time already exists in an interval you previously created");
                                canAddDaily = false;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            else // YOU'RE TRYING TO ADD AN INTERVAL TYPE
            {
                for(int i = 0; i < dailyTimes.size(); i++)
                {
                    if(!dailyTimes.get(i).getIsTime())
                    {
                        Utility.Toast(this, "Only one interval can be added per daily");
                        canAddDaily = false;
                        break;
                    }
                    else // YOU'RE LOOKING AT A TIME THAT MAY EXIST IN THIS INTERVAL YOU'RE TRYING TO ADD
                    {
                        // TODO - CLEAN UP ANY TIMES THAT MATCH THIS NEW INTERVAL BEING ADDED
                        List<DailyTime> dailyTimesToRemove = new ArrayList<>();
                        switch(dailyTime.getIntervalType())
                        {
                            case ONE_HR:
                                if(dailyTimes.get(i).getMinute() == 0)
                                {
                                    dailyTimesToRemove.add(dailyTimes.get(i));
                                }
                                break;
                            case THREE_HR:
                                if(dailyTimes.get(i).getMinute() == 0 &&
                                (dailyTimes.get(i).getHour() == 12 ||
                                dailyTimes.get(i).getHour() == 15 ||
                                dailyTimes.get(i).getHour() == 18 ||
                                dailyTimes.get(i).getHour() == 21 ||
                                dailyTimes.get(i).getHour() == 0 ||
                                dailyTimes.get(i).getHour() == 3 ||
                                dailyTimes.get(i).getHour() == 6 ||
                                dailyTimes.get(i).getHour() == 9))
                                {
                                    dailyTimesToRemove.add(dailyTimes.get(i));
                                }
                                break;
                            case SIX_HR:
                                if(dailyTimes.get(i).getMinute() == 0  &&
                                (dailyTimes.get(i).getHour() == 12 ||
                                dailyTimes.get(i).getHour() == 18 ||
                                dailyTimes.get(i).getHour() == 0 ||
                                dailyTimes.get(i).getHour() == 6))
                                {
                                    dailyTimesToRemove.add(dailyTimes.get(i));
                                }
                                break;
                            default:
                                break;
                        }
                        if(dailyTimesToRemove.size() > 0)
                        {
                            for(int j = 0; j < dailyTimesToRemove.size(); j++)
                            {
                                dailyTimes.remove(dailyTimesToRemove.get(j));
                            }
                            Utility.Toast(this, "Note: Adding this interval overwrote previously added times");
                        }
                    }
                }
            }
        }
        if(canAddDaily)
        {
            if(!dailyTime.getIsTime())
            {
                dailyTimes.add(0, dailyTime);
            }
            else
            {
                dailyTimes.add(dailyTime);
            }
            Log.d(LOG, "onTimeSet - Just added a new dailyTime, dailyTimes size is:" + dailyTimes.size());

            numOfTimesCreated++;

            Collections.sort(dailyTimes);

            dailyTimesListAdapter.notifyDataSetChanged();

            Log.d(LOG, "onTimeSet - dailyTimes count: " + dailyTimes.size());
            Log.d(LOG, "onTimeSet - numOfTimesCreated: " + numOfTimesCreated);
        }
    }
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {
        private String LOG = "------> TimePickerFragment";
        private Context con;

        public TimePickerFragment(Context con)
        {
            this.con = con;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            ((DailyActivity)con).addDailyTime(null, true, hourOfDay, minute);
        }
    }
}