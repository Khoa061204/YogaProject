package com.example.yogaadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.R;
import com.example.yogaadmin.models.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<Schedule> schedules;
    private final OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
        void onEditSchedule(Schedule schedule);
        void onDeleteSchedule(Schedule schedule);
        void onToggleCancelled(Schedule schedule);
    }

    public ScheduleAdapter(OnScheduleClickListener listener) {
        this.schedules = new ArrayList<>();
        this.listener = listener;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule, listener);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView teacherText;
        private final TextView enrollmentText;
        private final TextView commentsText;
        private final ImageButton menuButton;

        ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.scheduleDate);
            teacherText = itemView.findViewById(R.id.scheduleTeacher);
            enrollmentText = itemView.findViewById(R.id.scheduleEnrollment);
            commentsText = itemView.findViewById(R.id.scheduleComments);
            menuButton = itemView.findViewById(R.id.scheduleMenuButton);
        }

        void bind(final Schedule schedule, final OnScheduleClickListener listener) {
            dateText.setText(schedule.getDate());
            teacherText.setText(String.format("Teacher: %s", schedule.getTeacher()));
            enrollmentText.setText(String.format("Enrollment: %d", schedule.getCurrentEnrollment()));
            
            if (schedule.getComments() != null && !schedule.getComments().isEmpty()) {
                commentsText.setText(schedule.getComments());
                commentsText.setVisibility(View.VISIBLE);
            } else {
                commentsText.setVisibility(View.GONE);
            }

            if (schedule.isCancelled()) {
                itemView.setAlpha(0.5f);
                dateText.setText(String.format("%s (Cancelled)", schedule.getDate()));
            } else {
                itemView.setAlpha(1.0f);
            }

            itemView.setOnClickListener(v -> listener.onScheduleClick(schedule));

            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.menu_schedule_item);
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit_schedule) {
                        listener.onEditSchedule(schedule);
                        return true;
                    } else if (itemId == R.id.action_delete_schedule) {
                        listener.onDeleteSchedule(schedule);
                        return true;
                    } else if (itemId == R.id.action_toggle_cancelled) {
                        listener.onToggleCancelled(schedule);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}