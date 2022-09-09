package com.iumw.watm.workflows.completed;

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
import com.iumw.watm.database.CompletionsStructure;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompletedTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompletedTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedTasksFragment extends Fragment {
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
    DatabaseReference completionsRef = mainRef.child("completions").child(currentUserId);

    // Firebase RecyclerView options
    private FirebaseRecyclerOptions<CompletionsStructure> completionsOptions;
    private FirebaseRecyclerAdapter<CompletionsStructure, CompletionsViewHolder> completionsAdapter;
    //...
    private RecyclerView completedTasksRecyclerView;
    private LinearLayoutManager completedTasksLayoutManager;

    // constant string tags
    private final static String IS_COMPLETED_AS_CREATOR = "CREATOR";
    private final static String IS_COMPLETED_AS_TARGET_USER = "TARGET_USER";
    private final static String IS_COMPLETED_AS_COLLABORATOR = "COLLABORATOR";

    // Required empty public constructor
    public CompletedTasksFragment() {   }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedTasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedTasksFragment newInstance(String param1, String param2) {
        CompletedTasksFragment fragment = new CompletedTasksFragment();
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
                .setTitle(getString(R.string.completed_tasks));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        // initiate RecyclerView
        completedTasksLayoutManager = new LinearLayoutManager(getActivity());
        completedTasksLayoutManager.setReverseLayout(true);
        completedTasksLayoutManager.setStackFromEnd(true);
        completedTasksRecyclerView = view.findViewById(R.id.completedTasksRecyclerView);
        completedTasksRecyclerView.setHasFixedSize(true);
        completedTasksRecyclerView.setLayoutManager(completedTasksLayoutManager);
        // completedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize RecyclerView's adapter options
        completionsOptions = new FirebaseRecyclerOptions
                .Builder<CompletionsStructure>()
                .setQuery(completionsRef, CompletionsStructure.class)
                .build();

        // initialize RecyclerView's adapter
        completionsAdapter =
            new FirebaseRecyclerAdapter<CompletionsStructure, CompletionsViewHolder>(completionsOptions) {
            @Override
            protected void onBindViewHolder(
                @NonNull CompletionsViewHolder holder, int position, @NonNull CompletionsStructure model)
            {
                // filter and organize the details we need
                final String TASK_TITLE =  getResources().getString(R.string.task_title);
                final String COMPLETED_AS = getResources().getString(R.string.completed_as);
                final String COMPLETED_BY = getResources().getString(R.string.completed_by);
                final String DATE_COMPLETED = getResources().getString(R.string.date_completed);
                final String EXPECTED_COMPLETION = getResources().getString(R.string.expected_completion);
                String GET_COMPLETED_AS_VALUE;

                switch (model.getCompletedAs().trim())
                {
                    case IS_COMPLETED_AS_CREATOR:
                        GET_COMPLETED_AS_VALUE = getResources().getString(R.string.task_creator);
                        break;
                    case IS_COMPLETED_AS_COLLABORATOR:
                        GET_COMPLETED_AS_VALUE= getResources().getString(R.string.task_collaborator);
                        break;
                    case IS_COMPLETED_AS_TARGET_USER:
                        GET_COMPLETED_AS_VALUE = getResources().getString(R.string.assigned_user);
                        break;
                    default:
                        GET_COMPLETED_AS_VALUE = model.getCompletedAs();
                        break;
                }

                holder.textViewForCompletionsTaskTitle.setText(
                        String.format("%s: %s", TASK_TITLE, model.getTaskTitle()));
                holder.textViewForCompletionsCompletedAs.setText(
                        String.format("%s: %s", COMPLETED_AS, GET_COMPLETED_AS_VALUE));
                holder.textViewForCompletionsFrom.setText(
                        String.format("%s: %s", COMPLETED_BY, model.getFromExactNameEmail()));
                holder.textViewForCompletionsExpectedCompletion.setText(
                        String.format("%s: %s", EXPECTED_COMPLETION, model.getExpectedCompletion()));
                holder.textViewForCompletionsDateTime.setText(
                        String.format("%s: %s", DATE_COMPLETED, model.getDateTime().substring(3)));
            }

            @NonNull
            @Override
            public CompletionsViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType) {

                // inflate the RecyclerView
                View view1 = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.completions_single_view_layout, parent, false);

                return new CompletionsViewHolder(view1);
            }
        };

        // start updating the RecyclerViews
        completionsAdapter.startListening();
        completedTasksRecyclerView.setAdapter(completionsAdapter);


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
