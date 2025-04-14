package com.example.prueba;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CallActivity extends AppCompatActivity {
    private EditText etPhone;
    private static final int REQUEST_CALL_PHONE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        etPhone = findViewById(R.id.etPhone);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnBack = findViewById(R.id.btnBack);

        // Prellenar número si viene de la lista de contactos
        String phone = getIntent().getStringExtra("PHONE");
        if (phone != null) {
            etPhone.setText(phone);
        }

        btnCall.setOnClickListener(v -> makeCall());
        btnBack.setOnClickListener(v -> finish());
    }

    private void makeCall() {
        String phoneNumber = etPhone.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            etPhone.setError("Ingrese un número");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);
        } else {
            startCall(phoneNumber);
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(this, "Permiso denegado para llamar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}