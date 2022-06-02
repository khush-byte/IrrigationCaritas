package com.example.irrigationmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.irrigationmanager.tools.MyArray;
import com.example.irrigationmanager.tools.NetworkManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class MainActivity extends AppCompatActivity {
    TextView data_field;
    Activity activity;
    public static int plot_number = 0;
    public static boolean isPump = false;
    public static String date;
    public static int minutes = 0;
    public static int water_level = 0;
    public static boolean isSend = false;
    private String select_date;
    private ArrayList<MyArray> rec_data;

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary, this.getTheme()));
        activity = this;
        data_field = findViewById(R.id.data_field);
        rec_data = new ArrayList<>();

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        setTime();
        getRecommendation();

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
        SimpleDateFormat df3 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String day = df2.format(c);
        date = df1.format(c);
        select_date = df3.format(c);
        day = day.substring(0, 1).toUpperCase() + day.substring(1);
        data_field.setText(day +", "+date);
    }

    public void getRecommendation() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String sign = MD5(currentDate + "bCctS9eqoYaZl21a");
        rec_data.clear();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://wwcs.tj/meteo/irrigation/schedule.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sign", sign);
                    jsonParam.put("datetime", currentDate);
                    jsonParam.put("select_date", select_date);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    //Log.i("STATUS", String.valueOf(conn.getResponseCode()));

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    conn.disconnect();
                    String response = sb.toString();

                    if (response != null) {
                        Log.i("MSG" , response);

                        /*try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject fromJson = array.getJSONObject(i);
                                MyArray data = new MyArray();
                                data.select_date = fromJson.getString("select_date");
                                data.count_days = fromJson.getInt("count_days");
                                data.p2_state = fromJson.getInt("p2_state");
                                data.p3_state = fromJson.getInt("p3_state");
                                data.p2_need_mm = fromJson.getInt("p2_irrig_need_mm");
                                data.p3_need_min = fromJson.getInt("p3_rec_time_min");
                                rec_data.add(data);
                            }
                            Intent intent = new Intent();
                            intent.putCharSequenceArrayListExtra("data", rec_data);
                            Log.i("massive" , String.valueOf(rec_data.size()));
                        } catch(Exception e){
                            Log.e("Debug", "Error in parsing");
                        }*/

                        SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences("root_data", 0).edit();
                        editor.putString("response", response);
                        editor.apply();
                    } else {
                        Toast.makeText(getBaseContext(), "Server is not responding!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public void getRecPlot2() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String sign = MD5(currentDate + "bCctS9eqoYaZl21a");

        Thread thread = new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    URL url = new URL("https://wwcs.tj/meteo/irrigation/tomson.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sign", sign);
                    jsonParam.put("datetime", currentDate);
                    jsonParam.put("tomson", water_level);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    //Log.i("STATUS", String.valueOf(conn.getResponseCode()));

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    conn.disconnect();
                    String response = sb.toString();

                    String min =  response.replaceAll("[^0-9]", "");
                    Log.i("MSG2" , min + " мин.");
                    //plot2_rec_min.setText(min + " мин.");
                    minutes = Integer.parseInt(min);

                    NavController navController = Navigation.findNavController(activity, R.id.nav_host_main);
                    navController.navigate(R.id.fourthFragment);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onBackPressed()
    {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_main);
        String fname = Objects.requireNonNull(Objects.requireNonNull(navController.getCurrentDestination()).getLabel()).toString();
        Log.i("MSG2" , fname);

        if(fname.equals("fragment_auth")) activity.finish();
        else if(fname.equals("fragment_main")) activity.finish();
        else if(fname.equals("fragment_second")) navController.navigate(R.id.mainFragment);
        else if(fname.equals("fragment_third")) navController.navigate(R.id.mainFragment);
        else if(fname.equals("fragment_info")) navController.navigate(R.id.mainFragment);
        else if(fname.equals("fragment_fourth")) navController.navigate(R.id.secondFragment);
        else if(fname.equals("fragment_first")) navController.navigate(R.id.mainFragment);
        else navController.navigate(R.id.mainFragment);
    }
}