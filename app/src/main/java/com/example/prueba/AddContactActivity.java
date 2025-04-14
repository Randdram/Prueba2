package com.example.prueba;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {
    private EditText etName, etLastname;
    private LinearLayout phoneNumbersContainer;
    private Button btnAddNumber, btnSave, btnBack;
    private ContactOperations contactOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Inicializar vistas
        etName = findViewById(R.id.etName);
        etLastname = findViewById(R.id.etLastname);
        phoneNumbersContainer = findViewById(R.id.phoneNumbersContainer);
        btnAddNumber = findViewById(R.id.btnAddNumber);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        contactOps = new ContactOperations(this);

        // Configurar listeners
        btnAddNumber.setOnClickListener(v -> addPhoneNumberField());
        btnSave.setOnClickListener(v -> saveContact());
        btnBack.setOnClickListener(v -> finish());

        // Añadir primer campo de número
        addPhoneNumberField();
    }

    private void addPhoneNumberField() {
        EditText etPhone = new EditText(this);
        etPhone.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        etPhone.setHint("Número de teléfono");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        phoneNumbersContainer.addView(etPhone);
    }

    private void saveContact() {
        String name = etName.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();

        if (name.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Contact contact = new Contact(name, lastname);

        // Recoger todos los números ingresados
        for (int i = 0; i < phoneNumbersContainer.getChildCount(); i++) {
            View view = phoneNumbersContainer.getChildAt(i);
            if (view instanceof EditText) {
                String number = ((EditText) view).getText().toString().trim();
                if (!number.isEmpty()) {
                    if (contactOps.isPhoneNumberExists(number)) {
                        ((EditText) view).setError("Este número ya existe");
                        return;
                    }
                    contact.addPhoneNumber(number);
                }
            }
        }

        if (contact.getPhoneNumbers().isEmpty()) {
            Toast.makeText(this, "Debe ingresar al menos un número", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = contactOps.addContact(contact);
        if (result != -1) {
            Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al agregar contacto", Toast.LENGTH_SHORT).show();
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