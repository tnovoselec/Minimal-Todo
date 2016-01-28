package com.example.avjindersinghsekhon.minimaltodo;

import android.app.Application;

import com.example.avjindersinghsekhon.minimaltodo.business.AlarmHandler;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.business.StoreRetrieveData;

public class MinimalToDoApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AnalyticsTracker.INSTANCE.init(this);
    PreferenceAccessor.INSTANCE.init(this);
    StoreRetrieveData.INSTANCE.init(this);
    AlarmHandler.INSTANCE.init(this);
  }
}
