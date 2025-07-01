package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test_api.model.LoginResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8"); // for POST requests
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_activity_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonLogin = findViewById(R.id.button_login);
        EditText editUsername = findViewById(R.id.edit_username);
        EditText editPassword = findViewById(R.id.edit_password);

        buttonLogin.setOnClickListener(v -> {
            String urlString = "http://10.0.2.2:3000/api/v1/users";
            String postString = "{"
                    + "\"username\":\"" + editUsername.getText() + "\","
                    + "\"password\":\"" + editPassword.getText() + "\""
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
                        final String responseBody = response.body().string();
                        LoginResponse responseParsed = gson.fromJson(responseBody, LoginResponse.class);

                        if (responseParsed.success) {
                            switchActivity(responseParsed.userId);
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, responseParsed.message, Toast.LENGTH_SHORT).show());
                        }

                    } else {
                        System.err.println("Request failed: " + response.code());
                    }
                }
            });
        });
    }

    void switchActivity(int userId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    };
}