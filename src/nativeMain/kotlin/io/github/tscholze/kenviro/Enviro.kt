package io.github.tscholze.kenviro

import io.github.tscholze.kenviro.bmp280.BMP280
import io.ktgp.I2c
import io.ktgp.use
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Enviro {
    fun run() = runBlocking {
        launch {
            //  Create GPIO context with auto-release after use
            I2c(1).use { i2c ->

                // Create flow to collect and emit requested actions
                val actions = MutableSharedFlow<Command>()

                //  manage
                val bmp280 = BMP280(i2c, actions)
                println("Temp: " + bmp280.readTemperature())
                println("Pressure: " + bmp280.readPressure())
                bmp280.close()
            }

            println("---")
        }
    }
}