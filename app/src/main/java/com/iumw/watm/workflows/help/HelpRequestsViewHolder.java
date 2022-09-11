package com.iumw.watm.workflows.help;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

class HelpRequestsViewHolder extends RecyclerView.ViewHolder
{
    TextView
            textViewForHelpRequestsTaskTitle,
            textViewForHelpRequestsDateTime,
            textViewForHelpRequestedFrom,
            textViewForHelpRequestedAs;

    public HelpRequestsViewHolder(@NonNull View itemView)
    {
        super(itemView);

        textViewForHelpRequestsTaskTitle =
                itemView.findViewById(R.id.textViewForHelpRequestsTaskTitle);
        textViewForHelpRequestsDateTime =
                itemView.findViewById(R.id.textViewForHelpRequestsDateTime);
        textViewForHelpRequestedFrom =
                itemView.findViewById(R.id.textViewForHelpRequestedFrom);
        textViewForHelpRequestedAs =
                itemView.findViewById(R.id.textViewForHelpRequestedAs);

    }


}
