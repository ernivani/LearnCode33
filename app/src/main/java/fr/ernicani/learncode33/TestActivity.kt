package fr.ernicani.learncode33

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_test)

        val topBarLessonName: TextView = findViewById(R.id.topBarLessonName)
        topBarLessonName.text = "Nouveau nom de la leçon"
    }
}
