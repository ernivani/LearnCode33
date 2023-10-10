package fr.ernicani.learncode33

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var loginRedirect: MaterialButton
    private lateinit var registerRedirect: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Check for the logged_out flag and show Snackbar if needed
        if (intent.getBooleanExtra("logged_out", false)) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "You have been logged out",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        // Récupérez le token et le statut de connexion
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            navigateToLoggedActivity()
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


}
