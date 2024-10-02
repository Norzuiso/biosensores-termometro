package com.example.termometro_app_java;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.units.Temperature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String DEVICE_NAME = "Termometro"; // Nombre de ESP32 (debe de ser el mismo que se declaro en el programa del esp)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private TextView tvData;
    private TextView tvData2;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Temperatures temperatures;
    private LineChart lineChart;

    private static final String FILE_NAME = "temperature_records.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnGetTemperature = findViewById(R.id.btnGetTemperature);
        tvData = findViewById(R.id.tvData);
        tvData2 = findViewById(R.id.tvData2);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnShowRecords = findViewById(R.id.btnShowRecords);
        Button btnCleanRecords = findViewById(R.id.btnCleanRecords);
        lineChart = findViewById(R.id.lineChart);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not available");
            finish();
        }

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothPermissions();
            }
        });
        btnGetTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTemperature();
            }
        });
        // Save the current temperature record
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentTemperature();  // Example temperature, replace with actual data
            }
        });

        // Show saved temperature records
        btnShowRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavedRecords();
            }
        });
        // Show saved temperature records
        btnCleanRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSavedRecords();
            }
        });
    }

    private void clearSavedRecords() {
        try {
            // Open the file with MODE_PRIVATE, which truncates the file to zero length
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.close();
            tvData.setText("");  // Clear the display as well
            Log.d(TAG, "All temperature records cleared.");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing records", e);
        }
        showSavedRecords();
    }

    private void saveCurrentTemperature() {
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(temperatures.toStringOnFile() + "\n");
            osw.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving temperature", e);
        }

        // Mostrar los registros guardados
        showSavedRecords();
    }

    public static float randFloat(float min, float max) {

        Random rand = new Random();

        return rand.nextFloat() * (max - min) + min;

    }

    private void showSavedRecords() {
        List<Temperatures> records = new ArrayList<>();
        StringBuilder recordsstr= new StringBuilder();

        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            Temperatures temp;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("=");
                String s = split[1].split("\\|")[0];
                temp = new Temperatures(split[0], Float.parseFloat(s));
                recordsstr.append(temp.toStringSave()).append("\n");
                records.add(temp);
            }

            reader.close();
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, "Error reading records", e);
        }
        // Crear una lista de entradas para el gráfico
        List<Entry> entries = records.stream()
                .map(t -> new Entry(records.indexOf(t), t.getTempC()))
                .collect(Collectors.toList());

        // Crear el dataset para el gráfico de línea
        LineDataSet dataSet = new LineDataSet(entries, "Temperaturas");

        // Asignar el dataset al gráfico
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Formatear el eje X para que muestre la fecha y la hora
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Asegúrate de que los valores se muestren entre el mínimo y máximo de los datos
        xAxis.setAxisMinimum(entries.get(0).getX()); // Valor mínimo del eje X
        xAxis.setAxisMaximum(entries.get(entries.size() - 1).getX()); // Valor máximo del eje X


        // Refrescar el gráfico
        lineChart.invalidate();
        tvData2.setText(recordsstr.toString());

    }


    private void checkBluetoothPermissions() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToBluetoothDevice();
        }
    }

    private void connectToBluetoothDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                if (DEVICE_NAME.equals(device.getName())) {
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        bluetoothSocket.connect();
                        outputStream = bluetoothSocket.getOutputStream();
                        inputStream = bluetoothSocket.getInputStream();
                        showToast("Connected to " + DEVICE_NAME);
                        receiveDataFromBluetooth();
                    } catch (IOException e) {
                        Log.e(TAG, "Error connecting to device", e);
                        showToast("Connection failed");
                    }
                    break;
                }
            }
        } else {
            showToast("No paired devices found");
        }
    }

    private void requestTemperature() {
        if (outputStream != null) {
            try {
                outputStream.write("GET_TEMP\n".getBytes());
                receiveDataFromBluetooth();
            } catch (IOException e) {
                Log.e(TAG, "Error sending command", e);
                showToast("Failed to send command");
            }
        } else {
            showToast("Device not connected");
            temperatures = new Temperatures(randFloat(1, 40));

        }
    }

    private void receiveDataFromBluetooth() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getTemperaturesFromBT();
                    try {
                        runOnUiThread(() -> {
                            // Display the float value on the TextView
                            tvData.setText(String.format("Temperature:\n" + temperatures.toString()));
                        });
                    } catch (NumberFormatException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvData.setText("Error parsing temperature data.");
                            }
                        });
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Error reading from input stream", e);
                }
            }
        });

        thread.start();
    }

    private void getTemperaturesFromBT() throws IOException {
        int bytes;
        byte[] buffer = new byte[1024];
        // Read the data from Bluetooth
        bytes = inputStream.read(buffer);
        String incomingMessage = new String(buffer, 0, bytes); // Remove extra spaces and new lines
        String firstPart = incomingMessage.substring(0, 2);
        String secondPart = incomingMessage.substring(2, 4);
        String temperatureString = firstPart + "." + secondPart;
        float temperatureFromBt = Float.parseFloat(temperatureString);
        temperatures = new Temperatures(temperatureFromBt);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            connectToBluetoothDevice();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Bluetooth socket", e);
            }
        }
    }


    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
