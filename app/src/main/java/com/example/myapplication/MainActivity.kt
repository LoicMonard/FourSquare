package com.example.foursquare

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import layout.location
import layout.place
import java.util.jar.Manifest
import okhttp3.*
import org.esiea.cam_gidon.kotlineval.MyAdapter
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var places: MutableList<place> = mutableListOf()
    lateinit var locationManager: LocationManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps : Location? = null
    private var locationNetwork : Location? = null
    private val client = OkHttpClient()


    // Permissions that need to be agreed
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 10)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLocation()
        run("https://api.foursquare.com/v2/venues/search?client_id=BXMYSR3C0GWVBH1HV2S3GND2GZ5S5ZL1VOZ2WZVUQONK2BEP&client_secret=CPT5JXJ5AZKSMGUGK4PEYIP2W2NAIAQ0VTM3DUKAVS4BCFLC&ll="+locationGps!!.latitude+","+locationGps!!.longitude+"&query=pizza&v=20190412&limit=8")

        Thread.sleep(2000)
        // TODO : Rate implementation

        // Set Adapter
        viewManager = LinearLayoutManager( this )
        viewAdapter = MyAdapter(places)

        // Create recycler
        val recycler = findViewById(R.id.recycler) as RecyclerView
        recycler.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    // Retrieves GPS location if it's more accurate than Network connection, if not, returns network location
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(hasGps || hasNetwork) {
            if(hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if(location!=null) {
                            locationGps = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGpsLocation!=null) {
                    locationGps = localGpsLocation
                }
            }
            if(hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetwork")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if(location!=null) {
                            locationNetwork = location
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetworkLocation!=null) {
                    locationNetwork = localNetworkLocation
                }
            }

            Toast.makeText(this, "Hi there! This is a Toast.", Toast.LENGTH_LONG).show()

            if(locationGps!=null && locationNetwork!=null) {
                if(locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    Log.d("CodeAndroidLocation", "Network latitude: " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", "Network longitude: " + locationNetwork!!.longitude)
//                    val text : TextView = findViewById(R.id.text)
//                    text.append("\nNetwork :")
//                    text.append("\nLatitude: " + locationNetwork!!.latitude)
//                    text.append("\nLongitude : " + locationNetwork!!.longitude)
                } else {
                    Log.d("CodeAndroidLocation", "GPS latitude: " + locationGps!!.latitude)
                    Log.d("CodeAndroidLocation", "GPS longitude: " + locationGps!!.longitude)
//                    val text : TextView = findViewById(R.id.text)
//                    text.append("\nGps :")
//                    text.append("\nLatitude: " + locationGps!!.latitude)
//                    text.append("\nLongitude : " + locationGps!!.longitude)
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }

    fun run(url: String) {
        // Create request with OkHttp
        val request = Request.Builder()
            .url(url)
            .build()
        //send request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Fail", "Error while calling API")
            }
            override fun onResponse(call: Call, response: Response) {
                // Make an object from the json received
                Log.d("Success", "Retrieved data")
                val json = response.body()?.string() ?: ""
                val obj = JSONObject(json)
                val response = JSONObject(obj.getString("response"))
                val jsonList = response.getJSONArray("venues")
                for (i in 0 until jsonList.length()) {
                    val test =  jsonList.getJSONObject(i)
                    val id = test.getString("id")?: ""
                    val name = test.getString("name")?: ""
                    val locJson = JSONObject(test.getString("location"))
                    var address = ""
                    if (locJson.has("address")) {
                        address  = locJson.getString("address")
                    }
                    var city = ""
                    if (locJson.has("city")) {
                        city  = locJson.getString("city")
                    }
                    var country = ""
                    if (locJson.has("country")) {
                        country  = locJson.getString("country")
                    }
                    var postalCode = ""
                    if (locJson.has("postalCode")) {
                        postalCode  = locJson.getString("postalCode")
                    }

                    places.add(place(id = id, name = name ,distance = "10",location =location(address = address ?: "", postalCode = postalCode ?: "", country = country ?: "", city = city ?: ""), rate = 5  ))
                }

            }
        })
    }
}
