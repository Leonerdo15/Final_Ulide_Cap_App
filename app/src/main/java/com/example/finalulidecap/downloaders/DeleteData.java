package com.example.finalulidecap.downloaders;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteData extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("DELETE");

            //urlConnection.setRequestProperty("Authorization", "someAuthString");

            urlConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
