package com.fourdevs.diuquestionbnakadmin;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fourdevs.diuquestionbnakadmin.adapter.PdfAdapter;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityPdfViewerBinding;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
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
    private String courseLink;
    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private Boolean approved;
    private String userId, questionId, rejectCount, approveCount;
    private FirebaseFirestore database;
    private DocumentReference userReference, questionReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
            database = FirebaseFirestore.getInstance();
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

    private void downloadActivity(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        StorageReference pdfRef = storageRef.child(courseLink);
        File rootPath = new File(getBaseContext().getCacheDir().getPath()+"/Download");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        final File localFile = new File(rootPath,courseLink);
        if(localFile.exists()){
            loading(false);
            displayPdf(localFile);
        } else {
            pdfRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                loading(false);
                displayPdf(localFile);
            }).addOnFailureListener(exception -> makeToast("file not created" + exception));
        }
    }

    private void getUploadCount() {
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            approveCount = documentSnapshot.getString(Constants.KEY_APPROVE_COUNT);
            rejectCount = documentSnapshot.getString(Constants.KEY_REJECT_COUNT);
        });
    }


    private void displayPdf(File file) {
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            makeToast(e.getMessage());
        }

        try {
            pdfRenderer = new PdfRenderer(fileDescriptor);

        } catch (IOException e) {
            makeToast(e.getMessage());
        }

        int numberOfPage = pdfRenderer.getPageCount();
        List<Bitmap> list = new ArrayList<>();

        for(int i=0; i<numberOfPage; i++){
            PdfRenderer.Page rendererPage = pdfRenderer.openPage(i);
            int rendererPageWidth = rendererPage.getWidth()*2;
            int rendererPageHeight = rendererPage.getHeight()*2;
            Bitmap bitmap = Bitmap.createBitmap(
                    rendererPageWidth,
                    rendererPageHeight,
                    Bitmap.Config.ARGB_8888);
            rendererPage.render(bitmap, null, null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            list.add(bitmap);
            rendererPage.close();
        }
        if(list.size() > 0){
            PdfAdapter pdfAdapter = new PdfAdapter(list);
            binding.pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.pdfRecyclerView.setAdapter(pdfAdapter);
        }
        pdfRenderer.close();
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
        storageRef.delete();
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