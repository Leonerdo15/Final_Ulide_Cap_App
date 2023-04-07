package com.example.finalulidecap.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONArrayDownloader extends AsyncTask<String, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        Log.e("teste", "teste");
        try {
            Log.e("teste", "teste1");
            url = new URL(urls[0]);
            Log.e("teste", "teste2");
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.e("teste", "teste3");
            InputStream in = urlConnection.getInputStream();
            Log.e("teste", "teste4");
            InputStreamReader reader = new InputStreamReader(in);
            Log.e("teste", "teste5");
            int data = reader.read();
            while(data != -1) {
                char current = (char)data;
                result += current;
                data = reader.read();
            }

            Log.e("teste", result);
            JSONArray arr = new JSONArray(result);

            return arr;

        } catch (Exception e) {
            Log.e("JSON Class", "Error");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) { super.onPostExecute(jsonArray);}
}
