package io.github.tscholze.kenviro.tcs3472

import io.github.tscholze.kenviro.utils.toInt
import io.ktgp.i2c.I2c

/**
 * Represents an TCS3472 chip manager.
 * It allows the read color values from the environment.
 *
 * @param i2c I2C manager to access pins.
 */
class TCS3472(private val i2c: I2c) {
    // MARK: - Private properties -

    /** I2C device of the BMP2080 */
    private val device = this.i2c.device(TCS3472_ADDRESS.toUInt())

    // MARK: - Init -

    init {
        // Write enabling feature bits
        val bitmask = ubyteArrayOf((REGISTER_ENABLE_RGBC or REGISTER_ENABLE_POWER).toUByte())
        device.write(REGISTER_ENABLE.toUByte(), bitmask)
    }

    /**
     * Reads current RGB values from the ambient.
     */
    fun readRGB(): Rgb {
        val clear = device.read(REGISTER_CLEAR.toUByte(), 2U).toInt().toDouble()
        val rawRed = device.read(REGISTER_RED.toUByte(), 2U).toInt()
        val rawGreen = device.read(REGISTER_GREEN.toUByte(), 2U).toInt()
        val rawBlue = device.read(REGISTER_BLUE.toUByte(), 2U).toInt()

        val scaledClear = clear / 65_535
        val scaledRed = rawRed / clear
        val scaledGreen = rawGreen / clear
        val scaledBlue = rawBlue / clear

        val red = (scaledRed * 255).toInt()
        val blue = (scaledBlue * 255).toInt()
        val green = (scaledGreen * 255).toInt()

        return Rgb(
            clear,
            scaledClear,
            scaledRed,
            scaledGreen,
            scaledBlue,
            red,
            green,
            blue,
        )
    }

    companion object {
        /** I2C command bit mask */
        private const val REGISTER_COMMAND = 0b10000000

        /** I2C command to auto increment */
        private const val REGISTER_COMMAND_AUTO_INC = 0b00100000

        /** I2C address of the TCS3472. */
        const val TCS3472_ADDRESS: UByte = 0x29U

        /**  I2C register for clear measurement. */
        const val REGISTER_CLEAR = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x14

        /**  I2C register for red measurement. */
        const val REGISTER_RED = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x16

        /**  I2C register for green measurement. */
        const val REGISTER_GREEN = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x18

        /**  I2C register for blue measurement. */
        const val REGISTER_BLUE = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x1A

        /**  I2C register for enabling measurement. */
        const val REGISTER_ENABLE = REGISTER_COMMAND or 0

        /**  I2C register for enabling rgbc sensor. */
        const val REGISTER_ENABLE_RGBC = 1 shl 1

        /**  I2C register for controling the power switch. */
        const val REGISTER_ENABLE_POWER = 1
    }
}

/**
 * Represents RGBC values in different scales.
 */
data class Rgb(
    /** Clear (light) value with max of 65_535 */
    val clear: Double,
    /** Scaled clear value to percent of clear max */
    val scaledClear: Double,
    /** Scaled red value to percent of clear max */
    val scaledRed: Double,
    /** Scaled green value to percent of clear max */
    val scaledGreen: Double,
    /** Scaled blue value to percent of clear max */
    val scaledBlue: Double,
    /** Red value (0-255) */
    val red: Int,
    /** Green value (0-255) */
    val green: Int,
    /** Blue value (0-255) */
    val blue: Int,
) {
    override fun toString(): String {
        return """
            clear (base): $clear
            clear (scaled): $scaledClear
            --
            scaledRed: $scaledRed
            scaledGreen: $scaledGreen
            scaledBlue: $scaledBlue
            --
            red: $red
            green: $green
            blue: $blue
            """.trimIndent()
    }
}
