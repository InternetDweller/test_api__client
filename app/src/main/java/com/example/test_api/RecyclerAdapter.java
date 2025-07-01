package com.example.test_api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_api.model.Sighting;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static List<Sighting> mDataset;
    public interface RecyclerInterface {
        void onItemClickListener(int position, List<Sighting> data);
    }
    private final RecyclerInterface mRecyclerInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mBirdName;
        public TextView mDateTime;
        public TextView mLocation;

        public ViewHolder(View v, RecyclerInterface recyclerInterface) {
            super(v);
            mBirdName = v.findViewById(R.id.label_bird_name);
            mDateTime = v.findViewById(R.id.label_date_time);
            mLocation = v.findViewById(R.id.label_location);
            v.setOnClickListener(view -> {
                if (recyclerInterface != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerInterface.onItemClickListener(position, mDataset);
                    }
                }
            });
        }
    }

    public RecyclerAdapter(List<Sighting> dataset, RecyclerInterface recyclerInterface) {
        mDataset = dataset;
        mRecyclerInterface = recyclerInterface;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v, mRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBirdName.setText(mDataset.get(position).birdName);
        holder.mDateTime.setText(mDataset.get(position).dateTime);
        holder.mLocation.setText(mDataset.get(position).location);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}