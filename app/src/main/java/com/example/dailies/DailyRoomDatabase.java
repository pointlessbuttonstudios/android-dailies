package com.example.dailies;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Daily.class}, version = 6, exportSchema = false)
public abstract class DailyRoomDatabase extends RoomDatabase
{
    private static DailyRoomDatabase INSTANCE;
    private static RoomDatabase.Callback roomDatabaseCallback =
            new RoomDatabase.Callback()
            {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db)
                {
                    super.onOpen(db);
                    // TODO do we need to populate the database?
                }
            };
    public static DailyRoomDatabase getDatabase(final Context context)
    {
        if(INSTANCE == null)
        {
            synchronized (DailyRoomDatabase.class)
            {
                if(INSTANCE == null)
                {
                    // create databasee her
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DailyRoomDatabase.class, "daily_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract DailyDao dailyDao();
}
