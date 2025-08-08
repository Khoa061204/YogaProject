package com.example.yogaadmin.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.CourseDetailsActivity;
import com.example.yogaadmin.R;
import com.example.yogaadmin.models.YogaCourse;

import java.util.List;
import java.util.Locale;

public class YogaCourseAdapter extends RecyclerView.Adapter<YogaCourseAdapter.ViewHolder> {
    private List<YogaCourse> courses;

    public YogaCourseAdapter(List<YogaCourse> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yoga_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YogaCourse course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateCourses(List<YogaCourse> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTypeText;
        TextView courseTimeText;
        TextView courseDetailsText;
        TextView coursePriceText;

        ViewHolder(View itemView) {
            super(itemView);
            courseTypeText = itemView.findViewById(R.id.courseTypeText);
            courseTimeText = itemView.findViewById(R.id.courseTimeText);
            courseDetailsText = itemView.findViewById(R.id.courseDetailsText);
            coursePriceText = itemView.findViewById(R.id.coursePriceText);
        }

        void bind(final YogaCourse course) {
            courseTypeText.setText(course.getType());
            courseTimeText.setText(String.format("Time: %s on %s", course.getTime(), course.getDayOfWeek()));
            
            String details = course.getDescription();
            if (course.getDifficulty() != null && !course.getDifficulty().isEmpty()) {
                details = String.format("%s • %s", course.getDifficulty(), details);
            }
            courseDetailsText.setText(details);
            
            coursePriceText.setText(String.format(Locale.UK, "£%.2f", course.getPrice()));

            if (!course.isActive()) {
                itemView.setAlpha(0.5f);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CourseDetailsActivity.class);
                intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, course.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}