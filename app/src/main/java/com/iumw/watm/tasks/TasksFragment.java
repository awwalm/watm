package com.iumw.watm.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iumw.watm.R;
import com.iumw.watm.portal.StaffPortalFragment;
import com.iumw.watm.database.TaskStructure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Firebase data members
    FirebaseAuth auth;
    TextView userEmail;
    TextView userName;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database;
    DatabaseReference mainRef;

    // for dynamically generated Firebase RecyclerView
    private FirebaseRecyclerOptions<TaskStructure> tasksOptions;
    private FirebaseRecyclerAdapter<TaskStructure, TasksViewHolder> tasksAdapter;
    // RecyclerView for setting the details and displaying the tasks
    private RecyclerView tasksRecyclerView;
    private LinearLayoutManager tasksLayoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // misc
    private ExtendedFloatingActionButton newTaskEFab;
    private AssignTaskFragment assignTaskFragment;
    private StaffPortalFragment staffPortalFragment;
    private EditTaskFragment editTaskFragment;
    private FragmentTransaction transaction;

    private OnFragmentInteractionListener mListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);

        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.tasks_lc));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // generate Firebase reference
        mainRef = FirebaseDatabase.getInstance().getReference().child("tasks");

        // inflate the RecyclerView
        tasksLayoutManager = new LinearLayoutManager(getActivity());
        tasksLayoutManager.setReverseLayout(true);
        tasksLayoutManager.setStackFromEnd(true);
        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayoutManager(tasksLayoutManager);
        //tasksRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());

        //tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // initialize the RecyclerView options
        tasksOptions = new FirebaseRecyclerOptions
                            .Builder<TaskStructure>()
                            .setQuery(mainRef, TaskStructure.class)
                            .build();

        // initialize the adapter and inflate the RecyclerView
        tasksAdapter = new FirebaseRecyclerAdapter<TaskStructure, TasksViewHolder>(tasksOptions)
        {
            @Override
            protected void onBindViewHolder(
                    @NonNull TasksViewHolder holder, int position, @NonNull TaskStructure model)
            {
                // data we need
                final String HOT_TASK_ID = model.getTaskId();
                final String HOT_TASK_DESCRIPTION = model.getTaskDescription();
                final String HOT_TASK_TITLE = model.getTaskTitle();
                final String HOT_START_DATE = model.getStartDate();
                final String HOT_END_DATE = model.getEndDate();
                final String HOT_START_TIME = model.getStartTime();
                final String HOT_DUE_TIME = model.getEndTime();
                final String HOT_STATUS = model.getStatus();
                final String HOT_CREATOR_EMAIL = model.getCreatorEmail();
                final String HOT_CREATOR_NAME = model.getCreatorName();
                final String HOT_COLLABORATOR_EMAIL = model.getCollaboratorEmail();
                final String HOT_PRIORITY = model.getPriority();
                final String HOT_TARGET_EMAIL = model.getTargetUserEmail();
                final String[] HOT_PLANNING_DURATION = new String[1];
                final String HOT_EXTRA_DURATION = model.getExtraDuration();
                final String HOT_RAW_DURATION = model.getRawDuration();

                final String[] HOT_PLAN_HOUR_ALARM_TIME = new String[1];
                final String[] HOT_PLAN_MINUTE_ALARM_TIME = new String[1];
                final String[] HOT_EXTRA_HOUR_ALARM_TIME = new String[1];
                final String[] HOT_EXTRA_MINUTE_ALARM_TIME = new String[1];
                final String[] HOT_RAW_HOUR_ALARM_TIME = new String[1];
                final String[] HOT_RAW_MINUTE_ALARM_TIME = new String[1];

                final String[] HOT_IS_CREATOR_PLAN_ALARM_SET = new String[1];
                final String[] HOT_IS_CREATOR_RAW_ALARM_SET = new String[1];

                final String[] HOT_IS_COLLABORATOR_PLAN_ALARM_SET = new String[1];
                final String[] HOT_IS_COLLABORATOR_RAW_ALARM_SET = new String[1];

                final String[] HOT_IS_TARGET_USER_PLAN_ALARM_SET = new String[1];
                final String[] HOT_IS_TARGET_USER_RAW_ALARM_SET = new String[1];

                final String[] HOT_IS_TARGET_USER_ASKING_HELP = new String[1];
                final String[] HOT_IS_COLLABORATOR_ASKING_HELP = new String[1];

                // retrieve values of who's asking for help
                DatabaseReference targetUserHelpRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isTargetUserAskingForHelp");
                targetUserHelpRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_TARGET_USER_ASKING_HELP[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                DatabaseReference collaboratorHelpRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isCollaboratorAskingForHelp");
                collaboratorHelpRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_COLLABORATOR_ASKING_HELP[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });



                // get time values to set our alarms /////////////////////////////////
                // read PLAN_HOUR_ALARM_TIME
                DatabaseReference planHourRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("planHourAlarmTime");
                planHourRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_PLAN_HOUR_ALARM_TIME[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read PLAN_MINUTE_ALARM_TIME
                DatabaseReference planMinuteRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("planMinuteAlarmTime");
                planMinuteRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_PLAN_MINUTE_ALARM_TIME[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read RAW_HOUR_ALARM_TIME
                DatabaseReference rawHourRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("rawHourAlarmTime");
                rawHourRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_RAW_HOUR_ALARM_TIME[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read RAW_MINUTE_ALARM_TIME
                DatabaseReference rawMinuteRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("rawMinuteAlarmTime");
                rawMinuteRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_RAW_MINUTE_ALARM_TIME[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });



                // read details of those who've set alarms
                // read IS_CREATOR_PLAN_ALARM_SET status /////////////////////////////////////////
                DatabaseReference creatorPlanAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isCreatorPlanAlarmSet");
                creatorPlanAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_CREATOR_PLAN_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read IS_CREATOR_RAW_ALARM_SET status ----------------------------------------
                DatabaseReference creatorRawAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isCreatorRawAlarmSet");
                creatorRawAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_CREATOR_RAW_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                //////////////////////////////////////////////////////////////////////////////////




                // read IS_COLLABORATOR_PLAN_ALARM_SET status ////////////////////////////////////
                DatabaseReference collaboratorPlanAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isCollaboratorPlanAlarmSet");
                collaboratorPlanAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_COLLABORATOR_PLAN_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read IS_COLLABORATOR_RAW_ALARM_SET status -------------------------------------
                DatabaseReference collaboratorRawAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isCollaboratorRawAlarmSet");
                collaboratorRawAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_COLLABORATOR_RAW_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                ////////////////////////////////////////////////////////////////////////////////




                // read IS_TARGET_USER_PLAN_ALARM_SET status ///////////////////////////////////
                DatabaseReference targetUserPlanAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isTargetUserPlanAlarmSet");
                targetUserPlanAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_TARGET_USER_PLAN_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                // read IS_TARGET_USER_RAW_ALARM_SET status --------------------------------
                DatabaseReference targetUserRawAlarmRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("isTargetUserRawAlarmSet");
                targetUserRawAlarmRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_IS_TARGET_USER_RAW_ALARM_SET[0] = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                //////////////////////////////////////////////////////////////////////////////////





                // get planning duration from database because it's not working via offline
                DatabaseReference planDurationRef = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("tasks")
                        .child(HOT_TASK_ID)
                        .child("planDuration");
                planDurationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        HOT_PLANNING_DURATION[0] = value;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });





                // has the user created the task or is he/she a collaborator or has been assigned?
                // if not, deny access to the content and filter it from ever being displayed
                try {
                    if ( (currentUser.getEmail().equals(HOT_CREATOR_EMAIL))
                            || (currentUser.getEmail().equals(HOT_COLLABORATOR_EMAIL))
                            || (currentUser.getEmail().equals(HOT_TARGET_EMAIL)) )
                    {
                        try
                        {
                            // set the corresponding values
                            holder.textViewForTaskTitle.setText(model.getTaskTitle().toString().toUpperCase());
                            holder.textViewForPriority.setText(model.getPriority().toString());
                            holder.textViewForCreatorName.setText(model.getCreatorName().toString());
                            holder.textViewForCreatorEmail.setText(model.getCreatorEmail().toString());
                            holder.textViewForCollaboratorEmail.setText(model.getCollaboratorEmail().toString());
                            holder.textViewForEndTime.setText(model.getEndTime().toString());
                            holder.textViewForEndDate.setText(model.getEndDate().toString());
                            holder.textViewForStatus.setText(model.getStatus());
                            holder.textViewForTaskId.setText(model.getTaskId().toString());
                            holder.textViewForPlanningDuration.setText(model.getPlanningDuration().toString());
                            holder.textViewForExtraDuration.setText(model.getExtraDuration().toString());
                            holder.textViewForRawDuration.setText(model.getRawDuration().toString());
                        }
                        catch (NullPointerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else    // the tasks not associated with the current user is not displayed
                    {
                        try {
                            // set the corresponding values to null
                            holder.textViewForTaskTitle.setText(null);
                            holder.textViewForPriority.setText(null);
                            holder.textViewForCreatorName.setText(null);
                            holder.textViewForCreatorEmail.setText(null);
                            holder.textViewForCollaboratorEmail.setText(null);
                            holder.textViewForEndTime.setText(null);
                            holder.textViewForEndDate.setText(null);
                            holder.textViewForStatus.setText(null);
                            holder.textViewForTaskId.setText(null);
                            holder.textViewForPlanningDuration.setText(null);
                            holder.textViewForExtraDuration.setText(null);
                            holder.textViewForRawDuration.setText(null);

                            // hide the views entirely
                            holder.textViewForTaskTitle.setVisibility(View.GONE);
                            holder.textViewForPriority.setVisibility(View.GONE);
                            holder.textViewForCreatorName.setVisibility(View.GONE);
                            holder.textViewForCreatorEmail.setVisibility(View.GONE);
                            holder.textViewForCollaboratorEmail.setVisibility(View.GONE);
                            holder.textViewForEndTime.setVisibility(View.GONE);
                            holder.textViewForEndDate.setVisibility(View.GONE);
                            holder.textViewForStatus.setVisibility(View.GONE);
                            holder.textViewForTaskId.setVisibility(View.GONE);
                            holder.textViewForPlanningDuration.setVisibility(View.GONE);
                            holder.textViewForExtraDuration.setVisibility(View.GONE);
                            holder.textViewForRawDuration.setVisibility(View.GONE);

                            // diminish the layout height for the inner TextViews
                            holder.textViewForTaskTitle.setHeight(0);
                            holder.textViewForPriority.setHeight(0);
                            holder.textViewForCreatorName.setHeight(0);
                            holder.textViewForCreatorEmail.setHeight(0);
                            holder.textViewForCollaboratorEmail.setHeight(0);
                            holder.textViewForEndTime.setHeight(0);
                            holder.textViewForEndDate.setHeight(0);
                            holder.textViewForStatus.setHeight(0);
                            holder.textViewForTaskId.setHeight(0);
                            holder.textViewForPlanningDuration.setHeight(0);
                            holder.textViewForExtraDuration.setHeight(0);
                            holder.textViewForRawDuration.setHeight(0);

                            // and finally, hide the parent root LinearLayouts
                            holder.taskTitleLinearLayout.setVisibility(View.GONE);
                            holder.priorityLinearLayout.setVisibility(View.GONE);
                            holder.creatorNameLinearLayout.setVisibility(View.GONE);
                            holder.creatorEmailLinearLayout.setVisibility(View.GONE);
                            holder.collaboratorEmailLinearLayout.setVisibility(View.GONE);
                        }
                        catch (NullPointerException e)
                        {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // set on click listener for each item and store values in SharedPreferences bundle
                // this is because they need to be accessed on the resulting interface or page
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("HOT_TASK_ID", HOT_TASK_ID);
                            edit.putString("HOT_TASK_DESCRIPTION", HOT_TASK_DESCRIPTION);
                            edit.putString("HOT_TASK_TITLE", HOT_TASK_TITLE);
                            edit.putString("HOT_START_DATE", HOT_START_DATE);
                            edit.putString("HOT_DUE_TIME", HOT_DUE_TIME);
                            edit.putString("HOT_START_TIME", HOT_START_TIME);
                            edit.putString("HOT_STATUS", HOT_STATUS);
                            edit.putString("HOT_CREATOR_EMAIL", HOT_CREATOR_EMAIL);
                            edit.putString("HOT_CREATOR_NAME", HOT_CREATOR_NAME);
                            edit.putString("HOT_COLLABORATOR_EMAIL", HOT_COLLABORATOR_EMAIL);
                            edit.putString("HOT_PRIORITY", HOT_PRIORITY);
                            edit.putString("HOT_TARGET_EMAIL", HOT_TARGET_EMAIL);
                            edit.putString("HOT_PLANNING_DURATION", HOT_PLANNING_DURATION[0]);
                            edit.putString("HOT_EXTRA_DURATION", HOT_EXTRA_DURATION);
                            edit.putString("HOT_RAW_DURATION", HOT_RAW_DURATION);

                            edit.putString("HOT_PLAN_HOUR_ALARM_TIME", HOT_PLAN_HOUR_ALARM_TIME[0]);
                            edit.putString("HOT_PLAN_MINUTE_ALARM_TIME", HOT_PLAN_MINUTE_ALARM_TIME[0]);
                            edit.putString("HOT_RAW_HOUR_ALARM_TIME", HOT_RAW_HOUR_ALARM_TIME[0]);
                            edit.putString("HOT_RAW_MINUTE_ALARM_TIME", HOT_RAW_MINUTE_ALARM_TIME[0]);

                            edit.putString("HOT_IS_CREATOR_PLAN_ALARM_SET", HOT_IS_CREATOR_PLAN_ALARM_SET[0]);
                            edit.putString("HOT_IS_CREATOR_RAW_ALARM_SET", HOT_IS_CREATOR_RAW_ALARM_SET[0]);

                            edit.putString("HOT_IS_COLLABORATOR_PLAN_ALARM_SET", HOT_IS_COLLABORATOR_PLAN_ALARM_SET[0]);
                            edit.putString("HOT_IS_COLLABORATOR_RAW_ALARM_SET", HOT_IS_COLLABORATOR_RAW_ALARM_SET[0]);

                            edit.putString("HOT_IS_TARGET_USER_PLAN_ALARM_SET", HOT_IS_TARGET_USER_PLAN_ALARM_SET[0]);
                            edit.putString("HOT_IS_TARGET_USER_RAW_ALARM_SET", HOT_IS_TARGET_USER_RAW_ALARM_SET[0]);

                            edit.putString("HOT_IS_TARGET_USER_ASKING_HELP", HOT_IS_TARGET_USER_ASKING_HELP[0]);
                            edit.putString("HOT_IS_COLLABORATOR_ASKING_HELP", HOT_IS_COLLABORATOR_ASKING_HELP[0]);

                            edit.apply();
                        }
                        catch (NullPointerException npe)
                        {
                            Log.d("PREF_BUNDLE_ATTEMPT", "GET PREFERENCES FAILED", npe);
                        }
                        catch (IllegalArgumentException ile)
                        {
                            ile.printStackTrace();
                        }

                        // has the user created the task or is he/she a collaborator?
                        // if not, deny access to the content
                        try
                        {
                            if ( (currentUser.getEmail().equals(HOT_CREATOR_EMAIL))
                                    || (currentUser.getEmail().equals(HOT_COLLABORATOR_EMAIL))
                                    || (currentUser.getEmail().equals(HOT_TARGET_EMAIL)) )
                            {
                                editTaskFragment = new EditTaskFragment();
                                transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentContainer, editTaskFragment);
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                            else
                            {
                                Snackbar.make(
                                    getView(), R.string.no_permission, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                }); /**end {@linkplain holder.view.setOnClickListener()}*/

            } /**end {@link #onBindViewHolder(TasksViewHolder, int, TaskStructure)}*/

            @NonNull
            @Override
            public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.tasks_single_view_layout, parent, false);

                return new TasksViewHolder(v);
            }
        };

        // finally, start listening for changes
        tasksAdapter.startListening();
        tasksRecyclerView.setAdapter(tasksAdapter);


        tasksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /** add {@link RecyclerView#addOnScrollListener(RecyclerView.OnScrollListener)}
             * to cater for EFAB visibility while scrolling*/
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                { newTaskEFab.show(); }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                // super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && newTaskEFab.isShown())
                { newTaskEFab.hide(); }
            }
        });


        /** attempt to get specific value at specific area in the recycler view and style it
        /*by adding a {@link RecyclerView#getViewTreeObserver()} and attaching it to a
         * {@link ViewTreeObserver#addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener)}
         * @TODO : this technique works courtesy of a StackOverFlow user - it's reusable.
         */
        final String ONGOING = view.getContext().getString(R.string.ongoing).toUpperCase();
        final String COMPLETED = view.getContext().getString(R.string.completed).toUpperCase();
        tasksRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                final int itemCount = tasksAdapter.getItemCount();

                for (int i = 0; i < itemCount; i++)
                {
                    try
                    {
                        TextView statusTextView =
                            tasksRecyclerView.getChildAt(i).findViewById(R.id.textViewForStatus);
                        TextView priorityTextView =
                            tasksRecyclerView.getChildAt(i).findViewById(R.id.textViewForPriority);
                        TextView idTextView =
                            tasksRecyclerView.getChildAt(i).findViewById(R.id.textViewForTaskId);
                        TextView endDateTextView =
                            tasksRecyclerView.getChildAt(i).findViewById(R.id.textViewForEndDate);
                        TextView endTimeTextView =
                            tasksRecyclerView.getChildAt(i).findViewById(R.id.textViewForEndTime);

                        String priorityText = priorityTextView.getText().toString();
                        String statusText = statusTextView.getText().toString();
                        String id = idTextView.getText().toString();

                        if (priorityText.equals("1"))
                        {
                            priorityTextView.setText(getString(R.string.high_priority).toUpperCase());
                        }
                        if (priorityText.equals("2"))
                        {
                            priorityTextView.setText(getString(R.string.medium_priority).toUpperCase());
                        }
                        if (priorityText.equals("3"))
                        {
                            priorityTextView.setText(getString(R.string.low_priority).toUpperCase());
                        }

                        if (statusText.equals(ONGOING))
                        {
                            statusTextView.setTextColor(Color.RED);
                        }
                        if (statusText.equals(COMPLETED))
                        {
                            statusTextView.setTextColor(Color.DKGRAY);
                            priorityTextView.setTextColor(Color.DKGRAY);
                            endDateTextView.setTextColor(Color.DKGRAY);
                            endTimeTextView.setTextColor(Color.DKGRAY);
                        }

                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }

                }

                tasksRecyclerView
                    .getViewTreeObserver().removeGlobalOnLayoutListener(this::onGlobalLayout);
            }
        });

        // navigate to new task creation
        //@TODO: just delete all these duplicated code  and replace with the dedicated function
        newTaskEFab = view.findViewById(R.id.newTaskEFab);
        newTaskEFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                GoToNewTask();
            }
        });

        return view;

    } /** end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/





    // go to a new task fragment
    public void GoToNewTask()
    {
        try {
            // immediately send the current user's credentials into the bundle
            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            if (!(currentUser == null))
            {
                String tempUserEmail = currentUser.getEmail();
                String tempUserName = currentUser.getDisplayName();
                edit.putString("TARGET_EMAIL", tempUserEmail);
                edit.putString("TARGET_NAME", tempUserName);
                edit.apply();
            }

            assignTaskFragment = new AssignTaskFragment();
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, assignTaskFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit(); // display AssignTaskFragment

            Snackbar newTaskWarning =
                Snackbar.make(
                    Objects.requireNonNull(
                            getView()), R.string.new_task_warning, Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(
                            R.string.go_there), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                staffPortalFragment = new StaffPortalFragment();
                                transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentContainer, staffPortalFragment);
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                Snackbar.make(
                                    getView(), R.string.link_expired, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

            newTaskWarning.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
            newTaskWarning.show();
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
            npe.getMessage();
        }

    } /**end {@link #GoToNewTask}*/


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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
