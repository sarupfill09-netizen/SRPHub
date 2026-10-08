package com.srpteam.srphub

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*
import java.security.MessageDigest

class MainActivity : Activity() {

    private val blue = Color.rgb(25, 118, 242)
    private val dark = Color.rgb(20, 43, 82)
    private val gray = Color.rgb(105, 116, 132)
    private val lightBorder = Color.rgb(220, 226, 235)

    private val prefs by lazy {
        getSharedPreferences("srp_hub_account", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        // সেশন চেক
        if (prefs.getBoolean("logged_in", false)) {
            Toast.makeText(this, "Logged in! (Home Screen step is next)", Toast.LENGTH_SHORT).show()
            // আপাতত সেশন থাকলেও লগইন স্ক্রিন দেখাবে টেস্ট করার সুবিধার জন্য
            showLogin()
        } else {
            showLogin()
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

    private fun baseLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(26), dp(24), dp(26), dp(24))
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun textView(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun logo(): ImageView {
        return ImageView(this).apply {
            setImageResource(R.drawable.srp_hub_logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }
    }

    private fun field(
        hintText: String,
        password: Boolean = false
    ): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 14f
            setSingleLine(true)
            setTextColor(dark)
            setHintTextColor(Color.rgb(145, 154, 168))
            setPadding(dp(16), 0, dp(if (password) 48 else 16), 0)

            background = roundedBackground(Color.WHITE, 10, lightBorder)

            if (password) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0)
                compoundDrawablePadding = dp(8)

                setOnTouchListener { view, event ->
                    if (event.action == MotionEvent.ACTION_UP && event.x > width - dp(55)) {
                        val editText = view as EditText
                        val isPassword = editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

                        if (isPassword) {
                            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        editText.setSelection(editText.text.length)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    private fun blueButton(title: String): Button {
        return Button(this).apply {
            text = title
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            isAllCaps = false
            background = roundedBackground(blue, 9)
            stateListAnimator = null
            setPadding(dp(8), 0, dp(8), 0)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun addGap(root: LinearLayout, height: Int) {
        root.addView(Space(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    // 1. LOGIN SCREEN
    private fun showLogin() {
        val root = baseLayout()
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            isFillViewport = true
        }

        root.addView(logo(), LinearLayout.LayoutParams(dp(92), dp(92)))
        root.addView(textView("SRP Hub", 28f, dark, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42)))
        root.addView(textView("One Hub. Everything Connected.", 12.5f, gray), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

        addGap(root, 18)

        root.addView(textView("Welcome Back", 22f, dark, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(38)))
        root.addView(textView("Sign in to continue to SRP Hub", 12.5f, gray), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

        addGap(root, 8)

        val name = field("Full Name")
        if (prefs.contains("name")) name.setText(prefs.getString("name", ""))
        root.addView(name, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(8) })

        val email = field("Email Address").apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        if (prefs.contains("email")) email.setText(prefs.getString("email", ""))
        root.addView(email, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(10) })

        val password = field("Password", true)
        root.addView(password, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(10) })

        val login = blueButton("LOGIN")
        root.addView(login, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(14) })

        val createAccount = textView("Create Account", 13f, blue, true)
        root.addView(createAccount, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(4) })

        createAccount.setOnClickListener { showCreateAccount() }

        login.setOnClickListener {
            val savedName = prefs.getString("name", "")
            val savedEmail = prefs.getString("email", "")
            val savedPassword = prefs.getString("password_hash", "")

            val enteredName = name.text.toString().trim()
            val enteredEmail = email.text.toString().trim()
            val enteredPassword = password.text.toString()

            if (enteredName.isEmpty() || enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (savedEmail.isNullOrEmpty()) {
                Toast.makeText(this, "No account found. Please create an account first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (enteredName != savedName || enteredEmail != savedEmail || hashPassword(enteredPassword) != savedPassword) {
                Toast.makeText(this, "Incorrect account details.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            prefs.edit().putBoolean("logged_in", true).apply()
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
        }

        scroll.addView(root)
        setContentView(scroll)
    }

    // 2. CREATE ACCOUNT SCREEN
    private fun showCreateAccount() {
        val root = baseLayout()
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            isFillViewport = true
        }

        root.addView(logo(), LinearLayout.LayoutParams(dp(88), dp(88)))
        root.addView(textView("SRP Hub", 25f, dark, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42)))
        root.addView(textView("Create Account", 22f, dark, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(35)))
        root.addView(textView("Create your SRP Hub account", 12.5f, gray), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

        addGap(root, 14)

        val name = field("Full Name")
        root.addView(name, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)))

        val email = field("Email Address").apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        root.addView(email, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(10) })

        val password = field("Password", true)
        root.addView(password, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(10) })

        val confirmPassword = field("Confirm Password", true)
        root.addView(confirmPassword, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(10) })

        val create = blueButton("CREATE ACCOUNT")
        root.addView(create, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)).apply { topMargin = dp(16) })

        val back = textView("Back to Login", 13f, blue, true)
        root.addView(back, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)))

        back.setOnClickListener { showLogin() }

        create.setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val confirmText = confirmPassword.text.toString()

            if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!emailText.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("name", nameText)
                .putString("email", emailText)
                .putString("password_hash", hashPassword(passwordText))
                .putBoolean("logged_in", false)
                .apply()

            Toast.makeText(this, "Account created successfully.", Toast.LENGTH_LONG).show()
            showLogin()
        }

        scroll.addView(root)
        setContentView(scroll)
    }
}
