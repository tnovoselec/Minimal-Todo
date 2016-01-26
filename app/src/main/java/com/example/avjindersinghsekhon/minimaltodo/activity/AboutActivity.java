package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.avjindersinghsekhon.minimaltodo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

  @Bind(R.id.aboutVersionTextView)
  TextView mVersionTextView;
  @Bind(R.id.toolbar)
  Toolbar toolbar;

  private String appVersion = "0.1";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tracker.send(this);
    setContentView(R.layout.about_layout);
    ButterKnife.bind(this);

    final Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    if (backArrow != null) {
      backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    getAppVersion();

    mVersionTextView.setText(String.format(getResources().getString(R.string.app_version), appVersion));

    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(backArrow);
    }
  }

  private void getAppVersion(){
    try {
      PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
      appVersion = info.versionName;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @OnClick(R.id.aboutContactMe)
  public void onContactMeClicked() {
    tracker.send(this, "Action", "Feedback");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (NavUtils.getParentActivityName(this) != null) {
          NavUtils.navigateUpFromSameTask(this);
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
