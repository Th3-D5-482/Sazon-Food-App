package com.example.codex01.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.codex01.Actvities.CartActivity;
import com.example.codex01.Domain.Foods;
import com.example.codex01.Helpers.TinyDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ManagmentCart {
    private Context context;
    private TinyDB tinyDB;
    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB=new TinyDB(context);
    }

    public void insertFood(Foods item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        if (!userEmail.isEmpty()) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmail.replace(".", "_"));
            cartRef.orderByChild("Title").equalTo(item.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            assert key != null;
                            //favoritesRef.child(key).child("numberInFavorite").setValue(item.getNumberInFavorite());
                        }
                    } else {
                        DatabaseReference newCartRef = cartRef.push();
                        newCartRef.child("Title").setValue(item.getTitle());
                        newCartRef.child("Price").setValue(item.getPrice());
                        newCartRef.child("ImagePath").setValue(item.getImagePath());
                        newCartRef.child("numberInCart").setValue(item.getNumberInCart());
                        newCartRef.child("TotalPrice").setValue(item.getNumberInCart() * item.getPrice());
                        newCartRef.child("Description").setValue(item.getDescription());
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
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

    public ArrayList<Foods> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public Double getTotalFee(){
        ArrayList<Foods> listItem=getListCart();
        double fee=0;
        for (int i = 0; i < listItem.size(); i++) {
            fee=fee+(listItem.get(i).getPrice()*listItem.get(i).getNumberInCart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<Foods> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        int currentNumberInCart = listItem.get(position).getNumberInCart();
        if (currentNumberInCart == 1) {
            Foods removedItem = listItem.get(position);
            listItem.remove(position);
            deleteCartItemFromFirebase(removedItem, changeNumberItemsListener);
        } else {
            listItem.get(position).setNumberInCart(currentNumberInCart - 1);
            updateCartItemInFirebase(listItem.get(position), changeNumberItemsListener);
        }
    }

    private void updateCartItemInFirebase(Foods item, ChangeNumberItemsListener changeNumberItemsListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cartRef = database.getReference("Cart").child(userEmail.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(item.getTitle())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                assert key != null;
                                int updatedNumberInCart = item.getNumberInCart();
                                double updatedTotalPrice = updatedNumberInCart * item.getPrice();
                                cartRef.child(key).child("numberInCart").setValue(updatedNumberInCart);
                                cartRef.child(key).child("TotalPrice").setValue(updatedTotalPrice)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                changeNumberItemsListener.change();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle Failure
                                            }
                                        });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Handle Failure;
                    }
                });
    }

    private void deleteCartItemFromFirebase(Foods item, ChangeNumberItemsListener changeNumberItemsListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cartRef = database.getReference("Cart").child(userEmail.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(item.getTitle())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                assert key != null;
                                cartRef.child(key).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                changeNumberItemsListener.change();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle failure
                                            }
                                        });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error checking cart", databaseError.toException());
                    }
                });
    }

    public void plusNumberItem(ArrayList<Foods> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        double newTotalPrice = listItem.get(position).getNumberInCart() * listItem.get(position).getPrice();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cartRef = database.getReference("Cart").child(userEmail.replace(".", "_"));
        cartRef.orderByChild("Title").equalTo(listItem.get(position).getTitle())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                assert key != null;
                                cartRef.child(key).child("numberInCart").setValue(listItem.get(position).getNumberInCart());
                                cartRef.child(key).child("TotalPrice").setValue(newTotalPrice)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                changeNumberItemsListener.change();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle failure
                                            }
                                        });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Log.e("Firebase", "Error checking cart", databaseError.toException());
                    }
                });
    }

    public void clearCart()
    {
        tinyDB.remove("CartList");
    }

}
