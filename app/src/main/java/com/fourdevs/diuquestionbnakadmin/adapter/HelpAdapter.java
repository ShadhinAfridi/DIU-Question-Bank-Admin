package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerHelpBinding;
import com.fourdevs.diuquestionbnakadmin.models.Help;
import com.fourdevs.diuquestionbnakadmin.models.User;
import com.fourdevs.diuquestionbnakadmin.repository.SharedRepository;
import com.fourdevs.diuquestionbnakadmin.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpAdapter extends ListAdapter<Help, HelpAdapter.HelpViewHolder> {
    private final Application application;
    private final LifecycleOwner owner;

    public HelpAdapter(
            @NonNull DiffUtil.ItemCallback<Help> diffCallback,
                       Application application,
            LifecycleOwner owner
    ) {
        super(diffCallback);
        this.application = application;
        this.owner = owner;
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
        Help current = getItem(position);
        holder.setHelpData(current.getHelp());
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
            SharedRepository sharedRepository = new SharedRepository(application);

            sharedRepository.getUserData(userId).observe(owner, it->{
                if(it!=null) {
                    binding.userName.setText(it.userName);
                    if(it.profilePicture!=null){
                        binding.userPicture.setImageBitmap(getBitmapFromEncodedString(it.profilePicture));
                    }
                } else {
                    sharedRepository.getOnlineUserData(userId);
                }

            });

        }
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static class HelpDiff extends DiffUtil.ItemCallback<Help> {

        @Override
        public boolean areItemsTheSame(@NonNull Help oldItem, @NonNull Help newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Help oldItem, @NonNull Help newItem) {
            return oldItem.getHelp().equals(newItem.getHelp());
        }
    }

}
