package com.sdg.infocar;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;


/*
 This activity shows a graphic with the speed data loaded.
 It also shows the average speed and the number of times
 the speed limit is exceeded.
 */
public class SpeedInfoActivity extends Activity {

    private final float SPEED_THRESHOLD = 120;

    private LineChart speed_graph;
    private TextView speed_average, speed_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        speed_graph = findViewById(R.id.speed_graph);
        speed_average = findViewById(R.id.speed_average_tv);
        speed_limit = findViewById(R.id.speed_limit_tv);

        // Loads received data from FilesActivity
        ArrayList<Double> speed = (ArrayList) getIntent().getSerializableExtra("speed");
        ArrayList<Integer> time = (ArrayList) getIntent().getSerializableExtra("time");

        setData(speed,time);
        speed_graph.animateX(1000);

    }

    /*
    Puts the received data in the graphic, and calculates
    the average speed and the number of times the speed
    limit is exceeded.
     */
    private void setData(ArrayList<Double> speed, ArrayList<Integer> time) {

        ArrayList<Entry> yAXES = new ArrayList<>();
        ArrayList<Entry> y_threshold = new ArrayList<>();

        double average_speed = 0;
        int speed_excessed_count = 0;
        boolean speed_excessed = false;

        for (int i = 0; i < speed.size(); i++) {
            float x_value = time.get(i);
            double y_value = speed.get(i);

            average_speed += y_value;

            if (y_value > SPEED_THRESHOLD && !speed_excessed) {
                speed_excessed_count++;
                speed_excessed = true;
            }
            else if (y_value < SPEED_THRESHOLD && speed_excessed)
                speed_excessed = false;

            yAXES.add(new Entry( x_value, (float) y_value ));

            if(i==0 || (i==speed.size()-1))
                y_threshold.add(new Entry( x_value, SPEED_THRESHOLD ));
        }
        average_speed /= speed.size();

        speed_average.setText(String.format("Average Speed: %.2f Km/h", average_speed));
        speed_limit.setText(String.format("Speed limit excessed %d times", speed_excessed_count));

        LineDataSet set, set_threshold;
        set = new LineDataSet(yAXES, "Speed");
        set_threshold = new LineDataSet(y_threshold, "Speed limit");

        set_threshold.setColor(Color.RED);
        set_threshold.setCircleColor(Color.RED);

        LineData data = new LineData(set, set_threshold);

        speed_graph.setData(data);
    }
}