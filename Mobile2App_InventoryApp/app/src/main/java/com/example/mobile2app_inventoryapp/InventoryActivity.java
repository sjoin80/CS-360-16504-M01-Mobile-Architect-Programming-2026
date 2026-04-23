package com.example.mobile2app_inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * InventoryActivity
 *
 * Handles inventory with:
 * - Add
 * - Increment
 * - Decrement
 * - Delete
 * - Auto SMS trigger when quantity < 5
 */
public class InventoryActivity extends AppCompatActivity {

    private EditText itemNameInput, itemQtyInput;
    private Button addButton;
    private TableLayout tableLayout;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        dbHelper = new DatabaseHelper(this);

        itemNameInput = findViewById(R.id.itemNameInput);
        itemQtyInput = findViewById(R.id.itemQtyInput);
        addButton = findViewById(R.id.addButton);
        tableLayout = findViewById(R.id.tableLayout);

        addButton.setOnClickListener(v -> addItem());

        refreshTable();
    }

    /**
     * Add new item
     */
    private void addItem() {
        String name = itemNameInput.getText().toString().trim();
        String qtyString = itemQtyInput.getText().toString().trim();

        if (name.isEmpty() || qtyString.isEmpty()) {
            Toast.makeText(this, "Enter item name and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(qtyString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO inventory (name, quantity) VALUES (?, ?)",
                new Object[]{name, quantity}
        );

        checkLowInventory(name, quantity);

        itemNameInput.setText("");
        itemQtyInput.setText("");

        refreshTable();
    }

    /**
     * Refresh table UI
     */
    private void refreshTable() {
        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM inventory", null);

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int qty = cursor.getInt(2);

            TableRow row = new TableRow(this);

            TextView nameView = new TextView(this);
            nameView.setText(name);
            nameView.setPadding(8, 8, 8, 8);

            TextView qtyView = new TextView(this);
            qtyView.setText(String.valueOf(qty));
            qtyView.setPadding(8, 8, 8, 8);

            // ➕ Increment button
            Button incButton = new Button(this);
            incButton.setText("+");
            incButton.setOnClickListener(v -> updateQuantity(id, name, qty + 1));

            // ➖ Decrement button
            Button decButton = new Button(this);
            decButton.setText("-");
            decButton.setOnClickListener(v -> {
                if (qty > 0) {
                    updateQuantity(id, name, qty - 1);
                }
            });

            // ❌ Delete button
            Button deleteButton = new Button(this);
            deleteButton.setText("X");
            deleteButton.setOnClickListener(v -> deleteItem(id));

            row.addView(nameView);
            row.addView(qtyView);
            row.addView(incButton);
            row.addView(decButton);
            row.addView(deleteButton);

            tableLayout.addView(row);
        }

        cursor.close();
    }

    /**
     * Update quantity
     */
    private void updateQuantity(int id, String name, int newQty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE inventory SET quantity = ? WHERE id = ?",
                new Object[]{newQty, id}
        );

        checkLowInventory(name, newQty);

        refreshTable();
    }

    /**
     * Delete item
     */
    private void deleteItem(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM inventory WHERE id = ?", new Object[]{id});

        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();

        refreshTable();
    }

    /**
     * Trigger SMS if low
     */
    private void checkLowInventory(String name, int quantity) {
        if (quantity < 5) {
            Intent intent = new Intent(this, SmsActivity.class);
            intent.putExtra("itemName", name);
            intent.putExtra("quantity", quantity);
            startActivity(intent);
        }
    }
}