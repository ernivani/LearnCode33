package fr.ernicani.learncode33

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        loginButton.setOnClickListener { performAction("https://api.impin.fr/login") }

        val token = sharedPreferences.getString("token", null)
        if (token != null) navigateToLoggedActivity()
    }

    private fun navigateToLoggedActivity() {
        val intent = Intent(this, LoggedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performAction(url: String) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotBlank() && password.isNotBlank()) {
            sendRequest(url, email, password)
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Email or Password cannot be empty",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendRequest(url: String, email: String, password: String) {
        val payload = JSONObject().apply {
            put("username", email)
            put("password", password)
        }

        val requestBody = payload.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(getCallbackForApiResponse())
    }

    private fun getCallbackForApiResponse(): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Network error, please try again",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        with(sharedPreferences.edit()) {
                            putBoolean("isLogged", true)
                            val responseBody = response.body?.string()
                            val token = responseBody?.let { JSONObject(it).getString("token") }
                            putString("token", token)
                            apply()
                        }
                        navigateToLoggedActivity()
                    } else {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Login failed, please check your credentials",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
