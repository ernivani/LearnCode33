package fr.ernicani.learncode33

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {
    private lateinit var fetchDataButton: Button
    private lateinit var fetchedDataTextView: TextView
    private lateinit var cityEditText: EditText
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchDataButton = findViewById(R.id.fetchData)
        fetchedDataTextView = findViewById(R.id.fetchedDataTextView)
        cityEditText = findViewById(R.id.cityEditText)

        settingsClient = LocationServices.getSettingsClient(this)
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchDataButton.setOnClickListener { fetchData() }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun fetchData() {
        val city = cityEditText.text.toString().trim()

        if (city.isNullOrEmpty()) {
            // Fetch current location if city is not specified
            if (!isLocationEnabled()) {
                fetchedDataTextView.text = "Veuillez activer la localisation"
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                return
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        fetchWeatherDataByCoordinates(location.latitude, location.longitude)
                    } else {
                        fetchedDataTextView.text = "localisation indisponible"
                    }
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            return
        }

        fetchedDataTextView.text = "Chargement..."

        fetchWeatherDataByCity(city)
    }

    private fun fetchWeatherDataByCity(city: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=66bb8eed95ce71a17230f561a7be99a5&units=metric")
            .build()

        client.newCall(request).enqueue(getCallbackForWeatherResponse())
    }

    private fun fetchWeatherDataByCoordinates(lat: Double, lon: Double) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=66bb8eed95ce71a17230f561a7be99a5&units=metric")
            .build()

        client.newCall(request).enqueue(getCallbackForWeatherResponse())
    }

    @SuppressLint("SetTextI18n")
    private fun getCallbackForWeatherResponse(): Callback {
        return object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                this@MainActivity.runOnUiThread {
                    fetchedDataTextView.text = "Une erreur est survenue"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body?.string()
                    this@MainActivity.runOnUiThread {
                        val temperature = myResponse?.split("\"temp\":")?.get(1)?.split(",")?.get(0)
                        val weather = myResponse?.split("\"main\":\"")?.get(1)?.split("\"")?.get(0)
                        fetchedDataTextView.text = "Il fait $temperature°C et le temps est $weather"
                    }
                }
            }
        }
    }
}
