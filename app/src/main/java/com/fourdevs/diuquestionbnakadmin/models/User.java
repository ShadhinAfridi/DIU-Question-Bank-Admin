package com.fourdevs.diuquestionbnakadmin.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import java.io.Serializable;

@Entity(tableName = Constants.KEY_COLLECTION_USERS)
public class User implements Serializable {
    public String email;
    public String fcmToken;
    public String userName;
    public String profilePicture;
    public String uploadCount;
    public String approveCount;
    public String rejectCount;
    public Integer availability;
    public Boolean admin;
    public Boolean isVerified;

    @PrimaryKey
    @NonNull
    public String userId = null;

    public User() {

    }

    public User getUser() {
        User user = new User();
        user.email = this.email;
        user.fcmToken = this.fcmToken;
        user.userName = this.userName;
        user.profilePicture = this.profilePicture;
        user.uploadCount = this.uploadCount;
        user.approveCount = this.approveCount;
        user.rejectCount = this.rejectCount;
        user.availability = this.availability;
        user.admin = this.admin;
        user.isVerified = this.isVerified;
        user.userId = this.userId;
        return user;
    }
}
