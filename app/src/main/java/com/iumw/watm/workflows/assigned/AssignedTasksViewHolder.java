package com.iumw.watm.workflows.assigned;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

class AssignedTasksViewHolder extends RecyclerView.ViewHolder
{

    TextView
            textViewForNotificationsTaskTitle,
            textViewForNotificationsDateTime,
            textViewForNotificationFrom;


    public AssignedTasksViewHolder(@NonNull View itemView)
    {
        super(itemView);

        textViewForNotificationsTaskTitle =
                itemView.findViewById(R.id.textViewForNotificationsTaskTitle);
        textViewForNotificationsDateTime =
                itemView.findViewById(R.id.textViewForNotificationsDateTime);
        textViewForNotificationFrom =
                itemView.findViewById(R.id.textViewForNotificationFrom);
    }

}
