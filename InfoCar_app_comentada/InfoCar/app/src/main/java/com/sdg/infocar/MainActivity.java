package com.sdg.infocar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.utils.BluetoothEnabler;
import com.idevicesinc.sweetblue.utils.Interval;

/*
 MainActivity
 Is the principal activity, opened when the application starts.
 It permits connect to the Raspberry Pi, and then, start getting data
 from it, going into ObtainDataActivity.
 It also has two buttons to access OptionsActivity and FileActivity.
 */
public class MainActivity extends Activity {

    private Button start_bt, connect_bt, disconnect_bt, files_bt, options_bt;
    private TextView status_tv;

    private BleManager bleManager;
    private BleDevice device;
    private boolean discovered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get saved preferences and apply them to options.
        SharedPreferences sharedPref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        OptionsActivity.setSound(sharedPref.getBoolean("sound", true));
        OptionsActivity.setVibration(sharedPref.getBoolean("vibration", true));
        OptionsActivity.setWrite(sharedPref.getBoolean("write", false));


        start_bt = findViewById(R.id.start_bt);
        connect_bt = findViewById(R.id.connect_bt);
        disconnect_bt = findViewById(R.id.disconnect_bt);
        files_bt = findViewById(R.id.files_bt);
        options_bt = findViewById(R.id.options_bt);
        status_tv = findViewById(R.id.status_tv);

        ConnectButton();
        DisconnectButton();
        StartButton();
        FilesButton();
        OptionsButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (device != null && !device.is(BleDeviceState.INITIALIZED)) {
            connectToDevice();
        }

    }

    // Buttons setup

    /*
    Starts scanning for the Raspberry Pi. Disabled when pressed.
     */
    private void ConnectButton() {
        connect_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                connect_bt.setEnabled(false);
                startScan();
            }
        });
    }

    /*
    Disconnects from the connected device.
     */
    private void DisconnectButton() {
        disconnect_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                device.disconnect();
                connect_bt.setEnabled(true);
                start_bt.setEnabled(false);
                status_tv.setText("Disconnected. You can connect again by pressing connect.");
                discovered = false;
            }
        });
    }

    /*
    Sends a control flag to the Raspberry Pi to start getting data
    (and write it in a file, depending on whether the write opticion
    is activated), and then opens ObtainDataActivity.
     */
    private void StartButton() {
        start_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (OptionsActivity.getWrite())
                    DeviceControl.device_control(DeviceControl.START_WRITE);
                else
                    DeviceControl.device_control(DeviceControl.START);

                Intent intent = new Intent(MainActivity.this, ObtainDataActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Opens FilesActivity.
     */
    private void FilesButton() {
        files_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FilesActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    Opens OptionsActivity.
     */
    private void OptionsButton() {
        options_bt.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Bluetooth Low Energy methods

    /*
    Checks if the Bluetooth and Location Services are activated, and then starts
    scanning, passing discovered devices to processDiscoveryEvent.
     */
    private void startScan() {
        BleManagerConfig config = new BleManagerConfig();
        config.runOnMainThread = false;
        config.scanReportDelay = Interval.DISABLED;

        bleManager = BleManager.get(this, config);
        bleManager.setListener_Discovery(this::processDiscoveryEvent);

        BluetoothEnabler.start(this, new BluetoothEnabler.DefaultBluetoothEnablerFilter() {
            @Override public Please onEvent(BluetoothEnablerEvent bluetoothEnablerEvent) {
                Please please = super.onEvent(bluetoothEnablerEvent);

                if (bluetoothEnablerEvent.isDone() && bluetoothEnablerEvent.isEnabled(Stage.BLUETOOTH)
                        && bluetoothEnablerEvent.isEnabled(Stage.LOCATION_SERVICES)) {
                    bleManager.startScan();
                    status_tv.setText("Scanning...");
                }
                else if (bluetoothEnablerEvent.isDone()) {
                    status_tv.setText("Please, enable Bluetooth and Location Services and try again.");
                    connect_bt.setEnabled(true);
                }

                return please;
            }
        });
    }

    /*
    Determines if a discovered device is our Raspberry Pi (based on his MAC Address)
    and when it's found, executes connectToDevice.
     */
    private void processDiscoveryEvent(BleManager.DiscoveryListener.DiscoveryEvent discoveryEvent) {
        if (!discovered && discoveryEvent.device().getMacAddress().toLowerCase().equals("b8:27:eb:e1:cb:6b")) {
            discovered = true;
            bleManager.stopAllScanning();

            DeviceControl.setDevice(discoveryEvent.device());
            device = DeviceControl.getDevice();
            connectToDevice();
            status_tv.setText("Device discovered. Connecting...");
        }

    }

    /*
    Tries to connect to the device, discovering its GATT Service.
    If connection is successful, enables START button.
     */
    private void connectToDevice() {
        device.connect(new BleDevice.StateListener() {
            @Override public void onEvent(StateEvent stateEvent) {

                if(stateEvent.didEnter(BleDeviceState.INITIALIZED)) {
                    status_tv.setText("Connected and ready to start.");
                    connect_bt.setEnabled(false);
                    start_bt.setEnabled(true);
                    disconnect_bt.setEnabled(true);
                }
                if(stateEvent.didEnter(BleDeviceState.DISCONNECTED) && !device.is(BleDeviceState.RETRYING_BLE_CONNECTION)) {
                    status_tv.setText("Disconnected. You can connect again by pressing connect.");
                    connect_bt.setEnabled(true);
                    start_bt.setEnabled(false);
                    disconnect_bt.setEnabled(false);
                }
            }
        }, new BleDevice.DefaultConnectionFailListener() {
            @Override public Please onEvent(ConnectionFailEvent connectionFailEvent) {
                Please please = super.onEvent(connectionFailEvent);

                if(!please.isRetry()) {
                    status_tv.setText("Connection failure. Try again.");
                    connect_bt.setEnabled(true);
                    start_bt.setEnabled(false);
                    disconnect_bt.setEnabled(false);
                }


                return please;
            }
        });
    }

}
