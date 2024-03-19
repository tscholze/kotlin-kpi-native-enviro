package io.github.tscholze.kenviro.tcs3472

import io.github.tscholze.kenviro.Command
import io.ktgp.i2c.I2c
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Represents an TCS3472 chip manager.
 * It allows the read color values from the environment.
 *
 *
 * After usage the method [close] shall be called to clean up I2C usage.
 *
 * @param i2c I2C Controller to access pins.
 * @param actions Flow that shall be listened to for requested actions
 */
class TCS3472(
    private var i2c: I2c,
    private var actions: MutableSharedFlow<Command>,
) {

}