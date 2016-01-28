package com.example.avjindersinghsekhon.minimaltodo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.avjindersinghsekhon.minimaltodo.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.activity.AddToDoActivity;
import com.example.avjindersinghsekhon.minimaltodo.activity.MainActivity;
import com.example.avjindersinghsekhon.minimaltodo.business.PreferenceAccessor;
import com.example.avjindersinghsekhon.minimaltodo.model.ToDoItem;

import java.util.Collections;
import java.util.List;

public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {

  public interface OnItemClickedListener {
    void onToDoItemClicked(ToDoItem toDoItem);
  }

  public interface OnItemRemovedListener{
    void onItemRemoved(ToDoItem removedItem, int position);
  }

  private List<ToDoItem> items;
  private OnItemClickedListener onItemClickedListener;
  private OnItemRemovedListener onItemRemovedListener;
  private Context context;
  private PreferenceAccessor preferenceAccessor = PreferenceAccessor.INSTANCE;

  public BasicListAdapter(List<ToDoItem> items, OnItemClickedListener onItemClickedListener, OnItemRemovedListener onItemRemovedListener, Context context) {
    this.items = items;
    this.onItemClickedListener = onItemClickedListener;
    this.onItemRemovedListener = onItemRemovedListener;
    this.context = context;
  }

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
    ToDoItem removedItem = items.remove(position);
    onItemRemovedListener.onItemRemoved(removedItem, position);
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
      todoTextColor = context.getResources().getColor(R.color.secondary_text);
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
      if (android.text.format.DateFormat.is24HourFormat(context)) {
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

  @SuppressWarnings("deprecation")
  public class ViewHolder extends RecyclerView.ViewHolder {

    LinearLayout linearLayout;
    TextView mToDoTextview;
    ImageView mColorImageView;
    TextView mTimeTextView;

    public ViewHolder(View v) {
      super(v);
      v.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
          onItemClickedListener.onToDoItemClicked(item);
        }
      });
      mToDoTextview = (TextView) v.findViewById(R.id.toDoListItemTextview);
      mTimeTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
      mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);
      linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);
    }
  }
}
