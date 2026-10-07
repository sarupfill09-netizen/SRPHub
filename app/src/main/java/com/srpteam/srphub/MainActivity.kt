package com.srpteam.srphub

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.graphics.drawable.GradientDrawable
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

        if (prefs.getBoolean("logged_in", false)) {
            showHome()
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
            setPadding(
                dp(26),
                dp(24),
                dp(26),
                dp(24)
            )
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

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
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

            setPadding(
                dp(16),
                0,
                dp(if (password) 48 else 16),
                0
            )

            background = roundedBackground(
                Color.WHITE,
                10,
                lightBorder
            )

            if (password) {

                inputType =
                    InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    android.R.drawable.ic_menu_view,
                    0
                )

                compoundDrawablePadding = dp(8)

                setOnTouchListener { view, event ->

                    if (
                        event.action == MotionEvent.ACTION_UP &&
                        event.x >
                        width - dp(55)
                    ) {

                        val editText = view as EditText

                        val isPassword =
                            editText.inputType ==
                            (
                                InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                            )

                        if (isPassword) {

                            editText.inputType =
                                InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                        } else {

                            editText.inputType =
                                InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }

                        editText.setSelection(
                            editText.text.length
                        )

                        true

                    } else {
                        false
                    }
                }
            }
        }
    }

    private fun blueButton(
        title: String
    ): Button {

        return Button(this).apply {

            text = title
            textSize = 14f

            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            isAllCaps = false

            background = roundedBackground(
                blue,
                9
            )

            stateListAnimator = null

            setPadding(
                dp(8),
                0,
                dp(8),
                0
            )
        }
    }

    private fun hashPassword(
        password: String
    ): String {

        val bytes =
            MessageDigest
                .getInstance("SHA-256")
                .digest(
                    password.toByteArray()
                )

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private fun addGap(
        root: LinearLayout,
        height: Int
    ) {
        root.addView(
            Space(this),
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }

    private fun showLogin() {

        val root = baseLayout()

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            isFillViewport = true
        }

        // LOGO
        root.addView(
            logo(),
            LinearLayout.LayoutParams(
                dp(92),
                dp(92)
            )
        )

        // BRAND NAME
        root.addView(
            textView(
                "SRP Hub",
                28f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(42)
            )
        )

        // TAGLINE
        root.addView(
            textView(
                "One Hub. Everything Connected.",
                12.5f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(30)
            )
        )

        addGap(root, 18)

        // WELCOME
        root.addView(
            textView(
                "Welcome Back",
                22f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(38)
            )
        )

        // SUBTITLE
        root.addView(
            textView(
                "Sign in to continue to SRP Hub",
                12.5f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(30)
            )
        )

        addGap(root, 8)

        // NAME
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
                dp(50)
            ).apply {
                topMargin = dp(8)
            }
        )

        // EMAIL
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
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        // PASSWORD
        val password = field(
            "Password",
            true
        )

        root.addView(
            password,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        // LOGIN BUTTON
        val login = blueButton("LOGIN")

        root.addView(
            login,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(14)
            }
        )

        // CREATE ACCOUNT
        val createAccount =
            textView(
                "Create Account",
                13f,
                blue,
                true
            )

        root.addView(
            createAccount,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(4)
            }
        )

        createAccount.setOnClickListener {
            showCreateAccount()
        }

        // LOGIN ACTION
        login.setOnClickListener {

            val savedName =
                prefs.getString("name", "")

            val savedEmail =
                prefs.getString("email", "")

            val savedPassword =
                prefs.getString(
                    "password_hash",
                    ""
                )

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

            if (
                savedEmail.isNullOrEmpty()
            ) {

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
                hashPassword(
                    enteredPassword
                ) != savedPassword
            ) {

                Toast.makeText(
                    this,
                    "Incorrect account details.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            prefs.edit()
                .putBoolean(
                    "logged_in",
                    true
                )
                .apply()

            showHome()
        }

        scroll.addView(root)

        setContentView(scroll)
    }

    private fun showCreateAccount() {

        val root = baseLayout()

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            isFillViewport = true
        }

        // LOGO
        root.addView(
            logo(),
            LinearLayout.LayoutParams(
                dp(88),
                dp(88)
            )
        )

        // TITLE
        root.addView(
            textView(
                "Create Account",
                25f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(42)
            )
        )

        root.addView(
            textView(
                "Create your SRP Hub account",
                12.5f,
                gray
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(30)
            )
        )

        addGap(root, 14)

        // NAME
        val name = field("Full Name")

        root.addView(
            name,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            )
        )

        // EMAIL
        val email = field("Email Address")

        email.inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        root.addView(
            email,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        // PASSWORD
        val password =
            field("Password", true)

        root.addView(
            password,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        // CONFIRM PASSWORD
        val confirmPassword =
            field(
                "Confirm Password",
                true
            )

        root.addView(
            confirmPassword,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        // CREATE
        val create =
            blueButton("CREATE ACCOUNT")

        root.addView(
            create,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(16)
            }
        )

        // BACK
        val back =
            textView(
                "Back to Login",
                13f,
                blue,
                true
            )

        root.addView(
            back,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
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

            if (
                passwordText != confirmText
            ) {

                Toast.makeText(
                    this,
                    "Passwords do not match.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            prefs.edit()
                .putString(
                    "name",
                    nameText
                )
                .putString(
                    "email",
                    emailText
                )
                .putString(
                    "password_hash",
                    hashPassword(
                        passwordText
                    )
                )
                .putBoolean(
                    "logged_in",
                    false
                )
                .apply()

            Toast.makeText(
                this,
                "Account created successfully.",
                Toast.LENGTH_LONG
            ).show()

            showLogin()
        }

        scroll.addView(root)

        setContentView(scroll)
    } 

    private fun showHome() {

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(90)
            )
            setBackgroundColor(Color.WHITE)
        }

        // =========================
        // TOP HEADER
        // =========================

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logoSmall = ImageView(this).apply {
            setImageResource(R.drawable.srp_hub_logo)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        header.addView(
            logoSmall,
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            )
        )

        val brandBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brandName = textView(
            "SRP Hub",
            18f,
            dark,
            true
        )

        brandBox.addView(
            brandName,
            LinearLayout.LayoutParams(
                -1,
                dp(23)
            )
        )

        val tagline = textView(
            "One Hub. Everything Connected.",
            8.5f,
            gray
        )

        brandBox.addView(
            tagline,
            LinearLayout.LayoutParams(
                -1,
                dp(17)
            )
        )

        header.addView(
            brandBox,
            LinearLayout.LayoutParams(
                0,
                dp(42),
                1f
            ).apply {
                leftMargin = dp(8)
            }
        )

        val profile = TextView(this).apply {
            text = "S"
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            background = roundedBackground(
                dark,
                50
            )
        }

        header.addView(
            profile,
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            )
        )

        profile.setOnClickListener {

            Toast.makeText(
                this@MainActivity,
                "Profile",
                Toast.LENGTH_SHORT
            ).show()
        }

        root.addView(
            header,
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        // =========================
        // SEARCH BAR
        // =========================

        val searchBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                dp(14),
                0,
                dp(14),
                0
            )
            background = roundedBackground(
                Color.rgb(247, 249, 252),
                14
            )
        }

        val searchIcon = TextView(this).apply {
            text = "⌕"
            textSize = 25f
            setTextColor(gray)
            gravity = Gravity.CENTER
        }

        searchBox.addView(
            searchIcon,
            LinearLayout.LayoutParams(
                dp(32),
                dp(50)
            )
        )

        val search = EditText(this).apply {
            hint = "Search apps..."
            textSize = 14f
            setSingleLine(true)
            setTextColor(dark)
            setHintTextColor(
                Color.rgb(145, 154, 168)
            )
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(
                dp(4),
                0,
                0,
                0
            )
        }

        searchBox.addView(
            search,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            )
        )

        root.addView(
            searchBox,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(16)
            }
        )

        // =========================
        // SECTION TITLE
        // =========================

        root.addView(
            textView(
                "All Apps",
                21f,
                dark,
                true
            ),
            LinearLayout.LayoutParams(
                -1,
                dp(35)
            ).apply {
                topMargin = dp(20)
            }
        )

        // =========================
        // APP GRID
        // =========================

        val grid = GridLayout(this).apply {
            columnCount = 4
            useDefaultMargins = false
        }

        data class AppItem(
            val name: String,
            val shortName: String,
            val iconColor: Int
        )

        val apps = listOf(

            AppItem(
                "Instagram",
                "IG",
                Color.rgb(225, 48, 108)
            ),

            AppItem(
                "Facebook",
                "f",
                Color.rgb(24, 119, 242)
            ),

            AppItem(
                "YouTube",
                "▶",
                Color.rgb(255, 0, 0)
            ),

            AppItem(
                "WhatsApp",
                "WA",
                Color.rgb(37, 211, 102)
            ),

            AppItem(
                "TikTok",
                "♪",
                Color.BLACK
            ),

            AppItem(
                "Fiverr",
                "fi",
                Color.rgb(29, 191, 115)
            ),

            AppItem(
                "LinkedIn",
                "in",
                Color.rgb(10, 102, 194)
            ),

            AppItem(
                "Upwork",
                "Up",
                Color.rgb(20, 168, 0)
            ),

            AppItem(
                "Messenger",
                "M",
                Color.rgb(0, 132, 255)
            ),

            AppItem(
                "Telegram",
                "TG",
                Color.rgb(42, 171, 238)
            ),

            AppItem(
                "Discord",
                "DC",
                Color.rgb(88, 101, 242)
            )
        )

        for (app in apps) {

            val card = LinearLayout(this).apply {

                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                setPadding(
                    dp(4),
                    dp(8),
                    dp(4),
                    dp(8)
                )

                background = roundedBackground(
                    Color.rgb(248, 250, 253),
                    16
                )
            }

            val icon = TextView(this).apply {

                text = app.shortName
                textSize =
                    if (app.shortName.length <= 2)
                        15f
                    else
                        13f

                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)

                background =
                    roundedBackground(
                        app.iconColor,
                        13
                    )
            }

            card.addView(
                icon,
                LinearLayout.LayoutParams(
                    dp(48),
                    dp(48)
                )
            )

            val name = textView(
                app.name,
                10f,
                dark,
                true
            )

            card.addView(
                name,
                LinearLayout.LayoutParams(
                    -1,
                    dp(28)
                ).apply {
                    topMargin = dp(5)
                }
            )

            card.setOnClickListener {

                Toast.makeText(
                    this@MainActivity,
                    "${app.name} will open inside SRP Hub.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val params =
                GridLayout.LayoutParams().apply {

                    width = 0
                    height = dp(105)

                    columnSpec =
                        GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                        )

                    setMargins(
                        dp(4),
                        dp(5),
                        dp(4),
                        dp(5)
                    )
                }

            grid.addView(
                card,
                params
            )
        }

        root.addView(
            grid,
            LinearLayout.LayoutParams(
                -1,
                dp(300)
            )
        )

        // =========================
        // MORE APPS BANNER
        // =========================

        val banner = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(16),
                dp(12),
                dp(12),
                dp(12)
            )

            background = roundedBackground(
                Color.rgb(55, 82, 210),
                18
            )
        }

        val bannerText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val bannerTitle = textView(
            "More Apps Coming Soon",
            15f,
            Color.WHITE,
            true
        )

        bannerTitle.gravity = Gravity.LEFT

        bannerText.addView(
            bannerTitle,
            LinearLayout.LayoutParams(
                -1,
                dp(25)
            )
        )

        val bannerSub = textView(
            "We're working on adding more\npopular services for you.",
            10f,
            Color.WHITE
        )

        bannerSub.gravity = Gravity.LEFT

        bannerText.addView(
            bannerSub,
            LinearLayout.LayoutParams(
                -1,
                dp(35)
            )
        )

        banner.addView(
            bannerText,
            LinearLayout.LayoutParams(
                0,
                dp(62),
                1f
            )
        )

        val arrow = TextView(this).apply {
            text = "›"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }

        banner.addView(
            arrow,
            LinearLayout.LayoutParams(
                dp(40),
                dp(50)
            )
        )

        root.addView(
    banner,
    LinearLayout.LayoutParams(
        -1,
        dp(86)
    ).apply {
        topMargin = dp(14)
    }
)

