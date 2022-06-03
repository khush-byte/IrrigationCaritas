package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.minutes;
import static com.example.irrigationmanager.MainActivity.plot_number;
import static com.example.irrigationmanager.MainActivity.water_level;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.irrigationmanager.tools.MyArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button second_back;
    private Button second_send;
    private ImageButton btn_down;
    private ImageButton btn_up;
    private TextView tomson_field;
    ArrayList<MyArray> rec_data;
    int index = 0;
    //String tomson[] = {"3", "4", "5", "6", "7", "8", "9", "10", "12", "14", "16", "18", "20", "25", "30"};
    ArrayList<MyArray> tomsons = new ArrayList<>();

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        second_back = view.findViewById(R.id.second_back);
        second_send = view.findViewById(R.id.second_send);
        btn_down = view.findViewById(R.id.btn_down);
        btn_up = view.findViewById(R.id.btn_up);
        tomson_field = view.findViewById(R.id.tomson_field);
        rec_data = new ArrayList<>();

        SharedPreferences pref = view.getContext().getSharedPreferences("root_data", 0);
        String response = pref.getString("response", "");
        parseResponse(response);

        setTomson(3, 0.28);
        setTomson(4, 0.47);
        setTomson(5, 0.81);
        setTomson(6, 1.30);
        setTomson(7, 1.90);
        setTomson(8, 2.60);
        setTomson(9, 3.50);
        setTomson(10, 4.55);
        setTomson(12, 7.14);
        setTomson(14, 10.45);
        setTomson(16, 14.54);
        setTomson(18, 19.43);
        setTomson(20, 25.29);
        setTomson(25, 43.82);
        setTomson(30, 68.7);
        setTomson(35, 100.4);

        second_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.mainFragment, null, navBuilder.build());
            }
        });

        second_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);

                water_level = tomsons.get(index).num;

                if(plot_number == 2) {
                    /*second_send.setText("ЖДИТЕ...");
                    second_send.setEnabled(false);
                    ((MainActivity) requireActivity()).getRecPlot2();*/

                    minutes = countMinutes(tomsons.get(index).value, water_level);
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.fourthFragment, null, navBuilder.build());
                }else{
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.thirdFragment, null, navBuilder.build());

                }
            }
        });

        tomson_field.setText(tomsons.get(index).num + " см.");
        btn_up.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //btn_down.setBackgroundResource(R.drawable.background_down1);
                    btn_up.setBackgroundResource(R.drawable.background_up2);
                    if(index < tomsons.size()-1) index++;
                    tomson_field.setText(tomsons.get(index).num + " см.");
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //btn_down.setBackgroundResource(R.drawable.background_down1);
                    btn_up.setBackgroundResource(R.drawable.background_up1);
                }
                return false;
            }
        });

        btn_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //btn_down.setBackgroundResource(R.drawable.background_down1);
                    btn_down.setBackgroundResource(R.drawable.background_down2);

                    if(index > 0) index--;
                    else index = 0;
                    tomson_field.setText(tomsons.get(index).num + " см.");
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //btn_down.setBackgroundResource(R.drawable.background_down1);
                    btn_down.setBackgroundResource(R.drawable.background_down1);
                }
                return false;
            }
        });

        return view;
    }

    private void setTomson(int num, double value){
        MyArray array = new MyArray();
        array.num = num;
        array.value = value;
        tomsons.add(array);
    }

    private int countMinutes(double value, int level){
        if(value!=0){
            Log.i("Debug", value+"; "+level);
            double m3_min = value * 60 / 1000;
            int m3_need = rec_data.get(0).p2_need_m3;
            double minute_need = m3_need / m3_min;
            return (int)Math.ceil(minute_need);
        }else return 0;
    }

    private void parseResponse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject fromJson = array.getJSONObject(i);
                MyArray data = new MyArray();
                data.select_date = fromJson.getString("select_date");
                data.count_days = fromJson.getInt("count_days");
                data.p2_state = fromJson.getInt("p2_state");
                data.p3_state = fromJson.getInt("p3_state");
                data.p2_need_m3 = fromJson.getInt("p2_irrig_need_mm");
                rec_data.add(data);
            }
            Log.i("massive", String.valueOf(rec_data.size()));
        } catch (Exception e) {
            Log.e("Debug", "Error in parsing");
        }
    }
}