package com.fourdevs.diuquestionbnakadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.UsersAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityUsersBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.UsersListener;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class UsersActivity extends BaseActivity implements UsersListener {
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUserData();
        setListeners();
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUserData() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            List<User> users = new ArrayList<>();
            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                User user = new User();
                user.userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                if(Boolean.TRUE.equals(queryDocumentSnapshot.getBoolean(Constants.KEY_IS_VERIFIED))){
                    user.availability = Objects.requireNonNull(queryDocumentSnapshot.getLong(Constants.KEY_AVAILABILITY)).intValue();
                }else{
                    user.availability = 2;
                }
                user.admin = queryDocumentSnapshot.getBoolean(Constants.KEY_IS_ADMIN);
                user.isVerified = queryDocumentSnapshot.getBoolean(Constants.KEY_IS_VERIFIED);
                user.fcmToken = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                user.uploadCount = queryDocumentSnapshot.getString(Constants.KEY_UPLOAD_COUNT);
                user.approveCount = queryDocumentSnapshot.getString(Constants.KEY_APPROVE_COUNT);
                user.rejectCount = queryDocumentSnapshot.getString(Constants.KEY_REJECT_COUNT);
                user.profilePicture = queryDocumentSnapshot.getString(Constants.KEY_PROFILE_PICTURE);

                if(user.email.matches(preferenceManager.getString(Constants.KEY_EMAIL))){
                    continue;
                }
                users.add(user);
            }
            users.sort(Comparator.comparing(obj -> obj.name));

            if (users.size() > 0) {
                UsersAdapter usersAdapter = new UsersAdapter(users,this);
                binding.usersRecyclerView.setAdapter(usersAdapter);
                binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
                loading(false);
            }
        });

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ManageUserActivity.class);
        intent.putExtra(Constants.KEY_USER_ID, user.userId);
        intent.putExtra(Constants.KEY_NAME, user.name);
        intent.putExtra(Constants.KEY_EMAIL, user.email);
        intent.putExtra(Constants.KEY_UPLOAD_COUNT, user.uploadCount);
        intent.putExtra(Constants.KEY_REJECT_COUNT, user.rejectCount);
        intent.putExtra(Constants.KEY_APPROVE_COUNT, user.approveCount);
        intent.putExtra(Constants.KEY_PROFILE_PICTURE, user.profilePicture);
        intent.putExtra(Constants.KEY_AVAILABILITY, user.availability);
        intent.putExtra(Constants.KEY_IS_ADMIN, user.admin);
        intent.putExtra(Constants.KEY_IS_VERIFIED, user.isVerified);
        startActivity(intent);
    }
}