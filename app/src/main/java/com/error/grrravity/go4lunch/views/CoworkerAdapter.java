package com.error.grrravity.go4lunch.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.utils.helper.User;

import java.util.List;

public class CoworkerAdapter extends RecyclerView.Adapter<CoworkerViewHolder> {

    private List<User> mUser;
    private final int mOrigin;

    public CoworkerAdapter(List<User> user, int origin){
        this.mUser = user;
        this.mOrigin = origin;
    }

    @NonNull
    @Override
    public CoworkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_coworker_item, parent, false);
        return new CoworkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CoworkerViewHolder holder, int position) {
        holder.updateData(mUser.get(position), mOrigin);
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public void refreshAdapter(List<User> userList){
        mUser = userList;
        notifyDataSetChanged();
    }
}

