package fr.ernicani.learncode33;


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient;
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var fetchDataButton: Button
    private lateinit var fetchedDataTextView: TextView
    private lateinit var cityEditText: EditText  // New EditText field

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchDataButton = findViewById(R.id.fetchData)
        fetchedDataTextView = findViewById(R.id.fetchedDataTextView)
        cityEditText = findViewById(R.id.cityEditText)  // Initialize EditText

        fetchDataButton.setOnClickListener { fetchData() }
    }

    private fun fetchData() {
        val city = cityEditText.text.toString().trim()  // Get city name from EditText and trim whitespace


        if (city.isNullOrEmpty()) {
            fetchedDataTextView.text = "Please enter a city name"
            return
        }

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=66bb8eed95ce71a17230f561a7be99a5&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("SetTextI18n")
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                this@MainActivity.runOnUiThread {
                    fetchedDataTextView.text = "Une erreur est survenue"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body?.string()
                    this@MainActivity.runOnUiThread {
                        fetchedDataTextView.text = myResponse
                    }
                }
            }
        })
    }
}