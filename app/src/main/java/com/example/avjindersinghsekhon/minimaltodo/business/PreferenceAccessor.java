package com.example.avjindersinghsekhon.minimaltodo.business;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public enum PreferenceAccessor {
  INSTANCE;

  private static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
  private static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";

  private static final String EXIT = "com.avjindersekhon.exit";
  private static final String CHANGE_OCCURRED = "com.avjinder.changeoccured";
  private static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
  private static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
  public static final String DARKTHEME = "com.avjindersekon.darktheme";
  public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";

  private SharedPreferences sharedPreferences;
  private SharedPreferences themePreferences;

  public void init(Context context){
    sharedPreferences = context.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Activity.MODE_PRIVATE);
    themePreferences = context.getSharedPreferences(THEME_PREFERENCES, Activity.MODE_PRIVATE);
  }

  public void setExit(boolean exit){
    sharedPreferences.edit().putBoolean(EXIT, exit).apply();
  }

  public boolean getExit(){
    return sharedPreferences.getBoolean(EXIT, false);
  }

  public void setChangeOccurred(boolean changeOccurred){
    sharedPreferences.edit().putBoolean(CHANGE_OCCURRED, changeOccurred).apply();
  }

  public boolean getChangeOccurred(){
    return sharedPreferences.getBoolean(CHANGE_OCCURRED, false);
  }

  public void setRecreateActivity(boolean recreateActivity){
    themePreferences.edit().putBoolean(RECREATE_ACTIVITY, recreateActivity).apply();
  }

  public boolean getRecreateActivity(){
    return themePreferences.getBoolean(RECREATE_ACTIVITY, false);
  }

  public void setThemeSaved(String theme){
    themePreferences.edit().putString(THEME_SAVED, theme).apply();
  }

  public String getThemeSaved(){
    return themePreferences.getString(THEME_SAVED, LIGHTTHEME);
  }

  public void clearAll(){
    sharedPreferences.edit().clear().apply();
    themePreferences.edit().clear().apply();
  }
}
