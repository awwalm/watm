package com.iumw.watm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.iumw.watm.profile.EditProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment
{

    private static final int RC_SIGN_IN = 100; // sign-in confirmation code
    private FirebaseAuth auth;
    Button loginButton;
    private Button goHomeButton;
    private HomeFragment homeFragment; // displays HomeFragment
    private EditProfileFragment editProfileFragment; // displays EditProfileFragment

    // Configures this fragment's GUI
    @Override
    public View onCreateView
    (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // inflate GUI
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // on the pre-logout page, set action for main menu button
        goHomeButton = view.findViewById(R.id.goHomeButton);

        // inflation finished////////////////////////////////////////////////////////////////////

        /* Before using Firebase AuthUI, you need to initialize Firebase Auth and check to see
         * if there's a user already signed-in.
         */
        if (auth.getInstance().getCurrentUser() != null)
        {   // start signed in activity
            //startActivity(SignedInActivity.createIntent(getContext(), null));
            //getActivity().finish();

            // create HomeFragment
            homeFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, homeFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit(); // display HomeFragment

        }
        else  // no user is signed in, create the Auth UI
        {
            loginButton = view.findViewById(R.id.loginButton);
            // onClick listener for the sign in button
            loginButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
                    selectedProviders.add(new AuthUI.IdpConfig.GoogleBuilder().build());
                    selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder().build());
                    //selectedProviders.add(new AuthUI.IdpConfig.FacebookBuilder().build());

                    startActivityForResult (
                            //AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    //.setTheme(R.style.CustomTheme)
                                    //.setLogo(R.drawable.firelogo)
                                    .setAvailableProviders(selectedProviders)
                                    .setIsSmartLockEnabled(true)
                                    .build(), RC_SIGN_IN );
                }
            }); /*end inner class listener*/
        }


        return view;

    } /*end onCreateView*/


    // function to figure out what happens when anonymous signin activity is created
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            handleSignInResponse(resultCode, data);
            return;
        }
    }



    /*if the signin was successful, call the signed in activity, otherwise, describe failure*/
    @MainThread
    private void handleSignInResponse(int resultCode, Intent data)
    {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Toast toast;
        // Successfully signed in
        if (resultCode == Activity.RESULT_OK)
        {
            try {
                // hide the overlaying layouts
                getView().findViewById(R.id.loginButton).setVisibility(View.GONE);
                getView().findViewById(R.id.titleIcon).setVisibility(View.GONE);
                getView().findViewById(R.id.titleText).setVisibility(View.GONE);
                getView().findViewById(R.id.shortDescription).setVisibility(View.GONE);

                // optional: show a snackbar that suggests the user to update their profile
                Snackbar welcomeProfile =
                    Snackbar.make(getView(), R.string.welcome_profile, Snackbar.LENGTH_INDEFINITE);
                welcomeProfile.setAction(getResources().getString(R.string.got_it),
                        new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Objects.requireNonNull(((AppCompatActivity) Objects
                                .requireNonNull(getActivity())).getSupportActionBar()).show();

                    }
                });
                welcomeProfile.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                welcomeProfile.show();


                // now we attempt to get the user's device token once login is successful
                FirebaseAuth auth;
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database;
                DatabaseReference usersRef;
                DatabaseReference notificationRef;

                // get current user's userID
                String currentUserId =
                        currentUser.getEmail().replaceAll("[-+.@^:,]", "");
                // get device token
                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                // initiate the user path reference
                usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                usersRef.child(currentUserId)
                        .child("deviceToken")
                        .setValue(deviceToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    // do nothing
                                }
                            }
                        });

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(BuildConfig.SECAUTH)
                        .child(currentUser.getUid())
                        .setValue(String.valueOf(true));

                System.out.println("test uid: " + currentUser.getUid());

            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }


            Objects.requireNonNull(((AppCompatActivity) Objects
                    .requireNonNull(getActivity())).getSupportActionBar()).show();

            // create HomeFragment
            homeFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.loginContent, homeFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit(); // display HomeFragment
            //startActivity(SignedInActivity.createIntent(getContext(), response));
            //getActivity().finish();
            return;

            /*
            // create HomeFragment
            homeFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, homeFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //transaction.addToBackStack(null);
            transaction.commit(); // display HomeFragment
            */

            //return;
        }
        else
        {   // sign in failed
            if (response == null)
            {   // User pressed back button
                toast = Toast.makeText
                        (getContext(), R.string.signin_cancelled, Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
            {
                toast = Toast.makeText
                        (getContext(), R.string.no_internet, Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
            {
                toast = Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }

        toast = Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_LONG);
        toast.show();

    } /*end sign-in handler method*/




    // used by sign-in activity to generate its own dynamic activity:
    //      if a user sign-in is invalid, close and restart MainActivity
    public static Intent createIntent(Context context)
    {
        Intent in = new Intent();
        in.setClass(context, MainActivity.class);
        return in;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            View view = new View(getContext()) ;
            goHomeButton =  view.findViewById(R.id.goHomeButton);
            goHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Button works", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (NullPointerException npe)
        {
            //Toast.makeText(getContext(), "Doesn't work", Toast.LENGTH_LONG).show();
        }

    }

    // hide the toolbar from login page to make it look cleaner and smooth
    // it even adds a nice transition
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity())).getSupportActionBar()).hide();
    }

    // immediately display and release it back once done
    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity())).getSupportActionBar()).show();
    }


    // Called when the Fragment's View has been detached.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources related to the View.
    }
    // Called at the end of the full lifetime.
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up any resources including ending threads,
        // closing database connections etc.
    }
    // Called when the Fragment has been detached from its parent Activity.
    @Override
    public void onDetach() {
        super.onDetach();
        // Clean up any references to the parent Activity
        // including references to its Views or classes. Typically setting
    }


}
