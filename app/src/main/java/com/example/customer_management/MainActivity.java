package com.example.customer_management;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView customersListView;
    private EditText editTextName;
    private EditText editTextPhone;
    private AppDatabase database;
    private CustomerAdapter customerAdapter;
    private List<Customer> allCustomers;
    private List<Customer> filteredCustomers;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "customers")
                .allowMainThreadQueries() // Not recommended for production
                .build();

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        Button buttonAddCustomer = findViewById(R.id.buttonAddCustomer);
        SearchView searchView = findViewById(R.id.searchView);
        ListView customersListView = findViewById(R.id.customersListView);

        allCustomers = new ArrayList<>();
        customerAdapter = new CustomerAdapter(this, allCustomers);
        customersListView.setAdapter(customerAdapter);
        allCustomers = database.customerDao().getAllCustomersAlphabetically();
        filteredCustomers = new ArrayList<>(allCustomers);
        customerAdapter = new CustomerAdapter(this, filteredCustomers);
        customersListView.setAdapter(customerAdapter);

        displayCustomers();

        buttonAddCustomer.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                // Insert customer into database
                Customer customer = new Customer(name, phone);
                database.customerDao().insert(customer);

                Toast.makeText(MainActivity.this, "Customer added", Toast.LENGTH_SHORT).show();

                // Clear the input fields
                editTextName.setText("");
                editTextPhone.setText("");
                displayCustomers();
            } else {
                Toast.makeText(MainActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCustomers(newText);
                return true;
            }
        });
    }

    private void displayCustomers() {
        allCustomers.clear();
        allCustomers.addAll(database.customerDao().getAllCustomersAlphabetically());
        customerAdapter.notifyDataSetChanged();
    }

    private void filterCustomers(String query) {
        filteredCustomers.clear();
        if (query.isEmpty()) {
            filteredCustomers.addAll(allCustomers);
        } else {
            for (Customer customer : allCustomers) {
                if (customer.getName().toLowerCase().contains(query.toLowerCase()) ||
                        customer.getPhoneNumber().toLowerCase().contains(query.toLowerCase())) {
                    filteredCustomers.add(customer);
                }
            }
        }
        customerAdapter.notifyDataSetChanged();
    }
}