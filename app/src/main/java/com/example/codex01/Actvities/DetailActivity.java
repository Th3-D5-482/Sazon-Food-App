package com.example.codex01.Actvities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codex01.Adapters.FavoriteAdapter;
import com.example.codex01.Domain.Foods;
import com.example.codex01.Helpers.ManagmentCart;
import com.example.codex01.Helpers.ManagmentFavorite;
import com.example.codex01.R;
import com.example.codex01.databinding.ActivityDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends BaseActivity {
ActivityDetailBinding binding;
private int num = 1;
private ManagmentCart managmentCart;
private ManagmentFavorite managmentFavorite;
private FavoriteAdapter adapter;
private  Context context;
    private Foods object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentFavorite = new ManagmentFavorite(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        getIntentExtra();
        setVaraible();
    }
    private void setVaraible()
    {
        managmentCart = new ManagmentCart(this);
        binding.backBtn.setOnClickListener(v -> finish());
        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);
        binding.priceTxt.setText("$"+object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar()+" Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num*object.getPrice()+" $"));
        binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = num+1;
                binding.numTxt.setText(num+" ");
                binding.totalTxt.setText("$"+(num*object.getPrice()));
            }
        });
        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num > 1)
                {
                    num = num-1;
                    binding.numTxt.setText(num+" ");
                    binding.totalTxt.setText("$"+(num*object.getPrice()));
                }
            }
        });
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberInCart(num);
                managmentCart.insertFood(object);
                //startActivity(new Intent(DetailActivity.this,CartActivity.class));
            }
        });
        binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //object.setNumberInFavorite(num);
                managmentFavorite.insertFavorite(object);
            }
        });
    }
    private void getIntentExtra()
    {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}