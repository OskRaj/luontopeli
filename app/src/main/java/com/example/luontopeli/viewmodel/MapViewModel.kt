package com.example.luontopeli.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

/**
 * ViewModel karttanäkymälle (MapScreen).
 * Hallinnoi sijaintiseurantaa, reittipisteita ja luontolöytöjen näyttämistä kartalla.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val natureSpotRepository: NatureSpotRepository
) : ViewModel() {

    /** Kävelyn aikana kerätyt reittipisteet (GeoPoint-lista) kartalle piirtämistä varten */
    val routePoints: StateFlow<List<GeoPoint>> = locationManager.routePoints
    /** Nykyinen GPS-sijainti */
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation

    /** Kartalla näytettävät luontolöydöt joilla on validi sijainti */
    private val _natureSpots = MutableStateFlow<List<NatureSpot>>(emptyList())
    val natureSpots: StateFlow<List<NatureSpot>> = _natureSpots.asStateFlow()

    init {
        loadNatureSpots()
    }

    /** Käynnistää GPS-sijainnin seurannan. */
    fun startTracking() = locationManager.startTracking()
    /** Pysäyttää GPS-sijainnin seurannan. */
    fun stopTracking() = locationManager.stopTracking()
    /** Tyhjentää kaikki kerätyt reittipisteet. */
    fun resetRoute() = locationManager.resetRoute()

    private fun loadNatureSpots() {
        viewModelScope.launch {
            natureSpotRepository.spotsWithLocation.collect { spots ->
                _natureSpots.value = spots
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopTracking()
    }
}

/**
 * Laajennusfunktio Long-aikaleiman muuntamiseen luettavaan päivämäärämuotoon.
 *
 * @return Muotoiltu päivämäärä (esim. "11.03.2026 14:30")
 */
fun Long.toFormattedDate(): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}
