package com.example.yogaadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.adapters.SearchResultAdapter;
import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.Schedule;
import com.example.yogaadmin.models.YogaCourse;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SearchResultAdapter adapter;
    private Calendar selectedDate;

    private TextInputEditText teacherSearchInput;
    private TextInputEditText dateSearchInput;
    private AutoCompleteTextView dayOfWeekSearchInput;
    private TextView noResultsText;
    private RecyclerView searchResultsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        setupViews();
        setupSearchListeners();
    }

    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        teacherSearchInput = findViewById(R.id.teacherSearchInput);
        dateSearchInput = findViewById(R.id.dateSearchInput);
        dayOfWeekSearchInput = findViewById(R.id.dayOfWeekSearchInput);
        noResultsText = findViewById(R.id.noResultsText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Setup day of week dropdown
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_dropdown_item_1line);
        dayOfWeekSearchInput.setAdapter(daysAdapter);

        // Setup RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter();
        searchResultsRecyclerView.setAdapter(adapter);

        // Setup date picker
        dateSearchInput.setOnClickListener(v -> showDatePicker());
    }

    private void setupSearchListeners() {
        teacherSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });

        dayOfWeekSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });
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
                    updateDateDisplay();
                    performSearch();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        dateSearchInput.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void performSearch() {
        String teacherQuery = teacherSearchInput.getText().toString().trim();
        String dateQuery = dateSearchInput.getText().toString().trim();
        String dayQuery = dayOfWeekSearchInput.getText().toString().trim();

        List<SearchResult> results = new ArrayList<>();

        if (!teacherQuery.isEmpty()) {
            // Search by teacher name
            List<Schedule> schedules = dbHelper.searchSchedulesByTeacher(teacherQuery);
            for (Schedule schedule : schedules) {
                YogaCourse course = dbHelper.getYogaCourse(schedule.getYogaCourseId());
                if (course != null) {
                    results.add(new SearchResult(course, schedule));
                }
            }
        }

        if (!dateQuery.isEmpty() || !dayQuery.isEmpty()) {
            // Search by date or day of week
            List<YogaCourse> courses = dbHelper.getAllYogaCourses();
            for (YogaCourse course : courses) {
                if (!dayQuery.isEmpty() && !course.getDayOfWeek().equalsIgnoreCase(dayQuery)) {
                    continue;
                }

                List<Schedule> schedules = dbHelper.getSchedulesForCourse(course.getId());
                for (Schedule schedule : schedules) {
                    if (!dateQuery.isEmpty() && !schedule.getDate().equals(dateQuery)) {
                        continue;
                    }
                    results.add(new SearchResult(course, schedule));
                }
            }
        }

        updateSearchResults(results);
    }

    private void updateSearchResults(List<SearchResult> results) {
        if (results.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
        } else {
            noResultsText.setVisibility(View.GONE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            adapter.setResults(results);
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

    public static class SearchResult {
        private final YogaCourse course;
        private final Schedule schedule;

        public SearchResult(YogaCourse course, Schedule schedule) {
            this.course = course;
            this.schedule = schedule;
        }

        public YogaCourse getCourse() {
            return course;
        }

        public Schedule getSchedule() {
            return schedule;
        }
    }
}