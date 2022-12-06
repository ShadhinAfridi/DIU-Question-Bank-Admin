package com.fourdevs.diuquestionbnakadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.CourseDiff;
import com.fourdevs.diuquestionbnakadmin.adapter.QuestionApproveAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityApproveQuestionBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.viewModel.CourseViewModel;

import java.util.concurrent.atomic.AtomicInteger;

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
        QuestionApproveAdapter questionApproveAdapter = new QuestionApproveAdapter(new CourseDiff(), this);
        binding.courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.courseRecyclerView.setAdapter(questionApproveAdapter);
        courseViewModel.getCoursesForApprove().observe(this, it-> {
            if(it.size()>0) {
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
        courseViewModel.networkCourse();
    }


    @Override
    public void onCourseClicked(Course course) {
        Intent intent = new Intent(getApplicationContext(), ApproveViewActivity.class);
        intent.putExtra(Constants.KEY_NAME, course.courseName+" "+course.semester+" "+course.year+" "+course.exam);
        intent.putExtra(Constants.KEY_PDF_URL, course.fileUrl);
        intent.putExtra(Constants.KEY_IS_APPROVED, course.approved);
        intent.putExtra(Constants.KEY_QUESTION_ID, course.courseId);
        intent.putExtra(Constants.KEY_USER_ID, course.userId);
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