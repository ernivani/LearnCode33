package fr.ernicani.learncode33

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {

    private lateinit var loginRedirect: MaterialButton
    private lateinit var registerRedirect: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences
    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        val isLogged = sharedPreferences.getBoolean("isLogged", false)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            checkSession(token)
        }

        loginRedirect = findViewById(R.id.LoginRedirect)
        registerRedirect = findViewById(R.id.RegisterRedirect)

        loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerRedirect.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun navigateToLoggedActivity() {
        val intent = Intent(this, LoggedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkSession(token: String) {
        val url = "https://api.impin.fr/check-session"

        val payload = JSONObject().apply {
            put("token", token)
        }

        val requestBody = payload.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(getCallbackForApiResponse())


    }

    private fun getCallbackForApiResponse(): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val json = JSONObject(body)
                val isLogged = json.getBoolean("isLogged")

                if (isLogged) {
                    navigateToLoggedActivity()
                }
            }
        }
    }




}
