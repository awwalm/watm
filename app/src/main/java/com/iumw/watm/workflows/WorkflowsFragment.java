package com.iumw.watm.workflows;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.iumw.watm.HomeFragment;
import com.iumw.watm.LoginFragment;
import com.iumw.watm.R;
import com.iumw.watm.workflows.assigned.AssignedTasksFragment;
import com.iumw.watm.workflows.completed.CompletedTasksFragment;
import com.iumw.watm.workflows.help.HelpRequestsFragment;
import com.iumw.watm.workflows.started.StartedTasksFragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkflowsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkflowsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkflowsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // misc data members
    LinearLayout assignedWorkflowsLinLayout;
    LinearLayout startedWorkflowsLinLayout;
    LinearLayout completedWorkflowsLinLayout;
    LinearLayout helpRequestsWorkflowsLinLayout;
    AssignedTasksFragment assignedTasksFragment;
    StartedTasksFragment startedTasksFragment;
    CompletedTasksFragment completedTasksFragment;
    HelpRequestsFragment helpRequestsFragment;
    HomeFragment homeFragment;
    LoginFragment loginFragment;
    FragmentTransaction transaction;

    public WorkflowsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkflowsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkflowsFragment newInstance(String param1, String param2) {
        WorkflowsFragment fragment = new WorkflowsFragment();
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
        setHasOptionsMenu(true); // has menu options to display
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
        setHasOptionsMenu(true); // fragment has menu items to display

        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.workflows));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workflows, container, false);

        // configure clickable items functions
        assignedWorkflowsLinLayout = view.findViewById(R.id.assignedWorkflowsLinLayout);
        // go to history of assigned tasks
        assignedWorkflowsLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                assignedTasksFragment = new AssignedTasksFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, assignedTasksFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // go to startedWorkflows
        startedWorkflowsLinLayout = view.findViewById(R.id.startedWorkflowsLinLayout);
        startedWorkflowsLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startedTasksFragment = new StartedTasksFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, startedTasksFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // go completedWorkflows
        completedWorkflowsLinLayout = view.findViewById(R.id.completedWorkflowsLinLayout);
        completedWorkflowsLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                completedTasksFragment = new CompletedTasksFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, completedTasksFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // go to helpRequestsWorkflowsLinLayout
        helpRequestsWorkflowsLinLayout = view.findViewById(R.id.helpRequestsWorkflowsLinLayout);
        helpRequestsWorkflowsLinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                helpRequestsFragment = new HelpRequestsFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, helpRequestsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;

    } /**end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/





    // when menu is created
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater2)
    {
        menu.clear();
        inflater2.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater2);
    }


    // when menu options is selected
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
                                                startActivity(LoginFragment
                                                        .createIntent(getContext()));
                                                getActivity().finish();
                                            }
                                            // the previous step works whenever it's reattempted
                                            finally
                                            {
                                                startActivity(LoginFragment
                                                        .createIntent(getContext()));
                                                getActivity().finish();
                                            }

                                        }
                                        else
                                        {
                                            // Sign out failed
                                        }
                                    }
                                });

                            // confirm operation
                            Toast.makeText(
                                getContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
                        }
                        catch (IllegalStateException ise)
                        {
                            Snackbar.make(
                                    getView(), R.string.try_again, Snackbar.LENGTH_LONG).show();
                            ise.printStackTrace();
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

    } /**end {@link #onOptionsItemSelected(MenuItem)}*/




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
