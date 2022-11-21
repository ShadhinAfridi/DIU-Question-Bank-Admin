package com.fourdevs.diuquestionbnakadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fourdevs.diuquestionbnakadmin.databinding.ActivityNoticeBinding;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.fourdevs.diuquestionbnakadmin.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;


public class NoticeActivity extends BaseActivity{
    private ActivityNoticeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeListeners();
    }

    private void initializeListeners() {
        binding.iconBack.setOnClickListener(view -> onBackPressed());
        binding.buttonSend.setOnClickListener(view -> updateToDatabase());
    }

    private void updateToDatabase() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        String title = binding.inputTitle.getText().toString().trim();
        String message = binding.inputMessage.getText().toString().trim();
        if(title.isEmpty()){
            makeToast("Please give a title!");
            return;
        } else if (message.isEmpty()){
            makeToast("Please input message!");
            return;
        }
        HashMap<String, Object> contact = new HashMap<>();
        contact.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        contact.put(Constants.KEY_SUBJECT, binding.inputTitle.getText().toString().trim());
        contact.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString().trim());
        contact.put(Constants.KEY_LINK, binding.inputLink.getText().toString().trim());
        contact.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_NOTIFICATIONS)
                .add(contact)
                .addOnCompleteListener(task -> {
                    loading(false);
                    clearForm();
                    makeToast("Sent");
                }).addOnFailureListener(e -> makeToast(e.getMessage()));
    }

    private void makeToast(String value) {
        Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private void clearForm() {
        binding.inputTitle.setText("");
        binding.inputMessage.setText("");
        binding.inputLink.setText("");
    }
}