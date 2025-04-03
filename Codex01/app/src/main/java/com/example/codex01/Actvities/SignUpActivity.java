package com.example.codex01.Actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.codex01.R;
import com.example.codex01.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SignUpActivity extends BaseActivity {
    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.Edname.setText("Dave");
        binding.Edemailaddress.setText("davesquare482@gmail.com");
        binding.Edpassword.setText("12345678");
        binding.EdphoneNumber.setText("0123456789");
        setvariable();
        setupInputFilters();
    }

  private void setvariable() {
      
      binding.SignUpBtn.setOnClickListener(v -> {
          String name = binding.Edname.getText().toString();
          String email = binding.Edemailaddress.getText().toString();
          String password = binding.Edpassword.getText().toString();
          String phonenumber = binding.EdphoneNumber.getText().toString();
          if (!isValidEmail(email)) {
              Toast.makeText(SignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
              return;
          }
          if (password.length() < 6) {
              Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
              return;
          }
          if (phonenumber.length() != 10)
          {
              Toast.makeText(SignUpActivity.this, "Phone Number must be 10 digits", Toast.LENGTH_SHORT).show();
              return;
          }
          if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phonenumber.isEmpty()) {
              DatabaseReference authRef = FirebaseDatabase.getInstance().getReference("Authentication");
              Query emailQuery = authRef.orderByChild("EmailID").equalTo(email);
              Query phoneQuery = authRef.orderByChild("Phone Number").equalTo(phonenumber);
              ValueEventListener emailListener = new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot.exists()) {
                          Toast.makeText(SignUpActivity.this, "Account already exists, please Login", Toast.LENGTH_SHORT).show();
                      } else {
                          phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                  if (dataSnapshot.exists()) {
                                      Toast.makeText(SignUpActivity.this, "Account already exists, please Login", Toast.LENGTH_SHORT).show();
                                  } else {
                                      DatabaseReference newUserRef = authRef.push();
                                      newUserRef.child("Name").setValue(name);
                                      newUserRef.child("EmailID").setValue(email);
                                      newUserRef.child("Password").setValue(password);
                                      newUserRef.child("Phone Number").setValue(phonenumber);
                                      Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                      startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                      SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                      SharedPreferences.Editor editor = sharedPreferences.edit();
                                      editor.putString("name", name);
                                      editor.putString("email", email);
                                      editor.putString("password", password);
                                      editor.putString("phonenumber", phonenumber);
                                      editor.apply();
                                  }
                              }
                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {
                                  // Handle Errors
                              }
                          });
                      }
                  }
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                      // Handle Errors
                  }
              };

              emailQuery.addListenerForSingleValueEvent(emailListener);
          } else {
              Toast.makeText(SignUpActivity.this, "Please Enter the Credentials", Toast.LENGTH_SHORT).show();
          }

      });
      binding.linkLogin.setOnClickListener(v -> {
          startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
      });
      binding.EdphoneNumber.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              // Do nothing
          }
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.length() > 10) {
                  binding.EdphoneNumber.setText(s.subSequence(0, 10));
                  binding.EdphoneNumber.setSelection(10);
              }
          }
          @Override
          public void afterTextChanged(Editable s) {
            // Do nothing
          }
      });
  }
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
        binding.EdphoneNumber.setFilters(new InputFilter[]{phoneFilter});
    }
}
