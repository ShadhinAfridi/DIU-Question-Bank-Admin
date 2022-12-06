package com.fourdevs.diuquestionbnakadmin.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import java.util.List;

@Dao
public interface HelpDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void Insert(Help help);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_CONTACTS+" order by dateTime")
    LiveData<List<Help>> getAllHelpsInfo();
}
