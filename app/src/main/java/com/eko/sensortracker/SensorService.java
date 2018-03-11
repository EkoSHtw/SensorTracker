package com.eko.sensortracker;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.getwandup.rxsensor.RxSensor;
import com.getwandup.rxsensor.domain.RxSensorEvent;

import java.util.HashMap;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Eko on 31.07.2017.
 */

public class SensorService extends Service {

    private static boolean running = false;
    HashMap<Integer, Subscription> sensors = new HashMap<>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(getBaseContext()).setContentText("Sensor Tracking").setSmallIcon(R.mipmap.ic_launcher).build();
        startForeground(startId, notification);
        registerSensors();
        running = true;
        Log.d("SensorService", "Log started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        unregisterSensors();
        SensorLogger.logAll();

    }

    private void unregisterSensors() {
        for (Integer integer : sensors.keySet()) {
            sensors.get(integer).unsubscribe();
        }
    }

    private void registerSensors(){
        registerSensor(Sensor.TYPE_GYROSCOPE);
        registerSensor(Sensor.TYPE_ACCELEROMETER);
        registerSensor(Sensor.TYPE_GRAVITY);
        registerSensor(Sensor.TYPE_MAGNETIC_FIELD);
        registerSensor(Sensor.TYPE_ROTATION_VECTOR);
        registerSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }


    private void registerSensor(final int type){
        RxSensor rxSensor = new RxSensor(this);
        Subscription subscription = rxSensor.observe(type, 2100000000)
                .subscribe(new Subscriber<RxSensorEvent>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(RxSensorEvent sensorEvent) {
                        SensorLogger.log(type, sensorEvent, getBaseContext());
                    }
                });
        sensors.put(type, subscription);
    }

    public static boolean isRunnning() {
        return running;
    }
}
