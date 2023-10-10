package fr.ernicani.learncode33

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import fr.ernicani.learncode33.databinding.ActivityLoggedBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoggedActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences  // Make this public or add a public getter
    private val client = OkHttpClient()
    private lateinit var binding: ActivityLoggedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(LoggedHome())

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(LoggedHome())
                R.id.profile -> replaceFragment(LoggedProfile())
                R.id.notifications -> replaceFragment(LoggedNotifications())
                else -> {
                }
            }
            true
        }

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            checkSession(token)
        } else {
            logout()
        }
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("isLogged", false)
            putString("token", null)
            apply()
        }
        val intent = Intent(this@LoggedActivity, MainActivity::class.java)
        intent.putExtra("logged_out", true)
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}
