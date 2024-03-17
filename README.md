# Kpi.KEnviro

sudo apt-get install libgpiod2 libi2c0

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.
Dependencies or assets maybe licensed differently.

Enviro pHAT includes:
An LSM303D accelerometer/magnetometer for detecting orientation, motion and heading
A BMP280 temperature/pressure sensor
A TCS3472 colour sensor, for detecting the amount and colour of light
An ADS1015 analog sensor with four 3.3v tolerant channels for your external sensors
A 5v power supply pin for powering your sensors, which you can regulate or divide to 3v if needed
Two LEDs connected to GPIO #4 for illuminating objects over the colour sensor

https://github.com/taartspi/pi4j-example-devices/blob/master/src/main/java/com/pi4j/devices/bmp280/BMP280Device.java
https://github.com/androidthings/contrib-drivers/blob/master/bmx280/src/main/java/com/google/android/things/contrib/driver/bmx280/Bmx280.java