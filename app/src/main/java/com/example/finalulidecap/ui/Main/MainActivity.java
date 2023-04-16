package com.example.finalulidecap.ui.Main;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;

import com.example.finalulidecap.R;
import com.example.finalulidecap.databinding.ActivityMainBinding;
import com.example.finalulidecap.server.TinyWebServer;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private static final String TAG = "HTTPDIR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        if (!checkPermissions()) {
            requestPermissions();
        }

        if (!checkPermissions()) {
            Log.d(TAG, "Não foram dadas permissões");
        } else {
            Log.d(TAG, "Já há permissões de escrita");
        }

        File filesDir = getFilesDir();
        String dir = filesDir.getAbsolutePath();
        Log.d(TAG, dir);

        Context context = getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.e("IP", "" + ip);

        criaIndex(filesDir, ip);

        //TinyWebServer.startServer("localhost",8080, dir);
        TinyWebServer.startServer(ip,8080, dir);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //stop webserver on destroy of service or process
        TinyWebServer.stopServer();
    }

    public void criaIndex(File filesDir, String ip) {
        try {
            File f = new File(filesDir + "/index.html");

            if (f.exists()) {
                Log.d(TAG, "Ficheiro index.html existe");
                f.delete();
                Log.d(TAG, "Vou abrir ficheiro para escrita");
                FileWriter out = new FileWriter(f, true);
                Log.d(TAG, "Abri ficheiro para escrita");
                out.append("<html><head><meta charset='utf-8'></head><body><h2>Android</h2><p>O meu IP é: " + ip + "</p></body></html>");
                Log.d(TAG, "Escrevi no ficheiro");
                out.flush();
                out.close();
            }

        } catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Erro a criar o ficheiro: " + e.toString());
        }
    }

    private boolean checkPermissions() {
        Log.d(TAG, "checkPermissions");
        boolean perm = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                ;
        return  perm;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}