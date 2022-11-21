package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerApproveBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;

import java.util.List;

public class QuestionApproveAdapter extends RecyclerView.Adapter<QuestionApproveAdapter.QuestionApproveViewHolder>{

    private final List<Course> coursers;
    private final CourseListener courseListener;

    public QuestionApproveAdapter(List<Course> departments, CourseListener courseListener) {
        this.coursers = departments;
        this.courseListener = courseListener;
    }

    @NonNull
    @Override
    public QuestionApproveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerApproveBinding itemContainerApproveBinding = ItemContainerApproveBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new QuestionApproveViewHolder(itemContainerApproveBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionApproveViewHolder holder, int position) {
        holder.setCourseData(coursers.get(position));
    }

    @Override
    public int getItemCount() {
        return coursers.size();
    }

    class QuestionApproveViewHolder extends RecyclerView.ViewHolder{
        ItemContainerApproveBinding binding;

        QuestionApproveViewHolder(ItemContainerApproveBinding itemContainerApproveBinding) {
            super(itemContainerApproveBinding.getRoot());
            binding = itemContainerApproveBinding;
        }

        @SuppressLint("SetTextI18n")
        void setCourseData(Course course){
            binding.courseCode.setText(course.courseName);
            binding.semesterName.setText(course.semester+"\n"+course.year+"\n"+course.exam);
            binding.department.setText(course.departmentName);
            binding.getRoot().setOnClickListener(view -> courseListener.onCourseClicked(course));
        }

    }

}
