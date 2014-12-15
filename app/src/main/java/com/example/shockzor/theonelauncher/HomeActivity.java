package com.example.shockzor.theonelauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * Created by Shockzor on 12.12.2014.
 */

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showApps(View v){
        /** Call activity to show an app list */
        Intent i = new Intent(this, AppListActivity.class);

        startActivity(i);
    }

    public void showMessenger(View v){
        /** Creating an intent with default sms-mms app call  */
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");

        startActivity(smsIntent);
    }

    public void showDialer(View v){
        /** Creating an intent with the dialer call */
        Intent intent = new Intent("android.intent.action.DIAL");

        /** Starting the Dialer activity */
        startActivity(intent);
    }
}