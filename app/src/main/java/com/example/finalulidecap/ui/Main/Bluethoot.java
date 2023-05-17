package com.example.finalulidecap.ui.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalulidecap.R;
import com.example.finalulidecap.data.LoginDataSource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
public class Bluethoot extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String DEVICE_NAME = "ESP32test";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private boolean mConnected = false;

    private EditText mDataEditText;
    private Button mSendButton, mSendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        mDataEditText = findViewById(R.id.data_edit_text1);
        mSendButton = findViewById(R.id.send_button1);
        mSendId = findViewById(R.id.sendId);

        mDataEditText.setText("led_on");
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData(mDataEditText.getText().toString());
            }
        });

        mSendId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                sendData("id"+String.valueOf(LoginDataSource.ID));

            }
        });

        if (!checkPermission()) {
            Toast.makeText(this, "Bluetooth permission not granted, wait", Toast.LENGTH_LONG).show();
            requestPermission();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }




    }

    private void sendData(String data) {

        if (data.isEmpty()) {
            Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mConnected) {
            connectBluetooth();
        }
        if (mConnected) {
            try {
                mOutputStream.write(data.getBytes());
                mDataEditText.setText("");
                Toast.makeText(this, "Data sent successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error writing to output stream", e);
                Toast.makeText(this, "Error sending data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void connectBluetooth() {
        BluetoothDevice device = null;
        for (BluetoothDevice pairedDevice : mBluetoothAdapter.getBondedDevices()) {
            if (pairedDevice.getName().equals(DEVICE_NAME)) {
                device = pairedDevice;
                break;
            }
        }
        if (device == null) {
            Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
            return;
        }
        new ConnectBluetoothTask().execute(device);
    }

    private class ConnectBluetoothTask extends AsyncTask<BluetoothDevice, Void, Boolean> {
        @SuppressLint("MissingPermission")
        @Override
        protected Boolean doInBackground(BluetoothDevice... devices) {
            try {
                mSocket = devices[0].createRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();
                mOutputStream = mSocket.getOutputStream();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to Bluetooth", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            mConnected = connected;
            if (!connected) {
                Toast.makeText(Bluethoot.this, "Error connecting to Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        Toast.makeText(this, "Requesting Bluetooth permission", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                1);
        Toast.makeText(this, "Bluetooth permission requested, aaaaaa", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket", e);
            }
        }
    }
}