package com.example.codex01.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.codex01.Actvities.CartActivity;
import com.example.codex01.Adapters.FavoriteAdapter;
import com.example.codex01.Domain.Foods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ManagmentFavorite {
    private Context context;
    private TinyDB tinyDB;
    private DatabaseReference databaseReference;
    public ManagmentFavorite(Context context) {
        this.context = context;
        this.tinyDB=new TinyDB(context);
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("FavoriteList");
    }

    public void insertFavorite(Foods item) {
        // Get the currently logged-in user's email from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        if (!userEmail.isEmpty()) {
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userEmail.replace(".", "_"));
            favoritesRef.orderByChild("Title").equalTo(item.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            assert key != null;
                        }
                    } else {
                        DatabaseReference newFavoriteRef = favoritesRef.push();
                        newFavoriteRef.child("Title").setValue(item.getTitle());
                        newFavoriteRef.child("TimeValue").setValue(item.getTimeValue());
                        newFavoriteRef.child("Star").setValue(item.getStar());
                        newFavoriteRef.child("Price").setValue(item.getPrice());
                        newFavoriteRef.child("ImagePath").setValue(item.getImagePath());
                        newFavoriteRef.child("Description").setValue(item.getDescription());
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Handle Errors
                }
            });
        } else {
            // Handle case where user is not logged in
            Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Foods> getListFavorite() {
        return tinyDB.getListObject("FavoriteList");
    }

    public void clearFavorite()
    {
        tinyDB.remove("FavoriteList");
    }

}
