package com.example.yogaadmin.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.yogaadmin.R;

public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Network is available
                Toast.makeText(context, R.string.network_connected, Toast.LENGTH_SHORT).show();
            } else {
                // Network is not available
                Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        }
    }
}