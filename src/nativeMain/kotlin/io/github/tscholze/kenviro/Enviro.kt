package io.github.tscholze.kenviro

import io.github.tscholze.kenviro.bmp280.BMP280
import io.github.tscholze.kenviro.led.LEDs
import io.github.tscholze.kenviro.server.runServer
import io.github.tscholze.kenviro.tcs3472.TCS3472
import io.ktgp.Gpio
import io.ktgp.I2c
import io.ktgp.use
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Pirmoroni Enviro pHAT wrapper class to start
 * reading values from and writing values to
 * [BMP280], [TCS3472] and [LEDs].
 */
class Enviro {
    /**
     * Runs the life cycle of the pHAT
     */
    fun run() =
        runBlocking {
            // Print greeting
            println(
                """
                # ------------------------------------------ #
                # Kotlin Native + Raspberry Pi + Enviro pHAT #
                # ------------------------------------------ #
                """.trimIndent(),
            )

            // Launch sensor readings
            launch {
                // Open GPIO connections
                Gpio().use { gpio ->
                    // Create and access to the LEDs
                    val leds = LEDs(gpio)

                    // Open I2c connection
                    I2c(1).use { i2c ->
                        // Create and access the BMP280
                        val bmp280 = BMP280(i2c)
                        val tcs3472 = TCS3472(i2c)

                        // Start server
                        val server = runServer(leds, bmp280, tcs3472)

                        println("")
                        println("--- Environment reading START --")
                        println("Temperature (C°): ${bmp280.readTemperature()}")
                        println("Pressure (hPa): ${bmp280.readPressure()}")
                        println("Altitude (m): ${bmp280.readAltitude()}")
                        println("--- Sample reading END ----")
                        println("")
                        println("--- Ambient reading START --")

                        val rgb = tcs3472.readRGB()
                        println("Values in range of 0 to 255")
                        println("Red: ${rgb.red}")
                        println("Blue: ${rgb.blue}")
                        println("Green: ${rgb.clear}")
                        println("--- Ambient reading END ----")
                        println("")
                        // Present a closable action
                        println("Press any key to quit server")
                        readln()

                        // Stop gracefully
                        server.stop()
                        i2c.close()
                        gpio.close()
                        println("Bye bye.")
                    }
                }
            }
        }
}
