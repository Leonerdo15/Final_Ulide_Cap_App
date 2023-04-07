package com.example.finalulidecap.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONObjDownloader extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
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

            Log.e("JSON", result);
            JSONObject obj = new JSONObject(result);

            return obj;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) { super.onPostExecute(jsonObject);}
}
