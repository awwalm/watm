package com.iumw.watm;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.iumw.watm.tasks.AssignTaskFragment;
import com.iumw.watm.tasks.EditTaskFragment;
import com.iumw.watm.tasks.TasksFragment;
import com.iumw.watm.workflows.assigned.AssignedTasksFragment;
import com.iumw.watm.workflows.completed.CompletedTasksFragment;
import com.iumw.watm.workflows.help.HelpRequestsFragment;
import com.iumw.watm.workflows.started.StartedTasksFragment;
import com.iumw.watm.workflows.WorkflowsFragment;


// remote class meant for handling the starting screen presentation
// this eventually takes you to the autogenerated Firebase AuthUI login
// but if you're already signed in, you are remotely sent to a different home screen
public class MainActivity extends AppCompatActivity implements
        AssignTaskFragment.OnFragmentInteractionListener,
        TasksFragment.OnFragmentInteractionListener,
        EditTaskFragment.OnFragmentInteractionListener,
        WorkflowsFragment.OnFragmentInteractionListener,
        AssignedTasksFragment.OnFragmentInteractionListener,
        StartedTasksFragment.OnFragmentInteractionListener,
        CompletedTasksFragment.OnFragmentInteractionListener,
        HelpRequestsFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // essentially an empty placeholder layout container
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create a fragment for the login page
        // displays LoginFragment
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // check orientation configuration to avoid loss of activity or fragment data
        // if this doesn't work, change it explicitly in the manifest, else
        /* refer to <a href="https://www.youtube.com/watch?v=Njj9qb5v2aw">this link</a> */
        if (savedInstanceState == null)
        {
            // swap and exchange fragments only when there is currently no data
            transaction.add(R.id.fragmentContainer, loginFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit(); // display LoginFragment
        }

    } /*end onCreate*/

    // OnFragmentInteractionListener() interface method that allows transition between
    // fragments using the Android Component Navigation Architecture
    @Override
    public void onFragmentInteraction(Uri uri) {    }

} /*end class*/
