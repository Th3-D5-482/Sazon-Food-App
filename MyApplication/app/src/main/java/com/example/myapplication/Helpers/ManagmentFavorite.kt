package com.example.myapplication.Helpers

import android.content.Context
import android.widget.Toast
import com.example.myapplication.Domain.Foods
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ManagmentFavorite(private val context: Context) {
    private val tinyDB: TinyDB

    init {
        tinyDB = TinyDB(context)
    }


    fun insertFavorite(item: Foods) {
        // Get the currently logged-in user's email from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        if (!userEmail!!.isEmpty()) {
            val favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(
                userEmail!!.replace(".", "_")
            )
            favoritesRef.orderByChild("Title").equalTo(item.Title)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.getChildren()) {
                                val key = snapshot.key!!
                            }
                        } else {
                            val newFavoriteRef = favoritesRef.push()
                            newFavoriteRef.child("Title").setValue(item.Title)
                            newFavoriteRef.child("TimeValue").setValue(item.TimeValue)
                            newFavoriteRef.child("Star").setValue(item.Star)
                            newFavoriteRef.child("Price").setValue(item.Price)
                            newFavoriteRef.child("ImagePath").setValue(item.ImagePath)
                            newFavoriteRef.child("Description").setValue(item.Description)
                            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
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

    fun getListFavorite(): ArrayList<Foods> {
        return tinyDB.getListObject("FavoriteList") ?: ArrayList()
    }

    fun clearFavorite() {
        tinyDB.remove("FavoriteList")
    }
}
