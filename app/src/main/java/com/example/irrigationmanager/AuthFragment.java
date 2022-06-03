package com.example.irrigationmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.irrigationmanager.tools.NetworkManager;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthFragment extends Fragment implements View.OnTouchListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String pin = "";
    SharedPreferences pref;

    private Button[] btn = new Button[9];
    private Button btn_unfocus;
    private TextView pin_text;
    private int[] btn_id = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

    public AuthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AuthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthFragment newInstance(String param1, String param2) {
        AuthFragment fragment = new AuthFragment();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        pref = view.getContext().getSharedPreferences("root_data", 0);

        for(int i = 0; i < btn.length; i++){
            btn[i] = (Button) view.findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.button_on);
            btn[i].setOnTouchListener(this);
        }

        btn_unfocus = btn[0];
        pin_text = view.findViewById(R.id.pin_text);
        pin_text.setTextColor(Color.parseColor("#626363"));
        return view;
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(Color.rgb(255, 255, 255));
        //btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_unfocus.setBackgroundResource(R.drawable.button_on);
        btn_focus.setTextColor(Color.rgb(49, 50, 51));
        btn_focus.setBackgroundResource(R.drawable.button_off);
        this.btn_unfocus = btn_focus;
    }

    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.btn1:
                    setFocus(btn_unfocus, btn[0]);
                    enterPin("1");
                    break;

                case R.id.btn2:
                    setFocus(btn_unfocus, btn[1]);
                    enterPin("2");
                    break;

                case R.id.btn3:
                    setFocus(btn_unfocus, btn[2]);
                    enterPin("3");
                    break;

                case R.id.btn4:
                    setFocus(btn_unfocus, btn[3]);
                    enterPin("4");
                    break;

                case R.id.btn5:
                    setFocus(btn_unfocus, btn[4]);
                    enterPin("5");
                    break;

                case R.id.btn6:
                    setFocus(btn_unfocus, btn[5]);
                    enterPin("6");
                    break;

                case R.id.btn7:
                    setFocus(btn_unfocus, btn[6]);
                    enterPin("7");
                    break;

                case R.id.btn8:
                    setFocus(btn_unfocus, btn[7]);
                    enterPin("8");
                    break;

                case R.id.btn9:
                    setFocus(btn_unfocus, btn[8]);
                    enterPin("9");
                    break;
            }
        }if (event.getAction() == MotionEvent.ACTION_UP) {
            for (Button button : btn) {
                button.setBackgroundResource(R.drawable.button_on);
                button.setTextColor(Color.rgb(255, 255, 255));
            }
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void checkPin(String _pin){
        String check_pin = pref.getString("pin", "");

        if(_pin.equals(check_pin)) {
            pin = "";
            //Navigation.findNavController(requireActivity(),R.id.nav_host_main).navigate(R.id.mainFragment);
            // close this activity
            NavOptions.Builder navBuilder =  new NavOptions.Builder();
            navBuilder.setExitAnim(R.anim.exit).setEnterAnim(R.anim.enter);
            NavHostFragment.findNavController(AuthFragment.this)
                    .navigate(R.id.mainFragment, null, navBuilder.build());
        }
        else
        {
            pin_text.setTextColor(Color.parseColor("#F44336"));
            pin_text.setTextSize(28);
            pin_text.setText("не верно\nпопробуйте еще раз\n----");
            pin = "";
        }
    }

    private void enterPin(String num){
        pin_text.setTextSize(64);
        pin = pin.replace("-","");
        pin_text.setTextColor(Color.parseColor("#626363"));
        if(pin.length()==0){
            pin+=num;
            pin+="---";
            pin_text.setText(pin);
        }else if(pin.length()==1){
            pin+=num;
            pin+="--";
            pin_text.setText(pin);
        }else if(pin.length()==2){
            pin+=num;
            pin+="-";
            pin_text.setText(pin);
        }
        else if(pin.length()==3){
            pin+=num;
            pin_text.setText(pin);
            checkPin(pin_text.getText().toString());
        }
    }
}