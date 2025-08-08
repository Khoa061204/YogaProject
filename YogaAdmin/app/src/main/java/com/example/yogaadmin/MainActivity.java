package com.example.yogaadmin;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin.adapters.YogaCourseAdapter;
import com.example.yogaadmin.db.DatabaseHelper;
import com.example.yogaadmin.models.YogaCourse;
import com.example.yogaadmin.utils.NetworkStateReceiver;
import com.example.yogaadmin.sync.FirebaseSync;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private YogaCourseAdapter adapter;
    private RecyclerView recyclerView;
    private NetworkStateReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        setupToolbar();
        setupRecyclerView();
        setupFab();
        setupNetworkReceiver();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.yogaClassesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<YogaCourse> courses = dbHelper.getAllYogaCourses();
        adapter = new YogaCourseAdapter(courses);
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.addClassFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateYogaCourse.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_sync) {
            syncData();
            return true;
        } else if (itemId == R.id.action_reset_db) {
            resetDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void syncData() {
        FirebaseSync firebaseSync = new FirebaseSync(this, new FirebaseSync.SyncCallback() {
            @Override
            public void onSuccess() {
                adapter.updateCourses(dbHelper.getAllYogaCourses());
            }

            @Override
            public void onError(String error) {
                // Error is already shown via Toast in FirebaseSync
            }
        });
        firebaseSync.syncToCloud();
    }

    private void resetDatabase() {
        // Danger: clears all data.
        // Recreate the helper with a new writable DB and drop/recreate tables via onUpgrade logic.
        // Simplest safe approach: delete and recreate by bumping the version is heavy; instead, use helper's writable DB.
        // Here, we will delete all rows from tables using helper methods.
        getApplicationContext().deleteDatabase("YogaAdmin.db");
        dbHelper = new DatabaseHelper(this);
        adapter.updateCourses(dbHelper.getAllYogaCourses());
    }

    private void setupNetworkReceiver() {
        networkReceiver = new NetworkStateReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        if (adapter != null) {
            adapter.updateCourses(dbHelper.getAllYogaCourses());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }
}