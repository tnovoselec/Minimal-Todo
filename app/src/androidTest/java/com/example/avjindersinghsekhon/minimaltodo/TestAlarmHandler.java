package com.example.avjindersinghsekhon.minimaltodo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.example.avjindersinghsekhon.minimaltodo.activity.MainActivity;
import com.example.avjindersinghsekhon.minimaltodo.business.AlarmHandler;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.service.TodoNotificationService;

import java.util.Date;

public class TestAlarmHandler extends ActivityUnitTestCase<MainActivity> {

  private ToDoItem testItem = ToDoItem.createEmpty();

  private AlarmHandler alarmHandler = AlarmHandler.INSTANCE;

  public TestAlarmHandler() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    testItem.setToDoDate(new Date());
    alarmHandler.init(getInstrumentation().getTargetContext());
    alarmHandler.deleteAlarm(testItem, getInstrumentation().getTargetContext());
  }

  public void testCreateAlarm() {
    Context context = getInstrumentation().getTargetContext();
    alarmHandler.createAlarm(testItem, context);

    Intent intent = new Intent(context, TodoNotificationService.class);
    PendingIntent pendingIntent = PendingIntent.getService(context, testItem.getIdentifier().hashCode(), intent, PendingIntent.FLAG_NO_CREATE);
    assertNotNull(pendingIntent);
  }

  public void testDeleteAlarm(){
    Context context = getInstrumentation().getTargetContext();

    Intent intent = new Intent(context, TodoNotificationService.class);

    alarmHandler.createAlarm(testItem, context);
    PendingIntent pendingIntent = PendingIntent.getService(context, testItem.getIdentifier().hashCode(), intent, PendingIntent.FLAG_NO_CREATE);

    assertNotNull(pendingIntent);

    alarmHandler.deleteAlarm(testItem, context);
    pendingIntent = PendingIntent.getService(context, testItem.getIdentifier().hashCode(), intent, PendingIntent.FLAG_NO_CREATE);

    assertNull(pendingIntent);
  }
}
