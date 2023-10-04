package fr.ernicani.learncode33;


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import fr.ernicani.learncode33.R

class MainActivity : AppCompatActivity() {
    private lateinit var counterTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        counterTextView = findViewById(R.id.counterTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)

        // Set initial counter value
        updateCounterText()

        // Set click listeners for buttons
        incrementButton.setOnClickListener { incrementCounter() }
        decrementButton.setOnClickListener { decrementCounter() }
    }

    private fun incrementCounter() {
        counter++
        updateCounterText()
    }

    private fun decrementCounter() {
        if (counter > 0) {
            counter--
            updateCounterText()
        }
    }

    private fun updateCounterText() {
        counterTextView.text = counter.toString()
    }
}