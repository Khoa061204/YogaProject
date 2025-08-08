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
import com.example.yogaadmin.SearchActivity.SearchResult;
import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<SearchResult> results;

    public SearchResultAdapter() {
        this.results = new ArrayList<>();
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView courseTypeText;
        private final TextView courseScheduleText;
        private final TextView teacherText;
        private final TextView dateText;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            courseTypeText = itemView.findViewById(R.id.courseTypeText);
            courseScheduleText = itemView.findViewById(R.id.courseScheduleText);
            teacherText = itemView.findViewById(R.id.teacherText);
            dateText = itemView.findViewById(R.id.dateText);
        }

        void bind(SearchResult result) {
            YogaCourse course = result.getCourse();
            Schedule schedule = result.getSchedule();

            courseTypeText.setText(course.getType());
            courseScheduleText.setText(String.format("%s at %s", course.getDayOfWeek(), course.getTime()));
            teacherText.setText(String.format("Teacher: %s", schedule.getTeacher()));
            dateText.setText(String.format("Date: %s", schedule.getDate()));

            if (schedule.isCancelled()) {
                cardView.setAlpha(0.5f);
                dateText.setText(String.format("Date: %s (Cancelled)", schedule.getDate()));
            } else {
                cardView.setAlpha(1.0f);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CourseDetailsActivity.class);
                intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, course.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}