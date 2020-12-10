package com.example.dailies;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.List;

@Entity(tableName="daily_table")
@TypeConverters({Converters.class})
public class Daily implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name ="ID")
    int ID;

    @NonNull
    @ColumnInfo(name="title")
    String title;

    @ColumnInfo(name="description")
    @NonNull
    String description;

    @ColumnInfo(name="days")
    List<Integer> remindOnDays;

    @ColumnInfo(name="times")
    List<DailyTime> remindAtTimes;

    @ColumnInfo(name = "channel_id")
    int channelID; // todo this should later be called notification ID

    boolean isCheckBoxVisible = false;
    boolean isCheckBoxChecked = false;

    public Daily(String title, String description, List<Integer> remindOnDays, List<DailyTime> remindAtTimes, int channelID)
    {
        this.title = title;
        this.description = description;
        this.remindOnDays = remindOnDays;
        this.remindAtTimes = remindAtTimes;
        this.channelID = channelID;
    }
    @NonNull
    public String getTitle()
    {
        return title;
    }
    public List<Integer> getRemindOnDays()
    {
        return remindOnDays;
    }
    public List<DailyTime> getRemindAtTimes()
    {
        return remindAtTimes;
    }
    public String getDescription()
    {
        return description;
    }
    @NonNull
    public int getID()
    {
        return ID;
    }
    @NonNull
    public Daily getDaily()
    {
        return this;
    }
    @NonNull
    @Override
    public String toString()
    {
        // todo remember to change channel id naming to notification id
        String daily =
                "ID: " + ID +
                "\nChannelID: " + channelID +
                "\nTitle: " + title +
                "\nDescription: " + description;

        if(remindOnDays != null)
        {
            daily += "\nRemindOnDays: ";
            for(int i = 0; i < remindOnDays.size(); i++)
            {
                switch(remindOnDays.get(i))
                {
                    case 0:
                        daily += "\nMonday";
                        break;
                    case 1:
                        daily += "\nTuesday";
                        break;
                    case 2:
                        daily += "\nWednesday";
                        break;
                    case 3:
                        daily += "\nThursday";
                        break;
                    case 4:
                        daily += "\nFriday";
                        break;
                    case 5:
                        daily += "\nSaturday";
                        break;
                    case 6:
                        daily += "\nSunday";
                        break;
                }
            }
        }
        if(remindAtTimes != null)
        {
            daily += "\nRemindAtTimes:";
            for(int i = 0; i < remindAtTimes.size(); i++)
            {
                daily += "\n" + remindAtTimes.get(i).toString();

            }
        }
        return daily;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public void setRemindOnDays(List<Integer> remindOnDays)
    {
        this.remindOnDays = remindOnDays;
    }
    public void setRemindAtTimes(List<DailyTime> remindAtTimes)
    {
        this.remindAtTimes = remindAtTimes;
    }
    public void setCheckBoxVisible(boolean visible)
    {
        this.isCheckBoxVisible = visible;
    }
    public boolean isCheckBoxVisible()
    {
        return this.isCheckBoxVisible;
    }
    public void setCheckBoxChecked(boolean checked)
    {
        this.isCheckBoxChecked = checked;
    }
    public boolean isCheckBoxChecked()
    {
        return this.isCheckBoxChecked;
    }
    public int getChannelID()
    {
        return this.channelID;
    }
}