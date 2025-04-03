package com.example.myapplication.Helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myapplication.Domain.Foods
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ManagmentCart(private val context: Context) {
    private val tinyDB: TinyDB = TinyDB(context)
    fun insertFood(item: Foods) {
        val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        if (!userEmail!!.isEmpty()) {
            val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(
                userEmail!!.replace(".", "_")
            )
            cartRef.orderByChild("Title").equalTo(item.Title)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.getChildren()) {
                                val key = snapshot.key!!
                            }
                        } else {
                            val newCartRef = cartRef.push()
                            newCartRef.child("Title").setValue(item.Title)
                            newCartRef.child("Price").setValue(item.Price)
                            newCartRef.child("ImagePath").setValue(item.ImagePath)
                            newCartRef.child("numberInCart").setValue(item.numberInCart)
                            newCartRef.child("TotalPrice").setValue(item.numberInCart * item.Price)
                            newCartRef.child("Description").setValue(item.Description)
                            Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //Handle Errors
                    }
                })
        } else {
            // Handle case where user is not logged in
            Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun getListCart(): ArrayList<Foods> {
        return tinyDB.getListObject("CartList") ?: ArrayList()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (i in listItem.indices) {
            fee += listItem[i].Price * listItem[i].numberInCart
        }
        return fee
    }

    fun minusNumberItem(
        listItem: ArrayList<Foods?>,
        position: Int,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        val currentNumberInCart = listItem[position]!!.numberInCart
        if (currentNumberInCart == 1) {
            val removedItem = listItem[position]
            listItem.removeAt(position)
            if (removedItem != null) {
                deleteCartItemFromFirebase(removedItem, changeNumberItemsListener)
            }
        } else {
            listItem[position]!!.numberInCart = currentNumberInCart - 1
            listItem[position]?.let { updateCartItemInFirebase(it, changeNumberItemsListener) }
        }
    }

    private fun updateCartItemInFirebase(
        item: Foods,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("Cart").child(userEmail!!.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(item.Title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val key = snapshot.key!!
                            val updatedNumberInCart = item.numberInCart
                            val updatedTotalPrice = updatedNumberInCart * item.Price
                            cartRef.child(key).child("numberInCart").setValue(updatedNumberInCart)
                            cartRef.child(key).child("TotalPrice").setValue(updatedTotalPrice)
                                .addOnSuccessListener { changeNumberItemsListener.change() }
                                .addOnFailureListener {
                                    // Handle Failure
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Handle Failure;
                }
            })
    }

    private fun deleteCartItemFromFirebase(
        item: Foods,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("Cart").child(userEmail!!.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(item.Title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val key = snapshot.key!!
                            cartRef.child(key).removeValue()
                                .addOnSuccessListener { changeNumberItemsListener.change() }
                                .addOnFailureListener {
                                    // Handle failure
                                }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error checking cart", databaseError.toException())
                }
            })
    }

    fun plusNumberItem(
        listItem: ArrayList<Foods?>,
        position: Int,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        listItem[position]!!.numberInCart = listItem[position]!!.numberInCart + 1
        val newTotalPrice = listItem[position]!!.numberInCart * listItem[position]!!.Price
        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("Cart").child(userEmail!!.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(listItem[position]!!.Title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val key = snapshot.key!!
                            cartRef.child(key).child("numberInCart")
                                .setValue(listItem[position]!!.numberInCart)
                            cartRef.child(key).child("TotalPrice").setValue(newTotalPrice)
                                .addOnSuccessListener { changeNumberItemsListener.change() }
                                .addOnFailureListener {
                                    // Handle failure
                                }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    //Log.e("Firebase", "Error checking cart", databaseError.toException());
                }
            })
    }

    fun clearCart() {
        tinyDB.remove("CartList")
    }
}
