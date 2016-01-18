package com.aideus.tasky.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.aideus.tasky.MainActivity;
import com.aideus.tasky.R;
import com.aideus.tasky.adapter.TaskAdapter;
import com.aideus.tasky.alarm.AlarmHelper;
import com.aideus.tasky.dialog.EditTaskDialogFragment;
import com.aideus.tasky.model.Item;
import com.aideus.tasky.model.ModelTask;

public abstract class TaskFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;
    public MainActivity activity;
    public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }

        // Get instance of AlarmHelper
        alarmHelper = AlarmHelper.getInstance();

        // Add all existing tasks from DB.
        addTaskFromDB();
    }

    // Add task ModelTask using RecyclerView.Adapter to ArrayList and RecyclerView.
    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    // Update task (ModelTask)
    public void updateTask(ModelTask task) {
        adapter.updateTask(task);
    }

    public void removeTaskDialog(final int location) {

        // Init Builder to built AlertDialog for removing tasks.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        // Set AlertDialog title.
        dialogBuilder.setMessage(R.string.dialog_removing_message);

        // Get item from from ArrayList by its position.
        Item item = adapter.getItem(location);

        // If item is task...
        if (item.isTask()) {

            // Get ModelTask to remove from item.
            ModelTask removingTask = (ModelTask) item;

            // Get its timeStamp
            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            // setPositiveButton for AlertDialog
            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Remove item from items ArrayList
                    adapter.removeItem(location);
                    isRemoved[0] = true;

                    // Create Snackbar its text and cancel button action.
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // If cancel button is pressed than get task from DB by timeStamp and add it to list.
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });

                    // Add listener for attach state changes.
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {
                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            // When snackbar is DetachedFromWindow and if task isRemoved than
                            // remove its alarm and data from DB by timeStamp.
                            if (isRemoved[0]) {
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removeTask(timeStamp);
                            }

                        }
                    });
                    snackbar.show();
                    dialog.dismiss();
                }
            });
            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        dialogBuilder.show();
    }

    //TODO
    public void showTaskEditDialog(ModelTask task) {
        DialogFragment editTaskDialog = EditTaskDialogFragment.newInstance(task);
        editTaskDialog.show(getActivity().getFragmentManager(), "EditTaskDialogFragment");
    }

    // Abstract method to search tasks.
    public abstract void findTasks(String title);

    // Abstract method to check if instance of adapter exists.
    public abstract void checkAdapter();

    // Abstract method to add all current or done tasks from DB.
    public abstract void addTaskFromDB();

    // Abstract method to move tasks from current to done lists and back.
    public abstract void moveTask(ModelTask task);
}
