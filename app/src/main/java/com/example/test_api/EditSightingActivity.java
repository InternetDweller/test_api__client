package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test_api.model.LoginResponse;
import com.example.test_api.model.Sighting;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditSightingActivity extends AppCompatActivity {
    final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8"); // for POST requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_sighting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editBirdName = findViewById(R.id.edit_bird_name);
        EditText editDateTime = findViewById(R.id.edit_date_time);
        EditText editLocation = findViewById(R.id.edit_location);
        EditText editNotes = findViewById(R.id.edit_notes);

        Button buttonSave = findViewById(R.id.button_save);
        Button buttonCancel = findViewById(R.id.button_cancel);

        // Get data sent from another activity
        final int userId = getIntent().getIntExtra("userId", 0);
        final boolean isCreatingNewSighting = getIntent().getBooleanExtra("isCreatingNewSighting", false);
        if (isCreatingNewSighting) {
            TextView labelEditSighting = findViewById(R.id.label_edit_sighting);
            labelEditSighting.setText(R.string.caption_label_create_sighting);
            buttonSave.setText(R.string.caption_button_add);

            // Send a POST request (new sighting)
            buttonSave.setOnClickListener(v -> {
                String valueBirdName = editBirdName.getText().toString();
                String valueDateTime = editDateTime.getText().toString();
                String valueLocation = editLocation.getText().toString();
                String valueNotes = editNotes.getText().toString();

                if (!valueBirdName.isEmpty() && !valueDateTime.isEmpty() && !valueLocation.isEmpty()) {
                    String urlString = "http://10.0.2.2:3000/api/v1/users/" + userId + "/sightings";
                    String postString = "{"
                            + "\"owner\":\"" + userId + "\","
                            + "\"birdName\":\"" + valueBirdName + "\","
                            + "\"dateTime\":\"" + valueDateTime + "\","
                            + "\"location\":\"" + valueLocation + "\","
                            + "\"notes\":\"" + valueNotes + "\""
                            + "}";
                    RequestBody postBody = RequestBody.create(postString, JSON);
                    Request request = new Request.Builder()
                            .url(urlString)
                            .post(postBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                goToMainActivity(userId);

                            } else {
                                System.err.println("Request failed: " + response.code());
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // Get sighting data from extras
            final String sightingId = getIntent().getStringExtra("sightingId");
            final String birdName = getIntent().getStringExtra("birdName");
            final String dateTime = getIntent().getStringExtra("dateTime");
            final String location = getIntent().getStringExtra("location");
            final String notes = getIntent().getStringExtra("notes");

            // Initialise edit fields
            editBirdName.setText(birdName);
            editDateTime.setText(dateTime);
            editLocation.setText(location);
            editNotes.setText(notes);

            // Send a PUT request (updating existing sighting)
            buttonSave.setOnClickListener(v -> {
                String valueBirdName = editBirdName.getText().toString();
                String valueDateTime = editDateTime.getText().toString();
                String valueLocation = editLocation.getText().toString();
                String valueNotes = editNotes.getText().toString();

                if (!valueBirdName.isEmpty() && !valueDateTime.isEmpty() && !valueLocation.isEmpty()) {
                    String urlString = "http://10.0.2.2:3000/api/v1/users/" + userId + "/sightings/" + sightingId;
                    String postString = "{"
                            + "\"birdName\":\"" + valueBirdName + "\","
                            + "\"dateTime\":\"" + valueDateTime + "\","
                            + "\"location\":\"" + valueLocation + "\","
                            + "\"notes\":\"" + valueNotes + "\""
                            + "}";
                    RequestBody postBody = RequestBody.create(postString, JSON);
                    Request request = new Request.Builder()
                            .url(urlString)
                            .put(postBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                goToMainActivity(userId);

                            } else {
                                System.err.println("Request failed: " + response.code());
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // If user clicks Cancel, just return to main activity
        buttonCancel.setOnClickListener(v -> {
            goToMainActivity(userId);
        });
    }

    private void goToMainActivity(int userId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}