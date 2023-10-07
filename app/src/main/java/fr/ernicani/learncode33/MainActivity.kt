package fr.ernicani.learncode33

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var statusTextView: TextView
    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }

        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        statusTextView = findViewById(R.id.statusTextView)

        loginButton.setOnClickListener { performAction("https://api.impin.fr/login") }
        registerButton.setOnClickListener { performAction("https://api.impin.fr/register") }

        val isLogged = sharedPreferences.getBoolean("isLogged", false)
        if (isLogged) {
            navigateToLoggedActivity()
        }
    }

    private fun navigateToLoggedActivity() {
        val intent = Intent(this@MainActivity, LoggedActivity::class.java)
        startActivity(intent)
    }
    @SuppressLint("SetTextI18n")
    private fun performAction(url: String) {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isNotBlank() && password.isNotBlank()) {
            sendRequest(url, username, password)
        } else {
            statusTextView.text = "Please provide both username and password"
        }
    }

    private fun sendRequest(url: String, username: String, password: String) {
        val payload = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        val requestBody = payload.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(getCallbackForApiResponse())
    }

    private fun getCallbackForApiResponse(): Callback {
        return object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                this@MainActivity.runOnUiThread {
                    statusTextView.text = "Une erreur est survenue"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    this@MainActivity.runOnUiThread {
                        statusTextView.text = "Opération réussie"

                        with(sharedPreferences.edit()) {
                            putBoolean("isLogged", true)
                            apply()
                        }

                        navigateToLoggedActivity()

                    }
                } else {
                    this@MainActivity.runOnUiThread {
                        statusTextView.text = "Échec de l'opération"
                    }
                }
            }
        }
    }


    private fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("isLogged", false)
            apply()
        }

        val intent = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(intent)


    }
}
