package com.example.myapplication.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : BaseActivity() {
    var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding?.Edemailaddress?.setText("davesquare482@gmail.com")
        binding?.Edpassword?.setText("12345678")
        setVariable()
    }

    private fun setVariable() {
        binding!!.loginBtn.setOnClickListener { view ->
            val email = binding!!.Edemailaddress.text.toString()
            val password = binding!!.Edpassword.text.toString()
            if (!isValidEmail(email)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(
                    this@LoginActivity,
                    "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!email.isEmpty() && !password.isEmpty()) {
                val authRef = FirebaseDatabase.getInstance().getReference("Authentication")
                authRef.orderByChild("EmailID").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (snapshot in dataSnapshot.children) {
                                    val dbPassword = snapshot.child("Password").getValue(String::class.java)
                                    val name = snapshot.child("Name").getValue(String::class.java)
                                    val phoneNumber = snapshot.child("Phone Number").getValue(String::class.java)
                                    if (dbPassword == password) {
                                        val sharedPreferences = getSharedPreferences(
                                            "UserInfo",
                                            Context.MODE_PRIVATE
                                        )
                                        val editor = sharedPreferences.edit()
                                        editor.putString("name", name)
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.putString("phonenumber", phoneNumber)
                                        editor.apply()
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        return
                                    }
                                }
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Password incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Account doesn't exist, please SignUp",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle Errors
                        }
                    })
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please Enter the Email and Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding!!.linkSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
        }
    }
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
