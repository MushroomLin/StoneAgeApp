package com.example.miniresearchdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;



public class SettingActivity extends AppCompatActivity {
    SeekBar changeBrightness;
    float BackLightValue;
    Switch loginSwitch;
    SharedPreferences pref;  // 0 - for private mode
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);
            }
        }
        pref = getApplicationContext().getSharedPreferences("MyPref",0);
        editor = pref.edit();
        loginSwitch = findViewById(R.id.SigninSwitch);
        changeBrightness = findViewById(R.id.BrightnessProgressBar);
        changeBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BackLightValue = (float)progress/100;
                if (Settings.System.canWrite(getApplicationContext())) {
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    layoutParams.screenBrightness = BackLightValue;
                    getWindow().setAttributes(layoutParams);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Settings.System.canWrite(getApplicationContext())) {
                    int SysBackLightValue = (int) (BackLightValue * 255);
                    android.provider.Settings.System.putInt(getContentResolver(),
                            android.provider.Settings.System.SCREEN_BRIGHTNESS,
                            SysBackLightValue);
//                    Toast.makeText(SettingActivity.this,"Regular Mode",Toast.LENGTH_SHORT).
                }
            }
        });
        loginSwitch.setChecked(pref.getBoolean("checked",false));
        loginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked == true) {
                    editor.putBoolean("checked",true);
                    editor.commit();
                }
                else {
                    editor.putBoolean("checked", false);
                    editor.commit();
                }
            }
        });



    }
}
