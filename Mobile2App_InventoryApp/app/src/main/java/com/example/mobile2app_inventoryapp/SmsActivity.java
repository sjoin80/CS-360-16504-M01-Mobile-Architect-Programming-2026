package com.example.mobile2app_inventoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * SmsActivity
 *
 * Handles SMS permission and sends alerts for low inventory.
 */
public class SmsActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;

    private TextView statusText;
    private Button enableSmsButton, sendSmsButton;

    private String itemName;
    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        statusText = findViewById(R.id.statusText);
        enableSmsButton = findViewById(R.id.enableSmsButton);
        sendSmsButton = findViewById(R.id.testSmsButton);

        // Get passed data
        Intent intent = getIntent();
        if (intent != null) {
            itemName = intent.getStringExtra("itemName");
            quantity = intent.getIntExtra("quantity", -1);
        }

        updateStatus();

        enableSmsButton.setOnClickListener(v -> checkPermission());
        sendSmsButton.setOnClickListener(v -> sendSms());
    }

    /**
     * Update UI status
     */
    private void updateStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            statusText.setText("SMS Enabled\nItem: " + itemName + " (" + quantity + ")");

        } else {
            statusText.setText("SMS Permission Required");
        }
    }

    /**
     * Check/request permission
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);

        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            updateStatus();
        }
    }

    /**
     * Send SMS
     */
    private void sendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            String message = "Low Inventory Alert: " + itemName +
                    " is low (" + quantity + " remaining)";

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("1234567890", null, message, null, null);

                Toast.makeText(this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();

                // 🔥 Automatically go back to Inventory screen
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "SMS Failed", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle permission result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                updateStatus();

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}