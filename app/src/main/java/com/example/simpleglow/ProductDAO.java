package com.example.simpleglow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.annotation.SuppressLint;

public class ProductDAO
{
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public ProductDAO(Context context)
    {

        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {

        dbHelper.close();
    }

    // Add a new product
    public long addProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, product.getName());
        values.put(DBHelper.COLUMN_QUANTITY, product.getQuantity());
        values.put(DBHelper.COLUMN_CATEGORY, product.getCategory());
        return database.insert(DBHelper.TABLE_PRODUCTS, null, values);
    }

    // Get all products
    public Cursor getAllProducts() {
        return database.query(DBHelper.TABLE_PRODUCTS,
                null, null, null, null, null, null);
    }

    // Delete a product by ID
    public void deleteProduct(long productId) {
        database.delete(DBHelper.TABLE_PRODUCTS,
                DBHelper.COLUMN_ID + " = " + productId, null);
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean isTaken = cursor.getCount() > 0;
        cursor.close();
        return isTaken;
    }

    // Get a user by username
    public User getUser(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD));
            @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_NUMBER));
            return new User(username, password, phoneNumber);
        }

        cursor.close();
        return null; // Return null if no user found
    }
    // Add a new user to the database
    public void addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DBHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DBHelper.COLUMN_PHONE_NUMBER, user.getPhoneNumber());

        db.insert(DBHelper.TABLE_USERS, null, values);
    }

    // Check if a user with the given username and password exists
    public boolean isValidUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " +
                DBHelper.COLUMN_USERNAME + " = ? AND " + DBHelper.COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Check if a user is subscribed based on the username
    public boolean isUserSubscribed(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " +
                DBHelper.COLUMN_USERNAME + " = ?"; // Adjust the query based on your database schema
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean isSubscribed = cursor.getCount() > 0;
        cursor.close();
        return isSubscribed;
    }

}
