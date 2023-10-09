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

        if (isLogged) navigateToLoggedActivity()

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



}
