package com.fourdevs.diuquestionbnakadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fourdevs.diuquestionbnakadmin.databinding.ActivityManageUserBinding;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class ManageUserActivity extends AppCompatActivity {
    private ActivityManageUserBinding binding;

    private String userId, userName, userEmail, profilePicture;
    private String uploadCount, rejectCount, approveCount, fcmToken;
    private Integer availability;
    private Boolean isVerified, isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserData();
        setListeners();



    }

    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);
        userName = intent.getStringExtra(Constants.KEY_NAME);
        userEmail = intent.getStringExtra(Constants.KEY_EMAIL);
        profilePicture = intent.getStringExtra(Constants.KEY_PROFILE_PICTURE);
        uploadCount = intent.getStringExtra(Constants.KEY_UPLOAD_COUNT);
        rejectCount = intent.getStringExtra(Constants.KEY_REJECT_COUNT);
        approveCount = intent.getStringExtra(Constants.KEY_APPROVE_COUNT);
        fcmToken = intent.getStringExtra(Constants.KEY_FCM_TOKEN);
        availability = intent.getIntExtra(Constants.KEY_AVAILABILITY, 0);
        isAdmin = intent.getBooleanExtra(Constants.KEY_IS_ADMIN, false);
        isVerified = intent.getBooleanExtra(Constants.KEY_IS_VERIFIED, false);
        setUserData();
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void setUserData() {
        if(profilePicture!=null){
            binding.profileImage.setImageBitmap(getBitmapFromEncodedString(profilePicture));
        }
        binding.userName.setText(userName);
        binding.userEmail.setText(userEmail);
        binding.uploadCount.setText(uploadCount);
        binding.approvedCount.setText(approveCount);
        binding.rejectedCount.setText(rejectCount);

        if(isVerified) {
            binding.verificationText.setText("This user is verified.");
        } else {
            binding.buttonVerify.setVisibility(View.VISIBLE);
            binding.buttonVerify.setOnClickListener(view -> sendVerificationEmail());
            binding.verificationText.setText("Not verified! Send verification email.");
        }
        if(availability==1) {
            binding.profileImageBg.setBackgroundTintList(ColorStateList.valueOf(R.color.green));
        }


    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void sendVerificationEmail() {
        loading(true);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(userEmail)
                .addOnSuccessListener(task -> {
                    loading(false);
                    binding.buttonVerify.setClickable(false);
                }).addOnFailureListener(e -> {
                    makeToast(e.getMessage());
                });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void makeToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

}