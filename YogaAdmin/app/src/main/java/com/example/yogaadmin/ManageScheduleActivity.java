package com.example.yogaadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManageScheduleActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_SCHEDULE_ID = "schedule_id";

    private DatabaseHelper dbHelper;
    private YogaCourse course;
    private Schedule schedule;
    private Calendar selectedDate;

    private TextInputLayout dateLayout;
    private TextInputLayout teacherLayout;
    private TextInputEditText dateInput;
    private TextInputEditText teacherInput;
    private TextInputEditText commentsInput;
    private SwitchMaterial cancelledSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schedule);

        dbHelper = new DatabaseHelper(this);
        setupViews();
        loadData();
    }

    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        dateLayout = findViewById(R.id.dateLayout);
        teacherLayout = findViewById(R.id.teacherLayout);
        dateInput = findViewById(R.id.dateInput);
        teacherInput = findViewById(R.id.teacherInput);
        commentsInput = findViewById(R.id.commentsInput);
        cancelledSwitch = findViewById(R.id.cancelledSwitch);

        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        dateInput.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveSchedule());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadData() {
        int courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        if (courseId == -1) {
            Toast.makeText(this, "Error: Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        course = dbHelper.getYogaCourse(courseId);
        if (course == null) {
            Toast.makeText(this, "Error: Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int scheduleId = getIntent().getIntExtra(EXTRA_SCHEDULE_ID, -1);
        if (scheduleId != -1) {
            // Edit existing schedule
            // Load from DB and prefill
            for (Schedule s : dbHelper.getSchedulesForCourse(course.getId())) {
                if (s.getId() == scheduleId) {
                    schedule = s;
                    break;
                }
            }

            if (schedule != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Schedule");
                }
                try {
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                    Date parsed = df.parse(schedule.getDate());
                    selectedDate = Calendar.getInstance();
                    if (parsed != null) selectedDate.setTime(parsed);
                } catch (ParseException ignored) {
                    selectedDate = Calendar.getInstance();
                }

                dateInput.setText(schedule.getDate());
                teacherInput.setText(schedule.getTeacher());
                commentsInput.setText(schedule.getComments());
                cancelledSwitch.setChecked(schedule.isCancelled());
            } else {
                Toast.makeText(this, "Error: Schedule not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // New schedule
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Schedule");
            }
            selectedDate = Calendar.getInstance();
            updateDateDisplay();
        }
    }

    private void showDatePicker() {
        if (selectedDate == null) {
            selectedDate = Calendar.getInstance();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    if (validateSelectedDate()) {
                        updateDateDisplay();
                    } else {
                        Toast.makeText(this, "Selected date must be a " + course.getDayOfWeek(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateSelectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String dayOfWeek = sdf.format(selectedDate.getTime());
        return dayOfWeek.equalsIgnoreCase(course.getDayOfWeek());
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        dateInput.setText(dateFormat.format(selectedDate.getTime()));
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (dateInput.getText().toString().trim().isEmpty()) {
            dateLayout.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            dateLayout.setError(null);
        }

        if (teacherInput.getText().toString().trim().isEmpty()) {
            teacherLayout.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            teacherLayout.setError(null);
        }

        return isValid;
    }

    private void saveSchedule() {
        if (!validateInputs()) {
            return;
        }

        if (schedule == null) {
            schedule = new Schedule();
            schedule.setYogaCourseId(course.getId());
        }

        schedule.setDate(dateInput.getText().toString());
        schedule.setTeacher(teacherInput.getText().toString().trim());
        schedule.setComments(commentsInput.getText().toString().trim());
        schedule.setCancelled(cancelledSwitch.isChecked());

        if (schedule.getId() == 0) {
            long result = dbHelper.addSchedule(schedule);
            if (result != -1) {
                Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving schedule", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rows = dbHelper.updateSchedule(schedule);
            if (rows > 0) {
                Toast.makeText(this, "Schedule updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating schedule", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}