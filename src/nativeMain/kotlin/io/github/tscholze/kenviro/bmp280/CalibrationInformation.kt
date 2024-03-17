package io.github.tscholze.kenviro.bmp280

/**
 * BMP280 calibration information.
 *
 * Links:
 *   - Datasheet http://www.adafruit.com/datasheets/BST-BMP280-DS001-11.pdf
 */
data class CalibrationInformation(
    /** Calibration value for the first temperature digit. */
    val temperature1: Int,
    /**  Calibration value for the second temperature digit. */
    val temperature2: Int,
    /**  Calibration value for the third temperature digit. */
    val temperature3: Int,
    /** Calibration value for the first pressure digit. */
    val pressure1: Int,
    /** Calibration value for the second pressure digit. */
    val pressure2: Int,
    /** Calibration value for the third pressure digit. */
    val pressure3: Int,
    /** Calibration value for the fourth pressure digit. */
    val pressure4: Int,
    /** Calibration value for the fifth pressure digit. */
    val pressure5: Int,
    /** Calibration value for the sixth pressure digit. */
    val pressure6: Int,
    /** Calibration value for the seventh pressure digit. */
    val pressure7: Int,
    /** Calibration value for the eigth pressure digit. */
    val pressure8: Int,
    /** Calibration value for the ninth pressure digit. */
    val pressure9: Int,
)
