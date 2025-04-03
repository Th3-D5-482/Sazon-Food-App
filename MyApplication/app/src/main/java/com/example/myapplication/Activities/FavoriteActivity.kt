package com.example.myapplication.Activities

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.FavoriteAdapter
import com.example.myapplication.Domain.Foods
import com.example.myapplication.Helpers.ManagmentFavorite
import com.example.myapplication.databinding.ActivityFavoriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavoriteActivity : AppCompatActivity() {
    private var binding: ActivityFavoriteBinding? = null
    private var managmentFavorite: ManagmentFavorite? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.getRoot())
        managmentFavorite = ManagmentFavorite(this)
        window.statusBarColor = resources.getColor(R.color.white)
        setvariable()
        initList()
    }

    private fun initList() {
        val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")
        if (!userEmail!!.isEmpty()) {
            val favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(
                userEmail!!.replace(".", "_")
            )
            //binding.progressBarFavorites.setVisibility(View.VISIBLE);
            val favoriteList = ArrayList<Foods?>()
            favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (issue in dataSnapshot.getChildren()) {
                            favoriteList.add(issue.getValue(Foods::class.java))
                        }
                        if (!favoriteList.isEmpty()) {
                            binding!!.emptyTxt.visibility = View.GONE
                            binding!!.scrollviewCart.visibility = View.VISIBLE
                            val linearLayoutManager = LinearLayoutManager(
                                this@FavoriteActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding!!.CardView.setLayoutManager(linearLayoutManager)
                            adapter = FavoriteAdapter(favoriteList, this@FavoriteActivity)
                            binding!!.CardView.setAdapter(adapter)
                        } else {
                            binding!!.emptyTxt.visibility = View.VISIBLE
                            binding!!.scrollviewCart.visibility = View.GONE
                        }
                    } else {
                        binding!!.emptyTxt.visibility = View.VISIBLE
                        binding!!.scrollviewCart.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //Log.e("Firebase", "Error fetching favorites", databaseError.toException());
                    //binding.progressBarFavorites.setVisibility(View.GONE);
                }
            })
        } else {
            Toast.makeText(this@FavoriteActivity, "User is not logged in", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setvariable() {
        binding?.backBtn?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@FavoriteActivity,
                    MainActivity::class.java
                )
            )
        })
    }
}
