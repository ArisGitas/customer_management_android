package com.example.customer_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomerAdapter extends ArrayAdapter<Customer> {
    public CustomerAdapter(Context context, List<Customer> customers) {
        super(context, 0, customers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Customer customer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_customer, parent, false);
        }
        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView phoneTextView = convertView.findViewById(R.id.textViewPhone);

        assert customer != null;
        nameTextView.setText(customer.getName());
        phoneTextView.setText(customer.getPhoneNumber());

        return convertView;
    }
}
