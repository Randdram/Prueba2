package com.example.prueba;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SendWhatsappActivity extends AppCompatActivity {
    private EditText etPhone, etMessage;
    private static final String WHATSAPP_PACKAGE = "com.whatsapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_whatsapp);

        etPhone = findViewById(R.id.etWhatsappPhone);
        etMessage = findViewById(R.id.etWhatsappMessage);
        Button btnSend = findViewById(R.id.btnSendWhatsapp);
        Button btnSelect = findViewById(R.id.btnSelectWhatsappContact);
        Button btnBack = findViewById(R.id.btnWhatsappBack);

        // Precargar número si viene de selección
        String phone = getIntent().getStringExtra("PHONE");
        if (phone != null) {
            etPhone.setText(phone);
        }

        btnSend.setOnClickListener(v -> sendWhatsappMessage());
        btnSelect.setOnClickListener(v -> selectContact());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendWhatsappMessage() {
        String phone = etPhone.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (phone.isEmpty()) {
            Toast.makeText(this, "Ingrese un número", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar número (eliminar espacios, guiones, etc.)
        phone = phone.replaceAll("[^0-9+]", "");

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://wa.me/" + phone + "?text=" + Uri.encode(message);
            intent.setPackage(WHATSAPP_PACKAGE);
            intent.setData(Uri.parse(url));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
                // Alternativa: abrir WhatsApp Web
                openWhatsappWeb(phone, message);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openWhatsappWeb(String phone, String message) {
        String url = "https://web.whatsapp.com/send?phone=" + phone + "&text=" + Uri.encode(message);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void selectContact() {
        Intent intent = new Intent(this, ContactListActivity.class);
        intent.putExtra("SELECT_FOR_WHATSAPP", true);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String number = data.getStringExtra("SELECTED_NUMBER");
            etPhone.setText(number);
        }
    }
}