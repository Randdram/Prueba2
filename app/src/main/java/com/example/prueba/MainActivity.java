package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnList = findViewById(R.id.btnList);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnSms = findViewById(R.id.btnSms);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddContactActivity.class)));
        btnSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchContactActivity.class)));
        btnList.setOnClickListener(v -> startActivity(new Intent(this, ContactListActivity.class)));
        btnCall.setOnClickListener(v -> startActivity(new Intent(this, CallActivity.class)));
        btnEdit.setOnClickListener(v -> startActivity(new Intent(this, EditContactActivity.class)));
        btnDelete.setOnClickListener(v -> startActivity(new Intent(this, DeleteContactActivity.class)));
        btnSms.setOnClickListener(v -> startActivity(new Intent(this, SendSmsActivity.class)));
    }
}