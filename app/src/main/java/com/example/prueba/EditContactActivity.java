package com.example.prueba;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class EditContactActivity extends AppCompatActivity {
    private EditText etName, etLastname, etSearchPhone;
    private LinearLayout phoneNumbersContainer;
    private Button btnAddNumber, btnUpdate, btnSearch;
    private ContactOperations contactOps;
    private Contact contactToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        // Inicialización de vistas
        etSearchPhone = findViewById(R.id.etSearchPhone);
        etName = findViewById(R.id.etName);
        etLastname = findViewById(R.id.etLastname);
        phoneNumbersContainer = findViewById(R.id.phoneNumbersContainer);
        btnAddNumber = findViewById(R.id.btnAddNumber);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSearch = findViewById(R.id.btnSearch);

        contactOps = new ContactOperations(this);

        btnSearch.setOnClickListener(v -> searchContact());
        btnAddNumber.setOnClickListener(v -> addPhoneNumberField(""));
        btnUpdate.setOnClickListener(v -> updateContact());
    }

    private void searchContact() {
        String phone = etSearchPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etSearchPhone.setError("Ingrese un número");
            return;
        }

        contactToEdit = contactOps.getContactByPhone(phone);
        if (contactToEdit != null) {
            findViewById(R.id.editFieldsContainer).setVisibility(View.VISIBLE);
            loadContactData();
        } else {
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadContactData() {
        etName.setText(contactToEdit.getName());
        etLastname.setText(contactToEdit.getLastname());

        for (String number : contactToEdit.getPhoneNumbers()) {
            addPhoneNumberField(number);
        }
    }

    private void addPhoneNumberField(String number) {
        EditText etPhone = new EditText(this);
        etPhone.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        etPhone.setHint("Número de teléfono");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        etPhone.setText(number);
        phoneNumbersContainer.addView(etPhone);
    }

    private void updateContact() {
        String name = etName.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();

        if (name.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        contactToEdit.setName(name);
        contactToEdit.setLastname(lastname);
        contactToEdit.getPhoneNumbers().clear();

        // Obtener todos los números ingresados
        for (int i = 0; i < phoneNumbersContainer.getChildCount(); i++) {
            View view = phoneNumbersContainer.getChildAt(i);
            if (view instanceof EditText) {
                String number = ((EditText) view).getText().toString().trim();
                if (!number.isEmpty()) {
                    contactToEdit.addPhoneNumber(number);
                }
            }
        }

        if (contactToEdit.getPhoneNumbers().isEmpty()) {
            Toast.makeText(this, "Debe ingresar al menos un número", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactOps.updateContact(contactToEdit)) {
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
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