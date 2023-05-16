package com.example.finalulidecap.ui.Main.ui.esp32;

import android.Manifest;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalulidecap.R;
import com.example.finalulidecap.ui.Main.MainActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

public class Esp32Fragment extends Fragment {

    private Esp32ViewModel mViewModel;

    private static final String TAG = "AAAAAAAAAAABBBBBBBBCCCCCCCC";
    private static final String DEVICE_NAME = "ESP32test";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private boolean mConnected = false;

    private EditText mDataEditText;
    private Button mSendButton;


    public static Esp32Fragment newInstance() {
        return new Esp32Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_esp32, container, false);
        mDataEditText = view.findViewById(R.id.data_edit_text);
        mSendButton = view.findViewById(R.id.send_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }

        return inflater.inflate(R.layout.fragment_esp32, container, false);
    }

    public void changeText(String newText) {
        mDataEditText.setText(newText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(Esp32ViewModel.class);
        // TODO: Use the ViewModel
    }

    private void sendData() {
        String data = mDataEditText.getText().toString();
        if (data.isEmpty()) {
            Toast.makeText(getContext(), "Data is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mConnected) {
            connectBluetooth();
        }
        if (mConnected) {
            try {
                mOutputStream.write(data.getBytes());
                mDataEditText.setText("");
                Toast.makeText(getContext(), "Data sent successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error writing to output stream", e);
                Toast.makeText(getContext(), "Error sending data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Bluetooth not connected", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Device not found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error connecting to Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }





    @Override
    public void onDestroy() {
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