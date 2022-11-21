package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fourdevs.diuquestionbnakadmin.R;
import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerUserBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.UsersListener;
import com.fourdevs.diuquestionbnakadmin.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    private final List<User> users;
    private final UsersListener usersListener;

    public UsersAdapter(List<User> users, UsersListener usersListener) {
        this.users = users;
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
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        UsersViewHolder(ItemContainerUserBinding itemContainerUsersBinding) {
            super(itemContainerUsersBinding.getRoot());
            binding = itemContainerUsersBinding;
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        void setUserData(User user){
            binding.userName.setText(user.name);
            binding.userEmail.setText(user.email);

            if(user.availability == 0){
                binding.userAvailable.setText("Offline");
                binding.userAvailable.setTextColor(R.color.primary);
            }
            if(user.availability == 2){
                binding.userAvailable.setText("Not Verified");
                binding.userAvailable.setTextColor(Color.RED);
            }
            binding.getRoot().setOnClickListener(view -> usersListener.onUserClicked(user));
        }

    }



}