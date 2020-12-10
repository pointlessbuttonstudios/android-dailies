package com.example.dailies;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Utility
{
    private static String LOG = "------> Utility";
    public static SharedPreferences sharedPreferences;
    public static void Toast(Context con, String message)
    {
        Toast.makeText(con, message, Toast.LENGTH_LONG).show();
    }
    public enum Day
    {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }
    public enum ReceiverCommand
    {
        DEFAULT,
        DISMISS,
        ANNOUNCE,
        CANCEL
    }
    public enum IntervalType
    {
        ONE_HR,
        THREE_HR,
        SIX_HR
    }
    public static int convertFromReceiverCommandToInt(ReceiverCommand receiverCommand)
    {
        switch(receiverCommand)
        {
            case DEFAULT:
                return 0;
            case DISMISS:
                return 1;
            case ANNOUNCE:
                return 2;
        }
        return -1;
    }
    public static ReceiverCommand convertFromIntToReceiverCommand(int x)
    {
        switch(x)
        {
            case 0:
                return ReceiverCommand.DEFAULT;
            case 1:
                return ReceiverCommand.DISMISS;
            case 2:
                return ReceiverCommand.ANNOUNCE;
        }
        return null;
    }
    public static Day convertFromIntToDay(int x)
    {
        switch(x)
        {
            case 0:
                return Day.Monday;
            case 1:
                return Day.Tuesday;
            case 2:
                return Day.Wednesday;
            case 3:
                return Day.Thursday;
            case 4:
                return Day.Friday;
            case 5:
                return Day.Saturday;
            case 6:
                return Day.Sunday;
        }
        return null;
    }

    public static int getRandomInteger(int maximum, int minimum)
    {
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

    public static int createNewChannelID()
    {
        boolean canCreate = false;
        int randomChannelNumber = -1;
        while(!canCreate)
        {
            randomChannelNumber = getRandomInteger(9999, 1000);
            Log.d("UTILITY", "random channel number created:" + randomChannelNumber);

            String exists = sharedPreferences.getString(randomChannelNumber + "","");
            Log.d("UTILITY" ,exists);

            if(exists.length() > 0 == false)
            {
                // then it doesn't exist
                canCreate = true;
            }
        }
        return randomChannelNumber;
    }
    public static void saveChannel(int channelID, List<Integer> notificationIDs)
    {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

        preferencesEditor.putString(channelID + "", convertIntegerListToDelimitedString(notificationIDs));
        preferencesEditor.apply();
    }
    public static int createNewNotificationID(int channelID)
    {
        int finalResult = -1;
        // make sure the notificationID does not exist in the list of ids under the channel id
        String delimitedNotficationsIds = "";
        if(sharedPreferences != null)
        {
            delimitedNotficationsIds = sharedPreferences.getString(channelID + "", "");
            List<Integer> notificationIDs = convertDelimitedStringToIntegerList(delimitedNotficationsIds);

            boolean canAdd = false;
            int randomNotificationID = 0;
            while(!canAdd)
            {
                randomNotificationID = (int) Math.random() * 5000 + 9999;
                for(Integer ID: notificationIDs)
                {
                    if(ID == randomNotificationID)
                    {
                        break;
                    }
                }
                canAdd = true;
            }
            notificationIDs.add(randomNotificationID);
            finalResult = randomNotificationID;
        }
        return finalResult;
    }

    public static List<Integer> convertDelimitedStringToIntegerList(String value)
    {
        String [] stringList = value.split(":");
        List<Integer> numberList = new ArrayList<>();
        for(String s: stringList)
        {
            numberList.add(Integer.parseInt(s));
        }
        return numberList;
    }
    public static String convertIntegerListToDelimitedString(List<Integer> integerList)
    {
        String delimitedString = "";
        for(Integer integer : integerList)
        {
            delimitedString += integer + ":";
        }
        delimitedString = delimitedString.substring(0, delimitedString.length()-1);
        Log.d(LOG, "converted to delimitedString: " + delimitedString);
        return delimitedString;
    }

}
