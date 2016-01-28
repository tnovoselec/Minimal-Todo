package com.example.avjindersinghsekhon.minimaltodo.business;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.service.TodoNotificationService;

import java.util.Date;
import java.util.List;

public enum AlarmHandler {

  INSTANCE;

  private AlarmManager alarmManager;

  public void init(Context context) {
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  public void setAlarms(List<ToDoItem> items, Context context) {
    if (items != null) {
      for (ToDoItem item : items) {
        if (item.hasReminder() && item.getToDoDate() != null) {
          if (item.getToDoDate().before(new Date())) {
            item.setToDoDate(null);
            continue;
          }
          createAlarm(item, context);
        }
      }
    }
  }

  public PendingIntent createAlarm(ToDoItem item, Context context) {
    Intent intent = new Intent(context, TodoNotificationService.class);
    intent.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
    intent.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
    PendingIntent pi = PendingIntent.getService(context, item.getIdentifier().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.set(AlarmManager.RTC_WAKEUP, item.getToDoDate().getTime(), pi);
    return pi;
  }

  public void deleteAlarm(ToDoItem item, Context context) {
    int requestCode = item.getIdentifier().hashCode();
    Intent intent = new Intent(context, TodoNotificationService.class);
    PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
    if (pendingIntent != null) {
      pendingIntent.cancel();
      alarmManager.cancel(pendingIntent);
    }
  }

}
