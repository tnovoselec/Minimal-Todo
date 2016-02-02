package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.business.ShakeManager;
import com.example.avjindersinghsekhon.minimaltodo.util.VisualUtils;

public class BaseActivity extends AppCompatActivity implements ShakeManager.ShakeDetectListener {

  protected String theme;
  protected AnalyticsTracker tracker = AnalyticsTracker.INSTANCE;
  protected PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;
  protected boolean isActive;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    theme = preferenceAccessor.getThemeSaved();
    if (theme.equals(PreferenceAccessor.DARKTHEME)) {
      setTheme(R.style.CustomStyle_DarkTheme);
    } else {
      setTheme(R.style.CustomStyle_LightTheme);
    }
    ShakeManager.INSTANCE.addListener(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    isActive = true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    isActive = false;
  }

  @Override
  protected void onDestroy() {
    ShakeManager.INSTANCE.removeListener(this);
    super.onDestroy();
  }

  @Override
  public void shakeDetected() {
    VisualUtils.restartActivity(this, true);
  }
}
