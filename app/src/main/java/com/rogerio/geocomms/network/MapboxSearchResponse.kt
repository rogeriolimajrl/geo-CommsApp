package com.rogerio.geocomms.network

data class MapboxSearchResponse(
    val features: List<MapboxFeature>
)

data class MapboxFeature(
    val place_name: String,
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<Double> // [lon, lat]
)
