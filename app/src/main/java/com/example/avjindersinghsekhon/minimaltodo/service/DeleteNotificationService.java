package com.example.avjindersinghsekhon.minimaltodo.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.business.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;

import java.util.ArrayList;
import java.util.UUID;

public class DeleteNotificationService extends IntentService {

  private StoreRetrieveData storeRetrieveData = StoreRetrieveData.INSTANCE;
  private ArrayList<ToDoItem> mToDoItems;
  private ToDoItem mItem;
  private PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;

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
    preferenceAccessor.setChangeOccured(true);
  }

  private void saveData() {
    try {
      storeRetrieveData.saveToFile(mToDoItems);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    saveData();
  }

  private ArrayList<ToDoItem> loadData() {
    try {
      return storeRetrieveData.loadFromFile();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;

  }
}
