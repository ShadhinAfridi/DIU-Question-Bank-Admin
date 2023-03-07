package com.fourdevs.diuquestionbnakadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.CourseDiff;
import com.fourdevs.diuquestionbnakadmin.adapter.QuestionApproveAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityApproveQuestionBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.viewModel.CourseViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ApproveQuestionActivity extends BaseActivity implements CourseListener {
    private ActivityApproveQuestionBinding binding;
    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApproveQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        getCourses();
        setListeners();
        setAdapter();
    }

    private void setAdapter() {
        loading(true);
        QuestionApproveAdapter questionApproveAdapter = new QuestionApproveAdapter(new CourseDiff(), this, getApplication(), this);
        binding.courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.courseRecyclerView.setAdapter(questionApproveAdapter);
        courseViewModel.getCoursesForApprove().observe(this, it-> {
            if(it.size()>0) {
                loading(false);
                binding.linearLayout.setVisibility(View.GONE);
            } else {
                binding.linearLayout.setVisibility(View.VISIBLE);
                loading(false);
            }
            questionApproveAdapter.submitList(it);
            binding.titleCount.setText(String.valueOf(it.size()));
        });
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    private void getCourses() {
        courseViewModel.networkUserCourseApprove();
    }


    @Override
    public void onCourseClicked(Course course) {
        Intent intent = new Intent(getApplicationContext(), ApproveViewActivity.class);
        intent.putExtra(Constants.KEY_NAME, course);
        startActivity(intent);
        finish();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}