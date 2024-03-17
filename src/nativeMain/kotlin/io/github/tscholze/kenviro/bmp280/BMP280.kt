package io.github.tscholze.kenviro.bmp280

import io.ktgp.i2c.I2c
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Represents an BMP280 chip manager.
 * It allows the read temperature and pressure sensors
 *
 *
 * After usage the method [close] shall be called to clean up I2C usage.
 *
 * @param i2c I2C Controller to access pins.
 * @param actions Flow that shall be listened to for requested actions
 */
class BMP280(
    private var i2c: I2c,
    private var actions: MutableSharedFlow<Command>,
) {
    // MARK: - Private properties -

    /** I2C device of the BMP2080 */
    private val device = this.i2c.device(BMP280_ADDRESS.toUInt())

    /** Required calibriation information for BMP 280 sensors */
    private val calibrationInformation: CalibrationInformation

// MARK: - Init -

    init {
// Check if correct I2C has been found.
        val signature = device.read(REGISTER_CHIPID.toUByte(), 1U)
        if (signature.toInt() != BMP280_SIGNATURE) {
            throw RuntimeException("Found signature $signature but must be $BMP280_SIGNATURE")
        }

// Get calibration information for sensors
        calibrationInformation = CalibrationInformation(
            device.read(REGISTER_DIGIT_TEMPERATURE_1, 2U).toInt() and 0xffff,
            device.read(REGISTER_DIGIT_TEMPERATURE_2, 2U).toInt(),
            device.read(REGISTER_DIGIT_TEMPERATURE_3, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_1, 2U).toInt() and 0xffff,
            device.read(REGISTER_DIGIT_PRESSURE_2, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_3, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_4, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_5, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_6, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_7, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_8, 2U).toInt(),
            device.read(REGISTER_DIGIT_PRESSURE_9, 2U).toInt()
        )
    }

// MARK: - Getters -

    fun getTemperature(): Double {
        val msb = device.read(REGISTER_MSB_TEMPERATURE, 1U).toInt()
        val lsb = device.read(REGISTER_LSB_TEMPERATURE, 1U).toInt()
        val xlsb = device.read(REGISTER_XLSB_TEMPERATURE, 1U).toInt()
        val rawValue = (msb shl 12) + (lsb shl 4) + (xlsb shr 4)

// Apply compensation formula from the BMP280 datasheet.
        val part1 =
            ((rawValue / 16384.0) - (calibrationInformation.temperature1 / 1024.0)) * calibrationInformation.temperature2
        val part2 =
            (rawValue / 131072.0 - calibrationInformation.temperature1 / 8192.0) * (rawValue / 131072.0 - calibrationInformation.temperature2 / 8192.0) * calibrationInformation.temperature3

// Return combined / transformed value.
        return (part1 + part2) / 5120.0
    }

    fun close() {
        i2c.close()
    }

// MARK: - Companion -

    companion object {
        /** I2C address of the BMP280. */
        private const val BMP280_ADDRESS = 0x77

        /** I2C signature of the BMP280. Used to verify the address.*/
        private const val BMP280_SIGNATURE = 0x58

        /** Chip ID of the BMP280. */
        private const val REGISTER_CHIPID = 0xD0

        /**  Control register of the BMP280. */
        private const val REGISTER_CONTROL = 0xF4

        /**  I2C register for the first digit of the temperature measurement. */
        private const val REGISTER_DIGIT_TEMPERATURE_1: UByte = 0x88U

        /**  I2C register for the second digit of the temperature measurement. */
        private const val REGISTER_DIGIT_TEMPERATURE_2: UByte = 0x8AU

        /**  I2C register for the third digit of the temperature measurement. */
        private const val REGISTER_DIGIT_TEMPERATURE_3: UByte = 0x8CU

        /**  Gets the most significant bit of the temperature measurement. */
        private const val REGISTER_MSB_TEMPERATURE: UByte = 0xFAU

        /** Gets the least significant bit of the temperature measurement. */
        private const val REGISTER_LSB_TEMPERATURE: UByte = 0xFBU

        /**  Gets the bits between msb and lsb of the temperature measurement. */
        private const val REGISTER_XLSB_TEMPERATURE: UByte = 0xFCU

        /**  I2C register for the first digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_1: UByte = 0x8EU

        /**  I2C register for the second digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_2: UByte = 0x90U

        /**  I2C register for the third digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_3: UByte = 0x92U

        /**  I2C register for the fourth digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_4: UByte = 0x94U

        /**  I2C register for the fifth digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_5: UByte = 0x96U

        /**  I2C register for the sixth digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_6: UByte = 0x98U

        /**  I2C register for the seventh digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_7: UByte = 0x9AU

        /** I2C register for the eighth digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_8: UByte = 0x9CU

        /** I2C register for the ninth digit of the pressure measurement. */
        private const val REGISTER_DIGIT_PRESSURE_9: UByte = 0x9EU

        /** Gets the most significant bit of the pressure measurement. */
        private const val REGISTER_MSB_PRESSURE: UByte = 0xF7U

        /** Gets the least significant bit of the pressure measurement. */
        private const val REGISTER_LSB_PRESSURE: UByte = 0XF8U

        /** Gets the bits between msb and lsb of the pressure measurement.*/
        private const val REGISTER_XLSB_PRESSURE: UByte = 0xF9U
    }
}


fun UByteArray.toInt(): Int {
    var result = 0
    var shift = 0
    for (byte in this) {
        result = result or (byte.toInt() shl shift)
        shift += 8
    }

    return result
}