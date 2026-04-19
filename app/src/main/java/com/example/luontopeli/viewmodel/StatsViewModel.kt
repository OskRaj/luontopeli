package com.example.luontopeli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.WalkSession
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.data.repository.WalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel tilastonäkymälle (StatsScreen).
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val walkRepository: WalkRepository,
    private val natureSpotRepository: NatureSpotRepository
) : ViewModel() {

    private val _allSessions = MutableStateFlow<List<WalkSession>>(emptyList())
    val allSessions: StateFlow<List<WalkSession>> = _allSessions.asStateFlow()

    private val _totalSpots = MutableStateFlow(0)
    val totalSpots: StateFlow<Int> = _totalSpots.asStateFlow()

    init {
        viewModelScope.launch {
            walkRepository.allSessions.collect { sessions ->
                _allSessions.value = sessions
            }
        }
        viewModelScope.launch {
            natureSpotRepository.allSpots.collect { spots ->
                _totalSpots.value = spots.size
            }
        }
    }
}
