package com.aideus.tasky.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.aideus.tasky.fragment.TaskFragment;
import com.aideus.tasky.model.Item;
import com.aideus.tasky.model.ModelSeparator;
import com.aideus.tasky.model.ModelTask;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List containing list of tasks ModelTasks.
    List<Item> items;
    // CurrentTaskFragment or DoneTaskFragment to work with.
    TaskFragment taskFragment;

    // Boolean to check if list contains separators of different types.
    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;


    // Constructor to initialize TaskFragment and new ArrayList for tasks ModelTasks.
    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
    }

    // Get task ModelTask from position in ArrayList.
    public Item getItem(int position) {
        return items.get(position);
    }

    // Add task ModelTask to last position in ArrayList and notifyItemInserted.
    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    // Add task ModelTask to exact position in ArrayList and notifyItemInserted.
    public void addItem(int location, Item item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    // Update task with same TimeStamp. Delete it and then add newer version.
    public void updateTask(ModelTask newTask) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                ModelTask task = (ModelTask) getItem(i);
                if (newTask.getTimeStamp() == task.getTimeStamp()) {
                    removeItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }

    // Remove task ModelTask from exact position in ArrayList and notifyItemRemoved.
    public void removeItem(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            items.remove(location);
            notifyItemRemoved(location);

            // Check if there are empty separators, without tasks below them and remove if any.
            if (location - 1 >= 0 && location <= getItemCount() - 1) {
                if (!getItem(location).isTask() && !getItem(location - 1).isTask()) {
                    ModelSeparator separator = (ModelSeparator) getItem(location - 1);
                    checkSeparators(separator.getType());
                    items.remove(location - 1);
                    notifyItemRemoved(location - 1);
                }
            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                ModelSeparator separator = (ModelSeparator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());
                int locationTemp = getItemCount() - 1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    // Set separator presence boolean to false before deleting respective separator.
    public void checkSeparators(int type) {
        switch (type) {
            case ModelSeparator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case  ModelSeparator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }
    }

    // Remove all items in ArrayList by recreating it and set all separator presence to false.
    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorOverdue = false;
            containsSeparatorToday = false;
            containsSeparatorTomorrow = false;
            containsSeparatorFuture = false;
        }
    }

    // Get number of tasks in ArrayList.
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder describes an item view.
    // Providing reference to the views for each data item and access to all the views for a data item in a view holder.
    protected class TaskViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView date;
        protected CircleImageView priority;

        public TaskViewHolder(View itemView, TextView title, TextView date, CircleImageView priority) {
            super(itemView);
            this.title = title;
            this.date = date;
            this.priority = priority;
        }
    }

    // ViewHolder describes an item view.
    // Providing reference to the views for each data item and access to all the views for a data item in a view holder.
    protected class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;

        public SeparatorViewHolder(View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }

    // Return taskFragment
    public TaskFragment getTaskFragment() {
        return taskFragment;
    }

}
