package com.example.avjindersinghsekhon.minimaltodo.business;


import com.example.avjindersinghsekhon.minimaltodo.AppStateTracker;
import com.example.avjindersinghsekhon.minimaltodo.shake.ShakeSensor;
import com.example.avjindersinghsekhon.minimaltodo.shake.ShakeSensorEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Keeps track of all the activities that want to respond to shake events. It does this by registering a "master"
 * listener with the ShakeSensor, which will in turn trigger all the listeners registered here.
 */
public enum ShakeManager {

  INSTANCE;

  public interface ShakeDetectListener {

    void shakeDetected();
  }

  private final ShakeSensor sensor = ShakeSensor.INSTANCE;
  private final PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;
  private final AppStateTracker appStateTracker = AppStateTracker.INSTANCE;

  /**
   * Master listener. Responds to sensor events.
   */
  private final ShakeSensorEventListener sensorEventListener = new ShakeSensorEventListener(sensorListener());
  /**
   * Shake listeners. Triggered by master sensor event listener.
   */
  private final List<ShakeDetectListener> listeners = new ArrayList<ShakeDetectListener>();


  public synchronized void addListener(ShakeDetectListener listener) {
    if (listeners.isEmpty()) {
      sensor.register(sensorEventListener);
    }

    listeners.add(listener);
  }

  public synchronized void removeListener(ShakeDetectListener listener) {
    listeners.remove(listener);
    if (listeners.isEmpty()) {
      sensor.unregister(sensorEventListener);
    }
  }


  private Callable<Void> sensorListener() {
    return new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        if (appStateTracker.getActiveActivitiesCount() > 0) {
          String activeTheme = preferenceAccessor.getThemeSaved();
          preferenceAccessor.setThemeSaved(activeTheme.equals(PreferenceAccessor.DARKTHEME) ? PreferenceAccessor.LIGHTTHEME : PreferenceAccessor.DARKTHEME);
          for (ShakeDetectListener listener : listeners) {
            listener.shakeDetected();
          }
        }
        return null;
      }
    };
  }
}
