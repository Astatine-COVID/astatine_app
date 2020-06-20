package com.example.astatine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.format.DateTimeFormatter;

public class AppointmentConfirmActivity extends AppCompatActivity {

    private TextView mAppointmentLabel;
    private TestingCenter mTestingCenter;
    private int mKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_confirm);
        mAppointmentLabel = findViewById(R.id.appointment_time);
        mTestingCenter = (TestingCenter) getIntent().getSerializableExtra(AppointmentsAdapter.CENTER_KEY);
        mKey = getIntent().getIntExtra(AppointmentsAdapter.APPT_KEY, -1);
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("EEE MMM d 'at' h:mm a");
            mAppointmentLabel.setText(mTestingCenter.getAppointments().get(mKey).getLocalDateTime().format(formatter));
        }
    }

    public void makeAppointment(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference tcRef = database.getReference("TestingCenters");
        DatabaseReference apptRef = database.getReference("Appointments").child(mTestingCenter.getKey() + "" + mKey + auth.getCurrentUser().getDisplayName());

        Log.i("Firebase Path", "TestingCenters/" + "appointments/" + mTestingCenter.getKey() + "/" + mKey + "/available");

        tcRef.child(mTestingCenter.getKey()).child("appointments").child(mKey + "").child("available").setValue(false);
        tcRef.child(mTestingCenter.getKey()).child("appointments").child(mKey + "").child("patientEmail").setValue(auth.getCurrentUser().getEmail());

        apptRef.setValue(new UserAppointment(mTestingCenter.getAppointments().get(mKey).getLocalDateTime().toString(), auth.getCurrentUser().getEmail(), mTestingCenter));

        Intent intent = new Intent(this, HomeActivity.class);
        Toast.makeText(this, R.string.appt_sch, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}