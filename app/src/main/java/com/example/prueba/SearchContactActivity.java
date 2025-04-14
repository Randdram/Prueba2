package com.example.prueba;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SearchContactActivity extends AppCompatActivity {
    private EditText etPhone;
    private Button btnSearch, btnBack;
    private TextView tvResult;
    private ContactOperations contactOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        etPhone = findViewById(R.id.etPhone);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        tvResult = findViewById(R.id.tvResult);

        contactOps = new ContactOperations(this);

        btnSearch.setOnClickListener(v -> searchContact());
        btnBack.setOnClickListener(v -> finish());
    }

    private void searchContact() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Ingrese un número");
            return;
        }

        Contact contact = contactOps.getContactByPhone(phone);
        if (contact != null) {
            String result = "Nombre: " + contact.getName() + "\n" +
                    "Apellidos: " + contact.getLastname() + "\n" +
                    "Teléfonos: " + contact.getPhoneNumbers().toString();
            tvResult.setText(result);
        } else {
            tvResult.setText("Contacto no encontrado");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactOps.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        contactOps.close();
    }
}