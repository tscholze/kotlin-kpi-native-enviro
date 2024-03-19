package io.github.tscholze.kenviro.led

import io.github.tscholze.kenviro.Command
import io.ktgp.gpio.Gpio
import io.ktgp.gpio.PinState
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Represents two LEDs besides the tcs3472 sensor.
 * It allows to illuminate the environment.
 *
 *
 * After usage the method [close] shall be called to clean up I2C usage.
 *
 * @param gpio Gpio Controller to access pins.
 * @param actions Flow that shall be listened to for requested actions
 */
class LEDs(
        gpio: Gpio,
        private var actions: MutableSharedFlow<Command>,
    ) {

    // MARK: - Private properties -

    private val output = gpio.output(4)

    // MARK: - Light switch -

    /**
     * Turns both LEDs on.
     */
    fun turnOn() {
        output.setState(PinState.HIGH)
    }

    /**
     * Turns both LEDs off.
     */
    fun turnOff() {
        output.setState(PinState.LOW)
    }
}