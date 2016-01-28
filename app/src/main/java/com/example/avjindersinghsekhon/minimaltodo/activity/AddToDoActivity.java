package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.util.DateUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class AddToDoActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

  @Bind(R.id.toDoHasDateSwitchCompat)
  SwitchCompat mToDoDateSwitch;
  @Bind(R.id.toDoEnterDateLinearLayout)
  LinearLayout mUserDateSpinnerContainingLinearLayout;
  @Bind(R.id.newToDoDateTimeReminderTextView)
  TextView mReminderTextView;
  @Bind(R.id.toolbar)
  Toolbar mToolbar;
  @Bind(R.id.userToDoEditText)
  EditText mToDoTextBodyEditText;
  @Bind(R.id.newTodoDateEditText)
  EditText mDateEditText;
  @Bind(R.id.newTodoTimeEditText)
  EditText mTimeEditText;
  @Bind(R.id.userToDoReminderIconImageButton)
  ImageButton reminderIconImageButton;
  @Bind(R.id.userToDoRemindMeTextView)
  TextView reminderRemindMeTextView;

  private ToDoItem mUserToDoItem;

  private String mUserEnteredText;
  private boolean mUserHasReminder;
  private Date mUserReminderDate;
  private int mUserColor;

  @Override
  protected void onResume() {
    super.onResume();
    tracker.send(this);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_todo_test);
    ButterKnife.bind(this);

    setupToolbar();

    mUserToDoItem = (ToDoItem) getIntent().getSerializableExtra(MainActivity.TODOITEM);

    mUserEnteredText = mUserToDoItem.getToDoText();
    mUserHasReminder = mUserToDoItem.hasReminder();
    mUserReminderDate = mUserToDoItem.getToDoDate();
    mUserColor = mUserToDoItem.getTodoColor();

    if (theme.equals(PreferenceAccessor.DARKTHEME)) {
      reminderIconImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_add_white_24dp));
      reminderRemindMeTextView.setTextColor(Color.WHITE);
    }

    if (mUserHasReminder && (mUserReminderDate != null)) {
      setReminderTextView();
      setEnterDateLayoutVisibleWithAnimations(true);
    }
    if (mUserReminderDate == null) {
      mToDoDateSwitch.setChecked(false);
      mReminderTextView.setVisibility(View.INVISIBLE);
    }

    mToDoTextBodyEditText.requestFocus();
    mToDoTextBodyEditText.setText(mUserEnteredText);
    InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    mToDoTextBodyEditText.setSelection(mToDoTextBodyEditText.length());

    setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

    mToDoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));

    setDateAndTimeEditText();
  }

  private void setupToolbar() {
    setSupportActionBar(mToolbar);

    //Show an X in place of <-
    final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
    if (cross != null) {
      cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
    }

    if (getSupportActionBar() != null) {
      getSupportActionBar().setElevation(0);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(cross);
    }
  }

  @OnTextChanged(R.id.userToDoEditText)
  public void onBodyTextChanged() {
    mUserEnteredText = mToDoTextBodyEditText.getText().toString();
  }

  @OnCheckedChanged(R.id.toDoHasDateSwitchCompat)
  public void onSwitchCheckedChanged(boolean isChecked) {
    String action = isChecked ? "Reminder Set" : "Reminder Removed";
    tracker.send(this, AnalyticsTracker.ACTION, action);

    if (!isChecked) {
      mUserReminderDate = null;
    }
    mUserHasReminder = isChecked;
    setDateAndTimeEditText();
    setEnterDateLayoutVisibleWithAnimations(isChecked);
    hideKeyboard(mToDoTextBodyEditText);
  }

  @OnClick(R.id.todoReminderAndDateContainerLayout)
  public void onReminderContainerClicked() {
    hideKeyboard(mToDoTextBodyEditText);
  }

  @OnClick(R.id.makeToDoFloatingActionButton)
  public void onMakeToDoClicked() {
    if (mUserReminderDate != null && mUserReminderDate.before(new Date())) {
      tracker.send(this, AnalyticsTracker.ACTION, "Date in the Past");
      makeResult(RESULT_CANCELED);
    } else {
      tracker.send(this, AnalyticsTracker.ACTION, "Make Todo");
      makeResult(RESULT_OK);
    }
    hideKeyboard(mToDoTextBodyEditText);
    finish();
  }

  @OnClick(R.id.newTodoTimeEditText)
  public void onEditTimeClicked() {
    Date date = mUserToDoItem.getToDoDate() != null ? mUserReminderDate : new Date();
    hideKeyboard(mToDoTextBodyEditText);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoActivity.this, hour, minute, DateFormat.is24HourFormat(AddToDoActivity.this));
    if (theme.equals(PreferenceAccessor.DARKTHEME)) {
      timePickerDialog.setThemeDark(true);
    }
    timePickerDialog.show(getFragmentManager(), "TimeFragment");
  }

  @OnClick(R.id.newTodoDateEditText)
  public void onEditDateClicked() {
    Date date = mUserToDoItem.getToDoDate() != null ? mUserReminderDate : new Date();
    hideKeyboard(mToDoTextBodyEditText);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoActivity.this, year, month, day);
    if (theme.equals(PreferenceAccessor.DARKTHEME)) {
      datePickerDialog.setThemeDark(true);
    }
    datePickerDialog.show(getFragmentManager(), "DateFragment");
  }

  private void setDateAndTimeEditText() {

    if (mUserToDoItem.hasReminder() && mUserReminderDate != null) {
      String userDate = DateUtils.formatDate(this, mUserReminderDate);
      String userTime = DateUtils.formatTime(this, mUserReminderDate);
      mTimeEditText.setText(userTime);
      mDateEditText.setText(userDate);

    } else {
      mDateEditText.setText(getString(R.string.date_reminder_default));
      boolean time24 = DateFormat.is24HourFormat(this);
      Calendar cal = Calendar.getInstance();
      if (time24) {
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
      } else {
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
      }
      cal.set(Calendar.MINUTE, 0);
      mUserReminderDate = cal.getTime();
      String timeString;

      timeString = DateUtils.formatTime(this, mUserReminderDate);
      mTimeEditText.setText(timeString);
    }
  }

  public void hideKeyboard(EditText et) {
    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
  }

  public void setDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    int hour, minute;

    Calendar reminderCalendar = Calendar.getInstance();
    reminderCalendar.set(year, month, day);

    if (reminderCalendar.before(calendar)) {
      Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
      return;
    }

    if (mUserReminderDate != null) {
      calendar.setTime(mUserReminderDate);
    }

    if (DateFormat.is24HourFormat(this)) {
      hour = calendar.get(Calendar.HOUR_OF_DAY);
    } else {
      hour = calendar.get(Calendar.HOUR);
    }
    minute = calendar.get(Calendar.MINUTE);

    calendar.set(year, month, day, hour, minute);
    mUserReminderDate = calendar.getTime();
    setReminderTextView();
    setDateEditText();
  }

  public void setTime(int hour, int minute) {
    Calendar calendar = Calendar.getInstance();
    if (mUserReminderDate != null) {
      calendar.setTime(mUserReminderDate);
    }

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    calendar.set(year, month, day, hour, minute, 0);
    mUserReminderDate = calendar.getTime();

    setReminderTextView();
    setTimeEditText();
  }

  public void setDateEditText() {
    mDateEditText.setText(DateUtils.formatDate(this, mUserReminderDate));
  }

  public void setTimeEditText() {
    mTimeEditText.setText(DateUtils.formatTime(this, mUserReminderDate));
  }

  public void setReminderTextView() {
    if (mUserReminderDate != null) {
      mReminderTextView.setVisibility(View.VISIBLE);
      if (mUserReminderDate.before(new Date())) {
        mReminderTextView.setText(getString(R.string.date_error_check_again));
        mReminderTextView.setTextColor(Color.RED);
        return;
      }
      Date date = mUserReminderDate;
      String dateString = DateUtils.formatDate(this, date);
      String timeString = DateUtils.formatTime(this, date);
      String amPmString = "";

      String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
      mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
      mReminderTextView.setText(finalString);
    } else {
      mReminderTextView.setVisibility(View.INVISIBLE);

    }
  }

  public void makeResult(int result) {
    Intent i = new Intent();
    if (mUserEnteredText.length() > 0) {

      String capitalizedString = Character.toUpperCase(mUserEnteredText.charAt(0)) + mUserEnteredText.substring(1);
      mUserToDoItem.setToDoText(capitalizedString);
    } else {
      mUserToDoItem.setToDoText(mUserEnteredText);
    }
    if (mUserReminderDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(mUserReminderDate);
      calendar.set(Calendar.SECOND, 0);
      mUserReminderDate = calendar.getTime();
    }
    mUserToDoItem.setHasReminder(mUserHasReminder);
    mUserToDoItem.setToDoDate(mUserReminderDate);
    mUserToDoItem.setTodoColor(mUserColor);
    i.putExtra(MainActivity.TODOITEM, mUserToDoItem);
    setResult(result, i);
  }

  @Override
  public void onBackPressed() {
    if (mUserReminderDate.before(new Date())) {
      mUserToDoItem.setToDoDate(null);
    }
    makeResult(RESULT_OK);
    super.onBackPressed();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (NavUtils.getParentActivityName(this) != null) {
          tracker.send(this, AnalyticsTracker.ACTION, "Discard Todo");
          makeResult(RESULT_CANCELED);
          NavUtils.navigateUpFromSameTask(this);
        }
        hideKeyboard(mToDoTextBodyEditText);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }


  @Override
  public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
    setTime(hour, minute);
  }

  @Override
  public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
    setDate(year, month, day);
  }

  public void setEnterDateLayoutVisible(boolean checked) {
    mUserDateSpinnerContainingLinearLayout.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
  }

  public void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
    if (checked) {
      setReminderTextView();
      mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
          new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
              mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
            }
          }
      );
    } else {
      mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
          new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
            }
          }
      );
    }
  }
}

