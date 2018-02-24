package com.example.outofthecave.geburtstagskalender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Notifies the user of a birthday.
 */
public class BirthdayNotifier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO show notification
        Log.d("BirthdayNotifier", "would trigger notification");
    }
}
