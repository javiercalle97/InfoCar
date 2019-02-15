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
 This activity shows a graphic with the temperature data loaded.
 It also shows the average temperature.
 */
public class TempInfoActivity extends Activity {

    private LineChart temp_graph;
    private TextView temp_average;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_info);

        temp_graph = findViewById(R.id.temp_graph);
        temp_average = findViewById(R.id.temp_average_tv);

        // Loads received data from FilesActivity
        ArrayList<Double> temp = (ArrayList) getIntent().getSerializableExtra("temp");
        ArrayList<Integer> time = (ArrayList) getIntent().getSerializableExtra("time");

        setData(temp, time);
        temp_graph.animateX(1000);

    }

    /*
    Puts the received data in the graphic, and calculates
    the average temperature.
     */
    private void setData(ArrayList<Double> temp, ArrayList<Integer> time) {

        ArrayList<Entry> yAXES = new ArrayList<>();

        double average_temp = 0;

        for (int i = 0; i < temp.size(); i++) {
            float x_value = time.get(i);
            double y_value = temp.get(i);

            average_temp += y_value;
            yAXES.add(new Entry( x_value, (float) y_value ));
        }

        average_temp /= temp.size();

        temp_average.setText(String.format("Average Temperature: %.1f ºC", average_temp));

        LineDataSet set;
        set = new LineDataSet(yAXES, "Temperature (ºC)");

        set.setColor(Color.GRAY);
        set.setCircleColor(Color.GRAY);

        LineData data = new LineData(set);

        temp_graph.setData(data);
    }
}
