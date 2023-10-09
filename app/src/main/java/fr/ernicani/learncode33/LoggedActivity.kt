package fr.ernicani.learncode33

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoggedActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var infoTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged)


        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        val token = sharedPreferences.getString("token", null)
        val isLogged = sharedPreferences.getBoolean("isLogged", false)

        if (token != null) {
            checkSession(token)
        } else {
            logout()
        }


        infoTextView = findViewById(R.id.infoTextView)
        infoTextView.text = "Token: $token\nisLogged: $isLogged"


        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLogged", false)
            putString("token", null)
            apply()
        }

        val intent = Intent(this@LoggedActivity, MainActivity::class.java)
        intent.putExtra("logged_out", true)  // Add this line
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

                if (!isLogged) {
                    runOnUiThread {
                        logout()
                    }
                }
            }
        }
    }
}
