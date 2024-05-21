package com.example.customer_management;

import androidx.room.*;

import java.util.List;

@Dao
public interface CustomerDao {
    @Insert
    void insert(Customer customer);

    @Query("SELECT * FROM customers ORDER BY name ASC")
    List<Customer> getAllCustomersAlphabetically();
}
