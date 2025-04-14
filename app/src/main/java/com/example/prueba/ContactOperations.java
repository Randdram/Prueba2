package com.example.prueba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactOperations {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ContactOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addContact(Contact contact) {
        database.beginTransaction();
        try {
            ContentValues contactValues = new ContentValues();
            contactValues.put(DatabaseHelper.KEY_NAME, contact.getName());
            contactValues.put(DatabaseHelper.KEY_LASTNAME, contact.getLastname());

            long contactId = database.insert(DatabaseHelper.TABLE_CONTACTS, null, contactValues);

            for (String number : contact.getPhoneNumbers()) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(DatabaseHelper.KEY_CONTACT_ID, contactId);
                phoneValues.put(DatabaseHelper.KEY_PHONE_NUMBER, number);
                database.insert(DatabaseHelper.TABLE_PHONES, null, phoneValues);
            }

            database.setTransactionSuccessful();
            return contactId;
        } catch (Exception e) {
            Log.e("ContactOperations", "Error adding contact", e);
            return -1;
        } finally {
            database.endTransaction();
        }
    }

    public Contact getContact(String phone) {
        return getContactByPhone(phone);
    }

    public Contact getContactById(long id) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_CONTACTS + " WHERE " + DatabaseHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(0));
            contact.setName(cursor.getString(1));
            contact.setLastname(cursor.getString(2));

            List<String> numbers = new ArrayList<>();
            Cursor phoneCursor = database.query(
                    DatabaseHelper.TABLE_PHONES,
                    new String[]{DatabaseHelper.KEY_PHONE_NUMBER},
                    DatabaseHelper.KEY_CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())},
                    null, null, null);

            while (phoneCursor.moveToNext()) {
                numbers.add(phoneCursor.getString(0));
            }
            phoneCursor.close();

            contact.setPhoneNumbers(numbers);
            cursor.close();
            return contact;
        }
        return null;
    }

    public Contact getContactByPhone(String phone) {
        String query = "SELECT c.* FROM " + DatabaseHelper.TABLE_CONTACTS + " c " +
                "JOIN " + DatabaseHelper.TABLE_PHONES + " p ON c." + DatabaseHelper.KEY_ID + " = p." + DatabaseHelper.KEY_CONTACT_ID + " " +
                "WHERE p." + DatabaseHelper.KEY_PHONE_NUMBER + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{phone});

        if (cursor != null && cursor.moveToFirst()) {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(0));
            contact.setName(cursor.getString(1));
            contact.setLastname(cursor.getString(2));

            List<String> numbers = new ArrayList<>();
            Cursor phoneCursor = database.query(
                    DatabaseHelper.TABLE_PHONES,
                    new String[]{DatabaseHelper.KEY_PHONE_NUMBER},
                    DatabaseHelper.KEY_CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())},
                    null, null, null);

            while (phoneCursor.moveToNext()) {
                numbers.add(phoneCursor.getString(0));
            }
            phoneCursor.close();

            contact.setPhoneNumbers(numbers);
            cursor.close();
            return contact;
        }
        return null;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_CONTACTS,
                new String[]{DatabaseHelper.KEY_ID, DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_LASTNAME},
                null, null, null, null, DatabaseHelper.KEY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getLong(0));
                contact.setName(cursor.getString(1));
                contact.setLastname(cursor.getString(2));

                List<String> numbers = new ArrayList<>();
                Cursor phoneCursor = database.query(
                        DatabaseHelper.TABLE_PHONES,
                        new String[]{DatabaseHelper.KEY_PHONE_NUMBER},
                        DatabaseHelper.KEY_CONTACT_ID + " = ?",
                        new String[]{String.valueOf(contact.getId())},
                        null, null, null);

                while (phoneCursor.moveToNext()) {
                    numbers.add(phoneCursor.getString(0));
                }
                phoneCursor.close();

                contact.setPhoneNumbers(numbers);
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }

    public boolean updateContact(Contact contact) {
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_NAME, contact.getName());
            values.put(DatabaseHelper.KEY_LASTNAME, contact.getLastname());

            database.update(
                    DatabaseHelper.TABLE_CONTACTS,
                    values,
                    DatabaseHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())});

            database.delete(
                    DatabaseHelper.TABLE_PHONES,
                    DatabaseHelper.KEY_CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())});

            for (String number : contact.getPhoneNumbers()) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(DatabaseHelper.KEY_CONTACT_ID, contact.getId());
                phoneValues.put(DatabaseHelper.KEY_PHONE_NUMBER, number);
                database.insert(DatabaseHelper.TABLE_PHONES, null, phoneValues);
            }

            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("ContactOperations", "Error updating contact", e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public boolean deleteContact(long contactId) {
        database.beginTransaction();
        try {
            database.delete(
                    DatabaseHelper.TABLE_PHONES,
                    DatabaseHelper.KEY_CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contactId)});

            database.delete(
                    DatabaseHelper.TABLE_CONTACTS,
                    DatabaseHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(contactId)});

            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("ContactOperations", "Error deleting contact", e);
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PHONES,
                new String[]{DatabaseHelper.KEY_PHONE_ID},
                DatabaseHelper.KEY_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}