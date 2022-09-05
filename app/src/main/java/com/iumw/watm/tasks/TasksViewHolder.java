package com.iumw.watm.tasks;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

// ViewHolder class to sync the RecyclerView that displays the list of all relevant tasks
class TasksViewHolder extends RecyclerView.ViewHolder
{
    TasksFragment tasksFragment = new TasksFragment();
    View view;

    // the fields needed to be filled
    TextView
        textViewForTaskTitle,
        textViewForPriority,
        textViewForCreatorName,
        textViewForEndTime,
        textViewForCreatorEmail,
        textViewForEndDate,
        textViewForCollaboratorEmail,
        textViewForStatus,
        textViewForTaskId,
        textViewForPlanningDuration,
        textViewForExtraDuration,
        textViewForRawDuration;

    LinearLayout
        taskTitleLinearLayout,
        priorityLinearLayout,
        creatorNameLinearLayout,
        creatorEmailLinearLayout,
        collaboratorEmailLinearLayout;

    public TasksViewHolder(@NonNull View itemView)
    {
        super(itemView);

        textViewForTaskTitle = itemView.findViewById(R.id.textViewForTaskTitle);
        textViewForPriority = itemView.findViewById(R.id.textViewForPriority);
        textViewForCreatorName = itemView.findViewById(R.id.textViewForCreatorName);
        textViewForEndTime = itemView.findViewById(R.id.textViewForEndTime);
        textViewForCreatorEmail = itemView.findViewById(R.id.textViewForCreatorEmail);
        textViewForEndDate = itemView.findViewById(R.id.textViewForEndDate);
        textViewForCollaboratorEmail = itemView.findViewById(R.id.textViewForCollaboratorEmail);
        textViewForStatus = itemView.findViewById(R.id.textViewForStatus);
        textViewForTaskId = itemView.findViewById(R.id.textViewForTaskId);
        textViewForPlanningDuration = itemView.findViewById(R.id.textViewForPlanningDuration);
        textViewForExtraDuration = itemView.findViewById(R.id.textViewForExtraDuration);
        textViewForRawDuration = itemView.findViewById(R.id.textViewForRawDuration);

        taskTitleLinearLayout = itemView.findViewById(R.id.taskTitleLinearLayout);
        priorityLinearLayout = itemView.findViewById(R.id.priorityLinearLayout);
        creatorNameLinearLayout = itemView.findViewById(R.id.creatorNameLinearLayout);
        creatorEmailLinearLayout = itemView.findViewById(R.id.creatorEmailLinearLayout);
        collaboratorEmailLinearLayout = itemView.findViewById(R.id.collaboratorEmailLinearLayout);

        view = itemView;

    }
}
