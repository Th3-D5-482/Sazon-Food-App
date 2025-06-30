package com.example.codex01.Actvities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codex01.R;
import com.example.codex01.databinding.ActivityProfileBinding;
import com.example.codex01.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
        initdata();
        setupInputFilters();
    }
    private void initdata()
    {
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("name");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String phonenumber = intent.getStringExtra("phonenumber");
            binding.Edname.setText(username);
            binding.Edemailaddress.setText(email);
            binding.Edpassword.setText(password);
            binding.Edphonenumber.setText(phonenumber);
        }
    }
    private void setVariable()
    {
        binding.UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = binding.Edname.getText().toString().trim();
                final String phonenumber = binding.Edphonenumber.getText().toString().trim();
                final String email = binding.Edemailaddress.getText().toString().trim();
                final String password = binding.Edpassword.getText().toString().trim();
                if (password.length() < 6) {
                    Toast.makeText(ProfileActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phonenumber.length() != 10) {
                    Toast.makeText(ProfileActivity.this, "Phone Number must be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!name.isEmpty() && !phonenumber.isEmpty() && !password.isEmpty()) {
                    DatabaseReference authRef = FirebaseDatabase.getInstance().getReference("Authentication");
                    authRef.orderByChild("EmailID").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String userKey = snapshot.getKey(); // Get the unique key of the user
                                    DatabaseReference userRef = authRef.child(userKey);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String existingName = dataSnapshot.child("Name").getValue(String.class);
                                            String existingPhoneNumber = dataSnapshot.child("Phone Number").getValue(String.class);
                                            String existingPassword = dataSnapshot.child("Password").getValue(String.class);
                                            if (name.equals(existingName) && phonenumber.equals(existingPhoneNumber) && password.equals(existingPassword)) {
                                                Toast.makeText(ProfileActivity.this, "Information already up-to-date", Toast.LENGTH_SHORT).show();
                                            } else {
                                                userRef.child("Name").setValue(name);
                                                userRef.child("Phone Number").setValue(phonenumber);
                                                userRef.child("Password").setValue(password);
                                                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("name", name);
                                                editor.putString("email", email);
                                                editor.putString("password", password);
                                                editor.putString("phonenumber", phonenumber);
                                                editor.apply();
                                                Toast.makeText(ProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle Errors
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle Errors
                        }
                    });
                }
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            }
        });
        binding.Edphonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 10) {
                    binding.Edphonenumber.setText(s.subSequence(0, 10));
                    binding.Edphonenumber.setSelection(10);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }
    private void setupInputFilters() {
        InputFilter nameFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter phoneFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        binding.Edname.setFilters(new InputFilter[]{nameFilter});
        binding.Edphonenumber.setFilters(new InputFilter[]{phoneFilter});
    }
}