scroll.addView(root)

// =========================
// BOTTOM NAVIGATION
// =========================

        val bottom = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            setBackgroundColor(Color.WHITE)

            elevation = dp(8).toFloat()
        }

        val homeTab = TextView(this).apply {
            text = "⌂\nHome"
            textSize = 11f
            setTextColor(blue)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }

        val appsTab = TextView(this).apply {
            text = "▦\nApps"
            textSize = 11f
            setTextColor(gray)
            gravity = Gravity.CENTER
        }

        val profileTab = TextView(this).apply {
            text = "●\nProfile"
            textSize = 11f
            setTextColor(gray)
            gravity = Gravity.CENTER
        }

        bottom.addView(
            homeTab,
            LinearLayout.LayoutParams(
                0,
                dp(62),
                1f
            )
        )

        bottom.addView(
            appsTab,
            LinearLayout.LayoutParams(
                0,
                dp(62),
                1f
            )
        )

        bottom.addView(
            profileTab,
            LinearLayout.LayoutParams(
                0,
                dp(62),
                1f
            )
        )

        // =========================
        // FINAL LAYOUT
        // =========================

        val main = FrameLayout(this)

        main.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        main.addView(
            bottom,
            FrameLayout.LayoutParams(
                -1,
                dp(62),
                Gravity.BOTTOM
            )
        )

        setContentView(main)
    }
    }
