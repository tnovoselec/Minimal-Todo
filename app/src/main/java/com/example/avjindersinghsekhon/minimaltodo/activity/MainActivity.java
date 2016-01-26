package com.example.avjindersinghsekhon.minimaltodo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.avjindersinghsekhon.minimaltodo.CustomRecyclerScrollViewListener;
import com.example.avjindersinghsekhon.minimaltodo.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.business.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.service.TodoNotificationService;
import com.example.avjindersinghsekhon.minimaltodo.view.RecyclerViewEmptySupport;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends BaseActivity {

  public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
  public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
  public static final String TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity";
  private static final int REQUEST_ID_TODO_ITEM = 100;

  private RecyclerViewEmptySupport mRecyclerView;
  private FloatingActionButton mAddToDoItemFAB;
  private ArrayList<ToDoItem> mToDoItemsArrayList;
  private CoordinatorLayout mCoordLayout;
  private BasicListAdapter adapter;
  private ToDoItem mJustDeletedToDoItem;
  private int mIndexOfDeletedToDoItem;
  private StoreRetrieveData storeRetrieveData = StoreRetrieveData.INSTANCE;
  public ItemTouchHelper itemTouchHelper;
  private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;

  public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
    ArrayList<ToDoItem> items = null;

    try {
      items = storeRetrieveData.loadFromFile();

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }

    if (items == null) {
      items = new ArrayList<>();
    }
    return items;

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
    if (preferenceAccessor.getChangeOccured()) {

      mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
      adapter = new BasicListAdapter(mToDoItemsArrayList);
      mRecyclerView.setAdapter(adapter);
      setAlarms();

      preferenceAccessor.setChangeOccured(false);
    }
  }

  private void setAlarms() {
    if (mToDoItemsArrayList != null) {
      for (ToDoItem item : mToDoItemsArrayList) {
        if (item.hasReminder() && item.getToDoDate() != null) {
          if (item.getToDoDate().before(new Date())) {
            item.setToDoDate(null);
            continue;
          }
          Intent i = new Intent(this, TodoNotificationService.class);
          i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
          i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
          createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
        }
      }
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    //We recover the theme we've set and setTheme accordingly
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    preferenceAccessor.setChangeOccured(false);

    mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
    adapter = new BasicListAdapter(mToDoItemsArrayList);
    setAlarms();
    final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);


    mCoordLayout = (CoordinatorLayout) findViewById(R.id.myCoordinatorLayout);
    mAddToDoItemFAB = (FloatingActionButton) findViewById(R.id.addToDoItemFAB);

    mAddToDoItemFAB.setOnClickListener(new View.OnClickListener() {

      @SuppressWarnings("deprecation")
      @Override
      public void onClick(View v) {
        tracker.send(this, "Action", "FAB pressed");
        Intent newTodo = new Intent(MainActivity.this, AddToDoActivity.class);
        ToDoItem item = new ToDoItem("", false, null);
        int color = ColorGenerator.MATERIAL.getRandomColor();
        item.setTodoColor(color);
        newTodo.putExtra(TODOITEM, item);
        startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
      }
    });


    mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.toDoRecyclerView);
    if (theme.equals(PreferenceAccessor.LIGHTTHEME)) {
      mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
    }
    mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
      @Override
      public void show() {

        mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
      }

      @Override
      public void hide() {

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
        int fabMargin = lp.bottomMargin;
        mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
      }
    };
    mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);


    ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
    itemTouchHelper = new ItemTouchHelper(callback);
    itemTouchHelper.attachToRecyclerView(mRecyclerView);

    mRecyclerView.setAdapter(adapter);
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
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        return true;
      case R.id.preferences:
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
        Intent i = new Intent(this, TodoNotificationService.class);
        i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
        i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
        createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
      }

      for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
        if (item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())) {
          mToDoItemsArrayList.set(i, item);
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

  private AlarmManager getAlarmManager() {
    return (AlarmManager) getSystemService(ALARM_SERVICE);
  }

  private boolean doesPendingIntentExist(Intent i, int requestCode) {
    PendingIntent pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE);
    return pi != null;
  }

  private void createAlarm(Intent i, int requestCode, long timeInMillis) {
    AlarmManager am = getAlarmManager();
    PendingIntent pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
    am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
  }

  private void deleteAlarm(Intent i, int requestCode) {
    if (doesPendingIntentExist(i, requestCode)) {
      PendingIntent pi = PendingIntent.getService(this, requestCode, i, PendingIntent.FLAG_NO_CREATE);
      pi.cancel();
      getAlarmManager().cancel(pi);
    }
  }

  private void addToDataStore(ToDoItem item) {
    mToDoItemsArrayList.add(item);
    adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);
  }


  public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
    private ArrayList<ToDoItem> items;

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
      if (fromPosition < toPosition) {
        for (int i = fromPosition; i < toPosition; i++) {
          Collections.swap(items, i, i + 1);
        }
      } else {
        for (int i = fromPosition; i > toPosition; i--) {
          Collections.swap(items, i, i - 1);
        }
      }
      notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemoved(final int position) {
      //Remove this line if not using Google Analytics
      tracker.send(this, "Action", "Swiped Todo Away");

      mJustDeletedToDoItem = items.remove(position);
      mIndexOfDeletedToDoItem = position;
      Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
      deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
      notifyItemRemoved(position);

      String toShow = "Todo";
      Snackbar.make(mCoordLayout, "Deleted " + toShow, Snackbar.LENGTH_SHORT)
          .setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //Comment the line below if not using Google Analytics
              tracker.send(this, "Action", "UNDO Pressed");
              items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
              if (mJustDeletedToDoItem.getToDoDate() != null && mJustDeletedToDoItem.hasReminder()) {
                Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
              }
              notifyItemInserted(mIndexOfDeletedToDoItem);
            }
          }).show();
    }

    @Override
    public BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
      return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BasicListAdapter.ViewHolder holder, final int position) {
      ToDoItem item = items.get(position);
      //Background color for each to-do item. Necessary for night/day mode
      int bgColor;
      //color of title text in our to-do item. White for night mode, dark gray for day mode
      int todoTextColor;
      if (preferenceAccessor.getThemeSaved().equals(PreferenceAccessor.LIGHTTHEME)) {
        bgColor = Color.WHITE;
        todoTextColor = getResources().getColor(R.color.secondary_text);
      } else {
        bgColor = Color.DKGRAY;
        todoTextColor = Color.WHITE;
      }
      holder.linearLayout.setBackgroundColor(bgColor);

      if (item.hasReminder() && item.getToDoDate() != null) {
        holder.mToDoTextview.setMaxLines(1);
        holder.mTimeTextView.setVisibility(View.VISIBLE);
      } else {
        holder.mTimeTextView.setVisibility(View.GONE);
        holder.mToDoTextview.setMaxLines(2);
      }
      holder.mToDoTextview.setText(item.getToDoText());
      holder.mToDoTextview.setTextColor(todoTextColor);
      TextDrawable myDrawable = TextDrawable.builder().beginConfig()
          .textColor(Color.WHITE)
          .useFont(Typeface.DEFAULT)
          .toUpperCase()
          .endConfig()
          .buildRound(item.getToDoText().substring(0, 1), item.getTodoColor());

      holder.mColorImageView.setImageDrawable(myDrawable);
      if (item.getToDoDate() != null) {
        String timeToShow;
        if (android.text.format.DateFormat.is24HourFormat(MainActivity.this)) {
          timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
        } else {
          timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
        }
        holder.mTimeTextView.setText(timeToShow);
      }


    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    BasicListAdapter(ArrayList<ToDoItem> items) {

      this.items = items;
    }


    @SuppressWarnings("deprecation")
    public class ViewHolder extends RecyclerView.ViewHolder {

      View mView;
      LinearLayout linearLayout;
      TextView mToDoTextview;
      ImageView mColorImageView;
      TextView mTimeTextView;

      public ViewHolder(View v) {
        super(v);
        mView = v;
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
            Intent i = new Intent(MainActivity.this, AddToDoActivity.class);
            i.putExtra(TODOITEM, item);
            startActivityForResult(i, REQUEST_ID_TODO_ITEM);
          }
        });
        mToDoTextview = (TextView) v.findViewById(R.id.toDoListItemTextview);
        mTimeTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
        mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);
        linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      storeRetrieveData.saveToFile(mToDoItemsArrayList);
    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
  }
}


