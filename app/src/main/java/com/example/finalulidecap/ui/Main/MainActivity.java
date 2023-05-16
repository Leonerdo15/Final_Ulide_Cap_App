package com.example.finalulidecap.ui.Main;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.finalulidecap.R;
import com.example.finalulidecap.databinding.ActivityMainBinding;
import com.example.finalulidecap.server.TinyWebServer;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!checkPermission()) {
                Toast.makeText(this, "Bluetooth permission not granted, wait", Toast.LENGTH_LONG).show();
                requestPermission();
            }
        }

        File filesDir = getFilesDir();
        String dir = filesDir.getAbsolutePath();
        Log.d(TAG, dir);

        Context context = getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.e("IP", "" + ip);

        String filename = "index.html";

        criaIndex(filename, ip);

        //TinyWebServer.startServer("localhost",8080, dir);
        TinyWebServer.startServer(ip,8080, dir);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_esp32)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        Handler mHandler= new Handler();
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // do your stuff here, called every second
//                Log.d("POSTED", "POSTED" + TinyWebServer.DATA_POSTED);
//                mHandler.postDelayed(this, 5000);
//            }
//        };
//
//        mHandler.post(runnable);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //stop webserver on destroy of service or process
        TinyWebServer.stopServer();
    }

    public void criaIndex(String filename, String ip) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(ip.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestPermission() {
        Toast.makeText(this, "Requesting Bluetooth permission", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                1);
        Toast.makeText(this, "Bluetooth permission requested, aaaaaa", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}