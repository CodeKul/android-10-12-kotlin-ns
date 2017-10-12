package com.codekul.mylocation

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.content.ContentValues.TAG
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.location.LocationSettingsRequest


class MainActivity : Activity(), LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    /**
     * 1) Android Location API
     * 2) Fused Location Api provider
     *
     * */

    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.allProviders.forEach {
            Log.i("@codekul", """ $it """)
        }

        //myLastLoc(locationManager)

        if (isPlayServicesAvailable(this)) {
            buildGoogleApiClient()
        }
        else {
            Log.i("@codekul", "Play Services are no available")
            return
        }
    }

    fun myLastLoc(locationManager: LocationManager) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                AlertDialog.Builder(this).setPositiveButton("yes", {
                    di, which ->
                    ActivityCompat.requestPermissions(this,
                            arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            1234)
                }).setNegativeButton("no", {
                    di, which ->
                    di.dismiss()
                }).create().show()


            } else {

                ActivityCompat.requestPermissions(this,
                        arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        1234)
            }
        } else {
            val loc: Location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1234) {
            if (grantResults?.get(0) == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                myLastLoc(getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            }
        }
    }

    private fun buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient")
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        createLocationRequest()
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onLocationChanged(loc: Location?) {
        Log.i("@codekul", """" Lat - ${loc?.latitude} Lng - ${loc?.longitude}""")
    }

    override fun onConnected(p0: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    fun isPlayServicesAvailable(context: Context): Boolean {
        // Google Play Service APKが有効かどうかチェックする
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(context as Activity, resultCode, 2).show()
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    public override fun onResume() {
        super.onResume()
        isPlayServicesAvailable(this)


        if (mGoogleApiClient.isConnected) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected) {
            stopLocationUpdates()
        }
    }

    override fun onStop() {
        stopLocationUpdates()
        mGoogleApiClient.disconnect()

        super.onStop()
    }

    private fun startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates")

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
        // 現在位置の取得の前に位置情報の設定が有効になっているか確認する
        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status

            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    // 設定が有効になっているので現在位置を取得する
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this@MainActivity)
                    }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // 設定が有効になっていないのでダイアログを表示する
                    try {
                        status.startResolutionForResult(this@MainActivity, 1234)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
        }
    }

     private fun stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates");
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

}
