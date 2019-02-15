package com.sdg.infocar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
  This activity shows all the data files created, so they
  are accessible. When a file is selected, its data is loaded,
  and by pressing the bottom buttons, this data is showed
  to the user in the Info activities (Speed, Acceleration, Angle
  Velocity and Temperature)
 */
public class FilesActivity extends Activity {

    // The ListView will show the files
    private ListView listView;
    private TextView date_tv, duration_tv, number_tv;

    private File file;
    private BufferedReader br;

    // The data will be loaded here
    private ArrayList<Integer> time = new ArrayList();
    private ArrayList<Double> speed = new ArrayList();
    private ArrayList<Double> temp = new ArrayList();
    private ArrayList<Double> acc_x = new ArrayList();
    private ArrayList<Double> acc_z = new ArrayList();
    private ArrayList<Double> gyro_x = new ArrayList();
    private ArrayList<Double> gyro_z = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        listView = findViewById(R.id.listview);
        date_tv = findViewById(R.id.date_tv);
        duration_tv = findViewById(R.id.duration_tv);
        number_tv = findViewById(R.id.number_tv);

        setListView();

    }

    /*
    Puts the files in the ListView and set its listeners.
    When a file is pressed, its data will be loaded.
    When a file is long pressed, a pop up will ask
    for its deletion.
     */
    private void setListView() {
        getFiles();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String filename = (String) parent.getItemAtPosition(position);
                file = new File(FilesActivity.this.getFilesDir(), filename);
                int n = load_data();
                setDescription(n, filename);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                final String filename = (String) parent.getItemAtPosition(position);
                file = new File(FilesActivity.this.getFilesDir(), filename);

                new AlertDialog.Builder(FilesActivity.this)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete " + filename + " file?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                file.delete();
                                Toast.makeText(FilesActivity.this, "File deleted",
                                        Toast.LENGTH_SHORT).show();
                                getFiles();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });

    }

    /*
    Gets all stored files and puts them in the ListView.
     */
    private void getFiles() {
        ArrayList<String> myFiles = new ArrayList<String>();
        File f = FilesActivity.this.getFilesDir();
        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0) {
            //return;
        } else {
            for (int i = 0; i < files.length; i++)
                myFiles.add(files[i].getName());
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, myFiles);
        listView.setAdapter(adapter);
    }

    /*
    Put the file data in the arrays
     */
    private int load_data() {
        int n = 0;
        try {
            speed.clear();
            temp.clear();
            acc_x.clear();
            acc_z.clear();
            gyro_x.clear();
            gyro_z.clear();
            time.clear();

            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                parse_data(line);
                n++;
            }


        } catch (Exception e) {}
        return  n;
    }

    /*
    Separates the data of each line, putting it
    in its correspondent array.
     */
    private void parse_data(String data){
        String[] splitted_data = data.split(" ");

        speed.add(Double.parseDouble(splitted_data[0]));
        temp.add(Double.parseDouble(splitted_data[1]));
        acc_x.add(Double.parseDouble(splitted_data[2]));
        acc_z.add(Double.parseDouble(splitted_data[3]));
        gyro_x.add(Double.parseDouble(splitted_data[4]));
        gyro_z.add(Double.parseDouble(splitted_data[5]));
        time.add(Integer.parseInt(splitted_data[6]));
    }

    /*
    Shows information about the date and the time of the file, its duration
    and the number of samples it contains.
     */
    private void setDescription(int numberOfSamples, String filename) {
        String[] date_hour = filename.split("_");
        String[] date = date_hour[0].split("-");
        String[] hour = date_hour[1].split(":");

        int sec = time.isEmpty() ? 0 : time.get(time.size()-1);

        date_tv.setText(String.format("Date: %s-%s-%s, at %s:%s", date[2], date[1], date[0], hour[0], hour[1]));
        duration_tv.setText(String.format("Duration: %2d h, %2d min, %2d seg", sec/3600, (sec/60)%60, sec%60));
        number_tv.setText(String.format("Number of samples: %d", numberOfSamples));
    }

    /*
    Initializes SpeedInfoActivity, sending the speed data.
     */
    public void speedInfo(View v) {
        if (speed.size() == 0)
            return;

        Intent intent = new Intent(FilesActivity.this, SpeedInfoActivity.class);

        Bundle bundle_vel = new Bundle();
        bundle_vel.putSerializable("speed", speed);
        bundle_vel.putIntegerArrayList("time", time);
        intent.putExtras(bundle_vel);

        startActivity(intent);
    }

    /*
    Initializes TempInfoActivity, sending the temperature data.
     */
    public void tempInfo(View v) {
        if (temp.size() == 0)
            return;

        Intent intent = new Intent(FilesActivity.this, TempInfoActivity.class);

        Bundle bundle_vel = new Bundle();
        bundle_vel.putSerializable("temp", temp);
        bundle_vel.putIntegerArrayList("time", time);
        intent.putExtras(bundle_vel);

        startActivity(intent);
    }

    /*
    Initializes AccInfoActivity, sending the accelerations data.
     */
    public void accInfo(View v) {
        if (acc_x.size() == 0)
            return;

        Intent intent = new Intent(FilesActivity.this, AccInfoActivity.class);

        Bundle bundle_vel = new Bundle();
        bundle_vel.putSerializable("acc_x", acc_x);
        bundle_vel.putSerializable("acc_z", acc_z);
        bundle_vel.putIntegerArrayList("time", time);
        intent.putExtras(bundle_vel);

        startActivity(intent);
    }


    /*
    Initializes GyroInfoActivity, sending the angular velocity data.
     */
    public void gyroInfo(View v) {
        if (gyro_z.size() == 0)
            return;

        Intent intent = new Intent(FilesActivity.this, GyroInfoActivity.class);

        Bundle bundle_vel = new Bundle();
        bundle_vel.putSerializable("gyro_x", gyro_x);
        bundle_vel.putSerializable("gyro_z", gyro_z);
        bundle_vel.putIntegerArrayList("time", time);
        intent.putExtras(bundle_vel);

        startActivity(intent);
    }

    /*
    This auxiliary class is used to parse the ArrayList of files
    to feed the ListView.
     */
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
