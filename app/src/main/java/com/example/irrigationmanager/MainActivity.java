package com.example.irrigationmanager;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.example.irrigationmanager.tools.NetworkManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView data_field;
    Activity activity;

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary, this.getTheme()));
        activity = this;
        data_field = findViewById(R.id.data_field);

        setTime();

        /*if (!NetworkManager.isNetworkAvailable(getApplicationContext())) {
            Intent myIntent = new Intent(getApplicationContext(), CheckActivity.class);
            startActivity(myIntent);
            finish();
        }*/

        Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                if (!NetworkManager.isNetworkAvailable(getApplicationContext())) {
                    Intent myIntent = new Intent(getApplicationContext(), CheckActivity.class);
                    startActivity(myIntent);
                    activity.finish();
                }else{
                    setTime();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(refresh, 1000);
    }

    @SuppressLint("SetTextI18n")
    private void setTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("EEEE", Locale.getDefault());
        String day = df2.format(c);
        String date = df1.format(c);
        day = day.substring(0, 1).toUpperCase() + day.substring(1);
        data_field.setText(day +", "+date);
    }
}