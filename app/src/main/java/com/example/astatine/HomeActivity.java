package com.example.astatine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void goToMap(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void goToAppointments(View view) {
        startActivity(new Intent(this, AppointmentsActivity.class));
    }
}