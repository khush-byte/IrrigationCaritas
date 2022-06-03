package com.example.irrigationmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.irrigationmanager.tools.NetworkManager;

public class RegActivity extends AppCompatActivity {

    public EditText user_login, user_password, user_pinCode;
    public Button start_btn;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        user_login = findViewById(R.id.user_login);
        user_password = findViewById(R.id.user_password);
        user_pinCode = findViewById(R.id.user_pinCode);
        start_btn = findViewById(R.id.start_btn);
        activity = this;

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_btn.setBackgroundResource(R.drawable.btn_start_click);
                if (!user_login.getText().toString().equals("") && !user_password.getText().toString().equals("") && !user_pinCode.getText().toString().equals("")) {
                    if (user_pinCode.getText().toString().length() < 4) {
                        Toast.makeText(getBaseContext(), "ПИН код должен состоять минимум из 4-х цифр!", Toast.LENGTH_LONG).show();
                    } else {
                        if (user_pinCode.getText().toString().equals(user_password.getText().toString())) {
                            SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences("root_data", 0).edit();
                            editor.putString("login", user_login.getText().toString());
                            editor.putString("pin", user_pinCode.getText().toString());
                            editor.apply();

                            startActivity(new Intent(activity, MainActivity.class));
                            activity.finish();
                            Toast.makeText(getBaseContext(), "Регистрация прошла успешно!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getBaseContext(), "Оба ПИН кода должны совподать!", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(getBaseContext(), "Пожалуйста, заполните все поля!", Toast.LENGTH_LONG).show();
                }

                Handler handler = new Handler();
                Runnable refresh = () -> start_btn.setBackgroundResource(R.drawable.btn_start_bg);

                handler.postDelayed(refresh, 1000);
            }
        });
    }
}