package com.example.nearbyplaceskotlinmvvm

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nearbyplaceskotlinmvvm.databinding.ActivityMainBinding
import com.example.nearbyplaceskotlinmvvm.service.model.NearByPlaceModel
import com.example.nearbyplaceskotlinmvvm.view.adapter.NearByPlacesAdapter
import com.example.nearbyplaceskotlinmvvm.view.ui.RealTimeMap
import com.example.nearbyplaceskotlinmvvm.viewmodel.NearByPlacesViewModel
import org.jetbrains.anko.startActivity
import java.lang.Exception

class MainActivity : AppCompatActivity(){



    var activityMainBinding: ActivityMainBinding ?= null
    var nearByPlacesViewModel: NearByPlacesViewModel ?= null

    var current_location: String ?= null
    var keyword: String ?= null
    val radious = "2000"
    val key = "AIzaSyBH_TExjnT7McUcM-x39Gl0PTDPc7mSiUs"

    var current_lat: Double ?= null
    var current_long: Double ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
         nearByPlacesViewModel = ViewModelProviders.of(this).get(NearByPlacesViewModel::class.java)

        getMyLatLong()



        activityMainBinding!!.Hospital.setOnClickListener {
            getPlaces("hospital")
        }


        activityMainBinding!!.Restaurent.setOnClickListener { getPlaces("restaurant") }

        activityMainBinding!!.School.setOnClickListener { getPlaces("school") }
        activityMainBinding!!.liveMap.setOnClickListener {

            startActivity<RealTimeMap>()
        }



    }



    @SuppressLint("MissingPermission")
    private fun getMyLatLong()
    {

        var locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        current_lat = location.latitude
        current_long = location.longitude

        current_location = "$current_lat,$current_long"
    }



    private fun getPlaces(keyword: String)
    {
        try {
            nearByPlacesViewModel!!.getNearByPlacs(current_location, radious, keyword, key).observe(this, Observer {

                    activityMainBinding!!.setVariable(BR.MainNearByPlacesRespopnse, it)
                    activityMainBinding!!.nearByPlacesRecycle.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                    val nearByPlaceAdapterType = NearByPlacesAdapter(this, it.getResults() as List<NearByPlaceModel>)
                    activityMainBinding!!.nearByPlacesRecycle.adapter = nearByPlaceAdapterType

                })
        }
        catch (e: Exception)
        {
            Log.d("MainActivity","Exception: "+e)
        }



    }








}
