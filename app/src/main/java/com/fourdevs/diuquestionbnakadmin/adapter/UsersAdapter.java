package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.R;
import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerUserBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.UsersListener;
import com.fourdevs.diuquestionbnakadmin.models.User;

public class UsersAdapter extends ListAdapter<User, UsersAdapter.UsersViewHolder> {

    private final UsersListener usersListener;

    public UsersAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback,
                        UsersListener usersListener)
    {
        super(diffCallback);
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UsersViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
        User user = getItem(position);
        holder.setUserData(user.getUser());
    }


    class UsersViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        UsersViewHolder(ItemContainerUserBinding itemContainerUsersBinding) {
            super(itemContainerUsersBinding.getRoot());
            binding = itemContainerUsersBinding;
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        void setUserData(User user){
            binding.userName.setText(user.userName);
            binding.userEmail.setText(user.email);

            if(user.availability == 0){
                binding.userAvailable.setText("Offline");
            }
            if(user.availability == 2){
                binding.userAvailable.setText("Not Verified");
            }
            binding.getRoot().setOnClickListener(view -> usersListener.onUserClicked(user));
        }

    }

    public static class UserDiff extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUser().equals(newItem.getUser());
        }
    }



}