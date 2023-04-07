package com.example.finalulidecap.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PostData extends AsyncTask<String, Void, JSONArray> {
    JSONObject postData;
    JSONArray arr = null;

    public PostData(Map<String, String> postData) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }

    @Override
    protected JSONArray doInBackground(String... urls) {
        try {

            URL url = new URL(urls[0]);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Authorization", "someAuthString");

            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                String response = convertInputStreamToString(inputStream);
                Log.i("RESULT", response);

                JSONObject obj = new JSONObject(response);
                arr = new JSONArray();
                arr.put(obj);

            } else {
                // Quando o código não devolve o 200
                Log.i("ERRO", ""+statusCode);
            }

        } catch (Exception e) {
            Log.d("ERRO", e.getLocalizedMessage());
            return arr;
        }

        return arr;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
