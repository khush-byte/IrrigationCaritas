package com.example.irrigationmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.irrigationmanager.tools.MyArray;
import com.example.irrigationmanager.tools.NetworkManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

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
    private final OkHttpClient client = new OkHttpClient();

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

        Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                if (!NetworkManager.isNetworkAvailable(getApplicationContext())) {
                    Intent myIntent = new Intent(getApplicationContext(), CheckActivity.class);
                    startActivity(myIntent);
                    activity.finish();
                } else {
                    setTime();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(refresh, 1000);

        //sendPost();
    }

    @SuppressLint("SetTextI18n")
    private void setTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat df3 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String day = df2.format(c);
        date = df1.format(c);
        select_date = df3.format(c);
        day = day.substring(0, 1).toUpperCase() + day.substring(1);
        data_field.setText(day + ", " + date);
    }

    public void getRecommendation() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String sign = MD5(currentDate + "bCctS9eqoYaZl21a");
        rec_data.clear();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://kiosk.tj/irrigation/schedule.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sign", sign);
                    jsonParam.put("datetime", currentDate);
                    jsonParam.put("select_date", select_date);

                    //Log.i("MyTag", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    //Log.i("MyTag", String.valueOf(conn.getResponseCode()));

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    conn.disconnect();
                    String response = sb.toString();

                    //Log.i("MyTag", response);

                    SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences("root_data", 0).edit();
                    editor.putString("response", response);
                    editor.apply();

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

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_main);
        String fname = Objects.requireNonNull(Objects.requireNonNull(navController.getCurrentDestination()).getLabel()).toString();
        //Log.i("MSG2", fname);

        switch (fname) {
            case "fragment_auth":
                activity.finish();
                break;
            case "fragment_main":
                activity.finish();
                break;
            case "fragment_second":
                navController.navigate(R.id.mainFragment);
                break;
            case "fragment_third":
                navController.navigate(R.id.mainFragment);
                break;
            case "fragment_info":
                navController.navigate(R.id.mainFragment);
                break;
            case "fragment_fourth":
                navController.navigate(R.id.secondFragment);
                break;
            case "fragment_first":
                navController.navigate(R.id.mainFragment);
                break;
            default:
                navController.navigate(R.id.mainFragment);
                break;
        }
    }
}