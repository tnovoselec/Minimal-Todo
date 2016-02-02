package com.example.avjindersinghsekhon.minimaltodo.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

public enum ShakeSensor {

  INSTANCE;

  private SensorManager sensorManager;

  public void init(Context context){
    this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
  }

  public void register(ShakeSensorEventListener listener) {
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(listener, sensor, SENSOR_DELAY_GAME);
  }


  public void unregister(ShakeSensorEventListener listener) {
    sensorManager.unregisterListener(listener);
  }
}
