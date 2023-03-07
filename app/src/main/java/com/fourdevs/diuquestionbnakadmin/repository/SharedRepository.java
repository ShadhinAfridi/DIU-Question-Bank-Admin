package com.fourdevs.diuquestionbnakadmin.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.room.AppDatabase;
import com.fourdevs.diuquestionbnakadmin.room.HelpDao;
import com.fourdevs.diuquestionbnakadmin.room.UserDao;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class SharedRepository {

    private final UserDao userDao;
    private final HelpDao helpDao;
    private final FirebaseFirestore firebaseDatabase;

    public SharedRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        userDao = database.userDao();
        helpDao = database.helpDao();
        firebaseDatabase = FirebaseFirestore.getInstance();
    }

    public LiveData<User> getUserData(String userId) {
        return userDao.getUserInfo(userId);
    }

    public LiveData<List<User>> getAllUserData() {
        return userDao.getAllUserInfo();
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.Insert(user));
    }

    public void insertHelp(Help help) {
        AppDatabase.databaseWriteExecutor.execute(() -> helpDao.Insert(help));
    }

    public LiveData<List<Help>> getHelpData() {
        return helpDao.getAllHelpsInfo();
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.Update(user));
    }

    public void getOnlineUserData() {
        firebaseDatabase.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot snapshot : task.getResult()) {
                            addUserDataToRoom(snapshot);
                        }
                    }

                });
    }

    public void getOnlineUserData(String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            addUserDataToRoom(queryDocumentSnapshot);
                        }
                    }
                });
    }

    private void addUserDataToRoom(QueryDocumentSnapshot snapshot) {
        User user = new User();
        user.email = snapshot.getString(Constants.KEY_EMAIL);
        user.fcmToken = snapshot.getString(Constants.KEY_FCM_TOKEN);
        user.userName = snapshot.getString(Constants.KEY_NAME);
        user.profilePicture = snapshot.getString(Constants.KEY_PROFILE_PICTURE);
        user.uploadCount = snapshot.getString(Constants.KEY_UPLOAD_COUNT);
        user.approveCount = snapshot.getString(Constants.KEY_APPROVE_COUNT);
        user.rejectCount = snapshot.getString(Constants.KEY_REJECT_COUNT);
        user.admin = snapshot.getBoolean(Constants.KEY_IS_ADMIN);
        user.isVerified = snapshot.getBoolean(Constants.KEY_IS_VERIFIED);
        user.userId = Objects.requireNonNull(snapshot.getString(Constants.KEY_USER_ID));
        insert(user);
        updateUser(user);
    }

    public void getHelpsDataFromNetwork() {
        firebaseDatabase.collection(Constants.KEY_COLLECTION_CONTACTS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Help help = new Help();
                            help.id = queryDocumentSnapshot.getId();
                            help.userId = Objects.requireNonNull(queryDocumentSnapshot.getString(Constants.KEY_USER_ID));
                            help.message = queryDocumentSnapshot.getString(Constants.KEY_MESSAGE);
                            help.subject = queryDocumentSnapshot.getString(Constants.KEY_SUBJECT);
                            help.dateTime = getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP));
                            insertHelp(help);
                        }

                    }
                });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }
}
