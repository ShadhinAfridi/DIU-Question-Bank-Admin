package com.fourdevs.diuquestionbnakadmin.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerHelpBinding;
import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder>{
    private final List<Help> helps;

    public HelpAdapter(List<Help> helps) {
        this.helps = helps;
    }

    @NonNull
    @Override
    public HelpAdapter.HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerHelpBinding itemContainerHelpBinding = ItemContainerHelpBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HelpViewHolder(itemContainerHelpBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpAdapter.HelpViewHolder holder, int position) {
        holder.setHelpData(helps.get(position));
    }

    @Override
    public int getItemCount() {
        return helps.size();
    }

    class HelpViewHolder extends RecyclerView.ViewHolder {
        ItemContainerHelpBinding binding;

        public HelpViewHolder(ItemContainerHelpBinding itemContainerHelpBinding) {
            super(itemContainerHelpBinding.getRoot());
            binding = itemContainerHelpBinding;
        }

        public void setHelpData(Help help) {
            binding.subject.setText(help.subject);
            binding.message.setText(help.message);
            getUserData(help.userId);
        }

        private void getUserData(String userId) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_USER_ID, userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            binding.userName.setText(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                            String picture = queryDocumentSnapshot.getString(Constants.KEY_PROFILE_PICTURE);
                            if(picture!=null){
                                binding.userPicture.setImageBitmap(getBitmapFromEncodedString(picture));
                            }
                        }
                    });
        }
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
