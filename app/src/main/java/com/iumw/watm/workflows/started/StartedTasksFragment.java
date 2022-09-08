package com.iumw.watm.workflows.started;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iumw.watm.R;
import com.iumw.watm.database.StartedTasksStructure;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartedTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartedTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartedTasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // misc data members
    FirebaseAuth auth;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser.getEmail().replaceAll("[-+.@^:,]", "");
    FirebaseDatabase database;
    DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference startedTasksRef = mainRef.child("startedTasks").child(currentUserId);

    // for filtering RecyclerView
    private static final String IS_STARTED_AS_CREATOR = "CREATOR";
    private static final String IS_STARTED_AS_COLLABORATOR = "COLLABORATOR";
    private static final String IS_STARTED_AS_TARGET_USER = "TARGET_USER";

    // Firebase RecyclerView options
    private FirebaseRecyclerOptions<StartedTasksStructure> startedTasksOptions;
    private FirebaseRecyclerAdapter<StartedTasksStructure, StartedTasksViewHolder> startedTasksAdapter;
    private RecyclerView startedTasksRecyclerView;
    private LinearLayoutManager startedTasksLayoutManager;

    public StartedTasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartedTasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartedTasksFragment newInstance(String param1, String param2) {
        StartedTasksFragment fragment = new StartedTasksFragment();
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
                .setTitle(getString(R.string.started_tasks));

        View view = inflater.inflate(R.layout.fragment_started_tasks, container, false);

        // initialize RecyclerView
        startedTasksLayoutManager = new LinearLayoutManager(getActivity());
        startedTasksLayoutManager.setReverseLayout(true);
        startedTasksLayoutManager.setStackFromEnd(true);
        startedTasksRecyclerView = view.findViewById(R.id.startedTasksRecyclerView);
        startedTasksRecyclerView.setHasFixedSize(true);
        startedTasksRecyclerView.setLayoutManager(startedTasksLayoutManager);
        //startedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize RecyclerView's adapter options
        startedTasksOptions = new FirebaseRecyclerOptions
                .Builder<StartedTasksStructure>()
                .setQuery(startedTasksRef, StartedTasksStructure.class)
                .build();

        // initialize RecyclerView's adapter
        startedTasksAdapter = new FirebaseRecyclerAdapter
            <StartedTasksStructure, StartedTasksViewHolder>(startedTasksOptions)
        {
            @Override
            protected void onBindViewHolder(@NonNull StartedTasksViewHolder holder,
                                            int position, @NonNull StartedTasksStructure model)
            {
                // do some filtering
                final String TASK_TITLE =  getResources().getString(R.string.task_title);
                final String DATE_STARTED = getResources().getString(R.string.date_started);
                final String STARTED_AS = getResources().getString(R.string.started_as);
                final String STARTED_BY = getResources().getString(R.string.started_by);
                String GET_STARTED_AS_VALUE;

                switch (model.getStartedAs().trim())
                {
                    case IS_STARTED_AS_CREATOR:
                        GET_STARTED_AS_VALUE = getResources().getString(R.string.task_creator);
                        break;
                    case IS_STARTED_AS_COLLABORATOR:
                        GET_STARTED_AS_VALUE= getResources().getString(R.string.task_collaborator);
                        break;
                    case IS_STARTED_AS_TARGET_USER:
                        GET_STARTED_AS_VALUE = getResources().getString(R.string.assigned_user);
                        break;
                    default:
                        GET_STARTED_AS_VALUE = model.getStartedAs();
                        break;
                }

                holder.textViewForStartedTaskTitle.setText(
                        String.format("%s: %s", TASK_TITLE, model.getTaskTitle()));
                holder.textViewForStartedTaskDateTime.setText(
                        String.format("%s: %s", DATE_STARTED, model.getDateTime()));
                holder.textViewForStartedTaskStartedAs.setText(
                        String.format("%s: %s", STARTED_AS, GET_STARTED_AS_VALUE));
                holder.textViewForStartedTaskStartedFrom.setText(
                        String.format("%s: %s", STARTED_BY, model.getFromExactNameEmail()));
            }

            @NonNull
            @Override
            public StartedTasksViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType)
            {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.started_tasks_single_view_layout, parent, false);

                return new StartedTasksViewHolder(view1);
            }
        };

        // start updating RecyclerView
        startedTasksAdapter.startListening();
        startedTasksRecyclerView.setAdapter(startedTasksAdapter);


        return view;
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
