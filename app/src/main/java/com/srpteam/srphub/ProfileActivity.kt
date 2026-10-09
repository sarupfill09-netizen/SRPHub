package com.srpteam.srphub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class ProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout?.setOnClickListener {
            val prefs = getSharedPreferences("srp_hub_account", MODE_PRIVATE)
            prefs.edit().putBoolean("logged_in", false).apply()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
