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


/**
 * KN
 * t1: 27234
 * t2: 25444
 * t3: 50
 * ----
 * p1: 37768
 * p2: 54759
 * p3: 3024
 * p4: 7652
 * p5: 65296
 * p6: 65529
 * p7: 15500
 * p8: 50936
 * p9: 6000
 */

/* Python output
t1: 27234
t2: 25444
t3: 50
---
p1: 37768
p2: -10777
p3: 3024
p4: 7652
p5: -240
p6: -7
p7: 15500
p8: -14600
p9: 6000
 */
