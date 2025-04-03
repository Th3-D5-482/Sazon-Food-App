package com.example.myapplication.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.example.myapplication.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    var binding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setvariable()
        window.statusBarColor = Color.parseColor("#FFE4B5")
    }

    private fun setvariable() {
        binding!!.loginBtn.setOnClickListener {
            startActivity(Intent(this@IntroActivity,LoginActivity::class.java))
        }
        binding!!.SignUpBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@IntroActivity,
                    SignUpActivity::class.java
                )
            )
        }
    }
    override fun onBackPressed()
    {
        super.onBackPressed()
        finishAffinity()
    }
}
