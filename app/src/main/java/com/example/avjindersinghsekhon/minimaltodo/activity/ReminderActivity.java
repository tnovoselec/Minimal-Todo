package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.DataHandler;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.service.TodoNotificationService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

public class ReminderActivity extends BaseActivity {

  @Bind(R.id.toDoReminderTextViewBody)
  TextView mtoDoTextTextView;
  @Bind(R.id.todoReminderSnoozeSpinner)
  MaterialSpinner mSnoozeSpinner;
  @Bind(R.id.reminderViewSnoozeTextView)
  TextView mSnoozeTextView;
  @Bind(R.id.toolbar)
  Toolbar toolbar;

  private DataHandler dataHandler = DataHandler.INSTANCE;
  private List<ToDoItem> mToDoItems;
  private ToDoItem mItem;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tracker.send(this);
    setContentView(R.layout.reminder_layout);
    ButterKnife.bind(this);

    mToDoItems = dataHandler.getToDoItems();
    String[] snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);

    setSupportActionBar(toolbar);

    Intent i = getIntent();
    UUID id = (UUID) i.getSerializableExtra(TodoNotificationService.TODOUUID);
    getItem(id);

    mtoDoTextTextView.setText(mItem.getToDoText());

    if (theme.equals(PreferenceAccessor.LIGHTTHEME)) {
      mSnoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
    } else {
      mSnoozeTextView.setTextColor(Color.WHITE);
      mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_snooze_white_24dp, 0, 0, 0
      );
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text_view, snoozeOptionsArray);
    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

    mSnoozeSpinner.setAdapter(adapter);
  }

  @OnClick(R.id.toDoReminderRemoveButton)
  public void onRemoveTodo() {
    tracker.send(this, AnalyticsTracker.ACTION, "Todo Removed from Reminder Activity");
    mToDoItems.remove(mItem);
    changeOccurred();
    saveData();
    closeApp();
  }

  private void getItem(UUID id) {
    mItem = null;
    for (ToDoItem toDoItem : mToDoItems) {
      if (toDoItem.getIdentifier().equals(id)) {
        mItem = toDoItem;
        break;
      }
    }
  }

  private void closeApp() {
    Intent i = new Intent(ReminderActivity.this, MainActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    preferenceAccessor.setExit(true);
    startActivity(i);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_reminder, menu);
    return true;
  }

  private void changeOccurred() {
    preferenceAccessor.setChangeOccurred(true);
  }

  private Date addTimeToDate(int mins) {
    tracker.send(this, AnalyticsTracker.ACTION, "Snoozed", "For " + mins + " minutes");
    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, mins);
    return calendar.getTime();
  }

  private int valueFromSpinner() {
    switch (mSnoozeSpinner.getSelectedItemPosition()) {
      case 0:
        return 10;
      case 1:
        return 30;
      case 2:
        return 60;
      default:
        return 0;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.toDoReminderDoneMenuItem:
        Date date = addTimeToDate(valueFromSpinner());
        mItem.setToDoDate(date);
        mItem.setHasReminder(true);
        changeOccurred();
        saveData();
        closeApp();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void saveData() {
    dataHandler.saveToDoItems(mToDoItems);
  }
}
