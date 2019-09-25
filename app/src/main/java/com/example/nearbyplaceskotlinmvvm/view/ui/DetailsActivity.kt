package com.example.nearbyplaceskotlinmvvm.view.ui

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.beust.klaxon.*
import com.example.nearbyplaceskotlinmvvm.R
import com.example.nearbyplaceskotlinmvvm.databinding.ActivityDetailsBinding
import com.example.nearbyplaceskotlinmvvm.service.repository.LatLngInterPolator
import com.example.nearbyplaceskotlinmvvm.view.animations.MarkerAnimation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.Builder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.lang.StringBuilder
import java.net.URL
import java.util.*
import kotlin.math.roundToInt


open class DetailsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var name: String? = null
    private var hours: String? = null
    private var rating: String? = null
    private var totalRating: String? = null
    private var address: String? = null
    private var UPDATE_INTERVALE: Long = 1000
    private var currentLattitude: Double? = null
    private var currentLongitude: Double? = null
    private var lat: Double? = null
    private var long: Double? = null
    private var activityDetailsBinding: ActivityDetailsBinding? = null
    private var location: Location? = null
    open internal var map: GoogleMap? = null
    internal var AddressLocatiomarker: Marker? = null
    internal var googleApiClient: GoogleApiClient? = null
    internal var MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1
    var locationRequest: LocationRequest ?= null
    var fusedLocationProviderClient: FusedLocationProviderClient ?= null
    private var CurrentlatLng: LatLng? = null
    var geocoder: Geocoder? = null
    private var MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    var bitmapdraw: BitmapDrawable ?= null
    var currentLocationMarker: Marker ?= null
    var latLngCar:LatLng ?= null
    var builder = LatLngBounds.Builder()
    var smallMarker:Bitmap ?= null
    var vehicle = ""
    var currentLocation:Location ?= null
    var latLng:LatLng ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_details)

        activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        hours = intent.getStringExtra("hours")
        address = intent.getStringExtra("address")
        totalRating = intent.getStringExtra("totalRating")
        lat = intent.getDoubleExtra("lat", 1.0)
        long = intent.getDoubleExtra("long", 1.0)
        showData()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment!!.getMapAsync(this)

        activityDetailsBinding!!.bottomSheet.setOnClickListener { bottomSheetShow() }
    }


    override fun onStart() {
        super.onStart()
        startLocationUpdate()
    }

    private fun startLocationUpdate()
    {
        locationRequest = LocationRequest.create()
        locationRequest!!.run {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            setFastestInterval(UPDATE_INTERVALE)
        }
    }

    ///////////////// show details start
    private fun showData() {
        if (intent.getStringExtra("rating") != null) {
            rating = intent.getStringExtra("rating")
        }
        name = intent.getStringExtra("name")

        try {
            Log.d("DetailsActivity", "name: $name, hours: $hours, rating: $rating")
        } catch (e: Exception) {
            Log.d("DetailsActivity", "Exception: $e")
        }

        activityDetailsBinding!!.name.text = name
        activityDetailsBinding!!.rating.text = rating
        //activityDetailsBinding!!.totalRating.text = totalRating

        if (hours == "open") {
            activityDetailsBinding!!.open.setTextColor(Color.GREEN)
            activityDetailsBinding!!.open.textSize = 18F

        } else {
            activityDetailsBinding!!.open.setTextColor(Color.RED)
        }
        activityDetailsBinding!!.open.text = hours
        activityDetailsBinding!!.address.text = address
    }
