# Kpi.KEnviro

sudo apt-get install libgpiod2 libi2c0

**Supported sensors and periphery**
-  A BMP280 temperature/pressure sensor
- A TCS3472 colour sensor, for detecting the amount and colour of light
- Two LEDs connected to GPIO #4 for illuminating objects over the colour sensor

## How it looks
### Web
![Web screenshot](__docs/web-shrinked.png)

### Shell output
```bash
# ------------------------------------------ #
# Kotlin Native + Raspberry Pi + Enviro pHAT #
# ------------------------------------------ #

--- Environment reading START --
[INFO] (ktor.application): Responding at http://0.0.0.0:8080
Temperature (C°): 26.891849822597578
Pressure (hPa): 963.1150683186409
Altitude (m): 489.22244179802976
--- Sample reading END ----

--- Ambient reading START --
Values in range of 0 to 255
Red: 124
Blue: 49
Green: 918.0
--- Ambient reading END ----

Press any key to quit server
```

## Links 
https://www.dwd.de/DE/fachnutzer/luftfahrt/teaser/luftsportberichte/qnh_sued_node.html;jsessionid=6AE20EA1CBE7EB543CB7AD6A59EACBEF.live31083
https://github.com/taartspi/pi4j-example-devices/blob/master/src/main/java/com/pi4j/devices/bmp280/BMP280Device.java
https://cdn-shop.adafruit.com/datasheets/BST-BMP280-DS001-11.pdf
https://github.com/androidthings/contrib-drivers/blob/master/bmx280/src/main/java/com/google/android/things/contrib/driver/bmx280/Bmx280.java

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
Dependencies or assets maybe licensed differently.