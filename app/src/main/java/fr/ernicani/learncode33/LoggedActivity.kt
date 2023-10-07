package fr.ernicani.learncode33

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoggedActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged)

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }
    }

    private fun logout() {
        // Récupérez SharedPreferences et effacez la session
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLogged", false)
            apply()
        }

        // Redirigez vers MainActivity
        val intent = Intent(this@LoggedActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
