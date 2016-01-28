package com.example.avjindersinghsekhon.minimaltodo.business;

import android.content.Context;
import android.content.pm.PackageManager;

import com.example.avjindersinghsekhon.minimaltodo.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

public enum AnalyticsTracker {

  INSTANCE;

  public static final String ACTION = "Action";
  public static final String SETTINGS = "Settings";
  private static final boolean IS_ENABLED = true;

  public Tracker tracker;

  public void init(Context context) {
    GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);

            /*R.xml.app_tracker contains my Analytics code
            To use this, go to Google Analytics, and get
            your code, create a file under res/xml , and save
            your code as <string name="ga_trackingId">UX-XXXXXXXX-Y</string>
            */

    tracker = analytics.newTracker(R.xml.global_tracker);
    tracker.setAppName("Minimal");
    tracker.enableExceptionReporting(true);
    try {
      tracker.setAppId(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void send(Object screenName) {
    send(screenName, new HitBuilders.ScreenViewBuilder().build());
  }

  private void send(Object screenName, Map<String, String> params) {
    if (IS_ENABLED) {
      tracker.setScreenName(getClassName(screenName));
      tracker.send(params);
    }
  }

  private String getClassName(Object o) {
    Class c = o.getClass();
    while (c.isAnonymousClass()) {
      c = c.getEnclosingClass();
    }
    return c.getSimpleName();

  }

  public void send(Object screenName, String category, String action) {
    send(screenName, new HitBuilders.EventBuilder().setCategory(category).setAction(action).build());
  }

  public void send(Object screenName, String category, String action, String label) {
    send(screenName, new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
  }
}
