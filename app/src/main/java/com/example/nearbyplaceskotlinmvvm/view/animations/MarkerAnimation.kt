package com.example.nearbyplaceskotlinmvvm.view.animations

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Property
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.example.nearbyplaceskotlinmvvm.service.repository.LatLngInterPolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


open class MarkerAnimation
{

     fun animateMarker(marker: Marker,finalPosition: LatLng,latlngInterpolator: LatLngInterPolator )
    {

        val startPosition = marker.getPosition()
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 3000f


        handler.post(object:Runnable {
            internal var elapsed:Long = 0
            internal var t:Float = 0.toFloat()
            internal var v:Float = 0.toFloat()
            public override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                marker.position = latlngInterpolator.interpolate(v, startPosition, finalPosition)
                // Repeat till progress is complete.
                if (t < 1)
                {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }




//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    fun animateMarkerToHC(marker:Marker, finalPosition:LatLng, latLngInterpolator:LatLngInterPolator) {
//        val startPosition = marker.getPosition()
//        val valueAnimator = ValueAnimator()
//        valueAnimator.addUpdateListener(object:ValueAnimator.AnimatorUpdateListener
//        {
//            override fun onAnimationUpdate(animation: ValueAnimator) {
//                val v = animation.getAnimatedFraction()
//                val newPosition = latLngInterpolator.interpolate(v, startPosition, finalPosition)
//                marker.setPosition(newPosition)
//            }
//        })
//        valueAnimator.setFloatValues(0F, 1F) // Ignored.
//        valueAnimator.setDuration(3000)
//        valueAnimator.start()
//    }


//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    internal fun animateMarkerToICS(marker: Marker, finalPosition: LatLng, latLngInterpolator: LatLngInterPolator)
//    {
//        val typeEvaluator =
//            TypeEvaluator<LatLng?> { fraction, startValue, endValue ->
//                latLngInterpolator.interpolate(fraction, startValue!!, endValue!!)
//            }
//        val property: Property<Marker, LatLng> = Property.of(Marker::class.java, LatLng::class.java, "position")
//        val animator: ObjectAnimator = ObjectAnimator.ofObject(
//            marker,
//            property,
//            typeEvaluator,
//            finalPosition
//        )
//        animator.duration = 3000
//        animator.start()
//    }








}