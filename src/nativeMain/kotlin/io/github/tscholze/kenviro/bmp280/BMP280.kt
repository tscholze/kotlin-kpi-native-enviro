package io.github.tscholze.kenviro.bmp280

import io.github.tscholze.kenviro.utils.toInt
import io.ktgp.i2c.I2c
import kotlin.math.pow

/**
 * Represents an BMP280 chip manager.
 * It allows the read temperature and pressure sensors
 *
 *
 * After usage the method [close] shall be called to clean up I2C usage.
 *
 * @param i2c I2C Controller to access pins.
 */
class BMP280(private var i2c: I2c) {
    // MARK: - Private properties -

    /** I2C device of the BMP2080 */
    private val device = this.i2c.device(BMP280_ADDRESS.toUInt())

    /** Required calibration information for BMP 280 sensors */
    private val calibration: CalibrationInformation

    // MARK: - Init -

    init {
        // Check if correct I2C has been found.
        val signature = device.read(REGISTER_CHIP_ID.toUByte(), 1U)
        if (signature.toInt() != BMP280_SIGNATURE) {
            throw RuntimeException("Found signature $signature but must be $BMP280_SIGNATURE")
        }

        // Get calibration information for sensors
        calibration = CalibrationInformation(
            device.read(REGISTER_DIGIT_TEMPERATURE_1, 2U).toInt() and 0xffff,
            device.read(REGISTER_DIGIT_TEMPERATURE_2, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_TEMPERATURE_3, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_1, 2U).toInt() and 0xffff,
            device.read(REGISTER_DIGIT_PRESSURE_2, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_3, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_4, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_5, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_6, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_7, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_8, 2U).toInt().toShort(),
            device.read(REGISTER_DIGIT_PRESSURE_9, 2U).toInt().toShort()
        )
    }

    // MARK: - Getters -

    /**
     * Returns the current temperature in Celsius from the sensor data
     *
     * @param asFinite If true, the returned has not been transformed to human-readable value. Default value: false.
     */
    fun readTemperature(asFinite: Boolean = false): Double {
        val msb = device.read(REGISTER_MSB_TEMPERATURE, 1U).toInt()
        val lsb = device.read(REGISTER_LSB_TEMPERATURE, 1U).toInt()
        val xlsb = device.read(REGISTER_XLSB_TEMPERATURE, 1U).toInt()
        val rawValue = (msb shl 12) + (lsb shl 4) + (xlsb shr 4)

        // Apply compensation formula from the BMP280 datasheet.
        val part1 =
            ((rawValue / 16384.0) - (calibration.temperature1 / 1024.0)) * calibration.temperature2
        val part2 =
            (rawValue / 131072.0 - calibration.temperature1 / 8192.0) * (rawValue / 131072.0 - calibration.temperature2 / 8192.0) * calibration.temperature3

        // Return finite value or transformed
        val finiteValue = part1 + part2
        return if (asFinite) {
            finiteValue
        } else {
            finiteValue / 5120.0
        }
    }

    /**
     * Returns the current pressure in hPa
     */
    fun readPressure(): Double {
        val temperature = readTemperature(asFinite = true)
        val msb = device.read(REGISTER_MSB_PRESSURE, 1U).toInt()
        val lsb = device.read(REGISTER_LSB_PRESSURE, 1U).toInt()
        val xlsb = device.read(REGISTER_XLSB_PRESSURE, 1U).toInt()
        val rawValue = (msb shl 12) + (lsb shl 4) + (xlsb shr 4)

        var part1 = temperature / 2.0 - 64000.0
        var part2 = part1 * part1 * calibration.pressure6 / 32768.0
        part2 += part1 * calibration.pressure5 * 2
        part2 = part2 / 4.0 + calibration.pressure4 * 65536.0
        part1 = (calibration.pressure3 * part1 * part1 / 524288.0 + calibration.pressure2 * part1) / 524288.0
        part1 = (1.0 + part1 / 32768.0) * calibration.pressure1
        var pressure = 1048576.0 - rawValue
        pressure = (pressure - part2 / 4096.0) * 6250.0 / part1
        part1 = calibration.pressure9 * pressure * pressure / 2147483648.0
        part2 = pressure * calibration.pressure8 / 32768.0
        pressure += (part1 + part2 + calibration.pressure7) / 16.0

        return pressure / 100
    }

    /**
     * Returns the meters above-normal (sea level).
     * It assumes, the QNH is 1018 (Munich, Bavaria, Germany)
     */
    fun readAltitude(): Double {
        val temperature = readTemperature()
        val pressure = readPressure()

        val part1 = (QNH_MUNICH / pressure).pow(1 / 5.257) - 1
        val part2 = (temperature + 273.15) / 0.0065

        return part1 * part2
    }

    // MARK: - System -

    fun shutdown() {
        print("Shutting down BMP280")
        i2c.close()
    }

    // MARK: - Companion -

    companion object {
        /** I2C address of the BMP280. */
        private const val BMP280_ADDRESS = 0x77

        /** I2C signature of the BMP280. Used to verify the address.*/
        private const val BMP280_SIGNATURE = 0x58

        /** Chip ID of the BMP280. */
        private const val REGISTER_CHIP_ID = 0xD0

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

        /** QNH value of Munich, Bavaria, Germany. */
        private const val QNH_MUNICH = 1018
    }
}