package com.iumw.watm.workflows.assigned;

import android.content.Context;
import android.net.ConnectivityManager;
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
import com.iumw.watm.database.NotificationsStructure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AssignedTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AssignedTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignedTasksFragment extends Fragment {
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
    DatabaseReference notificationsRef = mainRef.child("notifications").child(currentUserId);

    // RecyclerView details
    private FirebaseRecyclerOptions<NotificationsStructure> assignedTasksOptions;
    private FirebaseRecyclerAdapter<NotificationsStructure, AssignedTasksViewHolder> assignedTasksAdapter;
    private RecyclerView assignedTasksRecyclerView;
    private LinearLayoutManager assignedTasksLayoutManager;

    public AssignedTasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignedTasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignedTasksFragment newInstance(String param1, String param2) {
        AssignedTasksFragment fragment = new AssignedTasksFragment();
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
                .setTitle(getString(R.string.assigned_tasks));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assigned_tasks, container, false);

        // start initializing RecyclerView features
        assignedTasksLayoutManager = new LinearLayoutManager(getActivity());
        assignedTasksLayoutManager.setReverseLayout(true);
        assignedTasksLayoutManager.setStackFromEnd(true);
        assignedTasksRecyclerView = view.findViewById(R.id.assignedTasksRecyclerView);
        assignedTasksRecyclerView.setHasFixedSize(true);
        assignedTasksRecyclerView.setLayoutManager(assignedTasksLayoutManager);
        // assignedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize RecyclerView's options
        assignedTasksOptions = new FirebaseRecyclerOptions
                .Builder<NotificationsStructure>()
                .setQuery(notificationsRef, NotificationsStructure.class)
                .build();

        // initialize Adapter
        assignedTasksAdapter =
            new FirebaseRecyclerAdapter<NotificationsStructure, AssignedTasksViewHolder>
                    (assignedTasksOptions) {
            @Override
            protected void onBindViewHolder(@NonNull AssignedTasksViewHolder holder,
                                            int position, @NonNull NotificationsStructure model)
            {
                // do some filtering
                final String TASK_TITLE = getResources().getString(R.string.task_title);
                final String DATE_ASSIGNED = getResources().getString(R.string.date_assigned);
                final String ASSIGNED_BY = getResources().getString(R.string.assigned_by);

                holder.textViewForNotificationsTaskTitle.setText(
                        String.format("%s: %s", TASK_TITLE, model.getTaskTitle()));
                holder.textViewForNotificationsDateTime.setText(
                        String.format("%s: %s", DATE_ASSIGNED, model.getDateTime()));
                holder.textViewForNotificationFrom.setText(
                        String.format("%s: %s", ASSIGNED_BY, model.getFromExactNameEmail()));
            }

            @NonNull
            @Override
            public AssignedTasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notifications_single_view_layout, parent, false);

                return new AssignedTasksViewHolder(view1);
            }
        };

        // start updating adapter
        assignedTasksAdapter.startListening();
        assignedTasksRecyclerView.setAdapter(assignedTasksAdapter);


        return view;

    } /**end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/






    // utility to check for NETWORK
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
