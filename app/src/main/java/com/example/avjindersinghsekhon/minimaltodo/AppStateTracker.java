package com.example.avjindersinghsekhon.minimaltodo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public enum AppStateTracker implements Application.ActivityLifecycleCallbacks {

  INSTANCE;

  private int activeActivities = 0;

  @Override
  public void onActivityCreated(Activity activity, Bundle bundle) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
    incrementActiveActivities();
  }

  @Override
  public void onActivityResumed(Activity activity) {

  }

  @Override
  public void onActivityPaused(Activity activity) {

  }

  @Override
  public void onActivityStopped(Activity activity) {
    decrementActiveActivities();
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {

  }


  private synchronized void incrementActiveActivities() {
    activeActivities++;
  }

  private synchronized void decrementActiveActivities() {
    activeActivities--;
  }

  public int getActiveActivitiesCount(){
    return activeActivities;
  }

}
