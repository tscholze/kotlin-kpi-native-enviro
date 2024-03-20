package io.github.tscholze.kenviro.server

import io.github.tscholze.kenviro.bmp280.BMP280
import io.github.tscholze.kenviro.led.LEDs
import io.github.tscholze.kenviro.tcs3472.TCS3472
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import platform.posix.exit


/**
 * Starts and runs the embedded server.
 *
 * @param leds LEDs manager
 * @param bmp280 BMP280 chip manager
 * @param tcs3472 TCS3472 chip manager
 */
fun runServer(leds: LEDs, bmp280: BMP280, tcs3472: TCS3472): ApplicationEngine {
    return embeddedServer(CIO, port = 8080) {
        routing {
            // Listen to GET requests on root
            get("/") {
                println("Request: Index")
                call.respondText(
                    contentType = ContentType.Text.Html,
                    text = generateIndexContent(bmp280, tcs3472)
                )
            }

            // Listen to POST requests on /on
            post("ledon") {
                println("Request: LED on")
                leds.turnOn()
                call.respond(HttpStatusCode.OK)
            }

            // Listen to POST requests on /off
            post("ledoff") {
                print("Request: LED off")
                leds.turnOff()
                call.respond(HttpStatusCode.OK)
            }

            post("shutdown") {
                println("Request: Shutdown")
                leds.shutdown()
                bmp280.shutdown()

                call.respond(HttpStatusCode.OK)
                exit(0)
            }
        }
    }.start()
}

/**
 * Generates the content of the index ('/') page.
 *
 * @param bmp280 BMP280 manager to render it's values.
 * @return Populated html template string.
 */
private fun generateIndexContent(bmp280: BMP280, tcs3472: TCS3472): String {
    val temperature = bmp280.readTemperature()
    val pressure = bmp280.readPressure()
    val altitude = bmp280.readAltitude()
    val rgb = tcs3472.readRGB()
    val bright = if(rgb.clear > 215) "Yes" else "No"

    return template
        .replace("{{temperature}}", temperature.toString().cutToTwoDecimals())
        .replace("{{pressure}}", pressure.toString().cutToTwoDecimals())
        .replace("{{altitude}}", altitude.toString().cutToTwoDecimals())
        .replace("{{Red}}", rgb.red.toString())
        .replace("{{Blue}}", rgb.blue.toString())
        .replace("{{Green}}", rgb.green.toString())
        .replace("{{Clear}}", rgb.clear.toString())
        .replace("{{Bright}}", bright)
}

/**
 * If string is representing a double, it cuts of all decimal
 * digits after tow.
 *
 * Input: "1.23456"
 * Output: "1.23"
 *
 * @return Cut off string or string in original length if it is e.g "1.2".
 */
private fun String.cutToTwoDecimals(): String {
    var endIndex = this.indexOf('.') + 3
    endIndex = kotlin.math.min(length, endIndex)

    return substring(0, endIndex)
}

/**
 * In Kotlin Native, you cannot load Ktor content from
 * `resources`, to have a simple template string was the
 * best of the worse.
 */
private val template = """
<html lang="en">

<head>
    <title>KPi - Enviro</title>
    <script>
        let xhr = new XMLHttpRequest();

        function turnOn() {
            xhr.open("POST", "/ledon");
            xhr.send()
        }

        function turnOff() {
            xhr.open("POST", "/ledoff");
            xhr.send()
        }

        function shutdown() {
            xhr.open("POST", "/shutdown");
            xhr.send()
        }
    </script>
</head>

<body>
    <h1>Kotlin Native Enviro</h1>
    <div style="width: 300px">
        <fieldset>
            <legend>Light switch</legend>
            <button onclick="turnOn()">On</button>
            <button onclick="turnOff()">Off</button>
        </fieldset>

        <fieldset>
            <legend>Environment properties</legend>
            <table>
                <tr>
                    <td>Temperature</td>
                    <td>{{temperature}} C°</td>
                </tr>
                <tr>
                    <td>Pressure</td>
                    <td>{{pressure}} hPa</td>
                </tr>
                <tr>
                    <td>Altitude</td>
                    <td>{{altitude}} m</td>
                </tr>
            </table>
        </fieldset>

        <fieldset>
            <legend>Ambient</legend>
            <table>
                <tr>
                    <td>Red</td>
                    <td>{{Red}}</td>
                    <td>Clear</td>
                    <td>{{Clear}}</td>
                </tr>
                <tr>
                    <td>Green</td>
                    <td>{{Red}}</td>
                    <td>Is bright?</td>
                    <td>{{Bright}}</td>
                </tr>
                <tr>
                    <td>Blue</td>
                    <td>{{Blue}}</td>
                    <td>Color</td>
                    <td>
                        <div style='background-color: rgb({{Red}},{{Green}},{{Blue}}); height:14px; width:14px;'>
                            &nbsp;
                        </div>
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset>
            <legend>System</legend>
            <button onclick="shutdown()">Shutdown</button>
        </fieldset>
    </div>
    <p>Try it, maybe it will explode.</p>
</body>

</html>
""".trimIndent()