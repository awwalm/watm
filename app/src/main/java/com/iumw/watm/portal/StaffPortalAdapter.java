package com.iumw.watm.portal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iumw.watm.MainActivity;
import com.iumw.watm.R;
import com.iumw.watm.SignedInActivity;
import com.iumw.watm.database.UserStructure;
import com.iumw.watm.tasks.AssignTaskFragment;

import java.security.AccessController;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class StaffPortalAdapter extends RecyclerView.Adapter<StaffPortalAdapter.StaffPortalAdapterViewHolder>
{

    public Context c;
    public ArrayList<UserStructure> arrayList;
    View viewMaster;
    StaffPortalFragment staffPortalFragment = new StaffPortalFragment();

    FirebaseAuth auth;
    TextView userEmail;
    TextView userName;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String plainEmail = currentUser.getEmail().replaceAll("[-+.@^:,]","");
    FirebaseDatabase database;
    DatabaseReference mainRef;
    DatabaseReference usersRef;





    public StaffPortalAdapter(Context c, ArrayList<UserStructure> arrayList)
    {
        this.arrayList = arrayList;
        this.c = c;
    }


    @NonNull
    @Override
    public StaffPortalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.staff_portal_single_view_layout, parent, false);

       return new StaffPortalAdapterViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull StaffPortalAdapterViewHolder holder, int position)
    {
        UserStructure model = arrayList.get(position);

        // get the holder to setup data getter via OnClickListener
        // this is achieved using classic Intent<-->Bundle style
        final String targetEmail = model.getEmail();
        final String targetName = model.getName();

        // set the details in the RecyclerView for each item
        holder.textViewForName.setText(model.getName().toString());
        holder.textViewForEmail.setText(model.getEmail().toString());
        holder.textViewForCompany.setText(model.getCompany().toString());
        holder.textViewForDepartment.setText(model.getDepartment().toString());

    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // inner class ViewHolder
    public class StaffPortalAdapterViewHolder extends RecyclerView.ViewHolder
    {
        // proprietary views
        TextView textViewForName;
        TextView textViewForEmail;
        TextView textViewForCompany;
        TextView textViewForDepartment;

        LinearLayout nameTextViewLinearLayout;
        LinearLayout emailTextViewLinearLayout;
        LinearLayout companyTextViewLinearLayout;
        LinearLayout departmentTextViewLinearLayout;

        public StaffPortalAdapterViewHolder(@NonNull View itemView)
        {
            super(itemView);

            textViewForName = itemView.findViewById(R.id.textViewForName);
            textViewForEmail = itemView.findViewById(R.id.textViewForEmail);
            textViewForCompany = itemView.findViewById(R.id.textViewForCompany);
            textViewForDepartment = itemView.findViewById(R.id.textViewForDepartment);

            // for filtering
            nameTextViewLinearLayout = itemView.findViewById(R.id.nameTextViewLinearLayout);
            emailTextViewLinearLayout = itemView.findViewById(R.id.emailTextViewLinearLayout);
            companyTextViewLinearLayout = itemView.findViewById(R.id.companyTextViewLinearLayout);
            departmentTextViewLinearLayout = itemView.findViewById(R.id.departmentTextViewLinearLayout);

        }

    } /**{@link }*/

}
