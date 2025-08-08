package com.example.yogaadmin.sync;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.yogaadmin.R;
import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;
import com.example.yogaadmin.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseSync {
    private static final String COURSES_REF = "courses";
    private static final String SCHEDULES_REF = "schedules";
    
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final DatabaseReference database;
    private final SyncCallback callback;

    public interface SyncCallback {
        void onSuccess();
        void onError(String error);
    }

    public FirebaseSync(Context context, SyncCallback callback) {
        this.context = context;
        this.callback = callback;
        this.dbHelper = new DatabaseHelper(context);
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public void syncToCloud() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            handleError(context.getString(R.string.no_network));
            return;
        }

        Toast.makeText(context, R.string.sync_in_progress, Toast.LENGTH_SHORT).show();

        // Sync courses
        List<YogaCourse> unsyncedCourses = dbHelper.getUnsyncedCourses();
        for (YogaCourse course : unsyncedCourses) {
            syncCourse(course);
        }

        // Sync schedules
        List<Schedule> unsyncedSchedules = dbHelper.getUnsyncedSchedules();
        for (Schedule schedule : unsyncedSchedules) {
            syncSchedule(schedule);
        }
    }

    private void syncCourse(YogaCourse course) {
        DatabaseReference courseRef = database.child(COURSES_REF).child(String.valueOf(course.getId()));
        Map<String, Object> courseValues = new HashMap<>();
        courseValues.put("dayOfWeek", course.getDayOfWeek());
        courseValues.put("time", course.getTime());
        courseValues.put("price", course.getPrice());
        courseValues.put("capacity", course.getCapacity());
        courseValues.put("duration", course.getDuration());
        courseValues.put("type", course.getType());
        courseValues.put("description", course.getDescription());
        courseValues.put("isActive", course.isActive());
        courseValues.put("difficulty", course.getDifficulty());
        courseValues.put("equipment", course.getEquipment());
        courseValues.put("lastModified", course.getLastModified());

        courseRef.setValue(courseValues, (error, ref) -> {
            if (error == null) {
                course.setSynced(true);
                dbHelper.updateYogaCourse(course);
            } else {
                handleError("Error syncing course: " + error.getMessage());
            }
        });
    }

    private void syncSchedule(Schedule schedule) {
        DatabaseReference scheduleRef = database.child(SCHEDULES_REF).child(String.valueOf(schedule.getId()));
        Map<String, Object> scheduleValues = new HashMap<>();
        scheduleValues.put("date", schedule.getDate());
        scheduleValues.put("teacher", schedule.getTeacher());
        scheduleValues.put("comments", schedule.getComments());
        scheduleValues.put("yogaCourseId", schedule.getYogaCourseId());
        scheduleValues.put("currentEnrollment", schedule.getCurrentEnrollment());
        scheduleValues.put("isCancelled", schedule.isCancelled());
        scheduleValues.put("lastModified", schedule.getLastModified());

        scheduleRef.setValue(scheduleValues, (error, ref) -> {
            if (error == null) {
                schedule.setSynced(true);
                dbHelper.updateSchedule(schedule);
            } else {
                handleError("Error syncing schedule: " + error.getMessage());
            }
        });
    }

    public void syncFromCloud() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            handleError(context.getString(R.string.no_network));
            return;
        }

        Toast.makeText(context, R.string.sync_in_progress, Toast.LENGTH_SHORT).show();

        // Sync courses
        database.child(COURSES_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    YogaCourse course = courseSnapshot.getValue(YogaCourse.class);
                    if (course != null) {
                        course.setId(Integer.parseInt(courseSnapshot.getKey()));
                        course.setSynced(true);
                        dbHelper.updateYogaCourse(course);
                    }
                }
                syncSchedulesFromCloud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleError("Error syncing courses: " + error.getMessage());
            }
        });
    }

    private void syncSchedulesFromCloud() {
        database.child(SCHEDULES_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                    if (schedule != null) {
                        schedule.setId(Integer.parseInt(scheduleSnapshot.getKey()));
                        schedule.setSynced(true);
                        dbHelper.updateSchedule(schedule);
                    }
                }
                handleSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleError("Error syncing schedules: " + error.getMessage());
            }
        });
    }

    private void handleSuccess() {
        Toast.makeText(context, R.string.sync_complete, Toast.LENGTH_SHORT).show();
        if (callback != null) {
            callback.onSuccess();
        }
    }

    private void handleError(String error) {
        Toast.makeText(context, R.string.sync_error, Toast.LENGTH_SHORT).show();
        if (callback != null) {
            callback.onError(error);
        }
    }
}