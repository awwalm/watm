package com.iumw.watm.tasks;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iumw.watm.HomeFragment;
import com.iumw.watm.LoginFragment;
import com.iumw.watm.R;
import com.iumw.watm.datetime.TimePickerFragment;
import com.iumw.watm.portal.StaffPortalAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * This is where Task Assignment operations are processed (among other things)
 * Activities that contain this fragment must implement the
 * {@link AssignTaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AssignTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignTaskFragment extends Fragment
        implements View.OnTouchListener, View.OnFocusChangeListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // menu destinations
    private HomeFragment homeFragment;
    private LoginFragment loginFragment;
    private TasksFragment tasksFragment;
    private FragmentTransaction transaction;

    // Buttons
    private ExtendedFloatingActionButton assignTaskNowEFab;
    private ExtendedFloatingActionButton assignTaskLaterEFab;


    // Firebase variables
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    // strip all special characters to create unique username string
    String plainEmail = currentUser.getEmail().replaceAll("[-+.@^:,]","");

    // TextInputLayouts we want to set disabled/enabled
    TextInputLayout outlinedTargetUserEmailTextField;
    TextInputLayout outlinedTargetUserNameTextField;

    // time and date fields and buttons
    static EditText startDateEditText;
    static EditText endDateEditText;
    Button startDateButton;
    Button endDateButton;
    Button startTimeButton;
    Button endTimeButton;

    // for monitoring the exact due date
    Date EXACT_DUE_DATE_TIME = new Date(); //new Date(System.currentTimeMillis());

    /**to be called publicly from {@link com.iumw.watm.portal.StaffPortalAdapter#onBindViewHolder(StaffPortalAdapter.StaffPortalAdapterViewHolder, int)} */
    public EditText CALL_TARGET_USER_NAME_EDIT_TEXT;
    public EditText CALL_TARGET_USER_EMAIL_EDIT_TEXT;

    // tasks important prerequisite database fields

    EditText editTargetUserNameEditText;
    EditText editTargetUserEmailEditText;
    EditText editCollaboratorEmailEditText;
    EditText taskTitleEditText;
    EditText taskDescriptionEditText;
    public EditText dynamicStartDateEditText;
    public EditText dynamicEndDateEditText;
    EditText startTimeEditText;
    EditText endTimeEditText;
    EditText planDurationEditText;
    EditText extraDurationEditText;
    EditText rawDurationEditText;
    private NumberPicker priorityPicker; // priority selector
    private EditText notationEditText;

    // static date and time inner class global variables
    private static int startYear; // mYear1
    private static int startMonth; // mMonth1
    private static int startDay; // mDay1
    private static int endYear; // mYear2
    private static int endMonth; // mMonth2
    private static int endDay; // mDay2

    // miscellaneous members
    ScrollView atfScrollView;   // the ScrollView in the AssignTaskFragment
    private OnFragmentInteractionListener mListener;  // for event listener

    // Required empty public constructor
    public AssignTaskFragment() {   }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignTaskFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static AssignTaskFragment newInstance(String param1, String param2)
    {
        AssignTaskFragment fragment = new AssignTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // onCreateView() FUNCTION ////////////////////////////////////////////////////////////////
    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.task_assignment));

        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assign_task, container, false);

        // initialize public edit texts
        CALL_TARGET_USER_NAME_EDIT_TEXT = view.findViewById(R.id.editTargetUserNameEditText);
        CALL_TARGET_USER_EMAIL_EDIT_TEXT = view.findViewById(R.id.editTargetUserEmailEditText);

        // or attempt to use SharedPreferences in case bundle fails (spoiler alert: it failed)
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        String targetEmail = sp.getString("TARGET_EMAIL", null);
        String targetName = sp.getString("TARGET_NAME", null);
        // get references to the corresponding EditTexts
        editTargetUserNameEditText
                = view.findViewById(R.id.editTargetUserNameEditText);
        editTargetUserEmailEditText
                = view.findViewById(R.id.editTargetUserEmailEditText);
        // finally, set and display the values
        editTargetUserNameEditText.setText(targetName);
        editTargetUserEmailEditText.setText(targetEmail);

        // initialize the collaborator's email EditText too
        editCollaboratorEmailEditText =
                view.findViewById(R.id.editCollaboratorEmailEditText);

        // initialize the EFABs
        assignTaskNowEFab = view.findViewById(R.id.assignTaskNowEFab);
        assignTaskLaterEFab = view.findViewById(R.id.assignTaskLaterEFab);

        // go back to previous fragment if user presses later
        assignTaskLaterEFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        // attempt to send the details to the database
        assignTaskNowEFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // check if there is internet access or not then disable the ability to update
                // go into strict mode to check for internet access asynchronously
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // check for internet access and network access
                    if (isInternetAvailable() && isNetworkAvailable(getContext()))
                    {
                        ExecuteAlpen();
                    }
                    else
                    {
                        Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    }
                }
                else    // device is really old so just do it anyway
                {
                    ExecuteAlpen();
                }

            }
        });

        // shrink the buttons, remove the texts,
        // and swap their icons when these text fields are touched
        editTargetUserNameEditText.setOnTouchListener(this::onTouch);
        editTargetUserEmailEditText.setOnTouchListener(this::onTouch);
        editCollaboratorEmailEditText.setOnTouchListener(this::onTouch);

        // upon scrolling, immediately shrink the floating action buttons
        atfScrollView = view.findViewById(R.id.atfScrollView);
        atfScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(
                    View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY)
                ShrinkFabs();
            }
        });


        // date selection processing -----------------------------------
        // details for start date
        String startDateString; // date1
        final String START_DATE_DIALOG = "datePicker1";
        startDateEditText = view.findViewById(R.id.startDateEditText);
        startDateButton = view.findViewById(R.id.startDateButton);

        // details for end date
        String endDateString; // date2
        final String END_DATE_DIALOG = "datePicker2";
        endDateEditText = view.findViewById(R.id.endDateEditText);
        endDateButton = view.findViewById(R.id.endDateButton);

        // processing for the start date
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShrinkFabs();
                DialogFragment newStartDateFragment = new StartDateFragment();
                newStartDateFragment.show(getFragmentManager(), START_DATE_DIALOG);
            }
        });

        // processing for the end date
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShrinkFabs();
                DialogFragment newEndDateFragment = new EndDateFragment();
                newEndDateFragment.show(getFragmentManager(), END_DATE_DIALOG);
            }
        });

        //------------------------------------------------------------


        // time selection processing -----------------------------------
        // details for start time
        final String START_TIME_DIALOG = "START TIME";
        startTimeButton = view.findViewById(R.id.startTimeButton);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   // use the custom TimePickerFragment to parse in the
                // text view and current context, shrink the FABs too
                ShrinkFabs();
                TimePickerFragment timePickerFragment =
                        new TimePickerFragment(startTimeEditText, getTargetFragment());
                timePickerFragment.show(getChildFragmentManager(), START_TIME_DIALOG);
            }
        });

        // details for end time
        final String END_TIME_DIALOG = "END TIME";
        endTimeButton = view.findViewById(R.id.endTimeButton);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShrinkFabs();
                TimePickerFragment timePickerFragment =
                        new TimePickerFragment(endTimeEditText, getTargetFragment());
                timePickerFragment.show(getChildFragmentManager(), END_TIME_DIALOG);

                // immediately set the value of the exact date date
            }
        });

        // assign values for the priority picker
        final String HIGH_P = getString(R.string.high_priority);
        final String MEDIUM_P = getString(R.string.medium_priority);
        final String LOW_P = getString(R.string.low_priority);
        final String ALL_PRIORITIES = HIGH_P + "\n" + MEDIUM_P + "\n" + LOW_P;
        priorityPicker = view.findViewById(R.id.priorityPicker);
        int valuePicked = priorityPicker.getValue();
        priorityPicker.setMinValue(1);
        priorityPicker.setMaxValue(3);
        priorityPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                switch (newVal)
                {   // display the priority messages as the user changes the priority
                    case 1:
                        Toast.makeText(getContext(), HIGH_P, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), MEDIUM_P, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), LOW_P, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        //---------------------------------------------------------------

        return view;

    } /**end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/  //////////////////////////////////////////////////////////




    // ********************************************************************************

    // firstly create a function for rounding large numeric double values
    public static double RoundDouble(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // operation for processing the task assignment and sending it to the database
    public void ExecuteAlpen()
    {
        // first thing is first, code might look ugly but field references are needed again
        assignTaskNowEFab = getView().findViewById(R.id.assignTaskNowEFab);
        assignTaskLaterEFab = getView().findViewById(R.id.assignTaskLaterEFab);
        startTimeButton = getView().findViewById(R.id.startTimeButton);
        endTimeButton = getView().findViewById(R.id.endTimeButton);
        startDateButton = getView().findViewById(R.id.startDateButton);
        endDateButton = getView().findViewById(R.id.endDateButton);

        //

        // ALPEN TASK FIELDS
        editTargetUserNameEditText = getView().findViewById(R.id.editTargetUserNameEditText);
        editTargetUserEmailEditText = getView().findViewById(R.id.editTargetUserEmailEditText);
        editCollaboratorEmailEditText = getView().findViewById(R.id.editCollaboratorEmailEditText);

        // ACTIVITY DATA FIELDS
        taskTitleEditText = getView().findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = getView().findViewById(R.id.taskDescriptionEditText);

        // LENGTH DATA FIELDS
        dynamicStartDateEditText = getView().findViewById(R.id.startDateEditText);
        dynamicEndDateEditText = getView().findViewById(R.id.endDateEditText);
        startTimeEditText = getView().findViewById(R.id.startTimeEditText);
        endTimeEditText = getView().findViewById(R.id.endTimeEditText);

        // PLANNING DATA FIELDS
        planDurationEditText = getView().findViewById(R.id.planDurationEditText);
        extraDurationEditText = getView().findViewById(R.id.extraDurationEditText);
        rawDurationEditText = getView().findViewById(R.id.rawDurationEditText);

        // PRIORITY DATA FIELDS
        priorityPicker = getView().findViewById(R.id.priorityPicker);

        // NOTATION DATA FIELDS
        notationEditText = getView().findViewById(R.id.notationEditText);

        // calculate ALPEN time and duration difference
        try
        {
            String startTime = startTimeEditText.getText().toString().trim();
            String endTime = endTimeEditText.getText().toString().trim();
            String startDate = dynamicStartDateEditText.getText().toString().trim();
            String endDate = dynamicEndDateEditText.getText().toString().trim();

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date date1 = format.parse(startTime);
            Date date2 = format.parse(endTime);
            long timeDifference = date2.getTime() - date1.getTime();

            // alternative routine for date differences alone
            long diffInSeconds = timeDifference / 1000 % 60;
            long diffInMinutes = timeDifference / (60 * 1000) % 60;
            long diffInHours = timeDifference / (60 * 60 * 1000) % 24;
            long diffInDays = timeDifference / (24 * 60 * 60 * 1000);

            // planning
            double planningTimeSeconds = 0.6 * diffInSeconds;
            double planningTimeMinutes = 0.6 * diffInMinutes;
            double planningTimeHours = 0.6 * diffInHours;
            double planningTimeDays = 0.6 * diffInDays;

            // extra
            double extraTimeSeconds = 0.4 * diffInSeconds;
            double extraTimeMinutes = 0.4 * diffInMinutes;
            double extraTimeHours = 0.4 * diffInHours;
            double extraTimeDays = 0.4 * diffInDays;



            // for alarm settable values for planning time
            Date planningAlarmDate = new Date();
            planningAlarmDate.setTime((long) (date2.getTime() - timeDifference*0.4));
            Log.d("TIME EXPERIMENT", String.valueOf(
                    planningAlarmDate.getHours()+":"+planningAlarmDate.getMinutes()));
            String PLANNING_HOUR_ALARM_TIME = String.valueOf(planningAlarmDate.getHours());
            String PLANNING_MINUTE_ALARM_TIME = String.valueOf(planningAlarmDate.getMinutes());

            // for alarm settable values for extra time
            Date extraAlarmDate = new Date();
            extraAlarmDate.setTime((long) (date2.getTime() - timeDifference*0.6));
            String EXTRA_HOUR_ALARM_TIME = String.valueOf(extraAlarmDate.getHours());
            String EXTRA_MINUTE_ALARM_TIME = String.valueOf(extraAlarmDate.getMinutes());

            // for alarm settable values for actual raw time (also retrievable from Firebase)
            Date rawAlarmDate = new Date();
            rawAlarmDate.setTime(date2.getTime());
            String RAW_HOUR_ALARM_TIME = String.valueOf(rawAlarmDate.getHours());
            String RAW_MINUTE_ALARM_TIME = String.valueOf(rawAlarmDate.getMinutes());

            // get EXACT DUE TIME and EXACT DUE DATE of the task from SharedPreferences
            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
            final String EXACT_DUE_TIME = sp.getString("EXACT_DUE_TIME", null);
            final String EXACT_DUE_DATE = sp.getString("EXACT_DUE_DATE", null);


            // raw duration in millisecond (to be used for alarm)
            final String RAW_DURATION_INFO =
                    "Raw Duration in Milliseconds: \n" +
                        Long.toString(timeDifference) + " ms";

            // original duration
            final String DURATION_INFO =
                    "Task Duration: \n" +
                    Long.toString(diffInDays) + " Days\n" +
                    Long.toString(diffInHours) + " Hours\n" +
                    Long.toString(diffInMinutes) + " Minutes\n" +
                    Long.toString(diffInSeconds) + " Seconds";

            // planning duration as per ALPEN
            final String PLANNING_TIME_INFO =
                    "ALPEN Planning Duration: \n" +
                        Double.toString(planningTimeDays) + " Days\n" +
                        Double.toString(RoundDouble(planningTimeHours, 2)) + " Hours\n" +
                        Double.toString(RoundDouble(planningTimeMinutes, 2)) + " Minutes\n" +
                        Double.toString(planningTimeSeconds) + " Seconds";

            // extra time duration
            final String EXTRA_TIME_INFO =
                    "ALPEN Extra Time Duration: \n" +
                        Double.toString(extraTimeDays) + " Days\n" +
                        Double.toString(RoundDouble(extraTimeHours, 2)) + " Hours\n" +
                        Double.toString(RoundDouble(extraTimeMinutes, 2)) + " Minutes\n" +
                        Double.toString(extraTimeSeconds) + " Seconds";


            double sixtyPercent = 0.6 * timeDifference;
            double fortyPercent = 0.4 * timeDifference;
            double castTimeDiffToDouble = 1.0 * timeDifference;

            // ^^^^^^^^^ @TODO: find a way to avoid this next time ^^^^^^^^^^^^^^^^^^^^^^^
            String checkTargetUserName = editTargetUserNameEditText.getText().toString();
            String checkTargetUserEmail = editTargetUserEmailEditText.getText().toString();
            String checkCollaboratorEmail = editCollaboratorEmailEditText.getText().toString();
            String checkTaskTitle = taskTitleEditText.getText().toString();
            String checkTaskDescription = taskDescriptionEditText.getText().toString();
            String checkStartDate = dynamicStartDateEditText.getText().toString();
            String checkEndDate = dynamicEndDateEditText.getText().toString();
            String checkStartTime = startTimeEditText.getText().toString();
            String checkEndTime = endTimeEditText.getText().toString();
            String checkPlanDuration = planDurationEditText.getText().toString();
            String checkExtraDuration = extraDurationEditText.getText().toString();
            String checkRawDuration = rawDurationEditText.getText().toString();
            String checkPriority = Integer.toString(priorityPicker.getValue());
            String checkNotation = notationEditText.getText().toString();
            // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

            // in case the time difference is negative
            if (timeDifference <= 3)
            {   // display notification for invalid time
                final String TIMEDIFFBAD =
                        getString(R.string.invalid_time_difference);
                Snackbar.make(getView(), TIMEDIFFBAD, Snackbar.LENGTH_LONG).show();
                return;
            }
            else
            {   // validate the fields to ensure they're not empty!
                if (  !(checkTargetUserName.isEmpty()) && !(checkTargetUserEmail.isEmpty()) &&
                     !(checkTaskTitle.isEmpty()) && !(checkTaskDescription.isEmpty()) &&
                    !(checkStartTime.isEmpty()) && !(checkEndTime.isEmpty()) &&
                    !(checkPriority.isEmpty()) && !(checkNotation.isEmpty())   )
                {
                    // display notification for all identified timings
                    Toast.makeText(getContext(), DURATION_INFO, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), PLANNING_TIME_INFO, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), EXTRA_TIME_INFO, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), RAW_DURATION_INFO, Toast.LENGTH_SHORT).show();

                    // set the respective values
                    planDurationEditText.setText(Double.toString(sixtyPercent) + " ms");
                    extraDurationEditText.setText(Double.toString(fortyPercent) + " ms");
                    rawDurationEditText.setText(Double.toString(castTimeDiffToDouble) + " ms");


                    // FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
                    // now we attempt to send everything to the database
                    // create database variable within try-catch vault
                    try
                    {
                        FirebaseAuth auth;
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseDatabase database;
                        DatabaseReference mainRef;
                        DatabaseReference notificationRef;

                        // check to see if the user is valid, if not go back to where you came from
                        if (currentUser == null) {
                            Toast incompleteSignInToast =
                                Toast.makeText(
                                    getContext(), R.string.incomplete_signin, Toast.LENGTH_SHORT);
                            incompleteSignInToast.show();
                            return;
                        }

                        // strip all special characters to create unique username string
                        String plainEmail =
                            currentUser.getEmail().replaceAll("[-+.@^:,]", "");

                        // obtain all necessary field values
                        String getCreatorEmail = currentUser.getEmail();
                        String getCreatorName = currentUser.getDisplayName();
                        String getTargetUserName = editTargetUserNameEditText.getText().toString();
                        String getTargetUserEmail = editTargetUserEmailEditText.getText().toString();
                        String getCollaboratorEmail = editCollaboratorEmailEditText.getText().toString();
                        String getTaskTitle = taskTitleEditText.getText().toString();
                        String getTaskDescription = taskDescriptionEditText.getText().toString();
                        String getStartDate = dynamicStartDateEditText.getText().toString();
                        String getEndDate = dynamicEndDateEditText.getText().toString();
                        String getStartTime = startTimeEditText.getText().toString();
                        String getEndTime = endTimeEditText.getText().toString();
                        String getPlanDuration = planDurationEditText.getText().toString();
                        String getExtraDuration = extraDurationEditText.getText().toString();
                        String getRawDuration = rawDurationEditText.getText().toString();
                        String getPriority = Integer.toString(priorityPicker.getValue());
                        String getNotation = notationEditText.getText().toString();

                        // generate the reference to the tasks database
                        mainRef = FirebaseDatabase.getInstance().getReference().child("tasks");
                        // get a push reference to a single id key-node
                        final String key = mainRef.push().getKey();
                        final String taskId = key;
                        String status = getString(R.string.ongoing);
                        // start sending the values to the database
                        mainRef.child(key).child("taskId").setValue(taskId);
                        mainRef.child(key).child("creatorEmail").setValue(getCreatorEmail);
                        mainRef.child(key).child("creatorName").setValue(getCreatorName);
                        mainRef.child(key).child("targetUserName").setValue(getTargetUserName);
                        mainRef.child(key).child("targetUserEmail").setValue(getTargetUserEmail);
                        mainRef.child(key).child("collaboratorEmail").setValue(getCollaboratorEmail);
                        mainRef.child(key).child("taskTitle").setValue(getTaskTitle);
                        mainRef.child(key).child("taskDescription").setValue(getTaskDescription);
                        mainRef.child(key).child("startDate").setValue(getStartDate);
                        mainRef.child(key).child("endDate").setValue(getEndDate);
                        mainRef.child(key).child("startTime").setValue(getStartTime);
                        mainRef.child(key).child("endTime").setValue(getEndTime);
                        mainRef.child(key).child("planDuration").setValue(getPlanDuration);
                        mainRef.child(key).child("extraDuration").setValue(getExtraDuration);
                        mainRef.child(key).child("rawDuration").setValue(getRawDuration);
                        mainRef.child(key).child("priority").setValue(getPriority);
                        mainRef.child(key).child("notation").setValue(getNotation);
                        mainRef.child(key).child("status").setValue(status);

                        mainRef.child(key).child("isCreatorPlanAlarmSet").setValue(String.valueOf(false));
                        mainRef.child(key).child("isCreatorRawAlarmSet").setValue(String.valueOf(false));
                        mainRef.child(key).child("isCollaboratorPlanAlarmSet").setValue(String.valueOf(false));
                        mainRef.child(key).child("isCollaboratorRawAlarmSet").setValue(String.valueOf(false));
                        mainRef.child(key).child("isTargetUserPlanAlarmSet").setValue(String.valueOf(false));
                        mainRef.child(key).child("isTargetUserRawAlarmSet").setValue(String.valueOf(false));

                        mainRef.child(key).child("planHourAlarmTime").setValue(PLANNING_HOUR_ALARM_TIME);
                        mainRef.child(key).child("planMinuteAlarmTime").setValue(PLANNING_MINUTE_ALARM_TIME);
                        mainRef.child(key).child("extraHourAlarmTime").setValue(EXTRA_HOUR_ALARM_TIME);
                        mainRef.child(key).child("extraMinuteAlarmTime").setValue(EXTRA_MINUTE_ALARM_TIME);
                        mainRef.child(key).child("rawHourAlarmTime").setValue(RAW_HOUR_ALARM_TIME);
                        mainRef.child(key).child("rawMinuteAlarmTime").setValue(RAW_MINUTE_ALARM_TIME);

                        mainRef.child(key).child("exactDueDate").setValue(EXACT_DUE_DATE);
                        mainRef.child(key).child("exactDueTime").setValue(EXACT_DUE_TIME);

                        mainRef.child(key).child("isTargetUserAskingForHelp").setValue(String.valueOf(false));
                        mainRef.child(key).child("isCollaboratorAskingForHelp").setValue(String.valueOf(false));


                        // quick test to check if values are the same
                        Log.d("CHECK_TIME",
                                RAW_HOUR_ALARM_TIME+":"+RAW_MINUTE_ALARM_TIME+"\n"
                                + getEndTime);


                        // send notification details to the database
                        // generate reference to the notifications node
                        notificationRef =
                            FirebaseDatabase.getInstance().getReference().child("notifications");
                        // start by getting sender and receiver details
                        // re-referencing the plainEmail variable for convenience
                        String senderUserId =
                                currentUser.getEmail().replaceAll("[-+.@^:,]", "");
                        String receiverUserId =
                                getTargetUserEmail.replaceAll("[-+.@^:,]", "");
                        String senderExactName =
                                currentUser.getDisplayName();
                        String senderExactEmail =
                                currentUser.getEmail();
                        // then creating HashMap object
                        HashMap<String, String> taskNotificationMap = new HashMap<>();
                        // declare the use case of the HashMap
                        taskNotificationMap.put("from", senderUserId);
                        taskNotificationMap.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                        taskNotificationMap.put("type", "newtask");
                        taskNotificationMap.put("taskTitle", getTaskTitle);
                        taskNotificationMap.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                        // structure the notification insertion with genuine push IDs
                        notificationRef.child(receiverUserId)
                            .push()
                            .setValue(taskNotificationMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(), R.string.notify_new_task,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        // also send a notification to the collaborator if there's one
                        if (    Patterns.EMAIL_ADDRESS.matcher(getCollaboratorEmail).matches() &&
                                !getCollaboratorEmail.isEmpty()    )
                        {
                            String secondReceiverId =
                                getCollaboratorEmail.replaceAll("[-+.@^:,]", "");
                            HashMap<String, String> taskNotificationMap2 = new HashMap<>();
                            taskNotificationMap2.put("from", senderUserId);
                            taskNotificationMap2.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                            taskNotificationMap2.put("type", "newtask");
                            taskNotificationMap2.put("taskTitle", getTaskTitle);
                            taskNotificationMap2.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                            notificationRef.child(secondReceiverId)
                                .push()
                                .setValue(taskNotificationMap2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getContext(), R.string.notify_new_task,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }



                        // then make the text field empty so the user can't reuse the details
                        editTargetUserNameEditText.setText("");
                        editTargetUserEmailEditText.setText("");

                        // also reset the exact date and times
                        SharedPreferences spClear = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editClear = spClear.edit();
                        editClear.putString("EXACT_DUE_TIME", null);
                        editClear.putString("EXACT_DUE_DATE", null);
                        editClear.apply();

                        // go back to the list of tasks and show TasksFragment
                        tasksFragment = new TasksFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer, tasksFragment);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        //transaction.addToBackStack(null);
                        transaction.commit(); // display TasksFragment
                        Toast.makeText(
                            getContext(), R.string.assign_successful, Toast.LENGTH_SHORT).show();

                    }
                    catch (NullPointerException npe)
                    {
                        npe.getCause();
                        npe.printStackTrace();
                        Log.d("TASK_I", "ERROR INSERTING TASK TO DATABASE", npe);
                        Snackbar.make(
                            getView(), R.string.missing_values, Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    catch (NoSuchElementException nse)
                    {
                        nse.printStackTrace();
                    }

                }
                else
                {   // surely now, that means some fields are still yet to be filled
                    Snackbar.make(
                        getView(), R.string.missing_values, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }


        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
            Snackbar.make(
                getView(), R.string.empty_fields_warning, Snackbar.LENGTH_SHORT).show();
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }

    } /** end {@link #ExecuteAlpen()}*/


    // ********************************************************************************



    // utility to check for NETWORK
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    // utility to check for INTERNET
    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }





    // function for shrinking the FABs
    public void ShrinkFabs()
    {
        assignTaskNowEFab = getView().findViewById(R.id.assignTaskNowEFab);
        assignTaskLaterEFab = getView().findViewById(R.id.assignTaskLaterEFab);

        assignTaskNowEFab.setText(null);
        //assignTaskNowEFab.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        assignTaskNowEFab.setWidth(50);
        //assignTaskNowEFab.setIconGravity(MaterialButton.ICON_GRAVITY_END);
        assignTaskNowEFab.setPadding(30, 15, 0, 15);
        // change the icon
        Drawable checkIcon = getResources().getDrawable(R.drawable.ic_check);
        assignTaskNowEFab.setIcon(checkIcon);

        // repeat the same process above for the other button
        assignTaskLaterEFab.setText(null);
        assignTaskLaterEFab.setWidth(50);
        assignTaskLaterEFab.setPadding(30, 15, 0, 15);
        Drawable closeIcon = getResources().getDrawable(R.drawable.ic_close);
        assignTaskLaterEFab.setIcon(closeIcon);
    }

    // inner class DialogFragment for StartDateFragment
    public static class StartDateFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // get current time and date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // create a unique instance
            DatePickerDialog theDatePickerDialog = new DatePickerDialog(
                    getContext(), R.style.DatePicker1, this, year, month, day);

            // limit the date selection range
            theDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            theDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000);

            return theDatePickerDialog;

            // create a new instance of DatePickerDialog and return it
           /* return new DatePickerDialog(
                    getContext(), R.style.DatePicker1, this, year, month, day);*/
        }

        // mandatory onDateSet method
        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // get selected date
            startYear = year;
            startMonth = month;
            startDay = day;

            // show selected date
            startDateEditText.setText(new StringBuilder()
                .append(startYear).append("-")
                .append(startMonth + 1).append("-")
                .append(startDay).append(" "));
        }
    }

    // inner class DialogFragment for EndDateFragment
    public static class EndDateFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // get least recent time and date
            /*String date = endDateEditText.getText().toString().trim();
            String[] data = date.split("-", 3);
            int year = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1])-1; // January 0th is impossible
            int day = Integer.parseInt(data[2]);*/
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // create a unique instance
            DatePickerDialog theDatePickerDialog = new DatePickerDialog(
                    getContext(), R.style.DatePicker2, this, year, month, day);

            // limit the selection range (maximum 7 days into the future)
            theDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            theDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 86400000*7);
            return theDatePickerDialog;

        }

        // mandatory onDateSet method
        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // get selected date
            endYear = year;
            endMonth = month;
            endDay = day;

            // format proper date
            Date dueDate = new Date(year, month, day);

            // show selected date
            endDateEditText.setText(new StringBuilder()
                    .append(endYear).append("-")
                    .append(endMonth + 1).append("-")
                    .append(endDay).append(" "));

            // immediately pass value to shared preferences
            if (endDateEditText.getId() == R.id.endDateEditText)
            {
                // store value in shared preferences
                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("EXACT_DUE_DATE", dueDate.toString());
                edit.apply();
            }

        }
    }


    // general onTouch listener event for shrinking the buttons when typing
    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view instanceof EditText || view instanceof TextInputLayout)
        {
            view.setOnFocusChangeListener(this); // User touched EditText
            // remove the text and reduce the size of the button
            // so it doesn't obstruct typing too much
            ShrinkFabs();
        }

        return false;
    }


    // onFocusChange event
    @Override
    public void onFocusChange(View v, boolean hasFocus) {    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater2)
    {
        //MenuInflater menuInflater = getMenuInflater();
        menu.clear();
        inflater2.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.menuHome:
                // create HomeFragment
                homeFragment = new HomeFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, homeFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit(); // display HomeFragment
                Toast.makeText(getContext(), R.string.home, Toast.LENGTH_SHORT).show();
                break;

            // if Logout was selected
            case R.id.menuLogout:
                // create a dialog for confirmation that sets up the entire logout operation
                final MaterialAlertDialogBuilder builder =
                        new MaterialAlertDialogBuilder(getContext());
                // set the title of the logout operation
                builder.setTitle(R.string.confirm_logout);
                builder.setMessage(R.string.logout_message);
                // set the background of the dialog as inspired by Material Design
                builder.setBackground(getResources()
                        .getDrawable(R.drawable.alert_dialog_bg, null));

                // adding the OK button to confirm logout followed by the action to perform
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            AuthUI.getInstance().signOut(getContext()).addOnCompleteListener
                            (new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        try
                                        {   // create variable for the login page
                                            // using fragment style
                                            loginFragment = new LoginFragment();
                                            // initialize fragment transaction manager
                                            transaction =
                                                    getFragmentManager().beginTransaction();
                                            // begin swapping process
                                            transaction.replace
                                                    (R.id.fragmentContainer, loginFragment);
                                            transaction.setTransition(FragmentTransaction
                                                    .TRANSIT_FRAGMENT_FADE);
                                            transaction.addToBackStack(null);
                                            // finally take us back to the sign-in page
                                            transaction.commit();
                                        }
                                        // in case the previous page is not in memory
                                        // attempt the same routine using activity style
                                        catch (NullPointerException npe)
                                        {
                                            startActivity(
                                                LoginFragment.createIntent(getContext()));
                                            getActivity().finish();
                                        }
                                        // the previous step works whenever it's reattempted
                                        finally
                                        {
                                            startActivity(
                                                LoginFragment.createIntent(getContext()));
                                            getActivity().finish();
                                        }

                                    }
                                    else
                                    {   // Sign out failed

                                    }
                                }
                            });

                            // confirm operation
                            Toast.makeText(
                                getContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
                        }
                        catch (IllegalStateException ise)
                        {
                            if (getView() != null)
                            {Snackbar.make(
                                getView(), R.string.try_again, Snackbar.LENGTH_LONG).show();
                            }
                        }

                    }
                });

                // in case user changes mind
                builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
                        return;
                    }
                });

                // finally show said dialog
                builder.show();
        }

        return true;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
