package com.iumw.watm.portal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iumw.watm.HomeFragment;
import com.iumw.watm.LoginFragment;
import com.iumw.watm.R;
import com.iumw.watm.database.UserStructure;
import com.iumw.watm.tasks.AssignTaskFragment;

import java.util.ArrayList;
import java.util.Objects;

public class StaffPortalFragment extends Fragment
{
    LoginFragment loginFragment;
    HomeFragment homeFragment;
    StaffPortalFragment staffPortalFragment;
    AssignTaskFragment assignTaskFragment;

    FirebaseAuth auth;
    TextView userEmail;
    TextView userName;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database;
    DatabaseReference mainRef;
    DatabaseReference usersRef;


    // for dynamically generated Firebase RecyclerView
    private FirebaseRecyclerOptions<UserStructure> staffPortalOptions;
    private FirebaseRecyclerAdapter<UserStructure, StaffPortalViewHolder> staffPortalAdapter;

    private RecyclerView staffPortalRecyclerView;// create the actual RecyclerView
    private ExtendedFloatingActionButton eFab; // manual task assignment ExtendedFAB

    private TextInputEditText spatialCrowdsourcingEditText;
    ArrayList<UserStructure> userArrayList;

    FragmentTransaction transaction;
    //private final FragmentActivity fragmentActivity = getActivity();

    private boolean phoneDevice = true; // used to force portrait mode

    MenuItem fav; // used to initialize menuItem






