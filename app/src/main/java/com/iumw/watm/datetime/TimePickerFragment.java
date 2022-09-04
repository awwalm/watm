package com.iumw.watm.datetime;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.iumw.watm.R;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// class for generating two TimePickerDialogs within one layout
public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener
{

    TextView textView;
    Context context;
    Fragment fragment;
    // for monitoring the exact due date
    //Date EXACT_DUE_DATE_TIME = new Date(); //new Date(System.currentTimeMillis());
    private TimePickerDialog timePickerDialog;

    // mandatory empty constructor
    public TimePickerFragment() {  }

    // constructor that parses textView and a given Context
    public TimePickerFragment(EditText textView, Context context)
    {
        this.textView = textView;
        this.context = context;
    }

    // optionally parses a Fragment since this is the basis of the application
    public TimePickerFragment(EditText textView, Fragment fragment)
    {
        this.textView = textView;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        // use the current time as the default values for the picker
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(),
                R.style.TimePicker2,
                this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
    {
        Time time = new Time(hourOfDay, minute, 0); // seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String mainTime = formatter.format(time);
        textView.setText(String.format("%d:%d", hourOfDay, minute));


        // immediately update the exact datetime and store it in shared preferences
        // for monitoring the exact due date
        if (textView.getId() == R.id.endTimeEditText)
        {
            // quick check time
            Toast.makeText(getContext(), time.toString(), Toast.LENGTH_SHORT).show();
            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("EXACT_DUE_TIME", time.toString());
            edit.apply();
        }


    }

}
