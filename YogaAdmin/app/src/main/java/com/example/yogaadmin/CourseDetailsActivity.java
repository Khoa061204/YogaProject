package com.example.yogaadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.adapters.ScheduleAdapter;
import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class CourseDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "course_id";

    private DatabaseHelper dbHelper;
    private YogaCourse course;
    private ScheduleAdapter scheduleAdapter;

    private TextView courseTypeText;
    private TextView courseScheduleText;
    private TextView courseDurationText;
    private TextView courseCapacityText;
    private TextView coursePriceText;
    private TextView courseDifficultyText;
    private TextView courseEquipmentText;
    private TextView courseDescriptionText;
    private RecyclerView scheduleRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        dbHelper = new DatabaseHelper(this);
        setupViews();
        setupScheduleRecyclerView();

        int courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        if (courseId == -1) {
            Toast.makeText(this, "Error loading course", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCourse(courseId);
    }

    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        courseTypeText = findViewById(R.id.courseTypeText);
        courseScheduleText = findViewById(R.id.courseScheduleText);
        courseDurationText = findViewById(R.id.courseDurationText);
        courseCapacityText = findViewById(R.id.courseCapacityText);
        coursePriceText = findViewById(R.id.coursePriceText);
        courseDifficultyText = findViewById(R.id.courseDifficultyText);
        courseEquipmentText = findViewById(R.id.courseEquipmentText);
        courseDescriptionText = findViewById(R.id.courseDescriptionText);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);

        FloatingActionButton addScheduleFab = findViewById(R.id.addScheduleFab);
        addScheduleFab.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailsActivity.this, ManageScheduleActivity.class);
            intent.putExtra(ManageScheduleActivity.EXTRA_COURSE_ID, course.getId());
            startActivity(intent);
        });
    }

    private void loadCourse(int courseId) {
        course = dbHelper.getYogaCourse(courseId);
        if (course == null) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateCourseDetails();
        loadSchedules();
    }

    private void updateCourseDetails() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(course.getType());
        }

        courseTypeText.setText(course.getType());
        courseScheduleText.setText(String.format("%s at %s", course.getDayOfWeek(), course.getTime()));
        courseDurationText.setText(String.format("%d minutes", course.getDuration()));
        courseCapacityText.setText(String.format("Capacity: %d persons", course.getCapacity()));
        coursePriceText.setText(String.format(Locale.UK, "£%.2f per class", course.getPrice()));
        
        String difficulty = course.getDifficulty();
        if (difficulty != null && !difficulty.isEmpty()) {
            courseDifficultyText.setText(String.format("Difficulty: %s", difficulty));
            courseDifficultyText.setVisibility(View.VISIBLE);
        } else {
            courseDifficultyText.setVisibility(View.GONE);
        }

        String equipment = course.getEquipment();
        if (equipment != null && !equipment.isEmpty()) {
            courseEquipmentText.setText(String.format("Required Equipment: %s", equipment));
            courseEquipmentText.setVisibility(View.VISIBLE);
        } else {
            courseEquipmentText.setVisibility(View.GONE);
        }

        String description = course.getDescription();
        if (description != null && !description.isEmpty()) {
            courseDescriptionText.setText(description);
            courseDescriptionText.setVisibility(View.VISIBLE);
        } else {
            courseDescriptionText.setVisibility(View.GONE);
        }
    }

    private void setupScheduleRecyclerView() {
        if (scheduleRecyclerView != null) {
            scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            scheduleAdapter = new ScheduleAdapter(new ScheduleAdapter.OnScheduleClickListener() {
                @Override
                public void onScheduleClick(Schedule schedule) {
                    // Open edit directly on click
                    Intent intent = new Intent(CourseDetailsActivity.this, ManageScheduleActivity.class);
                    intent.putExtra(ManageScheduleActivity.EXTRA_COURSE_ID, course.getId());
                    intent.putExtra(ManageScheduleActivity.EXTRA_SCHEDULE_ID, schedule.getId());
                    startActivity(intent);
                }

                @Override
                public void onEditSchedule(Schedule schedule) {
                    Intent intent = new Intent(CourseDetailsActivity.this, ManageScheduleActivity.class);
                    intent.putExtra(ManageScheduleActivity.EXTRA_COURSE_ID, course.getId());
                    intent.putExtra(ManageScheduleActivity.EXTRA_SCHEDULE_ID, schedule.getId());
                    startActivity(intent);
                }

                @Override
                public void onDeleteSchedule(Schedule schedule) {
                    new AlertDialog.Builder(CourseDetailsActivity.this)
                        .setTitle("Delete Schedule")
                        .setMessage("Are you sure you want to delete this scheduled class?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deleteSchedule(schedule.getId());
                            loadSchedules();
                            Toast.makeText(CourseDetailsActivity.this, R.string.course_deleted, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }

                @Override
                public void onToggleCancelled(Schedule schedule) {
                    schedule.setCancelled(!schedule.isCancelled());
                    dbHelper.updateSchedule(schedule);
                    loadSchedules();
                }
            });
            scheduleRecyclerView.setAdapter(scheduleAdapter);
        }
    }

    private void loadSchedules() {
        if (course != null && scheduleAdapter != null) {
            List<Schedule> schedules = dbHelper.getSchedulesForCourse(course.getId());
            scheduleAdapter.setSchedules(schedules);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_edit) {
            // TODO: Implement edit functionality
            Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course? This will also delete all scheduled classes.")
                .setPositiveButton("Delete", (dialog, which) -> deleteCourse())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCourse() {
        dbHelper.deleteYogaCourse(course.getId());
        Toast.makeText(this, R.string.course_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (course != null) {
            loadSchedules();
        }
    }
}