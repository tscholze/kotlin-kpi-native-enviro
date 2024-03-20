package io.github.tscholze.kenviro.tcs3472

import io.github.tscholze.kenviro.utils.toInt
import io.ktgp.i2c.I2c

/**
 * Represents an TCS3472 chip manager.
 * It allows the read color values from the environment.
 *
 *
 * After usage the method [close] shall be called to clean up I2C usage.
 *
 * @param i2c I2C Controller to access pins.
 */
class TCS3472(private val i2c: I2c) {

    // MARK: - Private properties -

    /** I2C device of the BMP2080 */
    private val  device = this.i2c.device(TCS3472_ADDRESS.toUInt())

    // MARK: - Init -

    init {
        // Write enabeling feature bits
        val bitmask = ubyteArrayOf((REGISTER_ENABLE_RGBC or REGISTER_ENABLE_POWER).toUByte())
        device.write(REGISTER_ENABLE.toUByte(), bitmask)

        // Write time
        //val time = (round(511.2 / 2.4)).toInt()
        //val maxCount = Int.min(65535, (256 - time) * 1024)
       // val maxcount = 44032

    }

    fun readRGB(): Rgb {
        val clear = device.read(REGISTER_CLEAR_L.toUByte(), 2U).toInt().toDouble()
        val rawRed = device.read(REGISTER_RED_L.toUByte(), 2U).toInt()
        val rawGreen = device.read(REGISTER_GREEN_L.toUByte(), 2U).toInt()
        val rawBlue = device.read(REGISTER_BLUE_L.toUByte(), 2U).toInt()

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
            blue
        )
    }

    fun shutdown() {

    }

    companion object {
        const val  TCS3472_ADDRESS: UByte = 0x29U

        const val  REGISTER_COMMAND = 0b10000000
        const val  REGISTER_COMMAND_AUTO_INC = 0b00100000
        const val  REGISTER_CLEAR_L = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x14
        const val  REGISTER_RED_L = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x16
        const val  REGISTER_GREEN_L = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x18
        const val  REGISTER_BLUE_L = REGISTER_COMMAND or REGISTER_COMMAND_AUTO_INC or 0x1A

        const val  REGISTER_ENABLE = REGISTER_COMMAND or 0
        const val  REGISTER_ATIME = REGISTER_COMMAND or 1
        const val  REGISTER_CONTROL = REGISTER_COMMAND or 0x0f
        const val  REGISTER_STATUS = REGISTER_COMMAND or 0x13

        const val  REGISTER_CONTROL_GAIN_1X = 0b00000000
        const val  REGISTER_CONTROL_GAIN_4X = 0b00000001
        const val  REGISTER_CONTROL_GAIN_16X = 0b00000010
        const val  REGISTER_CONTROL_GAIN_60X = 0b00000011

        const val  REGISTER_ENABLE_INTERRUPT = 1 shl 4
        const val  REGISTER_ENABLE_WAIT = 1 shl 3
        const val  REGISTER_ENABLE_RGBC = 1 shl 1
        const val  REGISTER_ENABLE_POWER = 1
    }
}

data class Rgb(
    val clear: Double,
    val scaledClear: Double,
    val scaledRed: Double,
    val scaledGreen: Double,
    val scaledBlue: Double,
    val red: Int,
    val green: Int,
    val blue: Int
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