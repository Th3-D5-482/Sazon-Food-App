package com.example.codex01.Actvities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codex01.Adapters.CartAdapter;
import com.example.codex01.Domain.Foods;
import com.example.codex01.Helpers.ChangeNumberItemsListener;
import com.example.codex01.Helpers.ManagmentCart;
import com.example.codex01.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends BaseActivity  {
private ActivityCartBinding binding;
private RecyclerView.Adapter adapter;
private ManagmentCart managmentCart;
private double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        binding.edAddress.setText("Cupertino CA, 95014");
        setVariable();
        calculateCart();
        initList();
    }

    private void initList() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        if (!userEmail.isEmpty()) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmail.replace(".", "_"));
            ArrayList<Foods> cartList = new ArrayList<>();

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            cartList.add(issue.getValue(Foods.class));
                        }
                        if (!cartList.isEmpty()) {
                            binding.emptyTxt.setVisibility(View.GONE);
                            binding.scrollviewCart.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.cardView.setLayoutManager(linearLayoutManager);
                            adapter = new CartAdapter(cartList, CartActivity.this, new ChangeNumberItemsListener() {
                                @Override
                                public void change() {
                                    calculateCart();
                                }
                            });
                            binding.cardView.setAdapter(adapter);
                        } else {
                            binding.emptyTxt.setVisibility(View.VISIBLE);
                            binding.scrollviewCart.setVisibility(View.GONE);
                        }
                    } else {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                        binding.scrollviewCart.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
            String phoneNumber = sharedPreferences.getString("phonenumber", "");
            binding.edPhoneNumber.setText(phoneNumber);
        } else {
            Toast.makeText(CartActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void calculateCart() {
        double percentTax = 0.1;
        double delivery = 10;

        // Get the currently logged-in user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        if (!userEmail.isEmpty()) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmail.replace(".", "_"));
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double totalCartPrice = 0.0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Double totalPrice = snapshot.child("TotalPrice").getValue(Double.class);
                        if (totalPrice != null) {
                            totalCartPrice += totalPrice;
                        }
                    }
                    double tax = Math.round(totalCartPrice * percentTax * 100.0) / 100;
                    double total = Math.round((totalCartPrice + tax + delivery) * 100) / 100;
                    double itemTotal = Math.round(totalCartPrice * 100) / 100;
                    binding.totalFeeTxt.setText("$" + itemTotal);
                    binding.taxTxt.setText("$" + tax);
                    binding.deliveryTxt.setText("$" + delivery);
                    binding.totalTxt.setText("$" + total);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle Errors
                }
            });
        } else {
            // Handle case where user is not logged in
            Toast.makeText(CartActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void setVariable()
    {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,MainActivity.class));
            }
        });
        
        binding.placeOdrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = binding.edAddress.getText().toString().trim();
                String phonenumber = binding.edPhoneNumber.getText().toString().trim();
                String delivery = binding.deliveryTxt.getText().toString().trim();
                String taxtxt = binding.taxTxt.getText().toString().trim();
                String totaltxt = binding.totalTxt.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(CartActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                } else if (phonenumber.length() != 10) {
                    Toast.makeText(CartActivity.this, "Phone Number must be 10 digits", Toast.LENGTH_SHORT).show();
                } else if (adapter == null || adapter.getItemCount() == 0) {
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the currently logged-in user's email from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    String userEmail = sharedPreferences.getString("email", "");
                    if (!userEmail.isEmpty()) {
                        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmail.replace(".", "_"));
                        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userEmail.replace(".", "_")).push();

                        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                double subtotal = 0.0;
                                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                    String title = itemSnapshot.child("Title").getValue(String.class);
                                    double price = itemSnapshot.child("Price").getValue(Double.class);
                                    int numberInCart = itemSnapshot.child("numberInCart").getValue(Integer.class);
                                    double totalPrice = itemSnapshot.child("TotalPrice").getValue(Double.class);

                                    // Store the item in Orders table
                                    DatabaseReference itemRef = ordersRef.child("Items").push();
                                    itemRef.child("Title").setValue(title);
                                    itemRef.child("Price").setValue(price);
                                    itemRef.child("NumberInCart").setValue(numberInCart);
                                    itemRef.child("TotalPrice").setValue(totalPrice);

                                    // Calculate subtotal
                                    subtotal += totalPrice;
                                }

                                // Store order details
                                ordersRef.child("Address").setValue(address);
                                ordersRef.child("Phone Number").setValue(phonenumber);
                                ordersRef.child("SubTotal").setValue(subtotal);
                                ordersRef.child("Delivery").setValue(delivery);
                                ordersRef.child("Total Tax").setValue(taxtxt);
                                ordersRef.child("Total").setValue(totaltxt);
                                // Add other order details like delivery fee, tax, total, etc.

                                // Remove the entire Cart table
                                cartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CartActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(CartActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(CartActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CartActivity.this, "Failed to retrieve cart items", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Handle case where user is not logged in
                        Toast.makeText(CartActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.edPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10) {
                    binding.edPhoneNumber.setText(s.subSequence(0, 10));
                    binding.edPhoneNumber.setSelection(10);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }
}
