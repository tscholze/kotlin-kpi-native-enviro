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
    val temperature2: Short,
    /**  Calibration value for the third temperature digit. */
    val temperature3: Short,
    /** Calibration value for the first pressure digit. */
    val pressure1: Int,
    /** Calibration value for the second pressure digit. */
    val pressure2: Short,
    /** Calibration value for the third pressure digit. */
    val pressure3: Short,
    /** Calibration value for the fourth pressure digit. */
    val pressure4: Short,
    /** Calibration value for the fifth pressure digit. */
    val pressure5: Short,
    /** Calibration value for the sixth pressure digit. */
    val pressure6: Short,
    /** Calibration value for the seventh pressure digit. */
    val pressure7: Short,
    /** Calibration value for the eigth pressure digit. */
    val pressure8: Short,
    /** Calibration value for the ninth pressure digit. */
    val pressure9: Short,
) {
    override fun toString(): String {
        return """
            t1: $temperature1
            t2: $temperature2
            t3: $temperature3
            ----
            p1: $pressure1
            p2: $pressure2
            p3: $pressure3
            p4: $pressure4
            p5: $pressure5
            p6: $pressure6
            p7: $pressure7
            p8: $pressure8
            p9: $pressure9
        """.trimIndent()
    }
}