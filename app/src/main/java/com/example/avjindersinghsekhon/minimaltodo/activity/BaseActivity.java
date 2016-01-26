package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.avjindersinghsekhon.minimaltodo.AnalyticsApplication;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;

public class BaseActivity extends AppCompatActivity {

  protected AnalyticsApplication app;
  protected String theme;
  protected AnalyticsTracker tracker = AnalyticsTracker.INSTANCE;
  protected PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    app = (AnalyticsApplication) getApplication();
    theme = preferenceAccessor.getThemeSaved();
    if (theme.equals(PreferenceAccessor.DARKTHEME)) {
      setTheme(R.style.CustomStyle_DarkTheme);
    } else {
      setTheme(R.style.CustomStyle_LightTheme);
    }
    super.onCreate(savedInstanceState);
  }
}
