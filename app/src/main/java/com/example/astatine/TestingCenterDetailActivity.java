package com.example.astatine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TestingCenterDetailActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mAddress;
    private TextView mDescription;
    private RecyclerView mAppointmentList;
    private TestingCenter mTestingCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_center_detail);
        mTitle = findViewById(R.id.testing_center_title);
        mAddress = findViewById(R.id.testing_center_address);
        mDescription = findViewById(R.id.testing_center_desc);
        mAppointmentList = findViewById(R.id.appointments_list);
        mTestingCenter = (TestingCenter) getIntent().getSerializableExtra(MapsActivity.MARKER_TAG_KEY);

        mTitle.setText(mTestingCenter.getName());
        mAddress.setText(mTestingCenter.getAddress());
        mDescription.setText(mTestingCenter.getDescription());
        AppointmentsAdapter appointmentsAdapter = new AppointmentsAdapter(mTestingCenter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        mAppointmentList.setLayoutManager(layoutManager);
        mAppointmentList.setAdapter(appointmentsAdapter);
    }

    public void getDirections(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + mTestingCenter.getAddress()));
        startActivity(intent);
    }
}