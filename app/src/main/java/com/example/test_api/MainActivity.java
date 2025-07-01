package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_api.model.LoginResponse;
import com.example.test_api.model.Sighting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.RecyclerInterface, DialogueFragment.DialogueListener {
    final OkHttpClient client = new OkHttpClient();
    Gson gson = new Gson();

    //=========================================================

    final List<Sighting> sightings = new ArrayList<>();
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;

    //=========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Prevent overlapping
            mRecyclerView.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        // Get data sent from another activity
        final int userId = getIntent().getIntExtra("userId", 0);

        //=========================================================

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter(sightings, this);
        mRecyclerView.setAdapter(mAdapter);

        //=========================================================

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/api/v1/users/" + userId + "/sightings")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    // Iterative type to parse array from json
                    Type listType = new TypeToken<ArrayList<Sighting>>() {}.getType();
                    // Parse json string as array
                    List<Sighting> tmp = gson.fromJson(responseBody, listType);
                    sightings.clear();
                    sightings.addAll(tmp);
                    runOnUiThread(() -> mAdapter.notifyDataSetChanged());

                } else {
                    System.err.println("Request failed: " + response.code());
                }
            }
        });

        //=========================================================

        Button buttonAddSighting = findViewById(R.id.button_add_sighting);
        buttonAddSighting.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditSightingActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("isCreatingNewSighting", true);
            startActivity(intent);
        });
    }

    @Override // RecyclerView Interface implementation
    public void onItemClickListener(int position, List<Sighting> data) {
        Sighting clickedSighting = data.get(position);
        DialogueFragment dialogue = DialogueFragment.newInstance(clickedSighting);
        dialogue.show(getSupportFragmentManager(), "LeDialogue");
    }

    @Override // Dialogue Interface implementation
    public void onDeleteSighting(String id) {
        //sightings.removeIf(el -> el.id.equals(id)); // Does not return index
        int index = -1;
        for (int i = 0; i < sightings.size(); i++) {
            if (sightings.get(i).id.equals(id)) {
                index = i;
                sightings.remove(i);
                break;
            }
        }
        mAdapter.notifyItemRemoved(index);
    }
}