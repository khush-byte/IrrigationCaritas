package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.minutes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.irrigationmanager.tools.MyArray;
import com.example.irrigationmanager.tools.NetworkManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button first_back;
    private Button first_send;
    private Button first_start;
    private Button first_stop;
    private TextView count_field;
    private TextView plot3_rec_min, text_status_plot3;
    ArrayList<MyArray> rec_data;
    boolean looped = true;
    int rec_min = 1;
    int min = 0;
    boolean isAlarm = false;
    MediaPlayer alarm;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
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
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        first_back = view.findViewById(R.id.first_back);
        first_send = view.findViewById(R.id.first_send);
        first_start = view.findViewById(R.id.first_start);
        first_stop = view.findViewById(R.id.first_stop);
        count_field = view.findViewById(R.id.count_field);
        plot3_rec_min = view.findViewById(R.id.plot3_rec_min);
        text_status_plot3 = view.findViewById(R.id.text_status_plot3);
        minutes = 0;
        rec_data = new ArrayList<>();

        SharedPreferences pref = view.getContext().getSharedPreferences("root_data", 0);
        String response = pref.getString("response", "");
        parseResponse(response);

        plot3_rec_min.setText(rec_min+" мин.");

        first_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stop();
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.mainFragment, null, navBuilder.build());
            }
        });

        first_stop.setEnabled(false);
        int state = rec_data.get(0).p3_state;

        if(state==1) {
            String sourceString = "Прошло дней: " + rec_data.get(0).count_days + "<br>Орошение: <b>необходимо орошение</b>";
            text_status_plot3.setText(Html.fromHtml(sourceString));
            plot3_rec_min.setText(rec_data.get(0).p3_need_min+ " мин.");
        }else{
            String sourceString = "Прошло дней: " + rec_data.get(0).count_days + "<br>Орошение: <b>орошени не нужно</b>";
            text_status_plot3.setText(Html.fromHtml(sourceString));
            plot3_rec_min.setText("0 мин.");
        }

        MotionLayout motionLayout = view.findViewById(R.id.motionLayout1);

        motionLayout.transitionToEnd();

        motionLayout.setTransitionListener(new MotionLayout.TransitionListener()
        {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1)
            {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v)
            {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i)
            {
                if(!looped)
                    motionLayout.transitionToStart();

                else
                    motionLayout.transitionToEnd();

                looped = !looped;
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v)
            {

            }
        });

        Handler handler = new Handler();
        final Runnable[] refresh = new Runnable[1];

        alarm = MediaPlayer.create(getContext(), R.raw.alarm);
        alarm.setLooping(true);

        first_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayout.transitionToStart();
                first_start.setEnabled(false);
                first_stop.setEnabled(true);
                first_send.setEnabled(false);

                final int[] num = {0};
                final String[] sec = {""};
                refresh[0] = new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        num[0]++;
                        if(num[0]==60){
                            min++;
                            num[0] = 0;
                        }
                        sec[0] = num[0]+"";
                        if(sec[0].length()<2) sec[0] = "0" + sec[0];
                        count_field.setText(min+" мин. "+sec[0]+" сек.");

                        if(rec_min!= 0 && rec_min == min && !isAlarm){
                            alarm.start();
                            isAlarm = true;
                        }

                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(refresh[0], 1000);
            }
        });

        first_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPump = true;
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.thirdFragment, null, navBuilder.build());
            }
        });

        first_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayout.transitionToEnd();
                handler.removeCallbacks(refresh[0]);
                isPump = true;
                minutes = min;
                alarm.stop();

                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.thirdFragment, null, navBuilder.build());
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