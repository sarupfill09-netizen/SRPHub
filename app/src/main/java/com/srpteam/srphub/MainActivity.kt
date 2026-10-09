package com.srpteam.srphub

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import java.security.MessageDigest

class MainActivity : Activity() {

    private val blue = Color.rgb(25, 118, 242)
    
    // Dynamic theme colors
    private var isDarkMode = false
    private var bgColor = Color.WHITE
    private var textColor = Color.rgb(20, 43, 82)
    private var subTextColor = Color.rgb(105, 116, 132)
    private var cardBgColor = Color.rgb(243, 245, 249)
    private var lightBorder = Color.rgb(220, 226, 235)

    private var activeWebView: WebView? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val FILE_CHOOSER_REQUEST_CODE = 1001

    private var currentTab = "home"

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

        if (prefs.getBoolean("logged_in", false)) {
            showHome()
        } else {
            showLogin()
        }
    }

    override fun onBackPressed() {
        if (activeWebView != null && activeWebView!!.canGoBack()) {
            activeWebView!!.goBack()
        } else if (activeWebView != null) {
            activeWebView = null
            if (currentTab == "apps") showAppsPage() else showHome()
        } else if (currentTab == "apps") {
            showHome()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (filePathCallback != null) {
                val results: Array<Uri>? = if (resultCode == RESULT_OK && data != null) {
                    if (data.dataString != null) {
                        arrayOf(Uri.parse(data.dataString))
                    } else if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        val uris = ArrayList<Uri>()
                        for (i in 0 until count) {
                            uris.add(data.clipData!!.getItemAt(i).uri)
                        }
                        uris.toTypedArray()
                    } else null
                } else null

                filePathCallback?.onReceiveValue(results)
                filePathCallback = null
            }
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
            setBackgroundColor(bgColor)
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
            setTextColor(textColor)
            setHintTextColor(subTextColor)
            setPadding(dp(16), 0, dp(if (password) 48 else 16), 0)

            background = roundedBackground(cardBgColor, 10, lightBorder)

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

    private fun showLogin() {
        updateThemeColors()
        activeWebView = null
        val root = baseLayout()
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
        }

        root.addView(logo(), LinearLayout.LayoutParams(dp(92), dp(92)))
        root.addView(textView("SRP Hub", 28f, textColor, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42)))
        root.addView(textView("One Hub. Everything Connected.", 12.5f, subTextColor), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

        addGap(root, 18)

        root.addView(textView("Welcome Back", 22f, textColor, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(38)))
        root.addView(textView("Sign in to continue to SRP Hub", 12.5f, subTextColor), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

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
            showHome()
        }

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun showCreateAccount() {
        updateThemeColors()
        activeWebView = null
        val root = baseLayout()
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
        }

        root.addView(logo(), LinearLayout.LayoutParams(dp(88), dp(88)))
        root.addView(textView("SRP Hub", 25f, textColor, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42)))
        root.addView(textView("Create Account", 22f, textColor, true), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(35)))
        root.addView(textView("Create your SRP Hub account", 12.5f, subTextColor), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(30)))

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

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    data class AppData(
        val name: String,
        val subtitle: String,
        val iconText: String,
        val bgColor: Int,
        val url: String,
        val isDesktop: Boolean = false
    )

    private val allApps = listOf(
        AppData("Instagram", "Share your moments.", "📷", Color.rgb(225, 48, 108), "https://www.instagram.com/accounts/login/"),
        AppData("Facebook", "Connect with people.", "f", Color.rgb(24, 119, 242), "https://www.facebook.com/login/"),
        AppData("TikTok", "Short videos. Big moments.", "🎵", Color.BLACK, "https://www.tiktok.com/login"),
        AppData("X (Twitter)", "What's happening?", "𝕏", Color.BLACK, "https://x.com/i/flow/login"),
        AppData("Telegram", "Fast. Secure. Private.", "✈", Color.rgb(42, 171, 238), "https://web.telegram.org/"),
        AppData("Pinterest", "Discover ideas.", "📌", Color.rgb(230, 0, 35), "https://www.pinterest.com/login/"),
        AppData("Reddit", "Real people. Real discussions.", "🤖", Color.rgb(255, 69, 0), "https://www.reddit.com/login/"),
        AppData("YouTube", "Watch. Learn. Grow.", "▶", Color.rgb(255, 0, 0), "https://m.youtube.com"),
        AppData("Netflix", "Movies. Series. More.", "N", Color.rgb(229, 9, 20), "https://www.netflix.com/login"),
        AppData("Spotify", "Music for everyone.", "🎧", Color.rgb(30, 215, 96), "https://open.spotify.com/"),
        AppData("Fiverr", "Freelance services.", "fi", Color.rgb(29, 191, 115), "https://www.fiverr.com/login"),
        AppData("Upwork", "Find skilled talent.", "up", Color.rgb(20, 168, 0), "https://www.upwork.com/ab/account-security/login"),
        AppData("LinkedIn", "Build your professional network.", "in", Color.rgb(10, 102, 194), "https://www.linkedin.com/login"),
        AppData("Twitch", "Talk. Play. Build.", "👾", Color.rgb(145, 70, 255), "https://www.twitch.com/login"),
        AppData("WhatsApp", "Message without limits.", "💬", Color.rgb(37, 211, 102), "https://web.whatsapp.com/", true),
        AppData("Messenger", "Chat. Call. Connect.", "⚡", Color.rgb(0, 132, 255), "https://www.messenger.com/", true)
    )

    private fun showHome() {
        currentTab = "home"
        updateThemeColors()
        activeWebView = null

        val main = FrameLayout(this).apply { setBackgroundColor(bgColor) }
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(110))
            setBackgroundColor(bgColor)
        }

        // Top Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(logo(), LinearLayout.LayoutParams(dp(46), dp(46)))

        val brandBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        brandBox.addView(TextView(this).apply {
            text = "SRP Hub"
            textSize = 21f
            setTextColor(textColor)
            setTypeface(null, Typeface.BOLD)
        })

        brandBox.addView(TextView(this).apply {
            text = "One Hub. Everything Connected."
            textSize = 11f
            setTextColor(subTextColor)
        })

        header.addView(brandBox, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            leftMargin = dp(12)
        })

        val profileIcon = TextView(this).apply {
            text = "👤"
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(cardBgColor, 50)
            isClickable = true
            isFocusable = true
            setOnClickListener { openProfile() }
        }
        header.addView(profileIcon, LinearLayout.LayoutParams(dp(44), dp(44)))

        root.addView(header, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 18)

        // Search Box (Exact same as original image)
        val searchBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), 0, dp(18), 0)
            background = roundedBackground(cardBgColor, 24)
        }

        val searchInput = EditText(this).apply {
            hint = "Search apps..."
            textSize = 15.5f
            setSingleLine(true)
            setTextColor(textColor)
            setHintTextColor(subTextColor)
            setBackgroundColor(Color.TRANSPARENT)
        }
        searchBox.addView(searchInput, LinearLayout.LayoutParams(0, dp(52), 1f))

        val searchIcon = TextView(this).apply {
            text = "🔍"
            textSize = 16f
            gravity = Gravity.CENTER
        }
        searchBox.addView(searchIcon)

        root.addView(searchBox, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)))

        addGap(root, 24)

        // Full 16 Apps Grid
        val grid = GridLayout(this).apply {
            columnCount = 4
            useDefaultMargins = false
        }

        for (app in allApps) {
            val itemContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
            }

            val iconBox = TextView(this).apply {
                text = app.iconText
                textSize = 24f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                background = roundedBackground(app.bgColor, 20)
            }

            itemContainer.addView(iconBox, LinearLayout.LayoutParams(dp(68), dp(68)))

            val appName = TextView(this).apply {
                text = app.name
                textSize = 12.5f
                setTextColor(textColor)
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
            }

            itemContainer.addView(appName, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(8)
            })

            itemContainer.setOnClickListener {
                openService(app.name, app.url, app.isDesktop)
            }

            val gridParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(2), dp(12), dp(2), dp(12))
            }

            grid.addView(itemContainer, gridParams)
        }

        root.addView(grid, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 22)

        // Banner
        val bannerGradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.rgb(28, 85, 230), Color.rgb(115, 80, 245))
        ).apply { cornerRadius = dp(20).toFloat() }

        val banner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(22), dp(20), dp(18), dp(20))
            background = bannerGradient
        }

        val bannerTextLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        bannerTextLayout.addView(TextView(this).apply {
            text = "More Apps Coming Soon"
            textSize = 16.5f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        })
        bannerTextLayout.addView(TextView(this).apply {
            text = "We're working on adding more\npopular services for you."
            textSize = 12f
            setTextColor(Color.rgb(225, 230, 255))
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })

        banner.addView(bannerTextLayout, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val arrowCircle = TextView(this).apply {
            text = "➔"
            textSize = 15f
            setTextColor(Color.rgb(28, 85, 230))
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 50)
        }
        banner.addView(arrowCircle, LinearLayout.LayoutParams(dp(38), dp(38)))

        root.addView(banner, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        scroll.addView(root)
        main.addView(scroll, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        // Bottom Nav
        main.addView(createBottomNav("home"), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(72), Gravity.BOTTOM))

        setContentView(main)
    }

    private fun showAppsPage() {
        currentTab = "apps"
        updateThemeColors()
        activeWebView = null

        val main = FrameLayout(this).apply { setBackgroundColor(bgColor) }
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(110))
            setBackgroundColor(bgColor)
        }

        // Top Header with ← Apps
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val backBtn = TextView(this).apply {
            text = "←"
            textSize = 22f
            setTextColor(textColor)
            setPadding(0, 0, dp(14), 0)
            setOnClickListener { showHome() }
        }
        topBar.addView(backBtn)

        val titleView = TextView(this).apply {
            text = "Apps"
            textSize = 20f
            setTextColor(textColor)
            setTypeface(null, Typeface.BOLD)
        }
        topBar.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        root.addView(topBar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        addGap(root, 20)

        // Dedicated App List Cards
        for (app in allApps) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(14), dp(16), dp(14))
                background = roundedBackground(cardBgColor, 16, lightBorder)
                isClickable = true
                isFocusable = true
                setOnClickListener { openService(app.name, app.url, app.isDesktop) }
            }

            val iconBox = TextView(this).apply {
                text = app.iconText
                textSize = 22f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                background = roundedBackground(app.bgColor, 14)
            }
            card.addView(iconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

            val infoBox = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), 0, dp(8), 0)
            }

            val titleTv = TextView(this).apply {
                text = app.name
                textSize = 15.5f
                setTextColor(textColor)
                setTypeface(null, Typeface.BOLD)
            }
            infoBox.addView(titleTv)

            val subTv = TextView(this).apply {
                text = app.subtitle
                textSize = 12f
                setTextColor(subTextColor)
            }
            infoBox.addView(subTv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

            card.addView(infoBox, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

            val arrowTv = TextView(this).apply {
                text = "›"
                textSize = 22f
                setTextColor(subTextColor)
            }
            card.addView(arrowTv)

            root.addView(card, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(12)
            })
        }

        addGap(root, 10)

        // Banner at bottom of Apps Page
        val bannerGradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.rgb(28, 85, 230), Color.rgb(115, 80, 245))
        ).apply { cornerRadius = dp(20).toFloat() }

        val banner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(18), dp(16), dp(18))
            background = bannerGradient
        }

        val bannerTextLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        bannerTextLayout.addView(TextView(this).apply {
            text = "And More..."
            textSize = 15.5f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        })
        bannerTextLayout.addView(TextView(this).apply {
            text = "We're constantly adding new\napps for you."
            textSize = 11.5f
            setTextColor(Color.rgb(225, 230, 255))
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })

        banner.addView(bannerTextLayout, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val arrowCircle = TextView(this).apply {
            text = "➔"
            textSize = 14f
            setTextColor(Color.rgb(28, 85, 230))
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 50)
        }
        banner.addView(arrowCircle, LinearLayout.LayoutParams(dp(36), dp(36)))

        root.addView(banner, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        scroll.addView(root)
        main.addView(scroll, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        // Bottom Nav
        main.addView(createBottomNav("apps"), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(72), Gravity.BOTTOM))

        setContentView(main)
    }

    private fun createBottomNav(selectedTab: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(cardBgColor)
            elevation = dp(20).toFloat()
            setPadding(0, dp(8), 0, dp(10))

            // Home Tab
            val homeTab = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener { showHome() }
            }
            val homeIcon = TextView(context).apply {
                text = "🏠"
                textSize = 18f
                gravity = Gravity.CENTER
            }
            val homeText = TextView(context).apply {
                text = "Home"
                textSize = 11.5f
                setTextColor(if (selectedTab == "home") blue else subTextColor)
                gravity = Gravity.CENTER
                if (selectedTab == "home") setTypeface(null, Typeface.BOLD)
            }
            val homeIndicator = View(context).apply {
                background = roundedBackground(if (selectedTab == "home") blue else Color.TRANSPARENT, 4)
            }
            homeTab.addView(homeIcon)
            homeTab.addView(homeText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
            homeTab.addView(homeIndicator, LinearLayout.LayoutParams(dp(18), dp(3)).apply { topMargin = dp(3) })

            // Apps Tab
            val appsTab = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener { showAppsPage() }
            }
            val appsIcon = TextView(context).apply {
                text = "▦"
                textSize = 18f
                setTextColor(if (selectedTab == "apps") blue else subTextColor)
                gravity = Gravity.CENTER
            }
            val appsText = TextView(context).apply {
                text = "Apps"
                textSize = 11.5f
                setTextColor(if (selectedTab == "apps") blue else subTextColor)
                gravity = Gravity.CENTER
                if (selectedTab == "apps") setTypeface(null, Typeface.BOLD)
            }
            val appsIndicator = View(context).apply {
                background = roundedBackground(if (selectedTab == "apps") blue else Color.TRANSPARENT, 4)
            }
            appsTab.addView(appsIcon)
            appsTab.addView(appsText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
            appsTab.addView(appsIndicator, LinearLayout.LayoutParams(dp(18), dp(3)).apply { topMargin = dp(3) })

            // Profile Tab
            val profileTab = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener { openProfile() }
            }
            val profileNavIcon = TextView(context).apply {
                text = "👤"
                textSize = 18f
                gravity = Gravity.CENTER
            }
            val profileText = TextView(context).apply {
                text = "Profile"
                textSize = 11.5f
                setTextColor(subTextColor)
                gravity = Gravity.CENTER
            }
            profileTab.addView(profileNavIcon)
            profileTab.addView(profileText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

            addView(homeTab, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(appsTab, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(profileTab, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }
    }

    private fun openService(title: String, url: String, isDesktopMode: Boolean = false) {
        updateThemeColors()
        val webContainer = FrameLayout(this).apply {
            setBackgroundColor(bgColor)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
        }

        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(10))
            setBackgroundColor(bgColor)
            elevation = dp(6).toFloat()
        }

        val backBtn = TextView(this).apply {
            text = "←"
            textSize = 22f
            setTextColor(textColor)
            setPadding(0, 0, dp(16), 0)
            setOnClickListener {
                activeWebView?.apply {
                    clearHistory()
                    clearCache(true)
                    loadUrl("about:blank")
                }
                activeWebView = null
                if (currentTab == "apps") showAppsPage() else showHome()
            }
        }
        topBar.addView(backBtn)

        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(textColor)
            setTypeface(null, Typeface.BOLD)
        }
        topBar.addView(titleView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        root.addView(topBar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        val progressLayoutParams = FrameLayout.LayoutParams(dp(48), dp(48)).apply {
            gravity = Gravity.CENTER
        }

        val webView = WebView(this)

        val cookieManager = android.webkit.CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        webView.apply {
            setBackgroundColor(bgColor)
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                
                allowFileAccess = true 
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

                if (isDesktopMode) {
                    userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                } else {
                    userAgentString = "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                }
            }

            cookieManager.setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    return !(url.startsWith("http://") || url.startsWith("https://"))
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    cookieManager.flush()

                    val jsFixer = """
                        javascript:(function() {
                            try {
                                var css = 'div[class*="tiktok-cookie-banner"], div[class*="bottom-banner"], div[class*="mask-container"], div[class*="modal-overlay"], div[class*="div-mask"], div[data-sigil="m_banner"], div[class*="app-upsell"] { display: none !important; opacity: 0 !important; visibility: hidden !important; pointer-events: none !important; }';
                                var style = document.createElement('style');
                                style.type = 'text/css';
                                style.appendChild(document.createTextNode(css));
                                document.head.appendChild(style);
                            } catch(e) {}
                        })()
                    """.trimIndent()

                    view?.evaluateJavascript(jsFixer, null)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback?.onReceiveValue(null)
                    this@MainActivity.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }

                    try {
                        startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE)
                    } catch (e: Exception) {
                        this@MainActivity.filePathCallback = null
                        return false
                    }
                    return true
                }
            }

            val extraHeaders = HashMap<String, String>()
            extraHeaders["Accept-Language"] = "en-US,en;q=0.9"

            loadUrl(url, extraHeaders)
        }

        activeWebView = webView
        
        webContainer.addView(webView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        webContainer.addView(progressBar, progressLayoutParams)

        root.addView(webContainer, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        setContentView(root)
    }
}
