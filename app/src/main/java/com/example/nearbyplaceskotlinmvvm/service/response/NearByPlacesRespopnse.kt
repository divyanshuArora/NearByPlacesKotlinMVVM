package com.example.nearbyplaceskotlinmvvm.service.response

import com.example.nearbyplaceskotlinmvvm.service.model.NearByPlaceModel
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class NearByPlacesRespopnse
{
    @SerializedName("html_attributions")
    @Expose
    private var htmlAttributions: List<Any>? = null
    @SerializedName("next_page_token")
    @Expose
    private var nextPageToken: String? = null
    @SerializedName("results")
    @Expose
    private var results: List<NearByPlaceModel>? = null
    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getHtmlAttributions(): List<Any>? {
        return htmlAttributions
    }

    fun setHtmlAttributions(htmlAttributions: List<Any>?) {
        this.htmlAttributions = htmlAttributions
    }

    fun getNextPageToken(): String? {
        return nextPageToken
    }

    fun setNextPageToken(nextPageToken: String?) {
        this.nextPageToken = nextPageToken
    }

    fun getResults(): List<NearByPlaceModel>? {
        return results
    }

    fun setResults(results: List<NearByPlaceModel>?) {
        this.results = results
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }


//    class Geometry {
//        @SerializedName("location")
//        @Expose
//        var location: Location? = null
//        @SerializedName("viewport")
//        @Expose
//        var viewport: Viewport? = null
//
//    }
//
//    class Location {
//        @SerializedName("lat")
//        @Expose
//        var lat: Double? = null
//        @SerializedName("lng")
//        @Expose
//        var lng: Double? = null
//
//    }


    class Northeast {
        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null

    }





    class Photo {
        @SerializedName("height")
        @Expose
        var height: Int? = null
        @SerializedName("html_attributions")
        @Expose
        var htmlAttributions: List<String>? = null
        @SerializedName("photo_reference")
        @Expose
        var photoReference: String? = null
        @SerializedName("width")
        @Expose
        var width: Int? = null

    }




    class Southwest {
        @SerializedName("lat")
        @Expose
        var lat: Double? = null
        @SerializedName("lng")
        @Expose
        var lng: Double? = null

    }


    class Viewport {
        @SerializedName("northeast")
        @Expose
        var northeast: Northeast? = null
        @SerializedName("southwest")
        @Expose
        var southwest: Southwest? = null

    }
}