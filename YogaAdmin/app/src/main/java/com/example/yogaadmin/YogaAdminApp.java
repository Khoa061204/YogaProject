package com.example.yogaadmin;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class YogaAdminApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}