package com.example.avjindersinghsekhon.minimaltodo.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.DATA_Z;


public class ShakeSensorEventListener implements SensorEventListener {

  private static final int SHAKE_CHECK_THRESHOLD = 200;

  private final Callable<Void> listener;

  // How is last update distinct from the last shake point time?
  private long lastUpdate;

  private ShakeUtils.ShakePoint last = new ShakeUtils.ShakePoint(0, 0, 0, System.currentTimeMillis());

  private List<ShakeUtils.ShakePoint> shakePoints = new ArrayList<ShakeUtils.ShakePoint>();

  private float[] sensorValues;

  private long lastShakeDetected = -1;

  /**
   * @param listener Fired when a shake event that passes our threshholds is detected.
   */
  public ShakeSensorEventListener(Callable<Void> listener) {
    this.listener = listener;
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      handleShakeEvent(event);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // We ain't found shit!
  }

  private void handleShakeEvent(SensorEvent event) {

    sensorValues = ShakeUtils.lowPass(event.values.clone(), sensorValues);

    ShakeUtils.ShakePoint current = new ShakeUtils.ShakePoint(sensorValues[DATA_X], sensorValues[DATA_Y], sensorValues[DATA_Z],
        System.currentTimeMillis());

    // if a shake in last X seconds ignore.
    if (ShakeUtils.wasRecentlyShaken(lastShakeDetected, current.time)) {
      return;
    }

    if (ShakeUtils.wasStateChange(last, current)) {
      ShakeUtils.ShakePoint delta = new ShakeUtils.ShakePoint(last.x - current.x, last.y - current.y, last.z - current.z, current.time);

      shakePoints.add(delta);

      if ((current.time - lastUpdate) > SHAKE_CHECK_THRESHOLD) {
        lastUpdate = current.time;
        ShakeUtils.removeOldShakePoints(shakePoints);

        if (ShakeUtils.isShakeDetected(shakePoints)) {
          last = new ShakeUtils.ShakePoint(0, 0, 0, System.currentTimeMillis());
          lastShakeDetected = System.currentTimeMillis();
          shakePoints.clear();
          triggerShakeDetected();
        }
      }
    }

    last = current;
  }

  private synchronized void triggerShakeDetected() {
    if (listener != null) {
      try {
        listener.call();
      } catch (Exception e) {
        // Do nothing
      }
    }
  }
}
