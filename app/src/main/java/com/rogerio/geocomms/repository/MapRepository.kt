package com.rogerio.geocomms.repository

data class Place(val id: String, val name: String, val lat: Double, val lon: Double)

class MapRepository {
    suspend fun searchPlacesMock(query: String): List<Place> {
        val q = query.lowercase()
        return when {
            "hospital" in q || "hospit" in q -> listOf(
                Place("h1", "Hospital Central", -23.561414, -46.656678),
                Place("h2", "Clínica São Paulo", -23.559000, -46.653000)
            )
            "parque" in q || "park" in q -> listOf(
                Place("p1", "Parque A", -23.557000, -46.660000),
                Place("p2", "Parque B", -23.553000, -46.658500)
            )
            else -> emptyList()
        }
    }
}
