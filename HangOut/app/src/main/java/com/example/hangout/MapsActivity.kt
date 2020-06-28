package com.example.hangout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var okHttpClient: OkHttpClient

    private lateinit var confirm: Button

    init {
        // Turn on console logging for our network traffic, useful during development
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        builder.addInterceptor(logging)
        okHttpClient = builder.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        confirm = findViewById(R.id.maps_button)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val GWU = LatLng(38.898365, -77.046753)
        val api = getString(R.string.apiKey)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GWU))
        //val api = getString(R.string.api_key)
        mMap = googleMap
        val zoomLevel = 16.0f
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(GWU, zoomLevel)
        )

        fun checkInButton() {
            val colorPrimary = ContextCompat.getColor(
                this, R.color.colorPrimary
            )
            confirm.setBackgroundColor(colorPrimary)
            confirm.text = "Check In Here"
            confirm.isEnabled = true
        }
        doAsync {
            var results: List<Business> = retrieveBusinesses(api)
            if (results.isNotEmpty()) {

                runOnUiThread {
                    for (i in 0 until results.size) {
                            mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        results[i].lat,
                                        results[i].long
                                    )
                                ).title(results[i].name).snippet((results[i].rating.toString())).icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_AZURE
                                    )
                                )
                            )
                    }
                    fun changeColor(clickedMarker:Business) {

                        mMap.addMarker(MarkerOptions().position(
                            LatLng(
                                clickedMarker.lat,
                                clickedMarker.long
                            )
                        ).title(clickedMarker.name).snippet(("CLICKED")).icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        ))
                        )
                        Log.e("here", mMap.toString())
                    }
                    mMap.setOnMarkerClickListener { marker ->
                        val clickedMarker =
                            results.find { result -> result.name == marker.title }
                        if (clickedMarker != null) {
                            checkInButton()
                            clickedMarker.checkin = true
                        }
                        false
                    }
                    confirm.setOnClickListener {
                        val clickedMarker =
                            results.find { result -> result.checkin == true }
                        if(clickedMarker != null) {
                            changeColor(clickedMarker)
                        }
                    }
                }

            }


       }

    }

  

    fun retrieveBusinesses(
        apiKey: String

    ): List<Business> {
        // Data setup
        val GWU = LatLng(38.898365, -77.046753)
        val latitude = 38.898365
        val longitude = -77.046753
        val radius1 = 150
        val categories = "pizza"
        Log.e("api", apiKey)
        //val businessList = mutableListOf<Business>()
        // Build our request to turn - for now, using a hardcoded OAuth token
        //this needs to be off the systems
        val request = Request.Builder()
            .url("https://api.yelp.com/v3/businesses/search?latitude=$latitude&longitude=$longitude&radius=$radius1")
            .header("Authorization", "Bearer $apiKey")
            .build()
        val response = okHttpClient.newCall(request).execute()

        val responseString: String? = response.body?.string()
        val businessList = mutableListOf<Business>()
        // Confirm that we retrieved a successful (e.g. 200) response with some body content
        if (response.isSuccessful && !responseString.isNullOrEmpty()) {
            // Parse the JSON response that was sent back by the server
            val jsonObject = JSONObject(responseString)
            val statuses = jsonObject.getJSONArray("businesses")
            for (i in 0 until statuses.length()) {

                val reviewJson = statuses.getJSONObject(i)
                val url = reviewJson.getString("url")
                val rating = reviewJson.getInt("rating")
                val name = reviewJson.getString("name")
                val coord = reviewJson.getJSONObject("coordinates")
                val lat = coord.getDouble("latitude")
                val long = coord.getDouble("longitude")
                val bID = reviewJson.getString("id")
                var checkIn = false
                var business = Business(
                    name = name,
                    rating = rating,
                    bId = bID,
                    lat = lat,
                    long = long,
                    url = url,
                    checkin = checkIn
                )
                businessList.add(business)
            }

        }
        return businessList

    }

}
