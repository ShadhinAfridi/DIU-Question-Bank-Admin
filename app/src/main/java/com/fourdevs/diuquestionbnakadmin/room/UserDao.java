package com.fourdevs.diuquestionbnakadmin.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void Insert(User user);

    @Update
    void Update(User user);

    @Delete
    void Delete(User user);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_USERS+" order by userName")
    LiveData<List<User>> getAllUserInfo();

    @Query("DELETE FROM "+ Constants.KEY_COLLECTION_USERS)
    void DeleteAllUsers();

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_USERS+" where userId = :userId ")
    LiveData<User> getUserInfo(String userId);

}