/////////////// show details over


   /////////////////////// map
    @RequiresApi(api = VERSION_CODES.M)
    @Override
    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d("DetailsActivity", "onMapReady ")

        map = googleMap

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            try {
                if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    buildGoogleApiClient()
                    map!!.isMyLocationEnabled = true
                    map!!.uiSettings.isMapToolbarEnabled = false
                    map!!.isTrafficEnabled = true
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf<String?>(permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)
                }
            } catch (e: java.lang.Exception) {
                Log.d("DetailsActivity", "onMapReadyException: ")
            }
        } else {
            buildGoogleApiClient()
            map!!.isMyLocationEnabled = true
            map!!.uiSettings.isMapToolbarEnabled = false
            map!!.isTrafficEnabled = true
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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

    override fun onLocationChanged(p0: Location) {
        Log.d("DetailsActivity", "onLocationChanged $p0")

        buildGoogleApiClient()
        map!!.isMyLocationEnabled = true
        map!!.uiSettings.isMapToolbarEnabled = false
        map!!.isTrafficEnabled = true

        latLngCar = LatLng(p0.latitude, p0.longitude)
        Log.d("DetailsActivity", "onLocationChanged, LatLng: $latLngCar!!")

        map!!.clear()
        var bearing = location!!.bearingTo(p0)

        //CurrentCarLocationarker = map!!.addMarker(MarkerOptions().position(latLngCar!!).title("${latLngCar!!}").flat(true).anchor(0.5f,0.5f).rotation(bearing))
        currentLocationMarker= map!!.addMarker(MarkerOptions().position(latLngCar!!).title("${latLngCar!!}").flat(true).anchor(0.5f,0.5f).rotation(bearing))

        //map!!.animateCamera(CameraUpdateFactory.zoomBy(16f))
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCar,16f))

    }

    override fun onResume() {
        super.onResume()

        if (isGooglePlayServicesAvailable())
        {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            startCurrentLocationUpdate()
        }
    }


    private val mLocationCallback = object:LocationCallback()
    {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

             currentLocation=  locationResult.lastLocation

            if (locationResult.lastLocation == null)
                return
            if ( map != null)
            {
                animateCamera(currentLocation!!)
//                firstTimeFlag = false
            }
            showMarker(currentLocation!!)
        }
    }

    private fun startCurrentLocationUpdate()
    {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000

        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                return
            }
        }
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    private fun isGooglePlayServicesAvailable():Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (ConnectionResult.SUCCESS === status)
            return true
        else
        {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(applicationContext, "Please Install google play services to use this application", Toast.LENGTH_LONG).show()
        }
        return false
    }

    private fun animateCamera(@NonNull location:Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)))
    }

    @NonNull
    private fun getCameraPositionWithBearing(latLng:LatLng):CameraPosition {
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    private fun showMarker(@NonNull currentLocation:Location)
    {
        smallMarker = getResizeCarMarker()
        Log.d("DetailsActivity","smallMarker= $smallMarker")

        latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        Log.d("DetailsActivity","Vehicle LatLng: $latLng")

        Log.d("DetailsActivity","Remove Icon3: $currentLocationMarker")
        if (currentLocationMarker == null)
        {
            currentLocationMarker = map!!.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(latLng!!))
            builder.include(currentLocationMarker!!.position)
        }
        else
        {
            var markerAnimation = MarkerAnimation()
            markerAnimation.animateMarker(currentLocationMarker!!,latLng!!, LatLngInterPolator.Spherical())
        }
    }

    private fun getResizeCarMarker():Bitmap {
        val width:Int = resources.getDimension(R.dimen._30sdp).toInt()
        val height:Int = resources.getDimension(R.dimen._40sdp).toInt()

        Log.d("DetailsActivity","$vehicle has been selected")

        bitmapdraw = when(vehicle) {
            "CAR" -> { resources.getDrawable(R.drawable.bluecar) as BitmapDrawable }
            "BIKE" -> { resources.getDrawable(R.drawable.bike_icon_png) as BitmapDrawable }

             else -> { resources.getDrawable(R.drawable.bluecar) as BitmapDrawable }
        }
        val b = bitmapdraw!!.bitmap
        Log.d("DetailsActivity","bitmap: $b")
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        Log.d("DetailsActivity", "onStatusChanged $p0")
    }

    override fun onProviderEnabled(p0: String?) {
        Log.d("DetailsActivity", "onProviderEnabled $p0")
    }

    override fun onProviderDisabled(p0: String?) {
        Log.d("DetailsActivity", "onProviderDisabled $p0")
    }

    override fun onConnected(p0: Bundle?) {
        Log.d("DetailsActivity", "onConnected ${p0.toString()}")
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        ///////////// current Location Marker And address
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

        if (location != null) {
            Log.e("Latitude : ", "" + location!!.latitude.toString() + "\nLongitude : " + location!!.longitude)
            currentLattitude = location!!.latitude
            currentLongitude = location!!.longitude
            Log.d("DetailsActivity", "onConnected: " + location!!.latitude.toString() + "\nLongitude : " + location!!.longitude)
            //val CurrentlatLng = LatLng(currentLattitude!!, currentLongitude!!)
            CurrentlatLng = currentLattitude?.let { currentLongitude?.let { it1 -> LatLng(it, it1) } }!!

            ////////////// getting current location address
            var Address: ArrayList<Address>? = null
            geocoder = Geocoder(this, Locale.getDefault())
            try {
                Address = geocoder!!.getFromLocation(currentLattitude!!, currentLongitude!!, 1) as ArrayList<Address>
            }
            catch (e: IOException) {
                Log.d("DetailsActivity", "" + e)
            }
            Log.d("DetailsACtivity", "Current Address:  Address: $Address!!.get(0).getAddressLine(2)")

            //////////////////////////// Current Location Marker
            //CurrentCarLocationarker = map!!.addMarker(MarkerOptions().position(latLngCar!!).title("${latLngCar!!}"))
              map!!.addMarker(MarkerOptions().position(CurrentlatLng!!).title(Address!![0].getAddressLine(0)))
           // map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentlatLng, 16f))
            Log.d("DetailsActivity", "Current LatLng: $CurrentlatLng")
        }

//////////////////////address marker and title
        var AddressLatLng: LatLng = lat?.let { long?.let { it1 -> LatLng(it, it1) } }!!
        Log.d("DetailsActivity", "Address Bundle: $address")

        AddressLocatiomarker = map!!.addMarker(MarkerOptions().position(AddressLatLng).title("$address"))

//        map!!.addMarker(MarkerOptions().position(AddressLatLng)).title = address
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(AddressLatLng, 16f))
        generatePolyLine(CurrentlatLng!!, AddressLatLng)
        builder.include(AddressLocatiomarker!!.position)

    }

    private fun generatePolyLine(currentlatLng: LatLng, addressLatLng: LatLng) {

        Log.d("DetailsActivity", "CurrentLatLong: $currentlatLng, AddressLatLong: $addressLatLng")
        val options = PolylineOptions()
        options.color(Color.BLUE)
        options.width(12f)

        var url = getDirectionUrl(currentlatLng, addressLatLng)

        async {

            val result = URL(url).readText()
            Log.d("DetailsActivity", "Async UrlResult: $result ")
            val LatLongB = LatLngBounds.Builder()

            uiThread {

                val parser = Parser()
                val stringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>

                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                options.add(currentlatLng)
                LatLongB.include(currentlatLng)

                for (point in polypts) {

                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(addressLatLng)
                LatLongB.include(addressLatLng)
                // build bounds
                // val bounds = LatLongB.build()
                map!!.addPolyline(options)
                map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng,16f))
                //padding
                // show map with route centered
            }
        }
    }


    private fun bottomSheetShow()
    {

        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val dialog = BottomSheetDialog(this)

        view.SelectBike.setOnClickListener {
            currentLocationMarker!!.remove()
            currentLocationMarker=null
            vehicle = "BIKE"
            Log.d("DetailsActivity","$vehicle")
            Toast.makeText(applicationContext,"Bike Selected",Toast.LENGTH_SHORT).show()
            showMarker(currentLocation!!)
            dialog.dismiss()
        }

        view.SelectCar.setOnClickListener {
            currentLocationMarker!!.remove()
            currentLocationMarker=null
            vehicle = "CAR"

            Log.d("DetailsActivity","$vehicle")
            Toast.makeText(applicationContext,"Car Selected",Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            showMarker(currentLocation!!)
        }

        dialog.setContentView(view)
        var height = 250

        Log.d("DetailsActivity","Height"+view.height)
        var latLngBounds: LatLngBounds=  builder.build()

//        var width = resources.displayMetrics.widthPixels - dpToPx(100, applicationContext)
//        //var paddingTop = getHeight()
//        var paddingTop = height
//
//        var padding =  (paddingTop+50) // offset from edges of the map 10% of screen
//        var paddingLeftRight = (width * 0.25).toInt() // offset from edges of the map 10% of screen

        map!!.setPadding(0, 50, 0, height) //(left, top, right, bottom)
        map!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))
        dialog.show()
    }

//    fun dpToPx(dp:Int, context:Context):Int {
//        var metrics =  DisplayMetrics()
//        getWindowManager().getDefaultDisplay().getMetrics(metrics)
//        if (context == null)
//        {
//            return 0
//        }
//        val displayMetrics = context.resources.displayMetrics
//        val px = (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
//        return px
//    }


    private fun getDirectionUrl(from : LatLng, to : LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val api_key = "AIzaSyBH_TExjnT7McUcM-x39Gl0PTDPc7mSiUs"
        val params = "$origin&$dest&$sensor"

        var url = "https://maps.googleapis.com/maps/api/directions/json?$params+&key=$api_key"
        Log.d("DetailsActivity","Url: $url")

        return url
    }

    private fun decodePoly(encoded: String): List<LatLng>
    {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }
// Creating an http connection to communicate with url



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




