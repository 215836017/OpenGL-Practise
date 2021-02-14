package com.cakes.demoopengl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private Intent intent = new Intent();

    public void mainClick(View view) {
        switch (view.getId()) {
            case R.id.main_activity_test_01:
                intent.setClass(this, TestActivity01.class);
                break;
        }

        startActivity(intent);
    }
}