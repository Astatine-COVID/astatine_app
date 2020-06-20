package com.example.astatine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentsActivity extends AppCompatActivity {
    private RecyclerView mAppointmentsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Appointments");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mAppointmentsView = findViewById(R.id.user_appt_view);
        final ArrayList<UserAppointment> userAppointments = new ArrayList<>();
        UserAppointmentsAdapter userAppointmentsAdapter = new UserAppointmentsAdapter(userAppointments);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.i("Display Name",auth.getCurrentUser().getDisplayName());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserAppointment appointment = snapshot.getValue(UserAppointment.class);

                    Log.i("Key",snapshot.getKey());
                    if (snapshot.getKey().contains(auth.getCurrentUser().getDisplayName())) {
                        userAppointments.add(appointment);
                        userAppointmentsAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error reading database", "Failed to read value.", error.toException());
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mAppointmentsView.setLayoutManager(layoutManager);
        mAppointmentsView.setAdapter(userAppointmentsAdapter);
    }
}