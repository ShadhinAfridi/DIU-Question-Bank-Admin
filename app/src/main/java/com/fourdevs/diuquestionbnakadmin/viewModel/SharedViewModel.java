package com.fourdevs.diuquestionbnakadmin.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.repository.SharedRepository;

import java.util.List;

public class SharedViewModel extends AndroidViewModel {
    private final SharedRepository sharedRepository;

    public SharedViewModel(Application application) {
        super(application);
        sharedRepository = new SharedRepository(application);
    }

    public void getOnlineUserData() {
        sharedRepository.getOnlineUserData();
    }

    public void getOnlineUserData(String userId) {
        sharedRepository.getOnlineUserData(userId);
    }

    public LiveData<User> getUserData(String userId) {
        return sharedRepository.getUserData(userId);
    }

    public void getHelpsDataFromNetwork() {
        sharedRepository.getHelpsDataFromNetwork();
    }

    public LiveData<List<Help>> getHelpData() {
        return sharedRepository.getHelpData();
    }

    public LiveData<List<User>> getAllUserData() {
        return sharedRepository.getAllUserData();
    }
}
