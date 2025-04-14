package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private ListView lvContacts;
    private ContactOperations contactOps;
    private List<Contact> contacts;
    private boolean selectForSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        lvContacts = findViewById(R.id.lvContacts);
        contactOps = new ContactOperations(this);
        selectForSms = getIntent().getBooleanExtra("SELECT_FOR_SMS", false);

        // Botón de regreso
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        registerForContextMenu(lvContacts);
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactOps.open();
        loadContacts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        contactOps.close();
    }

    private void loadContacts() {
        contacts = contactOps.getAllContacts();
        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                contacts);
        lvContacts.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Contact selectedContact = contacts.get(info.position);
        menu.setHeaderTitle(selectedContact.getName());

        if (selectForSms) {
            menu.add(0, v.getId(), 0, "Seleccionar para SMS");
        } else {
            getMenuInflater().inflate(R.menu.menu_contextual_contactos, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Contact selectedContact = contacts.get(info.position);

        if (selectForSms) {
            showNumberSelectionDialog(selectedContact, true);
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.menu_llamar) {
            showNumberSelectionDialog(selectedContact, false);
            return true;
        } else if (id == R.id.menu_editar) {
            editContact(selectedContact);
            return true;
        } else if (id == R.id.menu_eliminar) {
            deleteContact(selectedContact);
            return true;
        } else if (id == R.id.menu_enviar_sms) {
            showNumberSelectionDialog(selectedContact, true);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showNumberSelectionDialog(Contact contact, boolean forSms) {
        if (contact.getPhoneNumbers().isEmpty()) {
            Toast.makeText(this, "El contacto no tiene números", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(forSms ? "Seleccione número para SMS" : "Seleccione número para llamar");

        String[] numbers = contact.getPhoneNumbers().toArray(new String[0]);
        builder.setItems(numbers, (dialog, which) -> {
            if (forSms) {
                if (selectForSms) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_NUMBER", numbers[which]);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Intent smsIntent = new Intent(this, SendSmsActivity.class);
                    smsIntent.putExtra("PHONE", numbers[which]);
                    startActivity(smsIntent);
                }
            } else {
                Intent callIntent = new Intent(this, CallActivity.class);
                callIntent.putExtra("PHONE", numbers[which]);
                startActivity(callIntent);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void editContact(Contact contact) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("CONTACT_ID", contact.getId());
        startActivity(intent);
    }

    private void deleteContact(Contact contact) {
        if (contactOps.deleteContact(contact.getId())) {
            loadContacts();
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}