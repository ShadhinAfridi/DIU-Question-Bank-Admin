package com.fourdevs.diuquestionbnakadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fourdevs.diuquestionbnakadmin.databinding.ActivityManageUserBinding;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class ManageUserActivity extends AppCompatActivity {
    private ActivityManageUserBinding binding;

    private String userId, userName, userEmail, profilePicture;
    private String uploadCount, rejectCount, approveCount, fcmToken;
    private Integer availability;
    private Boolean isVerified, isAdmin;
    private FirebaseAuth auth;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            getUserData();
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        setListeners();
    }

    private void getUserData() throws FirebaseAuthException {
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

        auth = FirebaseAuth.getInstance();

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
        binding.titleProfile.setText(userId);

        if(isVerified) {
            binding.verificationText.setText("This user is verified.");
        } else {
            binding.verificationText.setText("Not verified! Send verification email.");
        }

    }

    private void setListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}