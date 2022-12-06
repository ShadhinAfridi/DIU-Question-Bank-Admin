package com.fourdevs.diuquestionbnakadmin.room;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Course.class, User.class, Help.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public abstract QuestionsDao questionsDao();
    public abstract UserDao userDao();
    public abstract HelpDao helpDao();



    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), AppDatabase.class, Constants.KEY_DB)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

