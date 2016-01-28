package com.example.avjindersinghsekhon.minimaltodo.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import com.example.avjindersinghsekhon.minimaltodo.PreferenceKeys;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

  private AnalyticsTracker tracker = AnalyticsTracker.INSTANCE;
  private PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences_layout);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    PreferenceKeys preferenceKeys = new PreferenceKeys(getResources());
    if (key.equals(preferenceKeys.night_mode_pref_key)) {
      //We tell our MainLayout to recreate itself because mode has changed
      preferenceAccessor.setRecreateActivity(true);
      CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(preferenceKeys.night_mode_pref_key);
      if (checkBoxPreference.isChecked()) {
        //Comment out this line if not using Google Analytics
        tracker.send(this, AnalyticsTracker.SETTINGS, "Night Mode used");
        preferenceAccessor.setThemeSaved(PreferenceAccessor.DARKTHEME);
      } else {
        preferenceAccessor.setThemeSaved(PreferenceAccessor.LIGHTTHEME);
      }

      getActivity().recreate();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }
}
