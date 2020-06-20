package com.example.astatine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.data.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class UserAppointmentsAdapter extends RecyclerView.Adapter<UserAppointmentsAdapter.AppointmentViewHolder> {

    public static String CENTER_KEY = "CENTER";
    public static String APPT_KEY = "APPT";

    private ArrayList<UserAppointment> mAppointments;

    public UserAppointmentsAdapter(ArrayList<UserAppointment> userAppointments) {
        mAppointments = userAppointments;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        UserAppointment appointment = mAppointments.get(position);
        holder.mUserAppointment = appointment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d 'at' h:mm a");
            holder.mAppointmentTime.setText(appointment.getLocalDateTime().format(formatter) + " at " + appointment.getTestingCenter().getName());
        } else {
            holder.mAppointmentTime.setText((appointment.getLocalDateTime().toString()));
        }
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mAppointmentTime;
        private UserAppointment mUserAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            mAppointmentTime = itemView.findViewById(R.id.appointment_time);
            mAppointmentTime.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + mUserAppointment.getTestingCenter().getAddress()));
            v.getContext().startActivity(intent);
        }
    }
}