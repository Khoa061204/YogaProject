package com.example.yogaadmin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.YogaCourse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class CreateYogaCourse extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private AutoCompleteTextView dayOfWeekInput;
    private AutoCompleteTextView timeInput;
    private TextInputEditText capacityInput;
    private TextInputEditText durationInput;
    private TextInputEditText priceInput;
    private AutoCompleteTextView typeInput;
    private AutoCompleteTextView difficultyInput;
    private TextInputEditText equipmentInput;
    private TextInputEditText descriptionInput;

    private TextInputLayout dayOfWeekLayout;
    private TextInputLayout timeLayout;
    private TextInputLayout capacityLayout;
    private TextInputLayout durationLayout;
    private TextInputLayout priceLayout;
    private TextInputLayout typeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_yoga_course);

        dbHelper = new DatabaseHelper(this);
        setupToolbar();
        setupInputFields();
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupInputFields() {
        // Get references to TextInputLayouts
        dayOfWeekLayout = findViewById(R.id.dayOfWeekLayout);
        timeLayout = findViewById(R.id.timeLayout);
        capacityLayout = findViewById(R.id.capacityLayout);
        durationLayout = findViewById(R.id.durationLayout);
        priceLayout = findViewById(R.id.priceLayout);
        typeLayout = findViewById(R.id.typeLayout);

        // Get references to input fields
        dayOfWeekInput = findViewById(R.id.dayOfWeekInput);
        timeInput = findViewById(R.id.timeInput);
        capacityInput = findViewById(R.id.capacityInput);
        durationInput = findViewById(R.id.durationInput);
        priceInput = findViewById(R.id.priceInput);
        typeInput = findViewById(R.id.typeInput);
        difficultyInput = findViewById(R.id.difficultyInput);
        equipmentInput = findViewById(R.id.equipmentInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        // Setup day of week dropdown
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_dropdown_item_1line);
        dayOfWeekInput.setAdapter(daysAdapter);

        // Setup time dropdown
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_times, android.R.layout.simple_dropdown_item_1line);
        timeInput.setAdapter(timeAdapter);

        // Setup type dropdown
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_types, android.R.layout.simple_dropdown_item_1line);
        typeInput.setAdapter(typeAdapter);

        // Setup difficulty dropdown
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_dropdown_item_1line);
        difficultyInput.setAdapter(difficultyAdapter);
    }

    private void setupButtons() {
        Button addButton = findViewById(R.id.addButton);
        Button clearButton = findViewById(R.id.clearButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    showConfirmationDialog();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        dayOfWeekLayout.setError(null);
        timeLayout.setError(null);
        capacityLayout.setError(null);
        durationLayout.setError(null);
        priceLayout.setError(null);
        typeLayout.setError(null);

        // Validate required fields
        if (dayOfWeekInput.getText().toString().trim().isEmpty()) {
            dayOfWeekLayout.setError(getString(R.string.required_field));
            isValid = false;
        }

        if (timeInput.getText().toString().trim().isEmpty()) {
            timeLayout.setError(getString(R.string.required_field));
            isValid = false;
        }

        String capacityStr = capacityInput.getText().toString().trim();
        if (capacityStr.isEmpty()) {
            capacityLayout.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    capacityLayout.setError("Capacity must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                capacityLayout.setError("Invalid capacity");
                isValid = false;
            }
        }

        String durationStr = durationInput.getText().toString().trim();
        if (durationStr.isEmpty()) {
            durationLayout.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0) {
                    durationLayout.setError("Duration must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                durationLayout.setError("Invalid duration");
                isValid = false;
            }
        }

        String priceStr = priceInput.getText().toString().trim();
        if (priceStr.isEmpty()) {
            priceLayout.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            try {
                float price = Float.parseFloat(priceStr);
                if (price <= 0) {
                    priceLayout.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                priceLayout.setError("Invalid price");
                isValid = false;
            }
        }

        if (typeInput.getText().toString().trim().isEmpty()) {
            typeLayout.setError(getString(R.string.required_field));
            isValid = false;
        }

        return isValid;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_course, null);

        // Populate confirmation dialog
        ((TextView) view.findViewById(R.id.confirmDayOfWeek)).setText(dayOfWeekInput.getText());
        ((TextView) view.findViewById(R.id.confirmTime)).setText(timeInput.getText());
        ((TextView) view.findViewById(R.id.confirmCapacity)).setText(capacityInput.getText());
        ((TextView) view.findViewById(R.id.confirmDuration)).setText(
                String.format("%s minutes", durationInput.getText()));
        ((TextView) view.findViewById(R.id.confirmPrice)).setText(
                String.format(Locale.UK, "£%.2f", Float.parseFloat(priceInput.getText().toString())));
        ((TextView) view.findViewById(R.id.confirmType)).setText(typeInput.getText());
        ((TextView) view.findViewById(R.id.confirmDifficulty)).setText(
                difficultyInput.getText().toString().isEmpty() ? "Not specified" : difficultyInput.getText());
        ((TextView) view.findViewById(R.id.confirmEquipment)).setText(
                equipmentInput.getText().toString().isEmpty() ? "None" : equipmentInput.getText());
        ((TextView) view.findViewById(R.id.confirmDescription)).setText(
                descriptionInput.getText().toString().isEmpty() ? "No description" : descriptionInput.getText());

        builder.setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> saveYogaCourse())
                .setNegativeButton(R.string.edit, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void saveYogaCourse() {
        YogaCourse course = new YogaCourse();
        course.setDayOfWeek(dayOfWeekInput.getText().toString());
        course.setTime(timeInput.getText().toString());
        course.setCapacity(Integer.parseInt(capacityInput.getText().toString()));
        course.setDuration(Integer.parseInt(durationInput.getText().toString()));
        course.setPrice(Float.parseFloat(priceInput.getText().toString()));
        course.setType(typeInput.getText().toString());
        course.setDifficulty(difficultyInput.getText().toString());
        course.setEquipment(equipmentInput.getText().toString());
        course.setDescription(descriptionInput.getText().toString());

        long result = dbHelper.addYogaCourse(course);
        if (result != -1) {
            Toast.makeText(this, R.string.course_added, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error_adding_course, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        dayOfWeekInput.setText("");
        timeInput.setText("");
        capacityInput.setText("");
        durationInput.setText("");
        priceInput.setText("");
        typeInput.setText("");
        difficultyInput.setText("");
        equipmentInput.setText("");
        descriptionInput.setText("");

        // Clear all error messages
        dayOfWeekLayout.setError(null);
        timeLayout.setError(null);
        capacityLayout.setError(null);
        durationLayout.setError(null);
        priceLayout.setError(null);
        typeLayout.setError(null);
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