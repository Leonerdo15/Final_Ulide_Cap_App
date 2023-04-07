package com.example.finalulidecap.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONStringDownloader extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();
            while(data != -1) {
                char current = (char)data;
                result += current;
                data = reader.read();
            }

            Log.e("JSON", result);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String jsonString) { super.onPostExecute(jsonString);}
}
