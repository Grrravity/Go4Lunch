package com.error.grrravity.go4lunch.views;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.utils.helper.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoworkerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.coworker_name)
    TextView mCoworkerName;
    @BindView(R.id.coworker_picture)
    ImageView mCoworkerPict;

    CoworkerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("NewApi")
    void updateData(User user) {
        if (user.getJoinedRestaurant() != null) {
            mCoworkerName.setText(itemView.getContext().getString(R.string.will_eat, user.getUsername(), user.getJoinedRestaurant()));
            mCoworkerName.setTypeface(mCoworkerName.getTypeface(), Typeface.NORMAL);
            mCoworkerName.setTextColor(itemView.getContext().getColor(R.color.quantum_black_100));
            mCoworkerName.setAlpha((float) 1);

        } else {
            mCoworkerName.setText("not yet decided");
            mCoworkerName.setTypeface(mCoworkerName.getTypeface(), Typeface.ITALIC);
            mCoworkerName.setTextColor(itemView.getContext().getColor(R.color.quantum_grey700));
            mCoworkerName.setAlpha((float) 0.5);
        }

        if (user.getUrlPicture() != null) {
            Glide.with(itemView)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mCoworkerPict);
        } else {
            Glide.with(itemView)
                    .load(R.drawable.goforfood_front_logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mCoworkerPict);
        }
    }

}