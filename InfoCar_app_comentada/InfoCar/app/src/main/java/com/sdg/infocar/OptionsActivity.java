package com.sdg.infocar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


/*
 This activity contains the app options.
 This options are saved when the app is closed.
 */
public class OptionsActivity extends Activity {

    private SharedPreferences sharedPref;

    //Values of the options
    private static boolean sound;
    private static boolean vibration;
    private static boolean write;

    private ToggleButton sound_bt, vibration_bt, write_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sharedPref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        sound_bt = findViewById(R.id.sound_option_bt);
        vibration_bt = findViewById(R.id.vibrate_option_bt);
        write_bt = findViewById(R.id.write_option_bt);

        sound_bt.setChecked(sound);
        vibration_bt.setChecked(vibration);
        write_bt.setChecked(write);

        setButtons();
    }


    /*
     When an option button is pressed, it actualizes its value
     and saves it in the SharedPreferences.
     */
    private void setButtons() {
        sound_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sound = b;
                SharedPreferences.Editor e = sharedPref.edit();
                e.putBoolean("sound", b);
                e.apply();
            }
        });

        vibration_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vibration = b;
                SharedPreferences.Editor e = sharedPref.edit();
                e.putBoolean("vibration", b);
                e.apply();
            }
        });

        write_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                write = b;
                SharedPreferences.Editor e = sharedPref.edit();
                e.putBoolean("write", b);
                e.apply();
            }
        });
    }


    // Getters and Setters

    public static boolean getSound() {
        return sound;
    }

    public static boolean getVibration() {
        return vibration;
    }

    public static boolean getWrite() {
        return write;
    }

    public static void setSound(boolean sound) {
        OptionsActivity.sound = sound;
    }

    public static void setVibration(boolean vibration) {
        OptionsActivity.vibration = vibration;
    }

    public static void setWrite(boolean write) {
        OptionsActivity.write = write;
    }
}
