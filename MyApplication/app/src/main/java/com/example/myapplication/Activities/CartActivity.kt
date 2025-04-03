package com.example.myapplication.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.CartAdapter
import com.example.myapplication.Domain.Foods
import com.example.myapplication.Helpers.ChangeNumberItemsListener
import com.example.myapplication.Helpers.ManagmentCart
import com.example.myapplication.databinding.ActivityCartBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private var adapter: RecyclerView.Adapter<CartAdapter.ViewHolder>? = null
    private lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentCart = ManagmentCart(this)
        binding.edAddress.setText("Cupertino CA, 95014");
        setVariable()
        calculateCart()
        initList()
    }
    
    private fun initList() {
        val sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        if (!userEmail!!.isEmpty()) {
            val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(
                userEmail!!.replace(".", "_")
            )
            val cartList = ArrayList<Foods?>()
            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (issue in dataSnapshot.getChildren()) {
                            cartList.add(issue.getValue(Foods::class.java))
                        }
                        if (!cartList.isEmpty()) {
                            binding.emptyTxt.visibility = View.GONE
                            binding.scrollviewCart.visibility = View.VISIBLE
                            val linearLayoutManager = LinearLayoutManager(
                                this@CartActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.cardView.setLayoutManager(linearLayoutManager)
                            adapter = CartAdapter(
                                cartList,
                                this@CartActivity,
                                object : ChangeNumberItemsListener {
                                    override fun change() {
                                        calculateCart()
                                    }
                                })
                            binding.cardView.setAdapter(adapter)
                        } else {
                            binding.emptyTxt.visibility = View.VISIBLE
                            binding.scrollviewCart.visibility = View.GONE
                        }
                    } else {
                        binding.emptyTxt.visibility = View.VISIBLE
                        binding.scrollviewCart.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
            val phoneNumber = sharedPreferences.getString("phonenumber", "")
            binding.edPhoneNumber.setText(phoneNumber)
        } else {
            Toast.makeText(this@CartActivity, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun calculateCart() {
        val percentTax = 0.1
        val delivery = 10.0

        // Get the currently logged-in user's email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        if (!userEmail!!.isEmpty()) {
            val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(
                userEmail!!.replace(".", "_")
            )
            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalCartPrice = 0.0
                    for (snapshot in dataSnapshot.getChildren()) {
                        val totalPrice = snapshot.child("TotalPrice").getValue(
                            Double::class.java
                        )
                        if (totalPrice != null) {
                            totalCartPrice += totalPrice
                        }
                    }
                    val tax = (Math.round(totalCartPrice * percentTax * 100.0) / 100).toDouble()
                    val total =
                        (Math.round((totalCartPrice + tax + delivery) * 100) / 100).toDouble()
                    val itemTotal = (Math.round(totalCartPrice * 100) / 100).toDouble()
                    binding.totalFeeTxt.text = "$$itemTotal"
                    binding.taxTxt.text = "$$tax"
                    binding.deliveryTxt.text = "$$delivery"
                    binding.totalTxt.text = "$$total"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle Errors
                }
            })
        } else {
            // Handle case where user is not logged in
            Toast.makeText(this@CartActivity, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@CartActivity,MainActivity::class.java))
        }
        
        binding.placeOdrBtn.setOnClickListener {
            val address = binding.edAddress.getText().toString().trim()
            val phonenumber = binding.edPhoneNumber.getText().toString().trim()
            val delivery = binding.deliveryTxt.text.toString().trim()
            val taxtxt = binding.taxTxt.text.toString().trim()
            val totaltxt = binding.totalTxt.text.toString().trim()
            if (TextUtils.isEmpty(address)) {
                Toast.makeText(this@CartActivity, "Please enter your address", Toast.LENGTH_SHORT)
                    .show()
            } else if (phonenumber.length != 10) {
                Toast.makeText(
                    this@CartActivity,
                    "Phone Number must be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (adapter == null || adapter!!.itemCount == 0) {
                Toast.makeText(this@CartActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                // Get the currently logged-in user's email from SharedPreferences
                val sharedPreferences =
                    getSharedPreferences("UserInfo", MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("email", "")
                if (!userEmail!!.isEmpty()) {
                    val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(
                        userEmail!!.replace(".", "_")
                    )
                    val ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(
                        userEmail!!.replace(".", "_")
                    ).push()
                    cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var subtotal = 0.0
                            for (itemSnapshot in dataSnapshot.getChildren()) {
                                val title = itemSnapshot.child("Title").getValue(
                                    String::class.java
                                )
                                val price =
                                    itemSnapshot.child("Price").getValue(
                                        Double::class.java
                                    )!!
                                val numberInCart =
                                    itemSnapshot.child("numberInCart").getValue(
                                        Int::class.java
                                    )!!
                                val totalPrice =
                                    itemSnapshot.child("TotalPrice").getValue(
                                        Double::class.java
                                    )!!

                                // Store the item in Orders table
                                val itemRef = ordersRef.child("Items").push()
                                itemRef.child("Title").setValue(title)
                                itemRef.child("Price").setValue(price)
                                itemRef.child("NumberInCart").setValue(numberInCart)
                                itemRef.child("TotalPrice").setValue(totalPrice)
                                // Calculate subtotal
                                subtotal += totalPrice
                            }

                            // Store order details
                            ordersRef.child("Address").setValue(address)
                            ordersRef.child("Phone Number").setValue(phonenumber)
                            ordersRef.child("SubTotal").setValue(subtotal)
                            ordersRef.child("Delivery").setValue(delivery)
                            ordersRef.child("Total Tax").setValue(taxtxt)
                            ordersRef.child("Total").setValue(totaltxt)
                            // Add other order details like delivery fee, tax, total, etc.

                            // Remove the entire Cart table
                            cartRef.removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Order Placed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@CartActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Failed to place order",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(
                                this@CartActivity,
                                "Failed to retrieve cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    // Handle case where user is not logged in
                    Toast.makeText(this@CartActivity, "User is not logged in", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.edPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 10) {
                    binding.edPhoneNumber.setText(s.subSequence(0, 10))
                    binding.edPhoneNumber.setSelection(10)
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })
    }
}
