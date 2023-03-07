package com.fourdevs.diuquestionbnakadmin.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;

import java.util.List;

@Dao
public interface QuestionsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void Insert(Course course);

    @Update
    void Update(Course course);

    @Delete
    void Delete(Course course);

    @Query("DELETE FROM "+ Constants.KEY_COLLECTION_QUESTIONS)
    void DeleteAllCourse();

    @Query("DELETE FROM "+ Constants.KEY_COLLECTION_QUESTIONS +" where courseId = :questionId")
    void DeleteCourse(String questionId);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS +" where departmentName = :department and approved == 1 order by courseName")
    LiveData<List<Course>> getCourses(String department);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS +" where approved == 0 order by dateTime DESC")
    LiveData<List<Course>> getCoursesForApprove();

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS+" where approved == 1 order by courseName")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS +" where userId = :userId order by dateTime")
    LiveData<List<Course>> getUserUploads(String userId);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS
            +" where departmentName = :department and approved == 1 and courseName Like '%' || :courseCode || '%' order by courseName")
    LiveData<List<Course>> getSearchedCourses(String department, String courseCode);

    @Query("SELECT * FROM "+ Constants.KEY_COLLECTION_QUESTIONS
            +" where departmentName = :department and approved == 1 and fileUrl Like '%' || :courseLink || '%' order by courseName")
    LiveData<List<Course>> getDuplicateCourses(String department, String courseLink);
}


