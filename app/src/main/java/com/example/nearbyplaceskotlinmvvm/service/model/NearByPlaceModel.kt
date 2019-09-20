package com.example.nearbyplaceskotlinmvvm.service.model

import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NearByPlaceModel
{

        @SerializedName("geometry")
        @Expose
        var geometry: Geometry? = null
        @SerializedName("icon")
        @Expose
        var icon: String? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("opening_hours")
        @Expose
        var openingHours: OpeningHours? = null
        @SerializedName("photos")
        @Expose
        var photos: List<Photo>? = null
        @SerializedName("place_id")
        @Expose
        var placeId: String? = null
        @SerializedName("plus_code")
        @Expose
        var plusCode: PlusCode ?= null
        @SerializedName("rating")
        @Expose
        var rating: String? = null
        @SerializedName("reference")
        @Expose
        var reference: String? = null
        @SerializedName("scope")
        @Expose
        var scope: String? = null
        @SerializedName("types")
        @Expose
        var types: List<String>? = null
        @SerializedName("user_ratings_total")
        @Expose
        var userRatingsTotal: String? = null
        @SerializedName("vicinity")
        @Expose
        var vicinity: String? = null
        @SerializedName("price_level")
        @Expose
        var priceLevel: Int? = null




        class OpeningHours {
                @SerializedName("open_now")
                @Expose
                var openNow: String? = null

        }

        class PlusCode {
                @SerializedName("compound_code")
                @Expose
                var compoundCode: String? = null
                @SerializedName("global_code")
                @Expose
                var globalCode: String? = null

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


        class Geometry {
                @SerializedName("location")
                @Expose
                var location: Location? = null
                @SerializedName("viewport")
                @Expose
                var viewport: NearByPlacesRespopnse.Viewport? = null

        }

        class Location {
                @SerializedName("lat")
                @Expose
                var lat: Double? = null
                @SerializedName("lng")
                @Expose
                var lng: Double? = null

        }



}


