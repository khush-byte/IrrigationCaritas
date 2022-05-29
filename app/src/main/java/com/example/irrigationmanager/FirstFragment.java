package com.example.irrigationmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.irrigationmanager.tools.NetworkManager;

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
    boolean looped = true;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        first_back = view.findViewById(R.id.first_back);
        first_send = view.findViewById(R.id.first_send);

        first_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions.Builder navBuilder =  new NavOptions.Builder();
                navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.mainFragment, null, navBuilder.build());
            }
        });


        final int[] num = {0};
        Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                num[0]++;
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(refresh, 1000);


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

        return view;
    }
}