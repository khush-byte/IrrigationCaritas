package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.minutes;
import static com.example.irrigationmanager.MainActivity.water_level;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.irrigationmanager.tools.MyArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FourthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FourthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button fourth_back;
    private Button fourth_send;
    private TextView plot2_minute_field, text_status_plot2, plot2_rec_min;
    ArrayList<MyArray> rec_data;

    public FourthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FourthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FourthFragment newInstance(String param1, String param2) {
        FourthFragment fragment = new FourthFragment();
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
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        // Inflate the layout for this fragment

        fourth_back = view.findViewById(R.id.fourth_back);
        fourth_send = view.findViewById(R.id.fourth_send);
        plot2_minute_field = view.findViewById(R.id.plot2_minute_field);
        text_status_plot2 = view.findViewById(R.id.text_status_plot2);
        plot2_rec_min = view.findViewById(R.id.plot2_rec_min);
        rec_data = new ArrayList<>();

        SharedPreferences pref = view.getContext().getSharedPreferences("root_data", 0);
        String response = pref.getString("response", "");
        parseResponse(response);

        int state;
        int days;

        if(rec_data.size()!=0) {
            state = rec_data.get(0).p2_state;
            days = rec_data.get(0).count_days;
        }else{
            state = 0;
            days = 0;
        }

        if(state==1) {
            String sourceString = "Прошло дней: " + days+ "<br>Орошение: <b>необходимо орошение</b>";
            text_status_plot2.setText(Html.fromHtml(sourceString));
            plot2_rec_min.setText(minutes + " мин.");
        }else{
            String sourceString = "Прошло дней: " + days + "<br>Орошение: <b>орошени не нужно</b>";
            text_status_plot2.setText(Html.fromHtml(sourceString));
            plot2_rec_min.setText("0 мин.");
        }

        fourth_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(FourthFragment.this)
                        .navigate(R.id.secondFragment, null, navBuilder.build());
            }
        });

        fourth_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(plot2_minute_field.getText().toString().length()>0) {
                    isPump = false;
                    minutes = Integer.parseInt(plot2_minute_field.getText().toString());
                    NavOptions.Builder navBuilder = new NavOptions.Builder();
                    navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                    NavHostFragment.findNavController(FourthFragment.this)
                            .navigate(R.id.thirdFragment, null, navBuilder.build());
                }else{
                    Toast.makeText(view.getContext(), "Минуты не указаны!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void parseResponse(String response){
        try {
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
            Log.i("massive" , String.valueOf(rec_data.size()));
        } catch(Exception e){
            Log.e("Debug", "Error in parsing");
        }
    }
}