package com.example.dailies;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters
{
    public static Gson gson = new Gson();

    @TypeConverter
    public static List<DailyTime> stringToDailyTime(String data)
    {
        if(data == null)
        {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<DailyTime>>(){}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String DailyTimeToString(List<DailyTime> someObjects)
    {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static List<Integer> stringToInteger(String data)
    {
        if(data == null)
        {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String IntegerToString(List<Integer> integerObjects)
    {
        return gson.toJson(integerObjects);
    }
}
