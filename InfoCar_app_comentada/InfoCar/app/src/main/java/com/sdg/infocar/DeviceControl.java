package com.sdg.infocar;


import com.idevicesinc.sweetblue.BleConnectionPriority;
import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.utils.Interval;
import com.idevicesinc.sweetblue.utils.Utils_Byte;
import java.util.UUID;

/*
 This class is used to communicate with the Raspberry Pi.
 It has a device singleton (the connected device), and the methods
 to read/write from/to its characteristics.
 */
public class DeviceControl {
    // Control Flags
    public static final int START = 0x01;
    public static final int STOP = 0x02;
    public static final int START_WRITE = 0x04;

    // Characteristic UUIDs
    private static final UUID CONTROL_UUID = UUID.fromString("00006001-0000-1000-8000-00805f9b34fb");
    private static final UUID ACC_X_UUID = UUID.fromString("00006010-0000-1000-8000-00805f9b34fb");
    private static final UUID ACC_Z_UUID = UUID.fromString("00006020-0000-1000-8000-00805f9b34fb");
    private static final UUID GYRO_X_UUID = UUID.fromString("00006030-0000-1000-8000-00805f9b34fb");
    private static final UUID GYRO_Z_UUID = UUID.fromString("00006040-0000-1000-8000-00805f9b34fb");
    private static final UUID TEMP_UUID = UUID.fromString("00006050-0000-1000-8000-00805f9b34fb");
    private static final UUID SPEED_UUID = UUID.fromString("00006060-0000-1000-8000-00805f9b34fb");
    private static final UUID[] UUIDS = {ACC_X_UUID, ACC_Z_UUID, GYRO_X_UUID, GYRO_Z_UUID, TEMP_UUID, SPEED_UUID};

    // Connected device singleton
    private static BleDevice device;

    // Read values
    private static volatile double acc_x = 0;
    private static volatile double acc_z = 0;
    private static volatile double gyro_x = 0;
    private static volatile double gyro_z = 0;
    private static volatile double temp = 0;
    private static volatile double speed = 0;

    /*
    Control function (write function).
    Sets the flag passed as parameter, to control the Raspberry Pi
     */
    public static void device_control(int control) {

        device.write(CONTROL_UUID, Utils_Byte.intToBytes(control));
    }


    /*
    Start to read the characteristics values every 100 ms.
     */
    public static void start_reading() {
        device.startPoll(UUIDS, Interval.millis(100), readEvent -> {
           if (readEvent.wasSuccess())
               read_data(readEvent);
        });
    }

    /*
    Stops to read the characteristics.
     */
    public static void stop_reading() {
        device.stopPoll(UUIDS);
    }

    /*
    Converts the received data to be human readable.
     */
    private static void read_data(BleDevice.ReadWriteListener.ReadWriteEvent readEvent) {
        if (readEvent.charUuid().equals(ACC_X_UUID))
            acc_x = 0.061 * (double) readEvent.data_int(false) / 1000;
        else if (readEvent.charUuid().equals(ACC_Z_UUID))
            acc_z = 0.061 * (double) readEvent.data_int(false) / 1000;
        else if (readEvent.charUuid().equals(GYRO_X_UUID))
            gyro_x = Math.abs(8.75 * (double) readEvent.data_int(false) / 1000);
        else if (readEvent.charUuid().equals(GYRO_Z_UUID))
            gyro_z = Math.abs(8.75 * (double) readEvent.data_int(false) / 1000);
        else if (readEvent.charUuid().equals(TEMP_UUID))
            temp = (0.1437 * (double) readEvent.data_int(false) + 168.86) / 8;
        else if (readEvent.charUuid().equals(SPEED_UUID))
            speed = 1.852 * (double) readEvent.data_int(false) / 100;
    }


    // Sets the connected device singleton.
    public static void setDevice(BleDevice d) {
        device = d;
        device.setConnectionPriority(BleConnectionPriority.HIGH);
    }
    // Gets the connected device singleton.
    public static BleDevice getDevice() {
        return device;
    }

    // Value getters

    public static double getAcc_x() {
        return acc_x;
    }

    public static double getAcc_z() {
        return acc_z;
    }

    public static double getGyro_x() {
        return gyro_x;
    }

    public static double getGyro_z() {
        return gyro_z;
    }

    public static double getTemp() {
        return temp;
    }

    public static double getSpeed() {
        return speed;
    }



}
