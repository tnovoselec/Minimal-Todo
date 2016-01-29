package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.avjindersinghsekhon.minimaltodo.CustomRecyclerScrollViewListener;
import com.example.avjindersinghsekhon.minimaltodo.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.adapter.BasicListAdapter;
import com.example.avjindersinghsekhon.minimaltodo.adapter.BasicListAdapter.OnItemRemovedListener;
import com.example.avjindersinghsekhon.minimaltodo.business.AlarmHandler;
import com.example.avjindersinghsekhon.minimaltodo.business.AnalyticsTracker;
import com.example.avjindersinghsekhon.minimaltodo.business.DataHandler;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.view.RecyclerViewEmptySupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

  public static final String TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity";
  private static final int REQUEST_ID_TODO_ITEM = 100;

  @Bind(R.id.toDoRecyclerView)
  RecyclerViewEmptySupport recyclerView;
  @Bind(R.id.addToDoItemFAB)
  FloatingActionButton addToDoItemFAB;
  @Bind(R.id.myCoordinatorLayout)
  CoordinatorLayout coordinatorLayout;
  @Bind(R.id.toolbar)
  Toolbar toolbar;

  private List<ToDoItem> toDoItems;
  private BasicListAdapter adapter;
  private AlarmHandler alarmHandler = AlarmHandler.INSTANCE;
  private DataHandler dataHandler = DataHandler.INSTANCE;
  private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;


  protected void onCreate(Bundle savedInstanceState) {
    //We recover the theme we've set and setTheme accordingly
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    preferenceAccessor.setChangeOccurred(false);

    toDoItems = dataHandler.getToDoItems();

    setAlarms();
    setSupportActionBar(toolbar);
    adapter = new BasicListAdapter(toDoItems, onItemClickedListener(), onItemRemovedListener(), this);

    setUpRecyclerView();
  }

  @Override
  protected void onResume() {
    super.onResume();
    tracker.send(this);

    if (preferenceAccessor.getExit()) {
      preferenceAccessor.setExit(false);
      finish();
    }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we would have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
    if (preferenceAccessor.getRecreateActivity()) {
      preferenceAccessor.setRecreateActivity(false);
      recreate();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (preferenceAccessor.getChangeOccurred()) {

      toDoItems = dataHandler.getToDoItems();
      adapter = new BasicListAdapter(toDoItems, onItemClickedListener(), onItemRemovedListener(), this);
      recyclerView.setAdapter(adapter);
      setAlarms();

      preferenceAccessor.setChangeOccurred(false);
    }
  }

  private void setAlarms() {
    alarmHandler.setAlarms(toDoItems, this);
  }

  private void setUpRecyclerView() {
    if (theme.equals(PreferenceAccessor.LIGHTTHEME)) {
      recyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
    }
    recyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));


    customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
      @Override
      public void show() {
        addToDoItemFAB.animate()
            .translationY(0)
            .setInterpolator(new DecelerateInterpolator(2.0f))
            .start();
      }

      @Override
      public void hide() {

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) addToDoItemFAB.getLayoutParams();
        int fabMargin = lp.bottomMargin;
        addToDoItemFAB.animate()
            .translationY(addToDoItemFAB.getHeight() + fabMargin)
            .setInterpolator(new AccelerateInterpolator(2.0f))
            .start();
      }
    };
    recyclerView.addOnScrollListener(customRecyclerScrollViewListener);

    ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
    itemTouchHelper.attachToRecyclerView(recyclerView);

    recyclerView.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.aboutMeMenuItem:
        startAboutActivity();
        return true;
      case R.id.preferences:
        startSettingsActivity();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
      ToDoItem item = (ToDoItem) data.getSerializableExtra(TODOITEM);
      if (item.getToDoText().length() <= 0) {
        return;
      }
      boolean existed = false;

      if (item.hasReminder() && item.getToDoDate() != null) {
        alarmHandler.createAlarm(item, this);
      }

      for (int i = 0; i < toDoItems.size(); i++) {
        if (item.getIdentifier().equals(toDoItems.get(i).getIdentifier())) {
          toDoItems.set(i, item);
          existed = true;
          adapter.notifyDataSetChanged();
          break;
        }
      }
      if (!existed) {
        addToDataStore(item);
      }
    }
  }

  @OnClick(R.id.addToDoItemFAB)
  public void onAddToDoClicked() {
    tracker.send(this, AnalyticsTracker.ACTION, "FAB pressed");
    startAddToDoActivity(ToDoItem.createEmpty());
  }

  private void startAddToDoActivity(ToDoItem item) {
    Intent intent = new Intent(MainActivity.this, AddToDoActivity.class);
    intent.putExtra(TODOITEM, item);
    startActivityForResult(intent, REQUEST_ID_TODO_ITEM);
  }

  private void startAboutActivity() {
    Intent i = new Intent(this, AboutActivity.class);
    startActivity(i);
  }

  private void startSettingsActivity() {
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }

  private void addToDataStore(ToDoItem item) {
    toDoItems.add(item);
    adapter.notifyItemInserted(toDoItems.size() - 1);
  }

  private OnItemRemovedListener onItemRemovedListener() {
    return new OnItemRemovedListener() {
      @Override
      public void onItemRemoved(ToDoItem removedItem, int position) {
        MainActivity.this.onItemRemoved(removedItem, position);
      }
    };
  }

  private BasicListAdapter.OnItemClickedListener onItemClickedListener() {
    return new BasicListAdapter.OnItemClickedListener() {
      @Override
      public void onToDoItemClicked(ToDoItem toDoItem) {
        startAddToDoActivity(toDoItem);
      }
    };
  }

  private void onItemRemoved(final ToDoItem removedItem, final int position) {
    //Remove this line if not using Google Analytics
    tracker.send(this, AnalyticsTracker.ACTION, "Swiped Todo Away");

    alarmHandler.deleteAlarm(removedItem, this);
    adapter.notifyItemRemoved(position);

    String toShow = "Todo";
    Snackbar.make(coordinatorLayout, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
        .setAction("UNDO", new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onUndoAction(removedItem, position);

          }
        }).show();
  }

  private void onUndoAction(final ToDoItem removedItem, final int position) {
    //Comment the line below if not using Google Analytics
    tracker.send(this, AnalyticsTracker.ACTION, "UNDO Pressed");
    toDoItems.add(position, removedItem);
    if (removedItem.getToDoDate() != null && removedItem.hasReminder()) {
      alarmHandler.createAlarm(removedItem, MainActivity.this);
    }
    adapter.notifyItemInserted(position);
  }


  @Override
  protected void onPause() {
    super.onPause();
    dataHandler.saveToDoItems(toDoItems);
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    recyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
  }
}


