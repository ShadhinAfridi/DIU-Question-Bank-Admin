package com.fourdevs.diuquestionbnakadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.UsersAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityUsersBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.UsersListener;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.viewModel.SharedViewModel;

public class UsersActivity extends BaseActivity implements UsersListener {
    private ActivityUsersBinding binding;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getOnlineUserData();
        setListeners();
        setAdapter();
    }

    private void setAdapter() {
        loading(true);
        UsersAdapter usersAdapter = new UsersAdapter(new UsersAdapter.UserDiff(),this);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRecyclerView.setAdapter(usersAdapter);
        sharedViewModel.getAllUserData().observe(this, it->{
            usersAdapter.submitList(it);
            if(it.size()>0) {
                loading(false);
            }
        });
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
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
        intent.putExtra(Constants.KEY_NAME, user.userName);
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