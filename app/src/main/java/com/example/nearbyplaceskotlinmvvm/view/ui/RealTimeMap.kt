package com.example.nearbyplaceskotlinmvvm.view.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nearbyplaceskotlinmvvm.R
import com.example.nearbyplaceskotlinmvvm.databinding.ActivityRealTimeMapBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class RealTimeMap : AppCompatActivity(), OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
{


    var activityRealTimeMapBinding: ActivityRealTimeMapBinding ?= null
    private var locationRequest = LocationRequest()
    private var UPDATE_INTERVALE: Long = 2000
    private var default_lattitude = 0.0
    private var default_longitude = 0.0
    private var location: Location? = null
    private  var map:  GoogleMap ?= null
    internal var googleApiClient: GoogleApiClient? = null
    var geocoder: Geocoder? = null

    private var currentLattitude: Double ?= null
    private var currentLongitude: Double ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    setContentView(R.layout.activity_real_time_map)


        activityRealTimeMapBinding = DataBindingUtil.setContentView(this,R.layout.activity_real_time_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.realTimeMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onStart() {
        super.onStart()
        startLocationUpdate()
    }

    private fun startLocationUpdate()
    {

        locationRequest = LocationRequest.create()
        locationRequest!!.run {
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            setFastestInterval(UPDATE_INTERVALE)
        }

    }

    override fun onMapReady(p0: GoogleMap?)
    {
        map = p0


        if (map != null)
        {

            map!!.addMarker(MarkerOptions().position(LatLng(default_lattitude,default_longitude))).title = "Home"

        }

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listener
        registerLocationListner()

    }

    private fun registerLocationListner()
    {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.getLastLocation())
            }
        }
        // 4. add permission if android version is greater then 23
        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient()
            map!!.isMyLocationEnabled = true
            return true;
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                registerLocationListner()
            }
        }
    }


        override fun onLocationChanged(p0: Location?)
        {

            Log.d("RealtimeMap","Location Changed")
            buildGoogleApiClient()
            map!!.isMyLocationEnabled = true

            // create message for toast with updated latitude and longitudefa
            var msg = "Updated Location: " + p0!!.latitude  + " , " +p0!!.longitude
            Log.d("RealtimeMap","Msg $msg")
            // show toast message with updated location
            //Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
            val location = LatLng(p0!!.latitude, p0.longitude)

            var Address: ArrayList<Address>? = null
            geocoder = Geocoder(this, Locale.getDefault())
            try
           {
               Address = geocoder!!.getFromLocation(p0.latitude!!, p0.longitude!!, 1) as ArrayList<Address>
           }
            catch (e: IOException)
            {
               Log.d("RealTimeMap", "" + e)
            }

            var place = Address!![0].getAddressLine(0)
            Log.d("RealTimeMap", "$place" )


            map!!.clear()
            map!!.addMarker(MarkerOptions().position(location).title(place))
            map!!.moveCamera(CameraUpdateFactory.newLatLng(location))

        
        }

    @Synchronized
    protected open fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        googleApiClient!!.connect()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        
    }

    override fun onProviderEnabled(p0: String?) {
        
    }

    override fun onProviderDisabled(p0: String?) {
        
    }

    override fun onConnected(p0: Bundle?) {


        Log.d("DetailsActivity", "onConnected $p0")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ///////////// current Location Marker And address
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
    }
        


    override fun onConnectionSuspended(p0: Int) {
        
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        
    }
}
