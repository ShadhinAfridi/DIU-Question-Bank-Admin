package com.fourdevs.diuquestionbnakadmin;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.HelpAdapter;
import com.fourdevs.diuquestionbnakadmin.adapter.UsersAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityHelpBinding;
import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.utilities.PreferenceManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getUserData();
        setListeners();
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
    }

    private void getUserData() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_CONTACTS)
                .get()
                .addOnCompleteListener(task -> {
                    List<Help> helps = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Help help = new Help();
                        help.userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                        help.message = queryDocumentSnapshot.getString(Constants.KEY_MESSAGE);
                        help.subject = queryDocumentSnapshot.getString(Constants.KEY_SUBJECT);
                        help.dateTime = getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP));
                        help.dateObject = queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP);
                        helps.add(help);
                    }
                    helps.sort(Comparator.comparing(obj -> obj.dateObject));
                    Collections.reverse(helps);
                    if (helps.size() > 0) {
                        HelpAdapter helpAdapter = new HelpAdapter(helps);
                        binding.helpRecyclerView.setAdapter(helpAdapter);
                        binding.helpRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        binding.helpRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        binding.helpEmpty.setVisibility(View.VISIBLE);
                    }
                    loading(false);
                });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.helpProgressBar.setVisibility(View.VISIBLE);
        }else{
            binding.helpProgressBar.setVisibility(View.GONE);
        }
    }

}