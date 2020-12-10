package com.example.dailies;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DailyRepository
{
    private DailyDao dailyDao;
    private LiveData<List<Daily>> allDailies;

    DailyRepository(Application application)
    {
        DailyRoomDatabase dailyRoomDatabase = DailyRoomDatabase.getDatabase(application);
        dailyDao = dailyRoomDatabase.dailyDao();
        allDailies = dailyDao.getAllDailies();
    }
    LiveData<List<Daily>> getAllDailies()
    {
        return allDailies;
    }
    public void update(Daily daily)
    {
        new updateAsyncTask(dailyDao).execute(daily);
    }
    public void insert(Daily daily)
    {
        new insertAsyncTask(dailyDao).execute(daily);
    }
    public void deleteAll()
    {
        new deleteAllDailiesAsyncTask(dailyDao).execute();
    }
    public void deleteDaily(Daily daily)
    {
        new deleteDailyAsyncTask(dailyDao).execute(daily);
    }
    private static class deleteAllDailiesAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private DailyDao asyncTaskDao;

        deleteAllDailiesAsyncTask(DailyDao dao)
        {
            asyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            asyncTaskDao.deleteAll();
            return null;
        }
    }
    private static class insertAsyncTask extends AsyncTask<Daily, Void, Void>
    {
        private DailyDao asyncTaskDao;

        // Constructor
        insertAsyncTask(DailyDao dao)
        {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Daily... params)
        {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<Daily, Void, Void>
    {
        private DailyDao asyncTaskDao;

        // Constructor
        updateAsyncTask(DailyDao dao)
        {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Daily... params)
        {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }
    private static class deleteDailyAsyncTask extends AsyncTask<Daily, Void, Void>
    {
        private DailyDao asyncTaskDao;
        deleteDailyAsyncTask(DailyDao dailyDao)
        {
            asyncTaskDao = dailyDao;
        }
        @Override
        protected Void doInBackground(final Daily... params)
        {
            asyncTaskDao.deleteDaily(params[0]);
            return null;
        }
    }
}
