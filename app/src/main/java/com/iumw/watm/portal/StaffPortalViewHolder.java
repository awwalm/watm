package com.iumw.watm.portal;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

// ViewHolder class to sync the RecyclerView that displays the list of all registered staffs
class StaffPortalViewHolder extends RecyclerView.ViewHolder
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

    // view declaration for preparing target data fetching
    View view;

    public StaffPortalViewHolder(@NonNull View itemView)
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

        // view initialization
        view = itemView;

    }

}
