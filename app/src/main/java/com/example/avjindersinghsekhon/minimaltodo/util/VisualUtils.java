package com.example.avjindersinghsekhon.minimaltodo.util;

import android.app.Activity;
import android.content.Intent;

import com.example.avjindersinghsekhon.minimaltodo.R;

public class VisualUtils {

  public static final String THEME_CHANGED = "THEME_CHANGED";

  // So, if Activity is active, we are restarting it by startActivity/finish, to get that nice fadeIn/fadeOut animation
  // If it's inactive, just call recreate to update theme on all elements.
  // You're probably wondering why can't we just call recreate() all the time? Well,
  // because it doesn't apply animations and it's ugly as fuck
  public static void restartActivity(Activity activity, boolean isActive) {
    if (isActive) {
      Intent i = activity.getIntent();
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      i.putExtra(THEME_CHANGED, true);
      activity.startActivity(i);
      activity.finish();
      activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    } else {
      activity.recreate();
    }
  }
}
