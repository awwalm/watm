package com.iumw.watm.workflows.started;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

class StartedTasksViewHolder extends RecyclerView.ViewHolder
{
    TextView
            textViewForStartedTaskTitle,
            textViewForStartedTaskDateTime,
            textViewForStartedTaskStartedAs,
            textViewForStartedTaskStartedFrom;

    public StartedTasksViewHolder(@NonNull View itemView)
    {
        super(itemView);

        textViewForStartedTaskTitle =
                itemView.findViewById(R.id.textViewForStartedTaskTitle);
        textViewForStartedTaskDateTime =
                itemView.findViewById(R.id.textViewForStartedTaskDateTime);
        textViewForStartedTaskStartedAs =
                itemView.findViewById(R.id.textViewForStartedTaskStartedAs);
        textViewForStartedTaskStartedFrom =
                itemView.findViewById(R.id.textViewForStartedTaskStartedFrom);

    }
}
