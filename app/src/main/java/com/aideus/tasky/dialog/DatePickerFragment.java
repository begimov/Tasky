package com.aideus.tasky.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initializing Calendar
        Calendar calendar = Calendar.getInstance();

        // Getting year, month and day from calendar.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Returning dialog containing android.widget.DatePicker with parameters we got earlier.
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    //Method of DatePickerDialog.OnDateSetListener interface.
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}
