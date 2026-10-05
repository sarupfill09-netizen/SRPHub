package com.srpteam.srphub

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {

    private val blue = Color.rgb(22, 119, 242)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogin()
    }

    private fun base(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(dp(28), dp(28), dp(28), dp(24))
        setBackgroundColor(Color.WHITE)
    }

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
        if (bold) {
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun field(
        hintText: String,
        password: Boolean = false
    ): EditText = EditText(this).apply {
        hint = hintText
        textSize = 15f
        singleLine = true
        setPadding(dp(14), 0, dp(14), 0)
        backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                Color.rgb(201, 212, 229)
            )

        if (password) {
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun showLogin() {
        val root = base()

        root.addView(
            text("✦", 58f, blue, true),
            LinearLayout.LayoutParams(
                -1,
                dp(70)
            )
        )

        root.addView(
            text("SRP Hub", 30f, Color.rgb(20, 33, 61), true),
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            )
        )

        root.addView(
            text(
                "One Hub. Everything Connected.",
                13f,
                Color.GRAY
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(40)
            )
        )

        val spacer = Space(this)
        root.addView(
            spacer,
            LinearLayout.LayoutParams(
                1,
                dp(28)
            )
        )

        root.addView(
            text(
                "Welcome Back",
                23f,
                Color.rgb(23, 35, 59),
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        root.addView(
            text(
                "Sign in to continue to SRP Hub",
                14f,
                Color.GRAY
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        val name = field("Full Name")
        root.addView(
            name,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val email = field("Email Address")
        email.inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        root.addView(
            email,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val password = field("Password", true)
        root.addView(
            password,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val login = Button(this).apply {
            text = "Login"
            textSize = 15f
            setTextColor(Color.WHITE)
            setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(blue)
            )
        }

        root.addView(
            login,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(12)
            }
        )

        val note = text(
            "Create Account",
            14f,
            blue,
            true
        )

        root.addView(
            note,
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        note.setOnClickListener {
            Toast.makeText(
                this,
                "Account registration will be added in a later step.",
                Toast.LENGTH_LONG
            ).show()
        }

        login.setOnClickListener {
            if (
                name.text.isNullOrBlank() ||
                email.text.isNullOrBlank() ||
                password.text.isNullOrBlank()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Login service is not connected yet.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }
}
