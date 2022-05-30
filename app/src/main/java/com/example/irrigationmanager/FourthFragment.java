package com.example.irrigationmanager;

import static com.example.irrigationmanager.MainActivity.isPump;
import static com.example.irrigationmanager.MainActivity.minutes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView plot2_minute_field;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        // Inflate the layout for this fragment

        fourth_back = view.findViewById(R.id.fourth_back);
        fourth_send = view.findViewById(R.id.fourth_send);
        plot2_minute_field = view.findViewById(R.id.plot2_minute_field);

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
}