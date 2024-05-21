package com.example.customer_management;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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
    private List<Customer> allCustomers; // Μεταβλητή για να κρατάει όλους τους πελάτες

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

        TextView textViewName = findViewById(R.id.textViewName1);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        Button buttonAddCustomer = findViewById(R.id.buttonAddCustomer);
        SearchView searchView = findViewById(R.id.searchView);
        customersListView = findViewById(R.id.customersListView);

        // Αρχικοποίηση της λίστας όλων των πελατών
        allCustomers = database.customerDao().getAllCustomersAlphabetically();

        // Αρχικοποίηση του adapter και ρύθμιση στη λίστα
        customerAdapter = new CustomerAdapter(this, allCustomers);
        customersListView.setAdapter(customerAdapter);

        buttonAddCustomer.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                // Προσθήκη πελάτη στη βάση δεδομένων
                Customer customer = new Customer(name, phone);
                database.customerDao().insert(customer);

                Toast.makeText(MainActivity.this, "Customer added", Toast.LENGTH_SHORT).show();

                // Καθαρισμός των πεδίων εισαγωγής
                editTextName.setText("");
                editTextPhone.setText("");

                // Ανανεώνει τη λίστα όλων των πελατών και του adapter
                allCustomers = database.customerDao().getAllCustomersAlphabetically();
                customerAdapter = new CustomerAdapter(MainActivity.this, allCustomers);
                customersListView.setAdapter(customerAdapter);

            } else {
                Toast.makeText(MainActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCustomers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCustomers(newText);
                return false;
            }
        });
    }

    private void filterCustomers(String query) {
        query = query.toLowerCase().trim();
        List<Customer> filteredCustomers = new ArrayList<>();
        for (Customer customer : allCustomers) {
            if (customer.getName().toLowerCase().contains(query) ||
                    customer.getPhoneNumber().toLowerCase().contains(query)) {
                filteredCustomers.add(customer);
            }
        }
        customerAdapter = new CustomerAdapter(this, filteredCustomers);
        customersListView.setAdapter(customerAdapter);
    }
}
