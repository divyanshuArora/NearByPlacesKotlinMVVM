package com.example.nearbyplaceskotlinmvvm.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nearbyplaceskotlinmvvm.R
import com.example.nearbyplaceskotlinmvvm.databinding.InfoWindowBinding
import com.example.nearbyplaceskotlinmvvm.service.model.MapData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomWindowAdapter(var context: Context,var mapData: MapData):GoogleMap.InfoWindowAdapter
{

        var infoWindowBinding:InfoWindowBinding ?= null

    override fun getInfoContents(p0: Marker?): View
    {

        //infoWindowBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.google_custom_info_item,false)
        var view: View = View.inflate(context,R.layout.google_custom_info_item,null)
        infoWindowBinding = DataBindingUtil.bind(view)
        infoWindowBinding!!.mapDataModel = mapData
        infoWindowBinding!!.executePendingBindings()

        return infoWindowBinding.run { view }
    }

    override fun getInfoWindow(p0: Marker?): View?
    {
        return null
    }

}