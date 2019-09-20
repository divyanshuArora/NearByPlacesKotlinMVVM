package com.example.nearbyplaceskotlinmvvm.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyplaceskotlinmvvm.R
import com.example.nearbyplaceskotlinmvvm.databinding.PlacesItemBinding
import com.example.nearbyplaceskotlinmvvm.service.model.NearByPlaceModel
import com.example.nearbyplaceskotlinmvvm.view.ui.DetailsActivity
import org.jetbrains.anko.startActivity

class NearByPlacesAdapter(var context: Context,list: List<NearByPlaceModel>): RecyclerView.Adapter<NearByPlacesAdapter.ItemViewHolder>() {



    var NearByPlaceslist = list


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearByPlacesAdapter.ItemViewHolder
    {

        val placesItemBinding: PlacesItemBinding

        placesItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.near_by_places_item,parent,false)

        return ItemViewHolder(placesItemBinding)

    }

    override fun getItemCount(): Int
    {
        return NearByPlaceslist.size
        Log.d("Adapter","listSize: "+NearByPlaceslist.size)

//        var intent = Intent(context,DetailsActivity::class.java)
//        intent.getStringArrayExtra("")

    }

    override fun onBindViewHolder(holder: NearByPlacesAdapter.ItemViewHolder, position: Int)
    {
        holder.bind(NearByPlaceslist.get(position),position)
        holder.placesItemBinding.item.setOnClickListener {
            Toast.makeText(context, "Name: " + NearByPlaceslist.get(position).name, Toast.LENGTH_SHORT).show()
            var hours: String? = null

            Log.d("NearByPlacesAdapter", "name: " + NearByPlaceslist.get(position).name)

            if (NearByPlaceslist.get(position).openingHours != null) {

                if (NearByPlaceslist.get(position).openingHours!!.openNow == "true") {
                    hours = "open"
                } else {
                    hours = "close"
                }
            }

                context.startActivity<DetailsActivity>(
                    "name" to NearByPlaceslist[position].name,
                    "hours" to hours,
                    "rating" to NearByPlaceslist[position].rating,
                    "totalRating" to NearByPlaceslist[position].userRatingsTotal,
                    "address" to NearByPlaceslist[position].vicinity,
                    "lat" to NearByPlaceslist[position].geometry!!.location!!.lat,
                    "long" to NearByPlaceslist[position].geometry!!.location!!.lng
                 )

            }

    }


    class ItemViewHolder(val placesItemBinding: PlacesItemBinding) : RecyclerView.ViewHolder(placesItemBinding.root)
    {
        fun bind(item: NearByPlaceModel,position: Int)
        {
            placesItemBinding.nearByViewModel = item
            placesItemBinding.executePendingBindings()
        }
    }




}