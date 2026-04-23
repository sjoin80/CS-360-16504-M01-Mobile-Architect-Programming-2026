package com.example.mobile2app_inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper
 *
 * Manages creation and version control of the SQLite database.
 * Contains tables for users and inventory items.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DB_NAME = "InventoryDB";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when database is first created.
     * Creates required tables.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, quantity INTEGER)");
    }

    /**
     * Called when database version changes.
     * Drops and recreates tables.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS inventory");
        onCreate(db);
    }
}