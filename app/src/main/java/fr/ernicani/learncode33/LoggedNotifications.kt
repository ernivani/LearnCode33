package fr.ernicani.learncode33

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class LoggedNotifications : Fragment(R.layout.fragment_notifications) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedActivity = activity as LoggedActivity
        val token = loggedActivity.sharedPreferences.getString("token", null)

        if (token != null) {
            getNotifications(token)
        }
    }

    private fun getNotifications(token: String) {
        val url = "https://api.impin.fr/get-notifications"

        // Création du JSON Body
        val json = JSONObject()
        json.put("token", token)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = RequestBody.create(mediaType, json.toString())

        // Construction de la requête
        val request = Request.Builder()
            .url(url)
            .post(requestBody)  // Utilisez POST au lieu de GET
            .addHeader("Authorization", "Bearer $token")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error fetching notifications: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val jsonResponse = JSONObject(body)
                val notifications = jsonResponse.getString("notifications")

                activity?.runOnUiThread {
                    val notificationsTextView = view?.findViewById<TextView>(R.id.notificationsTextView)
                    notificationsTextView?.text = notifications
                }
            }
        })
    }

}
