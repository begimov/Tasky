package com.aideus.tasky.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initializing Calendar
        Calendar calendar = Calendar.getInstance();

        // Getting hour and minute from calendar.
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Returning dialog containing android.widget.TimePicker with parameters we got earlier.
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    //Method of  TimePickerDialog.OnTimeSetListener interface.
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
