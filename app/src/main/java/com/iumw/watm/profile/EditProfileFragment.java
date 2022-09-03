package com.iumw.watm.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iumw.watm.HomeFragment;
import com.iumw.watm.LoginFragment;
import com.iumw.watm.R;
import com.iumw.watm.portal.StaffPortalFragment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

// operations for editing the user profile are processed here
public class EditProfileFragment extends Fragment implements
        View.OnTouchListener, View.OnFocusChangeListener
{

    private LoginFragment loginFragment;
    private HomeFragment homeFragment;

    private EditText editNameEditText;
    private EditText editEmailEditText;
    private EditText editCompanyEditText;
    private EditText editDepartmentEditText;
    private EditText editExpertiseEditText;
    private EditText editInterestsEditText;

    // EFABS for performing updates
    private ExtendedFloatingActionButton updateProfileNowEFab;
    private ExtendedFloatingActionButton updateProfileLaterFab;

    private StaffPortalFragment staffPortalFragment;
    private FragmentTransaction transaction;


    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(
            LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true); // fragment has menu items to display

        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.edit_profile));

        // inflate GUI step 1
        transaction = getFragmentManager().beginTransaction();
        transaction.remove(new LoginFragment());

        // force portrait mode @TODO to be removed once tested for integrity
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // inflate GUI step 2
        //getFragmentManager().popBackStack();
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // try retrieving the user data firstly, then...
        // set the user details on the edit and update screen
        try
        {
            // create database variable within try-catch vault
            FirebaseAuth auth;
            TextView userEmail;
            TextView userName;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database;
            DatabaseReference mainRef;

            // check to see if the user is valid, if not, then go back to where you came from
            if (currentUser == null)
            {
                Toast incompleteSignInToast =
                    Toast.makeText(getContext(),R.string.incomplete_signin,Toast.LENGTH_SHORT);
                incompleteSignInToast.show();
            }

            // strip all special characters to create unique username string
            String plainEmail = currentUser.getEmail().replaceAll("[-+.@^:,]","");

            // locate the views needed for the job
            editNameEditText = view.findViewById(R.id.editNameEditText);
            editEmailEditText = view.findViewById(R.id.editEmailEditText);
            editCompanyEditText = view.findViewById(R.id.editCompanyEditText);
            editDepartmentEditText = view.findViewById(R.id.editDepartmentEditText);
            editExpertiseEditText = view.findViewById(R.id.editExpertiseEditText);
            editInterestsEditText = view.findViewById(R.id.editInterestsEditText);


            // shrink the buttons, remove the texts,
            // and swap their icons when these text fields are touched
            editCompanyEditText.setOnTouchListener(this::onTouch);
            editDepartmentEditText.setOnTouchListener(this::onTouch);
            editExpertiseEditText.setOnTouchListener(this::onTouch);
            editInterestsEditText.setOnTouchListener(this::onTouch);

            // read from the database, but only some credentials
            editNameEditText.setText(currentUser.getDisplayName()); // this gets username
            editEmailEditText.setText(currentUser.getEmail()); // this method gets the user email

            //@TODO this is just for testing, it will be properly completed later on
            /////   READ COMPANY VALUE  ////////////////////////////////////////////////////////
            database = FirebaseDatabase.getInstance();
            DatabaseReference companyPrecheckRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(plainEmail).child("company");
            companyPrecheckRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String value = dataSnapshot.getValue(String.class);
                    editCompanyEditText.setText(value);
                    try
                    {
                        Snackbar updateDoneNotification =
                                Snackbar.make(getView(),
                                        R.string.update_refreshed, Snackbar.LENGTH_LONG);
                        updateDoneNotification.show();
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }
                    catch (IllegalArgumentException npe)
                    {
                        npe.printStackTrace();
                        Log.w("UPDATE_C",
                                "COULD NOT PERFORM QUERY OR SNACKBAR ERROR", npe);
                    }
                    catch (NoSuchElementException nse)
                    {
                        nse.printStackTrace();
                        Log.w("UPDATE_C",
                                "COULD NOT PERFORM QUERY OR MISSING ELEMENT", nse);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Toast.makeText(getContext(), R.string.read_failed, Toast.LENGTH_SHORT).show();
                    Log.w("READ_ATTEMPT",
                            "COULD NOT COMPLETE READ", databaseError.toException());
                }
            });


            /////   READ DEPARTMENT VALUE  //////////////////////////////////////////////////////
            // doing the same for the DEPARTMENT is a bit tedious
            database = FirebaseDatabase.getInstance();
            DatabaseReference departmentPrecheckRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(plainEmail).child("department");
            departmentPrecheckRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String value = dataSnapshot.getValue(String.class);
                    editDepartmentEditText.setText(value);
                    try
                    {
                        Snackbar updateDoneNotification =
                                Snackbar.make(getView(),
                                        R.string.update_refreshed, Snackbar.LENGTH_LONG);
                        updateDoneNotification.show();
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }
                    catch (IllegalArgumentException npe)
                    {
                        npe.printStackTrace();
                        Log.w("UPDATE_D",
                                "COULD NOT PERFORM QUERY OR SNACKBAR ERROR", npe);
                    }
                    catch (NoSuchElementException nse)
                    {
                        nse.printStackTrace();
                        Log.w("UPDATE_D",
                                "COULD NOT PERFORM QUERY OR MISSING ELEMENT", nse);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Toast.makeText(getContext(), R.string.read_failed, Toast.LENGTH_SHORT).show();
                    Log.w("READ_ATTEMPT",
                            "COULD NOT COMPLETE READ",
                            databaseError.toException());
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////


            /////   READ EXPERTISE VALUE  //////////////////////////////////////////////////////
            // doing the same for the EXPERTISE is just as hard
            database = FirebaseDatabase.getInstance();
            DatabaseReference expertisePrecheckRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(plainEmail).child("expertise");
            expertisePrecheckRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String value = dataSnapshot.getValue(String.class);
                    editExpertiseEditText.setText(value);
                    try
                    {
                        Snackbar updateDoneNotification =
                                Snackbar.make(getView(),
                                        R.string.update_refreshed, Snackbar.LENGTH_LONG);
                        updateDoneNotification.show();
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }
                    catch (IllegalArgumentException npe)
                    {
                        npe.printStackTrace();
                        Log.w("UPDATE_E",
                                "COULD NOT PERFORM QUERY OR SNACKBAR ERROR", npe);
                    }
                    catch (NoSuchElementException nse)
                    {
                        nse.printStackTrace();
                        Log.w("UPDATE_E",
                                "COULD NOT PERFORM QUERY OR MISSING ELEMENT", nse);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Toast.makeText(getContext(), R.string.read_failed, Toast.LENGTH_SHORT).show();
                    Log.w("READ_ATTEMPT"
                            , "COULD NOT COMPLETE READ"
                            , databaseError.toException());
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////


            /////   READ INTERESTS VALUE  //////////////////////////////////////////////////////
            // doing the same for the INTERESTS is just as hard
            database = FirebaseDatabase.getInstance();
            DatabaseReference interestsPrecheckRef =
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(plainEmail).child("interests");
            interestsPrecheckRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String value = dataSnapshot.getValue(String.class);
                    editInterestsEditText.setText(value);
                    try
                    {   // present notification operation
                        Snackbar updateDoneNotification =
                                Snackbar.make(
                                    getView(), R.string.update_refreshed, Snackbar.LENGTH_LONG);
                        updateDoneNotification.show();
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }
                    catch (IllegalArgumentException npe)
                    {
                        npe.printStackTrace();
                        Log.w("UPDATE_I",
                                "COULD NOT PERFORM QUERY OR SNACKBAR ERROR", npe);
                    }
                    catch (NoSuchElementException nse)
                    {
                        nse.printStackTrace();
                        Log.w("UPDATE_I",
                                "COULD NOT PERFORM QUERY OR MISSING ELEMENT", nse);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Toast.makeText(getContext(), R.string.read_failed, Toast.LENGTH_SHORT).show();
                    Log.w("READ_ATTEMPT",
                            "COULD NOT COMPLETE READ",
                            databaseError.toException());
                }
            });
            ////////////////////////////////////////////////////////////////////////////////////



        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }


        // handle pressing update "later" floating action buttons

        updateProfileLaterFab = view.findViewById(R.id.updateProfileLaterFab);
        updateProfileLaterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                else {
                    homeFragment = new HomeFragment();
                    transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer, homeFragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack(null);
                    transaction.commit(); // display HomeFragment
                }

            }
        });


        // prepare updates by getting reference
        updateProfileNowEFab = view.findViewById(R.id.updateProfileNowEFab);
        updateProfileNowEFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    FirebaseAuth auth;
                    TextView userEmail;
                    TextView userName;
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase database;
                    DatabaseReference mainRef;

                    // check to see if the user is valid, if not return to previous destination
                    if (currentUser == null)
                    {
                        Toast.makeText(getContext()
                                , R.string.incomplete_signin
                                , Toast.LENGTH_SHORT).show();
                    }

                    // strip all special characters to create unique username string
                    String plainEmail =
                            currentUser.getEmail().replaceAll("[-+.@^:,]","");

                    // get references to our fields (Views)
                    editNameEditText = view.findViewById(R.id.editNameEditText);
                    editEmailEditText = view.findViewById(R.id.editEmailEditText);
                    editCompanyEditText = view.findViewById(R.id.editCompanyEditText);
                    editDepartmentEditText = view.findViewById(R.id.editDepartmentEditText);
                    editExpertiseEditText = view.findViewById(R.id.editExpertiseEditText);
                    editInterestsEditText = view.findViewById(R.id.editInterestsEditText);

                    // assign values from the references
                    // note that name and email fields are already set by default in onCreateView()
                    String name = editNameEditText.getText().toString().trim();
                    String email = editEmailEditText.getText().toString().trim();
                    String company = editCompanyEditText.getText().toString().trim();
                    String department = editDepartmentEditText.getText().toString().trim();
                    String expertise = editExpertiseEditText.getText().toString().trim();
                    String interests = editInterestsEditText.getText().toString().trim();

                    // now this works by upgrading gradle for Firebase version
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef =
                            FirebaseDatabase.getInstance().getReference().child("test");

                    // create the reference variables
                    DatabaseReference emailRef, nameRef, companyRef;
                    DatabaseReference departmentRef, expertiseRef, interestsRef;
                    // one more for the specially generated userID just in case
                    DatabaseReference userIDRef;
                    userIDRef =
                            FirebaseDatabase.getInstance().getReference()
                                .child("users").child(plainEmail).child("userID");

                    // prepare the insertion or insert queries
                    nameRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("name");
                    emailRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("email");
                    companyRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("company");
                    departmentRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("department");
                    expertiseRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("expertise");
                    interestsRef =
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(plainEmail).child("interests");


                    // check if there is internet access or not then disable the ability to update
                    // go into strict mode to check for internet access asynchronously
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        // check for internet access
                        if (isInternetAvailable() && isNetworkAvailable(getContext()))
                        {
                            // insert the values into the database using the queries above
                            nameRef.setValue(name);
                            emailRef.setValue(email);
                            companyRef.setValue(company);
                            departmentRef.setValue(department);
                            expertiseRef.setValue(expertise);
                            interestsRef.setValue(interests);
                            // finally, add the special userID for safe measure
                            userIDRef.setValue(plainEmail);
                        }
                        else
                        {
                            Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else    // SDK version is less than 8 so just do it anyway (old devices!)
                    {
                        // insert the values into the database using the queries above
                        nameRef.setValue(name);
                        emailRef.setValue(email);
                        companyRef.setValue(company);
                        departmentRef.setValue(department);
                        expertiseRef.setValue(expertise);
                        interestsRef.setValue(interests);
                        // finally, add the special userID for safe measure
                        userIDRef.setValue(plainEmail);
                    }




                    // currently not in use : @TODO carry the above sequence in here
                    if (email != null && name != null && company != null)
                    {
                        // send and update the database
                    }
                }
                catch (NullPointerException npe)
                {
                    npe.printStackTrace();
                    Log.w("UPDATE_OPERATION", "ISSUES ENCOUNTERED", npe);
                }

            }
        });




        return view;

    }/*end onCreate*/





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





    // general onTouch listener event for shrinking the buttons when typing
    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view instanceof EditText || view instanceof TextInputLayout)
        {
            view.setOnFocusChangeListener(this); // User touched EditText
            // remove the text and reduce the size of the button
            // so it doesn't obstruct typing too much
            updateProfileNowEFab.setText(null);
            // reduce the width
            updateProfileNowEFab.setWidth(50);
            // adjust padding
            updateProfileNowEFab.setPadding(30, 15, 30, 15);
            // change the icon
            Drawable checkIcon = getResources().getDrawable(R.drawable.ic_check);
            updateProfileNowEFab.setIcon(checkIcon);

            // repeat the same process above for the other button
            updateProfileLaterFab.setText(null);
            updateProfileLaterFab.setWidth(50);
            updateProfileLaterFab.setPadding(30, 15, 30, 15);
            Drawable closeIcon = getResources().getDrawable(R.drawable.ic_close);
            updateProfileLaterFab.setIcon(closeIcon);
        }

        return false;
    }


    // onFocusChange event
    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {

    }


    // Configuring the menu options
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
                                            startActivity(LoginFragment
                                                    .createIntent(getContext()));
                                            getActivity().finish();
                                        }
                                        // it'll work whenever it's reattempted
                                        finally
                                        {
                                            startActivity(LoginFragment
                                                    .createIntent(getContext()));
                                            getActivity().finish();
                                        }

                                    }
                                    else
                                    {   // Sign out failed
                                        /*Toast.makeText(
                                        getContext(), R.string.signout_failed,
                                        Toast.LENGTH_SHORT).show();*/ // doesn't work
                                    }
                                }
                            });

                            // confirm operation
                            Toast.makeText(getContext(), R.string.logout_success,
                                    Toast.LENGTH_SHORT).show();
                            /*Snackbar.make(getView(), R.string.logout_success,
                                    Snackbar.LENGTH_LONG).show();*/
                        }
                        catch (IllegalStateException ise)
                        {
                            Snackbar.make(
                                getView(), R.string.try_again, Snackbar.LENGTH_LONG).show();
                            //ise.getCause().printStackTrace();
                        }

                    }
                });

                // in case user changes mind
                builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {   // do nothing
                        return;
                    }
                });

                // finally show said dialog
                builder.show();
        }

        return true;
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

    // Called when app state changes are detected in order to enforce persistence


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}/*end class*/
