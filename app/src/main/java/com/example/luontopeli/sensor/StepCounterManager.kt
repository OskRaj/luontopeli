package com.example.luontopeli.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sensorien hallintapalvelu askelmittarille ja gyroskoopille.
 */
@Singleton
class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Käytetään ensisijaisesti DETECTORia, mutta tallennetaan myös COUNTER varmuuden vuoksi
    private val detectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val counterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var stepListener: SensorEventListener? = null
    private var initialSteps = -1

    fun startStepCounting(onStep: () -> Unit) {
        stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    onStep()
                } else if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    // Jos DETECTOR ei toimi, käytetään COUNTERia erotuksen laskemiseen
                    val totalSteps = event.values[0].toInt()
                    if (initialSteps == -1) {
                        initialSteps = totalSteps
                    } else {
                        // Tämä on hieman epätarkempi live-päivitykseen, mutta parempi kuin ei mitään
                        onStep() 
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        // Rekisteröidään molemmat, jotta mahdollisimman moni laite toimii
        detectorSensor?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        counterSensor?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopStepCounting() {
        stepListener?.let { sensorManager.unregisterListener(it) }
        stepListener = null
        initialSteps = -1
    }

    fun stopAll() {
        stopStepCounting()
    }

    companion object {
        const val STEP_LENGTH_METERS = 0.74f
    }
}
