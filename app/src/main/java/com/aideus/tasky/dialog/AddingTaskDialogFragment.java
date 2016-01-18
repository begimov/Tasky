package com.aideus.tasky.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aideus.tasky.R;
import com.aideus.tasky.Utils;
import com.aideus.tasky.alarm.AlarmHelper;
import com.aideus.tasky.model.ModelTask;

import java.util.Calendar;

// A fragment that displays a dialog window, floating on top of its activity's window.
public class AddingTaskDialogFragment extends DialogFragment {

    // Callback interface required for host activity to implement.
    private AddingTaskListener addingTaskListener;

    public interface AddingTaskListener {
        void onTaskAdded(ModelTask newTask);
        void onTaskAddingCancel();
    }

    // Check if AddingTaskListener callback interface is implemented in host activity.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addingTaskListener = (AddingTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement AddingTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initializing builder for an alert dialog.
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Setting dialog title using the given resource id.
        builder.setTitle(R.string.dialog_title);

        // Initializing LayoutInflater which instantiates layout XML file into its corresponding View objects.
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflating new view hierarchy from the specified xml resource.
        View container = inflater.inflate(R.layout.dialog_task, null);

        // Initializing Title, Date and Time EditTexts and TextInputLayouts warping them in earlier inflated container.
        // TextInputLayout wraps EditText to show a floating label and supports showing an error.
        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();
        final TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();
        TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        // Initializing Spinner
        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        // Setting TextInputLayouts hint texts to display when the text is empty.
        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));

        // Setting our custom view to be the contents of the alert dialog.
        builder.setView(container);

        // Initializing task object from my ModelTask class.
        final ModelTask task = new ModelTask();

        // Initialize and set ArrayAdapter for spPriority Spinner to get and view data from ModelTask.PRIORITY_LEVELS array.
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);
        spPriority.setAdapter(priorityAdapter);

        // Setting OnItemSelectedListener for spPriority Spinner.
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When item is selected set priority of task ModelTask using its position.
                task.setPriority(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Initializing Calendar an class for converting between a Date object and a set of integer fields such as YEAR, MONTH, DAY, HOUR, and so on.
        // A Date object represents a specific instant in time with millisecond precision.
        final Calendar calendar = Calendar.getInstance();

        // Setting HOUR_OF_DAY field to the current value + 1.
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

        // Setting OnClickListener on etDate EditText.
        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Adding space to EditText if it's empty.
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }

                // Creating new DatePickerFragment dialog.
                DialogFragment datePickerFragment = new DatePickerFragment() {

                    // The callback used to indicate the user is done filling in the date (e.g. they clicked on the 'OK' button).
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Setting fields in calendar with user values when he's done filling in the date.
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Setting date to etDate EditText with value which is converted to user-friendly format from date in calendar.
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }
                    // If datePickerFragment dialog canceled etDate EditText becomes empty.
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                // Show datePickerFragment dialog with android.widget.DatePicker.
                datePickerFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        // Setting OnClickListener on etTime EditText.
        etTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Adding space to EditText if it's empty.
                if (etTime.length() == 0) {
                    etTime.setText(" ");
                }

                // Creating new TimePickerFragment dialog.
                DialogFragment timePickerFragment = new TimePickerFragment() {

                    // The callback used to indicate the user is done filling in the time (e.g. they clicked on the 'OK' button).
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        // Setting fields in calendar with user values when he's done filling in the time.
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        // Setting time to etTime EditText with value which is converted to user-friendly format from time in calendar.
                        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    // If datePickerFragment dialog canceled etTime EditText becomes empty.
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };
                // Show timePickerFragment dialog with android.widget.TimePicker.
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        // Set a listener to be invoked when the positive button of the dialog is pressed.
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //When positive button is pressed, set title of task ModelTask with text from etTitle EditText.
                task.setTitle(etTitle.getText().toString());

                // Set status of task to current.
                task.setStatus(ModelTask.STATUS_CURRENT);

                // If etDate EditText or etTime EditText not empty, set date of task ModelTask from calendar.
                if (etDate.length() != 0 || etTime.length() != 0) {
                    task.setDate(calendar.getTimeInMillis());

                    // Get AlarmHelper single-existing instance and setAlarm for new task created.
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }

                // Setting task's status to STATUS_CURRENT;
                task.setStatus(ModelTask.STATUS_CURRENT);

                // Use callback interface implemented in host activity to notify about new task and add it.
                addingTaskListener.onTaskAdded(task);

                // Close dialog.
                dialog.dismiss();
            }
        });

        // Set a listener to be invoked when the negative button of the dialog is pressed.
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Use callback interface implemented in host activity to notify about cancelling task adding.
                addingTaskListener.onTaskAddingCancel();

                // Close dialog.
                dialog.cancel();
            }
        });

        // Create AlertDialog with the arguments supplied earlier to this builder.
        AlertDialog alertDialog = builder.create();

        // Set listener to alertDialog which will be invoked when the dialog is shown.
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                // Initialize positiveButton with its identifier.
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                // if etTitle EditText is empty, disable positiveButton and set error to tilTitle TextInputLayout;
                if (etTitle.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                }

                // Add TextWatcher to the list of those whose methods are called whenever this TextView's text changes.
                // TextWatcher - when an object of a type is attached to an Editable, its methods will be called when the text is changed.
                etTitle.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    // When text in etTitle changes and its empty, disable positiveButton and set error to tilTitle TextInputLayout;
                    // When text in etTitle changes and its not empty, enable positiveButton and remove error in tilTitle TextInputLayout;
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                            tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                        } else {
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        });

        // Return built AlertDialog.
        return alertDialog;
    }
}
