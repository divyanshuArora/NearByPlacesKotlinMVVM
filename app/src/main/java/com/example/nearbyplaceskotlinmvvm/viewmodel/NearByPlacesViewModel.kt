package com.example.nearbyplaceskotlinmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.nearbyplaceskotlinmvvm.service.repository.NearByPlacesRepository
import com.example.nearbyplaceskotlinmvvm.service.response.NearByPlacesRespopnse
import java.nio.channels.spi.AbstractSelectionKey

class NearByPlacesViewModel(application: Application): AndroidViewModel(application)
{
    fun getNearByPlacs(location:String?,radious: String,keyword: String,key: String):LiveData<NearByPlacesRespopnse>
    {

        return NearByPlacesRepository.getInstance().getNearByPlacesList(location,radious,keyword,key)
    }
}