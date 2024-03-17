package io.github.tscholze.kenviro.bmp280

/**
 * Defines all available action that the
 * [BMP280] can be requested for.
 */
sealed class Command {
    /** Turns all LEDs on with a bright white color on */
    data object TurnLEDsOn : Command()

    /** Turns all LEDs off which means setting a color of black */
    data object TurnLEDsOff : Command()
}
