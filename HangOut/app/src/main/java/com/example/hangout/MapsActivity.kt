package com.example.hangout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.util.Log
import android.widget.Button
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.json.JSONObject

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var okHttpClient: OkHttpClient

    private lateinit var confirm: Button

    private lateinit var firebaseDatabase: FirebaseDatabase




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

        firebaseDatabase = FirebaseDatabase.getInstance()


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
        val reference = firebaseDatabase.getReference("friends")
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

        fun checkInButton(marker: Business) {
            val colorPrimary = ContextCompat.getColor(
                this, R.color.colorPrimary
            )
            confirm.setBackgroundColor(colorPrimary)
            confirm.text = "Check In Here"
            confirm.isEnabled = true

        }
        fun anyoneThere(place:String):Int{
            var count  = 0
            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@MapsActivity,
                        "Failed to retrieve Reviews: $error!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onDataChange(data: DataSnapshot) {
                    val bundle = Bundle()
                    var num =  0
                    val numReviews = data.children.count()
                    bundle.putInt("count", numReviews)
                    //loop through friends checked in to see if it matches place
                    data.children.forEach { child ->
                        val friend = child.getValue(FriendsList::class.java)
                        if (friend != null) {
                            if (friend.checkedIn == place) {
                               num ++
                                Log.e("count", num.toString())
                            }
                        }
                    }
                    count = num
                }
            })
            Log.e("num", count.toString())
           return count
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
                                //find who is checked in
                                ).title(results[i].name).snippet(anyoneThere(results[i].name).toString()).icon(
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
                        ).title(clickedMarker.name).snippet(anyoneThere(clickedMarker.name).toString()).icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        ))
                        )
                    }
                    mMap.setOnMarkerClickListener { marker ->
                        val clickedMarker =
                            results.find { result -> result.name == marker.title }
                        if (clickedMarker != null) {
                            checkInButton(clickedMarker)

                        }
                        false
                    }
                    confirm.setOnClickListener {
                        //need marker
                        val clickedMarker =
                            results.find { result -> result.checkin == true }
                        if(clickedMarker != null) {
                            //change the marker when the person checks in
                            changeColor(clickedMarker)
                            clickedMarker.checkin =  false
                            //write to check in
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val user: String = currentUser!!.email!!
                            val friend = FriendsList(user, clickedMarker.name)
                            Log.e("friends", friend.toString())
                            reference.push().setValue(friend)
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
        val latitude = 38.898365
        val longitude = -77.046753
        val radius1 = 150
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
