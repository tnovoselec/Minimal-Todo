package com.example.avjindersinghsekhon.minimaltodo.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.avjindersinghsekhon.minimaltodo.business.DataHandler;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;

import java.util.List;
import java.util.UUID;

public class DeleteNotificationService extends IntentService {

  private List<ToDoItem> mToDoItems;
  private ToDoItem mItem;
  private PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;
  private DataHandler dataHandler = DataHandler.INSTANCE;

  public DeleteNotificationService() {
    super("DeleteNotificationService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    UUID todoID = (UUID) intent.getSerializableExtra(TodoNotificationService.TODOUUID);

    mToDoItems = loadData();
    if (mToDoItems != null) {
      for (ToDoItem item : mToDoItems) {
        if (item.getIdentifier().equals(todoID)) {
          mItem = item;
          break;
        }
      }

      if (mItem != null) {
        mToDoItems.remove(mItem);
        dataChanged();
        saveData();
      }
    }
  }

  private void dataChanged() {
    preferenceAccessor.setChangeOccurred(true);
  }

  private void saveData() {
    dataHandler.saveToDoItems(mToDoItems);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    saveData();
  }

  private List<ToDoItem> loadData() {
    return dataHandler.getToDoItems();
  }
}
