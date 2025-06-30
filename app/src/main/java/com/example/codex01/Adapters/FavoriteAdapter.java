package com.example.codex01.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.codex01.Actvities.DetailActivity;
import com.example.codex01.Actvities.FavoriteActivity;
import com.example.codex01.Domain.Foods;
import com.example.codex01.Helpers.ChangeNumberItemsListener;
import com.example.codex01.Helpers.ManagmentCart;
import com.example.codex01.Helpers.ManagmentFavorite;
import com.example.codex01.Helpers.TinyDB;
import com.example.codex01.R;
import com.example.codex01.databinding.ActivityFavoriteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.viewholder> {
    ArrayList<Foods> list;
    private int getItemCount;
    private ManagmentFavorite managmentFavorite;
    Context context;

    public FavoriteAdapter(ArrayList<Foods> list, FavoriteActivity context) {
        this.list = list;
        this.context = context;
        this.managmentFavorite = new ManagmentFavorite(context);
    }
    
    @NonNull
    @Override
    public FavoriteAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_favorite,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.viewholder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.timeTxt.setText(list.get(position).getTimeValue()+" min");
        holder.rateTxt.setText(" "+list.get(position).getStar());
        holder.priceTxt.setText("$"+list.get(position).getPrice());
        Glide.with(context)
                .load(list.get(position).getImagePath())
                .transform(new CenterCrop(),new RoundedCorners(30))
                .into(holder.pic);
        int pos = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object", list.get(holder.getAdapterPosition()));
                context.startActivities(new Intent[]{intent});
            }
        });
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Foods removedItem = list.get(pos);
                list.remove(pos);
                notifyItemRemoved(pos);
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                String userEmail = sharedPreferences.getString("email", "");
                if (!userEmail.isEmpty()) {
                    DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userEmail.replace(".", "_"));
                    favoritesRef.orderByChild("Title").equalTo(removedItem.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String key = snapshot.getKey();
                                    assert key != null;
                                    favoritesRef.child(key).removeValue();
                                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Handle Errors
                        }
                    });
                } else {
                    Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show();
                }

                if (pos >= getItemCount()) {
                    pos = getItemCount() - 1;
                }
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title,timeTxt,rateTxt,priceTxt;
        Button removeBtn;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
