package com.iumw.watm.workflows.help;

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
import com.iumw.watm.database.HelpRequestsStructure;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpRequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpRequestsFragment extends Fragment {
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
    DatabaseReference helpRequestsRef = mainRef.child("help").child(currentUserId);

    // RecyclerView's details
    private FirebaseRecyclerOptions<HelpRequestsStructure> helpRequestsOptions;
    private FirebaseRecyclerAdapter<HelpRequestsStructure, HelpRequestsViewHolder> helpRequestsAdapter;
    private RecyclerView helpRequestsRecyclerView;
    private LinearLayoutManager helpRequestsLayoutManager;


    public HelpRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HelpRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpRequestsFragment newInstance(String param1, String param2) {
        HelpRequestsFragment fragment = new HelpRequestsFragment();
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
                .setTitle(getString(R.string.help_requests));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_requests, container, false);

        // initialize RecyclerView's features
        helpRequestsLayoutManager = new LinearLayoutManager(getActivity());
        helpRequestsLayoutManager.setReverseLayout(true);
        helpRequestsLayoutManager.setStackFromEnd(true);
        helpRequestsRecyclerView = view.findViewById(R.id.helpRequestsRecyclerView);
        helpRequestsRecyclerView.setHasFixedSize(true);
        helpRequestsRecyclerView.setLayoutManager(helpRequestsLayoutManager);
        // helpRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize RecyclerView's options
        helpRequestsOptions = new FirebaseRecyclerOptions
                .Builder<HelpRequestsStructure>()
                .setQuery(helpRequestsRef, HelpRequestsStructure.class)
                .build();

        // initialize Adapter
        helpRequestsAdapter =
            new FirebaseRecyclerAdapter<HelpRequestsStructure, HelpRequestsViewHolder>
            (helpRequestsOptions) {
            @Override
            protected void onBindViewHolder(
                @NonNull HelpRequestsViewHolder holder, int position, @NonNull HelpRequestsStructure model)
            {
                final String TASK_TITLE = getResources().getString(R.string.task_title);
                final String DATE_REQUESTED = getResources().getString(R.string.date_requested);
                final String REQUESTED_BY = getResources().getString(R.string.requested_by);
                final String REQUESTED_AS = getResources().getString(R.string.requested_as);

                holder.textViewForHelpRequestsTaskTitle.setText(
                        String.format("%s: %s", TASK_TITLE, model.getTaskTitle()));
                holder.textViewForHelpRequestedAs.setText(
                        String.format("%s: %s", REQUESTED_AS, model.getRequestedAs()));
                holder.textViewForHelpRequestedFrom.setText(
                        String.format("%s: %s", REQUESTED_BY, model.getFromExactNameEmail()));
                holder.textViewForHelpRequestsDateTime.setText(
                        String.format("%s: %s", DATE_REQUESTED, model.getDateTime()));
            }

            @NonNull
            @Override
            public HelpRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view1 = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.help_requests_single_view_layout, parent, false);

                return new HelpRequestsViewHolder(view1);
            }
        };

        // start updating adapter
        helpRequestsAdapter.startListening();
        helpRequestsRecyclerView.setAdapter(helpRequestsAdapter);


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
