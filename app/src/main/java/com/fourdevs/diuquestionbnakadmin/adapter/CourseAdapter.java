package com.fourdevs.diuquestionbnakadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourdevs.diuquestionbnakadmin.databinding.ItemContainerCoursesBinding;
import com.fourdevs.diuquestionbnakadmin.listeners.CourseListener;
import com.fourdevs.diuquestionbnakadmin.models.Course;

public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseViewHolder> {

    private final Context context;
    private final CourseListener courseListener;

    public CourseAdapter(@NonNull DiffUtil.ItemCallback<Course> diffCallback,
                         Context context,
                         CourseListener courseListener)
    {
        super(diffCallback);
        this.context = context;
        this.courseListener = courseListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCoursesBinding itemContainerCoursesBinding = ItemContainerCoursesBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CourseViewHolder(itemContainerCoursesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course current = getItem(position);
        holder.setCourseData(current.getCourse());
    }


    class CourseViewHolder extends RecyclerView.ViewHolder{
        ItemContainerCoursesBinding binding;

        CourseViewHolder(ItemContainerCoursesBinding itemContainerCoursesBinding) {
            super(itemContainerCoursesBinding.getRoot());
            binding = itemContainerCoursesBinding;
        }

        @SuppressLint("SetTextI18n")
        void setCourseData(Course course){
            binding.courseCode.setText(course.courseName);
            binding.semesterName.setText(course.semester+"("+course.year+")");
            binding.examName.setText(course.exam);
            binding.iconDownload.setOnClickListener(view -> {
                makeToast();
            });

            binding.getRoot().setOnClickListener(view -> courseListener.onCourseClicked(course));
        }
    }


    private void makeToast() {
        Toast.makeText(context, "Download not available.",Toast.LENGTH_SHORT).show();
    }

}