    // Configures this fragment's GUI
    @Override
    public View onCreateView
    (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // set the title of the Fragment
        Objects.requireNonNull(((AppCompatActivity) Objects
                .requireNonNull(getActivity()))
                .getSupportActionBar())
                .setTitle(getString(R.string.staff_portal));

        // inflate GUI
        View view =
            inflater.inflate(R.layout.fragment_staff_portal, container, false);

        // initialize userArrayList
        userArrayList = new ArrayList<>();

        // test the Extended FAB +++++++++++++++++++++++++++++++++++++++++++++++
        eFab = view.findViewById(R.id.eFab); // manual task assignment button
        eFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // if you're doing manual assignment, ensure the text fields are blank
                // or attempt to use SharedPreferences in case bundle fails
                // also, add try-catch vault in case the SharedPreference bundle is not found
                try
                {
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
                    else
                    {
                        edit.putString("TARGET_EMAIL", null);
                        edit.putString("TARGET_NAME", null);
                        edit.apply();
                    }
                }
                catch (NullPointerException npe)
                {
                    npe.printStackTrace();
                }

                // take us to our destination
                assignTaskFragment = new AssignTaskFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, assignTaskFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit(); // display AssignTaskFragment

            }
        });
        // unfortunately, NAV doesn't work, so proprietary Fragment Transaction is used
        // end test ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        /*
        // @TODO remove this for cleanup once development is done
        // ATTEMPT FOR LOOP TO GET THE KEYS ???????????????????????????????????????
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users");
        ArrayList<String> usernames = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String uid = ds.getKey();
                    Log.d("USERIDS AT THIS LEVEL: ", uid);
                    usernames.add(ds.getKey().toString);
                    System.out.println(uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {    }
        };
        userRef.addListenerForSingleValueEvent(valueEventListener);*/
        //???????????????????????????????????????????????????????????????????????????

        // get user's current company from Firebase
        // Firebase data members - we need to connect to the database
        // then store the value in shared preferences
        try
        {
            FirebaseUser currentUser =
                    FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database =
                    FirebaseDatabase.getInstance();
            DatabaseReference mainRef =
                    FirebaseDatabase.getInstance().getReference().child("users");
            String plainEmail =
                    currentUser.getEmail().replaceAll("[-+.@^:,]","");
            DatabaseReference userCompanyPath = mainRef.child(plainEmail).child("company");
            // perform database read to get current user's company
            userCompanyPath.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    String userCompany = dataSnapshot.getValue(String.class);
                    Log.d("FIRE_RETRIEVE","USER_COMPANY_FETCH:\t"+userCompany);

                    try
                    {
                        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        if (!(currentUser == null))
                        {
                            edit.putString("TEMP_COMPANY_FILTER", dataSnapshot.getValue(String.class));
                            edit.apply();
                        }
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    databaseError.getMessage();
                    Log.d("TAG", databaseError.getMessage());
                }
            }); /** {@linkplain #userCompanyPath}*/
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

        // retrieve current user's company from SharedPreferences
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        String userCompany = sp.getString("TEMP_COMPANY_FILTER", null);
        Log.d("SP_RETRIEVE","USER_COMPANY_FETCH:\t"+userCompany);

        //@TODO update the reference but we set this for now
        String userID =
                currentUser.getEmail().replaceAll("[-+.@^:,]","");
        // get database path reference
        mainRef = FirebaseDatabase.getInstance().getReference("users");


        // initialize value that we need to filter the search
        spatialCrowdsourcingEditText = view.findViewById(R.id.spatialCrowdsourcingEditText);
        // add OnTextChangeListener
        spatialCrowdsourcingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // check if the string being searched is empty
                if (!s.toString().isEmpty())
                {
                    // perform the search
                    search(s.toString());
                }
                else
                {
                    search("");
                }

            }
        });




        // initialize the RecyclerView
        staffPortalRecyclerView = view.findViewById(R.id.staffPortalRecyclerView);
        staffPortalRecyclerView.setHasFixedSize(true);
        //staffPortalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        staffPortalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // initialize the RecyclerView's adapter options
        staffPortalOptions = new FirebaseRecyclerOptions
                                    .Builder<UserStructure>()
                                    .setQuery(mainRef, UserStructure.class).build();

        // initialize the adapter
        staffPortalAdapter =
            new FirebaseRecyclerAdapter<UserStructure, StaffPortalViewHolder>(staffPortalOptions)
        {
            @Override
            protected void onBindViewHolder(
                @NonNull StaffPortalViewHolder holder, int position, @NonNull UserStructure model)
            {
                // get the holder to setup data getter via OnClickListener
                // this is achieved using classic Intent<-->Bundle style
                final String targetEmail = model.getEmail();
                final String targetName = model.getName();
                final String toShowCompany = model.getCompany();

                holder.view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // create a bundle instead
                        Bundle bundle = new Bundle();
                        // pass the needed values inside the bundle
                        bundle.putString("TARGET_EMAIL", targetEmail);
                        bundle.putString("TARGET_NAME", targetName);

                        // or attempt to use SharedPreferences in case bundle fails
                        // for safety, add NullPointerException in case of first time installation
                        // the SharedPreferences might be missing if installing for first time
                        try
                        {
                            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("TARGET_EMAIL", targetEmail);
                            edit.putString("TARGET_NAME", targetName);
                            edit.apply();
                        }
                        catch (NullPointerException npe)
                        {
                            Log.d("PREF_BUNDLE_ATTEMPT", "GET PREFERENCES FAILED", npe);
                        }

                        // does the user have the right to see other user's credentials?
                        try
                        {
                            if (userCompany.equals(toShowCompany))
                            {
                                // enable the inflation routine to start updating
                                staffPortalAdapter.startListening();
                                staffPortalRecyclerView.setAdapter(staffPortalAdapter);
                                // create the destination fragment instance
                                AssignTaskFragment assignTaskFragmentTarget
                                        = new AssignTaskFragment();
                                staffPortalFragment = new StaffPortalFragment();
                                // send the information in the bundle to fragment
                                assignTaskFragmentTarget.setArguments(bundle);
                                // now take us where we want (i.e. the destination fragment)
                                // using fragment style
                                assignTaskFragment = new AssignTaskFragment();
                                // initialize fragment transaction manager
                                transaction =
                                        getFragmentManager().beginTransaction();
                                // begin swapping process
                                transaction.replace
                                        (R.id.fragmentContainer, assignTaskFragment);
                                transaction.setTransition(FragmentTransaction
                                        .TRANSIT_FRAGMENT_FADE);
                                transaction.addToBackStack(null);
                                //transaction.remove(staffPortalFragment);
                                // finally take us back to the AssignTaskFragment page
                                transaction.commit();
                            }
                            else
                            {
                                Snackbar.make(
                                   getView(), R.string.no_user_access, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });

                // does the user have the right to see other user's credentials?
                try {
                    if (userCompany.equals(toShowCompany))
                    {
                        try {
                            // set the details in the RecyclerView for each item
                            holder.textViewForName.setText(model.getName().toString());
                            holder.textViewForEmail.setText(model.getEmail().toString());
                            holder.textViewForCompany.setText(model.getCompany().toString());
                            holder.textViewForDepartment.setText(model.getDepartment().toString());



                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            // set the corresponding values to null
                            holder.textViewForName.setText(null);
                            holder.textViewForEmail.setText(null);
                            holder.textViewForCompany.setText(null);
                            holder.textViewForDepartment.setText(null);

                            // hide the views entirely
                            holder.textViewForName.setVisibility(View.GONE);
                            holder.textViewForEmail.setVisibility(View.GONE);
                            holder.textViewForCompany.setVisibility(View.GONE);
                            holder.textViewForDepartment.setVisibility(View.GONE);

                            // diminish the layout height for the inner TextViews
                            holder.textViewForName.setHeight(0);
                            holder.textViewForEmail.setHeight(0);
                            holder.textViewForCompany.setHeight(0);
                            holder.textViewForDepartment.setHeight(0);

                            // and finally, hide the parent root LinearLayouts
                            holder.nameTextViewLinearLayout.setVisibility(View.GONE);
                            holder.emailTextViewLinearLayout.setVisibility(View.GONE);
                            holder.companyTextViewLinearLayout.setVisibility(View.GONE);
                            holder.departmentTextViewLinearLayout.setVisibility(View.GONE);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } /**end {@link #onBindViewHolder(StaffPortalViewHolder, int, UserStructure)}*/

            @NonNull
            @Override
            public StaffPortalViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType)
            {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(
                             R.layout.staff_portal_single_view_layout, parent, false);
                return new StaffPortalViewHolder(view1);
            }
        };

        // enable the inflation routine to start updating
        staffPortalAdapter.startListening();
        staffPortalRecyclerView.setAdapter(staffPortalAdapter);



        /** add {@link RecyclerView#addOnScrollListener(RecyclerView.OnScrollListener)}
         * to cater for EFAB visibility while scrolling*/
        staffPortalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                { eFab.show(); }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && eFab.isShown())
                { eFab.hide(); }
            }
        });

        return view;

    }

    /**end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/





    // search RecyclerView method
    private void search(String s)
    {
        Query query = mainRef.orderByChild("department")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChildren())
                {
                    // ensure it is empty for now
                    userArrayList.clear();
                    // parse in the user details
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        final UserStructure user = snapshot.getValue(UserStructure.class);
                        // now add it to the user array list
                        userArrayList.add(user);
                    }

                    StaffPortalAdapter staffPortalAdapterForSearch =
                            new StaffPortalAdapter(getActivity(), userArrayList);
                    staffPortalRecyclerView.setAdapter(staffPortalAdapterForSearch);

                    staffPortalAdapterForSearch.notifyDataSetChanged();

                }

                if (s.isEmpty())
                {
                    staffPortalAdapter.startListening();
                    staffPortalRecyclerView.setAdapter(staffPortalAdapter);
                }
                else
                {
                    // let the user know they can't click a user in Spatial Crowdsourcing mode
                    Snackbar notifySearchMode = Snackbar.make(
                            getView(), R.string.notify_search_mode, Snackbar.LENGTH_LONG);
                    notifySearchMode.setAction(
                            getResources().getString(R.string.got_it), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    notifySearchMode.dismiss();
                                }
                            });
                    notifySearchMode.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    notifySearchMode.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // enable the inflation routine to start updating
                staffPortalAdapter.startListening();
                staffPortalRecyclerView.setAdapter(staffPortalAdapter);
            }
        });
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
                                        // the previous step works whenever it's reattempted
                                        finally
                                        {
                                            startActivity(LoginFragment
                                                    .createIntent(getContext()));
                                            getActivity().finish();
                                        }

                                    }
                                    else
                                    {   // Sign out failed

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
                            Snackbar.make(getView(), R.string.try_again,
                                    Snackbar.LENGTH_LONG).show();
                            //ise.getCause().printStackTrace();
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
    }


    // Called when the Fragment's View has been detached.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources related to the View.
        // enable the inflation routine to start updating
        staffPortalAdapter.startListening();
        staffPortalRecyclerView.setAdapter(staffPortalAdapter);
    }
    // Called at the end of the full lifetime.
    @Override
    public void onDestroy() {
        super.onDestroy();

        // enable the inflation routine to start updating
        staffPortalAdapter.startListening();
        staffPortalRecyclerView.setAdapter(staffPortalAdapter);
        // Clean up any resources including ending threads,
        // closing database connections etc.
    }
    // Called when the Fragment has been detached from its parent Activity.
    @Override
    public void onDetach() {
        super.onDetach();
        // Clean up any references to the parent Activity
        // including references to its Views or classes. Typically setting
        // enable the inflation routine to start updating
        staffPortalAdapter.startListening();
        staffPortalRecyclerView.setAdapter(staffPortalAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // enable the inflation routine to start updating
        staffPortalAdapter.startListening();
        staffPortalRecyclerView.setAdapter(staffPortalAdapter);
    }
}
