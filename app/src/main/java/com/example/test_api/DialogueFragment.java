package com.example.test_api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.test_api.model.Sighting;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DialogueFragment extends DialogFragment {
    final OkHttpClient client = new OkHttpClient();
    private static final String ARG_SIGHTING = "sighting";
    private Sighting sighting;

    //=========================================================

    // To listen for element deletion in main activity
    public interface DialogueListener {
        void onDeleteSighting(String id);
    }
    private DialogueListener dialogueListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialogueListener = (DialogueListener)context;
    }

    //=========================================================

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
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:3000/api/v1/users/" + sighting.owner + "/sightings/" + sighting.id)
                            .delete()
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (dialogueListener != null) {
                                dialogueListener.onDeleteSighting(sighting.id);
                            }
                        }
                    });
                });

        return builder.create();
    }
}