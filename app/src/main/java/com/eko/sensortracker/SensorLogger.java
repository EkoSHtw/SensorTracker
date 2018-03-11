package com.eko.sensortracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.getwandup.rxsensor.domain.RxSensorEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Eko on 31.07.2017.
 */

public class SensorLogger {

    private static HashMap<Integer, ArrayList<RxSensorEvent>> localStorage = new HashMap<>();
    private static Context context;

    public static void log(int sensorType, RxSensorEvent sensorEvent, Context con) {
        ArrayList<RxSensorEvent> rxSensorEvents = localStorage.get(sensorType);
        context = con;
        if (rxSensorEvents == null) {
            rxSensorEvents = new ArrayList<>();
        }
        rxSensorEvents.add(sensorEvent);

        if (rxSensorEvents.size() > 10000) {
            saveToStorage(rxSensorEvents, sensorType);
            localStorage.remove(sensorType);
        } else {
            localStorage.put(sensorType, rxSensorEvents);
        }

    }

    static Object lock = new Object();

    private static void saveToStorage(ArrayList<RxSensorEvent> rxSensorEvents, final int sensorType) {
        List<RxSensorEvent> tempList = new ArrayList<>(rxSensorEvents);



        new AsyncTask<List<RxSensorEvent>, Void, Void>() {
            @Override
            protected Void doInBackground(List<RxSensorEvent>... params) {
                List<RxSensorEvent> list = params[0];

                String typeName = "";
                switch (sensorType) {
                    case Sensor.TYPE_ACCELEROMETER:
                        typeName = "Accelerometer";
                        break;
                    case Sensor.TYPE_GRAVITY:
                        typeName = "Gravity";
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        typeName = "Gyroscope";
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        typeName = "Linear Acceleration";
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        typeName = "Rotation Vector";
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        typeName = "Magnetic Field";
                        break;
                    default:
                        typeName = "Unknown sensor";
                }

                typeName += System.currentTimeMillis();

                synchronized (lock){

                    File root = Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath(), "/SensorLogger");
                    dir.mkdirs();
                    File sensorlog = new File(dir, typeName +".txt");
                    Uri contentUri = Uri.fromFile(sensorlog);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(contentUri);
                    context.sendBroadcast(mediaScanIntent);
                    try {
                        OutputStream outputStream = new FileOutputStream(sensorlog);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
                        String content = "";
                        //save file
                        for (RxSensorEvent rxSensorEvent : list) {
                            Log.d(typeName, rxSensorEvent.toString());
                            content += typeName + ": " + rxSensorEvent.toString() + "\n";
                        }
                        myOutWriter.write(content);

                        myOutWriter.flush();
                        myOutWriter.close();
                        outputStream.flush();
                        outputStream.close();

                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.doInBackground(tempList);

    }

    public static void logAll() {
        for (Integer integer : localStorage.keySet()) {
            saveToStorage(localStorage.get(integer), integer);
        }

    }
}
