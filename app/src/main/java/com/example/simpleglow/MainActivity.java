package com.example.simpleglow;

import androidx.appcompat.app.AppCompatActivity;
import com.example.simpleglow.ProductDAO;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button subscribeButton;
    private Button logoutButton;
    private ProductDAO productDAO;
    private User currentUser;
    private LinearLayout productContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the product DAO
        productDAO = new ProductDAO(this);
        productDAO.open();

        // Temporary login button for testing purposes
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view)
            {
                login(); // Call the login method when the button is clicked
            }
        });
        subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                subscribe();
            }
        });
        // Inside the updateMainScreen() method, after initializing buttons
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                logout(view);
            }

        });
    }

    @SuppressLint("MissingInflatedId")

    private void updateMainScreen() {
        setContentView(R.layout.main_screen);

        // Display the connected user's name
        TextView connectedUserTextView = findViewById(R.id.connectedUserTextView);
        if (currentUser != null) {
            connectedUserTextView.setText(getString(R.string.connected_user_text, currentUser.getUsername()));
        } else {
            // Handle the case where currentUser is null
            connectedUserTextView.setText(getString(R.string.default_user_text));
            // or display a message, log an error, etc.
            Toast.makeText(this, "User not found or database issue", Toast.LENGTH_SHORT).show();
        }

        // Initialize the product container
        productContainer = findViewById(R.id.productContainer);

        // Add product button and functionality
        Button addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement the logic for adding a product
                addProductToContainer(productContainer,"Jewelry Marriage Ring", 1, "Jewelry");
            }
        });

        // Remove product button and functionality
        Button removeProductButton = findViewById(R.id.removeProductButton);
        removeProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement the logic for removing a product
                removeProductFromContainer(productContainer);

            }
        });

        // Update the UI based on whether a user is logged in or not
        if (currentUser != null) {
            // User is logged in
            // Show relevant UI components, e.g., hide login and subscribe buttons
            loginButton.setVisibility(View.GONE);
            subscribeButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // No user is logged in
            // Show relevant UI components, e.g., show login and subscribe buttons
            loginButton.setVisibility(View.VISIBLE);
            subscribeButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    // New method to add a product with category to the container
    private void addProductToContainer(LinearLayout productContainer, String productName, int quantity, String category) {
        Product product = new Product(productName, quantity, category);
        long productId = productDAO.addProduct(product);

        // Display the added product in the UI
        TextView productTextView = new TextView(this);
        productTextView.setText(productName + " - Quantity: " + quantity + " - Category: " + category);
        productContainer.addView(productTextView);
    }

    private void removeProductFromContainer(LinearLayout productContainer) {
        if (this.productContainer.getChildCount() > 0)
        {
            this.productContainer.removeViewAt(productContainer.getChildCount() - 1);
        }
    }



    // Method to handle click event of the logout button
    public void logout(View view) {
        currentUser = null;
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        updateMainScreen();


        // Update the UI to show the login screen
        setContentView(R.layout.activity_main);

        // Initialize the login button and other UI components as needed
        loginButton = findViewById(R.id.loginButton);
        subscribeButton = findViewById(R.id.subscribeButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set visibility of buttons
        loginButton.setVisibility(View.VISIBLE);
        subscribeButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
        //finish(); // Close the current activity

    }



    private void login() {
        // Get username and password from EditText fields
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Check if the fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the username exists
        if (!productDAO.isUsernameTaken(username)) {
            Toast.makeText(this, "User does not exist. Please subscribe to register.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is subscribed
        if (!productDAO.isUserSubscribed(username)) {
            Toast.makeText(this, "Please subscribe to login.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the username and password match
        if (isValidUser(username, password)) {
            // Set the current user
            currentUser = productDAO.getUser(username);

            // Update the UI
            updateMainScreen();
        } else
        {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean subscribe() {
        // Get username, password, and phone number from EditText fields
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phoneNumber = phoneEditText.getText().toString();

        // Check if any of the fields are empty
        if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the username is already taken
        if (productDAO.isUsernameTaken(username)) {
            Toast.makeText(this, "Username is already taken, please try another username", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check password conditions
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password should have at least one uppercase letter, one lowercase letter, and be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check phone number length
        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Phone number should be 10 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Proceed with subscription
        User newUser = new User(username, password, phoneNumber);
        // Add the new user to the database
        productDAO.addUser(newUser);
        // Display a success message
        Toast.makeText(this, "Subscription successful", Toast.LENGTH_SHORT).show();

        // Now, try to log in the newly subscribed user
        if (isValidUser(username, password)) {
            // Set the current user
            currentUser = productDAO.getUser(username);
            // Update the UI
            updateMainScreen();
        } else {
            // Display an error message if login fails
            Toast.makeText(this, "User not found or database issue", Toast.LENGTH_SHORT).show();
        }

        return true;
    }


    // Method to handle user registration
    private void registerUser() {
        // Get username, password, and phone number from EditText fields
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phoneNumber = phoneEditText.getText().toString();

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the username is already taken
        if (productDAO.isUsernameTaken(username)) {
            Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the new user to the database
        User newUser = new User(username, password, phoneNumber);
        productDAO.addUser(newUser);

        // Display a success message or perform any additional actions
        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();

        // Clear the EditText fields
        usernameEditText.setText("");
        passwordEditText.setText("");
        phoneEditText.setText("");
    }

        private boolean isPasswordValid(String password) {
        // Check if the password has at least one uppercase letter, one lowercase letter, and is at least 8 characters long
        return password.matches("^(?=.*[a-z])(?=.*[A-Z]).{8,}$");
        }
        private boolean isUsernameTaken(String username) {
            // Query the database to check if the username exists
            // Return true if the username is already taken, false otherwise
            return productDAO.isUsernameTaken(username);
        }

    // Update the isValidUser method to include checking against the existing users
        private boolean isValidUser(String username, String password) {
        // Check if the username and password match any subscribed user in the system
        // and return true if they do
            return productDAO.isValidUser(username, password) && isSubscribed(username);
        }

    // Method to check if the user is subscribed based on the username
        private boolean isSubscribed(String username) {
        // Query the database to check if the user is subscribed
        // Return true if the user is subscribed, false otherwise
        // You need to implement this method based on your database structure
        // For example, you may check if the user exists in the User table
        // and if the user's subscription status is active
            return productDAO.isUserSubscribed(username);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        productDAO.close(); // Close the database connection when the activity is destroyed
    }
}
