package com.srpteam.srphub

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView

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
            textSize = 40f
            gravity = Gravity.CENTER
            background = roundedBackground(cardBgColor, 50)
        }
        root.addView(avatar, LinearLayout.LayoutParams(dp(88), dp(88)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })

        addGap(root, 16)

        val userName = prefs.getString("name", "User Name") ?: "User Name"
        val userEmail = prefs.getString("email", "user@email.com") ?: "user@email.com"

        val nameText = TextView(this).apply {
            text = userName
            textSize = 22f
            setTextColor(textColor)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        root.addView(nameText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val emailText = TextView(this).apply {
            text = userEmail
            textSize = 14f
            setTextColor(subTextColor)
            gravity = Gravity.CENTER
        }
        root.addView(emailText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        })

        addGap(root, 32)

        // Account Details Card
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(18))
            background = roundedBackground(cardBgColor, 16, lightBorder)
        }

        infoCard.addView(createDetailRow("Account Status", "Active"))
        infoCard.addView(createDetailRow("App Version", "1.0.0"))

        root.addView(infoCard, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 28)

        // Logout Button
        val btnLogout = Button(this).apply {
            text = "LOGOUT"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            isAllCaps = false
            background = roundedBackground(red, 10)
            stateListAnimator = null
        }

        btnLogout.setOnClickListener {
            prefs.edit().putBoolean("logged_in", false).apply()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        root.addView(btnLogout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)))

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun createDetailRow(label: String, value: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(8), 0, dp(8))

            val labelTv = TextView(context).apply {
                text = label
                textSize = 14f
                setTextColor(subTextColor)
            }
            addView(labelTv, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

            val valueTv = TextView(context).apply {
                text = value
                textSize = 14f
                setTextColor(textColor)
                setTypeface(null, Typeface.BOLD)
            }
            addView(valueTv)
        }
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
