package com.codekul.androd1012kotlin

import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onBtn(view: View) {

        val dt: MyData? = null
        dt?.toString()

        val geo = Geocoder(this)
        //val addresses = geo.getFromLocationName(etPlace.text.toString(), 5)
        val addresses = geo.getFromLocation(
                18.73,
                72.23,
                5
        )

        addresses.forEach {
            Log.i("@codekul", """
                    Country name ${it.countryName},
                    Latitude  ${it.latitude},
                    Longitude ${it.longitude}
                """)
        }
    }
}
