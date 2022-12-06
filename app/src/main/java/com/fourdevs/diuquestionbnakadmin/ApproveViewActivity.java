package com.fourdevs.diuquestionbnakadmin;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fourdevs.diuquestionbnakadmin.adapter.PdfAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityPdfViewerBinding;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.viewModel.CourseViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApproveViewActivity extends AppCompatActivity {

    private ActivityPdfViewerBinding binding;
    private Boolean approved;
    private String userId, questionId, rejectCount, approveCount;
    private DocumentReference userReference, questionReference;
    private CourseViewModel courseViewModel;
    private String courseLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        loading(true);
        getIntentExtra();
        checkApproveInfo();
        downloadActivity();
        getUploadCount();
        setListener();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        courseLink = intent.getStringExtra(Constants.KEY_PDF_URL);
        userId = intent.getStringExtra(Constants.KEY_USER_ID);
        questionId = intent.getStringExtra(Constants.KEY_QUESTION_ID);
        approved = intent.getBooleanExtra(Constants.KEY_IS_APPROVED, false);
        binding.textCourseName.setText(intent.getStringExtra(Constants.KEY_NAME));
    }

    private void checkApproveInfo() {
        if(!approved) {
            binding.constraintLayout2.setVisibility(View.VISIBLE);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            questionReference = database.collection(Constants.KEY_COLLECTION_QUESTIONS).document(questionId);
            userReference = database
                    .collection(Constants.KEY_COLLECTION_USERS).document(userId);
        }
    }

    private void setListener() {
        binding.iconBack.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
        binding.buttonApprove.setOnClickListener(view -> alertDialogApprove());
        binding.buttonReject.setOnClickListener(view -> alertDialogReject());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadActivity(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(courseLink);
        File rootPath = new File(getBaseContext().getCacheDir().getPath()+"/Download");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,courseLink);
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

    private void getUploadCount() {
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            approveCount = documentSnapshot.getString(Constants.KEY_APPROVE_COUNT);
            rejectCount = documentSnapshot.getString(Constants.KEY_REJECT_COUNT);
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
                .addOnCompleteListener(task -> {
                    deletePdfData();
                    updateData(Constants.KEY_REJECT_COUNT, rejectCount);
                    makeToast("Question rejected");
                })
                .addOnFailureListener(e -> makeToast("Unable to reject!"));
        binding.constraintLayout2.setVisibility(View.GONE);
    }

    private void approveQuestions() {
        questionReference.update(Constants.KEY_IS_APPROVED, true)
                .addOnCompleteListener(task -> {
                    makeToast("Question approved");
                    updateData(Constants.KEY_APPROVE_COUNT, approveCount);
                })
                .addOnFailureListener(e -> makeToast("Unable to approve!"));
        binding.constraintLayout2.setVisibility(View.GONE);
    }

    private void goToApprovePage() {
        Intent intent = new Intent(ApproveViewActivity.this, ApproveQuestionActivity.class);
        startActivity(intent);
        finish();
    }

    private void deletePdfData() {
        //storageRef.delete();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void updateData(String fieldName, String count){
        int value = Integer.parseInt(count)+1;
        userReference.update(fieldName, value+"");
    }

    private void makeToast(String value) {
        Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
    }

}