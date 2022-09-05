package com.iumw.watm.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iumw.watm.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditTaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // misc data members
    private TextInputEditText textViewForEditTaskTitle;
    private TextInputEditText textViewForEditTaskDescription;
    private TextInputEditText textViewForEditDueTime;
    private TextInputEditText textViewForEditStartDate;
    private TextInputEditText textViewForEditPlanTime;
    private TextView textViewForEditDueDate;
    private TextView textViewForEditTaskId;
    private TextView textViewForEditPlanningDuration;
    private TextView textViewForEditExtraDuration;
    private TextView textViewForEditRawDuration;
    private TextView textViewForEditStartTime;
    private TextInputEditText textViewForEditStatus;
    private Button markAsDoneButton;
    private Button setAlarmsButton;
    private Button setRawAlarmButton;
    private FragmentTransaction transaction;
    private TasksFragment tasksFragment;
    DatabaseReference completionRef;
    private Button outlinedAskForHelpButton;
    private TextView helpConfirmationTextView;

    public static final String AS_TARGET_USER = "TARGET_USER";
    public static final String AS_COLLABORATOR = "COLLABORATOR";

    // Required empty public constructor
    public EditTaskFragment() {    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTaskFragment newInstance(String param1, String param2) {
        EditTaskFragment fragment = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.edit_task));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        // get values from SharedPreferences
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        String taskId = sp.getString("HOT_TASK_ID", null);
        String taskDescription = sp.getString("HOT_TASK_DESCRIPTION", null);
        String taskTitle = sp.getString("HOT_TASK_TITLE", null);
        String startDate = sp.getString("HOT_START_DATE", null);
        String startTime = sp.getString("HOT_START_TIME", null);
        String dueTime = sp.getString("HOT_DUE_TIME", null);
        String status = sp.getString("HOT_STATUS", null);
        String creatorEmail = sp.getString("HOT_CREATOR_EMAIL", null);
        String creatorName = sp.getString("HOT_CREATOR_NAME", null);
        String targetEmail = sp.getString("HOT_TARGET_EMAIL", null);
        String collaboratorEmail = sp.getString("HOT_COLLABORATOR_EMAIL", null);
        String planDuration = sp.getString("HOT_PLANNING_DURATION", null);
        String extraDuration = sp.getString("HOT_EXTRA_DURATION", null);
        String rawDuration = sp.getString("HOT_RAW_DURATION", null);

        String planHourAlarmTime = sp.getString("HOT_PLAN_HOUR_ALARM_TIME", null);
        String planMinuteAlarmTime = sp.getString("HOT_PLAN_MINUTE_ALARM_TIME", null);
        String rawHourAlarmTime = sp.getString("HOT_RAW_HOUR_ALARM_TIME", null);
        String rawMinuteAlarmTime = sp.getString("HOT_RAW_MINUTE_ALARM_TIME", null);

        String isCreatorPlanAlarmSet = sp.getString("HOT_IS_CREATOR_PLAN_ALARM_SET", null);
        String isCreatorRawAlarmSet = sp.getString("HOT_IS_CREATOR_RAW_ALARM_SET", null);

        String isCollaboratorPlanAlarmSet = sp.getString("HOT_IS_COLLABORATOR_PLAN_ALARM_SET", null);
        String isCollaboratorRawAlarmSet = sp.getString("HOT_IS_COLLABORATOR_RAW_ALARM_SET", null);

        String isTargetUserPlanAlarmSet = sp.getString("HOT_IS_TARGET_USER_PLAN_ALARM_SET", null);
        String isTargetUserRawAlarmSet = sp.getString("HOT_IS_TARGET_USER_RAW_ALARM_SET", null);

        String isTargetUserAskingHelp = sp.getString("HOT_IS_TARGET_USER_ASKING_HELP", null);
        String isCollaboratorAskingHelp = sp.getString("HOT_IS_COLLABORATOR_ASKING_HELP", null);

        String planTime = planHourAlarmTime + ":" + planMinuteAlarmTime;

        // quick debug check to see if logic works
        Log.d("CHECK_NEW_DB_DATA",
            planHourAlarmTime +"\t"+ planMinuteAlarmTime +"\n"
              + rawHourAlarmTime +"\t"+ rawMinuteAlarmTime +"\n"
              + isCreatorPlanAlarmSet +"\t"+ isCreatorRawAlarmSet +"\n"
              + isCollaboratorPlanAlarmSet +"\t"+ isCollaboratorRawAlarmSet +"\n"
              + isTargetUserPlanAlarmSet +"\t"+ isTargetUserRawAlarmSet) ;

        Log.d("CHECK_HELP", isTargetUserAskingHelp + "\t" + isCollaboratorAskingHelp);


        // attempt to connect to Firebase and get our strings
        try
        {
            /*
            // Firebase data members
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference().child("tasks");
            DatabaseReference taskPath = mainRef.child(taskId);
            */

            textViewForEditTaskId =
                    view.findViewById(R.id.textViewForEditTaskId);
            textViewForEditTaskTitle =
                    view.findViewById(R.id.textViewForEditTaskTitle);
            textViewForEditTaskDescription =
                    view.findViewById(R.id.textViewForEditTaskDescription);
            textViewForEditStatus =
                    view.findViewById(R.id.textViewForEditStatus);
            textViewForEditStartDate =
                    view.findViewById(R.id.textViewForEditStartDate);
            textViewForEditStartTime =
                    view.findViewById(R.id.textViewForEditStartTime);
            textViewForEditPlanTime =
                    view.findViewById(R.id.textViewForEditPlanTime);
            textViewForEditDueTime =
                    view.findViewById(R.id.textViewForEditDueTime);
            textViewForEditPlanningDuration =
                    view.findViewById(R.id.textViewForEditPlanningDuration);
            textViewForEditExtraDuration =
                    view.findViewById(R.id.textViewForEditExtraDuration);
            textViewForEditRawDuration =
                    view.findViewById(R.id.textViewForEditRawDuration);
            helpConfirmationTextView =
                    view.findViewById(R.id.helpConfirmationTextView);
            outlinedAskForHelpButton =
                    view.findViewById(R.id.outlinedAskForHelpButton);
            markAsDoneButton =
                    view.findViewById(R.id.markAsDoneButton);
            setAlarmsButton =
                    view.findViewById(R.id.setAlarmsButton);
            setRawAlarmButton =
                    view.findViewById(R.id.setRawAlarmButton);


            try
            {
                // if a selected task is completed, alarms can no longer be set for it.
                if (status.equals(getString(R.string.completed)))
                {
                    markAsDoneButton.setEnabled(false);
                    markAsDoneButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                    markAsDoneButton.setBackgroundColor(Color.DKGRAY);
                    markAsDoneButton.setText(R.string.completed);

                    setAlarmsButton.setEnabled(false);
                    setAlarmsButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                    setAlarmsButton.setBackgroundColor(Color.DKGRAY);
                    setAlarmsButton.setText(R.string.alarms_already_set);

                    setRawAlarmButton.setEnabled(false);
                    setRawAlarmButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                    setRawAlarmButton.setBackgroundColor(Color.DKGRAY);
                    setRawAlarmButton.setText(R.string.alarms_already_set);

                    outlinedAskForHelpButton.setEnabled(false);
                    outlinedAskForHelpButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                    outlinedAskForHelpButton.setBackgroundColor(Color.DKGRAY);
                }

                // Firebase data members
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserEmail = currentUser != null ? currentUser.getEmail() : null;

                // check if user has already asked for help
                if (   isTargetUserAskingHelp.equals(String.valueOf(true))
                        || isCollaboratorAskingHelp.equals(String.valueOf(true))  )
                {
                    if (!currentUserEmail.equals(creatorEmail))
                    {
                        outlinedAskForHelpButton.setEnabled(false);
                        outlinedAskForHelpButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        //outlinedAskForHelpButton.setBackgroundColor(Color.DKGRAY);
                        outlinedAskForHelpButton.setText(R.string.help_requested);
                        outlinedAskForHelpButton.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_mood, 0, 0, 0);
                        helpConfirmationTextView.setVisibility(View.VISIBLE);
                    }
                }



                // logic to check who has set the alarms locally to prevent them from
                // incessantly being set repeatedly
                if (Objects.requireNonNull(currentUserEmail).equals(creatorEmail))
                {
                    if (isCreatorPlanAlarmSet.equals(String.valueOf(true)))
                    {
                        // disable the plan alarm button
                        setAlarmsButton.setEnabled(false);
                        setAlarmsButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setAlarmsButton.setBackgroundColor(Color.DKGRAY);
                        setAlarmsButton.setText(R.string.alarms_already_set);
                    }
                    if (isCreatorRawAlarmSet.equals(String.valueOf(true)))
                    {
                        // disable the raw alarm or end time alarm button
                        setRawAlarmButton.setEnabled(false);
                        setRawAlarmButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setRawAlarmButton.setBackgroundColor(Color.DKGRAY);
                        setRawAlarmButton.setText(R.string.alarms_already_set);
                    }
                }
                if (Objects.requireNonNull(currentUserEmail).equals(collaboratorEmail))
                {
                    if (isCollaboratorPlanAlarmSet.equals(String.valueOf(true)))
                    {
                        setAlarmsButton.setEnabled(false);
                        setAlarmsButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setAlarmsButton.setBackgroundColor(Color.DKGRAY);
                        setAlarmsButton.setText(R.string.alarms_already_set);
                    }
                    if (isCollaboratorRawAlarmSet.equals(String.valueOf(true)))
                    {
                        setRawAlarmButton.setEnabled(false);
                        setRawAlarmButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setRawAlarmButton.setBackgroundColor(Color.DKGRAY);
                        setRawAlarmButton.setText(R.string.alarms_already_set);
                    }
                }
                if (Objects.requireNonNull(currentUserEmail).equals(targetEmail))
                {
                    if (isTargetUserPlanAlarmSet.equals(String.valueOf(true)))
                    {
                        setAlarmsButton.setEnabled(false);
                        setAlarmsButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setAlarmsButton.setBackgroundColor(Color.DKGRAY);
                        setAlarmsButton.setText(R.string.alarms_already_set);
                    }
                    if (isTargetUserRawAlarmSet.equals(String.valueOf(true)))
                    {
                        setRawAlarmButton.setEnabled(false);
                        setRawAlarmButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        setRawAlarmButton.setBackgroundColor(Color.DKGRAY);
                        setRawAlarmButton.setText(R.string.alarms_already_set);
                    }
                }


            }
            catch (NullPointerException npe)
            {
                npe.printStackTrace();
            }

            textViewForEditTaskTitle.setText(taskTitle.toUpperCase());
            textViewForEditTaskDescription.setText(taskDescription);
            textViewForEditStatus.setText(status.toUpperCase());
            textViewForEditStartDate.setText(startDate);
            textViewForEditStartTime.setText(startTime);
            textViewForEditDueTime.setText(dueTime);
            textViewForEditPlanningDuration.setText(planDuration);
            textViewForEditExtraDuration.setText(extraDuration);
            textViewForEditRawDuration.setText(rawDuration);
            textViewForEditPlanTime.setText(planTime);

        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }


        // set on click action when user asks for help
        helpConfirmationTextView = view.findViewById(R.id.helpConfirmationTextView);
        outlinedAskForHelpButton = view.findViewById(R.id.outlinedAskForHelpButton);
        outlinedAskForHelpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Firebase data members
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference helpRef = FirebaseDatabase.getInstance().getReference().child("help");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserEmail = currentUser != null ? currentUser.getEmail() : null;

                String senderExactName = currentUser.getDisplayName();
                String senderExactEmail = currentUser.getEmail();
                String creatorUserId = creatorEmail.replaceAll("[-+.@^:,]", "");
                String senderUserId = currentUserEmail.replaceAll("[-+.@^:,]", "");

                // database reference if target user is asking for help
                DatabaseReference targetUserHelpRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(taskId)
                        .child("isTargetUserAskingForHelp");

                // database reference if collaborator is asking for help
                DatabaseReference collaboratorHelpRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(taskId)
                        .child("isCollaboratorAskingForHelp");

                // check if user hasn't already asked for help
                if (   !isTargetUserAskingHelp.equals(String.valueOf(true))
                        || !isCollaboratorAskingHelp.equals(String.valueOf(true))  )
                {
                    // send details to database
                    if (!currentUserEmail.equals(creatorEmail))
                    {
                        // ask creator for help as target user
                        if (currentUserEmail.equals(targetEmail))
                        {
                            targetUserHelpRef.setValue(String.valueOf(true));

                            HashMap<String, String> helpNotificationMap = new HashMap<>();
                            helpNotificationMap.put("from", senderUserId);
                            helpNotificationMap.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                            helpNotificationMap.put("type", "help");
                            helpNotificationMap.put("taskTitle", taskTitle);
                            helpNotificationMap.put("requestedAs", AS_TARGET_USER);
                            helpNotificationMap.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));

                            helpRef.child(creatorUserId).push().setValue(helpNotificationMap);
                        }
                        // ask creator for help as target user
                        if (currentUserEmail.equals(collaboratorEmail))
                        {
                            collaboratorHelpRef.setValue(String.valueOf(true));

                            HashMap<String, String> helpNotificationMap2 = new HashMap<>();
                            helpNotificationMap2.put("from", senderUserId);
                            helpNotificationMap2.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                            helpNotificationMap2.put("type", "help");
                            helpNotificationMap2.put("taskTitle", taskTitle);
                            helpNotificationMap2.put("requestedAs", AS_COLLABORATOR);
                            helpNotificationMap2.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));

                            helpRef.child(creatorUserId).push().setValue(helpNotificationMap2);
                        }

                        // button animations and state update
                        outlinedAskForHelpButton.setEnabled(false);
                        outlinedAskForHelpButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                        //outlinedAskForHelpButton.setBackgroundColor(Color.DKGRAY);
                        outlinedAskForHelpButton.setText(R.string.help_requested);
                        outlinedAskForHelpButton.setCompoundDrawablesWithIntrinsicBounds(
                                                    R.drawable.ic_mood, 0, 0, 0);
                        helpConfirmationTextView.setVisibility(View.VISIBLE);

                    }
                    else  // current user is asking for help on own task (how silly!)
                    {
                        helpConfirmationTextView.setVisibility(View.VISIBLE);
                        helpConfirmationTextView.setText(R.string.dont_be_silly);
                        outlinedAskForHelpButton.setText(R.string.you_got_this);
                        outlinedAskForHelpButton.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_sentiment_very_satisfied, 0, 0, 0);
                    }
                }
            }
        });


        // set onclick listener for mark as done button
        markAsDoneButton = view.findViewById(R.id.markAsDoneButton);
        markAsDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try  // check if task is being done late or nah
                {
                    // final String arrays needed to get the job done
                    final String[] EXACT_DUE_TIME = new String[1];
                    final String[] EXACT_DUE_DATE = new String[1];
                    final String[] PROPER_END_DATE =new String[1];

                    // Firebase data members - we need to connect to the database
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser =
                            FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference tasksRef =
                            FirebaseDatabase.getInstance().getReference().child("tasks");
                    DatabaseReference exactDueDateRef =
                            tasksRef.child(taskId).child("exactDueDate");
                    DatabaseReference exactDueTimeRef =
                            tasksRef.child(taskId).child("exactDueTime");
                    DatabaseReference properEndDateRef =
                            FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("tasks")
                            .child(taskId)
                            .child("endDate");

                    // read regular end date
                    properEndDateRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String value = dataSnapshot.getValue(String.class);
                            final ArrayList<String> DUE_DATE = new ArrayList<>();
                            DUE_DATE.add(dataSnapshot.getValue(String.class));
                            DUE_DATE.add(value);
                            PROPER_END_DATE[0] = DUE_DATE.get(0);

                            try {
                                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("PROPER_END_DATE", value);
                                edit.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {   }
                    });

                    // read the exact date value from the database
                    exactDueDateRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String value = dataSnapshot.getValue(String.class);
                            final ArrayList<String> DUE_DATE = new ArrayList<>();
                            DUE_DATE.add(dataSnapshot.getValue(String.class));
                            DUE_DATE.add(value);
                            EXACT_DUE_DATE[0] = DUE_DATE.get(0);

                            try {
                                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("DB_DUE_DATE", value);
                                edit.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    // read the exact time value from the database
                    exactDueTimeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String value = dataSnapshot.getValue(String.class);
                            final ArrayList<String> DUE_TIME = new ArrayList<>();
                            DUE_TIME.add(dataSnapshot.getValue(String.class));
                            DUE_TIME.add(value);
                            EXACT_DUE_TIME[0] = DUE_TIME.get(0);

                            try {
                                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("DB_DUE_TIME", value);
                                edit.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    SharedPreferences spDateTime = getActivity().getPreferences(Context.MODE_PRIVATE);
                    String actualExactDate = spDateTime.getString("DB_DUE_DATE", null);
                    String actualExactTime = spDateTime.getString("DB_DUE_TIME", null);


                }
                catch (Exception e)
                { e.printStackTrace(); }

                try
                {
                    // Firebase data members - we need to connect to the database
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference().child("tasks");
                    DatabaseReference taskStatusPath = mainRef.child(taskId).child("status");

                    // update the status in the database
                    taskStatusPath.setValue(getString(R.string.completed));
                    Snackbar.make(getView(), R.string.task_completed, Snackbar.LENGTH_LONG).show();

                    // send a notification to the manager or staff that a task is completed
                    // generate reference to the notifications node
                    completionRef =
                            FirebaseDatabase.getInstance().getReference().child("completions");
                    // start by getting sender and receiver details
                    // re-referencing the plainEmail variable for convenience
                    String senderUserId =
                            currentUser.getEmail().replaceAll("[-+.@^:,]", "");
                    String senderExactName =
                            currentUser.getDisplayName();
                    String senderExactEmail =
                            currentUser.getEmail();
                    // string variables to shred notification receivers
                    String firstReceiverUserId;     // user the task was assigned to
                    String secondReceiverUserId;    // the task collaborator (if any)
                    String thirdReceiverUserId;     // the task creator

                    // retrieve actual expected date and time
                    SharedPreferences spDateTime = getActivity().getPreferences(Context.MODE_PRIVATE);
                    String actualExactDate = spDateTime.getString("DB_DUE_DATE", null);
                    String actualExactTime = spDateTime.getString("DB_DUE_TIME", null);
                    String properEndDate = spDateTime.getString("PROPER_END_DATE", null);

                    final String COMPLETED_AS_CREATOR = "CREATOR";
                    final String COMPLETED_AS_TARGET_USER = "TARGET_USER";
                    final String COMPLETED_AS_COLLABORATOR = "COLLABORATOR";

                    // check if both emails are valid then send notifications
                    try
                    {
                        if (targetEmail.contains("@"))
                        {
                            // format the emails into the appropriate user IDs
                            firstReceiverUserId =
                                targetEmail.replaceAll("[-+.@^:,]", "");
                            // create an HashMap object for the first receiver
                            HashMap<String, String> taskCompletionNotificationMap = new HashMap<>();
                            // declare the use case of the HashMap
                            taskCompletionNotificationMap.put("from", senderUserId);
                            taskCompletionNotificationMap.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                            taskCompletionNotificationMap.put("type", "newtaskcompleted");
                            taskCompletionNotificationMap.put("completedAs", COMPLETED_AS_TARGET_USER);
                            taskCompletionNotificationMap.put("taskTitle", taskTitle);
                            taskCompletionNotificationMap.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                            taskCompletionNotificationMap.put("expectedCompletion", properEndDate + " " + actualExactTime);
                            // structure the notification insertion with genuine push IDs
                            completionRef.child(firstReceiverUserId)
                                .push()
                                .setValue(taskCompletionNotificationMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), R.string.notify_task_complete,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }

                        if (collaboratorEmail.contains("@"))
                        {
                            secondReceiverUserId =
                                collaboratorEmail.replaceAll("[-+.@^:,]", "");
                            // repeat the same process for second receiver
                            HashMap<String, String> taskCompletionNotificationMap2 = new HashMap<>();
                            taskCompletionNotificationMap2.put("from", senderUserId);
                            taskCompletionNotificationMap2.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                            taskCompletionNotificationMap2.put("type", "newtaskcompleted");
                            taskCompletionNotificationMap2.put("completedAs", COMPLETED_AS_COLLABORATOR);
                            taskCompletionNotificationMap2.put("taskTitle", taskTitle);
                            taskCompletionNotificationMap2.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                            taskCompletionNotificationMap2.put("expectedCompletion", properEndDate + " " + actualExactTime);
                            completionRef.child(secondReceiverUserId)
                                .push()
                                .setValue(taskCompletionNotificationMap2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getContext(), R.string.notify_task_complete,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }

                        if (creatorEmail.contains("@"))
                        {
                            // if the creator is not the end user, send a notification too
                            if ( !(creatorEmail.equals(targetEmail)) )
                            {
                                thirdReceiverUserId =
                                    creatorEmail.replaceAll("[-+.@^:,]", "");
                                HashMap<String, String> taskCompletionNotificationMap3 = new HashMap<>();
                                taskCompletionNotificationMap3.put("from", senderUserId);
                                taskCompletionNotificationMap3.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                                taskCompletionNotificationMap3.put("type", "newtaskcompleted");
                                taskCompletionNotificationMap3.put("completedAs", COMPLETED_AS_CREATOR);
                                taskCompletionNotificationMap3.put("taskTitle", taskTitle);
                                taskCompletionNotificationMap3.put("dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                                taskCompletionNotificationMap3.put("expectedCompletion", properEndDate + " " + actualExactTime);
                                completionRef.child(thirdReceiverUserId)
                                    .push()
                                    .setValue(taskCompletionNotificationMap3)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getContext(), R.string.notify_task_complete,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            }

                        }

                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }


                    // disable the buttons
                    markAsDoneButton.setEnabled(false);
                    setAlarmsButton.setEnabled(false);
                    setRawAlarmButton.setEnabled(false);

                    // go back to list of all tasks
                    tasksFragment = new TasksFragment();
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer, tasksFragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    // transaction.hide(tasksFragment);
                    // transaction.addToBackStack(null);
                    transaction.commit();

                }
                catch (NullPointerException npe)
                {
                    npe.printStackTrace();
                }
            }
        });


        // fields needed to set the alarm properties
        textViewForEditTaskTitle = view.findViewById(R.id.textViewForEditTaskTitle);
        final String ALARM_TASK_TITLE = String.valueOf(textViewForEditTaskTitle.getText());

        //set alarm operation when SET PLAN TIME ALARM button is pressed.
        setAlarmsButton = view.findViewById(R.id.setAlarmsButton);
        setAlarmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // the Firebase entries that needs to be modified and the respective references
                FirebaseUser currentUser =
                        FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference tasksRef =
                        FirebaseDatabase.getInstance().getReference().child("tasks");
                DatabaseReference creatorAlarmSetRef =
                        tasksRef.child(taskId).child("isCreatorPlanAlarmSet");
                DatabaseReference collaboratorAlarmSetRef =
                        tasksRef.child(taskId).child("isCollaboratorPlanAlarmSet");
                DatabaseReference targetUserAlarmSetRef =
                        tasksRef.child(taskId).child("isTargetUserPlanAlarmSet");

                // update the alarm set details in the database.
                // if the user setting the alarm is the creator then we take note of this
                if (currentUser.getEmail().equals(creatorEmail))
                {
                    creatorAlarmSetRef.setValue(String.valueOf(true))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(),
                                            R.string.plan_alarm_set, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }

                // do the same thing if current user is a collaborator instead
                if (currentUser.getEmail().equals(collaboratorEmail))
                {
                    collaboratorAlarmSetRef.setValue(String.valueOf(true))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(),
                                            R.string.plan_alarm_set, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }

                // do the same thing if current user is the target user
                if (currentUser.getEmail().equals(targetEmail))
                {
                    targetUserAlarmSetRef.setValue(String.valueOf(true))
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(),
                                            R.string.plan_alarm_set, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }

                // disable the button
                setAlarmsButton.setEnabled(false);
                setAlarmsButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                setAlarmsButton.setBackgroundColor(Color.DKGRAY);
                setAlarmsButton.setText(R.string.alarms_already_set);

                // inner-class thread for starting first alarm
                Thread threadIntent2 = new Thread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        // start alarm for planning time
                        Intent intent2 = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent2.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(planHourAlarmTime));
                        intent2.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(planMinuteAlarmTime));
                        intent2.putExtra(AlarmClock.EXTRA_MESSAGE,
                                ALARM_TASK_TITLE + getString(R.string.plan_time_notice));
                        intent2.putExtra(AlarmClock.ALARM_SEARCH_MODE_LABEL,
                                ALARM_TASK_TITLE + getString(R.string.plan_time_notice));
                        startActivity(intent2);
                    }
                });

                // start alarm activity
                threadIntent2.run();

            }
        }); /**end {@link #onClick(View)} for setAlarmsButton (PLAN DURATION ALARM)*/


        // set alarm for actual task duration (END TIME ALARM)
        setRawAlarmButton = view.findViewById(R.id.setRawAlarmButton);
        setRawAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // the Firebase entries that needs to be modified and the respective references
                FirebaseUser currentUser =
                        FirebaseAuth.getInstance().getCurrentUser();
                String currentUserEmail =
                        currentUser.getEmail();
                DatabaseReference tasksRef =
                        FirebaseDatabase.getInstance().getReference().child("tasks");
                DatabaseReference creatorAlarmSetRef =
                        tasksRef.child(taskId).child("isCreatorRawAlarmSet");
                DatabaseReference collaboratorAlarmSetRef =
                        tasksRef.child(taskId).child("isCollaboratorRawAlarmSet");
                DatabaseReference targetUserAlarmSetRef =
                        tasksRef.child(taskId).child("isTargetUserRawAlarmSet");

                String senderExactName =
                        currentUser.getDisplayName();
                String senderExactEmail =
                        currentUser.getEmail();

                // notification entries to let the person know task has started
                DatabaseReference startedTasksRef =
                        FirebaseDatabase.getInstance().getReference().child("startedTasks");
                String senderUserId =
                        currentUserEmail.replaceAll("[-+.@^:,]", "");
                String creatorReceiverId =
                        creatorEmail.replaceAll("[-+.@^:,]", "");
                String targetReceiverId =
                        targetEmail.replaceAll("[-+.@^:,]", "");

                final String STARTED_AS_CREATOR = "CREATOR";
                final String STARTED_AS_TARGET_USER = "TARGET_USER";
                final String STARTED_AS_COLLABORATOR = "COLLABORATOR";

                try // send task start notifications to all associated users
                {
                    // create HashMap object to key in creator data to Firebase
                    HashMap<String, String> taskStartedNotificationMap = new HashMap<>();
                    // declare the use case of the HashMap
                    taskStartedNotificationMap.put("from", senderUserId);
                    taskStartedNotificationMap.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                    taskStartedNotificationMap.put("type", "newtaskstarted");
                    taskStartedNotificationMap.put("startedAs", STARTED_AS_CREATOR);
                    taskStartedNotificationMap.put("taskTitle", taskTitle);
                    taskStartedNotificationMap.put(
                            "dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                    // structure the notification insertion with genuine push IDs
                    startedTasksRef.child(creatorReceiverId)
                            .push()
                            .setValue(taskStartedNotificationMap);

                    // repeat the same process for the task creator
                    HashMap<String, String> taskStartedNotificationMap2 = new HashMap<>();
                    taskStartedNotificationMap2.put("from", senderUserId);
                    taskStartedNotificationMap2.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                    taskStartedNotificationMap2.put("type", "newtaskstarted");
                    taskStartedNotificationMap2.put("startedAs", STARTED_AS_TARGET_USER);
                    taskStartedNotificationMap2.put("taskTitle", taskTitle);
                    taskStartedNotificationMap2.put(
                            "dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                    startedTasksRef.child(targetReceiverId)
                            .push()
                            .setValue(taskStartedNotificationMap2);

                    // repeat the same process for task collaborator too
                    if (collaboratorEmail.contains("@"))
                    {
                        String collaboratorReceiverId =
                                collaboratorEmail.replaceAll("[-+.@^:,]", "");
                        HashMap<String, String> taskStartedNotificationMap3 = new HashMap<>();
                        taskStartedNotificationMap3.put("from", senderUserId);
                        taskStartedNotificationMap3.put("fromExactNameEmail", senderExactName + " (" + senderExactEmail + ")");
                        taskStartedNotificationMap3.put("type", "newtaskstarted");
                        taskStartedNotificationMap3.put("startedAs", STARTED_AS_COLLABORATOR);
                        taskStartedNotificationMap3.put("taskTitle", taskTitle);
                        taskStartedNotificationMap3.put(
                                "dateTime", String.valueOf(new Date(System.currentTimeMillis())));
                        startedTasksRef.child(collaboratorReceiverId)
                                .push()
                                .setValue(taskStartedNotificationMap3);
                    }

                }
                catch (NullPointerException npe)
                {
                    npe.printStackTrace();
                }


                // update the alarm set details in the database.
                // if the user setting the alarm is the creator then we take note of this
                if (currentUser.getEmail().equals(creatorEmail))
                {
                    creatorAlarmSetRef.setValue(String.valueOf(true))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),
                                                R.string.end_alarm_set, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // do the same thing if current user is a collaborator instead
                if (currentUser.getEmail().equals(collaboratorEmail))
                {
                    collaboratorAlarmSetRef.setValue(String.valueOf(true))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),
                                                R.string.end_alarm_set, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // do the same thing if current user is the target user
                if (currentUser.getEmail().equals(targetEmail))
                {
                    targetUserAlarmSetRef.setValue(String.valueOf(true))
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),
                                                R.string.end_alarm_set, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // disable the button
                setRawAlarmButton.setEnabled(false);
                setRawAlarmButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                setRawAlarmButton.setBackgroundColor(Color.DKGRAY);
                setRawAlarmButton.setText(R.string.alarms_already_set);

                // inner-class thread for starting second alarm
                Thread threadIntent1 = new Thread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        // start alarm for actual task duration time
                        Intent intent1 = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent1.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(rawHourAlarmTime));
                        intent1.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(rawMinuteAlarmTime));
                        intent1.putExtra(AlarmClock.EXTRA_MESSAGE,
                                ALARM_TASK_TITLE + getString(R.string.end_time_notice));
                        intent1.putExtra(AlarmClock.ALARM_SEARCH_MODE_LABEL,
                            ALARM_TASK_TITLE + getString(R.string.end_time_notice));
                        startActivity(intent1);
                    }
                });

                // start the alarm using the system app
                threadIntent1.run();
            }

        }); /**end {@link #onClick(View)} for setRawAlarmButton (END TIME ALARM)*/


        return view;

    } /** end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/






    // simple utility for hiding the page once an operation is done
    public void hideView(View v)
    {
        Button markAsDoneButton;
        Button setAlarmsButton;
        Button setRawAlarmButton;

        try {
            if (v != null)
            {
                markAsDoneButton = v.findViewById(R.id.markAsDoneButton);
                setAlarmsButton = v.findViewById(R.id.setAlarmsButton);
                setRawAlarmButton = v.findViewById(R.id.setRawAlarmButton);
                markAsDoneButton.setVisibility(View.GONE);
                setRawAlarmButton.setVisibility(View.GONE);
                setAlarmsButton.setVisibility(View.GONE);
            }
            else
            {
                markAsDoneButton = getView().findViewById(R.id.markAsDoneButton);
                setAlarmsButton = getView().findViewById(R.id.setAlarmsButton);
                setRawAlarmButton = getView().findViewById(R.id.setRawAlarmButton);
                markAsDoneButton.setVisibility(View.GONE);
                setRawAlarmButton.setVisibility(View.GONE);
                setAlarmsButton.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // simple utility for deleting last four strings (needed for formatting date time)
    public static String replaceLastFour(String s) {
        int length = s.length();
        //Check whether or not the string contains at least four characters; if not, this method is useless
        if (length < 4) return "Error: The provided string is not greater than four characters long.";
        return s.substring(0, length - 4) + "";
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
