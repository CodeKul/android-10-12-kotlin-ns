package com.codekul.mapmodule

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val sydney = LatLng(-34.0, 151.0)
        val nearSydeny = LatLng(35.25, 152.3)
        mMap.addMarker(
                MarkerOptions()
                        .position(sydney)
                        .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.addMarker(
                MarkerOptions()
                        .position(nearSydeny)
                        .title("Some thing")
        )

        mMap.addPolyline(
                PolylineOptions()
                        .add(sydney, nearSydeny)
                        .color(Color.RED)
                        .width(2.3f)
        )

        mMap.setOnMapClickListener {
            mMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(it.latitude, it.longitude))
                            .title("Some thing")
            )
        }

        mMap.addMarker(
                MarkerOptions()
                        .position(LatLng(18.73, 72.56))
                        .title("Some thing")
        )

        mMap.addCircle(
                CircleOptions().center(
                        LatLng(18.73, 72.56)
                ).fillColor(Color.RED).radius(30.0)
        )


    }

    fun onOkay(view: View?) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(18.73, 72.56), 9f))
    }
}
