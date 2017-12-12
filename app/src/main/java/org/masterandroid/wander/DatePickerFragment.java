package org.masterandroid.wander;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Ivan on 11/12/2017.
 */

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener escuchador;

    public DatePickerFragment() {
        this.escuchador = escuchador;
    }
    public void setEscuchador(DatePickerDialog.OnDateSetListener escuchador){
        this.escuchador = escuchador;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), escuchador, year, month, day);
    }



}