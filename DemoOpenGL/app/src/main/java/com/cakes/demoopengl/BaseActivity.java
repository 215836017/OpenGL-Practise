package com.cakes.demoopengl;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cakes.demoopengl.utils.LogUtil;

public class BaseActivity extends AppCompatActivity {

    public void showToast(String tag, String msg) {
        LogUtil.d(tag, "showToast() -- msg: " + msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
