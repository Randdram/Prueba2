package com.example.prueba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ContactsDB";
    private static final int DATABASE_VERSION = 3;

    // Tabla de contactos
    public static final String TABLE_CONTACTS = "contacts";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LASTNAME = "lastname";

    // Tabla de teléfonos
    public static final String TABLE_PHONES = "phones";
    public static final String KEY_PHONE_ID = "phone_id";
    public static final String KEY_CONTACT_ID = "contact_id";
    public static final String KEY_PHONE_NUMBER = "number";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_LASTNAME + " TEXT NOT NULL)";

        String CREATE_PHONES_TABLE = "CREATE TABLE " + TABLE_PHONES + "("
                + KEY_PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_ID + " INTEGER NOT NULL,"
                + KEY_PHONE_NUMBER + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_CONTACT_ID + ") REFERENCES "
                + TABLE_CONTACTS + "(" + KEY_ID + "))";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_PHONES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public boolean isDatabaseIntegrityOk() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.rawQuery("PRAGMA integrity_check", null).close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }
    }

    public void recreateDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            onUpgrade(db, 1, DATABASE_VERSION);
        } finally {
            db.close();
        }
    }
}