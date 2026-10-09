package com.srpteam.srphub

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.security.MessageDigest

class ProfileActivity : Activity() {

    private val blue = Color.rgb(25, 118, 242)
    private val red = Color.rgb(220, 38, 38)

    private var isDarkMode = false
    private var bgColor = Color.WHITE
    private var textColor = Color.rgb(20, 43, 82)
    private var subTextColor = Color.rgb(105, 116, 132)
    private var cardBgColor = Color.rgb(243, 245, 249)
    private var lightBorder = Color.rgb(220, 226, 235)

    private val prefs by lazy {
        getSharedPreferences("srp_hub_account", MODE_PRIVATE)
    }

    private fun updateThemeColors() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            bgColor = Color.rgb(18, 18, 18)
            textColor = Color.rgb(240, 240, 240)
            subTextColor = Color.rgb(170, 170, 170)
            cardBgColor = Color.rgb(30, 30, 30)
            lightBorder = Color.rgb(50, 50, 50)
            window.statusBarColor = Color.rgb(18, 18, 18)
            window.navigationBarColor = Color.rgb(18, 18, 18)
        } else {
            bgColor = Color.WHITE
            textColor = Color.rgb(20, 43, 82)
            subTextColor = Color.rgb(105, 116, 132)
            cardBgColor = Color.rgb(243, 245, 249)
            lightBorder = Color.rgb(220, 226, 235)
            window.statusBarColor = Color.WHITE
            window.navigationBarColor = Color.WHITE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateThemeColors()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(dp(20), dp(16), dp(20), dp(24))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
        }

        // Top Bar Header
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val backBtn = TextView(this).apply {
            text = "←"
            textSize = 22f
            setTextColor(textColor)
            setPadding(0, 0, dp(16), 0)
            setOnClickListener { finish() }
        }
        topBar.addView(backBtn)

        val titleView = TextView(this).apply {
            text = "Profile"
            textSize = 20f
            setTextColor(textColor)
            setTypeface(null, Typeface.BOLD)
        }
        topBar.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        root.addView(topBar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 24)

        // User Avatar Circle
        val avatar = TextView(this).apply {
            text = "👤"
            textSize = 42f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(41, 121, 255), 50)
            setTextColor(Color.WHITE)
        }
        root.addView(avatar, LinearLayout.LayoutParams(dp(88), dp(88)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })

        addGap(root, 14)

        val userName = prefs.getString("name", "User Name") ?: "User Name"
        val userEmail = prefs.getString("email", "user@email.com") ?: "user@email.com"

        val nameText = TextView(this).apply {
            text = userName
            textSize = 21f
            setTextColor(textColor)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        root.addView(nameText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val emailText = TextView(this).apply {
            text = userEmail
            textSize = 13.5f
            setTextColor(subTextColor)
            gravity = Gravity.CENTER
        }
        root.addView(emailText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(3)
        })

        addGap(root, 28)

        // Options Group Container
        val cardGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(cardBgColor, 16, lightBorder)
        }

        // 1. SRP Hub Account
        val item1 = createOptionItem(
            iconText = "🛡️",
            title = "SRP Hub Account",
            subtitle = "User ID: #SH-001234"
        ) {
            Toast.makeText(this, "User ID: #SH-001234", Toast.LENGTH_SHORT).show()
        }

        // 2. Account Details
        val item2 = createOptionItem(
            iconText = "👤",
            title = "Account Details",
            subtitle = "View your information"
        ) {
            showAccountDetailsDialog(userName, userEmail)
        }

        // 3. Security
        val item3 = createOptionItem(
            iconText = "🛡️",
            title = "Security",
            subtitle = "Change password"
        ) {
            showChangePasswordDialog()
        }

        // 4. Help & Support
        val item4 = createOptionItem(
            iconText = "❓",
            title = "Help & Support",
            subtitle = "Get help"
        ) {
            showHelpSupportDialog()
        }

        cardGroup.addView(item1)
        cardGroup.addView(createDivider())
        cardGroup.addView(item2)
        cardGroup.addView(createDivider())
        cardGroup.addView(item3)
        cardGroup.addView(createDivider())
        cardGroup.addView(item4)

        root.addView(cardGroup, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 36)

        // Logout Button
        val btnLogout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = roundedBackground(Color.TRANSPARENT, 12, Color.rgb(200, 210, 225))
            setPadding(dp(16), dp(12), dp(16), dp(12))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                prefs.edit().putBoolean("logged_in", false).apply()
                val intent = Intent(this@ProfileActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }

        val logoutIcon = TextView(this).apply {
            text = "↪"
            textSize = 18f
            setTextColor(red)
            setTypeface(null, Typeface.BOLD)
        }
        btnLogout.addView(logoutIcon)

        val logoutText = TextView(this).apply {
            text = "LOGOUT"
            textSize = 14f
            setTextColor(red)
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        btnLogout.addView(logoutText)

        root.addView(btnLogout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(48)))

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun createOptionItem(
        iconText: String,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }

            val iconBox = TextView(context).apply {
                text = iconText
                textSize = 18f
                gravity = Gravity.CENTER
            }
            addView(iconBox, LinearLayout.LayoutParams(dp(36), dp(36)))

            val textBox = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, dp(8), 0)
            }

            val titleTv = TextView(context).apply {
                text = title
                textSize = 15f
                setTextColor(textColor)
                setTypeface(null, Typeface.BOLD)
            }
            textBox.addView(titleTv)

            val subTv = TextView(context).apply {
                text = subtitle
                textSize = 12f
                setTextColor(subTextColor)
            }
            textBox.addView(subTv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

            addView(textBox, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

            val arrowTv = TextView(context).apply {
                text = "›"
                textSize = 20f
                setTextColor(subTextColor)
            }
            addView(arrowTv)
        }
    }

    private fun createDivider(): View {
        return View(this).apply {
            setBackgroundColor(lightBorder)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)).apply {
                leftMargin = dp(16)
                rightMargin = dp(16)
            }
        }
    }

    private fun showAccountDetailsDialog(name: String, email: String) {
        AlertDialog.Builder(this)
            .setTitle("Account Details")
            .setMessage("Full Name: $name\nEmail: $email\nAccount Status: Active\nApp Version: 1.0.0")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), dp(10))
        }

        val input = EditText(this).apply {
            hint = "Enter New Password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val newPass = input.text.toString()
                if (newPass.length >= 6) {
                    val hashed = MessageDigest.getInstance("SHA-256")
                        .digest(newPass.toByteArray())
                        .joinToString("") { "%02x".format(it) }

                    prefs.edit().putString("password_hash", hashed).apply()
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showHelpSupportDialog() {
        AlertDialog.Builder(this)
            .setTitle("Help & Support")
            .setMessage("SRP Hub Support\n\nFor any query or support, feel free to contact SRP TEAM.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun roundedBackground(
        color: Int,
        radius: Int,
        strokeColor: Int? = null
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (strokeColor != null) {
                setStroke(dp(1), strokeColor)
            }
        }
    }

    private fun addGap(root: LinearLayout, height: Int) {
        root.addView(Space(this), LinearLayout.LayoutParams(1, dp(height)))
    }
}
