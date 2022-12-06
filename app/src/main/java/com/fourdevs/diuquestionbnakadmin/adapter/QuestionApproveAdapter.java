package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerApproveBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;

import java.util.List;

public class QuestionApproveAdapter extends ListAdapter<Course, QuestionApproveAdapter.QuestionApproveViewHolder> {

    private final CourseListener courseListener;

    public QuestionApproveAdapter(@NonNull DiffUtil.ItemCallback<Course> diffCallback, CourseListener courseListener) {
        super(diffCallback);
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
        Course current = getItem(position);
        holder.setCourseData(current.getCourse());
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
