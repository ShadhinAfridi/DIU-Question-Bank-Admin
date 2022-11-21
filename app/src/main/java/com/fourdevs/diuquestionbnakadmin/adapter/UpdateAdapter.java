package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.R;
import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerUploadBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;

import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.CourseViewHolder>{

    private final List<Course> coursers;
    private CourseListener courseListener;

    public UpdateAdapter(List<Course> departments, CourseListener courseListener) {
        this.coursers = departments;
        this.courseListener = courseListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUploadBinding itemContainerUploadBinding = ItemContainerUploadBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CourseViewHolder(itemContainerUploadBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.setCourseData(coursers.get(position));
    }

    @Override
    public int getItemCount() {
        return coursers.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUploadBinding binding;

        CourseViewHolder(ItemContainerUploadBinding itemContainerUploadBinding) {
            super(itemContainerUploadBinding.getRoot());
            binding = itemContainerUploadBinding;
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        void setCourseData(Course course){
            binding.courseCode.setText(course.courseName);
            binding.department.setText(course.departmentName);
            binding.semesterName.setText(course.semester +" "+course.year);
            if(course.approved != null) {
                if(!course.approved) {
                    binding.status.setText("Pending");
                    binding.status.setTextColor(R.color.primary);
                }
            } else {
                binding.status.setText("Rejected");
                binding.status.setTextColor(Color.RED);
            }

        }
    }

}
