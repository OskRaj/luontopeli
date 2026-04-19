package com.example.luontopeli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.WalkSession
import com.example.luontopeli.data.repository.WalkRepository
import com.example.luontopeli.location.LocationManager
import com.example.luontopeli.sensor.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val stepCounterManager: StepCounterManager,
    private val locationManager: LocationManager,
    private val walkRepository: WalkRepository
) : ViewModel() {

    private val _currentSession = MutableStateFlow<WalkSession?>(null)
    val currentSession: StateFlow<WalkSession?> = _currentSession.asStateFlow()

    private val _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking.asStateFlow()

    // Reaaliaikainen kesto merkkijonona
    private val _durationText = MutableStateFlow("0s")
    val durationText: StateFlow<String> = _durationText.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Seurataan sijaintia ja päivitetään matka heti kun uusia pisteitä tulee
        viewModelScope.launch {
            locationManager.routePoints.collect { points ->
                if (_isWalking.value) {
                    val gpsDistance = locationManager.calculateTotalDistance()
                    _currentSession.update { it?.copy(distanceMeters = gpsDistance) }
                }
            }
        }
    }

    fun startWalk() {
        if (_isWalking.value) return
        
        val session = WalkSession(startTime = System.currentTimeMillis())
        _currentSession.value = session
        _isWalking.value = true
        locationManager.resetRoute()

        // Käynnistä askeleet
        stepCounterManager.startStepCounting {
            _currentSession.update { current ->
                current?.copy(stepCount = (current?.stepCount ?: 0) + 1)
            }
        }

        // Käynnistä reaaliaikainen sekuntikello
        startTimer(session.startTime)
    }

    private fun startTimer(startTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isWalking.value) {
                _durationText.value = formatDuration(startTime)
                delay(1000) // Päivitys kerran sekunnissa
            }
        }
    }

    fun stopWalk() {
        if (!_isWalking.value) return
        
        _isWalking.value = false
        timerJob?.cancel()
        stepCounterManager.stopStepCounting()

        val finalSession = _currentSession.value?.copy(
            endTime = System.currentTimeMillis(),
            isActive = false
        )
        _currentSession.value = finalSession

        viewModelScope.launch {
            finalSession?.let { walkRepository.insertSession(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopAll()
        timerJob?.cancel()
    }
}

fun formatDistance(meters: Float): String {
    return if (meters < 1000f) "${meters.toInt()} m"
    else "${"%.1f".format(meters / 1000f)} km"
}

fun formatDuration(startTime: Long, endTime: Long = System.currentTimeMillis()): String {
    val seconds = (endTime - startTime) / 1000
    if (seconds < 0) return "0s"
    val minutes = seconds / 60
    val hours = minutes / 60
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}min"
        minutes > 0 -> "${minutes}min ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
