package com.example.test_api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test_api.model.Sighting;

import java.io.Serializable;

public class DialogueFragment extends DialogFragment {
    private static final String ARG_SIGHTING = "sighting";
    private Sighting sighting;

    public DialogueFragment() {
        // Required empty public constructor
    }

    public static DialogueFragment newInstance(Sighting sighting) {
        DialogueFragment fragment = new DialogueFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SIGHTING, sighting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sighting = (Sighting)getArguments().getSerializable(ARG_SIGHTING);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialogue, null);

        TextView birdName = view.findViewById(R.id.label_dialogue_bird_name);
        TextView dateTime = view.findViewById(R.id.label_dialogue_date_time);
        TextView location = view.findViewById(R.id.label_dialogue_location);
        TextView notes = view.findViewById(R.id.label_dialogue_notes);

        birdName.setText(sighting.birdName);
        dateTime.setText(sighting.dateTime);
        location.setText(sighting.location);
        notes.setText(sighting.notes);

        builder.setView(view)
                .setPositiveButton("Изменить", (dialog, id) -> {
                    Intent intent = new Intent(requireActivity(), EditSightingActivity.class);
                    intent.putExtra("userId", sighting.owner);
                    intent.putExtra("isCreatingNewSighting", false);
                    // Send ID (for updating):
                    intent.putExtra("sightingId", sighting.id);
                    // Send all data:
                    intent.putExtra("birdName", sighting.birdName);
                    intent.putExtra("dateTime", sighting.dateTime);
                    intent.putExtra("location", sighting.location);
                    intent.putExtra("notes", sighting.notes);
                    startActivity(intent);
                })
                .setNegativeButton("Удалить", (dialog, id) -> {
                    // TODO: handle delete
                });

        return builder.create();
    }
}