package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.isSend;
import static com.example.irrigationmanager.MainActivity.plot_number;
import static com.example.irrigationmanager.MainActivity.water_level;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        isSend = false;
        isPump = false;

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
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.firstFragment, null, navBuilder.build());
            }
        });

        return view;
    }
}