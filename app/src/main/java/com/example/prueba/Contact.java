package com.example.prueba;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class Contact {
    private long id;
    private String name;
    private String lastname;
    private List<String> phoneNumbers;

    public Contact() {
        phoneNumbers = new ArrayList<>();
    }

    public Contact(String name, String lastname) {
        this();
        this.name = name;
        this.lastname = lastname;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }
    public void addPhoneNumber(String number) { phoneNumbers.add(number); }

    @Override
    public String toString() {
        return name + " " + lastname + " - " + TextUtils.join(", ", phoneNumbers);
    }
}