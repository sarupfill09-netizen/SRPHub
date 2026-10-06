package com.srpteam.srphub

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*

import java.security.MessageDigest

class MainActivity : Activity() {

    private val blue = Color.rgb(22, 119, 242)
    private val dark = Color.rgb(20, 33, 61)
    private val gray = Color.GRAY

    private val prefs by lazy {
        getSharedPreferences("srp_hub_account", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (prefs.getBoolean("logged_in", false)) {
            showHome()
        } else {
            showLogin()
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun base(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(
                dp(28),
                dp(28),
                dp(28),
                dp(24)
            )
            setBackgroundColor(Color.WHITE)
        }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView =
        TextView(this).apply {
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
    ): EditText =
        EditText(this).apply {
            hint = hintText
            textSize = 15f
            setSingleLine(true)

            setPadding(
                dp(14),
                0,
                dp(14),
                0
            )

            backgroundTintList =
                ColorStateList.valueOf(
                    Color.rgb(201, 212, 229)
                )

            if (password) {
                inputType =
                    InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

    private fun button(
        title: String
    ): Button =
        Button(this).apply {
            text = title
            textSize = 15f
            setTextColor(Color.WHITE)

            setBackgroundTintList(
                ColorStateList.valueOf(blue)
            )
        }

    private fun hashPassword(password: String): String {
        val bytes =
            MessageDigest
                .getInstance("SHA-256")
                .digest(password.toByteArray())

        return bytes.joinToString("") {
            "%02x".format(it)
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
            text(
                "SRP Hub",
                30f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            )
        )

        root.addView(
            text(
                "One Hub. Everything Connected.",
                13f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(40)
            )
        )

        root.addView(
            Space(this),
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
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        val name = field("Full Name")

        if (prefs.contains("name")) {
            name.setText(
                prefs.getString("name", "")
            )
        }

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

        if (prefs.contains("email")) {
            email.setText(
                prefs.getString("email", "")
            )
        }

        root.addView(
            email,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val password =
            field("Password", true)

        root.addView(
            password,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val login =
            button("LOGIN")

        root.addView(
            login,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(12)
            }
        )

        val createAccount =
            text(
                "Create Account",
                14f,
                blue,
                true
            )

        root.addView(
            createAccount,
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        createAccount.setOnClickListener {
            showCreateAccount()
        }

        login.setOnClickListener {

            val savedName =
                prefs.getString("name", "")

            val savedEmail =
                prefs.getString("email", "")

            val savedPassword =
                prefs.getString("password_hash", "")

            val enteredName =
                name.text.toString().trim()

            val enteredEmail =
                email.text.toString().trim()

            val enteredPassword =
                password.text.toString()

            if (
                enteredName.isEmpty() ||
                enteredEmail.isEmpty() ||
                enteredPassword.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (savedEmail == null || savedEmail.isEmpty()) {

                Toast.makeText(
                    this,
                    "No account found. Please create an account first.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (
                enteredName != savedName ||
                enteredEmail != savedEmail ||
                hashPassword(enteredPassword) != savedPassword
            ) {

                Toast.makeText(
                    this,
                    "Incorrect account details.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            prefs.edit()
                .putBoolean("logged_in", true)
                .apply()

            showHome()
        }

        val scroll =
            ScrollView(this)

        scroll.addView(root)

        setContentView(scroll)
    }

    private fun showCreateAccount() {

        val root = base()

        root.addView(
            text("✦", 58f, blue, true),
            LinearLayout.LayoutParams(
                -1,
                dp(70)
            )
        )

        root.addView(
            text(
                "Create Account",
                28f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(55)
            )
        )

        root.addView(
            text(
                "Create your SRP Hub account",
                14f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(45)
            )
        )

        val name =
            field("Full Name")

        root.addView(
            name,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(15)
            }
        )

        val email =
            field("Email Address")

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

        val password =
            field("Password", true)

        root.addView(
            password,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val confirmPassword =
            field(
                "Confirm Password",
                true
            )

        root.addView(
            confirmPassword,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        val create =
            button("CREATE ACCOUNT")

        root.addView(
            create,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(18)
            }
        )

        val back =
            text(
                "Back to Login",
                14f,
                blue,
                true
            )

        root.addView(
            back,
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        back.setOnClickListener {
            showLogin()
        }

        create.setOnClickListener {

            val nameText =
                name.text.toString().trim()

            val emailText =
                email.text.toString().trim()

            val passwordText =
                password.text.toString()

            val confirmText =
                confirmPassword.text.toString()

            if (
                nameText.isEmpty() ||
                emailText.isEmpty() ||
                passwordText.isEmpty() ||
                confirmText.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!emailText.contains("@")) {
                Toast.makeText(
                    this,
                    "Please enter a valid email address.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                Toast.makeText(
                    this,
                    "Passwords do not match.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            prefs.edit()
                .putString("name", nameText)
                .putString("email", emailText)
                .putString(
                    "password_hash",
                    hashPassword(passwordText)
                )
                .putBoolean("logged_in", false)
                .apply()

            Toast.makeText(
                this,
                "Account created successfully.",
                Toast.LENGTH_LONG
            ).show()

            showLogin()
        }

        val scroll =
            ScrollView(this)

        scroll.addView(root)

        setContentView(scroll)
    }

    private fun showHome() {

        val root = base()

        root.addView(
            text("✦", 58f, blue, true),
            LinearLayout.LayoutParams(
                -1,
                dp(80)
            )
        )

        root.addView(
            text(
                "SRP Hub",
                30f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(55)
            )
        )

        val name =
            prefs.getString(
                "name",
                "User"
            )

        root.addView(
            text(
                "Welcome, $name!",
                22f,
                Color.rgb(23, 35, 59),
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(55)
            )
        )

        root.addView(
            text(
                "Your SRP Hub account is active.",
                14f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            )
        )

        val logout =
            button("LOGOUT")

        root.addView(
            logout,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            ).apply {
                topMargin = dp(25)
            }
        )

        logout.setOnClickListener {

            prefs.edit()
                .putBoolean("logged_in", false)
                .apply()

            showLogin()
        }

        val scroll =
            ScrollView(this)

        scroll.addView(root)

        setContentView(scroll)
    }
}
