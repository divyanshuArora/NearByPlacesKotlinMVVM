package com.example.nearbyplaceskotlinmvvm.service.model

import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse.Northeast
import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse.Southwest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MapModel
{


    @SerializedName("northeast")
    @Expose
    private var northeast: Northeast? =
        null
    @SerializedName("southwest")
    @Expose
    private var southwest: Southwest? =
        null

    fun getNortheast(): Northeast? {
        return northeast
    }

    fun setNortheast(northeast: Northeast?) {
        this.northeast = northeast
    }

    fun getSouthwest(): Southwest? {
        return southwest
    }

    fun setSouthwest(southwest: Southwest?) {
        this.southwest = southwest
    }


    class Distance {
        @SerializedName("text")
        @Expose
        var text: String? = null
        @SerializedName("value")
        @Expose
        var value: Int? = null

    }






}