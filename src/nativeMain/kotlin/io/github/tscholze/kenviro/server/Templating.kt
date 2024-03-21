package io.github.tscholze.kenviro.server

/**
 * In Kotlin Native, you cannot load Ktor content from
 * `resources`, to have a simple template string was the
 * best of the worse.
 */
val template =
    """
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
