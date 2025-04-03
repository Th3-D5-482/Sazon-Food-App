package com.example.codex01.Actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.codex01.Adapters.FavoriteAdapter;
import com.example.codex01.Domain.Foods;
import com.example.codex01.Helpers.ManagmentCart;
import com.example.codex01.Helpers.ManagmentFavorite;
import com.example.codex01.R;
import com.example.codex01.databinding.ActivityCartBinding;
import com.example.codex01.databinding.ActivityFavoriteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private ActivityFavoriteBinding binding;
    private ManagmentFavorite managmentFavorite;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentFavorite = new ManagmentFavorite(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        setvariable();
        initList();
    }
    
    private void initList() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        if (!userEmail.isEmpty()) {
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userEmail.replace(".", "_"));
            //binding.progressBarFavorites.setVisibility(View.VISIBLE);
            ArrayList<Foods> favoriteList = new ArrayList<>();
            favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            favoriteList.add(issue.getValue(Foods.class));
                        }
                        if (!favoriteList.isEmpty()) {
                            binding.emptyTxt.setVisibility(View.GONE);
                            binding.scrollviewCart.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FavoriteActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.CardView.setLayoutManager(linearLayoutManager);
                            adapter = new FavoriteAdapter(favoriteList, FavoriteActivity.this);
                            binding.CardView.setAdapter(adapter);
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
                    //Log.e("Firebase", "Error fetching favorites", databaseError.toException());
                    //binding.progressBarFavorites.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(FavoriteActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void setvariable()
    {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavoriteActivity.this,MainActivity.class));
            }
        });
    }
}
