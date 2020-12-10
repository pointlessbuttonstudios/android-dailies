package com.example.dailies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DailyViewModel extends AndroidViewModel
{
    private DailyRepository dailyRepository;
    private LiveData<List<Daily>> allDailies;

    public DailyViewModel(@NonNull Application application)
    {
        super(application);
        dailyRepository = new DailyRepository(application);
        allDailies = dailyRepository.getAllDailies();
    }
    LiveData<List<Daily>> getAllDailies()
    {
        return allDailies;
    }
    public void insert(Daily daily)
    {
        dailyRepository.insert(daily);
    }
    public void deleteAll()
    {
        dailyRepository.deleteAll();
    }
    public void deleteDaily(Daily daily)
    {
        dailyRepository.deleteDaily(daily);
    }
    public void update(Daily daily)
    {
        dailyRepository.update(daily);
    }
}
