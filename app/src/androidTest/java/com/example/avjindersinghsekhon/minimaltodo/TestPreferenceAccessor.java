package com.example.avjindersinghsekhon.minimaltodo;

import android.test.ActivityUnitTestCase;

import com.example.avjindersinghsekhon.minimaltodo.activity.MainActivity;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;

public class TestPreferenceAccessor extends ActivityUnitTestCase<MainActivity> {

  private PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;

  public TestPreferenceAccessor() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    preferenceAccessor.init(getInstrumentation().getTargetContext());
    preferenceAccessor.clearAll();
  }

  public void testExit(){
    assertEquals(preferenceAccessor.getExit(), false);

    preferenceAccessor.setExit(true);

    assertEquals(preferenceAccessor.getExit(), true);
  }

  public void testChangeOccurred(){
    assertEquals(preferenceAccessor.getChangeOccurred(), false);

    preferenceAccessor.setChangeOccurred(true);

    assertEquals(preferenceAccessor.getChangeOccurred(), true);
  }


  public void testRecreateActivity(){
    assertEquals(preferenceAccessor.getRecreateActivity(), false);

    preferenceAccessor.setRecreateActivity(true);

    assertEquals(preferenceAccessor.getRecreateActivity(), true);
  }


  public void testTheme(){
    assertEquals(preferenceAccessor.getThemeSaved(), PreferenceAccessor.LIGHTTHEME);

    preferenceAccessor.setThemeSaved(PreferenceAccessor.DARKTHEME);

    assertEquals(preferenceAccessor.getThemeSaved(), PreferenceAccessor.DARKTHEME);
  }
}
