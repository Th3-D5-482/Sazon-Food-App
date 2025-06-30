package com.example.codex01.Actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.codex01.Adapters.CategoryAdapter;
import com.example.codex01.Adapters.FoodListAdapter;
import com.example.codex01.Domain.Category;
import com.example.codex01.Domain.Foods;
import com.example.codex01.R;
import com.example.codex01.databinding.ActivityListFoodsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchActivity extends BaseActivity {
    ActivityListFoodsBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int category;
    private String searchText,categoryName;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        initList(category);
    }

    private void initList(int categoryId)
    {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;
        if (isSearch)
        {
            query = myRef.orderByChild("Title").startAt(searchText).endAt(searchText+'\uf8ff');
        }
        else
        {
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressBar.setVisibility(View.GONE);
                if (snapshot.exists())
                {
                    for (DataSnapshot issue: snapshot.getChildren())
                    {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0)
                    {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(SearchActivity.this,2));
                        adapterListFood = new FoodListAdapter(list);
                        binding.foodListView.setAdapter(adapterListFood);
                    }
                    else
                    {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getIntentExtra() {
        category = getIntent().getIntExtra("Category",0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        binding.titleText.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}