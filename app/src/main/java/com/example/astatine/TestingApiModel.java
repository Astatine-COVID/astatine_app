package com.example.astatine;

import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


final public class TestingApiModel {
    private static TestingApiModel instance;

    static {
        instance = new TestingApiModel();
        String[] states = {"arizona",
                "california",
                "delaware",
                "florida",
                "massachusetts",
                "nevada",
                "new-jersey",
                "new-york",
                "pennsylvania",
                "texas",
                "utah",
                "washington"};
        for (String state : states) {
            try {
                instance.testingCenters.put(state, new JsonGetter().execute(state).get());
                Log.i("Loading Status", "Loaded " + state);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("Loading Status", "Loading Done");
    }

    private ArrayMap<String, JSONArray> testingCenters;

    public static TestingApiModel getInstance() {
        return instance;
    }

    private TestingApiModel() {
        testingCenters = new ArrayMap<>();
    }

    public JSONArray getTestingCenter(String state) {
        return testingCenters.get(state);
    }

    public ArrayMap<String, JSONArray> getTestingCenters() {
        return testingCenters;
    }

    private static class JsonGetter extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://covid-19-testing.github.io/locations/" + strings[0] + "/complete.json");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);

                }
                return new JSONArray(buffer.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
