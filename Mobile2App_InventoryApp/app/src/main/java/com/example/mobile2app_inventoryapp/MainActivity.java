package com.example.mobile2app_inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles user authentication.
 * - Validates login credentials against the SQLite database
 * - Allows new users to register
 * - Navigates to InventoryActivity upon successful login
 */
public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton, registerButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Bind UI elements
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Handle login action
        loginButton.setOnClickListener(v -> handleLogin());

        // Handle user registration (basic implementation for Project 3)
        registerButton.setOnClickListener(v -> handleRegistration());
    }

    /**
     * Validates user credentials against the database.
     * If valid, navigates to InventoryActivity.
     */
    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Basic input validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query database for matching user
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        if (cursor.moveToFirst()) {
            // Successful login
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // Navigate to inventory screen
            startActivity(new Intent(this, InventoryActivity.class));
        } else {
            // Invalid credentials
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    /**
     * Registers a new user by inserting credentials into the database.
     * Prevents duplicate usernames.
     */
    private void handleRegistration() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password to register", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if user already exists
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        cursor.close();

        // Insert new user
        db.execSQL(
                "INSERT INTO users (username, password) VALUES (?, ?)",
                new Object[]{username, password}
        );

        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
    }
}