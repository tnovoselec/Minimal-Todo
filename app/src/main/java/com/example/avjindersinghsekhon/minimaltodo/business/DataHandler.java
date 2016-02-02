package com.example.avjindersinghsekhon.minimaltodo.business;

import android.util.Log;

import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;

import java.util.Collections;
import java.util.List;

public enum DataHandler {

  INSTANCE;

  private static final String TAG = DataHandler.class.getSimpleName();

  private StoreRetrieveData storeRetrieveData = StoreRetrieveData.INSTANCE;

  public List<ToDoItem> getToDoItems() {
    try {
      return storeRetrieveData.loadFromFile();
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  public void saveToDoItems(List<ToDoItem> items) {
    try {
      storeRetrieveData.saveToFile(items);
    } catch (Exception e) {
      Log.e(TAG, "unable to save items", e);
    }
  }
}
