package com.example.prueba;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SendSmsActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 100;
    private EditText etPhone, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        etPhone = findViewById(R.id.etPhone);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnSelectContact = findViewById(R.id.btnSelectContact);

        String phone = getIntent().getStringExtra("PHONE");
        if (phone != null) {
            etPhone.setText(phone);
        }

        btnSend.setOnClickListener(v -> sendSMS());
        btnBack.setOnClickListener(v -> finish());
        btnSelectContact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactListActivity.class);
            intent.putExtra("SELECT_FOR_SMS", true);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String selectedNumber = data.getStringExtra("SELECTED_NUMBER");
            etPhone.setText(selectedNumber);
        }
    }

    private void sendSMS() {
        String phoneNumber = etPhone.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            etPhone.setError("Ingrese un número");
            return;
        }

        if (message.isEmpty()) {
            etMessage.setError("Ingrese un mensaje");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        } else {
            actuallySendSms(phoneNumber, message);
        }
    }

    private void actuallySendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS enviado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al enviar SMS: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, "Permiso denegado para enviar SMS",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}