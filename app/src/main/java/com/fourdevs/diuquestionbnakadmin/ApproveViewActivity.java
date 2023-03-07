package com.fourdevs.diuquestionbnakadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fourdevs.diuquestionbnakadmin.adapter.CourseDiff;
import com.fourdevs.diuquestionbnakadmin.adapter.PdfAdapter;
import com.fourdevs.diuquestionbnakadmin.adapter.QuestionApproveAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityPdfViewerBinding;
import com.fourdevs.diuquestionbnakadmin.databinding.DialougeDuplicateDataBinding;
import com.fourdevs.diuquestionbnakadmin.databinding.DialougeEditPdfFileBinding;
import com.fourdevs.diuquestionbnakadmin.databinding.DialougeUploaderInfoBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.viewModel.CourseViewModel;
import com.fourdevs.diuquestionbnakadmin.viewModel.SharedViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ApproveViewActivity extends AppCompatActivity implements CourseListener {

    private ActivityPdfViewerBinding binding;
    private DocumentReference questionReference;
    private CourseViewModel courseViewModel;
    private StorageReference islandRef;
    private FirebaseFirestore database;
    private SharedViewModel sharedViewModel;
    private Course course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        binding.textCourseName.setText(getIntentExtra());
        database = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        loading(true);
        checkApproveInfo();
        downloadActivity();
        setListener();
        checkDuplicateFile();
    }

    private String getIntentExtra() {
        course = (Course) getIntent().getSerializableExtra(Constants.KEY_NAME);
        return course.courseName+"_"+course.exam+"_"+course.semester+"_"+course.year;
    }

    private void checkApproveInfo() {
        if(!course.approved) {
            binding.buttonApprove.setVisibility(View.VISIBLE);
            binding.buttonReject.setVisibility(View.VISIBLE);
        }
        questionReference = database
                .collection(Constants.KEY_COLLECTION_QUESTIONS).document(course.courseId);
    }

    private void setListener() {
        binding.iconBack.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
        binding.buttonApprove.setOnClickListener(view -> alertDialogApprove());
        binding.buttonReject.setOnClickListener(view -> alertDialogReject());
        binding.buttonEditFile.setOnClickListener(view -> editFileProjectDialog());
        binding.imageInfo.setOnClickListener(view -> uploaderInfo());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadActivity(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        islandRef = storageRef.child(course.fileUrl);
        File rootPath = new File(getBaseContext().getCacheDir().getPath()+"/Download");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,course.fileUrl);
        if(localFile.exists()){
            loading(false);
            displayPdf(localFile);
        } else {
            islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                loading(false);
                displayPdf(localFile);
            }).addOnFailureListener(exception ->{
                Log.d("file not created", exception.getMessage());
                makeToast("Cannot open this file");
            } );
        }
    }

    private void displayPdf(File file){
        courseViewModel.getListForDisplayPdf(file).observe(this, it->{
            PdfAdapter pdfAdapter = new PdfAdapter(it);
            binding.pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.pdfRecyclerView.setAdapter(pdfAdapter);
            loading(false);
        });
    }


    private void alertDialogApprove() {
        AlertDialog.Builder builderApprove= new AlertDialog.Builder(this);
        builderApprove.setTitle("Approve");
        builderApprove.setMessage("Are you sure, you want to approve?");
        builderApprove.setCancelable(true);

        builderApprove.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    approveQuestions();
                    dialog.cancel();
                    goToApprovePage();
                });

        builderApprove.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builderApprove.create();
        alert.show();
    }

    private void alertDialogReject() {
        AlertDialog.Builder builderReject = new AlertDialog.Builder(this);
        builderReject.setTitle("Reject");
        builderReject.setMessage("Are you sure, you want to reject?");
        builderReject.setCancelable(true);

        builderReject.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    rejectQuestion();
                    dialog.cancel();
                    goToApprovePage();
                });

        builderReject.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builderReject.create();
        alert11.show();
    }

    private void rejectQuestion() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_IS_APPROVED, FieldValue.delete());
        updates.put(Constants.KEY_IS_REJECTED, true);
        questionReference.update(updates)
                .addOnCompleteListener(task -> makeToast("Question rejected"))
                .addOnFailureListener(e -> makeToast("Unable to reject!"));
    }

    private void rejectOnlyQuestion() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_IS_APPROVED, FieldValue.delete());
        updates.put(Constants.KEY_IS_REJECTED, true);
        questionReference.update(updates)
                .addOnCompleteListener(task -> makeToast("Question rejected"))
                .addOnFailureListener(e -> makeToast("Unable to reject!"));
    }

    private void approveQuestions() {
        questionReference.update(Constants.KEY_IS_APPROVED, true)
                .addOnCompleteListener(task -> makeToast("Question approved"))
                .addOnFailureListener(e -> makeToast("Unable to approve!"));
    }

    private void goToApprovePage() {
        courseViewModel.deleteQuestion(course.courseId);
        Intent intent = new Intent(ApproveViewActivity.this, ApproveQuestionActivity.class);
        startActivity(intent);
        finish();
    }

    private void deletePdfFile() {
        islandRef.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                makeToast("Pdf File deleted!");
            }
        });
    }
    private void deletePdfInformation() {
        questionReference.delete().addOnCompleteListener(task -> makeToast("Data deleted!"));
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }


    private void makeToast(String value) {
        Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    private void editFileProjectDialog() {
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        DialougeEditPdfFileBinding viewBinding = DialougeEditPdfFileBinding.inflate(getLayoutInflater());
        builder.setView(viewBinding.getRoot());
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();

        viewBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.buttonRenameCourseCode) {
                viewBinding.courseCodeLayout.setVisibility(View.VISIBLE);
                viewBinding.courseCode.setText(course.courseName);
            } else {
                viewBinding.courseCodeLayout.setVisibility(View.GONE);
            }

        });


        viewBinding.buttonOk.setOnClickListener(view->{
            String confirmText = Objects.requireNonNull(viewBinding.confirm.getText()).toString();
            int checkedId = viewBinding.radioGroup.getCheckedRadioButtonId();
            if(confirmText.isEmpty()) {
                makeToast(" Confirm!");
            } else {
                if(confirmText.toLowerCase(Locale.ROOT).matches("confirm")) {
                    switch (checkedId) {
                        case R.id.buttonDeleteDocument:
                            deletePdfInformation();
                            alert.dismiss();
                            goToCourseActivity();
                            break;

                        case R.id.buttonDeleteAll:
                            deletePdfInformation();
                            goToCourseActivity();
                            alert.dismiss();
                            break;

                        case R.id.buttonRejectFile:
                            rejectOnlyQuestion();
                            alert.dismiss();
                            goToCourseActivity();
                            break;

                        case R.id.buttonRenameCourseCode:
                            String courseCode = Objects.requireNonNull(viewBinding.courseCode.getText()).toString().trim();
                            updateCourseCode(courseCode);
                            alert.dismiss();
                            break;

                        default:
                            makeToast("Select a button.");
                            break;
                    }
                }
            }
        });

    }

    private void checkDuplicateFile() {
        List<Course> courses = new ArrayList<>();
        courseViewModel
                .getDuplicateCourses(course.departmentName, course.courseName + "_" + course.exam + "_" + course.semester + "_" + course.year)
                .observe(this, it -> {
                    int count = 0;
                    for (Course course : it) {
                        if (course.courseId.equals(ApproveViewActivity.this.course.courseId)) {
                            continue;
                        }
                        count += 1;
                        courses.add(course);
                    }

                    if (count > 0) {
                        binding.buttonCopy.setVisibility(View.VISIBLE);
                        binding.buttonCopy.setOnClickListener(view -> showDuplicateFileList(courses));
                    }
                });
    }

    private void goToCourseActivity() {
        Intent intent = new Intent(ApproveViewActivity.this, DepartmentActivity.class);
        startActivity(intent);
        finish();

    }

    private void showDuplicateFileList(List<Course> courses) {
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        DialougeDuplicateDataBinding viewBinding = DialougeDuplicateDataBinding.inflate(getLayoutInflater());
        builder.setView(viewBinding.getRoot());
        builder.setCancelable(true);
        AlertDialog alert = builder.create();

        QuestionApproveAdapter questionApproveAdapter = new QuestionApproveAdapter(new CourseDiff(), this, getApplication(), this);
        viewBinding.courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.courseRecyclerView.setAdapter(questionApproveAdapter);
        questionApproveAdapter.submitList(courses);

        builder.setPositiveButton("Ok",
                (dialog, id) -> alert.dismiss()).show();
    }

    private void uploaderInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialougeUploaderInfoBinding viewBinding = DialougeUploaderInfoBinding.inflate(getLayoutInflater());
        builder.setView(viewBinding.getRoot());
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        builder.setNegativeButton("Ok", (dialog, which) -> alert.dismiss()).show();

        sharedViewModel.getUserData(course.userId).observe(this, it->{
            if(it!=null) {
                viewBinding.uploaderName.setText(it.userName);
                viewBinding.uploadDate.setText(it.email);

            } else {
                sharedViewModel.getOnlineUserData(course.userId);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateCourseCode(String courseCode) {
        if(!courseCode.isEmpty() && !courseCode.equals(course.courseName)) {
            questionReference.update(Constants.KEY_COURSE_CODE, courseCode).addOnSuccessListener(unused -> {
                makeToast("Course code updated.");
                course.courseName = courseCode;
                courseViewModel.update(course);
                binding.textCourseName.setText(course.courseName+"_"+course.exam+"_"+course.semester+"_"+course.year);
            });
        } else {
            makeToast("Nothing changed.");
        }
    }

    private void updateRejectReason(String rejectReason) {
        questionReference.update(Constants.KEY_REJECT_REASON, rejectReason);
    }

    @Override
    public void onCourseClicked(Course course) {
        Intent intent = new Intent(getApplicationContext(), ApproveViewActivity.class);
        intent.putExtra(Constants.KEY_NAME, course);
        startActivity(intent);
    }
}