package com.rogerio.geocomms

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rogerio.geocomms.network.RetrofitClient

data class SimplePlace(val id: String, val title: String, val lat: Double, val lon: Double)

class MapViewModel : ViewModel() {

    private var cameraCenteredOnce = false
    private var mapView: MapView? = null
    private var circleManager: CircleAnnotationManager? = null

    // user location (default fallback)
    private var userLat: Double = -23.561414
    private var userLon: Double = -46.656678

    val tokenMapbox = "pk.eyJ1Ijoicm9nZXJpb2xpbWFqcmwiLCJhIjoiY21pOTYyempmMGhibDJpcHhud3o1Mnp5NSJ9.K6qGGtxbMrz0DKYmsv-ktA"

    fun createMapView(context: Context): MapView {
        val mapView = MapView(context)

        mapView.mapboxMap.loadStyle(style(Style.MAPBOX_STREETS) {
            projection(ProjectionName.MERCATOR)
        })

        val locationComponent = mapView.location



        locationComponent.updateSettings {
            enabled = true              // ativa componente
            pulsingEnabled = true       // animação ao redor do ponto azul
        }

        // Listener para capturar localização automática
        locationComponent.addOnIndicatorPositionChangedListener { point ->

            if (!cameraCenteredOnce) {
                cameraCenteredOnce = true

                userLat = point.latitude()
                userLon = point.longitude()

                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(point)
                        .zoom(14.0)
                        .build()
                )
            }
        }

        this.mapView = mapView

        return mapView
    }

    // Add a simple circle marker (no images needed)
    fun addMarker(lat: Double, lon: Double, title: String? = null) {
        val mv = mapView ?: return
        // ensure manager exists
        if (circleManager == null) {
            circleManager = mv.annotations.createCircleAnnotationManager()
        }
        // create on main thread (MapView methods safe here)
        val point = Point.fromLngLat(lon, lat)
        val options = CircleAnnotationOptions()
            .withPoint(point)
            .withCircleRadius(10.0)
            .withCircleColor("#ff0000")
        circleManager?.create(options)
    }

    // clear existing circle markers
    fun clearMarkers() {
        circleManager?.deleteAll()
    }

    // Handle voice result -> run mock search around userLat/userLon and add markers
    fun onVoiceResult(rawText: String) {
        val query = rawText.lowercase().trim()
        viewModelScope.launch {

            withContext(Dispatchers.Main) {
                clearMarkers()
            }
            // simulate network / geocoding delay
            delay(400)
            // get mock results
            val results = searchPlacesMock(query, userLat, userLon)
            withContext(Dispatchers.Main) {
                if (results.isNotEmpty()) {
                    // add all markers
                    results.forEach { place ->
                        addMarker(place.lat, place.lon, place.title)
                    }
                    // center camera to first result with a closer zoom
                    val first = results.first()
                    mapView?.getMapboxMap()
                        ?.setCamera(CameraOptions.Builder().center(Point.fromLngLat(first.lon, first.lat)).zoom(15.0).build())
                } else {
                    Log.e("MapViewModel","No results" )
                }
            }
        }
    }

    // Real Places search
    suspend fun searchRealPlaces(query: String) {

        clearMarkers()
        delay(400)

        val lat = userLat ?: return
        val lon = userLon ?: return

        var lonRes = 0.0
        var latRes = 0.0

        val proximity = "$lon,$lat"

        try {
            val response = RetrofitClient.api.searchPlaces(
                query = query,
                proximity = proximity,
                token = tokenMapbox
            )

            response.features.forEach { feature ->
                lonRes = feature.geometry.coordinates[0]
                latRes = feature.geometry.coordinates[1]
                addMarker(latRes, lonRes, feature.place_name)
            }


            mapView?.getMapboxMap()
                ?.setCamera(CameraOptions.Builder().center(Point.fromLngLat(lonRes, latRes)).zoom(15.0).build())

        } catch (e: Exception) {
            Log.e("MapViewModel", "Search error: ${e.message}")
        }
    }


    // Mock search: produce a few nearby points for known keywords
    private fun searchPlacesMock(query: String, centerLat: Double, centerLon: Double): List<SimplePlace> {
        val places = mutableListOf<SimplePlace>()
        // keywords and sample generation
        when {
            "hospital" in query || "hospital" == query -> {
                places += generateNearby(centerLat, centerLon, "Hospital Central", 0.003, 0.002)
                places += generateNearby(centerLat, centerLon, "Clínica São Paulo", -0.002, 0.004)
            }
            "parque" in query || "park" in query || "parque" == query -> {
                places += generateNearby(centerLat, centerLon, "Parque das Flores", 0.004, -0.003)
                places += generateNearby(centerLat, centerLon, "Parque Central", -0.003, -0.002)
            }
            "escola" in query || "school" in query -> {
                places += generateNearby(centerLat, centerLon, "Escola Estadual A", 0.002, 0.002)
                places += generateNearby(centerLat, centerLon, "Colégio B", -0.002, 0.001)
                places += generateNearby(centerLat, centerLon, "Creche C", 0.0015, -0.0025)
            }
            // fallback: if single-word noun, generate a few random nearby places with that label
            else -> {
                if (query.isNotBlank()) {
                    places += generateNearby(centerLat, centerLon, "${query.replaceFirstChar{ it.uppercase() }} 1", 0.002, 0.001)
                    places += generateNearby(centerLat, centerLon, "${query.replaceFirstChar{ it.uppercase() }} 2", -0.0015, -0.002)
                }
            }
        }
        return places
    }

    // Helper: create a SimplePlace near center by deltaLat and deltaLon
    private fun generateNearby(centerLat: Double, centerLon: Double, title: String, dLat: Double, dLon: Double): SimplePlace {
        return SimplePlace(
            id = title + System.currentTimeMillis().toString(),
            title = title,
            lat = centerLat + dLat,
            lon = centerLon + dLon
        )
    }
}
