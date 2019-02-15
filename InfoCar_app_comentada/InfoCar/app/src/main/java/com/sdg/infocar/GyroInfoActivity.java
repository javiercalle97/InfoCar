package com.sdg.infocar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/*
 This activity shows a graphic with the angular velocity
 data loaded. It also shows the number of times turning
 too fast.
 */
public class GyroInfoActivity extends Activity {

    private static final double TURNING_THRESHOLD = 50;

    private LineChart gyro_graph;
    private TextView turning_limit_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyro_info);

        gyro_graph = findViewById(R.id.gyro_graph);
        turning_limit_tv = findViewById(R.id.turning_limit_tv);

        // Loads received data from FilesActivity
        ArrayList<Double> gyro_x = (ArrayList) getIntent().getSerializableExtra("gyro_x");
        ArrayList<Double> gyro_z = (ArrayList) getIntent().getSerializableExtra("gyro_x");
        ArrayList<Integer> time = (ArrayList) getIntent().getSerializableExtra("time");

        setData(gyro_z,time);
        gyro_graph.animateX(1000);

    }

    /*
    Puts the received data in the graphic, and calculates
    the number of times turning too fast.
    */
    private void setData(ArrayList<Double> gyro_z, ArrayList<Integer> time) {

        ArrayList<Entry> yAXES = new ArrayList<>();

        int turning_excessed_count = 0;
        boolean turning_excessed = false;

        for (int i = 0; i < time.size(); i++) {
            float x_value = time.get(i);
            double y_value = gyro_z.get(i);

            if (y_value > TURNING_THRESHOLD && !turning_excessed) {
                turning_excessed_count++;
                turning_excessed = true;
            }
            else if (y_value < TURNING_THRESHOLD && turning_excessed)
                turning_excessed = false;

            yAXES.add(new Entry(x_value, (float) y_value));
        }

        turning_limit_tv.setText(String.format("Turning too fast (>50dps): %d times",
                turning_excessed_count));

        LineDataSet set;
        set = new LineDataSet(yAXES, "Angular velocity (dps)");


        LineData data = new LineData(set);

        gyro_graph.setData(data);
    }
}
