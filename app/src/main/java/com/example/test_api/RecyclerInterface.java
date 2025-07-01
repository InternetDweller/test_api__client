package com.example.test_api;

import com.example.test_api.model.Sighting;

import java.util.List;

public interface RecyclerInterface {
    void onItemClickListener(int position, List<Sighting> data);
}