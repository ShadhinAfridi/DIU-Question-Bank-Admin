package com.fourdevs.diuquestionbnakadmin.viewModel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.repository.CourseRepository;

import java.io.File;
import java.util.List;

public class CourseViewModel extends AndroidViewModel {

    private final CourseRepository repository;
    private final LiveData<List<Course>> allCourse;

    public CourseViewModel(Application application) {
        super(application);
        repository = new CourseRepository(application);
        allCourse = repository.getAllCourses();

    }

    public void networkUserCourseApprove() {
        repository.networkUserCourseApprove();
    }

    public void update(Course course) {
        repository.update(course);
    }

    public void networkCourseInfo(String course) {repository.networkCourseInfo(course);}

    public LiveData<List<Course>> getAllCourse() {
        return allCourse;
    }

    public LiveData<List<Course>> getCourse(String department) {
        return repository.getCourses(department);
    }

    public LiveData<List<Course>> getSearchedCourse(String department, String courseCode) {
        return repository.getSearchedCourses(department, courseCode);
    }

    public LiveData<List<Course>> getCoursesForApprove() {
        return repository.getCoursesForApprove();
    }

    public void networkCourse() {
        repository.networkCourse();
    }

    public LiveData<List<Bitmap>> getListForDisplayPdf(File file) {
        return repository.getListForDisplayPdf(file);
    }

    public LiveData<List<Course>> getDuplicateCourses(String department, String courseLink) {
        return repository.getDuplicateCourses(department, courseLink);
    }

    public void deleteQuestion(String questionId) {
        repository.deleteQuestion(questionId);
    }

}
