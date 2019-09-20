package com.example.nearbyplaceskotlinmvvm.service.repository

import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse
import io.reactivex.Observable
import io.reactivex.Observer
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface
{


    @GET("api/place/nearbysearch/json?")
    fun GetNearByPlaces(
        @Query("location") LatLong: String?,
        @Query("radius") radius: String?,
        @Query("keyword") keyword: String?,
        @Query("key") key: String?
    ): Observable<NearByPlacesRespopnse>


    companion object Factory
    {
            fun create():ApiInterface
            {

                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://maps.googleapis.com/maps/")
                    .build()

                return retrofit.create(ApiInterface::class.java)
            }
    }









}