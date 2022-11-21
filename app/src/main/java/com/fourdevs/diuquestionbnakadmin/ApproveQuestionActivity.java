package com.fourdevs.diuquestionbnakadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fourdevs.diuquestionbnakadmin.adapter.QuestionApproveAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityApproveQuestionBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApproveQuestionActivity extends BaseActivity implements CourseListener {
    private ActivityApproveQuestionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApproveQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getCourses();
        setListeners();
    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    private void getCourses() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTIONS)
                .whereEqualTo(Constants.KEY_IS_APPROVED, false)
                .get()
                .addOnCompleteListener(task -> {
                    List<Course> courses = new ArrayList<>();

                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Course course = new Course();
                        course.id = queryDocumentSnapshot.getId();
                        course.departmentName = queryDocumentSnapshot.getString(Constants.KEY_DEPARTMENT);
                        course.courseName = queryDocumentSnapshot.getString(Constants.KEY_COURSE_CODE);
                        course.semester = queryDocumentSnapshot.getString(Constants.KEY_SEMESTER);
                        course.year = queryDocumentSnapshot.getString(Constants.KEY_YEAR);
                        course.fileUrl = queryDocumentSnapshot.getString(Constants.KEY_PDF_URL);
                        course.exam = queryDocumentSnapshot.getString(Constants.KEY_EXAM);
                        course.userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
                        course.approved = queryDocumentSnapshot.getBoolean(Constants.KEY_IS_APPROVED);
                        course.dateTime =  getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP));
                        course.dateObject = queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP);
                        courses.add(course);
                    }
                    courses.sort(Comparator.comparing(obj -> obj.dateObject));
                    Collections.reverse(courses);

                    if (courses.size() > 0) {
                        QuestionApproveAdapter questionApproveAdapter = new QuestionApproveAdapter(courses, this);
                        binding.courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        binding.courseRecyclerView.setAdapter(questionApproveAdapter);
                        binding.courseRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        binding.linearLayout.setVisibility(View.VISIBLE);
                    }
                    loading(false);
                });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void onCourseClicked(Course course) {
        Intent intent = new Intent(getApplicationContext(), ApproveViewActivity.class);
        intent.putExtra(Constants.KEY_NAME, course.courseName+" "+course.semester+" "+course.year+" "+course.exam);
        intent.putExtra(Constants.KEY_PDF_URL, course.fileUrl);
        intent.putExtra(Constants.KEY_IS_APPROVED, course.approved);
        intent.putExtra(Constants.KEY_QUESTION_ID, course.id);
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