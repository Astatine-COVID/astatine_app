package com.example.astatine;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    public static String CENTER_KEY = "CENTER";
    public static String APPT_KEY = "APPT";

    private TestingCenter mCenter;

    public AppointmentsAdapter(TestingCenter testingCenter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            testingCenter.getAppointments().sort((o1, o2) -> o1.getLocalDateTime().compareTo(o2.getLocalDateTime()));
            for (int i = 0; i < testingCenter.getAppointments().size(); i++)
                testingCenter.getAppointments().get(i).setAppointmentKey(i);
            testingCenter.getAppointments().removeIf(appointment -> !appointment.isAvailable() || appointment.getLocalDateTime().isBefore(LocalDateTime.now()));
        }
        mCenter = testingCenter;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view, mCenter);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = mCenter.getAppointments().get(position);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d 'at' h:mm a");
            holder.mAppointmentTime.setText((appointment.getLocalDateTime().format(formatter)));
        } else {
            holder.mAppointmentTime.setText((appointment.getLocalDateTime().toString()));
        }
    }

    @Override
    public int getItemCount() {
        return mCenter.getAppointments().size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mAppointmentTime;
        private TestingCenter mTestingCenter;

        public AppointmentViewHolder(@NonNull View itemView, TestingCenter testingCenter) {
            super(itemView);
            mAppointmentTime = itemView.findViewById(R.id.appointment_time);
            mTestingCenter = testingCenter;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), AppointmentConfirmActivity.class);
            intent.putExtra(CENTER_KEY, mTestingCenter);
            intent.putExtra(APPT_KEY, mTestingCenter.getAppointments().get(getAdapterPosition()).getAppointmentKey()+1);
            v.getContext().startActivity(intent);
        }
    }
}
