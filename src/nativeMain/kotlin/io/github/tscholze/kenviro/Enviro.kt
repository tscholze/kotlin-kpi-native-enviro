package io.github.tscholze.kenviro

import io.github.tscholze.kenviro.bmp280.BMP280
import io.github.tscholze.kenviro.led.LEDs
import io.ktgp.Gpio
import io.ktgp.I2c
import io.ktgp.use
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Enviro {
    fun run() = runBlocking {
        launch {
            val actions = MutableSharedFlow<Command>()


            Gpio().use { gpio ->
                val leds = LEDs(gpio, actions)
                leds.turnOff()


                //  Create GPIO context with auto-release after use
                I2c(1).use { i2c ->

                    // Create flow to collect and emit requested actions

                    //  manage
                    val bmp280 = BMP280(i2c, actions)
                    println("Temp: ${bmp280.readTemperature()} C")
                    println("Pressure: ${bmp280.readPressure()} hPa")
                    println("Altitude: ${bmp280.readAltitude()} m NN")

                    bmp280.close()
                }
            }
            println("---")
        }
    }
}