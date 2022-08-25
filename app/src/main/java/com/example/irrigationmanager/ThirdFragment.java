package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.date;
import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.isSend;
import static com.example.irrigationmanager.MainActivity.minutes;
import static com.example.irrigationmanager.MainActivity.plot_number;
import static com.example.irrigationmanager.MainActivity.water_level;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button third_back;
    private Button third_send;
    private ConstraintLayout water_box;
    private TextView report_title, report_date, report_type, report_counter;
    private EditText report_min, report_tomson, report_person;
    private String ir_type;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        if(isSend){
            NavOptions.Builder navBuilder =  new NavOptions.Builder();
            NavHostFragment.findNavController(ThirdFragment.this)
                    .navigate(R.id.mainFragment, null, navBuilder.build());
        }

        third_back = view.findViewById(R.id.third_back);
        third_send = view.findViewById(R.id.third_send);
        water_box = view.findViewById(R.id.water_box);
        report_title = view.findViewById(R.id.report_title);
        report_date = view.findViewById(R.id.report_date);
        report_type = view.findViewById(R.id.report_type);
        report_min = view.findViewById(R.id.report_min);
        report_tomson = view.findViewById(R.id.report_tomson);
        report_person = view.findViewById(R.id.report_person);
        report_counter = view.findViewById(R.id.report_counter);

        report_title.setText("Тестовое поле №"+plot_number);
        report_date.setText("Время: " + date);

        SharedPreferences pref = view.getContext().getSharedPreferences("root_data", 0);
        String user_name = pref.getString("login", "");
        report_person.setText(user_name);

        if(minutes!=0) report_min.setText(minutes+"");
        report_tomson.setText(water_level+"");

        if(isPump){
            report_type.setText("Способ орошения: насос.");
            ir_type = "pump";
        }
        else {
            report_type.setText("Способ орошения: канал.");
            ir_type = "canal";
        }

        third_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(ThirdFragment.this)
                        .navigate(R.id.mainFragment, null, navBuilder.build());
            }
        });

        third_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(report_min.getText().toString().length()>0 && report_person.getText().toString().length()>0 && report_counter.getText().toString().length()>0){
                    int min = 0;
                    if(minutes==0) {
                        min = Integer.parseInt(report_min.getText().toString());
                    }else{
                        min=minutes;
                    }
                    sendData(plot_number,ir_type,min,water_level,report_person.getText().toString(), view);
                }
                else  Toast.makeText(view.getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            }
        });

        if(isPump){
            water_box.setVisibility(View.GONE);
        }else{
            water_box.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void sendData(int plot, String type, int minute, int w_level, String name, View v) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String sign = MD5(currentDate + "bCctS9eqoYaZl21a");
        third_send.setEnabled(false);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://wwcs.tj/meteo/irrigation/monitoring.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sign", sign);
                    jsonParam.put("plot", plot);
                    jsonParam.put("datetime", currentDate);
                    jsonParam.put("type", type);
                    jsonParam.put("minutes", minute);
                    jsonParam.put("w_level", w_level);
                    jsonParam.put("name", convertStringToUTF8(name));
                    jsonParam.put("counter", report_counter.getText().toString());

                    //Log.i("JSON", jsonParam.toString());
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

                    //Log.i("MSG" , sb.toString());
                    String result = sb.toString();

                    JSONObject obj = new JSONObject(result);

                    if(obj.getString("result").equals("0")) {
                        NavOptions.Builder navBuilder =  new NavOptions.Builder();
                        NavHostFragment.findNavController(ThirdFragment.this)
                            .navigate(R.id.infoFragment, null, navBuilder.build());
                    }else{
                        Toast.makeText(v.getContext(), "Сервер не отвечает! Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                        third_send.setEnabled(true);
                    }

                    conn.disconnect();
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

    public static String convertStringToUTF8(String s) {
        String out = null;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }
}