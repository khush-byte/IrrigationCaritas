package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.isSend;
import static com.example.irrigationmanager.MainActivity.minutes;
import static com.example.irrigationmanager.MainActivity.plot_number;
import static com.example.irrigationmanager.MainActivity.water_level;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;

import com.example.irrigationmanager.tools.MyArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ConstraintLayout btn_plot1;
    private ConstraintLayout btn_plot2;
    private ConstraintLayout btn_plot3;
    private TextView state_field2, state_field3;
    ArrayList<MyArray> rec_data;
    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btn_plot1 = view.findViewById(R.id.plot_one);
        btn_plot2 = view.findViewById(R.id.plot_two);
        btn_plot3 = view.findViewById(R.id.plot_three);
        state_field2 = view.findViewById(R.id.state_field2);
        state_field3 = view.findViewById(R.id.state_field3);
        isSend = false;
        isPump = false;
        rec_data = new ArrayList<>();

        SharedPreferences pref = view.getContext().getSharedPreferences("root_data", 0);
        String response = pref.getString("response", "");
        Log.e("Debug", response);
        parseResponse(response);

        int state1 = rec_data.get(0).p2_state;
        int state2 = rec_data.get(0).p3_state;

        if(state1==1) {
            state_field2.setText("Необходимо орошение!");
            state_field2.setTextColor(Color.RED);
            btn_plot2.setBackgroundResource(R.drawable.plot_apply);
        }else{
            state_field2.setText("Орошение не нужно!");
            state_field2.setTextColor(Color.GRAY);
            btn_plot2.setBackgroundResource(R.drawable.plot_okey);
        }

        if(state2==1) {
            state_field3.setText("Необходимо орошение!");
            state_field3.setTextColor(Color.RED);
            btn_plot3.setBackgroundResource(R.drawable.plot_apply);
        }else{
            state_field3.setText("Орошение не нужно!");
            state_field3.setTextColor(Color.GRAY);
            btn_plot3.setBackgroundResource(R.drawable.plot_okey);
        }

        btn_plot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPump = false;
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.firstFragment, null, navBuilder.build());
            }
        });

        btn_plot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_number = 1;
                minutes = 0;
                water_level = 0;
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.secondFragment, null, navBuilder.build());
            }
        });

        btn_plot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_number = 2;
                minutes = 0;
                water_level = 0;
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.secondFragment, null, navBuilder.build());
            }
        });

        btn_plot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plot_number = 3;
                water_level = 0;
                minutes = 0;
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.firstFragment, null, navBuilder.build());
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