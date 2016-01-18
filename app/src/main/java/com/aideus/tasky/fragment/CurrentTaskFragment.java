package com.aideus.tasky.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.aideus.tasky.R;
import com.aideus.tasky.adapter.CurrentTasksAdapter;
import com.aideus.tasky.alarm.AlarmHelper;
import com.aideus.tasky.database.DBHelper;
import com.aideus.tasky.model.ModelSeparator;
import com.aideus.tasky.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CurrentTaskFragment extends TaskFragment {

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    // Callback interface required for host activity to implement.
    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    // Check if OnTaskDoneListener callback interface is implemented in host activity.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw (new ClassCastException(activity.toString() + " must implement OnTaskDoneListener interface"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        // Initialize RecyclerView in earlier inflated rootView.
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        // Initialize new LinearLayoutManager.
        // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
        // A LayoutManager is responsible for measuring and positioning item views within a RecyclerView
        // as well as determining the policy for when to recycle item views that are no longer visible to the user.
        layoutManager = new LinearLayoutManager(getActivity());

        // Set the LinearLayoutManager that RecyclerView will use.
        recyclerView.setLayoutManager(layoutManager);

        // Initialize CurrentTasksAdapter (TaskAdapter / RecyclerView.Adapter).
        adapter = new CurrentTasksAdapter(this);

        // Set adapter to provide child views on demand for recyclerView.
        recyclerView.setAdapter(adapter);

        // Return rootView View;
        return rootView;
    }

    //TODO
    @Override
    public void findTasks(String title) {
        checkAdapter();
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));

        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    //TODO
    @Override
    public void checkAdapter() {
        if (adapter == null) {
            adapter = new CurrentTasksAdapter(this);
            addTaskFromDB();
        }
    }

    // Add all current/overdue tasks to ArrayList from DB sorted by date and add them to list using adapter.
    @Override
    public void addTaskFromDB() {
        checkAdapter();
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DBHelper.TASK_DATE_COLUMN));

        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;

        // Init ModelSeparator.
        ModelSeparator separator = null;

        // Check if adapter exists.
        checkAdapter();

        // For each element (ModelTask) in ArrayList check if date of new task is set earlier.
        // And if it is add it to this position sorting tasks by its date.
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        // TODO
        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_FUTURE);
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }
        }

        if (position != -1) {

            //TODO
            if (!adapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter.getItem(position - 2).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0){
                    position -= 1;
                }
            }

            if (separator != null) {
                adapter.addItem(position - 1, separator);
            }

            adapter.addItem(position, newTask);

        } else {
            //TODO
            if (separator != null) {
                adapter.addItem(separator);
            }

            // If new task date is the latest, add to the last position in ArrayList.
            adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    // Move task to done tasks list and remove its alarm.
    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimeStamp());
        onTaskDoneListener.onTaskDone(task);
    }
}
