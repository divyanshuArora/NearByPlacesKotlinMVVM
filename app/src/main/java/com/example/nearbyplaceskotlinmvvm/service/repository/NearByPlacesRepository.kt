package com.example.nearbyplaceskotlinmvvm.service.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NearByPlacesRepository
{


    companion object Factory
    {
            fun getInstance():NearByPlacesRepository
            {
                return NearByPlacesRepository()
            }
    }



    fun getNearByPlacesList(location:String?,radious: String,keyword: String,key: String): LiveData<NearByPlacesRespopnse>
    {
        var data = MutableLiveData<NearByPlacesRespopnse>()

        ApiInterface.create().GetNearByPlaces(location,radious,keyword,key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                data.value = it
                Log.d("NearByPlacesRepository","successRepository"+it.getResults()+"status: "+it.getStatus())
            },
                {
                    Log.e("NearByPlacesRepository","failureRepository")
                })

        return data
    }






}