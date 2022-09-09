package com.iumw.watm.workflows.completed;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iumw.watm.R;

class CompletionsViewHolder extends RecyclerView.ViewHolder
{

    TextView
            textViewForCompletionsTaskTitle,
            textViewForCompletionsDateTime,
            textViewForCompletionsCompletedAs,
            textViewForCompletionsFrom,
            textViewForCompletionsExpectedCompletion;

    public CompletionsViewHolder(@NonNull View itemView)
    {
        super(itemView);

        textViewForCompletionsTaskTitle =
                itemView.findViewById(R.id.textViewForCompletionsTaskTitle);
        textViewForCompletionsDateTime =
                itemView.findViewById(R.id.textViewForCompletionsDateTime);
        textViewForCompletionsCompletedAs =
                itemView.findViewById(R.id.textViewForCompletionsCompletedAs);
        textViewForCompletionsFrom =
                itemView.findViewById(R.id.textViewForCompletionsFrom);
        textViewForCompletionsExpectedCompletion =
                itemView.findViewById(R.id.textViewForCompletionsExpectedCompletion);

    }
}
