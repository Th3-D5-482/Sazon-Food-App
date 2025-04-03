package com.example.myapplication.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class SignUpActivity : BaseActivity() {
    var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.Edname.setText("Dave")
        binding!!.EdphoneNumber.setText("0123456789")
        binding!!.Edemailaddress.setText("davesquare482@gmail.com")
        binding!!.Edpassword.setText("12345678")
        setvariable()
        setupInputFilters()
    }

     private fun setvariable() {
         
        binding!!.SignUpBtn.setOnClickListener { v ->
            val name = binding!!.Edname.getText().toString()
            val email = binding!!.Edemailaddress.getText().toString()
            val password = binding!!.Edpassword.getText().toString()
            val phonenumber = binding!!.EdphoneNumber.getText().toString()
            if (!isValidEmail(email)) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (phonenumber.length != 10) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Phone Number must be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phonenumber.isEmpty()) {
                val authRef =
                    FirebaseDatabase.getInstance().getReference("Authentication")
                val emailQuery: Query = authRef.orderByChild("EmailID").equalTo(email)
                val phoneQuery: Query = authRef.orderByChild("Phone Number").equalTo(phonenumber)
                val emailListener: ValueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Account already exists, please Login",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            phoneQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Account already exists, please Login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val newUserRef = authRef.push()
                                        newUserRef.child("Name").setValue(name)
                                        newUserRef.child("EmailID").setValue(email)
                                        newUserRef.child("Password").setValue(password)
                                        newUserRef.child("Phone Number").setValue(phonenumber)
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "SignUp Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                        val sharedPreferences =
                                            getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                                        val editor =
                                            sharedPreferences.edit()
                                        editor.putString("name", name)
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.putString("phonenumber", phonenumber)
                                        editor.apply()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle Errors
                                }
                            })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle Errors
                    }
                }
                emailQuery.addListenerForSingleValueEvent(emailListener)
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please Enter the Credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding!!.linkLogin.setOnClickListener { v ->
            startActivity(
                Intent(
                    this@SignUpActivity,
                    LoginActivity::class.java
                )
            )
        }
    }
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    private fun setupInputFilters() {
        val nameFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetter(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        val phoneFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    if (!Character.isDigit(source[i])) {
                        return@InputFilter ""
                    }
                }
                null
            }
        binding!!.Edname.setFilters(arrayOf(nameFilter))
        binding!!.EdphoneNumber.setFilters(arrayOf(phoneFilter))
    }
}
