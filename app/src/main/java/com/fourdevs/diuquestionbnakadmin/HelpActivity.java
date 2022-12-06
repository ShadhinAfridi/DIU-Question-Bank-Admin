package com.fourdevs.diuquestionbnakadmin;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.HelpAdapter;
import com.fourdevs.diuquestionbnakadmin.adapter.UsersAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityHelpBinding;
import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.utilities.PreferenceManager;
import com.fourdevs.diuquestionbnakadmin.viewModel.SharedViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HelpActivity extends BaseActivity {
    private ActivityHelpBinding binding;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getHelpsDataFromNetwork();
        getUserData();
        setListeners();
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUserData() {
        loading(true);
        HelpAdapter helpAdapter = new HelpAdapter(new HelpAdapter.HelpDiff(), getApplication(), this);
        binding.helpRecyclerView.setAdapter(helpAdapter);
        binding.helpRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.helpRecyclerView.setVisibility(View.VISIBLE);
        binding.helpEmpty.setVisibility(View.INVISIBLE);

        sharedViewModel.getHelpData().observe(this, it->{
            helpAdapter.submitList(it);

            if (it.size() == 0) {
                binding.helpEmpty.setVisibility(View.VISIBLE);
            }
            loading(false);
        });
    }


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.helpProgressBar.setVisibility(View.VISIBLE);
        }else{
            binding.helpProgressBar.setVisibility(View.GONE);
        }
    }

}