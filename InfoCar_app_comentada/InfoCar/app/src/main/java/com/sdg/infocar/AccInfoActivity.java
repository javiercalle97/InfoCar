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
 This activity shows a graphic with the acceleration data loaded.
 It also shows the number of times braking too hard and
 passing bumps too fast.
 */
public class AccInfoActivity extends Activity {

    private static final double BRAKING_THRESHOLD = 0.55;
    private static final double POTHOLE_THRESHOLD = 0.7;

    private LineChart acc_graph;
    private TextView braking_limit_tv, potholes_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_info);

        acc_graph = findViewById(R.id.acc_graph);
        braking_limit_tv = findViewById(R.id.braking_limit_tv);
        potholes_tv = findViewById(R.id.pothole_tv);

        // Loads received data from FilesActivity
        ArrayList<Double> acc_x = (ArrayList) getIntent().getSerializableExtra("acc_x");
        ArrayList<Double> acc_z = (ArrayList) getIntent().getSerializableExtra("acc_z");

        ArrayList<Integer> time = (ArrayList) getIntent().getSerializableExtra("time");

        setData(acc_x, acc_z,time);
        acc_graph.animateX(1000);

    }


    /*
    Puts the received data in the graphic, and calculates the number
    of times braking too hard and passing bumps too fast.
    */
    private void setData(ArrayList<Double> acc_x, ArrayList<Double> acc_z, ArrayList<Integer> time) {

        ArrayList<Entry> yAXES_x = new ArrayList<>();
        ArrayList<Entry> yAXES_z = new ArrayList<>();
;
        int braking_excessed_count = 0;
        int potholes = 0;
        boolean braking_excessed = false;
        boolean pothole_excessed = false;

        for (int i = 0; i < time.size(); i++) {
            float x_value = time.get(i);
            double y_value_x = acc_x.get(i);
            double y_value_z = acc_z.get(i);

            if (y_value_x > BRAKING_THRESHOLD && !braking_excessed) {
                braking_excessed_count++;
                braking_excessed = true;
            }
            else if (y_value_x < BRAKING_THRESHOLD && braking_excessed)
                braking_excessed = false;

            if (y_value_z < POTHOLE_THRESHOLD && !pothole_excessed) {
                potholes++;
                pothole_excessed = true;
            }
            else if (y_value_z > POTHOLE_THRESHOLD && pothole_excessed)
                pothole_excessed = false;

            yAXES_x.add(new Entry( x_value, (float) y_value_x ));

            yAXES_z.add(new Entry( x_value, (float) y_value_z ));

        }

        braking_limit_tv.setText(String.format("Braking too strong (>0.55g): %d times", braking_excessed_count));
        potholes_tv.setText(String.format("%d potholes/speed bumps passed too fast (<0.7g)", potholes));


        LineDataSet set_x, set_z;
        set_x = new LineDataSet(yAXES_x, "X axis, Braking (g)");
        set_z = new LineDataSet(yAXES_z, "Z axis, Bumps (g) ");

        set_x.setColor(Color.GREEN);
        set_x.setCircleColor(Color.GREEN);

        set_z.setColor(Color.BLUE);
        set_z.setCircleColor(Color.BLUE);

        LineData data = new LineData(set_x, set_z);

        acc_graph.setData(data);
    }
}