package com.aideus.tasky.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aideus.tasky.R;
import com.aideus.tasky.adapter.DoneTaskAdapter;
import com.aideus.tasky.database.DBHelper;
import com.aideus.tasky.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class DoneTaskFragment extends TaskFragment {

    public DoneTaskFragment() {
        // Required empty public constructor
    }

    OnTaskRestoreListener onTaskRestoreListener;

    public interface OnTaskRestoreListener {
        void onTaskRestore(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskRestoreListener = (OnTaskRestoreListener) activity;
        } catch (ClassCastException e) {
            throw (new ClassCastException((activity.toString() + " must implement OnTaskRestoreListener interface")));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_done_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvDoneTasks);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new DoneTaskAdapter(this);

        recyclerView.setAdapter(adapter);

        return rootView;
    }

    //TODO
    @Override
    public void findTasks(String title) {
        checkAdapter();
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                + DBHelper.SELECTION_STATUS, new String[]{"%" + title + "%",
                Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));

        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    //TODO
    @Override
    public void checkAdapter() {
        if (adapter == null) {
            adapter = new DoneTaskAdapter(this);
            addTaskFromDB();
        }
    }

    // Add all done tasks to ArrayList from DB sorted by date and add them to list using adapter.
    @Override
    public void addTaskFromDB() {
        checkAdapter();
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));

        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;
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
        if (position != -1) {
            adapter.addItem(position, newTask);
            // If new task date is the latest, add to the last position in ArrayList.
        } else {
            adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    // Move task to current tasks list and set alarm for it.
    @Override
    public void moveTask(ModelTask task) {
        if (task.getDate() != 0) {
            alarmHelper.setAlarm(task);
        }
        onTaskRestoreListener.onTaskRestore(task);

    }
}
