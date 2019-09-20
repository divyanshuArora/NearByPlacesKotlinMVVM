package com.example.nearbyplaceskotlinmvvm.view.ui

import android.Manifest.permission
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.nearbyplaceskotlinmvvm.R
import com.example.nearbyplaceskotlinmvvm.databinding.ActivityDetailsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.Builder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xml.sax.Parser
import java.io.IOException
import java.util.*


open class DetailsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private  var name:String ?= null
    private  var hours:String ?= null
    private  var rating:String ?= null
    private  var totalRating:String ?= null
    private  var address:String ?= null

    private var currentLattitude :Double? = null
    private  var currentLongitude:Double? = null
    private  var lat: Double ?= null
    private  var long: Double ?= null
    private  var activityDetailsBinding : ActivityDetailsBinding ?= null
    private  var location: Location? = null
    internal  var map: GoogleMap ?= null
    internal var CurrentLocationarker: Marker? = null
    internal var googleApiClient: GoogleApiClient? = null
    internal var MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1

    var CurrentlatLng:LatLng ?= null

    var geocoder:Geocoder ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_details)

        activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        hours =          intent.getStringExtra("hours")
        address =        intent.getStringExtra("address")
        totalRating =    intent.getStringExtra("totalRating")
        lat =            intent.getDoubleExtra("lat", 1.0)
        long =           intent.getDoubleExtra("long", 1.0)
        showData()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


///////////////// show details start
    fun showData()
    {
        if ( intent.getStringExtra("rating")!= null)
        {
            rating = intent.getStringExtra("rating")
        }
        name = intent.getStringExtra("name")

        try {
            Log.d("DetailsActivity","name: $name, hours: $hours, rating: $rating")
        }
        catch (e : Exception)
        {
            Log.d("DetailsActivity","Exception: "+e)
        }

        activityDetailsBinding!!.name.text = name
        activityDetailsBinding!!.rating.text = rating
        activityDetailsBinding!!.totalRating.text = totalRating

        if (hours == "open")
        {
            activityDetailsBinding!!.open.setTextColor(Color.GREEN)
            activityDetailsBinding!!.open.setTextSize(18F)

        }
        else
        {
            activityDetailsBinding!!.open.setTextColor(Color.RED)
        }


        activityDetailsBinding!!.open.text = hours
        activityDetailsBinding!!.address.text = address
    }
/////////////// show details over



/////////////////////// map
    @RequiresApi(api = VERSION_CODES.M) @Override
    override fun onMapReady(googleMap: GoogleMap?)
    {
        Log.d("DetailsActivity","onMapReady ")

        map = googleMap

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            try {
                if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    buildGoogleApiClient()
                    map!!.isMyLocationEnabled = true
                }
                else
                {
                    ActivityCompat.requestPermissions(this, arrayOf<String?>(permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)
                }
            } catch (e: java.lang.Exception) {
                Log.d("DetailsActivity", "onMapReadyException: ")
            }
        }
        else
        {
            buildGoogleApiClient()
            map!!.isMyLocationEnabled = true
        }
    }


    @Synchronized
    protected open fun buildGoogleApiClient() {
        googleApiClient = Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        googleApiClient!!.connect()
    }

    override fun onLocationChanged(p0: Location)
    {
         Log.d("DetailsActivity", "onLocationChanged $p0")

        val latLng = LatLng(p0.latitude,p0.longitude)
        Log.d("DetailsActivity","onLocationChanged, LatLng: $latLng")
        CurrentLocationarker = map!!.addMarker(MarkerOptions().position(latLng))

        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
    }


    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?)
    {
        Log.d("DetailsActivity", "onStatusChanged $p0")
    }

    override fun onProviderEnabled(p0: String?)
    {
        Log.d("DetailsActivity", "onProviderEnabled $p0")
    }

    override fun onProviderDisabled(p0: String?)
    {
        Log.d("DetailsActivity", "onProviderDisabled $p0")
    }

    override fun onConnected(p0: Bundle?)
    {
        Log.d("DetailsActivity", "onConnected $p0")
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }


        ///////////// current Location Marker And address
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

        if (location != null)
        {
            Log.e("Latitude : ", "" + location!!.latitude.toString() + "\nLongitude : " + location!!.longitude)
            currentLattitude = location!!.latitude
            currentLongitude = location!!.longitude
            Log.d("DetailsActivity", "onConnected: " + location!!.latitude.toString() + "\nLongitude : " + location!!.longitude)
            //val CurrentlatLng = LatLng(currentLattitude!!, currentLongitude!!)
            CurrentlatLng = currentLattitude?.let { currentLongitude?.let { it1 -> LatLng(it, it1) } }!!

            ////////////// getting current location address
            var Address : ArrayList<Address> ?= null
            geocoder = Geocoder(this, Locale.getDefault())
            try {
                Address = geocoder!!.getFromLocation(currentLattitude!!, currentLongitude!!, 1) as ArrayList<Address>
            } catch (e: IOException) {
                Log.d("DetailsActivity",""+e)
            }
            Log.d("DetailsACtivity", "Current Address:  Address$Address!!.get(0).getAddressLine(2)")

            //////////////////////////// Current Location Marker

            map!!.addMarker(MarkerOptions().position(CurrentlatLng!!)).title = Address!!.get(0).getAddressLine(0)
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentlatLng,16.0f))
            Log.d("DetailsActivity","Current LatLng: $CurrentlatLng")
        }

//////////////////////address marker and title
        var AddressLatLng:LatLng = lat?.let { long?.let { it1 -> LatLng(it, it1) } }!!
        Log.d("DetailsActivity","Address Bundle: $address")
        map!!.addMarker(MarkerOptions().position(AddressLatLng)).title = address
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(AddressLatLng,16.0f))

        generatePolyLine(CurrentlatLng!!,AddressLatLng)


    }

    private fun generatePolyLine(currentlatLng: LatLng, addressLatLng: LatLng) {

        Log.d("DetailsActivity","CurrentLatLong: $currentlatLng, AddressLatLong: $addressLatLng")
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        getURL(currentlatLng,addressLatLng)
    }

    private fun getURL(from : LatLng, to : LatLng) {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val api_key = "AIzaSyBH_TExjnT7McUcM-x39Gl0PTDPc7mSiUs"
        val params = "$origin&$dest&$sensor"


        var url ="https://maps.googleapis.com/maps/api/directions/json?$params+&key=$api_key"

        Log.d("DetailsActivity","Url: $url")

        getPolyLine(url)

       // return url
    }

    inner class getPolyLine(val url: String): AsyncTask<Void, Void, List<List<LatLng>>>()
    {
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body.toString()
            val result = ArrayList<List<LatLng>>()
            Log.d("DetailsActivity","data: $data")
            try
            {
                Log.d("DetailsActivity","data: $data")
            }
            catch (e:Exception)
            {
                Log.d("DetailsActivity","Exception: $e")
            }

        return  result
        }

    }


    //////////////////////////////////////////// generate Poly
    override fun onConnectionSuspended(p0: Int)
    {
        Log.d("DetailsActivity", "onConnectionSuspended $p0")
    }

    override fun onConnectionFailed(p0: ConnectionResult)
    {
        Log.d("DetailsActivity", "onConnectionFailed $p0")
    }

//////////////////// map close
}



