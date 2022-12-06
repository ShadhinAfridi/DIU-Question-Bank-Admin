package com.fourdevs.diuquestionbnakadmin.repository;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.room.AppDatabase;
import com.fourdevs.diuquestionbnakadmin.room.QuestionsDao;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseRepository {

    private final QuestionsDao questionsDao;
    private final LiveData<List<Course>> allCourses;
    private StorageReference pdfRef;
    private final DisplayMetrics metrics;
    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private MutableLiveData<File> fileName;


    public CourseRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        questionsDao = database.questionsDao();
        allCourses = questionsDao.getAllCourses();
        metrics = application.getApplicationContext().getResources().getDisplayMetrics();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }
    public LiveData<List<Course>> getCourses(String department) {
        return questionsDao.getCourses(department);
    }
    public LiveData<List<Course>> getCoursesForApprove() {
        return questionsDao.getCoursesForApprove();
    }
    public LiveData<List<Course>> getSearchedCourses(String department, String courseCode) {
        return questionsDao.getSearchedCourses(department, courseCode);
    }
    public LiveData<List<Course>> getUserUploads(String userId) {
        return questionsDao.getUserUploads(userId);
    }

    public void insert(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> questionsDao.Insert(course));
    }

    public void update(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> questionsDao.Update(course));
    }

    public void networkCourse() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTIONS)
                .get()
                .addOnCompleteListener(this::getDataInsert);
    }

    public void networkUserCourse(String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_QUESTIONS)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .get()
                .addOnCompleteListener(this::getDataUpdate);
    }

    private void getDataInsert(Task<QuerySnapshot> task) {
        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
            this.insert(arrangeData(queryDocumentSnapshot));
        }
    }

    private void getDataUpdate(Task<QuerySnapshot> task) {
        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
            Course course = arrangeData(queryDocumentSnapshot);
            this.insert(course);
            this.update(course);
        }
    }

    private Course arrangeData(QueryDocumentSnapshot queryDocumentSnapshot) {
        Course course = new Course();
        course.courseId = queryDocumentSnapshot.getId();
        course.departmentName = queryDocumentSnapshot.getString(Constants.KEY_DEPARTMENT);
        course.courseName = queryDocumentSnapshot.getString(Constants.KEY_COURSE_CODE);
        course.semester = queryDocumentSnapshot.getString(Constants.KEY_SEMESTER);
        course.year = queryDocumentSnapshot.getString(Constants.KEY_YEAR);
        course.fileUrl = queryDocumentSnapshot.getString(Constants.KEY_PDF_URL);
        course.exam = queryDocumentSnapshot.getString(Constants.KEY_EXAM);
        course.userId = queryDocumentSnapshot.getString(Constants.KEY_USER_ID);
        course.dateTime = getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP));
        course.approved = queryDocumentSnapshot.getBoolean(Constants.KEY_IS_APPROVED);
        return course;
    }


    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
    }



    public MutableLiveData<List<Bitmap>> getListForDisplayPdf(File file) {
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            pdfRenderer = new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfPage = pdfRenderer.getPageCount();
        List<Bitmap> list = new ArrayList<>();

        for(int i=0; i<numberOfPage; i++){
            PdfRenderer.Page rendererPage = pdfRenderer.openPage(i);
            Bitmap bitmap = Bitmap.createBitmap(
                    metrics.widthPixels,
                    metrics.heightPixels,
                    Bitmap.Config.ARGB_8888);
            rendererPage.render(bitmap, null, null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            list.add(bitmap);
            rendererPage.close();
        }
        pdfRenderer.close();
        return new MutableLiveData<>(list);
    }

}
