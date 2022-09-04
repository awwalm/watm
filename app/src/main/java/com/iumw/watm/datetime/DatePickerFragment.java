package com.iumw.watm.datetime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.iumw.watm.tasks.AssignTaskFragment;

import java.util.Calendar;

// @TODO currently not in use because it only allows singular binding
//  @TODO but is maintained here for potential future use
// remote dialog fragment for enabling date and time picker
public class DatePickerFragment extends DialogFragment
{
    AssignTaskFragment assignTaskFragment = new AssignTaskFragment();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // the context represents this particular DatePickerFragment displaying the Fragment
        // the listener is a fragment which is then cast to OnDateListener
        // year, month, and day is then given so it is displayed accordingly
        return new DatePickerDialog(getContext(),
                (DatePickerDialog.OnDateSetListener) getTargetFragment(),
                year,
                month,
                day);
    }
}
