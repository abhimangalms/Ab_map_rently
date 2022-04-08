/*
 * Created by Muhammad Utsman on 31/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/31/18 10:15 PM
 */

package com.abhimangal.map.kotlin

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.abhimangal.map.R
import com.abhimangal.map.java.model.DirectionResponses
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var cochin: LatLng
    private lateinit var coimbatore: LatLng
    private lateinit var idukki: LatLng
    private lateinit var munnar: LatLng
    private lateinit var finalCochin: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cochin = LatLng(9.9312, 76.2673)
        coimbatore = LatLng(11.0168, 76.9558)
        idukki = LatLng(9.9189, 77.1025)
        munnar = LatLng(10.0889, 77.0595)
        finalCochin = LatLng(9.9312, 76.2673)


        val mapFragment = maps_view as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val markerCochin = MarkerOptions()
            .position(cochin)
            .title("Cochin")
        val markerCoimbatore = MarkerOptions()
            .position(coimbatore)
            .title("Coimbatore")
        val markerIdukki = MarkerOptions()
            .position(idukki)
            .title("Idukki")
        val markerMunnar = MarkerOptions()
            .position(munnar)
            .title("Munnar")
        val markerFinalCochin = MarkerOptions()
            .position(finalCochin)
            .title("Final Cochin")

        map.addMarker(markerCochin)
        map.addMarker(markerCoimbatore)
        map.addMarker(markerIdukki)
        map.addMarker(markerMunnar)
        map.addMarker(markerFinalCochin)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coimbatore, 11.6f))

        val fromCochin = cochin.latitude.toString() + "," + cochin.longitude.toString()
        val toCoimbatore = coimbatore.latitude.toString() + "," + coimbatore.longitude.toString()
        val toIdukki = idukki.latitude.toString() + "," + idukki.longitude.toString()
        val toMunnar = munnar.latitude.toString() + "," + munnar.longitude.toString()
        val toFinalCochin = finalCochin.latitude.toString() + "," + finalCochin.longitude.toString()

        val apiServices = RetrofitClient.apiServices(this)
        apiServices.getDirection(fromCochin, toCoimbatore, getString(R.string.api_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    drawPolyline(response)
                    Log.d("", response.message())
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("", t.localizedMessage)
                }
            })

        apiServices.getDirection(toCoimbatore, toIdukki, getString(R.string.api_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    drawPolyline(response)
                    Log.d("", response.message())
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("", t.localizedMessage)
                }
            })

        apiServices.getDirection(toIdukki, toMunnar, getString(R.string.api_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    drawPolyline(response)
                    Log.d("", response.message())
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("", t.localizedMessage)
                }
            })
        apiServices.getDirection(toMunnar, toFinalCochin, getString(R.string.api_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>
                ) {
                    drawPolyline(response)
                    Log.d("", response.message())
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("", t.localizedMessage)
                }
            })

    }

    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        map.addPolyline(polyline)
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("key") apiKey: String
        ): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(context: Context): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.resources.getString(R.string.base_url))
                .build()

            return retrofit.create<ApiServices>(ApiServices::class.java)
        }
    }
}
