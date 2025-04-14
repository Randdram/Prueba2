package com.example.prueba;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteContactActivity extends AppCompatActivity {
    private EditText etPhone;
    private Button btnDelete, btnBack;
    private ContactOperations contactOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact);

        etPhone = findViewById(R.id.etPhone);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        contactOps = new ContactOperations(this);

        btnDelete.setOnClickListener(v -> deleteContact());
        btnBack.setOnClickListener(v -> finish());
    }

    private void deleteContact() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Ingrese un número");
            return;
        }

        Contact contact = contactOps.getContactByPhone(phone);
        if (contact != null) {
            if (contactOps.deleteContact(contact.getId())) {
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
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