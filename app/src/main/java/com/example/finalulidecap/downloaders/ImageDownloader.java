package com.example.finalulidecap.downloaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            // Criar a ligação
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Criar uma Stream para leitura de dados
            InputStream in = connection.getInputStream();

            // Guardar e construir a imagem
            Bitmap myBitmap = BitmapFactory.decodeStream(in);

            // Devolver a imagem
            return myBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
