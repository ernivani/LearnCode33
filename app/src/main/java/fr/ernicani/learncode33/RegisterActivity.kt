package fr.ernicani.learncode33

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity: AppCompatActivity() {

    private lateinit var usernameEditText: TextView
    private lateinit var passwordRegisterEditText: TextView
    private lateinit var emailRegisterEditText: TextView
    private lateinit var registerButton: MaterialButton

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)


        usernameEditText = findViewById(R.id.usernameEditText)
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText)
        emailRegisterEditText = findViewById(R.id.emailRegisterEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener { performAction("https://api.impin.fr/register") }

        val isLogged = sharedPreferences.getBoolean("isLogged", false)
        if (isLogged) navigateToLoggedActivity()
    }

    private fun navigateToLoggedActivity() {
        val intent = Intent(this, LoggedActivity::class.java)
        startActivity(intent)
    }

    private fun performAction(url: String) {
        val email = emailRegisterEditText.text.toString().trim()
        val password = passwordRegisterEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()

        if (email.isNotBlank() && password.isNotBlank()) {
            sendRequest(url, email, password, username)
        } else {
            // todo: handle error
        }
    }

    private fun sendRequest(url: String, email: String, password: String, username: String) {
        val payload = JSONObject().apply {
            put("email", email)
            put ("username", username)
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
                    // todo: handle error
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        with(sharedPreferences.edit()) {
                            putBoolean("isLogged", true)
                            apply()
                        }
                        navigateToLoggedActivity()
                    } else {
                        // todo: handle error
                    }
                }
            }
        }
    }

}