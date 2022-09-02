package com.iumw.watm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iumw.watm.portal.StaffPortalFragment;
import com.iumw.watm.profile.EditProfileFragment;
import com.iumw.watm.tasks.TasksFragment;
import com.iumw.watm.workflows.WorkflowsFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class HomeFragment extends Fragment
{
    // Fragments and Activities data members
    private LinearLayout staffPortalLinearLayout;
    private LinearLayout profileLinearLayout;
    private LinearLayout tasksLinearLayout;
    private LinearLayout workflowsLinearLayout;
    private MainActivity mainActivity;
    private SignedInActivity signedInActivity;
    private LoginFragment loginFragment;
    private HomeFragment homeFragment;
    private EditProfileFragment editProfileFragment;
    private StaffPortalFragment staffPortalFragment;
    private TasksFragment tasksFragment;
    private WorkflowsFragment workflowsFragment;
    private FragmentTransaction transaction;
    
    // extended floating action button
    ExtendedFloatingActionButton preferencesEFab;

    private final int CAMERA_REQUEST = 1; // for camera action
    View view;
    private ImageView profilePicture;

    // Firebase variables
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    // strip all special characters to create unique username string
    String plainEmail = currentUser.getEmail().replaceAll("[-+.@^:,]","");

    // used to force portrait mode //@TODO probably will be removed
    private boolean phoneDevice = true;
    // used to initialize menuItem //@TODO likely to be removed
    MenuItem fav;



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
                .setTitle(getString(R.string.home));

        // inflate GUI
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // sample SimpleDateFormat test @TODO: REMOVE IF UNNECESSARY
        Date sample = new Date(System.currentTimeMillis());
        Toast.makeText(getContext(), sample.toString(), Toast.LENGTH_SHORT).show();
        Log.d("CURRENT_TIME_CHECK", sample.toString());

        // initialize the settings EFAB @TODO: implement preferences
        preferencesEFab = view.findViewById(R.id.preferencesEFab);
        preferencesEFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "upcoming feature", Toast.LENGTH_SHORT).show();
            }
        });

        // shrink EFAB according to screen orientation
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // remove the text and reduce the size of the button
            // so it doesn't obstruct other layout objects
            preferencesEFab.setText(null);
            // reduce the width
            preferencesEFab.setWidth(50);
            // adjust padding
            preferencesEFab.setPadding(30, 15, 30, 15);
            // change the icon
            Drawable checkIcon =
                    getResources().getDrawable(R.drawable.ic_preferences);
            preferencesEFab.setIcon(checkIcon);
        }
        else
            {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {   }
            }

        profilePicture = profilePicture = view.findViewById(R.id.profilePicture);


        try // load the profile image
        {
            FirebaseAuth auth;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database;
            DatabaseReference mainRef;
            FirebaseStorage storage;
            StorageReference storageRef;

            profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
            // immediately attempt to load the profile picture on start
            downloadPicture();
            // set onclick listener upon tapping the photo
            profilePicture.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // inform the user the function might not work on old devices
                    Toast.makeText(getContext(),
                            R.string.old_device_warning,
                            Toast.LENGTH_LONG).show();
                    // take the picture with camera and upload
                    takePictureAndUpload();
                    // download the picture and display
                    downloadPicture();
                }
            });

        }
        catch (Exception e)
        {
            Log.d("PROFILE_PIC_LOAD", e.toString());
        } /*finish profile picture loading*/

        // staff portal action configuration
        staffPortalLinearLayout = view.findViewById(R.id.staffPortalLinearLayout);

        staffPortalLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                staffPortalFragment = new StaffPortalFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, staffPortalFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit(); // display HomeFragment

            }
        });

        // profile action configuration
        profileLinearLayout = view.findViewById(R.id.profileLinearLayout);
        // onClick listener for the said functionality
        profileLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editProfileFragment = new EditProfileFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, editProfileFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit(); // display EditProfileFragment

            }
        });

        //tasks action configuration
        tasksLinearLayout = view.findViewById(R.id.tasksLinearLayout);
        // take us to tasks
        tasksLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tasksFragment = new TasksFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, tasksFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit(); // display tasksFragment
            }
        });

        // workflows action configuration
        workflowsLinearLayout = view.findViewById(R.id.workflowsLinearLayout);
        // take us to workflows
        workflowsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                workflowsFragment = new WorkflowsFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, workflowsFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(null);
                transaction.commit(); // display workflowsFragment
            }
        });

        // set the user details on the dashboard
        try
        {
            FirebaseAuth auth;
            TextView userEmail;
            TextView userName;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // check to see if the user is valid, if not, then go back to where you came from
            if (currentUser == null)
            {
                Toast.makeText(getContext(),R.string.incomplete_signin,Toast.LENGTH_SHORT).show();
            }

            userEmail = (TextView) view.findViewById(R.id.emailTextView);
            userName = (TextView) view.findViewById(R.id.nameTextView);

            // reads from the database, but only some credentials
            userEmail.setText(currentUser.getEmail()); // uses predefined methods to get user email
            userName.setText(currentUser.getDisplayName()); // as above, this gets username

        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

        System.out.println("test uid: " + currentUser.getUid());
        System.out.println("test auth uid: " + FirebaseAuth.getInstance().getUid());

        return view;

    }/**end {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}*/


    // method for taking photo
    private void takePictureAndUpload()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // startActivityForResult(cameraIntent, Integer.parseInt(CAMERA_SERVICE));
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    // method for retrieving photo
    private void downloadPicture()
    {
        // initialize storage
        storage = FirebaseStorage.getInstance();
        // and get a reference to it
        storageRef = storage.getReference();
        // make the reference extend by or to a child node
        StorageReference imageRef = storageRef.child("dp")
                                     .child(plainEmail)
                                     .child("image.jpg");
        try
        {
            final File localFile = File.createTempFile("image", "jpg");
            // getFile is an asynchronous operation so it needs a callback monitor
            imageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                        {   // Local file has been created, create a bitmap image
                            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            // load the bitmap image onto the ImageView
                            try
                            {
                               // profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
                                profilePicture.setImageBitmap(myBitmap);
                            }
                            catch (NullPointerException npe)
                            {
                                //
                            }

                        }
                        // in case of failure, also add a callback monitor
                    }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    String foo = e.getLocalizedMessage();
                    e.printStackTrace();
                    Log.w("FETCH_IMAGE_fail", "TROUBLE FETCHING IMAGE", e);
                }
            });
        }
        catch (IOException ioe)
        {
            ioe.getLocalizedMessage();
            ioe.printStackTrace();
            Log.w("FETCH_IMAGE_io", "TROUBLE FETCHING IMAGE", ioe);
        }
        catch (Exception ex)
        {
            String foo = ex.getLocalizedMessage();
            ex.printStackTrace();
            Log.w("FETCH_IMAGE_ex", "TROUBLE FETCHING IMAGE" + foo, ex);
        }

    }/**end {@link #downloadPicture()} method*/


    /** method for getting photo operation done ... the {@param data} field contains the picture*/
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if ((requestCode == CAMERA_REQUEST) && (resultCode == Activity.RESULT_OK))
            {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageData = stream.toByteArray();

                storage = FirebaseStorage.getInstance();
                // this becomes the child node for the location of the image
                storageRef = storage.getReference();
                // send the image to the database by unprofessionally hard-coding the title
                StorageReference imageRef = storageRef.child("dp")
                                            .child(plainEmail)
                                            .child("image.jpg");

                // call a putBytes method that handles the file upload asynchronously
                // ...the upload is done byte by byte
                UploadTask uploadTask = imageRef.putBytes(imageData);
                // add onFailure listener to catch any errors if upload fails
                uploadTask.addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        String ex = e.getLocalizedMessage();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        Uri downloadUrl = urlTask.getResult();
                        Toast.makeText(
                               getContext(), R.string.dp_uploaded, Toast.LENGTH_LONG).show();
                        // Toast.makeText(
                        //      getContext(), downloadUrl.toString(), Toast.LENGTH_LONG).show();
                        // deprecated alternate procedure
                        /*StorageMetadata urlMetadata = taskSnapshot.getMetadata();
                        StorageReference urlReference = urlMetadata.getReference();
                        Task urlTask = urlReference.getDownloadUrl();
                        */
                    }
                });
            }

        } catch (Exception re)   // if RuntimeException occurs, device can't perform this
        {
            Log.wtf("CAMERA ISSUE ADVANCED", re);
            Log.d("CAMERA ISSUE ADVANCED", String.valueOf(re.getCause()));
            Log.v("CAMERA ISSUE ADVANCED", String.valueOf(re.getCause()));
            Log.getStackTraceString(re);
            Log.e("CAMERA ISSUE ADVANCED", re.getMessage(), re);
            re.printStackTrace();
            Toast.makeText(getActivity(), R.string.device_not_supported, Toast.LENGTH_SHORT).show();
        }
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
                                    {
                                        // Sign out failed
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

    // Called when the Fragment's View has been detached.
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        // Clean up resources related to the View.
    }

    // Called at the end of the full lifetime.
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Clean up any resources including ending threads,
        // closing database connections etc.
    }

    // Called when the Fragment has been detached from its parent Activity.
    @Override
    public void onDetach()
    {
        super.onDetach();
        // Clean up any references to the parent Activity
        // including references to its Views or classes.
    }


}
