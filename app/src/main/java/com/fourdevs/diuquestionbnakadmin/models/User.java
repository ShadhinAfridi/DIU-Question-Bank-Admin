package com.fourdevs.diuquestionbnakadmin.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    public String email, fcmToken, name, profilePicture, userId, uploadCount, approveCount, rejectCount;
    public Integer availability;
    public Boolean admin, isVerified;
    public Bitmap imageBitmap;
}
