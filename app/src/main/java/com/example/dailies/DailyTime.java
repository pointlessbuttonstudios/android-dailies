package com.example.dailies;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DailyTime implements Serializable, Comparable<DailyTime>
{
    private String LOG = "------> DailyTime";

    private Utility.IntervalType intervalType;
    private boolean isTime;

    private int hour;
    private int minute;

    private boolean isSwitchActivated;

    private boolean isCheckBoxVisible;
    private boolean isCheckBoxChecked;

    public DailyTime(Utility.IntervalType intervalType, boolean isTime ,int hour, int minute, boolean switchActivated, boolean checkBoxVisible, boolean checkBoxChecked)
    {
        this.intervalType = intervalType;
        this.isTime = isTime;

        this.hour = hour;
        this.minute = minute;

        this.isSwitchActivated = switchActivated;
        this.isCheckBoxVisible = checkBoxVisible;
        this.isCheckBoxChecked = checkBoxChecked;

        Log.d(LOG, "DailyTime Constructor Reached");

    }
    public int getHour()
    {
        return hour;
    }
    public int getMinute()
    {
        return minute;
    }

    /*
    a negative int if this < that
    0 if this == that
    a positive int if this > that
    */
    @Override
    public int compareTo(DailyTime o)
    {
        if(this.getIsTime() && !o.getIsTime())
        {
            return 1;
        }
        else
        {
            if(this.getHour() < o.getHour())
            {
                return -1;
            }
            else if(this.getHour() == o.getHour())
            {
                if(this.getMinute() < o.getMinute())
                {
                    return -1;
                }
                else return 1;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public String toString()
    {
        Log.d(LOG, "BEFORE TRANSFORM -> " + hour + " , " + minute);

        if(isTime)
        {
            String amPm = "AM";
            String minuteString = minute + "";

            int tempHour = 0;

            if(hour >= 12)
            {
                amPm = "PM";
                if (hour > 12)
                {
                    tempHour = hour - 12;
                }
                else
                {
                    tempHour = hour;
                }
            }
            else if(hour <= 12)
            {
                amPm = "AM";
                if(hour == 0)
                {
                    tempHour = 12;
                }
                else
                {
                    tempHour = hour;
                }
            }
            String hourString = tempHour + "";

            if(hourString.length() == 1)
            {
                hourString = "0" + hourString;
            }
            if(minuteString.length() == 1)
            {
                minuteString = "0"+ minuteString;
            }
            Log.d(LOG, "AFTER TRANSFORM -> " + hourString + " : " + minuteString + " " + amPm + "\n");
            return hourString + " : " + minuteString + " " + amPm;
        }
        else
        {
            return intervalType.toString();
        }
    }
    public boolean isSwitchActivated()
    {
        return this.isSwitchActivated;
    }
    public void setSwitchActivated(boolean isChecked)
    {
        this.isSwitchActivated = isChecked;
    }
    public void setCheckBoxChecked(boolean isChecked)
    {
        this.isCheckBoxChecked = isChecked;
    }
    public boolean isCheckBoxChecked()
    {
        return this.isCheckBoxChecked;
    }
    public boolean isCheckBoxVisible()
    {
        return this.isCheckBoxVisible;
    }
    public void setCheckBoxVisible(boolean visible)
    {
        this.isCheckBoxVisible = visible;
    }
    public boolean getIsTime()
    {
        return isTime;
    }
    public Utility.IntervalType getIntervalType()
    {
        return intervalType;
    }

}