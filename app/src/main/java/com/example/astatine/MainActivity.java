package com.example.astatine;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                onSignedInInitialize();
                // User is signed in
            } else {
                // User is signed out

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);


            }


        };
//        TestingApiModel.getInstance();
//        writeToDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeToDatabase() {
        TestingApiModel model = TestingApiModel.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("TestingCenters");

        model.getTestingCenters().forEach((state, centers) -> {
            try {
                outer:
                for (int i = 0; i < centers.length(); i++) {
                    JSONObject loc = centers.getJSONObject(i).getJSONArray("physical_address").getJSONObject(0);
                    final LatLng latLng = getLocationFromAddress(
                            loc.getString("address_1")
                                    + loc.getString("city")
                                    + loc.getString("state_province")
                                    + loc.getString("postal_code")
                                    + loc.getString("country"));

                    JSONArray schedule = centers.getJSONObject(i).getJSONArray("regular_schedule");
                    LocalDateTime appt = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
                    if (schedule.length() == 0) {
                        continue;
                    }
                    ArrayList<Appointment> appointments = new ArrayList<>();
                    for (int i1 = 0; i1 < schedule.length(); i1++) {
                        JSONObject day = schedule.getJSONObject(i1);
                        LocalTime begin = null;
                        LocalTime end = null;
                        try {
                            begin = LocalTime.parse(day.getString("opens_at"), DateTimeFormatter.ofPattern("h:mm a"));
                            end = LocalTime.parse(day.getString("closes_at"), DateTimeFormatter.ofPattern("h:mm a"));
                        } catch (DateTimeParseException e) {
                            Log.e("Error Formatting", day.getString("opens_at"));
                            continue outer;
                        }
                        while (appt.toLocalTime().isBefore(end)) {
                            Log.i("Appointment", centers.getJSONObject(i).getString("name") + " in " + state + " at " + appt.toString());
                            if (appt.toLocalTime().equals(begin) || appt.toLocalTime().isAfter(begin)) {
                                appointments.add(new Appointment(appt.toString()));
                            }
                            appt = appt.plusMinutes(15);
                        }
                        appt = appt.plusDays(1).withHour(0).withMinute(0);

                    }
                    TestingCenter center = new TestingCenter(centers.getJSONObject(i).getString("name"),
                            centers.getJSONObject(i).getString("description"),
                            loc.getString("address_1") + " "
                                    + loc.getString("city") + " "
                                    + loc.getString("state_province") + " "
                                    + loc.getString("postal_code") + " "
                                    + loc.getString("country"),
                            latLng.latitude,
                            latLng.longitude,
                            appointments);
                    myRef.child(state + " " + centers.getJSONObject(i).getString("id")).setValue(center);
                    Log.i("Upload Status", "Uploaded " + centers.getJSONObject(i).getString("name") + " in " + state);
                }
                Log.i("Upload Status", "Upload complete");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    private void onSignedInInitialize() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.

            Log.i("USER display name", name);
            Log.i("USER email", email);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

}