package com.sdg.infocar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.DeviceStateListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/*
 This activity starts a thread that reads the data sent by the Raspberry Pi
 (using DeviceControl methods), represents them on the screen and stores
 them in a file in the internal storage.
 It also provides in real time feedback, beeping and vibrating when something
 potentially dangerous happens.
 */
public class ObtainDataActivity extends Activity {

    private TextView acc_tv, gyro_tv, speed_tv, time_tv, temp_tv, error_tv;
    private Button stop_bt;

    // Controls the thread's loop
    private volatile boolean running = true;

    private String filename = "";
    private BufferedWriter bw;

    // Initial system time
    private long init_time;
    private int time = 0;

    private boolean sound = OptionsActivity.getSound();
    private boolean vibration = OptionsActivity.getVibration();
    private final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    private Vibrator vb;

    private boolean speed_alarm = false, temp_alarm = false, brake_alarm = false, turn_alarm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_data);


        speed_tv = findViewById(R.id.speed_tv);
        time_tv = findViewById(R.id.time_tv);
        temp_tv = findViewById(R.id.temp_tv);
        acc_tv = findViewById(R.id.acc_tv);
        gyro_tv = findViewById(R.id.gyro_tv);

        error_tv = findViewById(R.id.error_tv);
        stop_bt = findViewById(R.id.stop_bt);

        createFile();

        startReadingThread();

        setStopButton();

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /*
    Creates a new file, naming it with the actual date and time.
     */
    private void createFile() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        filename = df.format(c.getTime());

        File file = new File(ObtainDataActivity.this.getFilesDir(), filename);
        try {
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            error_tv.setText("Error creating file.");
        }
    }


    /*
    Receives and processes the data.
     */
    private void startReadingThread() {
        new Thread(new Runnable() {
            public void run() {
                init_time = System.currentTimeMillis(); // Initial time
                DeviceControl.start_reading(); // Starts reading the Raspberry Pi data.

                while (running) {
                    // time passed since start
                    time = (int) (System.currentTimeMillis() - init_time)/1000;

                    int seconds = time%60;
                    int minits = (time/60)%60;
                    int hours = time/3600;

                    // Shows the information on the screen
                    speed_tv.post(() -> {
                        speed_tv.setText(String.format("Speed:\n%.0f Km/h", DeviceControl.getSpeed()));
                        temp_tv.setText(String.format("Temperature: %.0f ºC", DeviceControl.getTemp()));
                        time_tv.setText(String.format("Travel Time: %02d:%02d:%02d", hours, minits, seconds));
                        acc_tv.setText(String.format("Acceleration (g):\nLinear %1.3f; Vertical %1.3f",
                                DeviceControl.getAcc_x(), DeviceControl.getAcc_z()));
                        gyro_tv.setText(String.format("Angular velocity (dps):\nLateral %3.1f; Transverse %3.1f",
                                DeviceControl.getGyro_z(), DeviceControl.getGyro_x()));
                        // Checks the data
                        alert();

                    });

                    try {
                        // Writes the data on a file
                        bw.write(String.format("%3.1f %2.1f %1.4f %1.4f %3.2f %3.2f %d\n", DeviceControl.getSpeed(),
                                DeviceControl.getTemp(), DeviceControl.getAcc_x(), DeviceControl.getAcc_z(),
                                DeviceControl.getGyro_x(), DeviceControl.getGyro_z(), time));
                        // Period: 100ms
                        Thread.sleep(100);
                    } catch (Exception e){
                        error_tv.post(() -> {
                           error_tv.setText("Error writing file.");
                        });
                    }
                }
            }
        }).start();
    }

    /*
    Stops the sampling, finishing the activity (executes onPause)
     */
    private void setStopButton() {
        stop_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
    }


    /*
    Checks the received data, looking for any dangerous values.
    If founded, calls soundAndVibrate, and sets that data to orange.
     */
    private void alert() {
        if (DeviceControl.getSpeed() > 120 && !speed_alarm) {
            speed_alarm = true;
            speed_tv.setTextColor(getResources().getColor(R.color.colorOrange));
            soundAndVibrate();
        } else if (speed_alarm && DeviceControl.getSpeed() < 115) {
            speed_alarm = false;
            speed_tv.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (DeviceControl.getTemp() > 32 && !temp_alarm) {
            temp_alarm = true;
            temp_tv.setTextColor(getResources().getColor(R.color.colorOrange));
            soundAndVibrate();
        } else if (temp_alarm && DeviceControl.getTemp() < 32) {
            temp_alarm = false;
            temp_tv.setTextColor(getResources().getColor(R.color.colorText));
        }

        if ((DeviceControl.getAcc_x() < 0.55) && !brake_alarm) {
            brake_alarm = true;
            acc_tv.setTextColor(getResources().getColor(R.color.colorOrange));
            soundAndVibrate();
        } else if (brake_alarm && DeviceControl.getAcc_x() > 0.3) {
            brake_alarm = false;
            acc_tv.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (Math.abs(DeviceControl.getGyro_z()) > 50 && !turn_alarm) {
            turn_alarm = true;
            gyro_tv.setTextColor(getResources().getColor(R.color.colorOrange));
            soundAndVibrate();
        } else if (turn_alarm && Math.abs(DeviceControl.getGyro_z()) < 30) {
            turn_alarm = false;
            gyro_tv.setTextColor(getResources().getColor(R.color.colorText));
        }

    }

    /*
    Beeps and vibrate (if activated in options).
     */
    private void soundAndVibrate() {
        if (sound)
            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
        if (vibration)
            vb.vibrate(500);
    }

    /*
    When the activity finish, it stops the reading, closes the file,
    and sends the STOP flag to the Raspberry.
     */
    @Override
    protected void onPause() {
        DeviceControl.stop_reading();
        running = false;
        try {
            bw.close();
        } catch (Exception e) {
            error_tv.setText("Error closing file.");
        }
        DeviceControl.device_control(DeviceControl.STOP);
        super.onPause();
    }
}
